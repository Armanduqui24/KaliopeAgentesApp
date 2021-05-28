package mx.greenmouse.kaliope;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class RealizarDevolucion extends AppCompatActivity {

    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);


    TextView nombreClienteTV;
    EditText cantidadET;
    EditText codigoET;
    TextView piezasCapturadasTV;
    TextView importeCapturadoTV;
    ListView listViewCaptura;
    TextView mercanciaAcargoTV;
    TextView importeAcargoTV;
    Button siguienteB;

    ArrayList<HashMap> list;
    String numeroDeCuentaCliente = "";
    String gradoCliente = "";


    MediaPlayer mediaPlayer = new MediaPlayer();
    Vibrator vibrator;

    Activity activity = this;


    int totalImporteDevuelto;
    int totalPiezasDevueltas;
    int importeAcargo = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_devolucion);
        getSupportActionBar().hide();
        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));


        nombreClienteTV = (TextView) findViewById(R.id.RealizarDevolucionNombre);
        cantidadET = (EditText) findViewById(R.id.RealizarDevolucionCantidad);
        codigoET = (EditText) findViewById(R.id.RealizarDevolucionCodigo);
        piezasCapturadasTV = (TextView) findViewById(R.id.RealizarDevolucionPiezasCapturadas);
        importeCapturadoTV = (TextView) findViewById(R.id.RealizarDevolucionImporteCapturado);
        mercanciaAcargoTV = (TextView) findViewById(R.id.RealizarDevolucionMercanciaAcargo);
        listViewCaptura = (ListView) findViewById(R.id.RealizarDevolucionLista);
        importeAcargoTV = (TextView) findViewById(R.id.RealizarDevolucionImporteAcargo);
        siguienteB = (Button) findViewById(R.id.RealizarDevolucionSiguiente);



         vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);




        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            numeroDeCuentaCliente = bundle.getString("NUMERO_CUENTA_ENVIADO");

            Cursor datosCliente = dataBaseHelper.clientes_dameClientePorCuentaCliente(numeroDeCuentaCliente);
            if (datosCliente.getCount() > 0) {
                datosCliente.moveToFirst();

                int keyid = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.KEY_ID));
                String cuentaCliente = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE));
                String nombreCliente = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE));
                importeAcargo = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE));
                String piezasAcargo = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_MERCANCIA_ACARGO));
                String vencimiento = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_VENCIMIENTO));
                gradoCliente = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_GRADO_CLIENTE));

                nombreClienteTV.setText(nombreCliente);
                mercanciaAcargoTV.setText(piezasAcargo);
                String aMostrar = "$" + importeAcargo;
                importeAcargoTV.setText(aMostrar);
                listaEntradas();




            }
        }

        cantidadET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    agregarEntradas();
                }
                return false;
            }
        });


        listViewCaptura.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap hashMap = (HashMap) adapterView.getItemAtPosition(i);
                int idEliminar = Integer.valueOf(hashMap.get("ROW_ID").toString());
                confirmaEliminacion(idEliminar);
            }
        });

        siguienteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (totalImporteDevuelto>importeAcargo){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RealizarDevolucion.this);
                    builder.setTitle("Alerta");
                    builder.setMessage("No puedes continuar el importe de devolcion $" + totalImporteDevuelto + " que capturaste\n\n" +
                            "Es mayor que los $" + importeAcargo + " que el cliente tenia acargo \n\n No puedes continuar por favor reviza la devolucion");
                    builder.setPositiveButton("Entiendo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.create();
                    builder.show();
                }else{
                    volcarDatosAtablaClientes();
                    Intent intent = new Intent(getApplicationContext(),RealizarCierre.class);
                    intent.putExtra("NUMERO_CUENTA_ENVIADO", numeroDeCuentaCliente);
                    startActivity(intent);

                }

            }
        });







        }


    private void listaEntradas(){


        list = new ArrayList<HashMap>();

        ListViewAdapterDevolucionMercancia adapter = new ListViewAdapterDevolucionMercancia(this, list);
        listViewCaptura.setAdapter(adapter);



        Cursor res = dataBaseHelper.devolucion_mercancia_dameDevolucion(numeroDeCuentaCliente);
        Log.d("dbg-resEntrdas", String.valueOf(res.getCount()));


        if(res.getCount()>0) {

            res.moveToFirst();
            do {
                HashMap temp1 = new HashMap();

                int cantidadPiezas = res.getInt(res.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_CANTIDAD));
                int precioDistribucion = res.getInt(res.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_DISTRIBUCION));
                int totalImporte = cantidadPiezas * precioDistribucion;

                temp1.put("ROW_ID", res.getString(res.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_ROW_ID)));//enviamos el id de la tabla en la columna 1 para poder sacarlo del view en el item clicl listener y eliminarlo
                temp1.put("CANTIDAD", cantidadPiezas);
                temp1.put("PRECIO", res.getString(res.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_PRECIO)));
                temp1.put("IMPORTE", totalImporte);

                list.add(temp1);



            } while (res.moveToNext());



        }


        calcularTotales();


    }

    private void agregarEntradas(){

        String codigo = codigoET.getText().toString();
        String cantidad = cantidadET.getText().toString();


        try {
            if (codigo.isEmpty()|| codigo.equals("0")) throw new Exception("El codigo esta vacio o es 0");
            if (cantidad.isEmpty()|| cantidad.equals("0")) throw new Exception("La cantidad esta vacia o es 0");
            int costoVentaProducto = dataBaseHelper.inventario_dameCostoDelProducto(codigo);
            int precioDistribucion = dataBaseHelper.inventario_damePrecioDistribucionDelProducto(codigo,gradoCliente);
            int gananciaDelProducto = dataBaseHelper.inventario_dameGananciaDelProducto(codigo,gradoCliente);

            ContentValues contentValues = new ContentValues(10);
            contentValues.put(DataBaseHelper.DEVOLUCION_MERCANCIA_CUENTA_CLIENTE,numeroDeCuentaCliente);
            contentValues.put(DataBaseHelper.DEVOLUCION_MERCANCIA_CANTIDAD,cantidad);
            contentValues.put(DataBaseHelper.DEVOLUCION_MERCANCIA_PRECIO,costoVentaProducto);
            contentValues.put(DataBaseHelper.DEVOLUCION_MERCANCIA_DISTRIBUCION,precioDistribucion);
            contentValues.put(DataBaseHelper.DEVOLUCION_MERCANCIA_GANANCIA,gananciaDelProducto);
            contentValues.put(DataBaseHelper.DEVOLUCION_MERCANCIA_CODIGO,codigo);
            contentValues.put(DataBaseHelper.DEVOLUCION_MERCANCIA_LATITUD,Constant.INSTANCE_LATITUDE);
            contentValues.put(DataBaseHelper.DEVOLUCION_MERCANCIA_LONGUITUD,Constant.INSTANCE_LONGITUDE);
            contentValues.put(DataBaseHelper.DEVOLUCION_MERCACIA_HORA_CAPTURA,utilidadesApp.dameHora());

            if (dataBaseHelper.devolucion_mercancia_insertarProducto(contentValues) == -1) throw new Exception("Ocurrio un problema al registrar el producto a la entrada");

            codigoET.setText("");
            cantidadET.setText("");
            codigoET.requestFocus();

            vibrator.vibrate(400);
            mediaPlayer = MediaPlayer.create(this,R.raw.harpsound);
            mediaPlayer.start();
            listaEntradas();

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            mediaPlayer = MediaPlayer.create(this,R.raw.error);
            mediaPlayer.start();
            vibrator.vibrate(100);
        }


    }

    private void calcularTotales(){
        totalImporteDevuelto = dataBaseHelper.devolucion_mercancia_calcularImporteDevuelto(numeroDeCuentaCliente);
        totalPiezasDevueltas = dataBaseHelper.devolucion_mercancia_calcularPiezasDevueltas(numeroDeCuentaCliente);


        piezasCapturadasTV.setText(String.valueOf(totalPiezasDevueltas));
        importeCapturadoTV.setText(String.valueOf(totalImporteDevuelto));
    }

    private void confirmaEliminacion(final int idEntrada) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("¡Cuidado!");
        //builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("¿Quieres elimiar este renglon?")
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                })
                .setNegativeButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        eliminaEntrada(idEntrada);
                    }
                });

        builder.create();
        builder.show();
    }

    private void eliminaEntrada(int idEntrada){

        dataBaseHelper.devolucion_mercancia_eliminaRenglonPorKeyID(idEntrada);
        vibrator.vibrate(100);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.error);
        mediaPlayer.start();
        vibrator.vibrate(100);

            listaEntradas();



    }





    @Override
    public void onBackPressed() {
            if (piezasCapturadasTV.getText().equals("0")){
                super.onBackPressed();
            }else{
                preguntarSeguroDeRegresar();
            }

    }

    private void preguntarSeguroDeRegresar () {

        //creamos este metodo que se llamara cuando regresemos, pero solo si fuimos llamados desde clientes
        //esto porque cuando llegamos a este activiti desde clientes se llenan los datos con la base de datos clientes
        // y supongamos que hicimos clic en un cliente maria guadalupe, si precionas volver regresa a clientes
        //seleccionas ahora otro cliente pero los datos no se actualizan, se debe evitar regresar al activyti clientes
        //sin antes reestablecer todas las constantes. el problema se acentua si ya habiamos ingresado mercancia

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("Confirmacion");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("¿Seguro que quieres regresar?  se eliminaran los movimientos registrados")
                .setPositiveButton("Si \n Quiero volver", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dataBaseHelper.devolucion_mercnacia_eliminarDevolucion(numeroDeCuentaCliente);
                        eliminarDatosTablaClientes(dataBaseHelper);
                        finish();


                    }
                })
                .setNegativeButton("NO ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;

                    }
                });

        builder.create();
        builder.show();


    }//fin de metodo presionar flecha atras*/





    private void volcarDatosAtablaClientes (){
        ContentValues contentValues = new ContentValues();

        //vamos a volcar los datos a la tabla clientes la descripcion de las piezas en json array para que
        //al enviar la informacion al servidor ya llege en json array. consultamos la tabla de detalles
        //donde se guardan temporalmente los productos que se van ingeresando a la lista,
        //una vez que cambiamos de pagina guardamos los detalles en formato json en el campo de la tabla
        //clientes correspondiente, de igual forma rellenamos los campos de total piezas devueltas y importe devuelto
        //estos datos se almacenaran en los clientes permanentemente hasta que el estado del cliente pase a VISITADO


        Cursor descripcionPiezas = dataBaseHelper.devolucion_mercancia_dameDevolucion(numeroDeCuentaCliente);


        if (descripcionPiezas.getCount()>0){
            descripcionPiezas.moveToFirst();


            contentValues.put(DataBaseHelper.CLIENTES_DEV_CANTIDAD_PIEZAS_DEVUELTAS,totalPiezasDevueltas);
            contentValues.put(DataBaseHelper.CLIENTES_DEV_IMPORTE_DEVUELTO,totalImporteDevuelto);
            contentValues.put(DataBaseHelper.CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS,dataBaseHelper.devolucion_mercancia_consultarDevolucionEnJsonArrayString(numeroDeCuentaCliente));

            dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);

        }else{
            //si se regresaron a la pantalla o no capturaron nada de producto ponemos en blanco los campos
            contentValues.put(DataBaseHelper.CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS,"");
            contentValues.put(DataBaseHelper.CLIENTES_DEV_IMPORTE_DEVUELTO,"");
            contentValues.put(DataBaseHelper.CLIENTES_DEV_CANTIDAD_PIEZAS_DEVUELTAS,"");
            dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);
        }



    }





    private void eliminarDatosTablaClientes(DataBaseHelper db){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_DEV_CANTIDAD_PIEZAS_DEVUELTAS,"");
        contentValues.put(DataBaseHelper.CLIENTES_DEV_IMPORTE_DEVUELTO,"");
        contentValues.put(DataBaseHelper.CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS,"");
        db.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);

    }



}

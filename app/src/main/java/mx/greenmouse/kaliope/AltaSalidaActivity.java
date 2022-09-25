package mx.greenmouse.kaliope;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static mx.greenmouse.kaliope.Constant.FIRST_COLUMN;
import static mx.greenmouse.kaliope.Constant.FIVE_COLUMN;
import static mx.greenmouse.kaliope.Constant.FOURTH_COLUMN;
import static mx.greenmouse.kaliope.Constant.SECOND_COLUMN;
import static mx.greenmouse.kaliope.Constant.SIX_COLUMN;
import static mx.greenmouse.kaliope.Constant.THIRD_COLUMN;
import static mx.greenmouse.kaliope.Constant.PUNTOS_CANJEADOS;

public class AltaSalidaActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    DataBaseHelper dbHelper = new DataBaseHelper(this);
    Constant c = new Constant();

    TextView txtDiasVencimiento, txtFechaVencimiento, txtLimiteCredito, txtPiezasTotales, txtImporteTotal, txtGradoSalida;
    TextView txtCodigoProducto, txtCantidadProductos;

    ImageButton btnAgregarSalida;
    Button btnTerminarSalidas,siguienteButton;

    int importeTotalSalida = 0;

    public static ListView lview;
    private ArrayList<HashMap> list;

    Map mapGradoCliente;
    ArrayList<String> salidasIdArray;

    SoundPool soundPool;
    int carga;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_salida);
        getSupportActionBar().hide();

        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));

//        Slide slide = new Slide(Gravity.END);
//        slide.setDuration(AltaEntradaActivity.DURATION_TRANSITION);
//        slide.setInterpolator(new DecelerateInterpolator());
//        getWindow().setEnterTransition(slide);
//        getWindow().setAllowEnterTransitionOverlap(false);

        mapGradoCliente     = new HashMap();
        mapGradoCliente.put("VENDEDORA",dbHelper.INVENTARIO_PRECIO_VENDEDORA);
        mapGradoCliente.put("SOCIA",dbHelper.INVENTARIO_PRECIO_SOCIA);
        mapGradoCliente.put("EMPRESARIA",dbHelper.INVENTARIO_PRECIO_EMPRESARIA);

        txtDiasVencimiento  = (TextView)findViewById(R.id.txtPlazoDias);
        txtFechaVencimiento = (TextView)findViewById(R.id.txtFechaVencimeinto);
        txtLimiteCredito    = (TextView)findViewById(R.id.txtLimiteCredito);
        txtGradoSalida      = (TextView)findViewById(R.id.txtGradoSalida);

        txtPiezasTotales    = (TextView)findViewById(R.id.txtPiezasTotalesSalida);
        txtImporteTotal     = (TextView)findViewById(R.id.txtImporteTotalSalida);

        txtCodigoProducto   = (TextView)findViewById(R.id.txtCodigoProductoSalida);
        txtCantidadProductos= (TextView)findViewById(R.id.txtCantidadProductosSalida);

        btnAgregarSalida    = (ImageButton)findViewById(R.id.btnAgregarSalida);
        btnTerminarSalidas  = (Button)findViewById(R.id.btnTermineSalida);
        //siguienteButton = (Button) findViewById(R.id.siguiente3Button);

        txtDiasVencimiento.setText(c.TMPMOV_DAYS);

        c.TMPMOV_EXPIRATION_DATE = utilidadesApp.dameFechaVencimiento(c.TMPMOV_DATE,c.TMPMOV_DAYS);

        txtFechaVencimiento.setText(c.TMPMOV_EXPIRATION_DATE);
        txtLimiteCredito.setText("$ " + c.TMPMOV_LIMIT);
        txtGradoSalida.setText(c.TMPMOV_GRADE);

        btnAgregarSalida.setOnClickListener(this);
        btnTerminarSalidas.setOnClickListener(this);
        btnAgregarSalida.setVisibility(View.GONE);
        //siguienteButton.setOnClickListener(this);

        lview = (ListView) findViewById(R.id.listViewSalida);

        lview.setLongClickable(true);

        dameTotalesSalida(c.TMPMOV_ID);

        listaSalidas();

        txtCantidadProductos.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    agregarSalida();
                }
                return false;
            }
        });

        v = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC,0);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        carga = soundPool.load(this, R.raw.harpsound,1);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnAgregarSalida:

                agregarSalida();

                break;

            case R.id.btnTermineSalida:

                //**original de la programacion abraham

                //Intent m = new Intent(this, AltaMovimientoActivity.class);
                //startActivity(m);


                regresar();

                break;



//            case R.id.siguiente3Button:
//                Intent s = new Intent(this, Puntos.class);
//                startActivity(s);
        }

    }


    //Lanzar pregunta al precionar la tecla volver. para recordarle al agente tomar su reporte de voz en whats app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            regresar();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }//fin escucha boton atras



    public void agregarSalida(){
        String codigoProductoSalida = txtCodigoProducto.getText().toString();
        String cantidadProductoSalida  = txtCantidadProductos.getText().toString();

        if(codigoProductoSalida.equals("") || cantidadProductoSalida.equals("") || cantidadProductoSalida.equals("0")){
            utilidadesApp.dialogoAviso(this, "Se require tanto el código del producto como la cantidad de piezas.");
        }
        else{
/*
            Cursor dameSalidaCodigo = dbHelper.dameSalidaCodigo(c.TMPMOV_ID, codigoProductoSalida);

            if(dameSalidaCodigo.getCount() == 0){*/

                int cantidadProductoSalidaI = Integer.parseInt(cantidadProductoSalida);
                Cursor detalleProducto = dbHelper.inventario_dameInformacionCompletaDelProducto(codigoProductoSalida);

                if(detalleProducto.getCount() == 0){
                    utilidadesApp.dialogoAviso(this,"El código del producto no existe ó no se encuentra en su inventario.");
                }
                else {

                    detalleProducto.moveToNext();




                        //(se supone que la constante TMPMOV_GRADE tiene un numero rescatado de la base de datos creditos este numero es 1 2 o 3
                        //se le envia al map como key y este map devuelve el nombre de la constante que es el identificador de la columna
                        // de la tabla de productos donde viene el precio que se le dara dependiendo del grado. Hay que cambiar los valores Key en el
                        //map grado cliente por VENDEDORA SOCIA EMPRESARIA
                        //
                        // buscar el renglon donde se declara el map y cambiar esos valores)
                        String colGradoCliente = (String) mapGradoCliente.get(c.TMPMOV_GRADE);
                        int colIndexCosto = detalleProducto.getColumnIndex(colGradoCliente);

                        int importeActual = Integer.parseInt(c.TMPMOV_TOPAY);
                        int valorProducto = Integer.parseInt(detalleProducto.getString(colIndexCosto));
                        int limiteCredito = Integer.parseInt(c.TMPMOV_LIMIT);

                        Log.e("dbg-grado", c.TMPMOV_GRADE + " :: " + colGradoCliente);

                        int nuevoSaldo = (valorProducto * cantidadProductoSalidaI) + importeActual;

                        if(nuevoSaldo > limiteCredito){
                            utilidadesApp.dialogoAviso(this, "No puede agrear esta solicitud ya que excede el límite de crédito.");
                        }
                        else {

                            ContentValues cv = new ContentValues(10);

                            int Ganancia = Integer.parseInt(detalleProducto.getString(1)) - Integer.parseInt(detalleProducto.getString(colIndexCosto));

                            cv.put(dbHelper.DETALLES_ID_DEL_MOVIMIENTO, c.TMPMOV_ID);
                            cv.put(dbHelper.DETALLES_CANTIDAD, cantidadProductoSalida);
                            cv.put(dbHelper.DETALLES_PRECIO_PRODUCTO, detalleProducto.getString(1));
                            cv.put(dbHelper.DETALLES_PRECIO_DISTRIBUCION, detalleProducto.getString(colIndexCosto));
                            cv.put(dbHelper.DETALLES_GANANCIA, String.valueOf(Ganancia));
                            cv.put(dbHelper.DETALLES_CODIGO_PRODUCTO, codigoProductoSalida);
                            cv.put(dbHelper.DETALLES_TIPO_MOVIMIENTO, "S");
                            cv.put(dbHelper.DETALLES_LATITUD, Constant.INSTANCE_LATITUDE);
                            cv.put(dbHelper.DETALLES_LONGUITUD, Constant.INSTANCE_LONGITUDE);
                            cv.put(dbHelper.ESTADO_DE_LA_COLUMNA, "A");
                            cv.put(dbHelper.DATE_UP, utilidadesApp.dameHora());

                            if (dbHelper.insertarDetalles(cv) == -1) {
                                utilidadesApp.dialogoAviso(this, "Ocurrió un problema al registrar el movimiento.");
                            } else {

                                v.vibrate(400);
                                soundPool.play(carga,1,1,0,0,1);
                                Constant.TMPMOV_OUTPUT = true;

                                dameTotalesSalida(c.TMPMOV_ID);
                                listaSalidas();
                                txtCodigoProducto.setText("");
                                txtCantidadProductos.setText("");
                                txtCodigoProducto.requestFocus();
                            }

                    }
                }


        }

    }

    public void listaSalidas(){
        list = new ArrayList<HashMap>();

        // VAMOS A MODIFICAR DONDE SE CREA EL LISTVIEW CADA VEZ
        // EMPEZAMOS CON LA ENTREGA DE MERCANCIA, VAMOS A CREAR UNA CONSTANTE QUE SE LLAME
        // MENSAJE ENTREGA DE MERCANCIA, Y EN LA LINEA EN DONDE SE CREA EL LISTVIEW CREAMOS EL MENSAJE
        // VAMOS A SUMARLE AL STRING LO QUE YA TIENE)
        //AL FINAL AÑADIMOS LA FECHA DE CLIENTES_ADMIN_VENCIMIENTO
        //EJEMPLO DEL MENSAJE FINAL
        //piezasEntregadas,ImporteEntregado,descripcionDePiezasEntergadas,CLIENTES_ADMIN_VENCIMIENTO

        //PRIMERO AL MENSAJE LE PONEMOS
        // LAS PIEZAS Y EL IMPORTE
        Constant.MENSAJE_ENTREGA = Integer.parseInt(txtPiezasTotales.getText().toString()) + "," + Constant.TMPMOV_TOPAY + ",";

        ListViewAdapterSix adapter = new ListViewAdapterSix(this, list);
        lview.setAdapter(adapter);

        HashMap temp = new HashMap();
        temp.put(FIRST_COLUMN,"CANT.");
        temp.put(SECOND_COLUMN, "PREC.");
        temp.put(THIRD_COLUMN, "DIST.");
        temp.put(FOURTH_COLUMN, "GAN.");
        temp.put(FIVE_COLUMN, "CÓD.");
        temp.put(SIX_COLUMN, "HORA");
        list.add(temp);

        Cursor res = dbHelper.dameSalidas(c.TMPMOV_ID);
        Log.d("dbg-res", String.valueOf(res.getCount()));


        if(res.getCount()>0) {
            final ArrayList<String> salidasIdArray = new ArrayList<String>();
            res.moveToFirst();
            do {
                HashMap temp1 = new HashMap();

                temp1.put(FIRST_COLUMN, res.getString(2));
                temp1.put(SECOND_COLUMN, res.getString(3));
                temp1.put(THIRD_COLUMN, res.getString(4));
                temp1.put(FOURTH_COLUMN, res.getString(5));
                temp1.put(FIVE_COLUMN, res.getString(6));
                temp1.put(SIX_COLUMN, res.getString(11));
                list.add(temp1);

                salidasIdArray.add(res.getString(0));

                //DESPUES DE LAS PIEZAS Y EL IMPORTE
                //AÑADIMOS DENTRO DE LA MISMA COMA LA DESCRIPCION DE LA MERCANCIA
                //EN CADA ITERACION DEL BUCLE SE AGREGA CADA RENGLON DE UN PRODUCTO
                Constant.MENSAJE_ENTREGA += res.getString(2) + "-" + res.getString(3) + "-" + res.getString(4) + " ";




            } while (res.moveToNext());


            lview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String idSalida = salidasIdArray.get(i-1);

                    Log.d("dbg-idSalida", idSalida);

                    String id = ((TextView) view.findViewById(R.id.FifthText)).getText().toString();
                    String precio = ((TextView) view.findViewById(R.id.SecondText)).getText().toString();
                    Log.d("dbg-onclick", precio);
                    lanzaPregunta(id, precio, idSalida);

                    return false;
                }

            });


        }
        else{
            Constant.TMPMOV_OUTPUT = false;
        }

        Log.d("dbg-output",String.valueOf(Constant.TMPMOV_OUTPUT));


        //AL FINAL AL MENSAJE LE ANEXAMOS EL LA FECHA DE CLIENTES_ADMIN_VENCIMIENTO.
        //USAMOS UN TOAS COMO AUXILIAR PARA MOSTRAR LOS RESULTADOS, EL UNICO DETALLITO ES QUE EL IMPORTE TOTAL
        //EN EL TOAS NO SE ACTUALIZA POR EL ORDEN DE LOS METODOS
        //YA QUE PRIMERO SE LLAMA A listarSalidas() y despues a dameTotalesSalida

        //Constant.MENSAJE_ENTREGA += "\n" + "Importe:" + "\n" + Constant.TMPMOV_TOPAY + "\n" + "Vencimiento:" + "\n" +Constant.TMPMOV_EXPIRATION_DATE;

        Constant.MENSAJE_ENTREGA += "," + Constant.TMPMOV_EXPIRATION_DATE;

        //Toast.makeText(this,Constant.MENSAJE_ENTREGA,Toast.LENGTH_LONG).show();

    }

    public void dameTotalesSalida(String idMovimiento){

        Cursor res = dbHelper.dameSalidas(idMovimiento);

        int p = 0;
        int i = 0;

        Log.d("dbg-dameDalidas",String.valueOf(res.getCount()));

        if(res.getCount() > 0){

            res.moveToNext();

            do{

                int rowCantidad = Integer.parseInt(res.getString(res.getColumnIndex(dbHelper.DETALLES_CANTIDAD)));
                int rowCosto    = Integer.parseInt(res.getString(res.getColumnIndex(dbHelper.DETALLES_PRECIO_DISTRIBUCION)));

                int rowTotal    = rowCosto * rowCantidad;

                i = i + rowCantidad;
                p = p + rowTotal;
            }
            while(res.moveToNext());

        }

        txtPiezasTotales.setText(String.valueOf(i));
        txtImporteTotal.setText("$ " + String.valueOf(p));
        importeTotalSalida = p;
        c.TMPMOV_TOPAY = String.valueOf(p);

    }

    public void lanzaPregunta(String id, String precio, String idRow){

        Log.d("dbg-idRow",idRow);
        dialogoConfirmacionSalida(this, id, precio, idRow);

    }

    public void dialogoConfirmacionSalida(final Activity activity, final String idSalida, final String precio, final String idRow) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("¡Cuidado!");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("¿Desea elimiar el registro o añadrilo como regalo?")
                .setPositiveButton("Eliminarlo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        eliminarSalida(idSalida, idRow);
                    }
                })
                .setNegativeButton("Hacerlo Regalo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        regalarSalida(idSalida, precio, idRow);
                    }
                });

        builder.create();
        builder.show();
    }

    public void eliminarSalida(String idSalida, String idRow){
        Log.d("dbg-elimina", String.valueOf(idSalida + "," + c.TMPMOV_ID));

        if(dbHelper.eliminaDetalleSalida(c.TMPMOV_ID, idSalida, idRow) >= 1) {
            dameTotalesSalida(c.TMPMOV_ID);
            listaSalidas();
        }
        else{
            Log.d("dbg-elimina-error", "Error otravez");
        }
    }

    public void regalarSalida(String idSalida, String precio, String idRow){
        Log.d("dbg-salida", String.valueOf(idSalida + "," + c.TMPMOV_ID));

        if(dbHelper.detalles_actualizaDetalle(c.TMPMOV_ID, idSalida, precio, idRow) >= 1) {
            dameTotalesSalida(c.TMPMOV_ID);
            listaSalidas();
        }
        else{
            Log.d("dbg-elimina-error", "Error otravez");
        }
    }


    /*@Override
    public void onBackPressed() {

        Intent m = new Intent(this, AltaMovimientoActivity.class);
        startActivity(m);
    }*/

    @Override
    public boolean onLongClick(View view) {
        return false;
    }




    //BUSCAR DOCUMENTACION LUISDA Pg1
    public void regresar (){

            c.PIEZAS_ENTREGADAS = Integer.parseInt(txtPiezasTotales.getText().toString());
            //c.IMPORTE_ENTREGADO = txtImporteTotal.getText().toString();
            Constant.IMPORTE_ENTREGADO = importeTotalSalida;
            reconocerCuandoSeRegala();

            finish();
    }



    private void reconocerCuandoSeRegala(){

       if (dbHelper.hayRegalos(Constant.TMPMOV_ID)){
           Log.i("Hay Regalos", "True");
           Constant.HAY_REGALOS = true;
       }else{
           Log.i("Hay Regalos", "False");
           Constant.HAY_REGALOS = false;
       }


       //BUscamos la mercancia regalada y obtenemos el total de puntos canjeados
        //multiplicando las piezas totales por el codigo total

        Constant.PUNTOS_CANJEADOS = dbHelper.obtenerPuntosCanjeados(Constant.TMPMOV_ID);
        Log.i("PuntosCanjeados",String.valueOf(PUNTOS_CANJEADOS));

    }


    /*public void presionoFlechaAtras(final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        int t = Integer.parseInt(txtPiezasTotales.getText().toString());

        if (t!=0){//añadimos este if es para cuando volvemos de alta salida y el usuario no capturo ningun producto entonces no nos aparese el mensaje de reporte de voz.

            TextView title = new TextView(activity);
            title.setText("Title");
            title.setPadding(10, 10, 10, 10);
            title.setGravity(Gravity.CENTER);
            title.setTextSize(20);

            builder.setTitle("FIRMA DE VOZ OBLIGATORIA");
            builder.setIcon(R.drawable.icono_pregunta);

            builder.setMessage("Recuerda que debes capturar la firma por voz del cliente y enviarla a Administracion por WhatsApp")
                    .setPositiveButton("Generar Firma", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Intent m = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                            //startActivity(m);
                            llamarWhatsApp();
                            //enviarMensajeAutomatico();
                        }
                    })
                    .setNegativeButton("Ya genere mi firma", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            regresarActivity();
                        }
                    });

            builder.create();
            builder.show();
        }//fin de if
        else{
            regresarActivity();
        }//fin de else
    }//fin de metodo presionar flecha atras*/


    /*public void llamarWhatsApp (){
        String nombrerecibido = getIntent().getStringExtra("nombreCliente"); //recibimos el valor de nombre enviado desde AltaMovimientoActivity.java
        String piezasTotales = txtPiezasTotales.getText().toString();
        String importeTotal = txtImporteTotal.getText().toString();
        final String telefono = "527121590729";
        final String mensaje = "Entrega de mercancia al cliente: "+nombrerecibido + " por: " + piezasTotales + "pz"+" con un importe de: "+importeTotal; // concatenamos el nombre del cliente
        PackageManager packageManager = this.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "https://api.whatsapp.com/send?phone="+ telefono +"&text=" + URLEncoder.encode(mensaje, "UTF-8")+"\n\n\n\n\n\n\n";
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));

            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }//fin de metodo llamarWhatsApp*/


}

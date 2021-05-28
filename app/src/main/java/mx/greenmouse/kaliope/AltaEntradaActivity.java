
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
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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

public class AltaEntradaActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {


    public static final long DURATION_TRANSITION = 1000 ;
    private Transition transition;
    DataBaseHelper dbHelper = new DataBaseHelper(this);
    Constant c = new Constant();

    TextView txCodigoProducto;
    TextView txCantidadProducto;
    TextView txPiezasTotales;
    TextView txImporteTotal;

    ImageButton btnAgregarEntrtada;
    Button btTermineEntrada;

    public static ListView lview;
    private ArrayList<HashMap> list;

    Map mapGradoClienteEntrada;

    SoundPool soundPool;
    int carga;
    Vibrator v;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alta_entrada);
        getSupportActionBar().hide();


        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));


        mapGradoClienteEntrada     = new HashMap();

        mapGradoClienteEntrada.put("VENDEDORA",dbHelper.INVENTARIO_PRECIO_VENDEDORA);
        mapGradoClienteEntrada.put("SOCIA",dbHelper.INVENTARIO_PRECIO_SOCIA);
        mapGradoClienteEntrada.put("EMPRESARIA",dbHelper.INVENTARIO_PRECIO_EMPRESARIA);

        txCodigoProducto    = (TextView)findViewById(R.id.txtCodigoProducto);
        txCantidadProducto  = (TextView)findViewById(R.id.txtCantidadProductos);
        txPiezasTotales     = (TextView)findViewById(R.id.txtPiezasTotalesEntrada);
        txImporteTotal      = (TextView)findViewById(R.id.txtImporteTotalEntrada);


        txPiezasTotales.setEnabled(false);
        txPiezasTotales.setClickable(false);
        txPiezasTotales.setFocusable(false);
        txPiezasTotales.setText("0");

        txImporteTotal.setEnabled(false);
        txImporteTotal.setClickable(false);
        txImporteTotal.setFocusable(false);
        txImporteTotal.setText("$ 0");

        btnAgregarEntrtada  = (ImageButton)findViewById(R.id.btnAgregarEntrada);
        btTermineEntrada    = (Button)findViewById(R.id.btnTermineEntradas);

        btnAgregarEntrtada.setOnClickListener(this);
        btTermineEntrada.setOnClickListener(this);
        btnAgregarEntrtada.setVisibility(View.GONE);


        lview = (ListView) findViewById(R.id.listViewEntrada);
        lview.setLongClickable(true);

        dameTotalesEntrada(c.TMPMOV_ID);
        listaEntradas();

        txCantidadProducto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    agregaEntrada();
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

        switch (v.getId()) {
            case R.id.btnAgregarEntrada:

                agregaEntrada();

                break;

            case R.id.btnTermineEntradas:

                //Intent m = new Intent(this, AltaMovimientoActivity.class);
                //startActivity(m);
                //presionoFlechaAtras(this);
                //transition = new Slide(Gravity.START);


                //iniciarActividadSiguiente();

                regresar();

                break;


        }

    }

    @SuppressWarnings("unchecked")
    private  void iniciarActividadSiguiente (){
        transition.setDuration(DURATION_TRANSITION);
        transition.setInterpolator(new DecelerateInterpolator());
        getWindow().setExitTransition(transition);
        Intent p = new Intent(this, Cierre.class);
        startActivity(p, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());

    }



    //Lanzar pregunta al precionar la tecla volver. para recordarle al agente tomar su reporte de voz en whats app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            // Esto es lo que hace mi botón al pulsar ir a atrás
            //presionoFlechaAtras(this);
            regresar();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }//fin pregunta al precionar boton atras



    public void listaEntradas(){

        // VAMOS A MODIFICAR DONDE SE CREA EL LISTVIEW CADA VEZ
        // EMPEZAMOS CON LA DEVOLUCION DE MERCANCIA, VAMOS A CREAR UNA CONSTANTE QUE SE LLAME
        // MENSAJE DEVOLUCION DE MERCANCIA, Y EN LA LINEA EN DONDE SE CREA EL LISTVIEW CREAMOS EL MENSAJE
        // VAMOS A SUMARLE AL STRING LO QUE YA TIENE
        // COMO DEBE QUEDAR EL MENSAJE FINAL:
        // piezasDevueltas,improterDevuelto,DescripcionDePiezadevueltas)


        //añadimos total de piezas y el impórte
        Constant.MENSAJE_DEVUELTO = Integer.parseInt(txPiezasTotales.getText().toString()) + "," + Constant.TMPMOV_TOREFUND + ",";

        list = new ArrayList<HashMap>();

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

        Cursor res = dbHelper.detalles_dameEntradas(c.TMPMOV_ID);
        Log.d("dbg-resEntrdas", String.valueOf(res.getCount()));


        if(res.getCount()>0) {

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

                //añadimos la descripcion de los productos solo con las piezas y con los precios
                Constant.MENSAJE_DEVUELTO += res.getString(2) + "-" + res.getString(3) + " ";

            } while (res.moveToNext());


            lview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String id = ((TextView) view.findViewById(R.id.FifthText)).getText().toString();

                    lanzaPregunta(id);

                    return false;
                }

            });


        }
        else{
            Constant.TMPMOV_INPUT = false;
        }

        Log.d("dbg-input",String.valueOf(Constant.TMPMOV_INPUT));

        //Toast.makeText(this,Constant.MENSAJE_DEVUELTO , Toast.LENGTH_LONG).show();

    }

    public void dameTotalesEntrada(String idMovimiento){

        Cursor res = dbHelper.detalles_dameEntradas(idMovimiento);

        int p = 0;
        int i = 0;

        Log.d("dbg-dameEstrdas",String.valueOf(res.getCount()));

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

            lview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String id = ((TextView) view.findViewById(R.id.FifthText)).getText().toString();
                    lanzaPregunta(id);

                    return false;
                }

            });

        }


        txPiezasTotales.setText(String.valueOf(i));
        txImporteTotal.setText("$ " + String.valueOf(p));
        c.TMPMOV_TOREFUND = String.valueOf(p);

    }

    public void agregaEntrada(){
        String codigoProductoEntrada = txCodigoProducto.getText().toString();
        String cantidadProductoEntrada = txCantidadProducto.getText().toString();

        if (codigoProductoEntrada.equals("") || cantidadProductoEntrada.equals("") || cantidadProductoEntrada.equals("0")) {
            utilidadesApp.dialogoAviso(this, "Se require tanto el código del producto como la cantidad de piezas.");
        } else {

            /*//es para que coteje que no se ingreso previamente un mismo codigo si fue asi arroja un error en el else que esta comentado
            Cursor dameEntradaCodigo = dbHelper.dameEntradaCodigo(c.TMPMOV_ID, codigoProductoEntrada);

            if (dameEntradaCodigo.getCount() == 0) {*/

                int cantidadProductoEntradaI = Integer.parseInt(cantidadProductoEntrada);
                Cursor detalleProducto = dbHelper.inventario_dameInformacionCompletaDelProducto(codigoProductoEntrada);
                detalleProducto.moveToNext();

                if (detalleProducto.getCount() == 0) {
                    utilidadesApp.dialogoAviso(this, "El código del producto no existe ó no se encuentra en su inventario.");
                } else {

                    String colGradoCliente = (String) mapGradoClienteEntrada.get(c.TMPMOV_GRADE); //la clave del mapa es VENDEDORA SOCIA EMPRESARIA, TMPMOV_GRADE SE LLENA CON EL DATO QUE VIENE DE LA BASE DE DATOS DE CLIENTES VENDEDORA SOCIA EMPRESARIA, Y
                    //EL MAP RETORNA LAS CONSTANTES GUARDADAS EN LA DATABASEHELPER ONE GRADE, INVENTARIO_PRECIO_SOCIA,INVENTARIO_PRECIO_EMPRESARIA

                    int colIndexCosto = detalleProducto.getColumnIndex(colGradoCliente);

                    //int importeActual = Integer.parseInt(c.TMPMOV_TOREFUND);
                    //int valorProducto = Integer.parseInt(detalleProducto.getString(colIndexCosto));

                    //int nuevoSaldo = (valorProducto * cantidadProductoEntradaI) + importeActual;

                    ContentValues cv = new ContentValues(10);

                    int Ganancia = Integer.parseInt(detalleProducto.getString(1)) - Integer.parseInt(detalleProducto.getString(colIndexCosto));

                    cv.put(dbHelper.DETALLES_ID_DEL_MOVIMIENTO, c.TMPMOV_ID);
                    cv.put(dbHelper.DETALLES_CANTIDAD, cantidadProductoEntrada);
                    cv.put(dbHelper.DETALLES_PRECIO_PRODUCTO, detalleProducto.getString(1));
                    cv.put(dbHelper.DETALLES_PRECIO_DISTRIBUCION, detalleProducto.getString(colIndexCosto));
                    cv.put(dbHelper.DETALLES_GANANCIA, String.valueOf(Ganancia));
                    cv.put(dbHelper.DETALLES_CODIGO_PRODUCTO, codigoProductoEntrada);
                    cv.put(dbHelper.DETALLES_TIPO_MOVIMIENTO, "E");
                    cv.put(dbHelper.DETALLES_LATITUD, Constant.INSTANCE_LATITUDE);
                    cv.put(dbHelper.DETALLES_LONGUITUD, Constant.INSTANCE_LONGITUDE);
                    cv.put(dbHelper.ESTADO_DE_LA_COLUMNA, "A");
                    cv.put(dbHelper.DATE_UP, utilidadesApp.dameHora());

                    if (dbHelper.insertarDetalles(cv) == -1) {
                        utilidadesApp.dialogoAviso(this, "Ocurrió un problema al registrar el movimiento.");
                    } else {

                        v.vibrate(400);
                        soundPool.play(carga,1,1,0,0,1);

                        Constant.TMPMOV_INPUT = true;

                        dameTotalesEntrada(c.TMPMOV_ID);
                        listaEntradas();


                        txCodigoProducto.setText("");
                        txCantidadProducto.setText("");

                        txCodigoProducto.requestFocus();

                    }
                }
            /*} else {
                utilidadesApp.dialogoAviso(this, "El código ya está registrado, si necesita ingresar mas cantidad borre el registro previo.");
            }*/

        }
    }

    public void lanzaPregunta(String id){
        confirmaEliminacion(this, id);

    }

    public void confirmaEliminacion(final Activity activity, final String idEntrada) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("¡Cuidado!");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("¿Realmente desea elimiar el registro?")
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

    public void eliminaEntrada(String idEntrada){

        if(dbHelper.detalles_eliminaEntrada(c.TMPMOV_ID, idEntrada) >= 1) {
            dameTotalesEntrada(c.TMPMOV_ID);
            listaEntradas();

        }
        else{
            Log.d("dbg-elimina-error", "Error otravez");
        }


    }



    @Override
    public boolean onLongClick(View view) {
        return false;
    }




    //BUSCAR DOCUMENTACION LUISDA Pg1
    public void regresar (){

            c.PIEZAS_DEVUELTAS = Integer.parseInt(txPiezasTotales.getText().toString());
            c.IMPORTE_DEVUELTO = txImporteTotal.getText().toString();

        finish();
    }
}

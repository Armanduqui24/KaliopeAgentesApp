package mx.greenmouse.kaliope;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import mx.com.kaliope.luisda.KaliopeServerClient;

public class  MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int MY_PERMISSION_FINE_LOCATION_CODE = 10;

    Button      btIngresar;
    EditText    etUsuario,pwCredencial;
    TextView tvInformacion,tvVersionApp, tvEstadoConexion;
    ImageView ivlogotipoKaliope;
    RelativeLayout mainRelativeLayout;

    DataBaseHelper dbHelper = new DataBaseHelper(this);
    String m, t, userToken;



    ProgressDialog progressDialog;



    VariablePassword variablePassword;

    boolean continuarHiloHacerPing = true;






    //instanciamos nuestros objetos para sonido y vibracion
    SoundPool soundPool;
    int sonido;
    Vibrator vibrator;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        variablePassword = new VariablePassword();
        activity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();



        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){

            Constant.PERMISOS_NECESARIOS_OTORGADOS = false;


        }else {
            Constant.PERMISOS_NECESARIOS_OTORGADOS = true;
            iniciarServiciosDependientesDePermisos();//iniciamos el servicio de localizacion y escribimos la carpeta donde estaran los datos en la memoria

            //VersionNameLuisda6.5
            //examples of metods to obtain IMEI and other ids
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String androidID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d("ID","Android ID: " + androidID);
            //Log.d("ID","Device ID: " + telephonyManager.getDeviceId()); //se requiere permiso te
            Log.d("ID","Device MODEL: " + Build.MODEL);


            try {
                Cursor prueba = dbHelper.inventario_dameInformacionCompletaDelProducto("359");
                prueba.moveToFirst();
                if(prueba.getCount()>0){
                    Log.d ("valorDeITEMpRICEI" , prueba.getString(prueba.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENTA_PRODUCTO)));
                    Log.d ("valorDeOneGradeI" , prueba.getString(prueba.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENDEDORA)));
                    Log.d ("valorDetwOGradeI" , prueba.getString(prueba.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_SOCIA)));
                    Log.d ("valorDeTHREEGradeI" , prueba.getString(prueba.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_EMPRESARIA)));
                }else {
                    Log.d ("conteo: " , "0");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }







        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC,0);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);


        //instanciamos la vibracion
        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

        btIngresar = (Button) findViewById(R.id.btIngresar);
        etUsuario = (EditText) findViewById(R.id.mainUsuarioET);
        pwCredencial = (EditText) findViewById(R.id.pwCredencial);
        tvInformacion = (TextView) findViewById(R.id.numeroPulceraMainTV);
        tvVersionApp = (TextView) findViewById(R.id.textView2);
        ivlogotipoKaliope  =(ImageView) findViewById(R.id.imgLogoKaliope);
        tvEstadoConexion = (TextView) findViewById(R.id.mainEstadoConexionTv);

        ivlogotipoKaliope.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                return false;
            }
        });



        try{
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);

            tvVersionApp.setText("®Kaliope México 2018 Version:" + packageInfo.versionName);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }




        etUsuario.setText("");
        pwCredencial.setText("");
        btIngresar.setOnClickListener(this);








        //(keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (


        //Manejo de la tecla "enter" en el edit text, esto para manejar el evento cuando
        //el escaner de codigo de barras ingresa el enter dijital despues de escanear
        //y tambien al presionar el boton "realizado" en el teclado numerico del celular
        pwCredencial.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){   //este era originalmente el fragmento encontrado en internet lo resumi a solo keycode_enter
                    //el problema con el resumido es que de alguna manera se llamaba 2 veces al evento porque tambien marcaba ActionDown entonces
                    //ejecutaba 2 veces el mismo metod y generaba errores, tube escribirlo de la manera original
                    //if (i == KeyEvent.KEYCODE_ENTER){//este es el resumido que puede llegar a ejecutar 2 veces el mismo codigo aqui no pasa porque llama al metodo ingresar que salta a otro activity
                    //Toast.makeText(getApplicationContext(),"tecla enter",Toast.LENGTH_SHORT).show();
                    //ingresar();
                    return true;
                }

                return false;
            }
        });

    }




    @Override
    public void onResume (){
        super.onResume();
        //Toast.makeText(this,"onResume",Toast.LENGTH_LONG).show();




        String mensaje  ="nombre: " + ConfiguracionesApp.getNombreEmpleado(activity) +
                "Estado Sesion: " + ConfiguracionesApp.getEstadDeSesion(activity) +
                "Ruta: " + ConfiguracionesApp.getRutaAsignada(activity)+
                "Pulsera: " + ConfiguracionesApp.getCodigoPulseraAsignada(activity) +
                "Usuario: " + ConfiguracionesApp.getUsuarioIniciado(activity);

        //tvInformacion.setText(mensaje);


        registerReceiver(networkStateReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        tvEstadoConexion.setBackgroundColor(Color.RED);
        tvEstadoConexion.setText("Sin Internet");
        if (ConfiguracionesApp.getEstadDeSesion(activity)){
            ingresar();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
        continuarHiloHacerPing = false;
    }




    @Override
    public void onClick(View v) {

        //solo activamos la funcion de los botones cuando esta validada la app



            if(Constant.PERMISOS_NECESARIOS_OTORGADOS){
                iniciarServiciosDependientesDePermisos();

                    //Activamos los botones
                        switch (v.getId()) {

                            case R.id.btIngresar:

                                if(etUsuario.getText().toString().equals("")){
                                    Toast.makeText(getApplicationContext(), "No has ingresado un usuario", Toast.LENGTH_SHORT).show();
                                }else if (pwCredencial.getText().toString().equals("")){
                                    Toast.makeText(getApplicationContext(), "No has ingresado un Password", Toast.LENGTH_SHORT).show();
                                }else {

                                        iniciarSesion();

                                }



                                break;

                        }





            }else {
                solicitudDePermisos();
            }




    }



    private void iniciarServiciosDependientesDePermisos (){
        //para que puedamos de alguna manera comprobar que los permisos ya estan otorgados y llamar a este metodo para hacer ciertas cosas

        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));

        //escribimos en la memoria la carpeta
        Constant.INSTANCE_PATH = String.valueOf(Environment.getExternalStorageDirectory());

        File nuevaCarpeta = new File(Constant.INSTANCE_PATH, "mx.4103.klp");
        nuevaCarpeta.mkdirs();
        //Constant.INSTANCE_PATH = System.getenv("SECONDARY_STORAGE");
        //Constant.INSTANCE_PATH = android.os.Environment.DIRECTORY_DCIM;
        //Constant.INSTANCE_PATH = this.getExternalMediaDirs();

        Log.d("dbg-GED","Path: " + Constant.INSTANCE_PATH);

    }



    private void ingresar (){

            sonido = soundPool.load(getApplicationContext(),R.raw.exito,1);
            soundPool.play(sonido,1,1,0,0,1);
            vibrator.vibrate(400);


            //(CONSULTAMOS NUESTRA BASE DE DATOS DE NUMEROS DE CUETNA, SI SU CONTADOR DE REGISTROS
            // NOS ARROJA 0 LLAMAMOS AL METODO QUE LLENA LA BASE DE DATOS CON NUMEROS CONSECUTIVOS DEL 1 AL 30
            // SI NOS ARROJA MAS DE 0 ES DECIR QUE ESTA LLENA ENTONCES NO LO LLAMAMOS)
            Cursor resNumClientes = dbHelper.obtenerTodosLosNumerosDeCuenta();
            if (resNumClientes.getCount()== 0){
                dbHelper.insertarNumerosCuenta();
                Log.i("DENTRO DE LLENADO" , "DE NUMEROS DE CUENTA");
            }

            Log.i("total en cuenta " , String.valueOf(resNumClientes.getCount()));

                            Intent i = new Intent(this, MenuPrincipalActivity.class);
                            startActivity(i);






    }





    /**
     * Solicitar permisos de Geolocalizacion
     * */


    void solicitudDePermisos(){

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE},MY_PERMISSION_FINE_LOCATION_CODE);

        //VersionNameLuisda6.5 READ_PHONE_STATE

    }


    //llamamos a nuestro manejador de eventos que se invoca cuando el usuario responde al permiso
    //el Callback recibe el mismo codigo de solicitud que le pasaste a requestPermissions()
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //VersionNameLuisda6.5 READ_PHONE_STATE


        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){

            Constant.PERMISOS_NECESARIOS_OTORGADOS = true;
            startActivity(new Intent(this,MainActivity.class));
        }else {
            Constant.PERMISOS_NECESARIOS_OTORGADOS = false;
        }

    }






    @Override
    protected void onStop() {
        super.onStop();
        //finish();
    }



    //coneccion al servidor
    public void iniciarSesion (){
        String uniqueUUID;
        if (ConfiguracionesApp.getCodigoUnicoDispositivo(activity).equals("SinValor")){
            //si aun no se guarda en las preferencias el UUID lo creamos y lo guardamos.
            uniqueUUID  = UUID.randomUUID().toString();
            Log.d("ID","UUID ID: " + uniqueUUID);
            ConfiguracionesApp.setCodigoDispositivoUnico(activity,uniqueUUID);
        }else{
            //corroboramos que el UUID se haya guardado en las preferencias
            uniqueUUID = ConfiguracionesApp.getCodigoUnicoDispositivo(activity);
            Log.d("ID","UUID ID preferences: " + uniqueUUID);
        }


        showProgresDialog(); //mostramos el progreso  indeterminado


        RequestParams parametros = new RequestParams();
        parametros.put("alias" , etUsuario.getText().toString());
        parametros.put("password" , pwCredencial.getText().toString());
        parametros.put("modeloDispositivo" , Build.MODEL);
        parametros.put("UUID" , uniqueUUID);


        KaliopeServerClient.get("app_kaliope/iniciar_sesion_dos_rutas.php",parametros,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();
                //el servidor nos enviara como respuesta un jsonObjet, dentro de ese jsonObjet
                //enviaremos un jsonArray que es el inventario y otro jsonAray el catalogo de clientes
                //tambien podemos enviar solo objetos!!!
                //http://thebestandroide.blogspot.com/2014/11/crear-y-leer-json-desde-android.html?m=1
                //https://support.brightcove.com/es/concepts-introducing-json
                //solo basta entender la forma en como se construllen los json
                InventarioActivity inventarioActivity = new InventarioActivity();
                Clientes llenarClientes = new Clientes();
                Log.d ("datosRecibidos",String.valueOf(response));



             /*{
               "inventario":[{"id":"1933","0":"1933","propietario":"Eduardo","1":"Eduardo","codigo":"29","2":"29","existencia":"0","3":"0","precio":"29","4":"29","vendedora":"22","5":"22","socia":"21","6":"21","empresaria":"21","7":"21","version":"25","8":"25","enviado_al_movil":"1","9":"1","hora_de_envio_al_movil":"21-11-2019  14:33:11","10":"21-11-2019  14:33:11","hora_sincronizado_desde_el_movil":"","11":""},{"id":"1934","0":"1934","propietario":"Eduardo","1":"Eduardo","codigo":"39","2":"39","existencia":"0","3":"0","precio":"39","4":"39","vendedora":"30","5":"30","socia":"29","6":"29","empresaria":"28","7":"28","version":"25","8":"25","enviado_al_movil":"0","9":"0","hora_de_envio_al_movil":"","10":"","hora_sincronizado_desde_el_movil":"","11":""}]
              ,"informacion":{"id":"Bienvenido Eduardo","nombre":"otro valor que quiera"}
              ,"zona":[{"zona":"CANALEJAS","zonificacion":"-99.6323495443894,19.93921032464321,0 -99.60848303176516,19.9479430714427,0 -99.59240270694212,19.95660140905689,0 -99.5751407982179,19.95659581871729,0 -99.52877487825697,19.99720081430345,0 -99.56509305729576,20.01590022179094,0 -99.5783381825041,20.02086692872206,0 -99.59716294841314,20.03425292013289,0 -99.60888307613377,20.04452049981353,0 -99.61991046589195,20.06078546332277,0 -99.62985801711589,20.06574325720041,0 -99.63869967277995,20.07157680717048,0 -99.66282677387721,20.0771827"},{"zona":"SAN JUAN DEL RIO","zonificacion":"-99.89717659228742,20.37502814621686,0 -99.87817863289942,20.43404142269898,0 -99.88849910583497,20.45004231623238,0 -99.95476702721507,20.49148390033696,0 -99.98413855211652,20.44735925623536,0 -100.0077545860647,20.41461869873617,0 -100.0289065842632,20.3799446837511,0 -100.0133382078995,20.35783191484072,0 -99.97663671220526,20.33929030040358,0 -99.94371403701467,20.35160342966244,0 -99.89717659228742,20.37502814621686,0"}]
              ,"clientes":[{"zona":"CANALEJAS","clientes":[{"cuenta":"1146","0":"1146","nombre":"ANA LAURA JUAREZ ARELLANO","1":"ANA LAURA JUAREZ ARELLANO","telefono":"55 39 60 97 72","2":"55 39 60 97 72","dias":"28","3":"28","grado":"EMPRESARIA","4":"EMPRESARIA","credito":"4000","5":"4000","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.021389875920317","7":"20.021389875920317","longitud_fija":"-99.66920361254584","8":"-99.66920361254584","adeudo_cargo":"0","9":"0","piezas_cargo":"13","10":"13","importe_cargo":"2687","11":"2687","fecha_vence_cargo":"12-09-2019","12":"12-09-2019","puntos_disponibles":"0","13":"0","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"1-159-0 1-279-0 1-159-0 4-299-239 2-309-247 1-339-271 2-369-295 1-459-376 ","16":"1-159-0 1-279-0 1-159-0 4-299-239 2-309-247 1-339-271 2-369-295 1-459-376 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 3099*Saldo: 0*Reporte: TODO BIEN****01-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****18-07-2019* Pago: 2700*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 3099*Saldo: 0*Reporte: TODO BIEN****01-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****18-07-2019* Pago: 2700*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"1148","0":"1148","nombre":"MANALI FLORES","1":"MANALI FLORES","telefono":"0","2":"0","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"1500","5":"1500","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.96900203","7":"19.96900203","longitud_fija":"-99.55851103","8":"-99.55851103","adeudo_cargo":"241","9":"241","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"15-08-2019","12":"15-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"LIQUIDA EN 15 DIAS","14":"LIQUIDA EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 200*Saldo: 241*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****18-07-2019* Pago: 77*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 250*Saldo: 77*Reporte: LIQUIDA EN 15 DIAS**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 200*Saldo: 241*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****18-07-2019* Pago: 77*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 250*Saldo: 77*Reporte: LIQUIDA EN 15 DIAS**"},{"cuenta":"1149","0":"1149","nombre":"YOLANDA SANTIAGO AGUILAR","1":"YOLANDA SANTIAGO AGUILAR","telefono":"0","2":"0","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"1400","5":"1400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":" 20.044472","7":" 20.044472","longitud_fija":"-99.655413","8":"-99.655413","adeudo_cargo":"0","9":"0","piezas_cargo":"5","10":"5","importe_cargo":"1345","11":"1345","fecha_vence_cargo":"29-08-2019","12":"29-08-2019","puntos_disponibles":"100","13":"100","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"0","15":"0","mercancia_cargo":"1-329-274 1-299-249 1-309-257 1-359-299 1-319-266 ","16":"1-329-274 1-299-249 1-309-257 1-359-299 1-319-266 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES, PERO DICE HIJO QUE NO ESTABA****04-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES DICEN VECINOS QUE NO ABRIO**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES, PERO DICE HIJO QUE NO ESTABA****04-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES DICEN VECINOS QUE NO ABRIO**"},{"cuenta":"1150","0":"1150","nombre":"ANTONIA GARCIA MIRANDA ","1":"ANTONIA GARCIA MIRANDA ","telefono":"0","2":"0","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"1700","5":"1700","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.94023779","7":"19.94023779","longitud_fija":"-99.56489413","8":"-99.56489413","adeudo_cargo":"639","9":"639","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"01-08-2019","12":"01-08-2019","puntos_disponibles":"550","13":"550","reporte_agente":"QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS","14":"QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS****15-08-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS****01-08-2019* Pago: 500*Saldo: 639*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 214*Saldo: 1372*Reporte: LIQUIDA EN 15 DIAS****04-07-2019* Pago: 928*Saldo: 214*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS****15-08-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS****01-08-2019* Pago: 500*Saldo: 639*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 214*Saldo: 1372*Reporte: LIQUIDA EN 15 DIAS****04-07-2019* Pago: 928*Saldo: 214*Reporte: TODO BIEN**"},{"cuenta":"1153","0":"1153","nombre":"LIZBETH JIMENEZ CAMACHO","1":"LIZBETH JIMENEZ CAMACHO","telefono":"55 82 21 24 43","2":"55 82 21 24 43","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"0","5":"0","estado":"LIO","6":"LIO","latitud_fija":" 19.975901","7":" 19.975901","longitud_fija":"-99.610482","8":"-99.610482","adeudo_cargo":"374","9":"374","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"11-04-2019","12":"11-04-2019","puntos_disponibles":"150","13":"150","reporte_agente":"PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA","14":"PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA","reporte_administracion":"INVESTIGAR CON VECINOS","15":"INVESTIGAR CON VECINOS","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA****15-08-2019* Pago: 0*Saldo: 0*Reporte: PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA****01-08-2019* Pago: 0*Saldo: 0*Reporte: SE INVESTIGA CON VECINOS DICEN QUE NO ESTA QUE SALE A TRABAJAR A QUERETARO Y LLEGA DESPUES DE LAS 8****18-07-2019* Pago: 0*Saldo: 0*Reporte: SU ESPOSO JOSE ALBERTO SE COMPROMETE A LIQUIDAR LA CUENTA DE SU ESPOSA EN 15 DIAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO DICEN FAMILIARES QUE SE FUE A QUERETARO**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA****15-08-2019* Pago: 0*Saldo: 0*Reporte: PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA****01-08-2019* Pago: 0*Saldo: 0*Reporte: SE INVESTIGA CON VECINOS DICEN QUE NO ESTA QUE SALE A TRABAJAR A QUERETARO Y LLEGA DESPUES DE LAS 8****18-07-2019* Pago: 0*Saldo: 0*Reporte: SU ESPOSO JOSE ALBERTO SE COMPROMETE A LIQUIDAR LA CUENTA DE SU ESPOSA EN 15 DIAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO DICEN FAMILIARES QUE SE FUE A QUERETARO**"},{"cuenta":"1154","0":"1154","nombre":"ELIZABET GARCIA GONZALES","1":"ELIZABET GARCIA GONZALES","telefono":"0","2":"0","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"1700","5":"1700","estado":"ACTIVO","6":"ACTIVO","latitud_fija":" 20.021470","7":" 20.021470","longitud_fija":"-99.669315","8":"-99.669315","adeudo_cargo":"0","9":"0","piezas_cargo":"6","10":"6","importe_cargo":"1405","11":"1405","fecha_vence_cargo":"29-08-2019","12":"29-08-2019","puntos_disponibles":"50","13":"50","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"0","15":"0","mercancia_cargo":"1-329-270 1-399-327 2-349-286 2-159-118 ","16":"1-329-270 1-399-327 2-349-286 2-159-118 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 412*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO DICE MAMA QUE SALIO QUE NO ESTABA SE DEJA RECADO****04-07-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA EL DINERO PENDIENTE EN 15 DIAS**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 412*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO DICE MAMA QUE SALIO QUE NO ESTABA SE DEJA RECADO****04-07-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA EL DINERO PENDIENTE EN 15 DIAS**"},{"cuenta":"1155","0":"1155","nombre":"ILSE ANDERIK HERNANDEZ MARTINEZ","1":"ILSE ANDERIK HERNANDEZ MARTINEZ","telefono":"56 11 07 48 99","2":"56 11 07 48 99","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1900","5":"1900","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.01093466745951","7":"20.01093466745951","longitud_fija":"-99.58897021733355","8":"-99.58897021733355","adeudo_cargo":"168","9":"168","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"15-08-2019","12":"15-08-2019","puntos_disponibles":"350","13":"350","reporte_agente":"LIQUIDA EN 15 DIAS","14":"LIQUIDA EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 300*Saldo: 168*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 850*Saldo: 177*Reporte: TODO BIEN****18-07-2019* Pago: 524*Saldo: 130*Reporte: TODO BIEN****04-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 300*Saldo: 168*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 850*Saldo: 177*Reporte: TODO BIEN****18-07-2019* Pago: 524*Saldo: 130*Reporte: TODO BIEN****04-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"1156","0":"1156","nombre":"MARIA LUISA HERNANDEZ MARTINEZ","1":"MARIA LUISA HERNANDEZ MARTINEZ","telefono":"0","2":"0","dias":"14","3":"14","grado":"SOCIA","4":"SOCIA","credito":"2400","5":"2400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.97242562551852","7":"19.97242562551852","longitud_fija":"-99.5655873412939","8":"-99.5655873412939","adeudo_cargo":"294","9":"294","piezas_cargo":"8","10":"8","importe_cargo":"2328","11":"2328","fecha_vence_cargo":"29-08-2019","12":"29-08-2019","puntos_disponibles":"200","13":"200","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"3-399-327 3-359-294 1-259-212 1-309-253 ","16":"3-399-327 3-359-294 1-259-212 1-309-253 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 278*Saldo: 294*Reporte: TODO BIEN****01-08-2019* Pago: 1038*Saldo: 294*Reporte: TODO BIEN****18-07-2019* Pago: 294*Saldo: 294*Reporte: TODO BIEN****04-07-2019* Pago: 270*Saldo: 0*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 278*Saldo: 294*Reporte: TODO BIEN****01-08-2019* Pago: 1038*Saldo: 294*Reporte: TODO BIEN****18-07-2019* Pago: 294*Saldo: 294*Reporte: TODO BIEN****04-07-2019* Pago: 270*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"1157","0":"1157","nombre":"ANTONIA HERNANDEZ CAMACHO","1":"ANTONIA HERNANDEZ CAMACHO","telefono":"55 49 69 62 44","2":"55 49 69 62 44","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"2200","5":"2200","estado":"REACTIVAR","6":"REACTIVAR","latitud_fija":"19.954782323839222","7":"19.954782323839222","longitud_fija":"-99.60686100794508","8":"-99.60686100794508","adeudo_cargo":"0","9":"0","piezas_cargo":"2","10":"2","importe_cargo":"0","11":"0","fecha_vence_cargo":"18-07-2019","12":"18-07-2019","puntos_disponibles":"0","13":"0","reporte_agente":"REACTIVAR EN 2 MESES EN AGOSTO","14":"REACTIVAR EN 2 MESES EN AGOSTO","reporte_administracion":"0","15":"0","mercancia_cargo":"1-399-0 1-159-0 ","16":"1-399-0 1-159-0 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****15-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****01-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****18-07-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****04-07-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****15-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****01-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****18-07-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****04-07-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO**"},{"cuenta":"1159","0":"1159","nombre":"EZPERANZA HERNANDEZ FLORENTINO","1":"EZPERANZA HERNANDEZ FLORENTINO","telefono":"0","2":"0","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"2700","5":"2700","estado":"ACTIVO","6":"ACTIVO","latitud_fija":" 19.993895","7":" 19.993895","longitud_fija":"-99.614844","8":"-99.614844","adeudo_cargo":"0","9":"0","piezas_cargo":"6","10":"6","importe_cargo":"1332","11":"1332","fecha_vence_cargo":"12-09-2019","12":"12-09-2019","puntos_disponibles":"0","13":"0","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"INVESTIGAR MAS CON VECINOS","15":"INVESTIGAR MAS CON VECINOS","mercancia_cargo":"1-299-0 1-299-245 1-339-278 1-309-253 1-279-229 1-399-327 ","16":"1-299-0 1-299-245 1-339-278 1-309-253 1-279-229 1-399-327 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 1757*Saldo: 0*Reporte: TODO BIEN****01-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES, QUE ESTA HOSPITALIZADA, SE DEJA RECADO CON HIJA****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 3 VECES SE DEJA RECADO CON HIJAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: DICEN FAMILIARES QUE NO HA REGRESADO DE SU EMERGENCIA DICEN FAMILIARES QUE ES SEGURO QUE PAGE EN 15 DIAS**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 1757*Saldo: 0*Reporte: TODO BIEN****01-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES, QUE ESTA HOSPITALIZADA, SE DEJA RECADO CON HIJA****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 3 VECES SE DEJA RECADO CON HIJAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: DICEN FAMILIARES QUE NO HA REGRESADO DE SU EMERGENCIA DICEN FAMILIARES QUE ES SEGURO QUE PAGE EN 15 DIAS**"},{"cuenta":"1408","0":"1408","nombre":"ORDO\u00d1EZ GARDU\u00d1O YOSELIN","1":"ORDO\u00d1EZ GARDU\u00d1O YOSELIN","telefono":"0","2":"0","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1700","5":"1700","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.96846161","7":"19.96846161","longitud_fija":"-99.56235819","8":"-99.56235819","adeudo_cargo":"145","9":"145","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"01-08-2019","12":"01-08-2019","puntos_disponibles":"200","13":"200","reporte_agente":"LIQUIDA EN 15 DIAS","14":"LIQUIDA EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 650*Saldo: 145*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 200*Saldo: 795*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 507*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 174*Saldo: 507*Reporte: LIQUIDA EN 15 DIAS**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 650*Saldo: 145*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 200*Saldo: 795*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 507*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 174*Saldo: 507*Reporte: LIQUIDA EN 15 DIAS**"},{"cuenta":"3303","0":"3303","nombre":"CLAUDIA ISABEL S\u00c1NCHEZ ","1":"CLAUDIA ISABEL S\u00c1NCHEZ ","telefono":"0","2":"0","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"2000","5":"2000","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.04443258","7":"20.04443258","longitud_fija":"-99.6554648","8":"-99.6554648","adeudo_cargo":"0","9":"0","piezas_cargo":"7","10":"7","importe_cargo":"1398","11":"1398","fecha_vence_cargo":"29-08-2019","12":"29-08-2019","puntos_disponibles":"50","13":"50","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"0","15":"0","mercancia_cargo":"2-329-274 1-359-299 1-159-0 2-199-159 1-279-233 ","16":"2-329-274 1-359-299 1-159-0 2-199-159 1-279-233 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 1182*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO LE MARCO QUE SALIO A TRABAJAR, QUEDA DE ENTREGAR EN 15 DIAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 1182*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO LE MARCO QUE SALIO A TRABAJAR, QUEDA DE ENTREGAR EN 15 DIAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"3433","0":"3433","nombre":"VIRGINIA VARGAS HERNANDEZ ","1":"VIRGINIA VARGAS HERNANDEZ ","telefono":"0","2":"0","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1700","5":"1700","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.97496327","7":"19.97496327","longitud_fija":"-99.61408679","8":"-99.61408679","adeudo_cargo":"285","9":"285","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"01-08-2019","12":"01-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"LIQUIDA EN 15 DIAS","14":"LIQUIDA EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 500*Saldo: 285*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 500*Saldo: 785*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 500*Saldo: 285*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 500*Saldo: 785*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"3783","0":"3783","nombre":"GUILLERMINA MART\u00cdNEZ NAVARRETE ","1":"GUILLERMINA MART\u00cdNEZ NAVARRETE ","telefono":"0","2":"0","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1400","5":"1400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.98794683","7":"19.98794683","longitud_fija":"-99.65328267","8":"-99.65328267","adeudo_cargo":"0","9":"0","piezas_cargo":"6","10":"6","importe_cargo":"1453","11":"1453","fecha_vence_cargo":"29-08-2019","12":"29-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"1-279-233 1-259-216 1-309-257 3-299-249 ","16":"1-279-233 1-259-216 1-309-257 3-299-249 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**"}],"fechaClientesConsulta":"29-08-2019"},{"zona":"SAN JUAN DEL RIO","clientes":[{"cuenta":"2170","0":"2170","nombre":"MARIA ISABEL PICHARDO CAMACHO","1":"MARIA ISABEL PICHARDO CAMACHO","telefono":"427 103 42 23","2":"427 103 42 23","dias":"14","3":"14","grado":"EMPRESARIA","4":"EMPRESARIA","credito":"2500","5":"2500","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.33718862282818","7":"20.33718862282818","longitud_fija":"-99.94939814772276","8":"-99.94939814772276","adeudo_cargo":"0","9":"0","piezas_cargo":"10","10":"10","importe_cargo":"2343","11":"2343","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"50","13":"50","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"1-259-207 2-299-239 1-329-263 1-339-271 3-399-319 1-279-0 1-209-167 ","16":"1-259-207 2-299-239 1-329-263 1-339-271 3-399-319 1-279-0 1-209-167 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 529*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 1123*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 1050*Saldo: 2*Reporte: TODO BIEN****02-07-2019* Pago: 813*Saldo: 0*Reporte: TODO BIEN**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 529*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 1123*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 1050*Saldo: 2*Reporte: TODO BIEN****02-07-2019* Pago: 813*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"2172","0":"2172","nombre":"EZPERANZA MARCELO SINECIO","1":"EZPERANZA MARCELO SINECIO","telefono":"427 593 34 01","2":"427 593 34 01","dias":"28","3":"28","grado":"EMPRESARIA","4":"EMPRESARIA","credito":"2500","5":"2500","estado":"REACTIVAR","6":"REACTIVAR","latitud_fija":"20.4379060153793","7":"20.4379060153793","longitud_fija":"-99.94742208072816","8":"-99.94742208072816","adeudo_cargo":"0","9":"0","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"13-08-2019","12":"13-08-2019","puntos_disponibles":"300","13":"300","reporte_agente":"REACTIVAR EN 15 DIAS","14":"REACTIVAR EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 15 DIAS****13-08-2019* Pago: 1396*Saldo: 0*Reporte: REACTIVAR EN 15 DIAS****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 822*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 15 DIAS****13-08-2019* Pago: 1396*Saldo: 0*Reporte: REACTIVAR EN 15 DIAS****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 822*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"2174","0":"2174","nombre":"ANA LAURA MENDOZA SUAREZ","1":"ANA LAURA MENDOZA SUAREZ","telefono":"266 61 72","2":"266 61 72","dias":"14","3":"14","grado":"SOCIA","4":"SOCIA","credito":"2400","5":"2400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.439199939147645","7":"20.439199939147645","longitud_fija":"-99.89563303688809","8":"-99.89563303688809","adeudo_cargo":"0","9":"0","piezas_cargo":"9","10":"9","importe_cargo":"1968","11":"1968","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"350","13":"350","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"2-159-118 2-209-171 2-279-229 1-339-278 2-399-327 ","16":"2-159-118 2-209-171 2-279-229 1-339-278 2-399-327 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 1145*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 800*Saldo: 1145*Reporte: LIQUIDA EN 15 DIAS****16-07-2019* Pago: 850*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: QUE SU SUEGRA ESTA ENFERMA Y LA ESTA ATENDIENDO DICE MAMA QUE NO DEJO DINERO SE DEJA RECADO**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 1145*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 800*Saldo: 1145*Reporte: LIQUIDA EN 15 DIAS****16-07-2019* Pago: 850*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: QUE SU SUEGRA ESTA ENFERMA Y LA ESTA ATENDIENDO DICE MAMA QUE NO DEJO DINERO SE DEJA RECADO**"},{"cuenta":"2175","0":"2175","nombre":"VICTORIA LOPEZ PEREZ","1":"VICTORIA LOPEZ PEREZ","telefono":"0","2":"0","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"2400","5":"2400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.3325333225939","7":"20.3325333225939","longitud_fija":"-99.98226281483967","8":"-99.98226281483967","adeudo_cargo":"0","9":"0","piezas_cargo":"9","10":"9","importe_cargo":"1635","11":"1635","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"0","15":"0","mercancia_cargo":"1-299-0 1-299-0 1-299-245 1-339-278 4-279-229 1-239-196 ","16":"1-299-0 1-299-0 1-299-245 1-339-278 4-279-229 1-239-196 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****30-07-2019* Pago: 1414*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****02-07-2019* Pago: 785*Saldo: 0*Reporte: TODO BIEN**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****30-07-2019* Pago: 1414*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****02-07-2019* Pago: 785*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"2179","0":"2179","nombre":"MARIA GUADALUPE RAMIREZ PEREZ","1":"MARIA GUADALUPE RAMIREZ PEREZ","telefono":"0","2":"0","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"1900","5":"1900","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.318255430494393","7":"20.318255430494393","longitud_fija":"-99.98400428016876","8":"-99.98400428016876","adeudo_cargo":"0","9":"0","piezas_cargo":"4","10":"4","importe_cargo":"1023","11":"1023","fecha_vence_cargo":"10-09-2019","12":"10-09-2019","puntos_disponibles":"350","13":"350","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"2-279-233 1-299-249 1-369-308 ","16":"2-279-233 1-299-249 1-369-308 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 815*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 1080*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 815*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 1080*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"2180","0":"2180","nombre":"GLORIA CRUZ CRUZ","1":"GLORIA CRUZ CRUZ","telefono":"427 273 13 87","2":"427 273 13 87","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"0","5":"0","estado":"LIO","6":"LIO","latitud_fija":"20.381798019241604","7":"20.381798019241604","longitud_fija":"-99.99014639932028","8":"-99.99014639932028","adeudo_cargo":"400","9":"400","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"02-01-2019","12":"02-01-2019","puntos_disponibles":"300","13":"300","reporte_agente":"NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO","14":"NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO****13-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO****30-07-2019* Pago: 100*Saldo: 400*Reporte: ABONA EN 15 DIAS****16-07-2019* Pago: 300*Saldo: 500*Reporte: ABONA EN 15 DIAS****02-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO SOLO SALE MAMA QUE NO LE DIJO NADA Y NO CONTESTA**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO****13-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO****30-07-2019* Pago: 100*Saldo: 400*Reporte: ABONA EN 15 DIAS****16-07-2019* Pago: 300*Saldo: 500*Reporte: ABONA EN 15 DIAS****02-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO SOLO SALE MAMA QUE NO LE DIJO NADA Y NO CONTESTA**"},{"cuenta":"2184","0":"2184","nombre":"MARIA DEL CARMEN PEREZ CASTA\u00d1EDA","1":"MARIA DEL CARMEN PEREZ CASTA\u00d1EDA","telefono":"0","2":"0","dias":"14","3":"14","grado":"SOCIA","4":"SOCIA","credito":"2400","5":"2400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.32174212","7":"20.32174212","longitud_fija":"-99.97660145","8":"-99.97660145","adeudo_cargo":"0","9":"0","piezas_cargo":"9","10":"9","importe_cargo":"2346","11":"2346","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"150","13":"150","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"3-279-229 1-299-245 1-349-286 1-329-270 1-339-278 1-399-327 1-309-253 ","16":"3-279-229 1-299-245 1-349-286 1-329-270 1-339-278 1-399-327 1-309-253 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 818*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 1421*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 606*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 875*Saldo: 0*Reporte: TODO BIEN**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 818*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 1421*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 606*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 875*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"2316","0":"2316","nombre":"MARIELA HERNANDEZ TREJO ","1":"MARIELA HERNANDEZ TREJO ","telefono":"0","2":"0","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"1900","5":"1900","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.32951403480396","7":"20.32951403480396","longitud_fija":"-99.95643287957716","8":"-99.95643287957716","adeudo_cargo":"0","9":"0","piezas_cargo":"10","10":"10","importe_cargo":"2346","11":"2346","fecha_vence_cargo":"10-09-2019","12":"10-09-2019","puntos_disponibles":"0","13":"0","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"*SE CAMBIA A SOCIA*","15":"*SE CAMBIA A SOCIA*","mercancia_cargo":"1-329-0 3-279-229 1-309-253 2-329-270 1-339-278 1-319-261 1-399-327 ","16":"1-329-0 3-279-229 1-309-253 2-329-270 1-339-278 1-319-261 1-399-327 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 506*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 1322*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 506*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 1322*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"2948","0":"2948","nombre":"ADRIANA RANGEL ","1":"ADRIANA RANGEL ","telefono":"427 116 8549","2":"427 116 8549","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1400","5":"1400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.3880195","7":"20.3880195","longitud_fija":"-99.97540027","8":"-99.97540027","adeudo_cargo":"490","9":"490","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"13-08-2019","12":"13-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"LIQUIDA EN 15 DIAS QUE NO LE PAGARON","14":"LIQUIDA EN 15 DIAS QUE NO LE PAGARON","reporte_administracion":"PENDIENTE EN 15 DIAS PASAR ANTES DE LA 1","15":"PENDIENTE EN 15 DIAS PASAR ANTES DE LA 1","mercancia_cargo":"0","16":"0","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS QUE NO LE PAGARON****13-08-2019* Pago: 0*Saldo: 490*Reporte: LIQUIDA EN 15 DIAS QUE NO LE PAGARON****30-07-2019* Pago: 233*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 0*Saldo: 0*Reporte: SAN JUAN DEL R\u00edO SAN CAYETANO R\u00edO CLARO R\u00edO CLARO 7. CERCA DEL CENTRO M\u00e9DICO COSCAMI.****02-07-2019* Pago: 0*Saldo: 0*Reporte: SAN JUAN DEL R\u00edO SAN CAYETANO R\u00edO CLARO R\u00edO CLARO 7. CERCA DEL CENTRO M\u00e9DICO COSCAMI.**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS QUE NO LE PAGARON****13-08-2019* Pago: 0*Saldo: 490*Reporte: LIQUIDA EN 15 DIAS QUE NO LE PAGARON****30-07-2019* Pago: 233*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 0*Saldo: 0*Reporte: SAN JUAN DEL R\u00edO SAN CAYETANO R\u00edO CLARO R\u00edO CLARO 7. CERCA DEL CENTRO M\u00e9DICO COSCAMI.****02-07-2019* Pago: 0*Saldo: 0*Reporte: SAN JUAN DEL R\u00edO SAN CAYETANO R\u00edO CLARO R\u00edO CLARO 7. CERCA DEL CENTRO M\u00e9DICO COSCAMI.**"},{"cuenta":"3417","0":"3417","nombre":"KARLA JUDITH JUAREZ RIVERA","1":"KARLA JUDITH JUAREZ RIVERA","telefono":"4272715432","2":"4272715432","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1500","5":"1500","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.43945776","7":"20.43945776","longitud_fija":"-99.98591199","8":"-99.98591199","adeudo_cargo":"0","9":"0","piezas_cargo":"6","10":"6","importe_cargo":"1495","11":"1495","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"PENDIENTE EN 15","15":"PENDIENTE EN 15","mercancia_cargo":"1-349-291 1-279-233 1-309-257 1-259-216 1-299-249 1-299-249 ","16":"1-349-291 1-279-233 1-309-257 1-259-216 1-299-249 1-299-249 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: SOY DE SAN PEDRO AHUACATLAN X LA \u00faNIDAD DEPORTIVA HAY UNA DESVIACI\u00f3N SAN JUAN DEL R\u00edO**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: SOY DE SAN PEDRO AHUACATLAN X LA \u00faNIDAD DEPORTIVA HAY UNA DESVIACI\u00f3N SAN JUAN DEL R\u00edO**"},{"cuenta":"2171","0":"2171","nombre":"MARIA ELENA NIETO SALASAR","1":"MARIA ELENA NIETO SALASAR","telefono":"414 105 50 79","2":"414 105 50 79","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"1500","5":"1500","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.47471218","7":"20.47471218","longitud_fija":"-99.93971997","8":"-99.93971997","adeudo_cargo":"0","9":"0","piezas_cargo":"11","10":"11","importe_cargo":"2551","11":"2551","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"200","13":"200","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"","15":"","mercancia_cargo":"1-279-0 6-279-229 1-309-253 1-329-270 2-399-327 ","16":"1-279-0 6-279-229 1-309-253 1-329-270 2-399-327 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****31-07-2019* Pago: 1013*Saldo: 254*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN SE REALIZO CIERRE EN SAN CLEMENTE****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****31-07-2019* Pago: 1013*Saldo: 254*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN SE REALIZO CIERRE EN SAN CLEMENTE****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"2173","0":"2173","nombre":"YAZMIN CRUZ TORRES","1":"YAZMIN CRUZ TORRES","telefono":"427 132 11 00","2":"427 132 11 00","dias":"28","3":"28","grado":"EMPRESARIA","4":"EMPRESARIA","credito":"1500","5":"1500","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.477873","7":"20.477873","longitud_fija":"-99.9341417","8":"-99.9341417","adeudo_cargo":"0","9":"0","piezas_cargo":"13","10":"13","importe_cargo":"2782","11":"2782","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"100","13":"100","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"SE CAMBIA A EMPRESARIA","15":"SE CAMBIA A EMPRESARIA","mercancia_cargo":"1-349-0 1-279-0 1-239-0 1-309-247 2-299-239 3-349-279 1-329-263 3-399-319 ","16":"1-349-0 1-279-0 1-239-0 1-309-247 2-299-239 3-349-279 1-329-263 3-399-319 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****31-07-2019* Pago: 2216*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN SE REALIZO CIERRE EN SAN CLEMENTE****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****31-07-2019* Pago: 2216*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN SE REALIZO CIERRE EN SAN CLEMENTE****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"2177","0":"2177","nombre":"ANA CAREN ALVARES","1":"ANA CAREN ALVARES","telefono":"0","2":"0","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"0","5":"0","estado":"LIO","6":"LIO","latitud_fija":" 20.475300","7":" 20.475300","longitud_fija":"-99.942300","8":"-99.942300","adeudo_cargo":"0","9":"0","piezas_cargo":"4","10":"4","importe_cargo":"1307","11":"1307","fecha_vence_cargo":"26-02-2019","12":"26-02-2019","puntos_disponibles":"0","13":"0","reporte_agente":"DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE","14":"DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE","reporte_administracion":"0","15":"0","mercancia_cargo":"2-399-333 1-399-333 1-369-308 ","16":"2-399-333 1-399-333 1-369-308 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE****13-08-2019* Pago: 0*Saldo: 0*Reporte: DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE****31-07-2019* Pago: 0*Saldo: 0*Reporte: SE ENCUENTRA CASA DE SU MAMA SE PLATICA CON MAMA DICE QUE VA A INVESTIGAR BUSCAR EN DIRECCION DE MAMA****30-07-2019* Pago: 0*Saldo: 0*Reporte: OSWALDO UBICA SU DOMICILIO SE TIENE QUE PASAR A VISITAR A SU MAMA ELLA QUEDO DE PAGAR EN 15 DIAS URGE BUSCAR****16-07-2019* Pago: 0*Saldo: 0*Reporte: SE PLATICO CON DELEGADO QUE NO LA HA PODIDO BUSCAR QUE SI LA V**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE****13-08-2019* Pago: 0*Saldo: 0*Reporte: DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE****31-07-2019* Pago: 0*Saldo: 0*Reporte: SE ENCUENTRA CASA DE SU MAMA SE PLATICA CON MAMA DICE QUE VA A INVESTIGAR BUSCAR EN DIRECCION DE MAMA****30-07-2019* Pago: 0*Saldo: 0*Reporte: OSWALDO UBICA SU DOMICILIO SE TIENE QUE PASAR A VISITAR A SU MAMA ELLA QUEDO DE PAGAR EN 15 DIAS URGE BUSCAR****16-07-2019* Pago: 0*Saldo: 0*Reporte: SE PLATICO CON DELEGADO QUE NO LA HA PODIDO BUSCAR QUE SI LA V**"},{"cuenta":"2178","0":"2178","nombre":"MARIA DANIELA MARTINEZ ALVAREZ","1":"MARIA DANIELA MARTINEZ ALVAREZ","telefono":"0","2":"0","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"1400","5":"1400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.479807339222255","7":"20.479807339222255","longitud_fija":"-99.93879139981577","8":"-99.93879139981577","adeudo_cargo":"573","9":"573","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"16-07-2019","12":"16-07-2019","puntos_disponibles":"0","13":"0","reporte_agente":"QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR","14":"QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR****13-08-2019* Pago: 0*Saldo: 0*Reporte: QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR****31-07-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINEROQUE IVA A CONSEGUIR QUE PASARAMOS EN LA TARDE PERO NO CONSIGUIO QUE EN 15 DIAS****30-07-2019* Pago: 0*Saldo: 0*Reporte: PASA OSWALDO QUE NO TENIA DINERO QUEDA DE LIQUIDAR EN 15 DIAS EXIGIR SU PAGO****16-07-2019* Pago: 300*Saldo: 573*Reporte: NO LE LIQUIDARON LAS CLIENTAS**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR****13-08-2019* Pago: 0*Saldo: 0*Reporte: QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR****31-07-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINEROQUE IVA A CONSEGUIR QUE PASARAMOS EN LA TARDE PERO NO CONSIGUIO QUE EN 15 DIAS****30-07-2019* Pago: 0*Saldo: 0*Reporte: PASA OSWALDO QUE NO TENIA DINERO QUEDA DE LIQUIDAR EN 15 DIAS EXIGIR SU PAGO****16-07-2019* Pago: 300*Saldo: 573*Reporte: NO LE LIQUIDARON LAS CLIENTAS**"}],"fechaClientesConsulta":"27-08-2019"}]
              ,"infoUsuario":[{"nombre_empleado":"Eduardo Baldomero Maximo","0":"Eduardo Baldomero Maximo","usuario":"Eduardo","1":"Eduardo","codigo_empleado_pulsera":"p5348","2":"p5348","ruta_asignada":"A1","3":"A1"}]
              }
             */


                try {
                    JSONArray inventario = response.getJSONArray("inventario");
                    JSONObject informacion = response.getJSONObject("informacion");
                    JSONArray zona = response.getJSONArray("zona");
                    JSONArray clientes = response.getJSONArray("clientes");
                    JSONArray infoUsuario = response.getJSONArray("infoUsuario");


                    Log.d("Inventario",String.valueOf(inventario.length()));
                    Log.d("Informacion",String.valueOf(informacion.getString("id")));
                    //Log.d("zona",zona.getString("zona") + zona.getString("zonificacion"));
                    Log.d("Clientes",String.valueOf(clientes.length()));
                    Log.d("Clientes",clientes.toString());

                    Toast.makeText(MainActivity.this, informacion.getString("id"), Toast.LENGTH_LONG).show();

                    inventarioActivity.llenarInventarioDesdeJsonArray(inventario,dbHelper,activity);
                    llenarClientes.cargarClientesDesdeJson(clientes,dbHelper);
                    llenarClientes.cargaZonificacionDesdeJson(zona,dbHelper);





                    //llenamos los datos de uso de sesion
                    ConfiguracionesApp.setDatosInicioSesion(activity,infoUsuario, clientes);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                ingresar();

            }




            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //cuando por se recibe como respuesta un objeto que no puede ser convertido a jsonData
                //es decir si se conecta al servidor, pero desde el retornamos un echo de error
                //con un simple String lo recibimos en este metodo, crei que lo recibiria en el metodo onSUcces que tiene como parametro el responseString
                //pero parese que no, lo envia a este onFaiulure con Status Code

                //Si el nombre del archivo php esta mal para el ejemplo el correcto es: comprobar_usuario_app_kaliope.php
                // y el incorrecto es :comprobar_usuario_app_kaliop.php se llama a este metodo y entrega el codigo 404
                //lo que imprime en el log es un codigo http donde dice que <h1>Object not found!</h1>
                //            <p>
                //
                //
                //                The requested URL was not found on this server.
                //
                //
                //
                //                If you entered the URL manually please check your
                //                spelling and try again.
                //es decir si se encontro conexion al servidor y este respondio con ese mensaje
                //tambien si hay errores con alguna variable o algo asi, en este medio retorna el error como si lo viernas en el navegador
                //te dice la linea del error etc.


                    String info = "Status Code: " + String.valueOf(statusCode) +"  responseString: " + responseString;
                    Log.d("onFauile 1" , info);
                    //Toast.makeText(MainActivity.this,responseString + "  Status Code: " + String.valueOf(statusCode) , Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    dialogoDeConexion("Fallo de inicio de sesion", responseString + "\nStatus Code: " + String.valueOf(statusCode));


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                //cuando no se ha podido conectar con el servidor el statusCode=0 cz.msebera.android.httpclient.conn.ConnectTimeoutException: Connect to /192.168.1.10:8080 timed out
                //para simular esto estoy en un servidor local, obiamente el celular debe estar a la misma red, lo desconecte y lo movi a la red movil

                //cuando no hay coneccion a internet apagados datos y wifi se llama al metodo retry 5 veces y arroja la excepcion:
                // java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /:: (port 0) after 10000ms: connect failed: ENETUNREACH (Network is unreachable)


                //Si la url principal del servidor esta mal para simularlo cambiamos estamos a un servidor local con:
                //"http://192.168.1.10:8080/KALIOPE/" cambiamos la ip a "http://192.168.1.1:8080/KALIOPE/";
                //se llama al onRetry 5 veces y se arroja la excepcion en el log:
                //estatus code: 0 java.net.ConnectException: failed to connect to /192.168.1.1 (port 8080) from /192.168.1.71 (port 36134) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                //no hay ruta al Host

                //Si desconectamos el servidor de la ip antes la ip en el servidor de la computadora era 192.168.1.10, lo movimos a 192.168.1.1
                //genera lo mismo como si cambiaramos la ip en el programa android la opcion dew arriba. No
                //StatusCode0  Twhowable:   java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /192.168.1.71 (port 37786) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                //Llamo a reatry 5 veces


                String info = "StatusCode" + String.valueOf(statusCode) +"  Twhowable:   "+  throwable.toString();
                Log.d("onFauile 2" , info);
                //Toast.makeText(MainActivity.this,info, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                dialogoDeConexion("Fallo de conexion", info);
            }


            @Override
            public void onRetry(int retryNo) {
                progressDialog.setMessage("Reintentando conexion No: " + String.valueOf(retryNo));
            }
        });
    }

    private void showProgresDialog(){

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Conectando al Servidor");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    void dialogoDeConexion (String title,String mensaje){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }







    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //https://medium.com/alvareztech/verificar-estado-de-conexi%C3%B3n-a-internet-en-tu-aplicaci%C3%B3n-android-d55e2b501302

            if(networkInfo != null && networkInfo.isConnected()){
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    Log.d("Main","conectado");

                    hacerPingAlServidor();
                    tvEstadoConexion.setText("Intentando Conexion");
                    tvEstadoConexion.setBackgroundColor(Color.CYAN);


                }
                Log.d("Main",String.valueOf(networkInfo.getState()));
            }else{
                Log.d("Main","desconectado");
                continuarHiloHacerPing = false;
                tvEstadoConexion.setText("WiFi o datos apagados\nActivalos");
                tvEstadoConexion.setBackgroundColor(Color.RED);
            }

        }
    };

    public void hacerPingAlServidor(){
        continuarHiloHacerPing = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(continuarHiloHacerPing){


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {



                            KaliopeServerClient.postNumeroIntentosTimeOut("app_kaliope/ping_servidor.php", null, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                    try {
                                        String estado = response.getString("estado");
                                        //Toast.makeText(MenuPrincipalActivity.this, estado, Toast.LENGTH_SHORT).show();
                                        tvEstadoConexion.setText("Conectado");
                                        tvEstadoConexion.setEnabled(true);
                                        tvEstadoConexion.setBackgroundColor(Color.GREEN);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                                    //cuando no se ha podido conectar con el servidor el statusCode=0 cz.msebera.android.httpclient.conn.ConnectTimeoutException: Connect to /192.168.1.10:8080 timed out
                                    //para simular esto estoy en un servidor local, obiamente el celular debe estar a la misma red, lo desconecte y lo movi a la red movil

                                    //cuando no hay coneccion a internet apagados datos y wifi se llama al metodo retry 5 veces y arroja la excepcion:
                                    // java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /:: (port 0) after 10000ms: connect failed: ENETUNREACH (Network is unreachable)


                                    //Si la url principal del servidor esta mal para simularlo cambiamos estamos a un servidor local con:
                                    //"http://192.168.1.10:8080/KALIOPE/" cambiamos la ip a "http://192.168.1.1:8080/KALIOPE/";
                                    //se llama al onRetry 5 veces y se arroja la excepcion en el log:
                                    //estatus code: 0 java.net.ConnectException: failed to connect to /192.168.1.1 (port 8080) from /192.168.1.71 (port 36134) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                                    //no hay ruta al Host

                                    //Si desconectamos el servidor de la ip antes la ip en el servidor de la computadora era 192.168.1.10, lo movimos a 192.168.1.1
                                    //genera lo mismo como si cambiaramos la ip en el programa android la opcion dew arriba. No
                                    //StatusCode0  Twhowable:   java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /192.168.1.71 (port 37786) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                                    //Llamo a reatry 5 veces
                                    if (tvEstadoConexion.getText().toString().equals("Sin Conexion al servidor\nRevisa WiFi")){
                                        tvEstadoConexion.setText("Sin Conexion al servidor\nRevisa WiFi.");
                                        tvEstadoConexion.setBackgroundColor(Color.CYAN);
                                    }else{
                                        tvEstadoConexion.setText("Sin Conexion al servidor\nRevisa WiFi");
                                        tvEstadoConexion.setBackgroundColor(Color.YELLOW);
                                    }
                                    String info = "StatusCode" + String.valueOf(statusCode) + "  Twhowable:   " + throwable.toString();
                                    Log.d("onFauile 2", info);
                                    //Toast.makeText(getApplicationContext(), "Falla en Ping Status Code: " + String.valueOf(statusCode) , Toast.LENGTH_LONG).show();


                                }

                            });



                        }
                    });

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();

    }





}

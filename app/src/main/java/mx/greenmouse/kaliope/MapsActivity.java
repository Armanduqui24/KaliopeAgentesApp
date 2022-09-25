package mx.greenmouse.kaliope;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;



    private LocationManager locationManager;
    private LocationListener locationListener;

    private DataBaseHelper dataBaseHelper = new DataBaseHelper(this);


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    private final int MY_PERMISSION_FINE_LOCATION_CODE = 10;
    private byte contador = 0;

    Thread threadContador;
    boolean hiloContadorIniciado = false;

    private final int IDENTIFICADOR_UNO = 1;
    private final LatLng UBICACION_ATLACOMULCO = new LatLng(19.7968, -99.8765);
    private LatLng ubicacionInicial = new LatLng(19.7968, -99.8765);

    String CuentaClienteRecibidoIntent = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Toolbar toolbar  =(Toolbar) findViewById(R.id.my_toolbar);
        //toolbar.setTitle("HOlaa");
        //toolbar.setLogo(R.drawable.logo_kaliope);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//mantenemos activa la pantalla, para que no se bloquee ene el gps




        //VERIFICAMOS SI LOS SERVICIOS DE GOOGLE ESTAN FUNCINANDO CORRECTAMENTE o enviamos a actualizar
        //google play services

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS){
            if(googleApiAvailability.isUserResolvableError(result)) {
                googleApiAvailability.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
        }else {

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }



        //SI AL LLAMAR ESTE ACTIVIDAD RECIBIMOS EL VALOR DEL NUMERO DE CUENTA ENTONCES UBICAMOS LA CAMARA EN ESE PUNTO
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            CuentaClienteRecibidoIntent = bundle.getString("CLIENTES_ADMIN_CUENTA_CLIENTE");
        }

        //Log.i("apiKey luisda", getResources().getString(R.string.google_maps_key));
        //Toast.makeText(this,getResources().getString(R.string.google_maps_key) , Toast.LENGTH_SHORT).show();


        //para que muestre en un toast el paquete y el api que usa para google
        /*try{
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);

            Toast.makeText(this, String.valueOf(packageInfo.packageName) +"  " + getResources().getString(R.string.google_maps_key), Toast.LENGTH_SHORT).show();
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }*/






    }








    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_json));
        UiSettings uiSettings = mMap.getUiSettings();
        //uiSettings.setZoomControlsEnabled(true);

        //registramos nuestro receiverBroadcast solo cuanod queremos usar el servicio de localizacion LocationService
        //LocalBroadcastManager.getInstance(this).registerReceiver(RecibirNotificacionDeUbicacion,new IntentFilter("LOCALIZACION_ACTUALIZADA"));





        try{
            //KmlLayer kmlLayer = new KmlLayer(mMap,R.raw.,getApplicationContext());
            //kmlLayer.addLayerToMap();
        }catch (Exception e){
            e.printStackTrace();
        }





        // al arrancar el mapa moveremos el marcador de la posicion al lugar donde indican las coordenadas
        //guardadas en las constantes, estas constantes se mantienen actualizadas mientras la app este abierta
        //gracias al servicio, cuando se entre al mapa y se puedan convertir los valores a float.

        try{
            float latitudInicial = Float.valueOf(Constant.INSTANCE_LATITUDE);
            float longitudeInicial = Float.valueOf(Constant.INSTANCE_LONGITUDE);
            ubicacionInicial = new LatLng(latitudInicial,longitudeInicial);

        }catch (Exception e){
            e.printStackTrace();
            ubicacionInicial = UBICACION_ATLACOMULCO;
        }



        float zoomlevel = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionInicial,zoomlevel));



        final Marker ubicacionActual = mMap.addMarker(new MarkerOptions()
                .position(ubicacionInicial)
                .title("ubicacionActual")
                .snippet("hola Aqui estoy")
                .anchor((float)0.5,(float) 0.5)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carrito_kaliope_gps)));



        markersOfDataBaseClients(); //creamos los marcadores de los clientes desde la base de datos de clientes
        setCameraInClient();//si se recivio un numero de cuenta especifico movemos la camara a ese cliente






        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String etiqueta = "";

                try{
                    etiqueta = (String) marker.getTag();

                }catch (Exception e){
                    e.printStackTrace();
                }

                Toast.makeText(MapsActivity.this, String.valueOf(etiqueta), Toast.LENGTH_SHORT).show();









                return false;
            }
        });

        /*usamos el escuchador de arrastre del marker como un OnLongClickListener
         * porque queremos que al dejar precionado un Marker del cliente nos envie a ver su detalle
         * los problemas que tendremos es que al dejar presionado el marker y se active el dragg
         * este marker se movera de las coordenadas en donde esta, entonces necesitamos que este cliente
         * se mantenga en su posicion, para ello con su tag que tiene asociada cada marker que es su numero de cuenta
         * al finalizarse el dragg se hara la consulta nuevamente para sacar las coordenadas del cliente y ponerlo
         * en su posicion correcta nevamente esto creo que solo sera necesario si se quiere hacer otra accion en el mapa
         * como mostrar antes un dialogo*/
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                //EN ESTA OPCION COMENTADA MOSTRABAMOS UN CUADRO DE DIALOGO PARA CONFIRMAR LA ACCION
                //PERO SI SE USA ESTA EN EL onMarkerDragEnd no se tiene que colocar los renglones el intent que envia a la actividad de detalle

                /*String cuenta;
                try {
                    cuenta = marker.getTag().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    cuenta = "";
                }

                final String cuentaCliente = cuenta;







                //mostramos un dialogo
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Ver Detalle Cliente")
                        .setMessage("Quieres ver el detalle del cliente?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MapsActivity.this, "Llendo a detalle", Toast.LENGTH_SHORT).show();
                                Intent intentDetalleCliente = new Intent(MapsActivity.this, DetallesClientes.class);
                                intentDetalleCliente.putExtra("CLIENTES_ADMIN_CUENTA_CLIENTE",cuentaCliente);
                                startActivity(intentDetalleCliente);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();*/

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                String cuentaCliente="";
                try{
                    cuentaCliente = marker.getTag().toString();
                }catch (Exception e){
                    e.printStackTrace();
                }


                Cursor cursorClientByCuentaCliente = dataBaseHelper.clientes_dameClientePorCuentaCliente(cuentaCliente);
                cursorClientByCuentaCliente.moveToFirst();
                if(cursorClientByCuentaCliente.getCount()>0){

                    try{
                        LatLng coordenadasDelCliente = new LatLng(
                                Float.valueOf(cursorClientByCuentaCliente.getString(cursorClientByCuentaCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_LATITUD_CLIENTE))),
                                Float.valueOf(cursorClientByCuentaCliente.getString(cursorClientByCuentaCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_LONGITUD_CLIENTE)))
                        );
                        marker.setPosition(coordenadasDelCliente);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                }

                //añadimos los renglones del intent para que cuando se pulse sobre el marker se arrastre y al soltarse los envie a la actividad de detalle
                Intent intentDetalleCliente = new Intent(MapsActivity.this, DetallesClientes.class);
                intentDetalleCliente.putExtra("CLIENTES_ADMIN_CUENTA_CLIENTE",cuentaCliente);
                startActivity(intentDetalleCliente);

            }
        });









        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i ("onLocationChanged","Entramos");
                LatLng actualUbication = new LatLng(location.getLatitude(), location.getLongitude());
                float velocidadKmHr = (location.getSpeed()*3600)/1000;


                ubicacionActual.setPosition(actualUbication);
                ubicacionActual.setFlat(true);
                ubicacionActual.setSnippet("Velocidad: " + velocidadKmHr);
                ubicacionActual.setAnchor(0, (float)0.4);

                //Diagrama de ayuda para entender el atributo Anchor de un marker
                //               imagen
                // 0 v1           *****|****|****|****|****|****|****|****|****|****
                // 0.1 v1         *                                                *
                // 0.2 v1         *                                                *
                // 0.3 v1         *                                                *
                // 0.4 v1         *                                                *
                // 0.5 v1         *                                                *
                // 0.6 v1         *                                                *
                // 0.7 v1         *                                                *
                // 0.8 v1         *                                                *
                // 0.9 v1         *                                                *
                // 1 v1           *****|****|****|****|***|*****|****|****|*****|***
                //                0v 0.1v 0.2v 0.3v 0.4v 0.5v 0.6v 0.7v 0.8v 0.9v 1v
                //
                //
                // El anchor define el punto de la imagen del icono en donde se colocara la coordenada
                // por ejemplo si tu quieres que el centro de la imagen sea en donde este la coordenada real de la ubicacion
                // se debera poner 0.5 0.5
                // si quieres que la esquina inferior izquierda de la imager reprecente la coordenada real entonces se pone 1,0
                // si quieres que sea la esquina superior derecha la que represente la coordenada se pone 0,1
                //
                // v y v1 son los nombres de variables que aparecen en el atributo anchor
                // en el caso del chevy de kaliope que demostrara la ubicacion actual del agente
                // la imagen tiene la trompita viendo hacia arriba y la cajuela hacia abajo las ruedas a la izq y el techo a la dere
                //
                //               **
                //              O***
                //               ****
                //               *****
                //              O*****
                //               ****
                // queremos que la ubicacion real este en las ruedas seria en el punto 0v
                // y que este por la ventana del chofer 0.4 v1


                //al icono del carrito añadimos una rotacion para que rote en el sentido en que se desplaza
                //pero para ello primero preguntaremos si el sistema a concegido una rotacion valida
                //cuando el gps no detecta una rotacion real envia 0.0 al sistema, solo rotaremos el icono
                //cuando la medida sea diferente de 0.0 esto porque si no, el icono se rotaria en un sentido real
                //y depronto cunndo no sepa a donde se dirige el icono volvera a la posicion 0.0 lo cual confunde
                //al usuario porque cuando el auto se detenga por ejemplo ya no sabra hacia donde se dirigia
                //cuando la rotacion entrege 0.0 el icono no se rotara y asi se quedara en la ultima direccion
                //que tenia
                if(location.getBearing()!=0.0){
                    ubicacionActual.setRotation(location.getBearing());//colocamos la rotacion para que el icono rote
                }
                //ubicacionActual.showInfoWindow();//para mostrar la ubicacion del marquer sin presionar el marker



                //cuando se agote el tiempo del contador enfocamos la vista en la ubicacion actual del vehiculo
                if (contador<=0){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualUbication,17));
                }










            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        solicitudDePermisos();











        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                //cuando movemos la camara, reiniciamos el contador para que le de 15 segundos al usuario de desplazarse libremente atravez del mapa
                //antes de volver a su ubicacion actual
                contador = 15;
                temporizadorDeMoverPorGPS();
            }
        });


    }







    private void temporizadorDeMoverPorGPS(){

        if (!hiloContadorIniciado){
            hiloContadorIniciado = true;

            new Thread(new Runnable() {
                @Override
                public void run() {






                    try{
                        while (contador>0){
                            Thread.sleep(1000);
                            contador--;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Toast.makeText(MapsActivity.this, String.valueOf(contador), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                        hiloContadorIniciado = false;

                    }catch (Exception e){
                        e.printStackTrace();
                    }



                }
            }).start();

        }





    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(RecibirNotificacionDeUbicacion);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_FINE_LOCATION_CODE:{

                //if request is cancelled, the result arrays are empty.
                if(grantResults.length>0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //PERMISSION WAS GRANTED, YAY! dO THE work that you want to do with this permission

                    solicitudDePermisos();
                    //vamos a solicitar los permisos nuevamente pero como ahora ya fueron autoirzados
                    //se saltara toda la parte de requestPermission y activara el RequestLocationUpdates


                }else{
                    //permission denied, boo! Disable the functionality that depends on this permission
                    Toast.makeText(this, "ExitAplication, we need the permission to work", Toast.LENGTH_SHORT).show();
                    //finish();
                }

                return;

            }

        }
    }


    void solicitudDePermisos(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){


            //if the android version is lower to android api 23, the permission was autorizate when the user istall the app
            //if the android version is higer or equals to android api 23, the permissions must be autorizate in the run time ejecution.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                //if the android version is higest to 23 we go to check the permission

                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    //si el usuario rechaza por primera ves el permiso este metodo devuelve true, el if lo evalua y nos permite mostrar una
                    //explciacion al ususario por segunda vez para que active los permisos

                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed because Kaliope needs ubicate the coordenates of new accounts")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_FINE_LOCATION_CODE);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();

                }else{

                    //si el permiso no fue rechazado anteriormente entonces solicitamos el permiso sin mostrar el dialogo de explicacion
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_FINE_LOCATION_CODE);
                }
                return;
            }


        }




        //if the permission was granted, the return line was not entry and now activate the refresh of location updates

        locationManager.requestLocationUpdates("gps",1000,0,locationListener);




    }




    private void markersOfDataBaseClients(){
        /*metodo que leera la base de datos de clientes y obtendremos de ella las coordenadas de los cleitnes para ubicar los markers en el mapa
         * sus nombres, su estado de cliente*/
        LatLng coordenadasCliente;
        String nombreDelCliente;
        String estadoDelCliente;
        Marker markerCliente;
        String numeroCuenta;


        Cursor cursorDameClientes = dataBaseHelper.clientes_dameTodosLosClientes();
        cursorDameClientes.moveToFirst();
        if(cursorDameClientes.getCount()>0){

            do{
                nombreDelCliente = cursorDameClientes.getString(cursorDameClientes.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE));
                estadoDelCliente = cursorDameClientes.getString(cursorDameClientes.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ESTADO_CLIENTE));
                numeroCuenta = cursorDameClientes.getString(cursorDameClientes.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE));

                try {
                    coordenadasCliente = new LatLng(
                            Float.valueOf(cursorDameClientes.getString(cursorDameClientes.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_LATITUD_CLIENTE))),
                            Float.valueOf(cursorDameClientes.getString(cursorDameClientes.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_LONGITUD_CLIENTE)))
                    );
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue;
                }

                markerCliente = mMap.addMarker(new MarkerOptions()
                        .position(coordenadasCliente)
                        .anchor(0,1)
                        .title(nombreDelCliente)
                        .draggable(true)
                );

                if(Constant.ACTIVO.equals(estadoDelCliente)){
                    markerCliente.setIcon(BitmapDescriptorFactory.fromBitmap(setMarkerDrawable(nombreDelCliente,Constant.ACTIVO)));
                }

                if(Constant.REACTIVAR.equals(estadoDelCliente)){
                    markerCliente.setIcon(BitmapDescriptorFactory.fromBitmap(setMarkerDrawable(nombreDelCliente,Constant.REACTIVAR)));
                }

                if(Constant.LIO.equals(estadoDelCliente)){
                    markerCliente.setIcon(BitmapDescriptorFactory.fromBitmap(setMarkerDrawable(nombreDelCliente,Constant.LIO)));
                }

                if(Constant.PROSPECTO.equals(estadoDelCliente)){
                    markerCliente.setIcon(BitmapDescriptorFactory.fromBitmap(setMarkerDrawable(nombreDelCliente,Constant.PROSPECTO)));
                }

                markerCliente.setTag(numeroCuenta);




            }while (cursorDameClientes.moveToNext());


        }



        //(añadimos el bloque try catch porque al actualizar la aplicacion de la version 6.4.3,
        // se añade la nueva columna a la talba idzonas, y si tiene clientes, esta columna estaba con
        // datos null, y generaba error en la funcion split de getPerimetro)
        try{
            getPerimetro();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error al mostrar el perimetro, puede no haber datos coordenadas. " + e.toString(), Toast.LENGTH_LONG).show();
            //
        }

    }

    private void getPerimetro() {
        //(Obtenemos de la base de datos el string con las corrdenadas lo dibiridermos primero por
        // espacios, y despues por comas
        // )
        String perimetro = "";
        //EN java no se puede redimencionar arrays, para ello crearemos un arrayList donde se ingresaran
        //las cordenadas, el array list es una clase de java que si se puede redimencionar, y lo llenaremos
        //de objetos LatLang, este array se le pasara cuando creemos el poligono




        Cursor cursor = dataBaseHelper.tabla_identificador_zona_obtenerIdZona();
        cursor.moveToFirst();
        if(cursor.getCount()>0) {

            do {
                ArrayList<LatLng> datosGeograficosPerimetro = new ArrayList<LatLng>();

                perimetro = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COORDENADAS_ZONA));
                //cadena almacenada en la base de datos:
                //-100.2396451502743,19.85561780694818,0 -100.2756115157535,19.85752633485765,0 -100.2980736287669,19.80861287205232,0 -100.3259518200383,19.80432374649399,0
                String espacios[] = perimetro.split(" ");

                for (String puntoGeografico : espacios) {
                    //cada indice del array espacios contiene esto:
                    //-100.2396451502743,19.85561780694818,0
                    //ahora dividimos en comas
                    String[] comas = puntoGeografico.split(",");
                    //en este momento el array comas en cada indice tiene algo como esto:
                    // -100.2396451502743
                    // 19.85561780694818
                    // 0
                    //convertimos el valor almacenado en un float y lo añadimos al objeto LatLang
                    //en new LatLng (double latitud, double longitud)
                    //y ese objeto LatLng lo ingresamos al arrayList

                    try {
                        Log.d("Lat,Long del array: ", comas[1] + comas[0]);

                        LatLng punto = new LatLng(Float.parseFloat(comas[1]), Float.parseFloat(comas[0]));
                        Log.d("LatLangObjeto: ", String.valueOf(punto));
                        datosGeograficosPerimetro.add(punto);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }


                //una ves llenado el array representamos en el mapa el perimetro con el .addAll
                //la otra manera es por solo .add pero ahi se tienen que especificar manualmente los
                //puntos. el adAll espera un objeto iterable con objetos LatLng dentro, para de esta
                //manera representar el poligono con un numero variable de puntos

                mMap.addPolygon(new PolygonOptions()
                        .clickable(false)
                        .strokeColor(Color.RED)
                        .addAll(datosGeograficosPerimetro));


            } while (cursor.moveToNext()); //si hay mas rutas que trazar las marcamos aqui

        }


    }

    private void setCameraInClient(){
        /*usamos este metodo para enfocar la camara en un cliente cuando se llega a esta actividad del mapa
         * al presionar a la lista de clientes*/
        LatLng vistaDeCliente = ubicacionInicial;
        //Toast.makeText(this, String.valueOf(CuentaClienteRecibidoIntent), Toast.LENGTH_SHORT).show();

        if(!CuentaClienteRecibidoIntent.equals("")){

            Cursor cursorClientesCuentaCliente = dataBaseHelper.clientes_dameClientePorCuentaCliente(CuentaClienteRecibidoIntent);
            cursorClientesCuentaCliente.moveToFirst();

            if(cursorClientesCuentaCliente.getCount()>0){
                try {
                    vistaDeCliente = new LatLng(
                            Float.valueOf(cursorClientesCuentaCliente.getString(cursorClientesCuentaCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_LATITUD_CLIENTE))),
                            Float.valueOf(cursorClientesCuentaCliente.getString(cursorClientesCuentaCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_LONGITUD_CLIENTE)))
                    );
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return;
                }

                //Toast.makeText(this, vistaDeCliente.toString(), Toast.LENGTH_SHORT).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vistaDeCliente,19));
                contador = 15;//le damos 15 segundos al contador para que no se mueva de inmediato la camara a la ubicacion actual

            }
        }
    }





















    //https://es.stackoverflow.com/questions/101063/mostrar-titulo-siempre-visible


    public Bitmap setMarkerDrawable(String nombre, String estadoCliente) {
        int background = R.drawable.blue_push_pin_medio;

        if(estadoCliente.equals(Constant.ACTIVO)){
            background = R.drawable.blue_push_pin_medio;
        }

        if(estadoCliente.equals(Constant.REACTIVAR)){
            background = R.drawable.yellow_push_pin_medio;
        }

        if(estadoCliente.equals(Constant.LIO)){
            background = R.drawable.red_push_pin_medio141_24px;
        }

        if(estadoCliente.equals(Constant.PROSPECTO)){
            background = R.drawable.green_push_pin_medio141_24px;
        }



        /* DO SOMETHING TO THE ICON BACKGROUND HERE IF NECESSARY */
        /* (e.g. change its tint color if the number is over a certain threshold) */

        Bitmap icon = drawTextToBitmap(background, String.valueOf(nombre));

        return icon;
    }

    public Bitmap setMarkerDrawableReactivar(String dato) {
        int background = R.drawable.yellow_push_pin_medio;

        /* DO SOMETHING TO THE ICON BACKGROUND HERE IF NECESSARY */
        /* (e.g. change its tint color if the number is over a certain threshold) */

        Bitmap icon = drawTextToBitmap(background, String.valueOf(dato));

        return icon;
    }

    public Bitmap drawTextToBitmap(int gResId, String gText) {
        Resources resources = getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        /* SET FONT COLOR (e.g. WHITE -> rgb(255,255,255)) */
        paint.setColor(Color.rgb(0, 0, 0));
        /* SET FONT SIZE (e.g. 15) */
        paint.setTextSize((int) (10 * scale));
        /* SET SHADOW WIDTH, POSITION AND COLOR (e.g. BLACK) */
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);

        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;
        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }








    public void CargarLista(ArrayList<String> datos) {
        double lat = 0, lon = 0, latD = 0, lonD = 0;
        String CodigoCliente = "", NombreCliente = "", RefCliente = "", telefonos = "", Op = "";
        // = null;

        for (int i = 0; i < datos.size(); i++) {
            String dato = datos.get(i).toString();

            try {

                CodigoCliente = String.valueOf(dato.substring(0, dato.indexOf("@")));
                NombreCliente = String.valueOf(dato.substring(dato.indexOf("@") + 1, dato.indexOf("$")));
                lat = Float.valueOf(dato.substring(dato.indexOf("$") + 1, dato.indexOf("%")));
                lon = Float.valueOf(dato.substring(dato.indexOf("%") + 1, dato.indexOf("&")));
                RefCliente = String.valueOf(dato.substring(dato.indexOf("&") + 1, dato.indexOf("*")));
                telefonos = String.valueOf(dato.substring(dato.indexOf("*") + 1, dato.indexOf("#")));
                Op = String.valueOf(dato.substring(dato.indexOf("#") + 1, dato.length()));

                //datos.get(0).equals("false")
                if (Op.equals("1")) {
                    Marker marcador = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lon))
                            .title(String.valueOf(NombreCliente))
                            .icon(BitmapDescriptorFactory.fromBitmap(setMarkerDrawableReactivar(CodigoCliente)))
                            .snippet(RefCliente + " Cel: " + telefonos)
                    );

                }

                if (Op.equals("2")) {
                    Marker marcador = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lon))
                            .title(String.valueOf(NombreCliente))
                            .icon(BitmapDescriptorFactory.fromBitmap(setMarkerDrawableReactivar(CodigoCliente)))
                            .snippet(RefCliente + " Cel: " + telefonos)
                    );


                }


            } catch (ArrayIndexOutOfBoundsException excepcion) {
                lat = 0;
                lon = 0;

            }
            if ((lat != 0) && (lon != 0)) {
                latD = lat;
                lonD = lon;

            }

            //marcador.showInfoWindow();

        }


        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(latD, lonD))   //Centramos el mapa en Madrid
                .zoom(15)         //Establecemos el zoom en 19
                .bearing(45)      //Establecemos la orientación con el noreste arriba
                .build();

        CameraUpdate camUpd3 =
                CameraUpdateFactory.newCameraPosition(camPos);

        mMap.animateCamera(camUpd3);


    }










    public void ObtDatos(String codigo){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://www.framac.com.bo/webservice/pListBuscarE.php";

        RequestParams parametros = new RequestParams();


        parametros.put ("codigo", codigo);
        //parametros.put ("dia", 2);

        client.post(url, parametros, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    CargarLista(ObtDatosJason(new String (responseBody)));
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }





    public ArrayList<String> ObtDatosJason(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto;
            for (int i=0;i<jsonArray.length();i++){
                texto = jsonArray.getJSONObject(i).getString("codigo")+ '@'+
                        jsonArray.getJSONObject(i).getString("cliente")+ '$'+
                        jsonArray.getJSONObject(i).getString("latitud")+ '%'+
                        jsonArray.getJSONObject(i).getString("longitud") + '&' +
                        jsonArray.getJSONObject(i).getString("referencia") + '*' +
                        jsonArray.getJSONObject(i).getString("telefonos") + '#' +
                        jsonArray.getJSONObject(i).getString("entrega");


                listado.add(texto);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return  listado;
    }














    //ejemplo para recibir broadcast, del LocationService que creamos, por si queremos usar
    // el mismo servicio que iniciamos para entregar las coordenadas a los movimientos
    //pero en este caso creamos dentro de esta actividad de mapa otro servicio de localizacion
    //independiente
    private BroadcastReceiver RecibirNotificacionDeUbicacion = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "LOCALIZACION ACTUALIZADA", Toast.LENGTH_SHORT).show();
        }
    };

}
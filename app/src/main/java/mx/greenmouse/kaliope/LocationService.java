package mx.greenmouse.kaliope;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
    LocationManager locationManager;
    LocationListener locationListener;
    MediaPlayer mediaPlayer;

    Intent intentSendNotification;

    private final int MY_PERMISSION_FINE_LOCATION_CODE =10;



    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {


         intentSendNotification = new Intent();
         intentSendNotification.setAction("LOCALIZACION_ACTUALIZADA");



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {


            @Override
            public void onLocationChanged(Location location) {
                //Toast.makeText(getApplicationContext(), "En gps" + String.valueOf(location.getLongitude()) + ", " + String.valueOf(location.getLatitude())
                //        , Toast.LENGTH_SHORT).show();

                Constant.INSTANCE_LATITUDE  = String.valueOf(location.getLatitude());
                Constant.INSTANCE_LONGITUDE = String.valueOf(location.getLongitude());

                Log.d("Coordenadas",Constant.INSTANCE_LATITUDE + ", " + Constant.INSTANCE_LONGITUDE);

                //sendBroadcast(intentSendNotification);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentSendNotification);



            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(getApplicationContext(), "No se puede iniciar el servicio de Localizacion faltan los permisos", Toast.LENGTH_LONG).show();

                }else {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }


            }
        };





    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //WE START THIS SERVICE IN:
        /*
        * MainActivity
        * MenuPrincipalActivity
        * Trabajar
        * AltaMovimientoActivity
        * AltaEntrada
        * AltaPagos
        * AltaSalidas
        * */


        if(!Constant.INSTANCE_GPS) {
            //we want that the service only start one time, because we call to startService in multiple Activitys


            //mediaPlayer = MediaPlayer.create(this,R.raw.error);
            //mediaPlayer.setLooping(true);
            //mediaPlayer.start();

            Log.i("LocalService", "Received start id " + startId + ": " + intent);
            Toast.makeText(getApplicationContext(), "LocationService Started", Toast.LENGTH_SHORT).show();
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates("gps",5000,0,locationListener);
                Constant.INSTANCE_GPS = true;
            }else{
//                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(i);
            }





        }

        return START_NOT_STICKY;
        //we used START_NOT_STICKY BECAUSE WE WANT THAT AT THE MOMENT WHEN WE KILL THE APP
        //IN THE MULTITASK THE SERVICE DOESN'T START AGAIN, EVEN IF THE APLICATION WAS KILLED

    }

    @Override
    public void onDestroy() {

        // Tell the user we stopped.
        Toast.makeText(this, "LocationService Stopped", Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(locationListener);
        Constant.INSTANCE_GPS=false;

        //mediaPlayer.stop();
    }















}
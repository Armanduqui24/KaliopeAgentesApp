package mx.greenmouse.kaliope;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
//clase feura de uso!
public class ObtenGeolocalizacionActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obten_geolocalizacion);
        getSupportActionBar().hide();
        //irAlMenu();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                Constant.INSTANCE_LATITUDE  = String.valueOf(location.getLatitude());
                Constant.INSTANCE_LONGITUDE = String.valueOf(location.getLongitude());

                Log.d("Coordenadas",Constant.INSTANCE_LATITUDE + ", " + Constant.INSTANCE_LONGITUDE);

                if(Constant.INSTANCE_GPS == false){
                    irAlMenu();
                    Constant.INSTANCE_GPS = true;
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("dbg-osc",s);
            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }

        locationManager.requestLocationUpdates("gps", 500, 0, listener);

    }

    public void irAlMenu(){
        Intent i = new Intent(this, MenuPrincipalActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(listener);
        Log.i("en pausa", "en pausa gps");
        Toast.makeText(getApplicationContext(), "OnPause, stop gps service", Toast.LENGTH_SHORT).show();
    }
}

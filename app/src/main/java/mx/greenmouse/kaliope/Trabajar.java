package mx.greenmouse.kaliope;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Trabajar extends AppCompatActivity implements View.OnClickListener{
    ImageButton irAClientes;
    ImageButton irNuevoCliente;
    ImageButton btMovimientoAutomatico;
    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
    Activity activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajar);
        getSupportActionBar().setTitle("A Trabajar!");

        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));

        irAClientes = (ImageButton) findViewById(R.id.irAClientesB);
        irNuevoCliente = (ImageButton) findViewById(R.id.irNuevoClienteB);
        btMovimientoAutomatico = (ImageButton) findViewById(R.id.Button103);

        irAClientes.setOnClickListener(mostrarClientes);
        irNuevoCliente.setOnClickListener(nuevoCliente);
        btMovimientoAutomatico.setOnClickListener(this);
    }

    private View.OnClickListener mostrarClientes = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //(CREAMOS UN NUEVO OBJETO TIPO FILE LLAMADO FOLDER CON EL CUAL ENCONTRAREMOS
            // LA CARPETA EN LA MEMORIA DEL DISPOSTIVO. DESPUES CREAMOS OTRO OBJETO FILE
            // CON EL QUE ABRIREMOS EL FOLDER Y CONTENDRA EL ARCHIVO CLIENTES.
            // SI EL ARCHIVO DE DATOS CLIENTES EXISTE EN LA MEMORA HACEMOS QUE NOS MANDE
            // A LA VISTA DE LOS CLIENTES, SI LA BASE DE DATOS DE CLIENTES TIENE ARCHVOS
            // ES DECIR ES MAYOR QUE 0 LOS REGISTROS DEVUETOS, TAMBIEN NOS MANDA A LA
            // VISTA DE CLIENTES. PERO SI NI EXISTE EL ARCHIVO Y TAMPOCO HAY DATOS
            // EN LA BASE DE DATOS, NOS MUESTRA EL MENSAJE DE ERROR)
            File folder = new File(Constant.INSTANCE_PATH,"/mx.4103.klp");
            Cursor res = dataBaseHelper.clientes_dameTodosLosClientes();
            File clientes = new File (folder + "/clientes.txt");

            if (clientes.isFile()|| res.getCount()>0){
                Intent intent = new Intent(getApplicationContext(),Clientes.class);
                startActivity(intent);
            }else{
                utilidadesApp.dialogoAviso(activity,"No existe el archivo clientes");
            }


        }
    };





    private View.OnClickListener nuevoCliente = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(),RealizarClienteNuevo.class);
            startActivity(intent);
        }
    };







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //(CREAMOS NUESTROS ITEM DE MENU COMO SOLO SON 2
        // NO DECLARO VARIABLES PARA LOS ID EN ESTE CASO
        // 1 Y 2)
        //menu.add(Menu.NONE,1,Menu.NONE,"Movimiento Manual de Almacen");
        //menu.add(Menu.NONE,2,Menu.NONE,"Sincronizar Clientes");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //(EN CASO DE QUE SE PRESIONE EL ITEM 1
        // EN CASO DE QUE SE PRESIONE EL ITEM 2)

        switch (item.getItemId()){
            case 1:
               // Intent intent = new Intent(this,AltaMovimientoActivity.class);
                // Constant.QUIEN_LLAMA =2; //le decimos al activyti siguiente que lo llamamos MOVIMIENTO ALMACEN
                // startActivity(intent);
                Toast.makeText(getApplicationContext(),"Funcion temporalmente no disponible",Toast.LENGTH_SHORT).show();

                return true;

            case 2:


                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent (this,MenuPrincipalActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

            switch (view.getId()){

                case  R.id.Button103:
                    //generar movimiento automatico
                    //Intent intent = new Intent(this, MovimientoAutomaticoAlmacen.class);
                    //startActivity(intent);

                    Intent intent1 = new Intent(this,VistaMovimientosClientes.class);
                    startActivity(intent1);

            }
    }

    @Override
    protected void onResume() {
        super.onResume();


        try {
            compararFechaInicioSesionConFechaActual();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void sincronizarClientesNoTerminadosDesdeServidor(){
        //nos conectamos a un nuevo archivo para por ejemplo si administracion realizo cambios en los clientes
        //(la aplicacion vuelva a sincronizar la informacion pero solo del cliente)
    }



    public void compararFechaInicioSesionConFechaActual()throws Exception {
        //(En este metodo Es casi una copia del que esta en MenuPrincipalActivity la diferencia esta que en lugar de llamar
        // directamente al metodo cerrar sesion que esta en MenuPrincipalActiviti lo que hacemos es enviarlo de esta actividad
        //a la del menu principal, donde se ejecutara el cuadro de dialogo y se cerrara la secion
        //lo hacemos asi porque a veces la aplicacion al dia siguiente se queda iniciada en la pantalla de trabajar entonces
        //quiero que aqui tambien sea capas de detectar un cabmio de dia

        // si la fecha del inicio de sesion registrada
        //
        // es diferente a la fecha actual del sistema forzamos a que la app vuelva a la pantalla de MenuPrincipal
        // donde se manejara adecuadamente lo que ocurra cuando las fechas son diferentes)
        String fechaInicioSesion = ConfiguracionesApp.getDiaInicioSesion(activity);
        String fechaActual = utilidadesApp.dameFecha();
        Date dateInicioSesion;
        Date dateActual;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");


        dateInicioSesion = sdf.parse(fechaInicioSesion);
        dateActual = sdf.parse(fechaActual);

        //(Si la fecha de inicio de sesion es anterior a la fecha actual del sistema entonces
        // forzamos un cierre de sesion)
        if(dateInicioSesion.compareTo(dateActual)!=0){
            Log.i("compararFechaInicioS","Las fecha de inicio de sesion no es la misma que la actual enviamos a la actividad MenuPrincipal");
            startActivity(new Intent(this,MenuPrincipalActivity.class));


        }

    }
}

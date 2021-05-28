package mx.greenmouse.kaliope;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RealizarClienteNuevo extends AppCompatActivity {
    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
    TextView tvCuentAsignada;
    TextView tvMensajeNombre;
    TextView tvMensajeTelefono1;
    TextView tvMensajeTelefono2;

    EditText etNombreCliente;
    EditText etTtelefono1;
    EditText etTelefono2;

    Button botonContinuar;

    String numeroDeCuentaCliente;

    boolean regresar = false;

    boolean telefono1Validado = false;
    boolean telefono2Validado = false;
    boolean nombreValidado = false;


    MediaPlayer mediaPlayer = new MediaPlayer();
    Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_cliente_nuevo);
        getSupportActionBar().hide();

        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));




        tvCuentAsignada = (TextView)findViewById(R.id.RealizarClienteNuevoNumeroCuenta);
        etNombreCliente = (EditText) findViewById(R.id.RealizarClienteNuevoCapturaNombre);
        tvMensajeNombre = (TextView) findViewById(R.id.RealizarClienteNuevoMensajeNombre);
        tvMensajeTelefono1 = (TextView) findViewById(R.id.RealizarClienteNuevoMensajeTelefono1);
        tvMensajeTelefono2 = (TextView) findViewById(R.id.RealizarClienteNuevoMensajeTelefono2);
        etTtelefono1 = (EditText) findViewById(R.id.RealizarClienteNuevoCapturaTelefono1);
        etTelefono2 = (EditText)findViewById(R.id.RealizarClienteNuevoCapturaTelefono2);
        botonContinuar = (Button) findViewById(R.id.RealizarClienteNuevoBotonSiguiente);

        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);



        //(Si se llamo desde el boton de cliente nuevo
        // rellenamos la cuenta del cliente con un numero OBTENIDO DESDE la base de datos de numeros clientes nuevos que estren desocupados OSEA EN 0
        //desde el 10 hasta el 30
        // y ponemos el codego de credito 191 para
        // que le de 14 dias y 1500 de credito dejamos que el
        // nombre del cliente se pueda escribir)
        Cursor res = dataBaseHelper.obtenerNumeroCuenta("NUEVO");
        Log.i("tamaño devuelto", String.valueOf(res.getCount()));

        if (res.getCount() > 0 ){
            res.moveToFirst();
            numeroDeCuentaCliente = res.getString(res.getColumnIndex(DataBaseHelper.NUMERO_CUENTA));
            tvCuentAsignada.setText(String.valueOf(numeroDeCuentaCliente));
        }else{
            //Si por alguna razon la base de datos no estuviera llena ponemos
            //Por seguridad un numero
            numeroDeCuentaCliente = "10";

            tvCuentAsignada.setText(numeroDeCuentaCliente);
        }

        botonContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = etNombreCliente.getText().toString();
                String telefono1 = etTtelefono1.getText().toString();
                String telefono2 = etTelefono2.getText().toString();
                String telefonos = telefono1 + "  " + telefono2;


                if(telefono1Validado && telefono2Validado &&nombreValidado){
                    guardarTablaCliente(nombre, telefonos);
                    Intent intent = new Intent(RealizarClienteNuevo.this, RealizarEntrega.class);
                    intent.putExtra("NUMERO_CUENTA_ENVIADO", numeroDeCuentaCliente);
                    startActivity(intent);
                }else{
                    Toast.makeText(RealizarClienteNuevo.this, "Por favor ingrese adecuadamente los datos", Toast.LENGTH_SHORT).show();
                }




            }

        });

        etNombreCliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                nombreValidado = etNombreCliente.getText().length() > 14;

                cargarVistas();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etTtelefono1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!etTtelefono1.getText().toString().equals("0")){

                    if (etTtelefono1.getText().length()>=7){
                        telefono1Validado = true;
                    }else{
                        telefono1Validado = false;
                    }
                }else{
                    telefono1Validado = true;
                }
                cargarVistas();


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        etTelefono2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!etTelefono2.getText().toString().equals("0")){

                    if (etTelefono2.getText().length()>=7){
                        telefono2Validado = true;
                    }else{
                        telefono2Validado = false;
                    }
                }else{
                    telefono2Validado = true;
                }

                cargarVistas();


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });





        cargarVistas();



    }


    private void cargarVistas (){
        if(nombreValidado){
            tvMensajeNombre.setVisibility(View.INVISIBLE);
            etNombreCliente.setBackgroundColor(Color.TRANSPARENT);

        }else{
            tvMensajeNombre.setVisibility(View.VISIBLE);
            etNombreCliente.setBackgroundColor(getResources().getColor(R.color.colorOrangeLight));

        }

        if(telefono1Validado){

            tvMensajeTelefono1.setVisibility(View.INVISIBLE);
            etTtelefono1.setBackgroundColor(Color.TRANSPARENT);

        }else{
            tvMensajeTelefono1.setVisibility(View.VISIBLE);
            etTtelefono1.setBackgroundColor(getResources().getColor(R.color.colorOrangeLight));

        }

        if(telefono2Validado){
            tvMensajeTelefono2.setVisibility(View.INVISIBLE);
            etTelefono2.setBackgroundColor(Color.TRANSPARENT);

        }else{
            tvMensajeTelefono2.setVisibility(View.VISIBLE);
            etTelefono2.setBackgroundColor(getResources().getColor(R.color.colorOrangeLight));

        }
    }


    @Override
    public void onBackPressed() {
        if(regresar){
            super.onBackPressed();
             }else{
            avisoSeguroRegresar();
        }
    }

    private void avisoSeguroRegresar (){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aviso");
        builder.setMessage("Si regresas se eliminaran los datos capturados");
        builder.setPositiveButton("Si, regresar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                eliminarClienteTablaCliente();
                regresar = true;
                onBackPressed();
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create();
        builder.show();

    }

    private void guardarTablaCliente (String nombreCliente, String telefono) {


        //primero revizamos que el numero de cuenta no exista ya ingresado porque
        //si avanzas a la siguiente pantalla y vuelves a regresar, vuelves a avanzar
        //se llama 2 veces a este metodo y se agregan 2 clientes iguales

        dataBaseHelper.clientes_insertarClienteNuevoSiNoExiste(numeroDeCuentaCliente);
        //apartir de aqui ya estamos seguros que si el registro del numero de cuenta 10 por ejemplo
        //no estaba ingresado se ingreso entonces abajo llamaremos al acualizar clientes y ya no a isertar

        ContentValues cv = new ContentValues();
        cv.put(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE,numeroDeCuentaCliente);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE,nombreCliente);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_TELEFONO,telefono);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_DIAS_CREDITO,Constant.QUINCE_DIAS);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_GRADO_CLIENTE,Constant.VENDEDORA);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_CREDITO_CLIENTE,1500);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_ESTADO_CLIENTE,Constant.ACTIVO);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_LATITUD_CLIENTE,0);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_LONGITUD_CLIENTE,0);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE,0);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE,0);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_VENCIMIENTO,"");
        cv.put(DataBaseHelper.CLIENTES_ADMIN_HISTORIALES,"");
        cv.put(DataBaseHelper.CLIENTES_ADMIN_PUNTOS_DISPONIBLES,0);
        cv.put(DataBaseHelper.CLIENTES_ADMIN_REPORTE,"");
        cv.put(DataBaseHelper.CLIENTES_ADMIN_INDICACIONES,"");
        cv.put(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA,"");
        cv.put(DataBaseHelper.CLIENTES_ADMIN_MERCANCIA_ACARGO,"");

        cv.put(DataBaseHelper.CLIENTES_CLI_CODIGO_ESTADO_FECHAS, Clientes.DIA_EXACTO_DE_CIERRE);
        cv.put(DataBaseHelper.CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE,0);
        //    cv.put(DataBaseHelper.CLIENTES_CLI_PRIORIDAD_DE_VISITA, prioridadDeVisita);

        cv.put(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_ZONA,ConfiguracionesApp.getZonaVisitar1(this));
        cv.put(DataBaseHelper.CLIENTES_ADMIN_FECHA_DE_CONSULTA, ConfiguracionesApp.getFechaClientesConsulta1(this));

        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(cv,numeroDeCuentaCliente);






    }

    private void eliminarClienteTablaCliente (){
        dataBaseHelper.clientes_eliminaClienteNumeroCuenta(numeroDeCuentaCliente);
    }

}

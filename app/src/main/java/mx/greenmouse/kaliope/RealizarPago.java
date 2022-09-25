package mx.greenmouse.kaliope;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RealizarPago extends AppCompatActivity {


    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

    String numeroDeCuentaCliente = "";


    TextView
    tvNombreCliente,
    tvVentaGenerada,
    tvAdeudoPendiente,
    tvTotalPorPagar,
    tvNuevoSaldoPendiente,
    tvMensajeParaContinuar;

    EditText etCapturaPago;

    Button botonSiguiente;




    int totalPorPagar = 0;
    int pagoMinimoNecesarioParaEntregarMercancia = 0;
    int pagoCapturado = 0;
    int nuevoAdeudoPendiente = 0;







    MediaPlayer mediaPlayer = new MediaPlayer();
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_pago);
        getSupportActionBar().hide();

        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));



        tvNombreCliente = (TextView)findViewById(R.id.RealizarPagosNombre);
        tvVentaGenerada = (TextView)findViewById(R.id.RealizarPagosVentaGenerada);
        tvAdeudoPendiente = (TextView)findViewById(R.id.RealizarPagosAdeudoPendiente);
        tvTotalPorPagar = (TextView)findViewById(R.id.RealizarPagosTotalPorPagar);
        tvNuevoSaldoPendiente = (TextView)findViewById(R.id.RealizarPagosNuevoSaldoPendiente);
        etCapturaPago = (EditText) findViewById(R.id.RealizarPagosCapturaPago);
        tvMensajeParaContinuar = (TextView) findViewById(R.id.RealizarPagosMensajeParaContinuar);
        botonSiguiente = (Button) findViewById(R.id.RealizarPagosSiguiente);











        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            numeroDeCuentaCliente = bundle.getString("NUMERO_CUENTA_ENVIADO");

            cargarVistas();




        }





        etCapturaPago.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if (!etCapturaPago.getText().toString().equals("")){

                    pagoCapturado = Integer.valueOf(etCapturaPago.getText().toString());
                    nuevoAdeudoPendiente = totalPorPagar - pagoCapturado;


                    if (nuevoAdeudoPendiente<0){
                        etCapturaPago.setTextColor(Color.RED);
                        tvNuevoSaldoPendiente.setTextColor(Color.RED);
                    }else{
                        etCapturaPago.setTextColor(Color.BLACK);
                        tvNuevoSaldoPendiente.setTextColor(Color.BLACK);
                    }


                    tvNuevoSaldoPendiente.setText(String.valueOf(nuevoAdeudoPendiente));
                    botonSiguiente.setVisibility(View.VISIBLE);
                    calcularMensajesParaContinuar();




                }else{
                    tvNuevoSaldoPendiente.setText("");

                    String mensaje = "Por favor captura el pago del cliente para continuar\n\n" +
                                        "si no te hara ningun pago captura 0";

                    tvMensajeParaContinuar.setText(mensaje);
                    botonSiguiente.setVisibility(View.GONE);

                }



            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarDatosTablaClientes();
                Intent intent = new Intent(RealizarPago.this, RealizarEntrega.class);
                intent.putExtra("NUMERO_CUENTA_ENVIADO", numeroDeCuentaCliente);
                startActivity(intent);
            }
        });




    }


    private void cargarVistas (){

        Cursor datosCliente = dataBaseHelper.clientes_dameClientePorCuentaCliente(numeroDeCuentaCliente);
        if (datosCliente.getCount()>0){
            datosCliente.moveToFirst();

            String nombreCliente = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE));
            int adeudoPendiente = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE));
            int ventaGenerada = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CIE_VENTA_GENERADA_LLENAR));


            totalPorPagar = ventaGenerada + adeudoPendiente;
            int ochentaPorcientoVenta = (int) Math.ceil(ventaGenerada*0.8);
            pagoMinimoNecesarioParaEntregarMercancia = ochentaPorcientoVenta + adeudoPendiente;




            tvNombreCliente.setText(nombreCliente);
            tvVentaGenerada.setText(String.valueOf(ventaGenerada));
            tvAdeudoPendiente.setText(String.valueOf(adeudoPendiente));
            tvTotalPorPagar.setText(String.valueOf(totalPorPagar));






            String mensaje = "Por favor captura el pago del cliente para continuar\n\n" +
                    "-Si no te hara ningun pago captura 0";

            tvMensajeParaContinuar.setText(mensaje);
            botonSiguiente.setVisibility(View.GONE);







        }


    }


    private void calcularMensajesParaContinuar(){
        String mensaje;



        if (pagoCapturado >= pagoMinimoNecesarioParaEntregarMercancia){
            //si el cliente pago el minimo del pago
            //podremos dejar mercancia
            //pero si dejo un adeudo pendiente no podremos canjear puntos

            if (nuevoAdeudoPendiente > 0){
                mensaje = "Tu cliente dejara un saldo pendiente de $" + nuevoAdeudoPendiente + " \n\n " +
                        "Informale que:\n" +
                        "-Podremos entregarle mercancia pero no podremos cambiar sus puntos \n\n" +
                        "-Para canjear puntos no debe tener ningun adeudo pendiente.";

                tvMensajeParaContinuar.setText(mensaje);
                tvMensajeParaContinuar.setTextColor(Color.GRAY);
                botonSiguiente.setVisibility(View.VISIBLE);
                botonSiguiente.setText("Ya le informe a mi cliente. continuar->");

            }else if (nuevoAdeudoPendiente < 0){

                if (nuevoAdeudoPendiente>=-500){
                    //Si el cliente cubrio su saldo total pero esta abonando a favor una cantidad que no sea mayor a 500 pesos
                    mensaje = "Has capturado un pago mayor al requerido \n\n " +
                            "tu cliente tendra un saldo a favor de $" + (nuevoAdeudoPendiente*-1) +"\n\n" +
                            "-Estas seguro que quieres continuar? \n\n" +
                            "-Ese saldo a favor se vera reflejado en su siguiente cierre";

                    tvMensajeParaContinuar.setText(mensaje);
                    tvMensajeParaContinuar.setTextColor(Color.BLACK);
                    botonSiguiente.setVisibility(View.VISIBLE);
                    botonSiguiente.setText("Ya revice el pago. Continuar->");
                }else {
                    //Si el cliente cubrio su saldo total pero esta abonando a favor una cantidad mayor a 500 entonces no permitimos continuar
                    mensaje = "Has capturado un pago mayor al requerido \n\n " +
                            "estas capturando $" + (nuevoAdeudoPendiente * -1) + " de mas\n\n" +
                            "-Necesitas corregir el pago para poder continuar \n\n";

                    tvMensajeParaContinuar.setText(mensaje);
                    tvMensajeParaContinuar.setTextColor(Color.RED);
                    botonSiguiente.setVisibility(View.GONE);
                    botonSiguiente.setText("Ya revice el pago. Continuar->");

                }


            }else{

                //Si el cliente cubrio su saldo total
                mensaje = "Tu cliente a realizado su pago completo \n\n " +
                        "Eso le ayudara a aumentar su credito y mejorar su historial\n\n" +
                        "-Podremos entregarle mercancia \n\n" +
                        "-Podremos canjear sus puntos";

                tvMensajeParaContinuar.setText(mensaje);
                tvMensajeParaContinuar.setTextColor(Color.GRAY);

                botonSiguiente.setVisibility(View.VISIBLE);
                botonSiguiente.setText("Ya felicite a mi clienta. Continuar->");

            }



        }else{
            //Si el cliente no cubrio el minimo del pago

            mensaje = "Tu cliente dejara un saldo pendiente de $" + nuevoAdeudoPendiente + " \n\n " +
                    "-Si quiere recibir mercancia debe realizar el pago minimo de $" + pagoMinimoNecesarioParaEntregarMercancia + "\n\n"+
                    "Debes platicar con ella para que tenga listo su pago completo\n\n"+
                    "Informale que:\n"+
                    "-No podremos cambiar sus puntos hasta que liquide su cuenta\n\n" +
                    "-El no tener su pago completo afecta negativamente su historial";


            tvMensajeParaContinuar.setText(mensaje);
            tvMensajeParaContinuar.setTextColor(Color.GRAY);
            botonSiguiente.setVisibility(View.VISIBLE);
            botonSiguiente.setText("Ya le informe a mi cliente. continuar->");

        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();

        eliminarDatosTablaClientes();


    }


    private void guardarDatosTablaClientes(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_PAG_PAGO_POR_VENTA_CAPTURADO, pagoCapturado);
        contentValues.put(DataBaseHelper.CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA, nuevoAdeudoPendiente);

        boolean cumplioConPagoMinimo;
        cumplioConPagoMinimo = pagoCapturado >= pagoMinimoNecesarioParaEntregarMercancia;//sentencia if simplificada lo mismo que poner if(pagoCapturado>=pagoMinimoNecesarioParaEntregarMercancia) cumpliConPagoMinimo=true else cumpliConPagoMinimo = false

        contentValues.put(DataBaseHelper.CLIENTES_PAG_PAGO_MINIMO_REQUERIDO_PARA_ENTREGA_MERCANCIA, pagoMinimoNecesarioParaEntregarMercancia);
        contentValues.put(DataBaseHelper.CLIENTES_PAG_CUMPLIO_CON_PAGO_MINIMO, cumplioConPagoMinimo); //si es true llega la base de datos guarda un 1 porque no admiten booleanos
        contentValues.put(DataBaseHelper.CLIENTES_PAG_LATITUD , Constant.INSTANCE_LATITUDE);
        contentValues.put(DataBaseHelper.CLIENTES_PAG_LONGITUD, Constant.INSTANCE_LONGITUDE);
        contentValues.put(DataBaseHelper.CLIENTES_PAG_HORA, utilidadesApp.dameHora());



        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);


    }

    private void eliminarDatosTablaClientes(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_PAG_PAGO_POR_VENTA_CAPTURADO, 0);
        contentValues.put(DataBaseHelper.CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA, 0);
        contentValues.put(DataBaseHelper.CLIENTES_PAG_PAGO_MINIMO_REQUERIDO_PARA_ENTREGA_MERCANCIA, 0);
        contentValues.put(DataBaseHelper.CLIENTES_PAG_CUMPLIO_CON_PAGO_MINIMO, false);  //cuando pones false la base de datos guarda un 0 porque no admiten booleanos
        contentValues.put(DataBaseHelper.CLIENTES_PAG_LATITUD , 0);
        contentValues.put(DataBaseHelper.CLIENTES_PAG_LONGITUD, 0);
        contentValues.put(DataBaseHelper.CLIENTES_PAG_HORA, "");

        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);
    }
}

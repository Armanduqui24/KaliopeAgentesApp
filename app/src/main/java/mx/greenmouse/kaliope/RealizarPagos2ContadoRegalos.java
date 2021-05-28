package mx.greenmouse.kaliope;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RealizarPagos2ContadoRegalos extends AppCompatActivity {


    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
    TextView
            tvNombre,
    tvDiferenciaRegalo,
    tvVentaContado,
    tvDifCredito,
    tvTotalPorPagar,
    tvMensajeParaContinuar;

    EditText etPagoCapturado;

    Button botonSiguiente;

    LinearLayout
    layoutDiferenciaRegalo,
    layoutVentaContado,
    layoutDifCredito;


    String numeroDeCuentaCliente;

    int puntosRestantesConsultar = 0;
    int diferenciaRegalo = 0;
    int diferenciaExcesoCredito = 0;
    int ventaContadoConsultar = 0;
    int totalPorPagarCalcular = 0;
    int pagoCapturado = 0;
    int adeudo = 0;

    //consultamos los puntos restantes porque si estan en negativo significa que aqui va a dar su pago correspondiente entonces los puntos restantes los pondremos en 0
    //es la variagle diferenciaDeRegaloConsultar


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_pagos2_contado_regalos);
        getSupportActionBar().hide();



        tvNombre = (TextView) findViewById(R.id.RealizarPagosContadoRegalosNombreCliente);
        tvDiferenciaRegalo = (TextView) findViewById(R.id.RealizarPagosContadoRegalosPagoDiferenciaRegalos);
        tvVentaContado = (TextView) findViewById(R.id.RealizarPagosContadoRegalosVentaAlContado);
        tvDifCredito = (TextView) findViewById(R.id.RealizarPagosContadoRegalosDifCredito);
        tvTotalPorPagar = (TextView) findViewById(R.id.RealizarPagosContadoRegalosTotalPorPagar);
        tvMensajeParaContinuar = (TextView) findViewById(R.id.RealizarPagosContadoRegalosMensajeParaContinuar);

        etPagoCapturado = (EditText) findViewById(R.id.RealizarPagosContadoRegalosPagoCapturado);

        botonSiguiente = (Button) findViewById(R.id.RealizarPagosContadoRegalosBotonSiguiente);

        layoutDiferenciaRegalo = (LinearLayout) findViewById(R.id.RealizarPagosContadoRegalosLayoutDiferenciaRegalo);
        layoutVentaContado = (LinearLayout) findViewById(R.id.RealizarPagosContadoRegalosLayoutContado);
        layoutDifCredito = (LinearLayout) findViewById(R.id.RealizarPagosContadoRegalosLayoutDifCredito);



        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            numeroDeCuentaCliente = bundle.getString("NUMERO_CUENTA_ENVIADO");

            cargarVistas();




        }



        etPagoCapturado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if (!etPagoCapturado.getText().toString().equals("")){

                pagoCapturado = Integer.valueOf(etPagoCapturado.getText().toString());
                adeudo = totalPorPagarCalcular - pagoCapturado;




                botonSiguiente.setVisibility(View.VISIBLE);

                if (adeudo>0){
                    String mensaje = "Le faltan $" + adeudo + " para finalizar el pago, \n tu cliente no puede dejar saldos pendientes en este menu \n -Si quieres continuar tu cliente debe dar su pago completo \n" +
                            "-Si no tienes que regresar a la entrega de mercancia para editar tu entrega";
                    tvMensajeParaContinuar.setText(mensaje);
                    botonSiguiente.setVisibility(View.GONE);
                }else if (adeudo<0){
                    String mensaje = "Has ingresado un pago mayor al requerido para finalizar el pago, \n-Corrige el pago";
                    tvMensajeParaContinuar.setText(mensaje);
                    botonSiguiente.setVisibility(View.GONE);
                }else{
                    String mensaje = "Has ingresado el pago total correctamente puedes continuar";
                    tvMensajeParaContinuar.setText(mensaje);
                    botonSiguiente.setVisibility(View.VISIBLE);
                }




            }else{

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
                guardarDatosCliente();
                Intent intent = new Intent(RealizarPagos2ContadoRegalos.this, RealizarFinalizarMovimiento.class);
                intent.putExtra("NUMERO_CUENTA_ENVIADO", numeroDeCuentaCliente);
                startActivity(intent);
            }
        });



    }


    private void cargarVistas (){
        Cursor datosCliente = dataBaseHelper.clientes_dameClientePorCuentaCliente(numeroDeCuentaCliente);
        if (datosCliente.getCount()>0) {
            datosCliente.moveToFirst();

            tvNombre.setText(datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE)));
            puntosRestantesConsultar = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_PUNTOS_RESTANTES));

            if (puntosRestantesConsultar >=0){
                //significa que hay puntos en positivo
                diferenciaRegalo = 0;
                layoutDiferenciaRegalo.setVisibility(View.GONE);

                //si los puntos restantes son positivos pero si llega un cargo de la variable estatica redondeo de puntos que sea menor a 0
                //es decir el cliente v a apagar diferencia para ono perder puntos entonces
                if (RealizarEntrega.redondeoPositivoOnegativoDePuntosCanjeados < 0){
                    diferenciaRegalo = RealizarEntrega.redondeoPositivoOnegativoDePuntosCanjeados *-1;
                    layoutDiferenciaRegalo.setVisibility(View.VISIBLE);
                }
            }else {
                diferenciaRegalo = puntosRestantesConsultar * -1; //invertimos los puntos restantes en negativo para mostrar un importe en positivo
                layoutDiferenciaRegalo.setVisibility(View.VISIBLE);
            }





            ventaContadoConsultar = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_IMPORTE_CONTADO));

            if (ventaContadoConsultar==0){
                //significa que no hay ventas al contado
                layoutVentaContado.setVisibility(View.GONE);
            }else {
                layoutVentaContado.setVisibility(View.VISIBLE);
            }


            diferenciaExcesoCredito = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_DIFERENCIA_EXCESO_CREDITO));
            if (diferenciaExcesoCredito==0){
                //significa que no hay ventas al contado
                layoutDifCredito.setVisibility(View.GONE);
            }else {
                layoutDifCredito.setVisibility(View.VISIBLE);
            }



            totalPorPagarCalcular = diferenciaRegalo + ventaContadoConsultar + diferenciaExcesoCredito;



            tvDiferenciaRegalo.setText(String.valueOf(diferenciaRegalo));
            tvVentaContado.setText(String.valueOf(ventaContadoConsultar));
            tvDifCredito.setText(String.valueOf(diferenciaExcesoCredito));
            tvTotalPorPagar.setText(String.valueOf(totalPorPagarCalcular));
            tvMensajeParaContinuar.setText("Por favor captura el pago de tu cliente para continuar");
            botonSiguiente.setVisibility(View.GONE);

        }


    }


    private void guardarDatosCliente (){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_PA2_PAGO_DIFERENCIA_REGALO, diferenciaRegalo);
        contentValues.put(DataBaseHelper.CLIENTES_PA2_PAGO_POR_VENTA_CONTADO,ventaContadoConsultar);
        contentValues.put(DataBaseHelper.CLIENTES_PA2_PAGO_CAPTURADO,pagoCapturado);
        contentValues.put(DataBaseHelper.CLIENTES_PA2_LATITUD ,Constant.INSTANCE_LATITUDE);
        contentValues.put(DataBaseHelper.CLIENTES_PA2_LONGITUD,Constant.INSTANCE_LONGITUDE);
        contentValues.put(DataBaseHelper.CLIENTES_PA2_HORA,utilidadesApp.dameHora());

        if (puntosRestantesConsultar < 0){
            //Si los puntos restantes que llegaron a este activity fueron menores que 0
            //el activity solicita el pago de la diferencia y en cuanto lo pagan el activity continua, solo si pagan el activyti continua si no no finaliza
            //como ya pagaron la diferencia los puntos restantes ya no deberian de estar en negativo sino en 0
            contentValues.put(DataBaseHelper.CLIENTES_ENT_PUNTOS_RESTANTES,"0");
        }



        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);
    }

    private void eliminarDatosCliente (){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_PA2_PAGO_DIFERENCIA_REGALO,0);
        contentValues.put(DataBaseHelper.CLIENTES_PA2_PAGO_POR_VENTA_CONTADO,0);
        contentValues.put(DataBaseHelper.CLIENTES_PA2_PAGO_CAPTURADO,0);
        contentValues.put(DataBaseHelper.CLIENTES_PA2_LATITUD ,0);
        contentValues.put(DataBaseHelper.CLIENTES_PA2_LONGITUD,0);
        contentValues.put(DataBaseHelper.CLIENTES_PA2_HORA,"");

        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        eliminarDatosCliente();
    }
}

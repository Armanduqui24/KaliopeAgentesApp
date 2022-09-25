package mx.greenmouse.kaliope;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RealizarDecisiones extends AppCompatActivity {


    LinearLayout
    layoutEntregarMercancia,
    layoutDarDeBaja,
    layoutFinalizarMovimiento;

    TextView
    tvMensajeEntregarMercancia,
    tvTituloEntregarMercancia,
    tvMensajeDarDeBaja,
    tvTituloDarDeBaja,
    tvMensajeFinalizarMovimiento,
    tvTituloFinalizarMovimiento;

    Button
    botonEntregarMercancia,
    botonDarDeBaja,
    botonFinalizarMovimiento;

    final int HABILITAR = 1;
    final int DESABILITAR = 2;
    final int OCULTAR = 3;








    String numeroDeCuentaCliente;


    String nombreCliente;
    String estadoCliente;
    int limiteDeCredito;
    int pagoPorVentaCapturado;
    int nuevoAdeudoPorVenta;
    boolean cumplioConSuPagoMinimo;


    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_decisiones);
        getSupportActionBar().hide();


                layoutEntregarMercancia = (LinearLayout)findViewById(R.id.RealizarDecisionesLayoutEntregaMercancia);
                layoutDarDeBaja = (LinearLayout)findViewById(R.id.RealizarDecisionesLayoutDarDeBaja);
                layoutFinalizarMovimiento = (LinearLayout)findViewById(R.id.RealizarDecisionesLayoutFinalizarMovimiento);


                tvMensajeEntregarMercancia = (TextView)findViewById(R.id.RealizarDecisionesMensajeEntregaMercancia);
                tvTituloEntregarMercancia = (TextView)findViewById(R.id.RealizarDecisionesTituloEntregaMercancia);
                tvMensajeDarDeBaja = (TextView)findViewById(R.id.RealizarDecisionesMensajeDarDeBaja);
                tvTituloDarDeBaja = (TextView)findViewById(R.id.RealizarDecisionesTituloDarDeBaja);
                tvMensajeFinalizarMovimiento = (TextView)findViewById(R.id.RealizarDecisionesMensajeFinalizarMovimiento);
                tvTituloFinalizarMovimiento = (TextView)findViewById(R.id.RealizarDecisionesTituloFinalizarMovimiento);


                botonEntregarMercancia = (Button) findViewById(R.id.RealizarDecisionesBotonEntregaMercancia);
                botonDarDeBaja = (Button) findViewById(R.id.RealizarDecisionesBotonDarDeBaja);
                botonFinalizarMovimiento = (Button) findViewById(R.id.RealizarDecisionesBotonFinalizarMovimiento);






        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            numeroDeCuentaCliente = bundle.getString("NUMERO_CUENTA_ENVIADO");

            consultarDatosDelCliente();
            tomarDecisionDeFlujo();


        }



        botonEntregarMercancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RealizarDecisiones.this, RealizarEntrega.class);
                intent.putExtra("NUMERO_CUENTA_ENVIADO", numeroDeCuentaCliente);
                startActivity(intent);

            }
        });


        botonDarDeBaja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RealizarDecisiones.this, RealizarFinalizarMovimiento.class);
                intent.putExtra("NUMERO_CUENTA_ENVIADO", numeroDeCuentaCliente);
                startActivity(intent);

            }
        });

        botonFinalizarMovimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RealizarDecisiones.this, RealizarFinalizarMovimiento.class);
                intent.putExtra("NUMERO_CUENTA_ENVIADO", numeroDeCuentaCliente);
                startActivity(intent);

            }
        });

        layoutEntregarMercancia.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(RealizarDecisiones.this, "Anulando mediante codigo", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }


    private void consultarDatosDelCliente() {

        Cursor datosCliente = dataBaseHelper.clientes_dameClientePorCuentaCliente(numeroDeCuentaCliente);
        if (datosCliente.getCount() > 0) {
            datosCliente.moveToFirst();

            nombreCliente = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE));
            estadoCliente = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ESTADO_CLIENTE));
            limiteDeCredito = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CREDITO_CLIENTE));
            pagoPorVentaCapturado = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_PAGO_POR_VENTA_CAPTURADO));
            nuevoAdeudoPorVenta = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA));
            cumplioConSuPagoMinimo = (datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_CUMPLIO_CON_PAGO_MINIMO)) == 1); //para obtener el valor booleano debido a que no hay metodo getBoolean, esta es una forma practica encontrada en interntet
            Log.d("RealizarDecisiones1", "auxiliar cumplio con pago minimo " + cumplioConSuPagoMinimo);

        }


    }


    //Si el cliente esta activo y genero su pago, y puede recivir mercancia
    //nos vamos a entrega de mercancia
    //(si no genero su pago completo para dejarle mercancia o no tiene credito disponible por parte de administracion entonces
    // nos vamos a finalizar el movimiento
    // )


    private void tomarDecisionDeFlujo() {


        Log.d("RealizarDecisiones2", "grado cliente " + estadoCliente);
        Log.d("RealizarDecisiones2", "cumplio con pago minimo " + cumplioConSuPagoMinimo);
        Log.d("RealizarDecisiones2", "limite de credito " + limiteDeCredito);

        String mensaje;



        if(estadoCliente.equals(Constant.ACTIVO)){

            if(cumplioConSuPagoMinimo && limiteDeCredito > 0){



                if (nuevoAdeudoPorVenta>0){


                    entregaMercanciaHabilitarDesabilitarOcultar(HABILITAR,"Todo en orden puedes realizar la entrega de mercancia a tu cliente");
                    darDeBajaHabilitarDesabilitarOcultar(HABILITAR," Si tu cliente ya no quiere mas mercancia o quiere descansar un tiempo presiona aqui");
                    finalizarMovimientoHabilitarDesabilitarOcultar(OCULTAR,"");
                }else{

                    entregaMercanciaHabilitarDesabilitarOcultar(HABILITAR,"Todo en orden puedes realizar la entrega de mercancia a tu cliente");
                    darDeBajaHabilitarDesabilitarOcultar(OCULTAR,"");
                    finalizarMovimientoHabilitarDesabilitarOcultar(HABILITAR,"Si tu cliente ya no quiere mas mercancia finaliza el movimiento");


                }






            }else{
                //si no cumplio su pago minimo

                if (!cumplioConSuPagoMinimo){
                    mensaje =
                            "No podemos entregar mercancia tu cliente no cumplio con el pago minimo, puedes volver a pagos para realizar un pago mayor o anularlo con un codigo";
                }else if (limiteDeCredito ==0){
                    mensaje =
                            "No podemos entregar mercancia se a cancelado el credito del cliente desde administracion";
                }else{
                    mensaje="";
                }


                entregaMercanciaHabilitarDesabilitarOcultar(DESABILITAR,mensaje);
                darDeBajaHabilitarDesabilitarOcultar(OCULTAR,"");
                finalizarMovimientoHabilitarDesabilitarOcultar(HABILITAR,"Debemos terminar el movimiento del cliente");


                }


        }



        if(estadoCliente.equals(Constant.REACTIVAR)){

            if(limiteDeCredito > 0){


            }

        }


        if(estadoCliente.equals(Constant.PROSPECTO)){

            if(limiteDeCredito > 0){


            }

        }


        if(estadoCliente.equals(Constant.LIO)){

            if(limiteDeCredito > 0){

            }

        }






    }



    private void entregaMercanciaHabilitarDesabilitarOcultar(int accion, String mensaje){


        switch (accion){

            case HABILITAR:

                layoutEntregarMercancia.setVisibility(View.VISIBLE);
                botonEntregarMercancia.setEnabled(true);
                tvMensajeEntregarMercancia.setText(mensaje);
                tvTituloEntregarMercancia.setTextColor(getResources().getColor(R.color.colorPrimary));
                layoutEntregarMercancia.setBackgroundResource(R.drawable.border_luisda);

                break;

            case DESABILITAR:
                layoutEntregarMercancia.setVisibility(View.VISIBLE);
                botonEntregarMercancia.setEnabled(false);
                tvMensajeEntregarMercancia.setText(mensaje);
                tvTituloEntregarMercancia.setTextColor(getResources().getColor(R.color.colorGris));
                layoutEntregarMercancia.setBackgroundResource(R.drawable.border_gris);
                break;

            case OCULTAR:

                layoutEntregarMercancia.setVisibility(View.GONE);
                botonEntregarMercancia.setEnabled(true);
                tvMensajeEntregarMercancia.setText(mensaje);

                break;


        }




    }




    private void darDeBajaHabilitarDesabilitarOcultar(int accion, String mensaje){


        switch (accion) {

            case HABILITAR:

                layoutDarDeBaja.setVisibility(View.VISIBLE);
                botonDarDeBaja.setEnabled(true);
                tvMensajeDarDeBaja.setText(mensaje);

                tvTituloDarDeBaja.setTextColor(getResources().getColor(R.color.colorPrimary));
                layoutDarDeBaja.setBackgroundResource(R.drawable.border_luisda);

                break;

            case DESABILITAR:
                layoutDarDeBaja.setVisibility(View.VISIBLE);
                botonDarDeBaja.setEnabled(false);
                tvMensajeDarDeBaja.setText(mensaje);
                tvTituloDarDeBaja.setTextColor(getResources().getColor(R.color.colorGris));
                layoutDarDeBaja.setBackgroundResource(R.drawable.border_gris);
                break;

            case OCULTAR:

                layoutDarDeBaja.setVisibility(View.GONE);
                botonDarDeBaja.setEnabled(true);
                tvMensajeDarDeBaja.setText(mensaje);

                break;


        }




    }

    private void finalizarMovimientoHabilitarDesabilitarOcultar(int accion, String mensaje){


        switch (accion) {

            case HABILITAR:

                layoutFinalizarMovimiento.setVisibility(View.VISIBLE);
                botonFinalizarMovimiento.setEnabled(true);
                tvMensajeFinalizarMovimiento.setText(mensaje);

                tvTituloFinalizarMovimiento.setTextColor(getResources().getColor(R.color.colorPrimary));
                layoutFinalizarMovimiento.setBackgroundResource(R.drawable.border_luisda);

                break;

            case DESABILITAR:
                layoutFinalizarMovimiento.setVisibility(View.VISIBLE);
                botonFinalizarMovimiento.setEnabled(false);
                tvMensajeFinalizarMovimiento.setText(mensaje);
                tvTituloFinalizarMovimiento.setTextColor(getResources().getColor(R.color.colorGris));
                layoutFinalizarMovimiento.setBackgroundResource(R.drawable.border_gris);
                break;

            case OCULTAR:

                layoutFinalizarMovimiento.setVisibility(View.GONE);
                botonFinalizarMovimiento.setEnabled(true);
                tvMensajeFinalizarMovimiento.setText(mensaje);

                break;


        }




    }
}

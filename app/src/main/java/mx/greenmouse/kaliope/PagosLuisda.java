package mx.greenmouse.kaliope;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PagosLuisda extends AppCompatActivity implements View.OnClickListener {
    private Transition transition;
    Button termine;
    DataBaseHelper dBHelper = new DataBaseHelper(this);
    int adeudoAnterior,
        porLiquidar,
        nuevoSaldo,
        el80porciento,
        para80porciento,
        pagoIngresado;
    String nombre;

    EditText etPagoIngresado;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos_luisda);
        getSupportActionBar().hide();

        Slide slide = new Slide(Gravity.END);
        slide.setDuration(AltaEntradaActivity.DURATION_TRANSITION);
        slide.setInterpolator(new DecelerateInterpolator());
        getWindow().setEnterTransition(slide);
        getWindow().setAllowEnterTransitionOverlap(false);

        termine = (Button) findViewById(R.id.terminePagosButton);
        termine.setOnClickListener(this);

        etPagoIngresado = (EditText) findViewById(R.id.editText);




        etPagoIngresado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Toast.makeText(getApplicationContext(),"BeforeTextChanged",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String valor = etPagoIngresado.getText().toString();
                //Toast.makeText(getApplicationContext(),"onTextChanged" + valor,Toast.LENGTH_SHORT).show();

                refrescarVistas();
                Log.i("puntos Ganados:", String.valueOf(calcularPuntos()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Toast.makeText(getApplicationContext(),"AfterTextChanged",Toast.LENGTH_SHORT).show();

            }
        });




        obtenerDatosCliente();
        porLiquidar = calcularTotalLiquidar();
        calcularNuevoSaldo80porciento();
        cargarVistas();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.terminePagosButton:
                transition = new Slide(Gravity.START);
        }
    }





    private int calcularPuntos (){

        int tablaPuntos = 500;
        int puntosGanados = 50;



        while (pagoIngresado >= tablaPuntos){

            //(por ejemplo si el pago ingresado es de 1100)

            puntosGanados += 50;
            tablaPuntos += 250;
        }


        if (puntosGanados == 50){
            puntosGanados = 0;
        }


        Log.i("puntos Ganados:", String.valueOf(puntosGanados));
        return puntosGanados;

    }






    private void obtenerDatosCliente () {

        Cursor res = dBHelper.clientes_dameClientesPorId(Constant.ID_CLIENTE);
        res.moveToFirst();

        if (res.getCount()==1) {
            adeudoAnterior = Integer.parseInt(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE)));
            nombre = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE));
        }

    }




    //Creamos nuestro contenedor de vistas, para mantener el codigo mas ordenado
    private class Holder {
        TextView nombreView,
                adeudoView,
                ventaView,
                porLiquidarView,
                porLiquidarView2,
                el80porcientoView,
                para80porcientoView,
                nuevoSaldoView;

    }




    private void cargarVistas (){

        Holder h = new Holder();
        h.nombreView = (TextView) findViewById(R.id.textView50);
        h.adeudoView = (TextView) findViewById(R.id.textView34);
        h.ventaView = (TextView) findViewById(R.id.textView38);
        h.porLiquidarView = (TextView) findViewById(R.id.textView43);
        h.porLiquidarView2 = (TextView) findViewById(R.id.textView57);
        h.el80porcientoView = (TextView) findViewById(R.id.textView61);
        h.para80porcientoView = (TextView) findViewById(R.id.textView64);
        h.nuevoSaldoView = (TextView) findViewById(R.id.textView67);


        h.nombreView.setText(nombre);
        h.adeudoView.setText(String.valueOf(adeudoAnterior));
        h.ventaView.setText(String.valueOf(Constant.VENTA_GENERADA));
        h.porLiquidarView.setText(String.valueOf(porLiquidar));
        h.porLiquidarView2.setText(String.valueOf(porLiquidar));
        h.nuevoSaldoView.setText(String.valueOf(nuevoSaldo));
        h.el80porcientoView.setText(String.valueOf(el80porciento));
        h.para80porcientoView.setText(String.valueOf(para80porciento));


    }


    public void refrescarVistas(){


        calcularNuevoSaldo80porciento();
        cargarVistas();



        /*//refrescamos en tiempo de ejecucion
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {




                } catch (Exception e) {
                    Log.e("Error", "Exception: " + e.getMessage());
                }
            }
        });*/




    }


    private int calcularTotalLiquidar (){
        int totalLiquidar;
        totalLiquidar = adeudoAnterior + Constant.VENTA_GENERADA;
        return totalLiquidar;
    }

    private void calcularNuevoSaldo80porciento (){

        try {
            pagoIngresado = Integer.parseInt(etPagoIngresado.getText().toString());
            Constant.PAGO_INGRESADO = pagoIngresado;
        }catch (java.lang.NumberFormatException e){
            pagoIngresado = 0;
            e.printStackTrace();
        }

        nuevoSaldo = porLiquidar - pagoIngresado;
        //(redondemaos resultado al numero entero superior mas cercano y le hacemos un cast a int
        // ya que al multiplicar por el 80porciento se crea un double esto nos generaria
        // problemas al el agente ingresar el pago, o por ejemplo al validar
        // si el pago ingresado es mayor al 80%, mejor manejamos numeros enteros)
        el80porciento = (int) Math.ceil(porLiquidar * 0.8);
        para80porciento = (int) Math.ceil(el80porciento - pagoIngresado);

        //verificamos que para el 80% no sea menor a 0 si es menor colocamos 0 en su valor

        if (para80porciento <0){
            para80porciento = 0;
        }
    }




    private void resumenPagosAlertDialog (String venta, String saldo, String pagoTotal, String abono, String puntos, String mensaje){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.puntos_alert_dialog,null);

        TextView ventaGenerada = (TextView) v.findViewById(R.id.textView69);
        TextView saldoPendiente = (TextView) v.findViewById(R.id.textView71);
        TextView pagoTotalEntregado = (TextView) v.findViewById(R.id.textView73);
        TextView abonaAVenta = (TextView) v.findViewById(R.id.textView75);
        TextView puntosGenerados = (TextView) v.findViewById(R.id.textView77);
        TextView mensajeAdicional = (TextView) v.findViewById(R.id.textView78);

        ventaGenerada.setText(venta);
        saldoPendiente.setText(saldo);
        pagoTotalEntregado.setText(pagoTotal);
        abonaAVenta.setText(abono);
        puntosGenerados.setText(puntos);
        mensajeAdicional.setText(mensaje);

        builder.setView(v)
                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("quedarse en Pagos", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create();
        builder.show();




    }


}

package mx.greenmouse.kaliope;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RealizarCierre extends AppCompatActivity {

    String numeroDeCuentaCliente = "";
    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);


    TextView nombreClienteTV;
    TextView importeAcargoTV;
    TextView importeDevueltoTV;
    TextView ventaGeneradaTV;
    TextView puntosGanadosTV;
    TextView mensajeGanarMasPuntosET;
    TextView pagoExtraAbonadoET;

    Button regresarTomarPiezaDevolucionB;
    Button pagoAdicionalPuntosB;
    Button siguienteB;
    Button eliminarPagoExtraB;

    LinearLayout layoutPagoExtra;
    Vibrator vibrator;
    MediaPlayer mediaPlayer;


    int ventaTotalGenerada = 0;
    int puntosGanados = 0;
    int cantidadFaltanteParaSiguientesPuntos = 0;
    int pagoExtraGanarMasPuntos = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_cierre);

        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));


        getSupportActionBar().hide();
        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

        nombreClienteTV = (TextView) findViewById(R.id.RealizarCierreNombre);
        importeAcargoTV = (TextView) findViewById(R.id.RealizarCierreAcargo);
        importeDevueltoTV = (TextView) findViewById(R.id.RealizarCierreImporteDevuelto);
        ventaGeneradaTV = (TextView) findViewById(R.id.RealizarCierreVentaGenerada);
        siguienteB = (Button) findViewById(R.id.RealizarCierreSiguiente);
        puntosGanadosTV = (TextView) findViewById(R.id.RealizarCierrePuntosGanados);
        mensajeGanarMasPuntosET = (TextView) findViewById(R.id.RealizarCierreDescripcionGanarMasPuntos);
        pagoAdicionalPuntosB = (Button) findViewById(R.id.RealizarCierreGenerarPagoAdicional);
        eliminarPagoExtraB = (Button) findViewById(R.id.RealizarCierreEliminarPagoExtra);
        regresarTomarPiezaDevolucionB = (Button) findViewById(R.id.RealizarCierreTomarUnaPiezaMasDevolucion);
        layoutPagoExtra = (LinearLayout) findViewById(R.id.RealizarCierreLayoutPagoExtra);
        pagoExtraAbonadoET = (TextView) findViewById(R.id.RealizarCierreCargoAdicionalGanarPuntos);




        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            numeroDeCuentaCliente = bundle.getString("NUMERO_CUENTA_ENVIADO");

            cargarVistas();

        }



        siguienteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                guardarDatosTablaClientes();
                Intent intent = new Intent(getApplicationContext(), RealizarPago.class);
                intent.putExtra("NUMERO_CUENTA_ENVIADO", numeroDeCuentaCliente);
                startActivity(intent);


            }
        });

        pagoAdicionalPuntosB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 realizarCargoExtraParaGanarPuntos();
                vibrator.vibrate(100);
                mediaPlayer = MediaPlayer.create(RealizarCierre.this,R.raw.harpsound);
                mediaPlayer.start();
                Toast.makeText(RealizarCierre.this, "Pago agregado", Toast.LENGTH_SHORT).show();
            }
        });

        eliminarPagoExtraB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarCargoExtraParaGanarPuntos();
                vibrator.vibrate(100);
                mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.error);
                mediaPlayer.start();
                Toast.makeText(RealizarCierre.this, "Pago Eliminado", Toast.LENGTH_SHORT).show();
            }
        });



        regresarTomarPiezaDevolucionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regresarTomarPiezaDevolucion();
            }
        });

    }


    private void cargarVistas(){



        Cursor datosCliente = dataBaseHelper.clientes_dameClientePorCuentaCliente(numeroDeCuentaCliente);
        if (datosCliente.getCount() > 0) {
            datosCliente.moveToFirst();

            String nombreCliente = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE));
            int acargoCliente = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE));
            int importeDevuelto = dataBaseHelper.devolucion_mercancia_calcularImporteDevuelto(numeroDeCuentaCliente);
            pagoExtraGanarMasPuntos = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CIE_PAGO_EXTRA_PARA_GANAR_PUNTOS_LLENAR));




            nombreClienteTV.setText(nombreCliente);
            importeAcargoTV.setText(String.valueOf(acargoCliente));
            importeDevueltoTV.setText(String.valueOf(importeDevuelto));
            ventaTotalGenerada = acargoCliente - importeDevuelto + pagoExtraGanarMasPuntos;
            ventaGeneradaTV.setText(String.valueOf(ventaTotalGenerada));


            pagoExtraAbonadoET.setText(String.valueOf(pagoExtraGanarMasPuntos));
            //Si el pago extra registrado en la tabla de cleintes esta en 0 entonces ocultamos el layout
            if (pagoExtraGanarMasPuntos==0){
                layoutPagoExtra.setVisibility(View.GONE);
            }


            calcularPuntosGanados();



        }






    }







    private void calcularPuntosGanados (){



            int ventaInverior = 0;
            int ventaSuperior = 500;






            //Este metodo se podria hacer mas sencillo con bucle for pero para fines de que el codigo
        //sea mas claro y facil de mantener lo haremos con puros if
        if (ventaTotalGenerada<500){
            puntosGanados = 0;
            ventaInverior = 0;
            ventaSuperior = 500;

        }else if(ventaTotalGenerada>=500 && ventaTotalGenerada<750){
            puntosGanados = 100;
            ventaInverior = 500;
            ventaSuperior = 750;

        }else if (ventaTotalGenerada>=750 && ventaTotalGenerada<1000){
            puntosGanados = 150;
            ventaInverior = 750;
            ventaSuperior = 1000;

        }else if (ventaTotalGenerada>=1000 && ventaTotalGenerada<1250) {
            puntosGanados = 200;
            ventaInverior = 1000;
            ventaSuperior = 1250;

        }else if (ventaTotalGenerada>=1250 && ventaTotalGenerada<1500) {
            puntosGanados = 250;
            ventaInverior = 1250;
            ventaSuperior = 1500;

        }else if (ventaTotalGenerada>=1500 && ventaTotalGenerada<1750) {
            puntosGanados = 300;
            ventaInverior = 1500;
            ventaSuperior = 1750;

        }else if (ventaTotalGenerada>=1750 && ventaTotalGenerada<2000) {
            puntosGanados = 350;
            ventaInverior = 1750;
            ventaSuperior = 2000;

        }else if (ventaTotalGenerada>=2000 && ventaTotalGenerada<2250) {
            puntosGanados = 400;
            ventaInverior = 2000;
            ventaSuperior = 2250;

        }else if (ventaTotalGenerada>=2250 && ventaTotalGenerada<2500) {
            puntosGanados = 450;
            ventaInverior = 2250;
            ventaSuperior = 2500;

        }else if (ventaTotalGenerada>=2500 && ventaTotalGenerada<2750) {
            puntosGanados = 500;
            ventaInverior = 2500;
            ventaSuperior = 2750;

        }else if (ventaTotalGenerada>=2750 && ventaTotalGenerada<3000) {
            puntosGanados = 550;
            ventaInverior = 2750;
            ventaSuperior = 3000;

        }else if (ventaTotalGenerada>=3000 && ventaTotalGenerada<3250) {
            puntosGanados = 600;
            ventaInverior = 3000;
            ventaSuperior = 3250;

        }else if (ventaTotalGenerada>=3250 && ventaTotalGenerada<3500) {
            puntosGanados = 650;
            ventaInverior = 3250;
            ventaSuperior = 3500;

        }else if (ventaTotalGenerada>=3500 && ventaTotalGenerada<3750) {
            puntosGanados = 700;
            ventaInverior = 3500;
            ventaSuperior = 3750;

        }else if (ventaTotalGenerada>=3750 && ventaTotalGenerada<4000) {
            puntosGanados = 750;
            ventaInverior = 3750;
            ventaSuperior = 4000;

        }else if (ventaTotalGenerada>=4000 && ventaTotalGenerada<4250) {
            puntosGanados = 800;
            ventaInverior = 4000;
            ventaSuperior = 4250;

        }else if (ventaTotalGenerada>=4250 && ventaTotalGenerada<4500) {
            puntosGanados = 850;
            ventaInverior = 4250;
            ventaSuperior = 4500;

        }else if (ventaTotalGenerada>=4500 && ventaTotalGenerada<4750) {
            puntosGanados = 900;
            ventaInverior = 4500;
            ventaSuperior = 4750;

        }else if (ventaTotalGenerada>=4750 && ventaTotalGenerada<5000) {
            puntosGanados = 950;
            ventaInverior = 4750;
            ventaSuperior = 5000;

        }else if (ventaTotalGenerada>=5000 && ventaTotalGenerada<5250) {
            puntosGanados = 1000;
            ventaInverior = 5000;
            ventaSuperior = 5250;

        }else if (ventaTotalGenerada>=5250 && ventaTotalGenerada<5500) {
            puntosGanados = 1050;
            ventaInverior = 5250;
            ventaSuperior = 5500;

        }else if (ventaTotalGenerada>=5500 && ventaTotalGenerada<5750) {
            puntosGanados = 1100;
            ventaInverior = 5500;
            ventaSuperior = 5750;

        }else if (ventaTotalGenerada>=5750 && ventaTotalGenerada<6000) {
            puntosGanados = 1150;
            ventaInverior = 5750;
            ventaSuperior = 6000;

        }else if (ventaTotalGenerada>=6000 && ventaTotalGenerada<6250) {
            puntosGanados = 1200;
            ventaInverior = 6000;
            ventaSuperior = 6250;

        }else if (ventaTotalGenerada>=6250 && ventaTotalGenerada<6500) {
            puntosGanados = 1250;
            ventaInverior = 6250;
            ventaSuperior = 6500;

        }else if (ventaTotalGenerada>=6500 && ventaTotalGenerada<6750) {
            puntosGanados = 1300;
            ventaInverior = 6500;
            ventaSuperior = 6750;

        }else if (ventaTotalGenerada>=6750 && ventaTotalGenerada<7000) {
            puntosGanados = 1350;
            ventaInverior = 6750;
            ventaSuperior = 7000;

        }


        puntosGanadosTV.setText(String.valueOf(puntosGanados));
        calcularMensajeYcantidadParaGanarMasPuntos(ventaInverior,ventaSuperior);





    }

    private void calcularMensajeYcantidadParaGanarMasPuntos (int ventainferior, int ventaSuperior){
        //(Este metodo mostrara los mensajes al cliente si gano puntos, si no los gano dira el motivo
        // y si no los gano pero le falta una cantidad pequeña para llegar al siguiente escalon y ganarlos
        // permitira un abono de dinero extra o si la cantidad es un poco mas grande permitira que vuelva a la devolucion
        // y elimine una pieza para incrementar su cierre)


        Log.d("RealizarCierre10","inferior " + String.valueOf(ventainferior));
        Log.d("RealizarCierre10","Superior " + String.valueOf(ventaSuperior));

        cantidadFaltanteParaSiguientesPuntos = ventaSuperior - ventaTotalGenerada;

        Log.d("RealizarCierre10","cantidadParaSiguietnesPuntos " + String.valueOf(cantidadFaltanteParaSiguientesPuntos));
        Log.d("RealizarCierre10","ventaTotalGenerada " + String.valueOf(ventaTotalGenerada));







        if (ventaTotalGenerada>=500){

            String mensaje;

            if (cantidadFaltanteParaSiguientesPuntos <=40){
                //(Como aqui solo puede ganarce 50 puntos mas no le veo caso que por ejemplo el sistema marque
                // le faltan 90 pesos para ganar 50 puntos, jaja me explico que chiste? se trata de motivar al cliente
                // no de sacarle mas dinero)


                mensaje  = "Felicita a tu cliente por su venta superior a $"+ ventainferior + "\n\n" +
                        "Informale que:\n\n" +
                        "-Le faltan solamente $" + cantidadFaltanteParaSiguientesPuntos +" para ganar otros 50 puntos \n\n" +
                        "-Si quiere ganarlos puede hacer lo siguiente:";
                mensajeGanarMasPuntosET.setText(mensaje);


                if (cantidadFaltanteParaSiguientesPuntos <= 35){
                    String mensajeCasilla = "-Hacer un pago extra por $" + cantidadFaltanteParaSiguientesPuntos;
                    pagoAdicionalPuntosB.setText(mensajeCasilla);
                    regresarTomarPiezaDevolucionB.setVisibility(View.VISIBLE);
                    pagoAdicionalPuntosB.setVisibility(View.VISIBLE);
                }else{
                    //si la cantidad para los siguientes puntos es mayor que 30 entonces desabilitamos el boton de cargo extra
                    pagoAdicionalPuntosB.setVisibility(View.GONE);
                    regresarTomarPiezaDevolucionB.setVisibility(View.VISIBLE);
                }

                siguienteB.setText("No hacer nada, continuar a pagos->");

            }else{


                //Si le faltan mas de 80 pesos para alcanzar la siguiente meta de puntos
                //no mostraremos nada de las ocpines para ganar mas puntos y solo mostramos el boton de siguiente
                mensaje  = "Felicita a tu cliente por su venta superior a $" + ventainferior;
                mensajeGanarMasPuntosET.setText(mensaje);

                pagoAdicionalPuntosB.setVisibility(View.GONE);
                regresarTomarPiezaDevolucionB.setVisibility(View.GONE);
                siguienteB.setText("Ya la felicite continuar a pagos->");
            }






        }else{

            //(Si la venta generada es menor de 500 se podria ganar en lugar de 50 100 puntos
            //
            // como aqui puede ganar 100 puntos entonces si le faltan menos de 80 pesos para
            // ganarce los 100 puntos habilitamos las opciones)


            if (cantidadFaltanteParaSiguientesPuntos <=80){

                String mensaje  = "No ha gando puntos porque la venta ha sido menor que $500 \n\n" +
                        "Informale que:\n\n" +
                        "-Le faltan solamente $" + cantidadFaltanteParaSiguientesPuntos +" para ganar los 100 puntos \n\n" +
                        "-Si quiere ganarlos puede hacer lo siguiente:";
                mensajeGanarMasPuntosET.setText(mensaje);


                if (cantidadFaltanteParaSiguientesPuntos <= 45){
                    String mensajeCasilla = "-Hacer un pago extra por $" + cantidadFaltanteParaSiguientesPuntos;
                    pagoAdicionalPuntosB.setText(mensajeCasilla);
                    pagoAdicionalPuntosB.setVisibility(View.VISIBLE);
                    regresarTomarPiezaDevolucionB.setVisibility(View.VISIBLE);

                }else{
                    //si la cantidad para los siguientes puntos es mayor que 30 entonces desabilitamos el boton de
                    //para generar el cargo del dinero
                    pagoAdicionalPuntosB.setVisibility(View.GONE);
                    regresarTomarPiezaDevolucionB.setVisibility(View.VISIBLE);
                }

                siguienteB.setText("No hacer nada, continuar a pagos->");



            }else{
                //si le faltan mas de 80 para ganar puntos quitamos las opciones
                String mensaje  = "No ha gando puntos porque la venta ha sido menor que $500 \n\n" +
                                    "-Motivala para que eleve sus ventas";
                mensajeGanarMasPuntosET.setText(mensaje);
                pagoAdicionalPuntosB.setVisibility(View.GONE);
                regresarTomarPiezaDevolucionB.setVisibility(View.GONE);
                siguienteB.setText("Ya la motive continuar a pagos->");
            }





        }




    }

    private void realizarCargoExtraParaGanarPuntos (){


        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_CIE_PAGO_EXTRA_PARA_GANAR_PUNTOS_LLENAR, cantidadFaltanteParaSiguientesPuntos);
        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);

        int auxiliar = ventaTotalGenerada + cantidadFaltanteParaSiguientesPuntos;
        ventaGeneradaTV.setText(String.valueOf(auxiliar));
        layoutPagoExtra.setVisibility(View.VISIBLE);

        cargarVistas();

    }

    private void eliminarCargoExtraParaGanarPuntos (){
        dataBaseHelper.clientes_pagosEliminaPagoExtraParaGanarPuntos(numeroDeCuentaCliente);
        cargarVistas();
    }


    private void regresarTomarPiezaDevolucion(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(RealizarCierre.this);

        TextView title = new TextView(RealizarCierre.this);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("Tomar pieza de devolucion");
        //builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("Regresaremos a la pantalla de devolucion solo deberas eliminar el producto para que incremente la venta del cliente\n\n" +

                "-Deja presionado el producto que quieres eliminar\n\n" +
                "-Despues da clic en el boton siguiente\n\n" +
                "-Si no quieres eliminar nada solo vuelve a dar clic en siguiente")

                .setNegativeButton("Entiendo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                    }
                });

        builder.create();
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        eliminarDatosTablaClientes(dataBaseHelper);
    }

    private void guardarDatosTablaClientes(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_CIE_VENTA_GENERADA_LLENAR,ventaTotalGenerada);
        contentValues.put(DataBaseHelper.CLIENTES_CIE_PAGO_EXTRA_PARA_GANAR_PUNTOS_LLENAR,pagoExtraGanarMasPuntos);
        contentValues.put(DataBaseHelper.CLIENTES_CIE_PUNTOS_GANADOS_VENTA_LLENAR,puntosGanados);
        contentValues.put(DataBaseHelper.CLIENTES_CIE_LATITUD,Constant.INSTANCE_LATITUDE);
        contentValues.put(DataBaseHelper.CLIENTES_CIE_LONGITUD,Constant.INSTANCE_LONGITUDE);
        contentValues.put(DataBaseHelper.CLIENTES_CIE_HORA,utilidadesApp.dameHora());


        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);



    }

    public void eliminarDatosTablaClientes(DataBaseHelper db){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_CIE_VENTA_GENERADA_LLENAR,ventaTotalGenerada);
        contentValues.put(DataBaseHelper.CLIENTES_CIE_PAGO_EXTRA_PARA_GANAR_PUNTOS_LLENAR,0); //este no lo eliminamos para que si se capturo un pago extra y por error regresa a la devolucion al volver ese pago extra siga apareciendo
        contentValues.put(DataBaseHelper.CLIENTES_CIE_PUNTOS_GANADOS_VENTA_LLENAR,"0");
        contentValues.put(DataBaseHelper.CLIENTES_CIE_LATITUD,0);
        contentValues.put(DataBaseHelper.CLIENTES_CIE_LONGITUD,0);
        contentValues.put(DataBaseHelper.CLIENTES_CIE_HORA,0);
        db.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);
    }
}

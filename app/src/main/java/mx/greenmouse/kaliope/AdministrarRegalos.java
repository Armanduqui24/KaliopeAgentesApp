package mx.greenmouse.kaliope;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdministrarRegalos {

   private Activity activity;

    private String numeroDeCuentaCliente;

    private int row_id;
    private DataBaseHelper dataBaseHelper;

    MediaPlayer mediaPlayer = new MediaPlayer();
    Vibrator vibrator;



    Button
            botonExito,
            botonCerrarDialogo,
            botonEliminar;

   private TextView
    tvMensaje1,
    tvMensaje2,
    tvPiezasDescripcionRegalo,
    tvPuntosDisponibesDesdeAdministracion,
    tvPuntosGanadosPorVenta,
    tvPuntosGanadosPorVentaContado,
    tvPuntosTotales,
    tvPuntosYaCanjeados,
    tvPuntosPorCanjear,
    tvPuntosRestantes
    ;


   private LinearLayout layoutDescripcionPuntos;





    private int cantidadConsultar=0;
    private int precioConsultar=0;
    private int nuevoSaldoPendienteConsultar = 0;
    private int importeEnPuntosCalculado=0;

    private int puntosDisponiblesDesdeAdministracionConsulta = 0;
    private int puntosGanadosPorVentaConsulta = 0;
    private int puntosGanadosPorContadoConsulta = 0;
    private int puntosTotalesCalculado = 0;
    private int puntosYaCanjeadosConsulta = 0;
    private int puntosPorCanjearCalculado = 0;
    private int puntosRestantesCalculado = 0;




    public AdministrarRegalos(Activity activity, String numeroDeCuentaCliente,int row_id, DataBaseHelper dataBaseHelper ){

        this.activity = activity;
        this.numeroDeCuentaCliente = numeroDeCuentaCliente;
        this.row_id = row_id;
        this.dataBaseHelper = dataBaseHelper;

        vibrator = (Vibrator) activity.getSystemService(activity.VIBRATOR_SERVICE);
    }











    public void dialogoRegalarSalida (){


        consultarDatos();





        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogo_pantalla_de_regalo_mercancia,null);



        tvMensaje1 = (TextView)view.findViewById(R.id.DialogoPantallaDeRegaloMensajeUno);
        tvMensaje2 = (TextView)view.findViewById(R.id.DialogoPantallaDeRegaloMensajeDos);
        tvPiezasDescripcionRegalo = (TextView)view.findViewById(R.id.DialogoPantallaDeRegaloDescripcionPiezas);
        tvPuntosDisponibesDesdeAdministracion = (TextView)view.findViewById(R.id.DialogoPantallaDeRegaloPuntosDisponiblesDesdeAdministracion);
        tvPuntosGanadosPorVenta = (TextView)view.findViewById(R.id.DialogoPantallaDeRegaloPuntosGanadosPorVenta);
        tvPuntosGanadosPorVentaContado = (TextView)view.findViewById(R.id.DialogoPantallaDeRegaloPuntosGanadosPorVentaContado);
        tvPuntosTotales = (TextView)view.findViewById(R.id.DialogoPantallaDeRegaloPuntosTotales);
        tvPuntosYaCanjeados = (TextView)view.findViewById(R.id.DialogoPantallaDeRegaloPuntosYaCanjeados);
        tvPuntosPorCanjear = (TextView)view.findViewById(R.id.DialogoPantallaDeRegaloPuntosPorCanjear);
        tvPuntosRestantes = (TextView)view.findViewById(R.id.DialogoPantallaDeRegaloPuntosRestantes);
        layoutDescripcionPuntos = (LinearLayout) view.findViewById(R.id.DialogoPantallaDeRegaloLayoutCalculoPuntos);



        botonExito = (Button) view.findViewById(R.id.DialogoPantallaDeRegaloBoton1);
        botonCerrarDialogo = (Button) view.findViewById(R.id.DialogoPantallaDeRegaloBoton2);
        botonEliminar =(Button) view.findViewById(R.id.DialogoPantallaDeRegaloBoton3);

        llenarVistas();



        //esto lo hacemos para poder cerrar el alert dialog la otra opcion esta en la clase de variable password
        final AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);



        botonExito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertirPiezaAregalo();
                //guardarDatosTablaClientes();
                alertDialog.cancel();
            }
        });


        botonCerrarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });


        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();

            }
        });






        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.create();
        alertDialog.show();




    }




    private void consultarDatos(){

        Cursor cursor = dataBaseHelper.clientes_dameClientePorCuentaCliente(numeroDeCuentaCliente);
        if(cursor.getCount()>0){
            cursor.moveToFirst();


            puntosDisponiblesDesdeAdministracionConsulta = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_PUNTOS_DISPONIBLES));
            puntosGanadosPorVentaConsulta = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.CLIENTES_CIE_PUNTOS_GANADOS_VENTA_LLENAR));
            puntosGanadosPorContadoConsulta = dataBaseHelper.clientes_calcularPuntosGanadosVentaAlContado(numeroDeCuentaCliente);
            puntosTotalesCalculado = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_PUNTOS_DISPONIBLES))
                                    + cursor.getInt(cursor.getColumnIndex(DataBaseHelper.CLIENTES_CIE_PUNTOS_GANADOS_VENTA_LLENAR))
                                    + dataBaseHelper.clientes_calcularPuntosGanadosVentaAlContado(numeroDeCuentaCliente);

            puntosYaCanjeadosConsulta = dataBaseHelper.entrega_mercancia_calcularImporteEntrega(numeroDeCuentaCliente,RealizarEntrega.REGALO);
            nuevoSaldoPendienteConsultar = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA));

        }





        Cursor consultarProducto = dataBaseHelper.entrega_mercancia_dameRenglon(row_id);
        if(consultarProducto.getCount()>0){
            consultarProducto.moveToFirst();
            cantidadConsultar = consultarProducto.getInt(consultarProducto.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CANTIDAD));
            precioConsultar = consultarProducto.getInt(consultarProducto.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_PRECIO));
            puntosPorCanjearCalculado = cantidadConsultar * precioConsultar;
            puntosRestantesCalculado = puntosTotalesCalculado-puntosYaCanjeadosConsulta-puntosPorCanjearCalculado;
        }



    }





    private void llenarVistas(){





        layoutDescripcionPuntos.setVisibility(View.VISIBLE);
        String descripcion = cantidadConsultar + "  " + precioConsultar;
        tvPiezasDescripcionRegalo.setText(descripcion);
        tvPuntosDisponibesDesdeAdministracion.setText(String.valueOf(puntosDisponiblesDesdeAdministracionConsulta));
        tvPuntosGanadosPorVenta.setText(String.valueOf(puntosGanadosPorVentaConsulta));
        tvPuntosGanadosPorVentaContado.setText(String.valueOf(puntosGanadosPorContadoConsulta));
        tvPuntosTotales.setText(String.valueOf(puntosTotalesCalculado));
        tvPuntosYaCanjeados.setText(String.valueOf(puntosYaCanjeadosConsulta));
        tvPuntosPorCanjear.setText(String.valueOf(puntosPorCanjearCalculado));
        tvPuntosRestantes.setText(String.valueOf(puntosRestantesCalculado));
        tvMensaje2.setText("");



        //como en el activity de entrega para que pueda aparecer el boton de regalar el producto
        //tiene que tener puntos disponibles, y la prohibicion de registrar regalos debe de estar en false es decir
        //se pone en false si el cliente tiene saldo pendiente, ya no validaremos eso en esta actividad
        //Crei que seria buena idea no validar que sus puntos si estan en negativo no sean menores a 150
        // pero creo que si es importante porque si no los promotores podrian ingrear e ingresar regalos con la intencion de
        // dejarle mas mercancia y en el area de pagos de regalo y puntos solo ingrear el pago completo y al llegar a oficina
        // reportar que a la clienta se le dejron esas piezas de mas y hacer un completo relajo entonces limitamos que cuando al cliente
        // ya se le hayan terminado los puntos solo pueda dar una diferencia de 150 en efectivo si no, el regalo ya no se puede ingresar)


        if(puntosRestantesCalculado<-500){
            tvMensaje1.setText("No podemos regalar el producto los puntos no son suficientes para cubrir el regalo, el cliente tendria que dar una diferencia mayor a 500");
            String mensaje = "Si quieres capturar mas regalos elimina los que ya estan capturados";
            tvMensaje2.setText(mensaje);
            botonExito.setVisibility(View.GONE);
            botonCerrarDialogo.setVisibility(View.VISIBLE);
            botonCerrarDialogo.setText("Entiendo");
            botonEliminar.setVisibility(View.GONE);

        }else if (puntosRestantesCalculado<0 && puntosRestantesCalculado>-500){
            String mensajes = "$" + puntosRestantesCalculado;
            tvMensaje1.setText("Si capturas este regalo el cliente tendra que dar una diferencia de:");
            tvMensaje2.setText(mensajes);
            //layoutDescripcionPuntos.setVisibility(View.GONE);
            botonExito.setVisibility(View.VISIBLE);
            botonExito.setText("Continuar");
            botonCerrarDialogo.setVisibility(View.VISIBLE);
            botonCerrarDialogo.setText("Cancelar");
            botonEliminar.setVisibility(View.GONE);
        }else{
                //si todo en orden
            tvMensaje1.setText("Confirma que canjearas estos productos");
            tvMensaje2.setText("");
            //layoutDescripcionPuntos.setVisibility(View.GONE);
            botonExito.setVisibility(View.VISIBLE);
            botonExito.setText("Continuar");
            botonCerrarDialogo.setVisibility(View.VISIBLE);
            botonCerrarDialogo.setText("Cancelar");
            botonEliminar.setVisibility(View.GONE);

        }










    }

    private void convertirPiezaAregalo (){

        //convertimos la pieza que nos enviaron a regalo
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO, RealizarEntrega.REGALO);
        contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_IMPORTE_TOTAL_ENTREGA,"");
        contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_DISTRIBUCION,"");
        contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_GANANCIA,precioConsultar);
        contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_GRADO_DE_ENTREGA,"");
        dataBaseHelper.entrega_mercancia_actualizaRenglon(row_id,contentValues);

        mediaPlayer = MediaPlayer.create(activity,R.raw.exito);
        mediaPlayer.start();
        vibrator.vibrate(100);




    }






}

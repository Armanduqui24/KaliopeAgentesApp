package mx.greenmouse.kaliope;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import static mx.greenmouse.kaliope.Constant.FIRST_COLUMN;
import static mx.greenmouse.kaliope.Constant.FIVE_COLUMN;
import static mx.greenmouse.kaliope.Constant.FOURTH_COLUMN;
import static mx.greenmouse.kaliope.Constant.SECOND_COLUMN;
import static mx.greenmouse.kaliope.Constant.SIX_COLUMN;
import static mx.greenmouse.kaliope.Constant.THIRD_COLUMN;

public class MenuMovimientosActivity extends AppCompatActivity implements View.OnClickListener {

    DataBaseHelper dbHelper = new DataBaseHelper(this);
    Constant c = new Constant();

    String entradasTotales;
    String salidasTotales;
    String pagosTotales;

    Button btnImprimeMovimientos;
    Button btnExportarMovimientos;


    TextView txtTotalEntradas;
    TextView txtTotalSalidas;
    TextView txtTotalPagos;
    Activity activity;

    public static ListView lview;
    private ArrayList<HashMap> list;

    private static BluetoothSocket btsocket_bit;
    private static OutputStream btoutputstream_bit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_movimientos);
        getSupportActionBar().hide();
        activity = this;

        txtTotalEntradas = (TextView) findViewById(R.id.txtTotalEntradas);
        txtTotalSalidas = (TextView) findViewById(R.id.txtTotalSalidas);
        txtTotalPagos   = (TextView) findViewById(R.id.txtTotalPagos);





        try {
            entradasTotales = dbHelper.dameMovimientosTotales("E");
            salidasTotales  = dbHelper.dameMovimientosTotales("S");
            pagosTotales    = dbHelper.damePagosTotales();

            txtTotalEntradas.setText(entradasTotales);
            txtTotalSalidas.setText(salidasTotales);
            txtTotalPagos.setText(pagosTotales);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error en OnCreate " + e.toString(),Toast.LENGTH_LONG).show();
        }







        btnImprimeMovimientos = (Button) findViewById(R.id.btnImprimeMovimientos);
        btnExportarMovimientos = (Button) findViewById(R.id.btExportarMovimientos);


        btnImprimeMovimientos.setOnClickListener(this);
        btnExportarMovimientos.setOnClickListener(this);
        btnExportarMovimientos.setVisibility(View.INVISIBLE);

        lview = (ListView) findViewById(R.id.lvMovimientos);

        try {
            if(btsocket_bit!= null){
                btoutputstream_bit.close();
                btsocket_bit.close();
                btsocket_bit = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            listaMovimientos();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error de listaMovimientos: " + e.toString(),Toast.LENGTH_LONG).show();
        }

    }

    public void listaMovimientos () throws Exception{


        list = new ArrayList<HashMap>();
        Integer suma = 0;

        ListViewAdapterMovimientos adapter = new ListViewAdapterMovimientos(this, list);
        lview.setAdapter(adapter);


        Cursor res = dbHelper.dameMovimientos();
        Log.d("dbg-res", String.valueOf(res.getCount()));

        if(res.getCount()>0) {

            res.moveToFirst();
            do {

                String nombreClienteCorto = res.getString(res.getColumnIndex(DataBaseHelper.MOVEMENTS_NAME)).substring(0, 12);


                suma = Integer.parseInt(res.getString(3)) + Integer.parseInt(res.getString(4)) +
                       Integer.parseInt(res.getString(5)) + Integer.parseInt(res.getString(6));

                HashMap temp1 = new HashMap();
                temp1.put(FIRST_COLUMN, res.getString(res.getColumnIndex(DataBaseHelper.KEY_ID)));
                temp1.put(SECOND_COLUMN, nombreClienteCorto);
                temp1.put(THIRD_COLUMN, res.getString(res.getColumnIndex(DataBaseHelper.MOVEMENTS_ACCOUNT)));
                temp1.put(FOURTH_COLUMN, dbHelper.dameMovimientosTotalesId(res.getString(0), "E"));
                temp1.put(FIVE_COLUMN, dbHelper.dameMovimientosTotalesId(res.getString(0), "S"));
                temp1.put(SIX_COLUMN, String.valueOf(suma));

                list.add(temp1);

            } while (res.moveToNext());


            lview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String cuentaCliente = ((TextView) view.findViewById(R.id.ThirdText)).getText().toString();
                    String idMovimiento  = ((TextView) view.findViewById(R.id.FirstText)).getText().toString();

                    verMovimiento(idMovimiento, cuentaCliente);

                    return false;
                }

            });


        }

        Log.d("dbg-output",String.valueOf(Constant.TMPMOV_OUTPUT));

    }

    public void verMovimiento(String idMovimiento, String cuentaCliente){
        Intent intent = new Intent(this, VistaMovimientoActivity.class);
        intent.putExtra("cuentaCliente", cuentaCliente);
        intent.putExtra("idMovimiento", idMovimiento);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            /*case R.id.btnNuevoMovimiento:

                Intent i = new Intent(this, AltaMovimientoActivity.class);
                Constant.QUIEN_LLAMA=0; //le decimos al activyti siguiente que lo llamamos desde alta movimiento
                startActivity(i);

                break;*/

            case R.id.btnImprimeMovimientos:

                connect();

                break;

            case R.id.btExportarMovimientos:

               //dialogoConfirmacionExportarMovimientos(this);

                break;
        }
    }

    public void dialogoConfirmacionExportarMovimientos(final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final Integer[] r = new Integer[1];

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("¡Cuidado!");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("¿Realmente desea exportar los movimientos?")
                .setPositiveButton("SI, EXPORTAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            exportaMovimientos(dbHelper, activity);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("NO, CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });

        builder.create();
        builder.show();


    }

    public void exportaMovimientos(DataBaseHelper dataBaseHelper, Activity activity) throws IOException {

        Cursor res = dataBaseHelper.dameMovimientosCompletos();
        res.moveToFirst();

        Constant.INSTANCE_PATH = String.valueOf(Environment.getExternalStorageDirectory());

        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(Constant.INSTANCE_PATH + "/mx.4103.klp/MOV_" + ConfiguracionesApp.getUsuarioIniciado(activity) + "_" + ConfiguracionesApp.getZonaVisitar1(activity) + " " + utilidadesApp.dameFechaConSegundo() + ".txt"));


        if (res.getCount()>0){

            do{
                bw.write("CUENTA,NOMBRE,FECHA,CLIENTES_ADMIN_VENCIMIENTO,VENTA,HORA_VENTA,REGDIF,HORA_REGDIF,SALDO_PENDIENTE,HORA_SALDOPENDIENTE,OTROS,HORA_OTROS,NUEVO_SALDO,HORA_NUEVOSALDO,CLIENTES_ADMIN_PUNTOS_DISPONIBLES,HORA_PUNTOS,CLIENTES_ADMIN_REPORTE DE VISITA\n");
                bw.write(res.getString(res.getColumnIndex(DataBaseHelper.MOVEMENTS_ACCOUNT)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.MOVEMENTS_NAME)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.DATE_UP)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.MOVEMENTS_EXPIRATION_DATE)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_PAGO)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_PAGOS_HORA)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_DIFERENCIA)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_HORA_DIFERENCIA)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_SALDO_PENDIENTE)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_HORA_SALDO_PENDIENTE)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_OTRO)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_HORA_OTRO)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_ADEUDO)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_HORA_ADEUDO)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_PUNTOS)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.PAGOS_HORA_PUNTOS)) + "," +
                        res.getString(res.getColumnIndex(DataBaseHelper.MOVEMENTS_REPORT)) +
                        "\n");

                bw.write("CANTIDAD,PRECIO,DISTRIBUIDOR,GANANCIA,INVENTARIO_CODIGO_PRODUCTO,TIPO_MOVIMIENTO,LATITUD,LONGITUD,FECHA\n");

                Cursor mov = dataBaseHelper.dameDetallesCompletos(res.getString(0));

                if(mov.getCount() > 0) {

                    mov.moveToFirst();

                    do {
                        bw.write(mov.getString(mov.getColumnIndex(DataBaseHelper.DETALLES_CANTIDAD)) + "," +
                                mov.getString(mov.getColumnIndex(DataBaseHelper.DETALLES_PRECIO_PRODUCTO)) + "," +
                                mov.getString(mov.getColumnIndex(DataBaseHelper.DETALLES_PRECIO_DISTRIBUCION)) + "," +
                                mov.getString(mov.getColumnIndex(DataBaseHelper.DETALLES_GANANCIA)) + "," +
                                mov.getString(mov.getColumnIndex(DataBaseHelper.DETALLES_CODIGO_PRODUCTO)) + "," +
                                mov.getString(mov.getColumnIndex(DataBaseHelper.DETALLES_TIPO_MOVIMIENTO)) + "," +
                                mov.getString(mov.getColumnIndex(DataBaseHelper.DETALLES_LATITUD)) + "," +
                                mov.getString(mov.getColumnIndex(DataBaseHelper.DETALLES_LONGUITUD)) + "," +
                                mov.getString(mov.getColumnIndex(DataBaseHelper.DATE_UP)) +
                                "\n");
                    }
                    while (mov.moveToNext());
                }
                else{
                    bw.write("Este movimiento no tuvo entradas ni salidas y se realizó en: ,,,,,," + Constant.INSTANCE_LATITUDE + "," + Constant.INSTANCE_LONGITUDE + "\n");
                }

                bw.write("\n");

            }while(res.moveToNext());


        }


        bw.close();

        //(restablecemoslos numeros que estan ocupados los desocupamos
        // escribiendo un 0 en ESTADO_DEL_NUMERO
        // tambien eliminablos los datos en la tabla mensajesAdministracion
        // para que este disponible para llenarse con los nuevos mensajes del dia
        // de trabajo nuevo)
        dataBaseHelper.restablecerNumerosCuenta();
        dataBaseHelper.eliminarMensajes();


        Integer del = dataBaseHelper.eliminaTodosMovimientos();
        if(del >= 1){

            Integer bor = dataBaseHelper.eliminaTodosDetalles();
            Integer pay = dataBaseHelper.eliminaTodosPagos();

            Cursor bal = dataBaseHelper.dameMovimientos();

            Log.d("dbg-bal",String.valueOf(bal.getCount()));

            try {
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d("dbg-respuesta",String.valueOf(del));
        }
    }




//ORIGINAL DE LA PROGRAMACION DE ABRAHM YO LO MODIFIQUE PARA QUITAR EL CODIGO Y PARA UTILIZAR LOS NOMBRES DE LAS CONSTANTES COMO INDICES.
//    do{
//        bw.write("CUENTA,NOMBRE,CODIGO,GRADO,CLIENTES_ADMIN_DIAS_CREDITO,FECHA,CLIENTES_ADMIN_VENCIMIENTO,VENTA,HORA_VENTA,REGDIF,HORA_REGDIF,SALDO_PENDIENTE,HORA_SALDOPENDIENTE,OTROS,HORA_OTROS,NUEVO_SALDO,HORA_NUEVOSALDO,CLIENTES_ADMIN_PUNTOS_DISPONIBLES,HORA_PUNTOS,CLIENTES_ADMIN_REPORTE DE VISITA\n");
//        bw.write(res.getString(1) + "," + res.getString(2) + "," + res.getString(3) + "," +
//                res.getString(9) + "," + res.getString(10) + "," + res.getString(4) + "," +
//                res.getString(5) + "," + res.getString(6) + "," + res.getString(15) + "," +
//
//                res.getString(12) + "," +  res.getString(16) + "," + res.getString(13) + "," + res.getString(17) + "," + res.getString(14) + "," + res.getString(18) + "," +
//
//                res.getString(7) + "," + res.getString(19) + "," +
//                res.getString(8) + "," + res.getString(20) + "," + res.getString(11) + "\n");
//
//        bw.write("CANTIDAD,PRECIO,DISTRIBUIDOR,GANANCIA,INVENTARIO_CODIGO_PRODUCTO,TIPO_MOVIMIENTO,LATITUD,LONGITUD,FECHA\n");
//
//        Cursor mov = dbHelper.dameDetallesCompletos(res.getString(0));
//
//        if(mov.getCount() > 0) {
//
//            mov.moveToFirst();
//
//            do {
//                bw.write(mov.getString(2) + "," + mov.getString(3) + "," + mov.getString(4) + "," +
//                        mov.getString(5) + "," + mov.getString(6) + "," + mov.getString(7) + "," +
//                        mov.getString(8) + "," + mov.getString(9) + "," + mov.getString(11) + "\n");
//            }
//            while (mov.moveToNext());

    /********** ESTO HAY QUE CAMBIARLO ES PARA QUE IMPRIMA PERO HAY QUE HACERLO GLOBAL **********/

    protected void connect() {

        String s = Constant.INTANCE_PRINT_SEPARATOR;

        if(btsocket_bit == null){
            Log.e("ConnectSTS", "null");
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        }
        else{
            OutputStream opstream = null;
            try {
                opstream = btsocket_bit.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btoutputstream_bit = opstream;

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                btoutputstream_bit = btsocket_bit.getOutputStream();

                Cursor resMovimientosTicket = dbHelper.dameMovimientos();
                resMovimientosTicket.moveToFirst();

                Log.d("dbg-res-ticket", resMovimientosTicket.getString(2));

                int iCuenta     = 0;
                int iNombre     = 0;
                int iEntrada    = 0;
                int iSalida     = 0;
                int iPago       = 0;

                int piezasTotales   = 0;
                int importeCodigo   = 0;
                int importeTotal    = 0;

                String totalEntradasMovimiento  = null;
                String totalSalidasMovimiento   = null;

                String celdaCuenta  = "    ";
                String celdaNombre  = "     ";
                String celdaEntrada = "   ";
                String celdaSalida  = "   ";
                String celdaPago    = "     ";

                String printCuenta  = "";
                String printNombre  = "";
                String printEntrada = "";
                String printSalida  = "";
                String printPago    = "";


                resetPrint();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(c.INTANCE_PRINT_COMPANY);
                printNewLine();

                printPhoto();

                printNewLine();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                printText("Opeador: " + ConfiguracionesApp.getUsuarioIniciado(activity) + "\n");
                printText("Ruta: " + ConfiguracionesApp.getZonaVisitar1(activity) + "\n");
                printText("Fecha: " + utilidadesApp.dameFecha() + "\n");

                printUnicode();

                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                printText("MOVIMIENTOS\n");
                btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                resetPrint();

                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                resetPrint();
                printText("Cta  " + s + " Nomb. " + s + " Ent " + s + " Sal " + s + "  Pago");

                do{

                    totalEntradasMovimiento = dbHelper.dameMovimientosTotalesId(resMovimientosTicket.getString(0), "E");
                    totalSalidasMovimiento  = dbHelper.dameMovimientosTotalesId(resMovimientosTicket.getString(0), "S");

                    iCuenta     = resMovimientosTicket.getString(1).length();
                    iNombre     = resMovimientosTicket.getString(2).length();
                    iPago       = resMovimientosTicket.getString(3).length();
                    iEntrada    = totalEntradasMovimiento.length();
                    iSalida     = totalSalidasMovimiento.length();

                    iNombre = iNombre > celdaNombre.length() ? celdaNombre.length() : resMovimientosTicket.getString(2).length();

                    printCuenta = resMovimientosTicket.getString(1) + celdaCuenta.subSequence(0,celdaCuenta.length() - iCuenta);
                    printNombre = celdaNombre.subSequence(0,celdaNombre.length() - iNombre) + resMovimientosTicket.getString(2).substring(0,iNombre);
                    printPago   = celdaPago.subSequence(0,celdaPago.length() - iPago) + resMovimientosTicket.getString(3);

                    printEntrada= celdaEntrada.subSequence(0,celdaEntrada.length() - iEntrada) + totalEntradasMovimiento;
                    printSalida = celdaSalida.subSequence(0,celdaSalida.length() - iSalida) + totalSalidasMovimiento;

                    printText( printCuenta + " " + s + " " + printNombre + " " + s + " " + printEntrada + " " + s + " " + printSalida + " " + s + " " + printPago + "\n");

                }while(resMovimientosTicket.moveToNext());

                printNewLine();

                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                printText("Entradas: " + entradasTotales + "\n");
                printText("Salidas: " + salidasTotales + "\n");
                printText("Pagos: " + pagosTotales + "\n");

                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();

                btoutputstream_bit.flush();

                c.TMPMOV_TICKET++;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //print Title
    private void printTitle(String msg) {
        try {
            //Print config
            byte[] bb = new byte[]{0x1B,0x21,0x08};
            byte[] bb2 = new byte[]{0x1B,0x21,0x20};
            byte[] bb3 = new byte[]{0x1B,0x21,0x10};
            byte[] cc = new byte[]{0x1B,0x21,0x00};

            btoutputstream_bit.write(bb3);

            //set text into center
            btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
            btoutputstream_bit.write(msg.getBytes());
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print photo
    public void printPhoto() {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.logokaliopeticket);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                printText(command);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    //print unicode
    public void printUnicode(){
        try {
            btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //print new line
    private void printNewLine() {
        try {
            btoutputstream_bit.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void resetPrint() {
        try{
            btoutputstream_bit.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            btoutputstream_bit.write(PrinterCommands.FS_FONT_ALIGN);
            btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
            btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
            btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //print text
    private void printText(String msg) {
        try {
            // Print normal text
            btoutputstream_bit.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            btoutputstream_bit.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(btsocket_bit!= null){
                btoutputstream_bit.close();
                btsocket_bit.close();
                btsocket_bit = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket_bit = DeviceList.getSocket();
            if(btsocket_bit != null){
                    /*printText(message.getText().toString());*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        Intent m = new Intent(this, Trabajar.class);
        startActivity(m);
    }
}

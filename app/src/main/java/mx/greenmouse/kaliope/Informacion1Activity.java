package mx.greenmouse.kaliope;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class Informacion1Activity extends AppCompatActivity implements View.OnClickListener {

    Button imprimirB;
    Button exportarEmergencia;
    DataBaseHelper dbHelper = new DataBaseHelper(this);


    Activity activity;


    private static BluetoothSocket btsocket_bit;
    private static OutputStream btoutputstream_bit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion1);

        getSupportActionBar().hide();
        activity = this;


        imprimirB = (Button) findViewById(R.id.btnRegresar);
        exportarEmergencia = (Button) findViewById(R.id.exportarEmergenciaBi);

        imprimirB.setOnClickListener(this);
        exportarEmergencia.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegresar:
                connect();
                //onBackPressed();
                break;

            case R.id.exportarEmergenciaBi :
                try{
                    exportaMovimientos();
                }catch (IOException e){
                    e.printStackTrace();
                }
                break;

        }

    }

    @Override
    public void onBackPressed (){

        Intent intent = new Intent(this,MenuPrincipalActivity.class);
        startActivity(intent);
    }


    public void exportaMovimientos() throws IOException {

        Cursor res = dbHelper.dameMovimientosCompletos();
        res.moveToFirst();

        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(Constant.INSTANCE_PATH + "/mx.4103.klp/MOV_" + ConfiguracionesApp.getUsuarioIniciado(activity) + "_" + utilidadesApp.dameFecha() + "_" + ConfiguracionesApp.getZonaVisitar1(activity)+ ".txt"));



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

            Cursor mov = dbHelper.dameDetallesCompletos(res.getString(0));

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

        bw.close();
        Toast.makeText(this, "Los datos fueron grabados correctamente", Toast.LENGTH_SHORT).show();

        //(restablecemoslos numeros que estan ocupados los desocupamos
        // escribiendo un 0 en ESTADO_DEL_NUMERO
        // tambien eliminablos los datos en la tabla mensajesAdministracion
        // para que este disponible para llenarse con los nuevos mensajes del dia
        // de trabajo nuevo)
        dbHelper.restablecerNumerosCuenta();
        dbHelper.eliminarMensajes();


        Integer del = dbHelper.eliminaTodosMovimientos();
        if(del >= 1){

            Integer bor = dbHelper.eliminaTodosDetalles();
            Integer pay = dbHelper.eliminaTodosPagos();

            Cursor bal = dbHelper.dameMovimientos();

            Log.d("dbg-bal",String.valueOf(bal.getCount()));

            Intent i = new Intent(this, MenuPrincipalActivity.class);
            startActivity(i);
        }
        else{
            Log.d("dbg-respuesta",String.valueOf(del));
        }
    }






    /** Metodos para imprimir*/


    protected void connect() {


        if(btsocket_bit == null){
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

            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                btoutputstream_bit = btsocket_bit.getOutputStream();


                resetPrint();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(Constant.INTANCE_PRINT_COMPANY);
                printNewLine();

                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                printPhoto();

                printNewLine();
                //btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText("Numero de tarjeta:" + "\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printNewLine();
                printTitle( "4772-1430-0714-4799" + "\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);


                printNewLine();
                printNewLine();
                //btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText("Numero cuenta BBVA (Bancomer):" + "\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printNewLine();
                printTitle( "0103377695" + "\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);

                printNewLine();
                printNewLine();
                //btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText("Cuenta CLABE:" + "\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printNewLine();
                printTitle( "012426001033776956" + "\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);


                printNewLine();
                printNewLine();
                printNewLine();
                //btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                printText( "Estimado cliente con este numero\n" +
                        "de tarjeta y cuenta, podra hacer\n" +
                        "depocitos en cualquier OXXO\n" +
                        "o directo en ventanilla\n" +
                        "BBVA (BANCOMER).\n\n" +
                        "Con la CLABE podra hacer\n" +
                        "transferencias interbancarias\n\n"+
                        "Una vez hecho el depocito\n" +
                        "guarde el comprobate para\n" +
                        "entregarcelo a su agente\n" +
                        "de ventas, o comuniquese\n" +
                        "directo a Kaliope para\n" +
                        "informar sobre el depocito\n\n" +
                        "Estamos para servirle en el\n" +
                        "telefono de oficina:\n" +
                        "           712-159-07-29");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);



                resetPrint();

                //printUnicode();


                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();


                btoutputstream_bit.flush();

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
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.logokaliopeticketjustificacionizq);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    public void onDestroy() {
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


}



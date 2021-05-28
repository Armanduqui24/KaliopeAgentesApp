package mx.greenmouse.kaliope;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
import static mx.greenmouse.kaliope.Constant.THIRD_COLUMN;

public class InventarioActivity extends AppCompatActivity implements View.OnClickListener {

    DataBaseHelper dbHelper = new DataBaseHelper(this);
    Constant c = new Constant();

    Button printbtn;
    //Button exportar;
    Button mostrarDetalleB;
    TextView txExistencias;
    TextView txImporte;
    TextView tvVersion;
    ListView lview;

    File folder;
    File inventario;

    private static BluetoothSocket btsocket;
    private static OutputStream btoutputstream;

    VariablePassword variablePassword;
    Activity activity;




    private ArrayList<HashMap> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        activity = this;
        printbtn = (Button)findViewById(R.id.btImprimir);
        //exportar = (Button)findViewById(R.id.btExportar);
        mostrarDetalleB = (Button)findViewById(R.id.btMostrarDetalle);
        tvVersion = (TextView) findViewById(R.id.txtVersionInv);
        lview = (ListView) findViewById(R.id.lvInventario);


        txExistencias = (TextView)findViewById(R.id.txtExistencias);
        txImporte = (TextView)findViewById(R.id.txtImporte);

        getSupportActionBar().hide();

        if(validaInventario()){
            listaInventario(false);
        }
        else{
            utilidadesApp.dialogoError(this,"Error al listar el inventario.");
        }

        printbtn.setOnClickListener(this);
        //exportar.setOnClickListener(this);
        mostrarDetalleB.setOnClickListener(this);

        try {
            if(btsocket!= null){
                btoutputstream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        variablePassword = new VariablePassword();
        printbtn.setVisibility(View.INVISIBLE);


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btImprimir:
                connect();
                break;
            /*case R.id.btExportar:
                dialogoConfirmacionExportarInventario(this);
                break;*/

            case R.id.btMostrarDetalle:

                if(!variablePassword.getValidacion()){
                    //si la varaible validacion del objeto variablePassword es falsa entonces llamamos al cuadro de dialogo, como el objeto cada que se crea
                    //su constructor inicializa a false la validacion, la primera ves entrara aqui.
                    variablePassword.alertDialogValidaPassword(this,this,"Mostrar el inventario es necesario ingresar el codigo." +
                            " Por favor llame a sistemas Kaliope ");

                    //una ves que aparese el dialogo, si el usuario valida correctamente el codigo
                    //la variable validacion dentro de la clase VariablePassword
                    //cambia a True por lo tanto al presionar por segunda vez el boton pulseras
                    //ahora entrara directo al catalogo
                }else{
                    listaInventario(true);
                    printbtn.setVisibility(View.VISIBLE);
                    mostrarDetalleB.setVisibility(View.INVISIBLE);
                }

                break;
        }
    }

    //creo el metodo aqui para que se pueda llamar desde multiples areas de la app, solicito un Database
    //para que puedan funcionar los metodos de eliminar el inventario etc, porque si no marca que
    //nullpointerException
    public void llenarInventarioDesdeJsonArray (JSONArray inventarioJsonArray,DataBaseHelper dataBaseHelper, Activity activity){
        dataBaseHelper.inventario_eliminaInventario();

        for (int i = 0; i<inventarioJsonArray.length() ; i++) {
            try {
                String codigo = inventarioJsonArray.getJSONObject(i).getString("codigo");
                String precio = inventarioJsonArray.getJSONObject(i).getString("precio");
                String existencia = inventarioJsonArray.getJSONObject(i).getString("existencia");
                String vendedora = inventarioJsonArray.getJSONObject(i).getString("vendedora");
                String socia = inventarioJsonArray.getJSONObject(i).getString("socia");
                String empresaria = inventarioJsonArray.getJSONObject(i).getString("empresaria");




                ContentValues contentValues = new ContentValues(5);
                contentValues.put(DataBaseHelper.INVENTARIO_CODIGO_PRODUCTO, codigo);
                contentValues.put(DataBaseHelper.INVENTARIO_PRECIO_VENTA_PRODUCTO, precio);
                contentValues.put(DataBaseHelper.INVENTARIO_PRECIO_VENDEDORA, vendedora);
                contentValues.put(DataBaseHelper.INVENTARIO_PRECIO_SOCIA, socia);
                contentValues.put(DataBaseHelper.INVENTARIO_PRECIO_EMPRESARIA,empresaria);
                contentValues.put(DataBaseHelper.INVENTARIO_EXISTENCIAS, existencia);

                dataBaseHelper.inventario_insertarInventario(contentValues);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //fuera del for recuperamos la version de solo un producto
        int version = 0;
        try {
            version = Integer.valueOf(inventarioJsonArray.getJSONObject(0).getString("version"));
            ConfiguracionesApp.setVersionInventario(activity,version);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public String enviarInventarioPorJsonArray (DataBaseHelper dataBaseHelper){
        JSONArray jsonArrayInventario = new JSONArray();

        Cursor cursorInventario = dataBaseHelper.inventario_dameInventarioConExistencias();
        if (cursorInventario.getCount() > 0){
            cursorInventario.moveToFirst();

            do{
                try {
                    JSONObject producto = new JSONObject();
                    producto.put("codigo", cursorInventario.getString(cursorInventario.getColumnIndex(DataBaseHelper.INVENTARIO_CODIGO_PRODUCTO)));
                    //producto.put("precio", cursorInventario.getString(cursorInventario.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENTA_PRODUCTO)));
                    //producto.put("vendedora", cursorInventario.getString(cursorInventario.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENDEDORA)));
                    //producto.put("socia", cursorInventario.getString(cursorInventario.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_SOCIA)));
                    //producto.put("empresaria", cursorInventario.getString(cursorInventario.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_EMPRESARIA)));
                    producto.put("existencias", cursorInventario.getString(cursorInventario.getColumnIndex(DataBaseHelper.INVENTARIO_EXISTENCIAS)));
                    jsonArrayInventario.put(producto);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }while (cursorInventario.moveToNext());

        }

        //dataBaseHelper.inventario_eliminaInventario();
        Log.d("jsonInventario",jsonArrayInventario.toString());
        return jsonArrayInventario.toString();
    }

    public boolean validaInventario(){

        Cursor res = dbHelper.inventario_dameInvenarioCompleto();
        Log.d("dbg-inv",String.valueOf(res.getCount()));

        if(res.getCount() == 0){

            folder     = new File(Constant.INSTANCE_PATH, "/mx.4103.klp");
            inventario = new File(folder + "/inventario.txt");

            try {

                FileReader txtInventario = new FileReader(inventario);
                BufferedReader buffer = new BufferedReader(txtInventario);

                String line;

                while ((line = buffer.readLine()) != null) {
                    String[] colums = line.split(",");
                    Cursor res1 = dbHelper.inventario_dameInvenarioCompleto();
                    //VersionNameLuisda6.6
                    if (colums.length != 6) {
                        Log.d("CSVParser", "Skipping Bad CSV Row");
                        continue;
                    }

                    ContentValues cv = new ContentValues(5);
                    //se modifica el tamaño del inventario ya que ahora alvergara precio de socia y emrpoesaria
                    cv.put(dbHelper.INVENTARIO_CODIGO_PRODUCTO, colums[0]);
                    cv.put(dbHelper.INVENTARIO_PRECIO_VENTA_PRODUCTO, colums[1]);
                    cv.put(dbHelper.INVENTARIO_PRECIO_VENDEDORA, colums[2]);
                    cv.put(dbHelper.INVENTARIO_PRECIO_SOCIA, colums[3]);
                    cv.put(dbHelper.INVENTARIO_PRECIO_EMPRESARIA, colums[4]);
                    cv.put(dbHelper.INVENTARIO_EXISTENCIAS, colums[5]);

                    Log.d("COUNT",String.valueOf(res1.getCount()));

                    if (dbHelper.inventario_insertarInventario(cv) == -1) {
                        return false;
                    }
                }

                inventario.delete();
                c.INSTANCE_DB = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void dialogoConfirmacionExportarInventario(final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final Integer[] r = new Integer[1];

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("¡Cuidado!");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("¿Realmente desea exportar el inventario?")
                .setPositiveButton("SI, EXPORTAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            exportaDB(dbHelper, activity);
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

    public void listaInventario(boolean mostrarExistencias){


        list = new ArrayList<HashMap>();

        Integer iExistencias = 0;
        Integer iImporte = 0;

        ListViewAdapter adapter = new ListViewAdapter(this, list, mostrarExistencias);
        lview.setAdapter(adapter);

        Cursor res = dbHelper.inventario_dameInvenarioCompleto();
        res.moveToFirst();


        do
        {
            HashMap temp1 = new HashMap();
            temp1.put(FIRST_COLUMN,res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENDEDORA)));
            temp1.put(SECOND_COLUMN, res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_SOCIA)));
            temp1.put(THIRD_COLUMN, res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_EMPRESARIA)));
            temp1.put(FOURTH_COLUMN, res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_CODIGO_PRODUCTO)));
            temp1.put(FIVE_COLUMN, res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_EXISTENCIAS)));
            list.add(temp1);

            iExistencias = iExistencias + (Integer.parseInt(res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_EXISTENCIAS))));
            iImporte    = iImporte + (Integer.parseInt(res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENDEDORA))) * Integer.parseInt(res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_EXISTENCIAS))));


            Log.e("Existencias", iImporte.toString());

        }while(res.moveToNext());

        c.INSTANCE_STOCK    = iExistencias.toString();
        c.INSTANCE_AMOUNT   = iImporte.toString();


        txExistencias.setText(c.INSTANCE_STOCK);
        txImporte.setText("$ " + c.INSTANCE_AMOUNT + ".00");
        tvVersion.setText(String.valueOf(ConfiguracionesApp.getVersionInventario(activity)));
    }

    /********** METODO PARA EXPORTAR LA BASE DE DATOS Y VACIARLA **********/

    //la excepcion IOException se produce por un error de entrada o salida, por ejemplo cuando
    //leemos desde la consola o leemos un archivo fichero
    public void exportaDB(DataBaseHelper dataBaseHelper, Activity activity) throws IOException {

        Cursor res = dataBaseHelper.inventario_dameInvenarioCompleto();
        res.moveToFirst();

        Constant.INSTANCE_PATH = String.valueOf(Environment.getExternalStorageDirectory());

        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(Constant.INSTANCE_PATH + "/mx.4103.klp/INV_" + ConfiguracionesApp.getUsuarioIniciado(activity) + "_" + ConfiguracionesApp.getZonaVisitar1(activity) + " " + utilidadesApp.dameFechaConSegundo() + ".txt"));
        bw.write("CÓDIGO,PRECIO,UNO,SOCIA,EMPRESARIA,EXCISTENCIAS\n");

        if (res.getCount()>0){
            do{
                bw.write(res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_CODIGO_PRODUCTO)) + "," + res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENTA_PRODUCTO)) + ","
                        + res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENDEDORA)) + "," + res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_SOCIA)) + ","
                        + res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_EMPRESARIA)) + "," + res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_EXISTENCIAS)) +"\n");
            }while(res.moveToNext());
        }


        bw.close();

        Integer del = dataBaseHelper.inventario_eliminaInventario();
        if(del >= 1){
            c.INSTANCE_DB = false;
            try {
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }



    /********** ESTO HAY QUE CAMBIARLO ES PARA QUE IMPRIMA PERO HAY QUE HACERLO GLOBAL **********/

        protected void connect() {

            String s = Constant.INTANCE_PRINT_SEPARATOR;

            if(btsocket == null){
                Log.e("ConnectSTS", "null");
                Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
                this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
            }
            else{
                OutputStream opstream = null;
                try {
                    opstream = btsocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                btoutputstream = opstream;

                //print command
                try {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    btoutputstream = btsocket.getOutputStream();

                    Cursor res = dbHelper.inventario_dameInventarioConExistencias();
                    res.moveToFirst();

                    int iCodigo;
                    int iPrecio = 0;
                    int iUno = 0;
                    int iExistencias = 0;

                    String celdaCodigo      = "    ";
                    String celdaPrecio      = "      ";
                    String celdaUno         = "     ";
                    String celdaExistencias = "        ";

                    String printCodigo      = "";
                    String printPrecio      = "";
                    String printUno         = "";
                    String printExistencias = "";

                    resetPrint();
                    btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText(Constant.INTANCE_PRINT_COMPANY);
                    printNewLine();

                    //printPhoto();

                    printNewLine();
                    btoutputstream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    printText("Operador: " + ConfiguracionesApp.getUsuarioIniciado(activity)  + "\n");
                    printText("Ruta: " + ConfiguracionesApp.getZonaVisitar1(activity) + "\n");
                    printText("Fecha: " + utilidadesApp.dameFecha() + "\n");

                    printUnicode();

                    btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    btoutputstream.write(PrinterCommands.ESC_SETTING_BOLD);
                    printText("INVENTARIO\n");
                    btoutputstream.write(PrinterCommands.ESC_CANCEL_BOLD);

                    resetPrint();

                    btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);

                    resetPrint();
                    printText("Cod. " + s + " Precio " + s + "  Uno  " + s + "   Exist.");

                    if (res.getCount()>0){
                        do{

                            iCodigo = res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_CODIGO_PRODUCTO)).length();
                            iPrecio = res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENTA_PRODUCTO)).length();
                            iUno    = res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENDEDORA)).length();
                            iExistencias = res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_EXISTENCIAS)).length();

                            printCodigo = celdaCodigo.subSequence(0,celdaCodigo.length() - iCodigo) + res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_CODIGO_PRODUCTO));
                            printPrecio = celdaPrecio.subSequence(0,celdaPrecio.length() - iPrecio) + res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENTA_PRODUCTO));
                            printUno    = celdaUno.subSequence(0,celdaUno.length() - iUno) + res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENDEDORA));
                            printExistencias = celdaExistencias.subSequence(0,celdaExistencias.length() - iExistencias) + res.getString(res.getColumnIndex(DataBaseHelper.INVENTARIO_EXISTENCIAS));


                            printText( printCodigo + " " + s + " " + printPrecio + " " + s + " " + printUno + " " + s + " " + printExistencias + "\n");



                        }while(res.moveToNext());

                    }else{
                        printText("Sin existencias!!!" + "\n");
                    }





                    printNewLine();
                    btoutputstream.write(PrinterCommands.ESC_SETTING_BOLD);
                    printText("Existencias: " + c.INSTANCE_STOCK + "\n");
                    printText("TOTAL: $ " + c.INSTANCE_AMOUNT + ".00");

                    printNewLine();
                    printNewLine();
                    printNewLine();
                    printNewLine();


                    btoutputstream.flush();
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

                btoutputstream.write(bb3);

                //set text into center
                btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);
                btoutputstream.write(msg.getBytes());
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
                btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);
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
                btoutputstream.write(PrinterCommands.FEED_LINE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public static void resetPrint() {
            try{

                btoutputstream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
                btoutputstream.write(PrinterCommands.FS_FONT_ALIGN);
                btoutputstream.write(PrinterCommands.ESC_ALIGN_LEFT);
                btoutputstream.write(PrinterCommands.ESC_CANCEL_BOLD);
                btoutputstream.write(PrinterCommands.SELECT_FONT_A);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //print text
        private void printText(String msg) {
            try {
                // Print normal text
                btoutputstream.write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //print byte[]
        private void printText(byte[] msg) {
            try {
                // Print normal text
                btoutputstream.write(msg);
                printNewLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            try {
                if(btsocket!= null){
                    btoutputstream.close();
                    btsocket.close();
                    btsocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                btsocket = DeviceList.getSocket();
                if(btsocket != null){
                    /*printText(message.getText().toString());*/
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    @Override
    public void onBackPressed(){

        Intent m = new Intent(this, MenuPrincipalActivity.class);
        startActivity(m);
    }


}



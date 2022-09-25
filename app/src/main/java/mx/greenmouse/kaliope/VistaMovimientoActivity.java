package mx.greenmouse.kaliope;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

public class VistaMovimientoActivity extends AppCompatActivity implements View.OnClickListener {

    String cuentaCliente, movimientoId;
    String idMovimiento;


    Integer sumaTotal;

    String  strCuentaVM, strNombreVM, strCodigoVM, strDiasVencimientoVM, strGradoVM, strFechaVM, strVencimientoVM,
            strTotalPzSalidasVM, strTotalSalidasVM, strTotalPzEntradasVM, strTotalEntradasVM, strPagoVM, strSaldoVM, strPuntosVM,
            strSaldoPendienteVM, strDiferenciaVM, strOtroVM;
    String parametrosCliente; //guardaremos los parametros donde viene credito grado y dias divivido por comas, lo dividiremos con la funcion split y se lo asignaremos a cada variable

    TextView txtCuentaVM, txtNombreVM, txtCodigoVM, txtDiasVencimientoVM, txtGradoVM, txtFechaVM, txtVencimientoVM,
             txtTotalPzSalidasVM, txtTotalSalidasVM, txtTotalPzEntradasVM, txtTotalEntradasVM, txtPagoVM, txtSaldoVM, txtPuntosVM,
             txtImporteVentaVM, txtImporteDiferenciaVM, txtImporteSaldoPentienteVM, txtImporteOtroVM;

    TextView lblImportePago, lblImporteDiferencia, lblImporteSaldoPendiente, lblImporteOtro;

    Button btnImprimirMovimientoVM;

    DataBaseHelper dbHelper = new DataBaseHelper(this);
    Constant c = new Constant();
    Activity activity;

    public static ListView lviewSVM;
    private ArrayList<HashMap> listSVM;

    public static ListView lviewEVM;
    private ArrayList<HashMap> listEVM;

    private static BluetoothSocket btsocket_bit;
    private static OutputStream btoutputstream_bit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_movimiento);
        getSupportActionBar().hide();
        activity = this;

        Log.i("envistamovimiento","control 1");

        lblImportePago = (TextView) findViewById(R.id.lblImporteVenta);
        lblImporteDiferencia = (TextView) findViewById(R.id.lblImporteDiferencia);
        lblImporteSaldoPendiente = (TextView) findViewById(R.id.lblImporteSaldoPendiente);
        lblImporteOtro = (TextView) findViewById(R.id.lblImporteOtro);

        txtCuentaVM = (TextView) findViewById(R.id.txtCuentaVM);
        txtNombreVM = (TextView) findViewById(R.id.txtNombreVM);
        txtCodigoVM = (TextView) findViewById(R.id.txtCodigoVM);
        txtDiasVencimientoVM = (TextView) findViewById(R.id.txtDiasVM);
        txtGradoVM = (TextView) findViewById(R.id.txtGradoVM);
        txtFechaVM = (TextView) findViewById(R.id.txtFechaVM);
        txtVencimientoVM = (TextView) findViewById(R.id.txtVencimientoVM);
        txtTotalPzSalidasVM = (TextView) findViewById(R.id.txtTotalSalidasPzVM);
        txtTotalSalidasVM = (TextView) findViewById(R.id.txtTotalSalidasVM);
        txtTotalPzEntradasVM = (TextView) findViewById(R.id.txtTotalEntradasPzVM);
        txtTotalEntradasVM = (TextView) findViewById(R.id.txtTotalEntradasVM);
        txtPagoVM = (TextView) findViewById(R.id.txtPagoVM);

        txtImporteVentaVM = (TextView) findViewById(R.id.txtImporteVentaVM);
        txtImporteDiferenciaVM = (TextView) findViewById(R.id.txtImporteDiferenciaVM);
        txtImporteSaldoPentienteVM = (TextView) findViewById(R.id.txtImporteSaldoPendienteVM);
        txtImporteOtroVM = (TextView) findViewById(R.id.txtImporteOtroVM);

        txtSaldoVM = (TextView) findViewById(R.id.txtSaldoVM);
        txtPuntosVM = (TextView) findViewById(R.id.txtPuntosVM);

        movimientoId = getIntent().getExtras().getString("idMovimiento");
        cuentaCliente = getIntent().getExtras().getString("cuentaCliente");
        Log.i("movimiento id",movimientoId);
        Log.i("cuentacliente",cuentaCliente);

        Cursor res = dbHelper.dameCabeceraMovimiento(movimientoId, cuentaCliente);
        res.moveToFirst();
        Log.i("envistamovimiento","control 2");
        Log.i("registros: " ,String.valueOf(res.getCount()));


        idMovimiento = res.getString(res.getColumnIndex(dbHelper.KEY_ID));
        Log.i("envistamovimiento","control 2.1");
        strCuentaVM = res.getString(res.getColumnIndex(dbHelper.MOVEMENTS_ACCOUNT));
        strNombreVM = res.getString(res.getColumnIndex(dbHelper.MOVEMENTS_NAME));
        parametrosCliente = res.getString(res.getColumnIndex(DataBaseHelper.MOVEMENTS_CREDIT_CODE));

        //AHORA EN PARAMETROS CLIENTE TENEMOS EL GRADO,DIAS,LIMITE EN ESE ORDEN DIVIDIDO POR COMAS
        //LOS SACAREMOS CON SPLIT Y LOS ASIGNAREMOS A SU VARIABLE CORRESPONDIENTE
        String [] arrayAuxiliarParametrosCliente = parametrosCliente.split(",");
        strGradoVM = arrayAuxiliarParametrosCliente[0];
        strDiasVencimientoVM = arrayAuxiliarParametrosCliente[1];
        strCodigoVM = arrayAuxiliarParametrosCliente[2];

        //strCodigoVM = res.getString(res.getColumnIndex(dbHelper.MOVEMENTS_CREDIT_CODE));
        Log.i("envistamovimiento","control 2.2");

        //strDiasVencimientoVM = res.getString(res.getColumnIndex(dbHelper.CREDIT_DAYS));
        //strGradoVM = res.getString(res.getColumnIndex(dbHelper.CUSTOMER_GRADE));

        Log.i("envistamovimiento","control 2.3");

        strFechaVM = res.getString(res.getColumnIndex(dbHelper.DATE_UP));
        strVencimientoVM = res.getString(res.getColumnIndex(dbHelper.MOVEMENTS_EXPIRATION_DATE));
        Log.i("envistamovimiento","control 2.4");

        strPagoVM = res.getString(res.getColumnIndex(dbHelper.PAGOS_PAGO));
        strDiferenciaVM = res.getString(res.getColumnIndex(dbHelper.PAGOS_DIFERENCIA));
        strSaldoPendienteVM = res.getString(res.getColumnIndex(dbHelper.PAGOS_SALDO_PENDIENTE));
        strOtroVM = res.getString(res.getColumnIndex(dbHelper.PAGOS_OTRO));
        strSaldoVM = res.getString(res.getColumnIndex(dbHelper.PAGOS_ADEUDO));
        strPuntosVM = res.getString(res.getColumnIndex(dbHelper.PAGOS_PUNTOS));

        txtCuentaVM.setText(strCuentaVM);
        txtNombreVM.setText(strNombreVM);
        txtCodigoVM.setText(strCodigoVM);

        txtDiasVencimientoVM.setText(strDiasVencimientoVM);
        txtGradoVM.setText(strGradoVM);

        txtFechaVM.setText(strFechaVM);
        txtVencimientoVM.setText(strVencimientoVM);

        Log.i("envistamovimiento","control 3");

        txtImporteVentaVM.setText("$ " + strPagoVM);
        txtImporteDiferenciaVM.setText("$ " + strDiferenciaVM);
        txtImporteSaldoPentienteVM.setText("$ " + strSaldoPendienteVM);
        txtImporteOtroVM.setText("$ " + strOtroVM);


        sumaTotal = Integer.parseInt(strPagoVM) + Integer.parseInt(strDiferenciaVM) + Integer.parseInt(strSaldoPendienteVM) + Integer.parseInt(strOtroVM);

        txtPagoVM.setText("$ " + String.valueOf(sumaTotal));
        txtSaldoVM.setText("$ " + strSaldoVM);
        txtPuntosVM.setText("$ " + strPuntosVM);

        if(strPagoVM.equals("0")){
            lblImportePago.setVisibility(View.GONE);
            txtImporteVentaVM.setVisibility(View.GONE);
        }
        if(strDiferenciaVM.equals("0")){
            lblImporteDiferencia.setVisibility(View.GONE);
            txtImporteDiferenciaVM.setVisibility(View.GONE);
        }
        if(strSaldoPendienteVM.equals("0")){
            lblImporteSaldoPendiente.setVisibility(View.GONE);
            txtImporteSaldoPentienteVM.setVisibility(View.GONE);
        }
        if(strOtroVM.equals("0")){
            lblImporteOtro.setVisibility(View.GONE);
            txtImporteOtroVM.setVisibility(View.GONE);
        }

        lviewSVM = (ListView) findViewById(R.id.lvSalidasVM);
        lviewEVM = (ListView) findViewById(R.id.lvEntradasVM);
        Log.i("envistamovimiento","control 5");

        listaSalidas();
        listaEntradas();

        dameTotalesEntrada();
        dameTotalesSalida();

        txtTotalPzSalidasVM.setText(strTotalPzSalidasVM);
        txtTotalSalidasVM.setText(strTotalSalidasVM);

        txtTotalPzEntradasVM.setText(strTotalPzEntradasVM);
        txtTotalEntradasVM.setText(strTotalEntradasVM);

        btnImprimirMovimientoVM = (Button) findViewById(R.id.btnImprimeVistaMovimiento);
        btnImprimirMovimientoVM.setOnClickListener(this);

        Log.i("envistamovimiento","control 6");


    }

    public void listaSalidas(){

        listSVM = new ArrayList<HashMap>();

        ListViewAdapterSix adapter = new ListViewAdapterSix(this, listSVM);
        lviewSVM.setAdapter(adapter);

        HashMap temp = new HashMap();
        temp.put(FIRST_COLUMN,"CANT.");
        temp.put(SECOND_COLUMN, "PREC.");
        temp.put(THIRD_COLUMN, "DIST.");
        temp.put(FOURTH_COLUMN, "GAN.");
        temp.put(FIVE_COLUMN, "CÓD.");
        temp.put(SIX_COLUMN, "HORA");
        listSVM.add(temp);

        Cursor resSalidasVM = dbHelper.dameSalidas(idMovimiento);

        if(resSalidasVM.getCount() > 0) {

            resSalidasVM.moveToFirst();

            do {
                HashMap temp1 = new HashMap();
                temp1.put(FIRST_COLUMN, resSalidasVM.getString(2));
                temp1.put(SECOND_COLUMN, resSalidasVM.getString(3));
                temp1.put(THIRD_COLUMN, resSalidasVM.getString(4));
                temp1.put(FOURTH_COLUMN, resSalidasVM.getString(5));
                temp1.put(FIVE_COLUMN, resSalidasVM.getString(6));
                temp1.put(SIX_COLUMN, resSalidasVM.getString(11));
                listSVM.add(temp1);

            } while (resSalidasVM.moveToNext());
        }


        setListViewHeightBasedOnChildren(lviewSVM);

    }

    public void listaEntradas(){

        listEVM = new ArrayList<HashMap>();

        ListViewAdapterSix adapter = new ListViewAdapterSix(this, listEVM);
        lviewEVM.setAdapter(adapter);

        HashMap temp = new HashMap();
        temp.put(FIRST_COLUMN,"CANT.");
        temp.put(SECOND_COLUMN, "PREC.");
        temp.put(THIRD_COLUMN, "DIST.");
        temp.put(FOURTH_COLUMN, "GAN.");
        temp.put(FIVE_COLUMN, "CÓD.");
        temp.put(SIX_COLUMN, "HORA");
        listEVM.add(temp);

        Cursor res = dbHelper.detalles_dameEntradas(idMovimiento);

        if(res.getCount()>0) {

            res.moveToFirst();
            do {
                HashMap temp1 = new HashMap();
                temp1.put(FIRST_COLUMN, res.getString(2));
                temp1.put(SECOND_COLUMN, res.getString(3));
                temp1.put(THIRD_COLUMN, res.getString(4));
                temp1.put(FOURTH_COLUMN, res.getString(5));
                temp1.put(FIVE_COLUMN, res.getString(6));
                temp1.put(SIX_COLUMN, res.getString(11));
                listEVM.add(temp1);

            } while (res.moveToNext());

        }

        setListViewHeightBasedOnChildren(lviewEVM);

    }

    public void dameTotalesEntrada(){

        Cursor res = dbHelper.detalles_dameEntradas(idMovimiento);

        int p = 0;
        int i = 0;

        if(res.getCount() > 0){

            res.moveToNext();

            do{

                int rowCantidad = Integer.parseInt(res.getString(res.getColumnIndex(dbHelper.DETALLES_CANTIDAD)));
                int rowCosto    = Integer.parseInt(res.getString(res.getColumnIndex(dbHelper.DETALLES_PRECIO_DISTRIBUCION)));

                int rowTotal    = rowCosto * rowCantidad;

                i = i + rowCantidad;
                p = p + rowTotal;
            }
            while(res.moveToNext());

        }


        strTotalPzEntradasVM = String.valueOf(i);
        strTotalEntradasVM = "$" + String.valueOf(p);

    }

    public void dameTotalesSalida(){

        Cursor res = dbHelper.dameSalidas(idMovimiento);

        int p = 0;
        int i = 0;

        if(res.getCount() > 0){

            res.moveToNext();

            do{

                int rowCantidad = Integer.parseInt(res.getString(res.getColumnIndex(dbHelper.DETALLES_CANTIDAD)));
                int rowCosto    = Integer.parseInt(res.getString(res.getColumnIndex(dbHelper.DETALLES_PRECIO_DISTRIBUCION)));

                int rowTotal    = rowCosto * rowCantidad;

                i = i + rowCantidad;
                p = p + rowTotal;
            }
            while(res.moveToNext());

        }

        strTotalPzSalidasVM = String.valueOf(i);
        strTotalSalidasVM = "$ " + String.valueOf(p);

    }

    //https://es.switch-case.com/53695591
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnImprimeVistaMovimiento:

                connect();

                break;
        }
    }

    /********** ESTO HAY QUE CAMBIARLO ES PARA QUE IMPRIMA PERO HAY QUE HACERLO GLOBAL **********/

    protected void connect() {

        String s = Constant.INTANCE_PRINT_SEPARATOR;

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

                Cursor resSalidas = dbHelper.dameSalidas(idMovimiento);

                int iCantidad       = 0;
                int iPrecio         = 0;
                int iDistribuidor   = 0;
                int iGanancia       = 0;

                int iCantidadDev    = 0;
                int iPrecioDev      = 0;
                int iDistribucionDev= 0;

                int piezasTotales   = 0;
                int importeCodigo   = 0;
                int importeTotal    = 0;

                int piezasTotalesDev    = 0;
                int importeCodigoDev    = 0;
                int importeTotalDev     = 0;

                String celdaCantidad    = "     ";
                String celdaPrecio      = "      ";
                String celdaDistribucion= "     ";
                String celdaGanancia    = "       ";

                String celdaCantidadDev     = "        ";
                String celdaPrecioDev       = "      ";
                String celdaDistribucionDev = "            ";

                String printCantidad    = "";
                String printPrecio      = "";
                String printDistribuidor= "";
                String printGanancia    = "";

                String printCantidadDev     = "";
                String printPrecioDev       = "";
                String printDistribuidorDev = "";

                resetPrint();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(c.INTANCE_PRINT_COMPANY);
                printNewLine();

                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                printPhoto();

                printNewLine();
                //btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                printText("Cuenta: " + strCuentaVM + "\n");
                printText("Nombre: " + strNombreVM + "\n");
                printText("Codigo: " + strCodigoVM + "\n");
                printText("Dias de credito:" + strDiasVencimientoVM + "\n");
                printText("Grado: " + strGradoVM + "\n");
                printText("Fecha: " + strFechaVM.substring(0, strFechaVM.indexOf(" ")) + "\n");
                printText("Vencimiento: " + strVencimientoVM + "\n");
                printText(ConfiguracionesApp.getUsuarioIniciado(activity) + " " + ConfiguracionesApp.getZonaVisitar1(activity) + " " + utilidadesApp.dameFecha() + "\n");

                printUnicode();

                if(resSalidas.getCount()>0) {
                    resSalidas.moveToFirst();

                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                    printText("Entrega de Mercancia\n");
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    resetPrint();
                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText("(Salida)\n");
                    resetPrint();
                    printText("Cant. " + s + " Precio " + s + " Dist. " + s + " Gananc.");

                    do {

                        iCantidad = resSalidas.getString(2).length();
                        iPrecio = resSalidas.getString(3).length();
                        iDistribuidor = resSalidas.getString(4).length();
                        iGanancia = resSalidas.getString(5).length();


                        printCantidad = resSalidas.getString(2) + celdaCantidad.subSequence(0, celdaCantidad.length() - iCantidad);
                        printPrecio = celdaPrecio.subSequence(0, celdaPrecio.length() - iPrecio) + resSalidas.getString(3);
                        printDistribuidor = celdaDistribucion.subSequence(0, celdaDistribucion.length() - iDistribuidor) + resSalidas.getString(4);
                        printGanancia = celdaGanancia.subSequence(0, celdaGanancia.length() - iGanancia) + resSalidas.getString(5);

                        printText(printCantidad + " " + s + " " + printPrecio + " " + s + " " + printDistribuidor + " " + s + " " + printGanancia + "\n");


                        importeCodigo = Integer.parseInt(resSalidas.getString(2)) * Integer.parseInt(resSalidas.getString(4));

                        piezasTotales = piezasTotales + Integer.parseInt(resSalidas.getString(2));
                        importeTotal = importeTotal + importeCodigo;

                    } while (resSalidas.moveToNext());


                    printNewLine();
                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                    printText("Total pz: " + strTotalPzSalidasVM + "\n");
                    printText("TOTAL: " + strTotalSalidasVM + "\n");
                }

                printNewLine();

                Cursor resEntradas = dbHelper.detalles_dameEntradas(idMovimiento);

                if(resEntradas.getCount()>0) {

                    resEntradas.moveToFirst();

                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText("Devolucion\n");
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    resetPrint();
                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText("(Entrada)\n");
                    resetPrint();
                    printText("Cantidad " + s + " Precio " + s + " Distribucion");


                    do {

                        iCantidadDev = resEntradas.getString(2).length();
                        iPrecioDev = resEntradas.getString(3).length();
                        iDistribucionDev = resEntradas.getString(4).length();


                        printCantidadDev = resEntradas.getString(2) + celdaCantidadDev.subSequence(0, celdaCantidadDev.length() - iCantidadDev);
                        printPrecioDev = celdaPrecioDev.subSequence(0, celdaPrecioDev.length() - iPrecioDev) + resEntradas.getString(3);
                        printDistribuidorDev = celdaDistribucionDev.subSequence(0, celdaDistribucionDev.length() - iDistribucionDev) + resEntradas.getString(4);

                        printText(printCantidadDev + " " + s + " " + printPrecioDev + " " + s + " " + printDistribuidorDev + "\n");

                        importeCodigoDev = Integer.parseInt(resEntradas.getString(2)) * Integer.parseInt(resEntradas.getString(4));

                        piezasTotalesDev = piezasTotalesDev + Integer.parseInt(resEntradas.getString(2));
                        importeTotalDev = importeTotalDev + importeCodigoDev;

                    } while (resEntradas.moveToNext());

                    printNewLine();
                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                    printText("Total pz: " + strTotalPzEntradasVM + "\n");
                    printText("TOTAL: " + strTotalEntradasVM + "\n");
                }

                printNewLine();

                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText("Pagos\n");
                btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);

                resetPrint();
                printNewLine();

                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                /*btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);*/

                if(!strPagoVM.equals("0")){
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    printText("$ " + strPagoVM);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText(" Venta\n");
                }

                if(!strDiferenciaVM.equals("0")){
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    printText("$ " + strDiferenciaVM);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText(" difReg\n");
                }

                if(!strSaldoPendienteVM.equals("0")){
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    printText("$ " + strSaldoPendienteVM);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText(" Saldo Pendiente\n");
                }

                if(!strOtroVM.equals("0")){
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    printText("$ " + strOtroVM);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText(" Otros\n");
                }

                printNewLine();
                printNewLine();

                printText("Recibimos un pago de: $" + sumaTotal + "\n");
                printText("Nuevo saldo: $" + strSaldoVM);

                resetPrint();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();

                printUnicode();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText("Firma del Cliente");

                printNewLine();
                printNewLine();

                printText("Estamos para servirle en el\ntelefono: 712-159-07-29");

                printNewLine();
                printNewLine();
                printNewLine();

                printText("- - - - - - - - - - - - - - - - ");

                if(!strPuntosVM.equals("0")) {

                    printNewLine();
                    printNewLine();
                    printNewLine();
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    printText("Talon de Puntos\n");

                    resetPrint();

                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText(ConfiguracionesApp.getUsuarioIniciado(activity) + " " + ConfiguracionesApp.getZonaVisitar1(activity) + " " + c.TMPMOV_DATE + "\n");
                    printNewLine();

                    printTitle("*** FELICIDADES ***");
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("\n" + strNombreVM + "\n");
                    printText("Has ganado por tu venta\n");
                    printNewLine();
                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                    printTitle(strPuntosVM);
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("\nCLIENTES_ADMIN_PUNTOS_DISPONIBLES\n");

                }

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

        Intent m = new Intent(this, MenuMovimientosActivity.class);
        startActivity(m);
    }
}


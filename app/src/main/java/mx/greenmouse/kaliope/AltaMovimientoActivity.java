package mx.greenmouse.kaliope;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class AltaMovimientoActivity extends AppCompatActivity implements View.OnClickListener, FragmentParametrosClientes.OnFragmentInteractionListener, FragmentParametrosClientes.OnVariableCambiada{

    DataBaseHelper dbHelper = new DataBaseHelper(this);
    Constant c = new Constant();

    TextView txtFechaMovimiento, txtCuentaCliente,
            txtNombreCliente , firmaVoz;

    ImageButton btnEntradas, btnSalidas, btnPagos, btnReporteVisita;

    Button btnImprimirMovimiento, btnEliminarMovimiento,siguienteButton;






    //creamos la instancia de nuestro fragment de parametros
    FragmentParametrosClientes fragment = new FragmentParametrosClientes();

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_movimiento);
        getSupportActionBar().hide();
        activity = this;

        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));














        btnEntradas = (ImageButton) findViewById(R.id.btnEntradas);
        btnSalidas = (ImageButton) findViewById(R.id.btnPagosN);
        btnPagos = (ImageButton) findViewById(R.id.btnSalidasN);
        btnReporteVisita = (ImageButton) findViewById(R.id.btnReporteVisita);

        txtFechaMovimiento = (TextView) findViewById(R.id.txtFechaMov);
        txtCuentaCliente = (TextView) findViewById(R.id.txtCuentaMov);
        txtNombreCliente = (TextView) findViewById(R.id.txtNombre);
        firmaVoz = (TextView) findViewById(R.id.lblReporteMovimiento);

        btnImprimirMovimiento = (Button) findViewById(R.id.btImprimirMovimiento);
        btnEliminarMovimiento = (Button) findViewById(R.id.btnEliminarMovimiento);
        //siguienteButton = (Button) findViewById(R.id.siguiente1Button);

        btnEntradas.setOnClickListener(this);
        btnSalidas.setOnClickListener(this);
        btnPagos.setOnClickListener(this);
        btnImprimirMovimiento.setOnClickListener(this);
        btnEliminarMovimiento.setOnClickListener(this);
        btnReporteVisita.setOnClickListener(this);
        //siguienteButton.setOnClickListener(this);

        /*btnImprimirMovimiento.setOnLongClickListener(this);*/

        txtFechaMovimiento.setEnabled(false);
        txtFechaMovimiento.setClickable(false);
        txtFechaMovimiento.setFocusable(false);

        txtNombreCliente.setFilters(new InputFilter[]{new InputFilter.AllCaps()});






        if (Constant.QUIEN_LLAMA == 0){
            //(Si se llamo desde el boton de cliente nuevo
            // rellenamos la cuenta del cliente con un numero OBTENIDO DESDE la base de datos de numeros clientes nuevos que estren desocupados OSEA EN 0
            //desde el 10 hasta el 30
            // y ponemos el codego de credito 191 para
            // que le de 14 dias y 1500 de credito dejamos que el
            // nombre del cliente se pueda escribir)
            Cursor res = dbHelper.obtenerNumeroCuenta("NUEVO");
            Log.i("tamaño devuelto", String.valueOf(res.getCount()));

            if (res.getCount() > 0 ){
                res.moveToFirst();
                txtCuentaCliente.setText(res.getString(res.getColumnIndex(DataBaseHelper.NUMERO_CUENTA)));
            }else{
                //Si por alguna razon la base de datos no estuviera llena ponemos
                //Por seguridad un numero
                txtCuentaCliente.setText("10");
            }

            //txtCodigoCredito.setText("1141500");
            //BLOQUEMOS LOS EDIT TEXT,
            // evitamos que se pueda hacer foco
            // Y QUE NO SE PUEDA HACER CLIC

            Constant.TMPMOV_DAYS = "14";
            Constant.TMPMOV_GRADE = "VENDEDORA";
            Constant.TMPMOV_LIMIT = "1500";

            txtFechaMovimiento.setText(Constant.TMPMOV_DATE);
            txtCuentaCliente.setEnabled(false);
            txtCuentaCliente.setClickable(false);
            txtCuentaCliente.setFocusable(false);





            //(limpiamos las etiquetas del Scrooll view
            // cuando añadimos un cliente nuevo
            // ya que en este escrool view se muestran los
            // detalles de la base de datos, ocmo un cliente
            // nuevo no tiene historial si no llamabamos
            // a este metodo las etiquetas se quedaban vacias
            // y se veia mal)
            limpiarEtiquetasDetalles();
        }



        if (Constant.QUIEN_LLAMA == 1){
            //si lo llamo el activity DetallesClientes
            //vamos primero a rescatar los datos de la base de datos clientes.
            //para rellenar el nombre y demas datos desde la base de datos
            llenarCampos();
        }




        inicializaMovimeinto();
        txtFechaMovimiento.setText(Constant.TMPMOV_DATE);

    }

    @Override
    public void onClick(View v) {

        if (validaCabeceraMovimento()) {

            switch (v.getId()) {
                case R.id.btnEntradas:

                    if (registraCabecera()) {

                        c.MOVIMIENTO_INICIADO = true;
                        Intent e = new Intent(this, AltaEntradaActivity.class);
                        startActivity(e);
                    }




                    //PARA CERRAR EL FRAGMENT DESDE LA ACTIVIDAD
                    //https://www.flipandroid.com/eliminar-un-fragmento-especfico-de-la-backstack-de-android.html
                    //FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
                    //fragmentTransaction1.remove(fragment).commit();


                    break;
                case R.id.btnPagosN:

                    if (registraCabecera()) {

                        c.MOVIMIENTO_INICIADO = true;

                        Intent p = new Intent(this, AltaPagosActivity.class);
                        startActivity(p);

                    }

                    break;
                case R.id.btnSalidasN:

                    if (registraCabecera()) {

                        c.MOVIMIENTO_INICIADO = true;

                        Intent s = new Intent(this, AltaSalidaActivity.class);
                        startActivity(s);

                    }

                    break;
                case R.id.btImprimirMovimiento:

                    Log.d("dbg-ticket",String.valueOf(c.TMPMOV_TICKET));


                        if (c.MOVIMIENTO_INICIADO && c.TMPMOV_PAYMENT) {

                            //BUSCAR DOCUMENTACION LUISDA Pg1
                            if (c.GENERO_FIRMA_VOZ){

                                if(!c.TMPMOV_INPUT || !c.TMPMOV_OUTPUT){
                                    dialogoConfirmacionGuardarMovimeinto(this);
                                }
                                else{
                                    afectaInventario();
                                    Log.i("AltaMovimientoActivity","guardar 1");
                                    cambiarEstadoVisita();
                                    Log.i("AltaMovimientoActivity","guardar 2");

                                    terminaTMPMOV();
                                    Log.i("AltaMovimientoActivity","guardar 3");

                                    restablecerConstantes();
                                    Log.i("AltaMovimientoActivity","guardar 4");




                                }

                            } else{
                                utilidadesApp.dialogoAviso(this,"No ha generado firma de voz");
                            }
                        }

                    else {
                    utilidadesApp.dialogoAviso(this, "No se puede guardar el movimiento, aun no captura pagos");
                   }

                    break;




                case R.id.btnEliminarMovimiento:

                    //eliminaMovimientoTemporal();



                    //PARA CARGAR NUESTRO FRAGMENT A LA ACTIVIDAD
                    //PARA CERRARLO PODEMOS VER UN EJEMPLO EN LAS LINEAS DE ARRIBA EN EL
                    //BOTON ENTRADAS
                    //FragmentManager fragmentManager = getFragmentManager();
                    //FragmentTransaction transaction = fragmentManager.beginTransaction();
                    //transaction.add(R.id.activityLuisda111,fragment);
                    //transaction.commit();


                    //Para enviar datos al fragmento solo necesitamos crear una instancia del fragment
                    //y llamar a sus metodos
                    fragment.reciboDeActivity("le envio la info al fragment desde Actividad");






                    break;

                case R.id.btnReporteVisita:

                    //if(c.MOVIMIENTO_INICIADO){
                        /*Intent z = new Intent(this, ReporteDeVisita.class);
                        startActivity(z);*/
                        //si el agente resitro alguna entrada de mercancia, alguna devolucion, algun pago, algun saldo o algun punto
                        //entonces nos deja generar el reporte de audio

                       //BUSCAR DOCUMENTACION LUISDA Pg.1
                        if (c.MOVIMIENTO_INICIADO && c.TMPMOV_PAYMENT){


                        if (c.PIEZAS_DEVUELTAS!=0 || c.PIEZAS_ENTREGADAS!=0 || c.PAGO_TOTAL!=0 || c.PUNTOS!=0 || c.SALDO_PENDIENTE != 0){
                            preguntarGenerarFirmaVoz(this);

                        }else {
                            utilidadesApp.dialogoAviso(this,"Aun no puedes firmar no has capturado ningun movimiento ");
                        }
                        }else{
                            utilidadesApp.dialogoAviso(this,"Aun no puedes firmar no has capturado pagos");
                        }


                    //}
                    //else{
                    //    utilidadesApp.dialogoAviso(this, "No se ha inicializado un movimiento.");
                    //}


                    break;
//                case R.id.siguiente1Button:
//                    registraCabecera();
//                    c.MOVIMIENTO_INICIADO = true;
//                    Intent e = new Intent(this, AltaEntradaActivity.class);
//                    startActivity(e);
//                    break;

            }
        }
    }

    public void afectaInventario(){
        Constant.ULTIMOS_DATOS_SINCRONIZADOS = false;
        Log.i("afectaInventario","inicio");
        Cursor resEntradasInv = dbHelper.detalles_dameEntradas(Constant.TMPMOV_ID);

        if(resEntradasInv.getCount() > 0) {

            resEntradasInv.moveToNext();

            do {

                Log.d("dbg-entrada", resEntradasInv.getString(2));

                dbHelper.inventario_incrementaInventario(resEntradasInv.getString(6), resEntradasInv.getString(2));

            }
            while (resEntradasInv.moveToNext());
        }

        Cursor resSalidasInv = dbHelper.dameSalidas(Constant.TMPMOV_ID);
        resSalidasInv.moveToNext();

        if(resSalidasInv.getCount() > 0) {

            do {

                Log.d("dbg-entrada", resSalidasInv.getString(2));

                dbHelper.inventario_decrementaInventario(resSalidasInv.getString(6), resSalidasInv.getString(2));

            }
            while (resSalidasInv.moveToNext());
        }
        Log.i("afectaInventario","final");
    }

//este metodo se llama en el onCreate
//si el movimiento ya se inicio pone en los campos los datos ingrsados anteriormente
//despues los desabilita si aun no se inicia no hace nada

    public void inicializaMovimeinto() {


        if (Constant.MOVIMIENTO_INICIADO) {
            txtFechaMovimiento.setText(Constant.TMPMOV_DATE);
            txtCuentaCliente.setText(Constant.TMPMOV_ACCOUNT);

            txtNombreCliente.setText(Constant.TMPMOV_CLIENT);



            txtNombreCliente.setEnabled(false);
            txtNombreCliente.setClickable(false);
            txtNombreCliente.setFocusable(false);

            txtCuentaCliente.setEnabled(false);
            txtCuentaCliente.setClickable(false);
            txtCuentaCliente.setFocusable(false);



            Log.e("DBG: " , Constant.TMPMOV_ID);

        } else {

            Constant.TMPMOV_DATE = utilidadesApp.dameFehaHora();
            Constant.TMP_FECHA = utilidadesApp.dameFecha();

        }

    }


    //se llama en los metodos on clic, en pagos en entradas y en boton salidas

    public boolean registraCabecera() {
        boolean tof;

        //reaccionamos al presionar un boton
        //si el movimiento aun no se inicia por primera vez
        if (!Constant.MOVIMIENTO_INICIADO) {

//            aqui abraham inserta los datos del movimiento en la base de datos
//            lo que vamos  hacer es ponerle el texto desde la base de datos clientes:
//              inserta el movimiento a la base de datos donde le pide que los campos de texto
//            envien sus datos,
            //AGREGAMOS LOS CODIGOS DIAS Y GRADO LOS 3 JUNTOS EN EL CAMPO DE LA TABLA LLAMADO
            //MOVEMENTS_CREDIT_CODE PARA ESTE PUNTO LAS CONSTANTES YA CONTIENEN LOS VALORES
            //PORQUE SE LLENAN EN EL ONCREATE Y EN EL METODO LLENAR CAMPOS DE LA BASE DE DATOS DE
            //CLIENTES POR LO TANTO AL LLEGAR A ESTE METODO YA SE TIENE LA INFORMACION
            long i = dbHelper.insertaMovimeinto(txtCuentaCliente.getText().toString(),
                    txtNombreCliente.getText().toString(),
                    txtFechaMovimiento.getText().toString(),
                    utilidadesApp.dameFechaVencimiento(Constant.TMPMOV_DATE,Constant.TMPMOV_DAYS),
                    Constant.TMPMOV_GRADE + "," + Constant.TMPMOV_DAYS + "," + Constant.TMPMOV_LIMIT
                    );

            if (i >= 1) {
                Constant.TMPMOV_ID = dbHelper.dameUltimoId(dbHelper.TABLE_MOVEMENTS);
                Constant.TMPMOV_DATE = txtFechaMovimiento.getText().toString();
                Constant.TMPMOV_ACCOUNT = txtCuentaCliente.getText().toString();
                Constant.TMPMOV_CLIENT = txtNombreCliente.getText().toString();

                Log.i("ultimo id",String.valueOf(dbHelper.dameUltimoId(DataBaseHelper.TABLE_MOVEMENTS)));
                Log.i("ultimo id",Constant.TMPMOV_ID);

                tof = true;
            } else {
                utilidadesApp.dialogoAviso(this, "Error la intentar registrar el movimiento.");
                tof = false;
            }
        }
        else{
            tof = true;
        }

        return tof;


    }




    // si este metodo no devuelve true ningun boton se habilita
    public boolean validaCabeceraMovimento() {

        boolean v = true;


        //txtCodigoCredito.getText().toString().equals("") ||
        if (txtCuentaCliente.getText().toString().equals("") ||  txtNombreCliente.getText().toString().equals("")) {

            utilidadesApp.dialogoAviso(this, "Todos los datos son necesarios.");
            v = false;
        } else {


            //Cursor res = dbHelper.dameElCodigo(txtCodigoCredito.getText().toString());
            //res.moveToFirst();

            //if (res.getCount() >= 1) {

                //quitar estos renglones poner aqui el llenado perdo desde la base de datos de clientes se puede hacer directo desde el quien llama
                //Constant.TMPMOV_CREDIT_CODE = res.getString(1);
                //Constant.TMPMOV_GRADE = res.getString(2);
                //Constant.TMPMOV_LIMIT = res.getString(3);
                //Constant.TMPMOV_DAYS = res.getString(4);

                //Log.d("dbg-asigno-grado",Constant.TMPMOV_GRADE + " = " + res.getString(2));

            //} else {
                //utilidadesApp.dialogoAviso(this, "El código es incorrecto.");
                //v = false;
            //}

        }

        return v;
    }



    public void eliminaMovimientoTemporal() {

        if (!c.MOVIMIENTO_INICIADO) {
            utilidadesApp.dialogoAviso(this, "No existe movimiento a eliminar.");
        } else {

            restablecerConstantes();//BUSCAR DOCUMENTACION LUISDA Pg1
            String estatusMovimiento = dbHelper.dameUnicoDato(dbHelper.TABLE_MOVEMENTS, dbHelper.ESTADO_DE_LA_COLUMNA, c.TMPMOV_ID);

            if (estatusMovimiento.equals("A")) {

                dialogoConfirmacionEliminarMovimeinto(this);

            } else {
                utilidadesApp.dialogoAviso(this, "No se puede eliminar un movimiento finalizado.");
            }

        }
    }

    /*public boolean imprimeMovimiento() {
        boolean tof;
        if (c.MOVIMIENTO_INICIADO && c.TMPMOV_INPUT && c.TMPMOV_OUTPUT && c.TMPMOV_PAYMENT) {
            tof = true;
        } else {
            tof = false;
            utilidadesApp.dialogoAviso(this, "No se puede imprimir el movimiento, aun no termina de capturar entradas, salidas o pagos.");
        }
        return tof;
    }*/

    public void limpiarConstantesCamposTablas(){

        dbHelper.eliminaPagos(Constant.TMPMOV_ID);
        dbHelper.eliminaDetalles(Constant.TMPMOV_ID);
        dbHelper.eliminaMovimiento(Constant.TMPMOV_ID);

        reiniciaTMPMOV();

    }

    public void terminaTMPMOV(){
        dbHelper.terminaMovimiento();
        Log.i("terminaTMPMOV","control1");
        reiniciaTMPMOV();
    }

    public void reiniciaTMPMOV(){

        String cuentaCliente = c.TMPMOV_ACCOUNT;
        String idMovimiento = c.TMPMOV_ID;


        Constant.MOVIMIENTO_INICIADO = false;
        Constant.TMPMOV_ID = "";
        Constant.TMPMOV_CREDIT_CODE = "";
        Constant.TMPMOV_GRADE = "";
        Constant.TMPMOV_DAYS = "";
        Constant.TMPMOV_LIMIT = "";
        Constant.TMPMOV_TOPAY = "";
        Constant.TMPMOV_DATE = "";
        Constant.TMP_FECHA = "";
        Constant.MENSAJE_PAGOS = "";
        Constant.MENSAJE_DEVUELTO = ",,";
        Constant.MENSAJE_ENTREGA = ",,,";
        Constant.TMPMOV_TOREFUND = "";
        Constant.TMPMOV_EXPIRATION_DATE = "";

        Constant.TMPMOV_ACCOUNT = "";
        Constant.TMPMOV_CLIENT = "";
        Constant.TMPMOV_REPORT = false;

        Constant.TMPMOV_INPUT = false;
        Constant.TMPMOV_OUTPUT = false;
        Constant.TMPMOV_PAYMENT = false;

        Constant.TMPMOV_TICKET = 0;

        Log.i("reiniciaTMPMOV","antes de enviar a vista de movimiento");
        Intent intent = new Intent(this, VistaMovimientoActivity.class);
        intent.putExtra("cuentaCliente", cuentaCliente);
        intent.putExtra("idMovimiento", idMovimiento);
        startActivity(intent);
    }

    public void dialogoConfirmacionGuardarMovimeinto(final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final Integer[] r = new Integer[1];

        String mensaje;

        if(!Constant.TMPMOV_INPUT && Constant.TMPMOV_OUTPUT){
            mensaje = "entradas";
        }
        else if(Constant.TMPMOV_INPUT && !Constant.TMPMOV_OUTPUT){
            mensaje = "salidas";
        }
        else{
            mensaje = "entradas ni salidas";
        }

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("¡Cuidado!");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("No ha registrado " + mensaje + " de mercancía. ¿Realmente desea guardar el movimiento sin " + mensaje + "?")
                .setPositiveButton("SI, GUARDAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        afectaInventario();
                        Log.i("confirmacionguardarmov","control1");
                        cambiarEstadoVisita();
                        Log.i("confirmacionguardarmov","control2");
                        terminaTMPMOV();
                        Log.i("confirmacionguardarmov","control3");
                        restablecerConstantes();
                        Log.i("confirmacionguardarmov","control4");

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });

        builder.create();
        builder.show();


    }

    public void dialogoConfirmacionEliminarMovimeinto(Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final Integer[] r = new Integer[1];

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("¡Cuidado!");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("¿Está seguro de querer eliminar el movimiento? Todos los datos serán eliminados, así como las entradas, salidas, pagos, saldos y puntos relacinados a este.")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        limpiarConstantesCamposTablas();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });

        builder.create();
        builder.show();


    }


    public String crearMensaje (){

        Log.i("en crear mensaje","1");
        String mensajeDevueltas = "";
        String mensajeEntregadas = "";
        String mensajePago= "";
        String mensajePuntos = "";
        String mensajeSaldo = "";
        String nombre = txtNombreCliente.getText().toString();
        String piezaOpiezasDevueltas = "pieza";
        String piezaOpiezasEntregadas = "pieza";
        String tipoInicio [] = {"Yo "+ nombre +" entrego a "+ConfiguracionesApp.getNombreCorto(activity)+ "\n",
                                "Yo "+ nombre +" recibo de "+ ConfiguracionesApp.getNombreCorto(activity)+ "\n",
                                "Yo "+ nombre +" quedo a deber a Kaliope "};

        String inicioMensaje ="";
        Constant.MENSAJE_ADMINISTRACION_TEMPORAL = "";

        boolean hayInicio = false;


        if (Constant.PIEZAS_DEVUELTAS > 1){
            piezaOpiezasDevueltas = "piezas";
        }
        if (Constant.PIEZAS_ENTREGADAS > 1){
            piezaOpiezasEntregadas = "piezas";
        }


        if (Constant.PIEZAS_DEVUELTAS!= 0){
            //si no hay inicio de mensaje, devolucion lo crea con la palabra entrego
            if (!hayInicio) {
                inicioMensaje = tipoInicio[0];
                hayInicio= true;
            }
            //si se entrego devolucion se muestra este mensaje + el mensaje de inicio que ya dice entrego
            mensajeDevueltas = Constant.PIEZAS_DEVUELTAS +" "+piezaOpiezasDevueltas+ " como devolucion\n";
        }

        if (Constant.PIEZAS_ENTREGADAS != 0){
            //si no hay inicio de mensaje es decir que no se capturo devolucion, entrega lo crea con la palabra recibo
            if (!hayInicio){inicioMensaje =
                tipoInicio[1];
                hayInicio= true;
                mensajeEntregadas = Constant.PIEZAS_ENTREGADAS +" " +piezaOpiezasEntregadas + " para venta\n";
            }
            //si devolucion ya creo el inicio de mensaje este texto continua despues de devolucion
            else
            mensajeEntregadas = "recibo " + Constant.PIEZAS_ENTREGADAS + " " +piezaOpiezasEntregadas + " para venta\n";

        }

        if (Constant.PAGO_TOTAL!=0){
            //si se entergo un pago pero ni devolucion ni entrega han creado el inicio del mensaje, es decir que no se recibio ni devolucion ni entrega
            //pago crea el inicio con la palabra Entrego
            if (!hayInicio){
                inicioMensaje = tipoInicio[0]; hayInicio= true;
                mensajePago =  "un pago total de " + "$"+Constant.PAGO_TOTAL + "\n" ;
            }
            //si ya devolucion o entrega crearon el mensaje complementa con esta frase
            else
            mensajePago =  "realizo un pago total de " + "$"+Constant.PAGO_TOTAL + "\n" ;
        }



        if (Constant.PUNTOS!=0){
         //SI devolucion o entrega o pagos no hay creado el inicio, es decir que solo se caputra entrega de puntos
         //entonces Puntos lo crea con la palabra Recibo
            if (!hayInicio){
                inicioMensaje = tipoInicio[1];
                hayInicio= true;
                mensajePuntos =  Constant.PUNTOS + " puntos \n" ;
            }
            //si ya habia un inicio de mensaje por devolucion entrega o pago, completa el mensaje con esta frase
            else
            mensajePuntos = "recibo "+ Constant.PUNTOS + " puntos \n" ;
        }

        if (Constant.SALDO_PENDIENTE!=0){
            //si devolucion o entrega o pagos o puntos no han generado el inicio de mensaje es decir que solo se captura saldo pendiente
            //entonces Saldo lo crea con el texto debo a kaliope
            if (!hayInicio){
                inicioMensaje = tipoInicio[2];
                //hayInicio = true;  ya no pinemos hay inicio porque nadie mas lo vuelve a ocupar saldo es lo ultimo que se imprime
                mensajeSaldo = "$"+ Constant.SALDO_PENDIENTE + " porque .... \n";
            }
            //si devolucion o entrega o pagos o puntos ya generaron el inicio de mensaje se completa con esta frase
            else
                mensajeSaldo = "y quedo a deber " + "$"+ Constant.SALDO_PENDIENTE + " porque .... \n";
        }

        Log.i("en crear mensaje","2");




        Log.i("en crear mensaje","3");





        Log.i("en crear mensaje","4");



        Constant.MENSAJE_ADMINISTRACION_TEMPORAL =     txtCuentaCliente.getText().toString() + "," + txtNombreCliente.getText().toString() + "," + Constant.MENSAJE_DEVUELTO + "," +
                                            Constant.MENSAJE_PAGOS + "," + Constant.MENSAJE_ENTREGA + "," + Constant.TMPMOV_DATE + "," + Constant.PUNTOS_CANJEADOS + ","+
                                            Constant.INSTANCE_LATITUDE + ","+Constant.INSTANCE_LONGITUDE;



        return inicioMensaje + " " + mensajeDevueltas + mensajeEntregadas + mensajePago + mensajePuntos + mensajeSaldo +
                "\n\n\n\n\n\n\n\n\n"+
                "?\n" +
                obtenerTodosLosMovimientos(dbHelper)+
                Constant.MENSAJE_ADMINISTRACION_TEMPORAL + "\n" +
                "?";



    }//fin de metodo crear mensaje



    public String obtenerTodosLosMovimientos (DataBaseHelper dataBaseHelper){

        //(Con este metodo devolvemos todos los mensajes de los movimientos almacenados en la base de datos
        // en realidad estos son los que usan administracion para obtener toda la informacion de los
        // movimientos del dia, lo hacemos en un metodo diferente para poder enviar esta informacion
        // por internet)

        Cursor allMesajes = dataBaseHelper.obtenerMensaje();
        Cursor resIdZona = dataBaseHelper.tabla_identificador_zona_obtenerIdZona();

        String todosLosMensajes = "";
        String idZona = "";


        //(siempre que la base de datos de mensajes tenga valores
        // llenamos la variable todos los mensajes con todos los datos
        // obtenidos de la tabla mensajes dandole al final de cada mensaje un salto de linea)
        if (allMesajes.getCount()>0 ){
            allMesajes.moveToFirst();
            do {
                todosLosMensajes += allMesajes.getString(allMesajes.getColumnIndex(DataBaseHelper.MENSAJES)) + "\n";
            }while(allMesajes.moveToNext());
        }


        //rescatamos el identificador
        //de zona
        if (resIdZona.getCount()>0){
            resIdZona.moveToFirst();
            idZona = resIdZona.getString(resIdZona.getColumnIndex(DataBaseHelper.ID_ZONA));
        }


        return  idZona + "\n" +
                todosLosMensajes;

    }
    public String enviarMovimientosJsonObjet (DataBaseHelper dataBaseHelper, Activity activity){

        //consultamos denuevo el id zona;

        JSONObject mensaje = new JSONObject();
        try {
            mensaje.put("fechaClientesConsulta",ConfiguracionesApp.getFechaClientesConsulta1(activity));
            mensaje.put("usuario",ConfiguracionesApp.getUsuarioIniciado(activity));
            mensaje.put("zona",ConfiguracionesApp.getZonaVisitar1(activity));
            mensaje.put("cadenaDatos","?\n" + obtenerTodosLosMovimientos(dataBaseHelper) + "?");//añadimos los inicio de mensaje y final de mensaje
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(mensaje.toString());
        return mensaje.toString();
    }





    @Override
    public void onBackPressed() {
            preguntarSeguroDeRegresar(this);


    }


    //BUSCAR DOCUMENTACION LUISDA Pg1
    public void restablecerConstantes () {
        Constant.PIEZAS_DEVUELTAS = 0;
        Constant.IMPORTE_DEVUELTO = "";
        Constant.PIEZAS_DEVUELTAS2 = 0;


        Constant.PIEZAS_ENTREGADAS = 0;
        Constant.IMPORTE_ENTREGADO = 0;
        Constant.PIEZAS_ENTREGADAS2 = 0;
        Constant.IMPORTE_ENTREGADO2 = 0;

        Constant.PAGO_TOTAL = 0;
        Constant.SALDO_PENDIENTE = 0;
        Constant.PUNTOS = 0;
        Constant.PAGO_TOTAL2 = 0;
        Constant.SALDO_PENDIENTE2 = 0;
        Constant.PUNTOS2 = 0;

        Constant.GENERO_FIRMA_VOZ= false;
        Constant.MANTENER = false;

        //reiniciamos nuestras constantes
        //para que al siguiente movimiento
        //que se vuelva  aacceder esten en blanco
        Constant.TMP_ADEUDO_CLIENTE = "";
        Constant.TMP_ACARGO = "";
        Constant.TMP_PUNTOS_DISPONIBLES = "";
        Constant.HAY_REGALOS = false;
        Constant.PUNTOS_CANJEADOS = 0;



    }


    //BUSCAR DOCUMENTACION LUISDA Pg.1
    public boolean evaluarModificacionMovimientos (){

        boolean modificacion;//false no se modifico movimientos, true se modificaron movimientos

        if (Constant.PAGO_TOTAL == Constant.PAGO_TOTAL2
                && Constant.PUNTOS == Constant.PUNTOS2
                && Constant.SALDO_PENDIENTE == Constant.SALDO_PENDIENTE2
                && Constant.PIEZAS_ENTREGADAS == Constant.PIEZAS_ENTREGADAS2
                && Constant.IMPORTE_ENTREGADO == Constant.IMPORTE_ENTREGADO2
                && Constant.PIEZAS_DEVUELTAS== Constant.PIEZAS_DEVUELTAS2){
            //si el pago antes de ingresar a pagos es igual a pagos depues de salir, si las entregas antes de entrar son iguales al salir, si las devolociones antes de entrar son iguales al salir
            modificacion = false;
        }

        else{
            modificacion = true;
        }

        return modificacion;
    }//fin de metodo evaluarModificacionMovimientos


    //BUSCAR DOCUMENTACION LUISDA Pg1
    //en el on resume analizamos el cambio de color del texto firma del cliente cada que vuelve de otras activytis
    @Override
    public void onResume (){
        super.onResume();

        //si ya se genero la firma de voz entonces ahora revizamos que los movimientos no se hayan modificado
        if (Constant.GENERO_FIRMA_VOZ){


        //si se modificaron los movimientos
        if (evaluarModificacionMovimientos()){

            firmaVoz.setBackgroundColor(Color.parseColor("#FF0000"));//rojo
            firmaVoz.setText(R.string.firmavozmodificada);
            Constant.GENERO_FIRMA_VOZ = false; //cambiamos para que nos invalide el guardado del movimiento por la modificacion
            Constant.MANTENER = true;//la usamos porque cuando ya se genero la firma de voz y modificamos un movimiento generoFirmaVoz pasa a false, el color se pine en rojo, entonces al entrar a otro activity, el primer if que valida como verdadero firma de voz te manda ahora al else en donde si no se usaba la variable mantener, al volver al activity la alerta en rojo desaparecia y se generaba el texto por default de la vista, ahora mantener le indica que debe poner la vista en rojo otra vez, mantener cambia de valor unevamente cuando la vista se pone en verde indicando que se creo la firma y que no ubo modificacion en los movimientos

        }else{
            //si los movimientos no se modificaron
            firmaVoz.setBackgroundColor(Color.parseColor("#228B22"));
            firmaVoz.setText("Firma generada correctamente puedes Guardar el movimiento");
            Constant.MANTENER=false;
        }
        }
        //si la firma de voz no se ha generado = false entonces hace esto
        else{
            if (Constant.MANTENER){//si se detecto una modificacion de movimiento mantenemos el texto en rojo
                firmaVoz.setBackgroundColor(Color.parseColor("#FF0000"));//rojo
                firmaVoz.setText(R.string.firmavozmodificada);
            }
        }
    }


     public void preguntarGenerarFirmaVoz (final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    TextView title = new TextView(activity);
            title.setText("Title");
            title.setPadding(10, 10, 10, 10);
            title.setGravity(Gravity.CENTER);
            title.setTextSize(20);

            builder.setTitle("Confirmacion");
            builder.setIcon(R.drawable.icono_pregunta);

            builder.setMessage("¿Seguro que quieres firmar?")
                    .setPositiveButton("Si \n Quiero firmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //BUSCAR DOCUMENTACION LUISDA Pg1
                            Constant.GENERO_FIRMA_VOZ= true;
                            Constant.PAGO_TOTAL2 = Constant.PAGO_TOTAL;
                            Constant.PUNTOS2 = Constant.PUNTOS;
                            Constant.SALDO_PENDIENTE2 = Constant.SALDO_PENDIENTE;
                            Constant.PIEZAS_ENTREGADAS2 = Constant.PIEZAS_ENTREGADAS;
                            Constant.IMPORTE_ENTREGADO2 = Constant.IMPORTE_ENTREGADO;
                            Constant.PIEZAS_DEVUELTAS2 = Constant.PIEZAS_DEVUELTAS;
                            ponerEnPortapapeles(crearMensaje());
                            llamarWhatsApp(crearMensaje());

                        }
                    })
                    .setNegativeButton("NO \n Quiero firmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            return;

                        }
                    });

            builder.create();
            builder.show();


    }//fin de metodo presionar flecha atras*/


    public void llamarWhatsApp (String mensaje){
        final String telefono = "527121590729";
        PackageManager packageManager = this.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "https://api.whatsapp.com/send?phone="+ telefono +"&text=" + URLEncoder.encode(mensaje, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));

            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }//fin de metodo llamarWhatsApp


    public void ponerEnPortapapeles (String mensaje){
        //String version = String.valueOf(android.os.Build.VERSION.SDK_INT);
        //int versionSDK = android.os.Build.VERSION.SDK_INT;
        //Toast.makeText(this,String.valueOf(versionSDK),Toast.LENGTH_SHORT).show();

        //vALIDAMOS SI LA VERSION DEL SDK ES MALOR O IGUAL A 11 USAMOS LA SINTAXIS UNO DE CLIPBOARD

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            //Toast.makeText(this, "tu version es mayor a 11", Toast.LENGTH_SHORT).show();
            ClipData clip = ClipData.newPlainText("El mensaje",mensaje);
            ClipboardManager clipboard = (ClipboardManager)this.getSystemService(CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(clip);

        }else

        {
            //Toast.makeText(this, "tu version es menor a 11", Toast.LENGTH_SHORT).show();
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)this.getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText (mensaje);
        }
    }




    /**
     * Metodos que se implementan para poder cargar nuestro fragmen adecuadamente y tener comunicacion con nuestro
     * activity desde el fragment
     *
     *
     * */
    @Override
    public void onFragmentInteraction(Uri uri) {
        //we implements this metod for show the fragment
        //we can leave empty

        Toast.makeText(this,String.valueOf(uri),Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onVariableCambiada(String palabra) {
       //Toast.makeText(this, palabra, Toast.LENGTH_SHORT).show();
        txtNombreCliente.setText(palabra);
    }

























    public class Holder{

        //almacena las vistas que tenemos
        //en el scroll view donde muestra todos
        //los detalles del cliente

        TextView adeudo;
        TextView aCargo;
        TextView vencimiento;
        TextView historial;
        TextView puntos;
        TextView reporte;
        TextView indicaciones;
        TextView estadoVisita;
        TextView mercanciaAcargo;

        //ETIQUETAS
        TextView
                eadeudoAM,
                epuntosAM,
                eaCargoAM,
                evencimientoAM,
                emercanciaAM,
                ehistorialPagosAM,
                ereporteAM,
                eindicacionesAM;


        ScrollView scrollView;
    }




    //luisda, la uso para llenar los campos de nombres desde la base
    //de datos clientes
    public void llenarCampos (){
        //llamamos a dame clientes
        //para que nos devuelva al cliente con
        //el registro de id recibido desde detalles clientes

        //recibimos el dato enviado desde
        //detalles clientes, contiene el id del cliente sobre el cual
        //se hiso clic

        Holder h = new Holder();


        Cursor res = dbHelper.clientes_dameClientesPorId(Constant.ID_CLIENTE);
        res.moveToFirst();


        if (res.getCount() == 1){

            txtCuentaCliente.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE))); //la columna 1 de la db tiene la cuenta
            txtNombreCliente.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE)));//la columna 2 de la db tiene el nombre
            //txtCodigoCredito.setText(res.getString(res.getColumnIndex(DataBaseHelper.CODIGO_CLIENTE)));

            //Relacionamos las vistas de nuestro holder
            h.adeudo = (TextView) findViewById(R.id.adeudoTVam);
            h.aCargo = (TextView) findViewById(R.id.aCargoTVam);
            h.vencimiento = (TextView) findViewById(R.id.vencimientoTVam);
            h.historial = (TextView) findViewById(R.id.historialTVam);
            h.puntos = (TextView) findViewById(R.id.puntosTVam);
            h.reporte = (TextView) findViewById(R.id.reporteTVam);
            h.indicaciones = (TextView) findViewById(R.id.indicacionesTVam);
            h.mercanciaAcargo = (TextView) findViewById(R.id.mercanciaTVam);


            //LLENAMOS LAS CONSTANTES CON LO RESCATADO DE LA BASE DE DATOS
            Constant.TMPMOV_DAYS = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_DIAS_CREDITO));
            Constant.TMPMOV_GRADE = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_GRADO_CLIENTE));
            Constant.TMPMOV_LIMIT = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CREDITO_CLIENTE));
            Constant.TMP_ADEUDO_CLIENTE = res.getString(res.getColumnIndex((DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE)));
            Constant.TMP_ACARGO = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE));
            Constant.TMP_PUNTOS_DISPONIBLES = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_PUNTOS_DISPONIBLES));

            //mostramos el contenido en los textView
            h.adeudo.setText(Constant.TMP_ADEUDO_CLIENTE); // LO MOSTRAMOS DEL VALOR DE UNA CONSTANTE
            h.aCargo.setText(Constant.TMP_ACARGO);
            h.vencimiento.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_VENCIMIENTO)));
            h.historial.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_HISTORIALES)));
            h.puntos.setText(Constant.TMP_PUNTOS_DISPONIBLES);
            h.reporte.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_REPORTE)));
            h.indicaciones.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_INDICACIONES)));
            h.mercanciaAcargo.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_MERCANCIA_ACARGO)));


            //BLOQUEMOS LOS EDIT TEXT, evitamos que se pueda hacer foco Y QUE NO SE PUEDA HACER CLIC
            txtFechaMovimiento.setText(Constant.TMPMOV_DATE);
            txtNombreCliente.setEnabled(false);
            txtNombreCliente.setClickable(false);
            txtNombreCliente.setFocusable(false);

            txtCuentaCliente.setEnabled(false);
            txtCuentaCliente.setClickable(false);
            txtCuentaCliente.setFocusable(false);



        }



    }





    public void preguntarSeguroDeRegresar (final Activity activity) {

        //creamos este metodo que se llamara cuando regresemos, pero solo si fuimos llamados desde clientes
        //esto porque cuando llegamos a este activiti desde clientes se llenan los datos con la base de datos clientes
        // y supongamos que hicimos clic en un cliente maria guadalupe, si precionas volver regresa a clientes
        //seleccionas ahora otro cliente pero los datos no se actualizan, se debe evitar regresar al activyti clientes
        //sin antes reestablecer todas las constantes. el problema se acentua si ya habiamos ingresado mercancia

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("Confirmacion");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("¿Seguro que quieres regresar?  se eliminaran los movimientos registrados")
                .setPositiveButton("Si \n Quiero volver", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        reiniciaTMPMOV();
                        restablecerConstantes();

                        if (Constant.QUIEN_LLAMA == 0 || Constant.QUIEN_LLAMA == 2 ){
                            //si el activiti fue llamado desde menuMovimientos
                            //al regresar retornamos a ese mismo activity
                            Intent m = new Intent(getApplicationContext(), Trabajar.class);
                            startActivity(m);
                        }

                        //si el que lo llamo fue clientes advertimos que al regresr se eliminaran todos los datos
                        if (Constant.QUIEN_LLAMA == 1  ){
                            Intent intent = new Intent (getApplicationContext(),Clientes.class);
                            startActivity(intent);
                        }


                    }
                })
                .setNegativeButton("NO ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;

                    }
                });

        builder.create();
        builder.show();


    }//fin de metodo presionar flecha atras*/





    public void cambiarEstadoVisita (){


        //llamamos al metodo para cambiar el estado de visita del cliente
        //a visitado, mostramos un toast que le indique que el cliente
        //se a movido a visitado

        ContentValues cv = new ContentValues(1);
        cv.put(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA,Constant.ESTADO_VISITADO);
        dbHelper.clientes_actualizaTablaClientesPorKeyID(cv,Constant.ID_CLIENTE);
        Toast.makeText(getApplicationContext(),"El cliente se ha movido a visitado", Toast.LENGTH_SHORT).show();
        Constant.filtro = Constant.ESTADO_VISITADO;

        //(ponemos en ocupado el numero de cuenta que se rescato
        // de la tablanumeros de cuenta pero esto unicamente cuando}
        // llegmaos a este activity desde un cliuente nuevo o desde movimientos
        // de almacen)
        if (Constant.QUIEN_LLAMA == 0 || Constant.QUIEN_LLAMA == 2){
            dbHelper.actualizaNumerosCuenta(Integer.parseInt(Constant.TMPMOV_ACCOUNT));
        }

        //(insertamos en nuestra tabla mensajes
        // el mensaje temporal del movimiento)
        dbHelper.insertarMensaje(Constant.MENSAJE_ADMINISTRACION_TEMPORAL);


        }



    private void limpiarEtiquetasDetalles (){
        //metodo que limpia las etiquetas
        //porque cuando se accede por medio de ingresar un cliente
        //nuevo aquel que no tiene ningun historial
        //el escroll view aparece con las etiquetas
        // y eso no se ve bien.
        //entonces declaramos las vistas en el holder
        //y las relacionamos aqui, las ponemos en vacio

        Holder h = new Holder();
        h.eadeudoAM = (TextView) findViewById(R.id.adeudoAM);
        h.epuntosAM = (TextView) findViewById(R.id.puntosAM);
        h.eaCargoAM = (TextView) findViewById(R.id.aCargoAM);
        h.evencimientoAM = (TextView) findViewById(R.id.vencimientoAM);
        h.emercanciaAM = (TextView) findViewById(R.id.mercanciaAM);
        h.ehistorialPagosAM = (TextView) findViewById(R.id.historialPagosAM);
        h.ereporteAM = (TextView) findViewById(R.id.reporteAM);
        h.eindicacionesAM = (TextView) findViewById(R.id.indicacionesAM);

        h.scrollView = (ScrollView) findViewById(R.id.scrollAM);


        h.eadeudoAM.setText("");
        h.epuntosAM.setText("");
        h.eaCargoAM.setText("");
        h.evencimientoAM.setText("");
        h.emercanciaAM.setText("");
        h.ehistorialPagosAM.setText("");
        h.ereporteAM.setText("");
        h.eindicacionesAM.setText("");
        h.scrollView.setBackgroundColor(Color.TRANSPARENT);
    }







/*
    public void confirmacionImprimirOriginalMovimeinto(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final Integer[] r = new Integer[1];

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("¡Cuidado!");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("¿Desea imprimir el ticket original?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        connect();

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });

        builder.create();
        builder.show();
    }*/

    /*public void confirmacionImprimirCopiaMovimeinto(Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final Integer[] r = new Integer[1];

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("¡Cuidado!");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("¿Desea imprimir la copia del ticket?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        connect();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });

        builder.create();
        builder.show();
    }*/

    /********** ESTO HAY QUE CAMBIARLO ES PARA QUE IMPRIMA PERO HAY QUE HACERLO GLOBAL **********/

    /*protected void connect() {

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

                Cursor resSalidas = dbHelper.dameSalidas(c.TMPMOV_ID);




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

                String celdaCantidad    = "        ";      *//** La celda de Cantidad es de 9 espacios **//*
                String celdaPrecio      = "        ";      *//** La celda de Precio es de 9 espacios **//*
                String celdaDistribucion= "        ";      *//** La celda de Distribuidor es de 9 espacios **//*
                String celdaGanancia    = "          ";    *//** La celda de Ganancia es de 12 espacios **//*

                String celdaCantidadDev     = "              ";      *//** La celda de Cantidad es de 15 espacios **//*
                String celdaPrecioDev       = "        ";      *//** La celda de Precio es de 9 espacios **//*
                String celdaDistribucionDev = "               ";      *//** La celda de Distribuidor es de 16 espacios **//*

                String printCantidad    = "";
                String printPrecio      = "";
                String printDistribuidor= "";
                String printGanancia    = "";

                String printCantidadDev     = "";
                String printPrecioDev       = "";
                String printDistribuidorDev = "";

                resetPrint();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText("Kaliope Distribuidora S.A. de C.V.\n");
                printNewLine();

                printPhoto();

                printNewLine();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                printText("Cuenta: " + c.TMPMOV_ACCOUNT + "\n");
                printText("Nombre: " + c.TMPMOV_CLIENT + "\n");
                printText("Codigo: " + c.TMPMOV_CREDIT_CODE + "     " + c.TMPMOV_DAYS + " Dias de credito Grado " + c.TMPMOV_GRADE + "\n");
                printText("Fecha: " + utilidadesApp.dameFecha() + "  Vencimiento: " + c.TMPMOV_EXPIRATION_DATE + "\n");
                printText(c.INSTANCE_OWNER + " " + c.INSTANCE_ROUTE + " " + c.INSTANCE_DATE + "\n");

                printUnicode();

                if(resSalidas.getCount() >= 1) {
                    resSalidas.moveToFirst();


                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                    printText("Entrega de Mercancia\n");
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    resetPrint();
                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText("(Salida)\n");
                    resetPrint();
                    printText("Cant.    | Precio  | Dist.   | Ganancia   ");

                    do {

                        iCantidad = resSalidas.getString(2).length();
                        iPrecio = resSalidas.getString(3).length();
                        iDistribuidor = resSalidas.getString(4).length();
                        iGanancia = resSalidas.getString(5).length();


                        printCantidad = resSalidas.getString(2) + celdaCantidad.subSequence(0, celdaCantidad.length() - iCantidad);
                        printPrecio = celdaPrecio.subSequence(0, celdaPrecio.length() - iPrecio) + resSalidas.getString(3);
                        printDistribuidor = celdaDistribucion.subSequence(0, celdaDistribucion.length() - iDistribuidor) + resSalidas.getString(4);
                        printGanancia = celdaGanancia.subSequence(0, celdaGanancia.length() - iGanancia) + resSalidas.getString(5);

                        printText(printCantidad + " |" + printPrecio + " |" + printDistribuidor + " |" + printGanancia + "\n");


                        importeCodigo = Integer.parseInt(resSalidas.getString(2)) * Integer.parseInt(resSalidas.getString(4));

                        piezasTotales = piezasTotales + Integer.parseInt(resSalidas.getString(2));
                        importeTotal = importeTotal + importeCodigo;

                    } while (resSalidas.moveToNext());


                    printNewLine();
                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                    printText("  Total pz: " + String.valueOf(piezasTotales) + "   TOTAL: ");
                    *//*btoutputstream_bit.write(PrinterCommands.MX);*//*
                    printText("MXS " + String.valueOf(importeTotal) + "\n");

                    printNewLine();
                }

                Cursor resEntradas = dbHelper.detalles_dameEntradas(c.TMPMOV_ID);

                if(resEntradas.getCount() >=1) {

                    resEntradas.moveToFirst();

                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText("Devolucion\n");
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    resetPrint();
                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText("(Entrada)\n");
                    resetPrint();
                    printText("      Cant.    | Precio  | Distribucion   ");


                    do {

                        iCantidadDev = resEntradas.getString(2).length();
                        iPrecioDev = resEntradas.getString(3).length();
                        iDistribucionDev = resEntradas.getString(4).length();


                        printCantidadDev = resEntradas.getString(2) + celdaCantidadDev.subSequence(0, celdaCantidadDev.length() - iCantidadDev);
                        printPrecioDev = celdaPrecioDev.subSequence(0, celdaPrecioDev.length() - iPrecioDev) + resEntradas.getString(3);
                        printDistribuidorDev = celdaDistribucionDev.subSequence(0, celdaDistribucionDev.length() - iDistribucionDev) + resEntradas.getString(4);

                        printText(printCantidadDev + " |" + printPrecioDev + " |" + printDistribuidorDev + "\n");

                        importeCodigoDev = Integer.parseInt(resEntradas.getString(2)) * Integer.parseInt(resEntradas.getString(4));

                        piezasTotalesDev = piezasTotalesDev + Integer.parseInt(resEntradas.getString(2));
                        importeTotalDev = importeTotalDev + importeCodigoDev;

                    } while (resEntradas.moveToNext());

                    printNewLine();
                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                    printText("  Total pz: " + piezasTotalesDev + " TOTAL: MXS ");
                   *//* btoutputstream_bit.write(PrinterCommands.MX);*//*
                    printText(importeTotalDev + "\n");

                    printNewLine();
                }

                Cursor resPagos = dbHelper.damePagos(c.TMPMOV_ID);
                resPagos.moveToNext();


                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText("Pagos\n");
                btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);

                resetPrint();
                printNewLine();

                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                *//*btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);*//*
                printText("Recibimos un pago de: $" + resPagos.getString(2) + "\n");
                printText("Nuevo saldo: $" + resPagos.getString(4));

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
                printNewLine();

                printText("- - - - - - - - - - - - - - - - - - - - - ");

                if(!resPagos.getString(6).equals("0")) {

                    printNewLine();
                    printNewLine();
                    printNewLine();
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    printText("Talon de Puntos\n");

                    resetPrint();

                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText(c.INSTANCE_OWNER + " " + c.INSTANCE_ROUTE + " " + c.TMPMOV_DATE + "\n");
                    printNewLine();

                    printTitle("*** FELICIDADES ***");
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("\n" + c.TMPMOV_CLIENT + "\n");
                    printText("Has ganado por tu venta\n");
                    printNewLine();
                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                    printTitle(resPagos.getString(6));
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("\nPUNTOS\n");

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
                    *//*printText(message.getText().toString());*//*
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/


/*    @Override
    public boolean onLongClick(View view) {

        switch(view.getId()){
            case R.id.btImprimirMovimiento:
                afectaInventario();
                terminaTMPMOV();
                break;
        }

        return false;
    }*/




}

package mx.greenmouse.kaliope;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import mx.greenmouse.kaliope.DataBaseHelper;

public class RealizarEntrega extends AppCompatActivity {

    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
    private String numeroDeCuentaCliente;



    TextView
            tvSubtituloMensaje,
            tvNombreCliente,
    tvEstadoCliente,
    tvLimiteCredito,
    tvGradoCliente,
    tvDiasCredito,
    tvRegalosPiezasCapturadas,
    tvPuntosTotales,
    tvPuntosCanjeados,
    tvPuntosRestantes,
    tvCreditoPiezasCapturadas,
    tvCreditoImporteCapturado,
    tvVencimiento,
    tvContadoPiezasCapturadas,
    tvContadoImporteApagar,
    tvContadoPuntosGanados;

    EditText
    etCapturaCodigo,
    etCapturaPiezas;

    LinearLayout
    layoutEntregaCredito,
    layoutRegalos,
    layoutContado,
    layoutDatosCliente;

    TextView
            tvMensajeAdvertenciaLayoutCredito,
            tvMensajeAdvertenciaLayoutContado,
            tvMensajeAdvertenciaLayoutPuntos;



    ListView listViewPiezasCapturadas;

    Button
    botonSiguiente,
    botonNoPuedeRecibirMercanciaCredito,
    botonNoPuedeCanjearRegalos;



    int limiteCreditoConsultar = 0;
    String gradoClienteConsultar = "";
    String estadoClienteConsultar = "";
    int diasCreditoConsultar = 0;
    int regalosPiezasCapturadas = 0;
    int regalosImporteCapturado = 0;
    int puntosTotalesConsultar = 0;
    int puntosRestantes = 0;
    int puntosGanadosVentaContado = 0;
    int creditoPiezasCapturadas = 0;
    int creditoImporteCapturado = 0;
    int contadoPiezasCapturadas = 0;
    int contadoImporteApagar = 0;
    String fechaInicial = "";
    String fechaVencimiento = "";
    int diasFaltantesOsobrantesParaCorte = 0;

    int nuevoAdeudoPendienteConsultar;
    Button botonAnularRedondeoPuntos;
    Button botonAnularDiferenciaCreditoSobreapsado;


    ArrayList<HashMap> list;
    MediaPlayer mediaPlayer = new MediaPlayer();
    Vibrator vibrator;



    //CREAMOS NUESTRAS CONSTANTES QUE USAREMOS COMO KEYS EN EL MAP PARA MOSTRAR LA LSITA DEL ADAPTADOR Y NO TENER
    //QUE ESTAR ESCRIBIENDO EN EL ADAPTADOR Y EN ESTE ACTIVITY CORRECTAMENTE LOS NOMBRES A CADA MOMENTO
    public static String ADAPTADOR_LISTA_ROW_ID = "ROW_ID";
    public static String ADAPTADOR_LISTA_CANTIDAD = "CANTIDAD";
    public static String ADAPTADOR_LISTA_PRECIO = "PRECIO";
    public static String ADAPTADOR_LISTA_GRADO = "GRADO";
    public static String ADAPTADOR_LISTA_IMPORTE_TOTAL = "IMPORTE_TOTAL";
    public static String ADAPTADOR_LISTA_DISTRIBUCCION_UNITARIA = "DISTRIBUCION_UNITARIA";
    public static String ADAPTADOR_LISTA_GANACIA_UNITARIA = "GANANCIA";
    public static String ADAPTADOR_LISTA_CREDITO_CONTADO_REGALO = "CREDITO_CONTADO_REGALO";


    public static final int CREDITO = 1;
    public static final int REGALO = 2;
    public static final int CONTADO = 3;
    public static final int MOSTRAR_TODOS = 20;//solo para la base de datos

    //prohibiciones
    boolean prohibidoEntregaMercanciaCredito = true;
    boolean prohibidoCanjarPuntos = true;
    int nuevoLimiteCreditoAutorizado = 0;
    String nuevoGradoAutorizado = "";
    int nuevoDiasAutorizados = 0;
    boolean cumplioConPagoMinimoConsultar;
    int pagoMinimoRequeridoConsultar;


    //para redondear puntos canjeados dependiendo si dara diferencia de puntos
    static int redondeoPositivoOnegativoDePuntosCanjeados = 0;
    int redondeoDiferenciaCredito = 0;







    Animation animationLatido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_entrega);
        getSupportActionBar().hide();
        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));

        redondeoPositivoOnegativoDePuntosCanjeados = 0;


        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        animationLatido = AnimationUtils.loadAnimation(this,R.anim.escala); //cargamos la animacion pruebas de animacion de elementos en la pantalla
        animationLatido.setFillAfter(true);//para que se quede donde termina la anim
        animationLatido.setRepeatMode(Animation.REVERSE); //modo de repeticion, en el reverse se ejecuta la animacion y cuando termine de ejecutarse va  adar reversa
        animationLatido.setRepeatCount(Animation.INFINITE); //cuantas veces queremos que se repita la animacion, podria ser un numero entero 20 para 20 veces por ejemplo




        tvSubtituloMensaje = (TextView)findViewById(R.id.RealizarEntregaSubTituloMensaje);
        tvNombreCliente = (TextView)findViewById(R.id.RealizarEntregaNombre);
        tvEstadoCliente = (TextView)findViewById(R.id.RealizarEntregaEstadoCliente);
        tvLimiteCredito = (TextView)findViewById(R.id.RealizarEntregaLimiteCredito);
        tvGradoCliente = (TextView)findViewById(R.id.RealizarEntregaGrado);
        tvDiasCredito = (TextView)findViewById(R.id.RealizarEntregaDias);
        tvRegalosPiezasCapturadas = (TextView)findViewById(R.id.RealizarEntregaRegalosPiezasCapturadas);
        tvPuntosTotales = (TextView)findViewById(R.id.RealizarEntregaPuntosTotales);
        tvPuntosCanjeados = (TextView)findViewById(R.id.RealizarEntregaPuntosCanjeados);
        tvPuntosRestantes = (TextView)findViewById(R.id.RealizarEntregaPuntosRestantes);
        tvCreditoPiezasCapturadas = (TextView)findViewById(R.id.RealizarEntregaCreditoPiezas);
        tvCreditoImporteCapturado = (TextView)findViewById(R.id.RealizarEntregaCreditoImporte);
        tvVencimiento = (TextView)findViewById(R.id.RealizarEntregaVence);
        tvContadoPiezasCapturadas = (TextView)findViewById(R.id.RealizarEntregaContadoPiezas);
        tvContadoImporteApagar = (TextView)findViewById(R.id.RealizarEntregaContadoPagoTotal);
        tvContadoPuntosGanados = (TextView)findViewById(R.id.RealizarEntregaContadoPuntosGanados);


        etCapturaCodigo = (EditText) findViewById(R.id.RealizarEntregaCapturaCodigo);
        etCapturaPiezas = (EditText) findViewById(R.id.RealizarEntregaCapturaCantidad);


        layoutDatosCliente = (LinearLayout) findViewById(R.id.RealizarEntregaLayoutDatosCliente);
        layoutEntregaCredito = (LinearLayout) findViewById(R.id.RealizarEntregaLayoutCredito);
        layoutRegalos = (LinearLayout)findViewById(R.id.RealizarEntregaLayoutRegalos);
        layoutContado = (LinearLayout)findViewById(R.id.RealizarEntregaLayoutContado);
        tvMensajeAdvertenciaLayoutCredito = (TextView)findViewById(R.id.RealizarEntregaMensajeAdvertenciaLayoutCredito);
        tvMensajeAdvertenciaLayoutContado = (TextView)findViewById(R.id.RealizarEntregaMensajeAdvertenciaLayoutContado);
        tvMensajeAdvertenciaLayoutPuntos = (TextView)findViewById(R.id.RealizarEntregaMensajeAdvertenciaLayoutPuntos);



        listViewPiezasCapturadas = (ListView) findViewById(R.id.RealizarEntregaLista);

        botonSiguiente = (Button) findViewById(R.id.RealizarEntregaBotonSiguiente);
        botonNoPuedeRecibirMercanciaCredito = (Button) findViewById(R.id.RealizarEntregaBotonNoPuedeRecibirMercanciaCredito);
        botonNoPuedeCanjearRegalos = (Button) findViewById(R.id.RealizarEntregaBotonNoPuedeCanjearPuntos);
        botonAnularRedondeoPuntos = (Button) findViewById(R.id.RealizarEntregaBotonAnularRedondeoPuntos);
        botonAnularDiferenciaCreditoSobreapsado = (Button) findViewById(R.id.RealizarEntregaBotonAnularCreditoExcedido);
        botonAnularRedondeoPuntos.setVisibility(View.GONE);
        botonAnularDiferenciaCreditoSobreapsado.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            numeroDeCuentaCliente = bundle.getString("NUMERO_CUENTA_ENVIADO");

            cargarVistas();




        }


        etCapturaPiezas.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    agregarSalidas();
                }
                return false;
            }
        });

        listViewPiezasCapturadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap map = (HashMap) adapterView.getItemAtPosition(i);
                int rowId = Integer.valueOf(map.get(ADAPTADOR_LISTA_ROW_ID).toString());
                dialogoQueHacerConLaSalida(rowId);
            }
        });


        botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("RealizarEntregabtnSig","creditoImporteCapturado " + creditoImporteCapturado);
                Log.d("RealizarEntregabtnSig","limiteCreditoConsultar " + limiteCreditoConsultar);
                Log.d("RealizarEntregabtnSig","contadoImporteApagar " + contadoImporteApagar);
                Log.d("RealizarEntregabtnSig","puntosRestantes " + puntosRestantes);


                if (creditoImporteCapturado>limiteCreditoConsultar){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RealizarEntrega.this);
                    builder.setTitle("Credito excedido");

                    final int diferencia = creditoImporteCapturado-limiteCreditoConsultar;
                    //obtenemos un numero en positivo con la cantidad sobrepasada del credito,

                    String mensaje = "";
                    if (diferencia < 150){


                        mensaje = "El credito esta excedido por $" + diferencia + "" +
                                "\n\n-Regresa a corregir la entrega" +
                                "\n\n-Tu cliente puede generar un pago por diferencia de $" + diferencia +
                                "\n\n-Regresa y marca una pieza de contado lo cual es recomendable porque la pagaria como empresaria";

                        builder.setMessage(mensaje);
                        builder.setPositiveButton("Regresar a corregir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //ponemos los 3 botones aunque no se ocupen porque asi matiene el formato de un boton sobre otro

                            }
                        });

                        builder.setNegativeButton("Dar diferencia de $" + diferencia, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                redondeoDiferenciaCredito = diferencia; //en la parte donde se calcula el nuevo total de la mercancia se restara esta varibale
                                botonAnularDiferenciaCreditoSobreapsado.setVisibility(View.VISIBLE);
                                botonAnularDiferenciaCreditoSobreapsado.startAnimation(animationLatido);
                                mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.error);
                                mediaPlayer.start();
                                vibrator.vibrate(100);
                            }
                        });

                        builder.setNeutralButton("Regresar a marcar como contado", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                    }else{
                        //si la el exceso de credito es mayor a 150


                        mensaje = "El credito esta excedido por $" + diferencia +
                                "\n\n-El exceso es mayor a $150 no es recomendable que de diferencia en efectivo" +
                                "\n\n-Lo mejor es que tome una pieza de contado la cual entrara a precio de EMPRESARIA"+
                                "\n\n-O regresa a eliminar la mercancia excedente o marcarla como regalos";

                        builder.setMessage(mensaje);
                        builder.setPositiveButton("Ok, entiendo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                            }
                        });



                    }




                    builder.create();
                    builder.show();

                }
                else if (puntosRestantes<-500){

                    final AlertDialog.Builder builder = new AlertDialog.Builder(RealizarEntrega.this);
                    builder.setTitle("Alerta");
                    builder.setMessage("Los puntos del cliente estan en negativo, tiene que dar una diferencia mayor que $500 regresa y recaptura los regalos correctamente");
                    builder.setPositiveButton("Entiendo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.create();
                    builder.show();

                }

                else if (puntosRestantes>0 && (puntosRestantes%50) != 0){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RealizarEntrega.this);
                    builder.setTitle("Alerta");
                    //si hay sobrante de puntos en positivo y estos puntos no son multiplos de 50
                    //calculamos cual es el residuo, porque esa sifra la vamos a redodear hacia abajo
                    final int residuo = puntosRestantes%50;

                    //por ejemplo si le sobran 143 puntos el residuo nos dara 43
                    //(si le sobran 120 puntos el reciduo sera 20
                    // si le sobran 235 el reciduo sera 35
                    //
                    // entonces estableceremos que el reciduo es mayor que 25 el sistema permitira que se añada
                    // una diferencia de $25 pesos de cargo al cliente para que no se pierdan esos puntos
                    // claro podra elegir perderlos entonces quitaremos el residuo de los puntos restantes
                    //
                    // en cambio si el residuo es menor que 25 pesos el cliente perdera esos 25 puntos
                    // porque claro tendria que dar 25 pesos para no perder 25 puntos o dar 37 para no perder 13)

                    //si decide perder los puntos al total de puntos canjeados le sumaremos el reciduo
                    //si decide dar la diferencia en efectivo a los puntos canjeados le restaremos la diferencia en efectivo
                    //solo haremos eso emitimos un sonido, cambiamos la variable adecuada y cerramos el dialogo
                    //para que nuevamente el usuario tenga que dar en siguiente y ya lo dirija al menu correspondiente
                    //ahora se supone que la actividad en cuanto se cierre el dialogo recibira el focus
                    //y en ese focus se vuelven a calcular los totales, creo solo bastaria con agregar otra variable
                    //que

                    final int importeAdicional = 50 - residuo;
                    String mensaje;
                    if (residuo > 29){
                        // si el reciduo es de 30 hacia arriba
                        mensaje = "Los puntos solo pueden cambiarse en multiplos de 50.\n\n Tu cliente puede decidir:\n\n" +
                                "-Perder " + residuo + " puntos para que le queden " + (puntosRestantes - residuo) + "puntos\n\n -O dar un pago de diferencia de $" + importeAdicional + " para terminar con " + (puntosRestantes + importeAdicional) + " puntos";


                        builder.setMessage(mensaje);
                        builder.setPositiveButton("Perder " + residuo + "puntos", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                redondeoPositivoOnegativoDePuntosCanjeados = residuo;
                                mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.error);
                                mediaPlayer.start();
                                vibrator.vibrate(100);
                                botonAnularRedondeoPuntos.setVisibility(View.VISIBLE);
                                botonAnularRedondeoPuntos.startAnimation(animationLatido);
                            }
                        });

                        builder.setNegativeButton("Dar diferencia de $" + importeAdicional, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                redondeoPositivoOnegativoDePuntosCanjeados = importeAdicional * -1; //lo multiplicamos por -1 para que el importe salga en negativo, y en la parte donde sumamos esta variable, se reste en automatico
                                mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.error);
                                mediaPlayer.start();
                                vibrator.vibrate(100);
                                botonAnularRedondeoPuntos.setVisibility(View.VISIBLE);
                                botonAnularRedondeoPuntos.startAnimation(animationLatido);
                            }
                        });

                        builder.setNeutralButton("Regresar para canjear pieza mas costosa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.create();
                        builder.show();




                    }else{
                        //si el reciduo es de 29 hacia abajo
                        mensaje = "Los puntos solo pueden cambiarse en multiplos de 50.\n\n Tu cliente perdera " +
                                 residuo + " puntos" + " para que queden " + (puntosRestantes - residuo) + "\n\n -No puede dar diferencia en efectivo daria una diferencia mayor que los puntos sobrantes,\n\n -O recomiendale que tome un regalo mas costoso";

                        builder.setMessage(mensaje);
                        builder.setPositiveButton("Continuar y perder " + residuo + " puntos", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                redondeoPositivoOnegativoDePuntosCanjeados = residuo;
                                mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.error);
                                mediaPlayer.start();
                                vibrator.vibrate(100);
                                botonAnularRedondeoPuntos.setVisibility(View.VISIBLE);
                                botonAnularRedondeoPuntos.startAnimation(animationLatido);
                            }
                        });

                        builder.setNegativeButton("Regresar para canjear pieza mas costosa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.create();
                        builder.show();

                    }





                }

                else if (contadoImporteApagar > 0 || puntosRestantes < 0 || redondeoPositivoOnegativoDePuntosCanjeados<0 || redondeoDiferenciaCredito > 0){
                    //(Si tenemos puntos en negativo no mayores a 150 o venta de contado entonces vamos a registrar los pagos o si el redondeo es negativo es decir que el
                    // cleinte opto por dar una diferencia para no perder sus puntos, entonces vamos a los pagos
                    // aqui solo puede haber la opcion ya que los if de arriba validan primero que los puntos restantes no sean negativos
                    // si son negativos no se activa la parte de redondeo de puntos entonces solo podrias entrar aqui por una diferencia de
                    // regalo por puntos en negativo, pero si los puntos son mayores que 0 si esta la posibilidad que entre al if superior
                    // y claro como los puntos restantes ya solo son positivos solo podriamos entrar aqui por el redondeo de puntos)
                    guardarDatosClientes();
                    Intent intent = new Intent(RealizarEntrega.this, RealizarPagos2ContadoRegalos.class);
                    intent.putExtra("NUMERO_CUENTA_ENVIADO", numeroDeCuentaCliente);
                    startActivity(intent);
                }
                else{
                    //si ya no hay nada mas nos dirigimos a finalizar el movimiento
                    guardarDatosClientes();
                    Intent intent = new Intent(RealizarEntrega.this, RealizarFinalizarMovimiento.class);
                    intent.putExtra("NUMERO_CUENTA_ENVIADO", numeroDeCuentaCliente);
                    startActivity(intent);
                }








            }
        });


        botonNoPuedeCanjearRegalos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        botonNoPuedeRecibirMercanciaCredito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        botonAnularRedondeoPuntos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redondeoPositivoOnegativoDePuntosCanjeados = 0;
                mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.error);
                mediaPlayer.start();
                vibrator.vibrate(100);
                botonAnularRedondeoPuntos.setVisibility(View.GONE);
                botonAnularRedondeoPuntos.clearAnimation();
                cargarVistas();
            }
        });

        botonAnularDiferenciaCreditoSobreapsado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redondeoDiferenciaCredito = 0;
                mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.error);
                mediaPlayer.start();
                vibrator.vibrate(100);
                botonAnularDiferenciaCreditoSobreapsado.setVisibility(View.GONE);
                botonAnularDiferenciaCreditoSobreapsado.clearAnimation();
                cargarVistas();//para que se calculen denuevo lso totales
            }
        });


    }




    private void cargarVistas(){

        Cursor datosCliente = dataBaseHelper.clientes_dameClientePorCuentaCliente(numeroDeCuentaCliente);
        if (datosCliente.getCount()>0){
            datosCliente.moveToFirst();



            limiteCreditoConsultar = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CREDITO_CLIENTE));
            gradoClienteConsultar = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_GRADO_CLIENTE));
            estadoClienteConsultar = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ESTADO_CLIENTE));
            puntosTotalesConsultar = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_PUNTOS_DISPONIBLES))
                                    + datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CIE_PUNTOS_GANADOS_VENTA_LLENAR))
                                    + dataBaseHelper.clientes_calcularPuntosGanadosVentaAlContado(numeroDeCuentaCliente);
            nuevoAdeudoPendienteConsultar = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA));
            nuevoAdeudoPendienteConsultar = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA));
            diasCreditoConsultar = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_DIAS_CREDITO));
            cumplioConPagoMinimoConsultar = (datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_CUMPLIO_CON_PAGO_MINIMO)) == 1); //para obtener el valor booleano debido a que no hay metodo getBoolean, esta es una forma practica encontrada en interntet
            pagoMinimoRequeridoConsultar = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_PAGO_MINIMO_REQUERIDO_PARA_ENTREGA_MERCANCIA));
            diasFaltantesOsobrantesParaCorte = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE));
            fechaInicial = utilidadesApp.getFecha();







            String nombreCliente = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE));






            tvNombreCliente.setText(nombreCliente);

            tvLimiteCredito.setText(String.valueOf(limiteCreditoConsultar));
            tvGradoCliente.setText(gradoClienteConsultar);
            tvPuntosTotales.setText(String.valueOf(puntosTotalesConsultar));
            tvPuntosRestantes.setText(String.valueOf(puntosRestantes));
            tvDiasCredito.setText(String.valueOf(diasCreditoConsultar));

            tvEstadoCliente.setText(estadoClienteConsultar);
            switch (estadoClienteConsultar) {
                case Constant.ACTIVO:
                    tvEstadoCliente.setBackgroundResource(R.color.colorActivo);
                    break;
                case Constant.REACTIVAR:
                    tvEstadoCliente.setBackgroundResource(R.color.colorReactivar);
                    break;
                case Constant.LIO:
                    tvEstadoCliente.setBackgroundResource(R.color.colorLio);
                    break;
                case Constant.PROSPECTO:
                    tvEstadoCliente.setBackgroundResource(R.color.colorProspecto);
                    break;
            }






            listaSalidas();
            calcularBotonesProhibiciones();




        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            //Toast.makeText(this, "focusGanado", Toast.LENGTH_SHORT).show();
            cargarVistas();
        }else{
            //Toast.makeText(this, "focusPerdido", Toast.LENGTH_SHORT).show();

        }

    }

    private void listaSalidas(){


        list = new ArrayList<HashMap>();

        ListViewAdapterEntregaMercancia adapter = new ListViewAdapterEntregaMercancia(this, list);
        listViewPiezasCapturadas.setAdapter(adapter);



        Cursor res = dataBaseHelper.entrega_mercancia_dameEntrega(numeroDeCuentaCliente,MOSTRAR_TODOS);
        Log.d("dbg-resEntrdas", String.valueOf(res.getCount()));


        if(res.getCount()>0) {

            res.moveToFirst();
            do {
                HashMap temp1 = new HashMap();



                temp1.put(ADAPTADOR_LISTA_ROW_ID, res.getString(res.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_ROW_ID)));//enviamos el id de la tabla en la columna 1 para poder sacarlo del view en el item clicl listener y eliminarlo
                temp1.put(ADAPTADOR_LISTA_CANTIDAD, res.getInt(res.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CANTIDAD)));
                temp1.put(ADAPTADOR_LISTA_PRECIO, res.getString(res.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_PRECIO)));
                temp1.put(ADAPTADOR_LISTA_IMPORTE_TOTAL,res.getString(res.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_IMPORTE_TOTAL_ENTREGA)));
                temp1.put(ADAPTADOR_LISTA_GRADO, res.getString(res.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_GRADO_DE_ENTREGA)));
                temp1.put(ADAPTADOR_LISTA_DISTRIBUCCION_UNITARIA,res.getInt(res.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_DISTRIBUCION)));
                temp1.put(ADAPTADOR_LISTA_GANACIA_UNITARIA, res.getString(res.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_GANANCIA)));
                temp1.put(ADAPTADOR_LISTA_CREDITO_CONTADO_REGALO,res.getString(res.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO)));

                list.add(temp1);



            } while (res.moveToNext());



        }


        calcularTotales();
        setListViewHeightBasedOnChildren(listViewPiezasCapturadas);


    }



    private void calcularTotales(){
        creditoPiezasCapturadas = dataBaseHelper.entrega_mercancia_calcularPiezasEntregadas(numeroDeCuentaCliente,CREDITO);
        creditoImporteCapturado = dataBaseHelper.entrega_mercancia_calcularImporteEntrega(numeroDeCuentaCliente,CREDITO) - redondeoDiferenciaCredito;
        contadoPiezasCapturadas = dataBaseHelper.entrega_mercancia_calcularPiezasEntregadas(numeroDeCuentaCliente,CONTADO);
        contadoImporteApagar = dataBaseHelper.entrega_mercancia_calcularImporteEntrega(numeroDeCuentaCliente,CONTADO);
        regalosPiezasCapturadas = dataBaseHelper.entrega_mercancia_calcularPiezasEntregadas(numeroDeCuentaCliente,REGALO);
        regalosImporteCapturado = dataBaseHelper.entrega_mercancia_calcularImporteEntrega(numeroDeCuentaCliente,REGALO) + redondeoPositivoOnegativoDePuntosCanjeados;
        puntosRestantes = puntosTotalesConsultar - regalosImporteCapturado;
        puntosGanadosVentaContado = dataBaseHelper.clientes_calcularPuntosGanadosVentaAlContado(numeroDeCuentaCliente);



        if (creditoPiezasCapturadas!=0 || creditoImporteCapturado!=0){
            layoutEntregaCredito.setVisibility(View.VISIBLE);
        }else{
            layoutEntregaCredito.setVisibility(View.GONE);
        }

        if (contadoPiezasCapturadas!=0 || contadoImporteApagar!=0){
            layoutContado.setVisibility(View.VISIBLE);
        }else{
            layoutContado.setVisibility(View.GONE);
        }

        if (regalosPiezasCapturadas!=0 || regalosImporteCapturado!=0){
            layoutRegalos.setVisibility(View.VISIBLE);
        }else{
            layoutRegalos.setVisibility(View.GONE);
        }








        tvCreditoPiezasCapturadas.setText(String.valueOf(creditoPiezasCapturadas));
        tvCreditoImporteCapturado.setText(String.valueOf(creditoImporteCapturado));
        tvContadoPiezasCapturadas.setText(String.valueOf(contadoPiezasCapturadas));
        tvContadoImporteApagar.setText(String.valueOf(contadoImporteApagar));
        tvRegalosPiezasCapturadas.setText(String.valueOf(regalosPiezasCapturadas));
        tvPuntosCanjeados.setText(String.valueOf(regalosImporteCapturado));
        tvPuntosRestantes.setText(String.valueOf(puntosRestantes));
        tvContadoPuntosGanados.setText(String.valueOf(puntosGanadosVentaContado));




        //(Para calcular la fecha de vencimiento tomamos en cuenta los dias sobrantes o faltantes para corte por ejemplo si adelantamos la ruta del lunes a visitarla el Domingo
        // el sistema marcara que faltan 1 dias para su corte entonces al visitarla si tomamos la fecha inicial de hoy el sistema al sumar los dias de credito arrojara
        // de hoy domingo en 14 dias pero eso sera incorrecto porque en si a la clienta no la movimos de su dia de cierre del lunes al domingo sino que seguira viendose el lunes
        // solo que por motivos de fin de año adelantamos a la clienta 1 dia, entonces este sistema sumara a los dias de credito los dias que estemos adelantados al corte para asi
        // resultar en el dia lunes nuevamente. Al igual si se carga la ruta de un dia anterior sabado por ejemplo el sistema marcara -1 dia de cierre es decir ya esta pasada un dia
        // a los 14 dias de credito le quitara 1 dia lo cual sumara al final 13 dias, para que la cuenta vuelva a quedar en el dia sabado
        // PUTO IMPORTANTE: Si el cliente presenta un atrazo de por ejemplo -28 dias si permitimos que pase asi el sistema restara esa cantidad de dias
        // cuando pro ejemplo en el area de clientes solo permitimos 3 dias antes o 3 dias despues para interpretar que el cleinte esta en AUN NO TOCA CIERRE PERO SE PODRIA HACER VISITA
        // aqui calidaremos eso, si son + 3 dias o -3 dias de su fecha de cierre quiere decir que este cliente se esta adelantando la visita intencionalmente por adminsitracion
        // pero si ya son mas dias en negativo significa que el cliente realmente presenta un atraso y por lo tanto se debe quedar en su dia actual de cierre mas sus dias de credito
        // normales)
        if(diasFaltantesOsobrantesParaCorte<=3 && diasFaltantesOsobrantesParaCorte>=-3){
            fechaVencimiento = utilidadesApp.calcularFechaVencimiento(utilidadesApp.getFecha(), Calendar.DAY_OF_YEAR, diasCreditoConsultar + diasFaltantesOsobrantesParaCorte);
            String mensajeVencimiento = utilidadesApp.getFecha() + " + " + diasCreditoConsultar + " dias credito  + " + diasFaltantesOsobrantesParaCorte + "dias sobrantes o faltantes para corte = " + fechaVencimiento;
            Log.i("calculo_fecha_vence ", mensajeVencimiento);
           }else{
            //si los dias de vencimiento son menores a -3 o mayores a 3 entonces ponemos su fecha de corte normal
            fechaVencimiento = utilidadesApp.calcularFechaVencimiento(utilidadesApp.getFecha(), Calendar.DAY_OF_YEAR, diasCreditoConsultar);
            String mensajeVencimiento = "los dias son mayores o menores a 3 dias se ha ajustado la formula de calculo" + utilidadesApp.getFecha() + " + " + diasCreditoConsultar + " dias credito  + se ignoran: " + diasFaltantesOsobrantesParaCorte + " dias faltantes o sobrantes para corte = " + fechaVencimiento;
            Log.i("calculo_fecha_vence ", mensajeVencimiento);
        }
        tvVencimiento.setText(fechaVencimiento);



        if (creditoImporteCapturado>limiteCreditoConsultar){
                Toast.makeText(this, "Limite de credito excedido", Toast.LENGTH_SHORT).show();

            if((creditoImporteCapturado-limiteCreditoConsultar)>150){
                tvLimiteCredito.setTextColor(Color.RED);
                tvLimiteCredito.setTextSize(20);
                tvLimiteCredito.startAnimation(animationLatido);
                tvCreditoImporteCapturado.setTextColor(Color.RED);
                tvCreditoImporteCapturado.startAnimation(animationLatido);
                tvMensajeAdvertenciaLayoutCredito.setVisibility(View.VISIBLE);
                String mensaje = "Limite de credito excedido por $" + (creditoImporteCapturado-limiteCreditoConsultar) + " son mas de 150";
                tvMensajeAdvertenciaLayoutCredito.setText(mensaje);
                tvMensajeAdvertenciaLayoutCredito.setTextColor(Color.RED);
            }else{
                tvLimiteCredito.setTextColor(Color.BLUE);
                tvLimiteCredito.setTextSize(20);
                tvLimiteCredito.startAnimation(animationLatido);
                tvCreditoImporteCapturado.setTextColor(Color.BLUE);
                tvCreditoImporteCapturado.startAnimation(animationLatido);
                tvMensajeAdvertenciaLayoutCredito.setVisibility(View.VISIBLE);
                String mensaje = "Limite de credito excedido por $" + (creditoImporteCapturado-limiteCreditoConsultar) + " podra dar diferencia " ;
                tvMensajeAdvertenciaLayoutCredito.setText(mensaje);
                tvMensajeAdvertenciaLayoutCredito.setTextColor(Color.BLUE);

            }



        }else{
            tvLimiteCredito.setTextColor(Color.BLACK);
            tvLimiteCredito.setTextSize(15);
            tvLimiteCredito.clearAnimation();
            tvCreditoImporteCapturado.setTextColor(Color.BLACK);
            tvCreditoImporteCapturado.clearAnimation();
            tvMensajeAdvertenciaLayoutCredito.setVisibility(View.GONE);
        }


        if (puntosRestantes<-500){
            tvPuntosRestantes.setTextColor(Color.RED);
            tvPuntosRestantes.startAnimation(animationLatido);
            tvMensajeAdvertenciaLayoutPuntos.setVisibility(View.VISIBLE);
            tvMensajeAdvertenciaLayoutPuntos.setText("Puntos en negativo mayor a 500");
            tvMensajeAdvertenciaLayoutPuntos.setTextColor(Color.RED);

        }else if (puntosRestantes < 0){
            tvPuntosRestantes.setTextColor(Color.BLUE);
            tvPuntosRestantes.startAnimation(animationLatido);
            tvMensajeAdvertenciaLayoutPuntos.setVisibility(View.VISIBLE);
            String mensaje = "Debera pagar diferencia de $" + (puntosRestantes * -1);
            tvMensajeAdvertenciaLayoutPuntos.setText(mensaje);
            tvMensajeAdvertenciaLayoutPuntos.setTextColor(Color.BLUE);
        }else{
            tvPuntosRestantes.setTextColor(Color.BLACK);
            tvPuntosRestantes.clearAnimation();
            tvMensajeAdvertenciaLayoutPuntos.setVisibility(View.GONE);
        }



        if (puntosGanadosVentaContado>0){
            tvPuntosTotales.setTextColor(getResources().getColor(R.color.colorActivo));
            tvPuntosTotales.startAnimation(animationLatido);
            tvContadoPuntosGanados.setTextColor(getResources().getColor(R.color.colorActivo));
            tvContadoPuntosGanados.startAnimation(animationLatido);
            String mensaje = "Ha ganado " + puntosGanadosVentaContado + "puntos por esta venta de contado, se han sumado a sus puntos disponibles";
            tvMensajeAdvertenciaLayoutContado.setText(mensaje);
            tvMensajeAdvertenciaLayoutContado.setTextColor(getResources().getColor(R.color.colorActivo));
            tvMensajeAdvertenciaLayoutContado.setVisibility(View.VISIBLE);
        }else{
            tvPuntosTotales.setTextColor(Color.BLACK);
            tvPuntosTotales.clearAnimation();
            tvContadoPuntosGanados.setTextColor(Color.BLACK);
            tvContadoPuntosGanados.clearAnimation();
            tvMensajeAdvertenciaLayoutContado.setVisibility(View.GONE);
        }




    }



    private void agregarSalidas(){

        String codigo = etCapturaCodigo.getText().toString();
        String cantidad = etCapturaPiezas.getText().toString();


        try {
            if (codigo.isEmpty()|| codigo.equals("0")) throw new Exception("El codigo esta vacio o es 0");
            if (cantidad.isEmpty()|| cantidad.equals("0")) throw new Exception("La cantidad esta vacia o es 0");

            int costoVentaProducto = dataBaseHelper.inventario_dameCostoDelProducto(codigo);
            int precioDistribucion = dataBaseHelper.inventario_damePrecioDistribucionDelProducto(codigo,gradoClienteConsultar);
            int gananciaDelProducto = dataBaseHelper.inventario_dameGananciaDelProducto(codigo,gradoClienteConsultar);
            int importeTotalEntrega  = precioDistribucion * Integer.valueOf(cantidad);

            Log.d("realizarSalidasAgr","Cantidad " + cantidad);
            Log.d("realizarSalidasAgr","costoVentaProducto " + costoVentaProducto);
            Log.d("realizarSalidasAgr","precioDistribucion " + precioDistribucion);
            Log.d("realizarSalidasAgr","gananciaDelProducto " + gananciaDelProducto);
            Log.d("realizarSalidasAgr","importeTotalEntrega " + importeTotalEntrega);


            ContentValues contentValues = new ContentValues(10);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_CUENTA_CLIENTE,numeroDeCuentaCliente);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_CANTIDAD,cantidad);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_PRECIO,costoVentaProducto);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_DISTRIBUCION,precioDistribucion);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_GANANCIA,gananciaDelProducto);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_IMPORTE_TOTAL_ENTREGA,importeTotalEntrega);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_CODIGO,codigo);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_GRADO_DE_ENTREGA,gradoClienteConsultar);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO,CREDITO);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_LATITUD,Constant.INSTANCE_LATITUDE);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_LONGUITUD,Constant.INSTANCE_LONGITUDE);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_HORA_CAPTURA,utilidadesApp.dameHora());


            if(prohibidoEntregaMercanciaCredito){
                precioDistribucion = dataBaseHelper.inventario_damePrecioDistribucionDelProducto(codigo,Constant.EMPRESARIA);
                gananciaDelProducto = dataBaseHelper.inventario_dameGananciaDelProducto(codigo,Constant.EMPRESARIA);
                importeTotalEntrega = precioDistribucion * Integer.valueOf(cantidad);
                contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO,CONTADO);
                contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_DISTRIBUCION,precioDistribucion);
                contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_GANANCIA,gananciaDelProducto);
                contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_IMPORTE_TOTAL_ENTREGA,importeTotalEntrega);
                contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_GRADO_DE_ENTREGA,Constant.EMPRESARIA);
            }
//(Pensaba que si el sistema prohivia entregar a credito, entonces cualquier producto que se entregara marcarlo como de contado en automatico
// mi poroblema viene que estoy permitiendo que se pueda anular la prohivicion de la entrega de mercancia a credito con un codigo variable
// entonces si el promotor ya ingreso productos a y el sistema los marca como contado cuando anulemos este codigo que pasara?
// podria eliminar la tabla producto de la lista para que pueda volver a ingresar el producto ahora si como contado
// o que lo que ya se ingreso de contado se cambie en automatico a credito
// o )



            if (dataBaseHelper.entrega_mercancia_insertarProducto(contentValues) == -1) throw new Exception("Ocurrio un problema al registrar el producto a la entrada"); //FUNCINA SI POR EJEMPLO NO ENCUENTRA EL NOMBRE DE UNA COLUMNA

            etCapturaCodigo.setText("");
            etCapturaPiezas.setText("");
            etCapturaCodigo.requestFocus();

            vibrator.vibrate(400);
            mediaPlayer = MediaPlayer.create(this,R.raw.harpsound);
            mediaPlayer.start();
            listaSalidas();

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            mediaPlayer = MediaPlayer.create(this,R.raw.error);
            mediaPlayer.start();
            vibrator.vibrate(100);
        }


    }





    @Override
    public void onBackPressed() {


        AlertDialog.Builder builder = new AlertDialog.Builder(RealizarEntrega.this);
        builder.setTitle("Seguro que quieres regresar");
        builder.setMessage("Al regresar se eliminaran los productos capturados");
        builder.setPositiveButton("Si, regresar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dataBaseHelper.entrega_mercnacia_eliminarEntrega(numeroDeCuentaCliente);
                eliminarDatosClientes();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create();
        builder.show();

    }




    private void dialogoQueHacerConLaSalida(final int rowId){
        final AlertDialog.Builder builder = new AlertDialog.Builder(RealizarEntrega.this);

        LayoutInflater inflater = RealizarEntrega.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_regalar_contado_eliminar,null);


        Button botonCredito = view.findViewById(R.id.DialogoRegalarContadoEliminarBotonCredito);
        Button botonRegalar = view.findViewById(R.id.DialogoRegalarContadoEliminarBotonRegalo);
        Button botonContado = view.findViewById(R.id.DialogoRegalarContadoEliminarBotonContado);
        Button botonEliminar = view.findViewById(R.id.DialogoRegalarContadoEliminarBotonEliminar);






        //esto lo hacemos para poder cerrar el alert dialog la otra opcion esta en la clase de variable password
        final AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);



        botonRegalar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdministrarRegalos administrarRegalos = new AdministrarRegalos(RealizarEntrega.this, numeroDeCuentaCliente, rowId,dataBaseHelper);
                administrarRegalos.dialogoRegalarSalida();
                alertDialog.cancel();
            }
        });


        botonContado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ventaAlContado(rowId);
                alertDialog.cancel();
            }
        });


        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarSalida(rowId);
                alertDialog.cancel();

            }
        });

        botonCredito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marcarComoCredito(rowId);
                alertDialog.cancel();

            }
        });










        //consultamos del renglon seleccionado el codigo que se ingreso para mostrar los botones adecuados
        Cursor infoRenglon = dataBaseHelper.entrega_mercancia_dameRenglon(rowId);
        infoRenglon.moveToFirst();
        int esCreditoContadoRegalo = infoRenglon.getInt(infoRenglon.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO));

        switch (esCreditoContadoRegalo){
            case CREDITO:
                botonCredito.setVisibility(View.GONE);
                botonContado.setVisibility(View.VISIBLE);
                botonEliminar.setVisibility(View.VISIBLE);
                botonRegalar.setVisibility(View.VISIBLE);
                break;

            case CONTADO:

                botonCredito.setVisibility(View.VISIBLE);
                botonContado.setVisibility(View.GONE);
                botonEliminar.setVisibility(View.VISIBLE);
                botonRegalar.setVisibility(View.VISIBLE);
                break;

            case REGALO:

                botonCredito.setVisibility(View.VISIBLE);
                botonContado.setVisibility(View.VISIBLE);
                botonEliminar.setVisibility(View.VISIBLE);
                botonRegalar.setVisibility(View.GONE);
                break;

        }


            if (prohibidoCanjarPuntos || puntosTotalesConsultar == 0){
                botonRegalar.setVisibility(View.GONE);
            }

            if (prohibidoEntregaMercanciaCredito){
                botonCredito.setVisibility(View.GONE);
            }












        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.create();
        alertDialog.show();



    }


    private void eliminarSalida(int rowId){

        mediaPlayer = MediaPlayer.create(this,R.raw.error);
        mediaPlayer.start();
        vibrator.vibrate(100);

        dataBaseHelper.entrega_mercancia_eliminaRenglonPorKeyID(rowId);
        listaSalidas();

    }

    private void ventaAlContado (int rowId){

        //consultamos del renglon seleccionado el codigo que se ingreso
        Cursor infoRenglon = dataBaseHelper.entrega_mercancia_dameRenglon(rowId);
        infoRenglon.moveToFirst();

        String codigoDelProducto = infoRenglon.getString(infoRenglon.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CODIGO));
        int cantidad = infoRenglon.getInt(infoRenglon.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CANTIDAD));



        //consultamos el costo del producto a empresaria
        try {
            int precioDistribucionEmpresaria = dataBaseHelper.inventario_damePrecioDistribucionDelProducto(codigoDelProducto,Constant.EMPRESARIA);
            int gananciaDelProducto = dataBaseHelper.inventario_dameGananciaDelProducto(codigoDelProducto,Constant.EMPRESARIA);
            int importeTotalEntrega = cantidad*precioDistribucionEmpresaria;

            //convertimos la pieza que nos enviaron a contado
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO, RealizarEntrega.CONTADO);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_DISTRIBUCION,precioDistribucionEmpresaria);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_GANANCIA,gananciaDelProducto);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_IMPORTE_TOTAL_ENTREGA,importeTotalEntrega);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_GRADO_DE_ENTREGA,Constant.EMPRESARIA);
            dataBaseHelper.entrega_mercancia_actualizaRenglon(rowId,contentValues);


            mediaPlayer = MediaPlayer.create(this,R.raw.exito);
            mediaPlayer.start();
            vibrator.vibrate(100);



        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    private void marcarComoCredito (int rowId){

        //consultamos del renglon seleccionado el codigo que se ingreso
        Cursor infoRenglon = dataBaseHelper.entrega_mercancia_dameRenglon(rowId);
        infoRenglon.moveToFirst();

        String codigoDelProducto = infoRenglon.getString(infoRenglon.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CODIGO));
        int cantidad = infoRenglon.getInt(infoRenglon.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CANTIDAD));


        try {
            int precioDistribucion = dataBaseHelper.inventario_damePrecioDistribucionDelProducto(codigoDelProducto,gradoClienteConsultar);
            int gananciaDelProducto = dataBaseHelper.inventario_dameGananciaDelProducto(codigoDelProducto,gradoClienteConsultar);
            int importeTotalEntrega = cantidad*precioDistribucion;
            //convertimos la pieza que nos enviaron a contado
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO, RealizarEntrega.CREDITO);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_DISTRIBUCION,precioDistribucion);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_GANANCIA,gananciaDelProducto);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_IMPORTE_TOTAL_ENTREGA,importeTotalEntrega);
            contentValues.put(DataBaseHelper.ENTREGA_MERCANCIA_GRADO_DE_ENTREGA,gradoClienteConsultar);
            dataBaseHelper.entrega_mercancia_actualizaRenglon(rowId,contentValues);

            mediaPlayer = MediaPlayer.create(this,R.raw.exito);
            mediaPlayer.start();
            vibrator.vibrate(100);


        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    private  void guardarDatosClientes (){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_ENT_CANTIDAD_CREDITO,creditoPiezasCapturadas);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_CANTIDAD_CONTADO,contadoPiezasCapturadas);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_CANTIDAD_REGALOS,regalosPiezasCapturadas);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_IMPORTE_CREDITO,creditoImporteCapturado);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_IMPORTE_CONTADO,contadoImporteApagar);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_IMPORTE_REGALOS,regalosImporteCapturado);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA,dataBaseHelper.entrega_mercancia_consultarEntregaEnJsonArrayString(numeroDeCuentaCliente,MOSTRAR_TODOS));
        contentValues.put(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA_CREDITO,dataBaseHelper.entrega_mercancia_consultarEntregaEnJsonArrayString(numeroDeCuentaCliente,CREDITO));
        contentValues.put(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA_CONTADO,dataBaseHelper.entrega_mercancia_consultarEntregaEnJsonArrayString(numeroDeCuentaCliente,CONTADO));
        contentValues.put(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA_REGALOS,dataBaseHelper.entrega_mercancia_consultarEntregaEnJsonArrayString(numeroDeCuentaCliente,REGALO));
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PUNTOS_GANADOS_CONTADO,puntosGanadosVentaContado);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PUNTOS_TOTALES,puntosTotalesConsultar);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PUNTOS_CANJEADOS,regalosImporteCapturado);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PUNTOS_RESTANTES,puntosRestantes);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PROHIBIDO_ENTREGAR_MERCANCIA_CREDITO,prohibidoEntregaMercanciaCredito);   //si es true guardara 1
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PROHIBIDO_CANJEAR_PUNTOS,prohibidoCanjarPuntos);  //si es true guarada 1
        contentValues.put(DataBaseHelper.CLIENTES_ENT_NUEVO_CREDITO_CALCULADO,nuevoLimiteCreditoAutorizado);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_NUEVOS_DIAS_DE_CREDITO,nuevoDiasAutorizados);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_NUEVOS_GRADO_CALCULADO,nuevoGradoAutorizado);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_FECHA_VENCIMIENTO,fechaVencimiento);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_DIFERENCIA_EXCESO_CREDITO,redondeoDiferenciaCredito);

        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);

    }

    private  void eliminarDatosClientes (){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_ENT_CANTIDAD_CREDITO,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_CANTIDAD_CONTADO,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_CANTIDAD_REGALOS,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_IMPORTE_CREDITO,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_IMPORTE_CONTADO,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_IMPORTE_REGALOS,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA_CREDITO,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA_CONTADO,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA_REGALOS,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PUNTOS_GANADOS_CONTADO,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PUNTOS_TOTALES,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PUNTOS_CANJEADOS,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PUNTOS_RESTANTES,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PROHIBIDO_ENTREGAR_MERCANCIA_CREDITO,false); //si es false la db guarda 0
        contentValues.put(DataBaseHelper.CLIENTES_ENT_PROHIBIDO_CANJEAR_PUNTOS,false); //si es false la db guarda 0
        contentValues.put(DataBaseHelper.CLIENTES_ENT_NUEVO_CREDITO_CALCULADO,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_NUEVOS_DIAS_DE_CREDITO,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_NUEVOS_GRADO_CALCULADO,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_FECHA_VENCIMIENTO,0);
        contentValues.put(DataBaseHelper.CLIENTES_ENT_DIFERENCIA_EXCESO_CREDITO,0);


        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);

    }



    private void calcularBotonesProhibiciones(){
        //Lio
        //(no se puede entregar mercancia
        //
        // Activo
        // se puede entregar mercancia
        // si no cubrio su pago minimo no se puede entregar mercancia- ni cambiar puntos
        // si su limite de credito es 0 no se puede entregar mercancia- pero cambiar puntos si
        // si dejo saldo pendiente no puede canjear regalos y cubrio pago minimo- no se pueden canjear regalos
        //
        // Reactivar
        // Se puede entregar mercancia
        // si su limite es 0 no se puede entregar mercancia
        //
        //
        // Prospecto
        // Se puede entregar Mercancia
        //
        //
        //)
        Log.d("RealizarEntrega5","estado " + estadoClienteConsultar);
        Log.d("RealizarEntrega5","grado " + gradoClienteConsultar);
        Log.d("RealizarEntrega5","nuevo adeudo " + nuevoAdeudoPendienteConsultar);
        Log.d("RealizarEntrega5","cumplio pago minimo " + cumplioConPagoMinimoConsultar);
        Log.d("RealizarEntrega5","limite credito " + limiteCreditoConsultar);

        String mensaje;
        switch (estadoClienteConsultar){

            case Constant.ACTIVO:


                if(nuevoAdeudoPendienteConsultar > 0 ){
                    //(si el cliente tiene un adeudo pendiente
                    // si cumplio su pago minimo entregamos mercancia pero no cambiamos regalos
                    // si no cumplio su pago minimo no entregamos mercancia ni regalos)

                    if (cumplioConPagoMinimoConsultar){
                        botonNoPuedeRecibirMercanciaCredito.setVisibility(View.GONE);
                        botonNoPuedeCanjearRegalos.setVisibility(View.VISIBLE);
                        prohibidoEntregaMercanciaCredito = false;
                        prohibidoCanjarPuntos = true;
                        mensaje = "Puede recibir mercancia a credito o contado \n-Dejo un adeudo pendiente de $" + nuevoAdeudoPendienteConsultar;
                        tvSubtituloMensaje.setText(mensaje);
                        tvSubtituloMensaje.setTextColor(tvSubtituloMensaje.getResources().getColor(R.color.colorActivo));
                        tvSubtituloMensaje.setVisibility(View.VISIBLE);
                    }else{
                        botonNoPuedeRecibirMercanciaCredito.setVisibility(View.VISIBLE);
                        botonNoPuedeCanjearRegalos.setVisibility(View.VISIBLE);
                        prohibidoEntregaMercanciaCredito = true;
                        prohibidoCanjarPuntos = true;

                        mensaje = "No cumplio con su pago minimo de $"+ pagoMinimoRequeridoConsultar + "\n-La mercancia que captures sera de venta al CONTADO";
                        tvSubtituloMensaje.setText(mensaje);
                        tvSubtituloMensaje.setTextColor(tvSubtituloMensaje.getResources().getColor(R.color.colorLio));
                        tvSubtituloMensaje.setVisibility(View.VISIBLE);
                    }

                }else{
                    //si no tiene ningun saldo pendiente
                    if (limiteCreditoConsultar==0){
                        botonNoPuedeRecibirMercanciaCredito.setVisibility(View.VISIBLE);
                        prohibidoEntregaMercanciaCredito = true;
                        botonNoPuedeCanjearRegalos.setVisibility(View.GONE);
                        prohibidoCanjarPuntos = false;
                        mensaje = "Se ha cancelado el credito del cliente \n -La mercancia que captures sera de venta al CONTADO";
                        tvSubtituloMensaje.setText(mensaje);
                        tvSubtituloMensaje.setTextColor(tvSubtituloMensaje.getResources().getColor(R.color.colorLio));
                        tvSubtituloMensaje.setVisibility(View.VISIBLE);
                    }else{
                        //si no se a cancelado el credito del cliente entonces esta bien y no mostramos ningun boton de anulacion
                        //ni colocoamos prohibicion alguna
                        botonNoPuedeRecibirMercanciaCredito.setVisibility(View.GONE);
                        prohibidoEntregaMercanciaCredito = false;
                        botonNoPuedeCanjearRegalos.setVisibility(View.GONE);
                        prohibidoCanjarPuntos = false;
                        mensaje = "Todo en orden puedes entregar mercancia a:\n-Credito \n-Contado \n-Regalos";
                        tvSubtituloMensaje.setText(mensaje);
                        tvSubtituloMensaje.setTextColor(tvSubtituloMensaje.getResources().getColor(R.color.colorPrimary));
                        tvSubtituloMensaje.setVisibility(View.VISIBLE);
                    }
                }



                break;

            case Constant.REACTIVAR:


                botonNoPuedeRecibirMercanciaCredito.setVisibility(View.GONE);
                botonNoPuedeCanjearRegalos.setVisibility(View.VISIBLE);
                prohibidoEntregaMercanciaCredito = false;
                prohibidoCanjarPuntos = true;
                mensaje = "Puede recibir mercancia a credito o contado \n-Pero no podemos camjear puntos hasta que se reactive y se haga su cierre";
                tvSubtituloMensaje.setText(mensaje);
                tvSubtituloMensaje.setTextColor(tvSubtituloMensaje.getResources().getColor(R.color.colorActivo));
                tvSubtituloMensaje.setVisibility(View.VISIBLE);



                break;

            case Constant.LIO:

                botonNoPuedeRecibirMercanciaCredito.setVisibility(View.VISIBLE);
                botonNoPuedeCanjearRegalos.setVisibility(View.VISIBLE);
                prohibidoEntregaMercanciaCredito = true;
                prohibidoCanjarPuntos = true;
                mensaje = "Es lio no podemos entregar mercancia \n-No podemos camjear puntos hasta que se reactive y se haga su cierre";
                tvSubtituloMensaje.setText(mensaje);
                tvSubtituloMensaje.setTextColor(tvSubtituloMensaje.getResources().getColor(R.color.colorLio));
                tvSubtituloMensaje.setVisibility(View.VISIBLE);


                break;

            case Constant.PROSPECTO:

                botonNoPuedeRecibirMercanciaCredito.setVisibility(View.GONE);
                botonNoPuedeCanjearRegalos.setVisibility(View.GONE);
                prohibidoEntregaMercanciaCredito = false;
                prohibidoCanjarPuntos = false;
                mensaje = "Es un prospecto por favor realiza la entrega de credito o ventas al contado";
                tvSubtituloMensaje.setText(mensaje);
                tvSubtituloMensaje.setTextColor(tvSubtituloMensaje.getResources().getColor(R.color.colorPrimary));
                tvSubtituloMensaje.setVisibility(View.VISIBLE);

                break;
        }
    }








    //https://es.switch-case.com/53695591
    private static void setListViewHeightBasedOnChildren(ListView listView) {
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




}

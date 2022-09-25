package mx.greenmouse.kaliope;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CursorTreeAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RealizarFinalizarMovimiento extends AppCompatActivity {

    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
    Activity activity = this;
    private static BluetoothSocket btsocket_bit;
    private static OutputStream btoutputstream_bit;


    Button
    botonGenerarFirmaVoz,
    botonFinalizarMovimiento,
    botonImprimirMovimiento,
    botonIrAmenu;

    ScrollView scrollViewParent;

    TextView tvMensajeFinalizar;


    boolean seGeneroFirmaDeVoz = false;
    boolean seGuardoMovimiento = false;
    String numeroDeCuentaCliente;


    //VAMOS A VOLCAR TODOS LOS CAMPOS DE LA TABLA DE CLIENTES AQUI!, ESTA VISTA SERA EL TICKET ENTONCES
    //AQUI PONDREMOS TODOS LOS CAMPOS DENTRO DE LA TABLA CLIENTES PARA MOSTRAR LOS QUE QUERAMOS EN EL TICKET

    //DatosClienteDesdeAdministracion

    String  adminNombreCliente = "";
    String  adminTelefonos = "";
    int     adminDiasDeCredito = 0;
    String  adminGradoCliente = "";
    int     adminLimiteCredito = 0;
    String  adminEstadoCliente = "";
    long    adminLatitud = 0;
    long    adminLongitud = 0;
    int     adminAdeudoAnterior = 0;
    int     adminAcargoCliente = 0;
    String  adminAcargoVencimiento = "";
    String  adminHistoriales = "";
    int     adminPuntosDisponibles = 0;
    String  adminReporteVisitaAnterior = "";
    String  adminIndicacionesDesdeAdministracion = "";
    String  adminClientesMercanciaAcargo = "";
    int     adminPagoAnterior1 = 0;
    int     adminPagoAnterior2 = 0;
    int     adminPagoAnterior3 = 0;
    int     adminPagoAnterior4 = 0;
    int     adminPagoAnterior5 = 0;
    String  adminNombreZona = "";

    TextView
    tvAdminNombreCliente,
    tvAdminTelefonos,
    tvAdminDiasDeCredito,
    tvAdminGradoCliente,
    tvAdminLimiteCredito,
    tvAdminEstadoCliente,
    tvAdminLatitud,
    tvAdminLongitud,
    tvAdminAdeudoAnterior,
    tvAdminAcargoCliente,
    tvAdminAcargoVencimiento,
    tvAdminHistoriales,
    tvAdminPuntosDisponibles,
    tvAdminReporteVisitaAnterior,
    tvAdminIndicacionesDesdeAdministracion,
    tvAdminClientesMercanciaAcargo,
    tvAdminPagoAnterior1,
    tvAdminPagoAnterior2,
    tvAdminPagoAnterior3,
    tvAdminPagoAnterior4,
    tvAdminPagoAnterior5,
    tvAdminNombreZona;


    //Clientes
    String  cliEstadoVisita = "";
    int     cliCodigoEstadoFechas = 0;
    int     cliDiasDeVencimientoOfaltantesParaCorte = 0;
    int     cliPrioridadDeVisita = 0;
    String  cliMensajeMostrarPorVisitar = "";

    TextView
    tvCliEstadoVisita,
    tvCliCodigoEstadoFechas,
    tvCliDiasDeVencimientoOfaltantesParaCorte,
    tvCliPrioridadDeVisita,
    tvCliMensajeMostrarPorVisitar;





    //devoluciones
    int     devPiezasDevueltas = 0;
    int     devImporteDevuelto = 0;
    String devDescripcionPiezasDevueltas = "";

    TextView
    tvDevPiezasDevueltas,
    tvDevImporteDevuelto,
    tvDevDescripcionPiezasDevueltas;

    //cierre
    int     cieVentaGenerada = 0;
    int     ciePagoExtraGanarMasPuntos = 0;
    int     ciePuntosGanadosVenta = 0;
    long    cieLatitud = 0;
    long    cieLongitud = 0;
    String  cieFecha = "";

    TextView
    tvCieVentaGenerada,
    tvCiePagoExtraGanarMasPuntos,
    tvCiePuntosGanadosVenta,
    tvCieLatitud,
    tvCieLongitud,
    tvCieFecha;

    //pagos
    int     pagPagoPorVentaCapturado = 0;
    int     pagNuevoAdeudoPorVenta = 0;
    int     pagMinimoParaEntregarMercancia = 0;
    boolean pagCumplioConPagoMinimo = false;
    long    pagLatitud = 0;
    long    pagLongitud = 0;
    String  pagFecha = "";

    TextView
    tvPagPagoPorVentaCapturado,
    tvPagNuevoAdeudoPorVenta,
    tvPagMinimoParaEntregarMercancia,
    tvPagCumplioConPagoMinimo,
    tvPagLatitud,
    tvPagLongitud,
    tvPagFecha;

    //entrega
    int     entCantidadCredito = 0;
    int     entCantidadContado = 0;
    int     entCantidadRegalos = 0;
    int     entImporteCredito = 0;
    int     entImporteContado = 0;
    int     entImporteRegalos = 0;
    String  entDescripcionTodos = "";
    String  entDescripcionCredito = "";
    String  entDescripcionContado = "";
    String  entDescripcionRegalos = "";
    int     entPuntosGanadosContado = 0;
    int     entpuntosTotales = 0;
    int     entPuntosCanjeados = 0;
    int     entPuntosRestantes = 0;
    boolean entProhibidoEntregarMercanciaCredito = false;
    boolean entProhibidoCanjearPuntos = false;
    int     entNuevoCreditoCalculado = 0;
    int     entNuevosDiasDeCreditoCalculados = 0;
    int     entNuevoGradoCalculado = 0;
    String  entfechaInicial = "";
    String  entFechaVencimiento = "";
    int  entDiferenciaExcesoCredito = 0;

    TextView
    tvEntCantidadCredito,
    tvEntCantidadContado,
    tvEntCantidadRegalos,
    tvEntImporteCredito,
    tvEntImporteContado,
    tvEntImporteRegalos,
    tvEntDescripcionTodos,
    tvEntDescripcionCredito,
    tvEntDescripcionContado,
    tvEntDescripcionRegalos,
    tvEntPuntosGanadosContado,
    tvEntpuntosTotales,
    tvEntPuntosCanjeados,
    tvEntPuntosRestantes,
    tvEntProhibidoEntregarMercanciaCredito,
    tvEntProhibidoCanjearPuntos,
    tvEntNuevoCreditoCalculado,
    tvEntNuevosDiasDeCreditoCalculados,
    tvEntNuevoGradoCalculado,
    tvEntfechaInicial,
    tvEntFechaVencimiento,
    tvEntDiferenciaExcesoCredito;

    //pagos2
    int     pa2PagoDiferenciaRegalo = 0;
    int     pa2PagoPorVentaContado = 0;
    int     pa2PagoCapturado = 0;
    long    pa2Latitud = 0;
    long    pa2Longitud = 0;
    String  pa2Fecha = "";

    TextView
    tvPa2PagoDiferenciaRegalo,
    tvPa2PagoPorVentaContado,
    tvPa2PagoCapturado,
    tvPa2Latitud,
    tvPa2Longitud,
    tvPa2Fecha;


    //finalizado
    boolean finEstadoMovimientoFinalizado = false;
    boolean finFirmaDeVoz = false;


    String piezasOrdenadasDevueltas = "";
    String piezasOrdenadasCredito = "";
    String piezasOrdenadasRegalo = "";
    String piezasOrdenadasContado = "";
    int pagosTotalesCapturados = 0;
    TextView tvPagosTotalesCapturados;


    Animation animationLatido;
    MediaPlayer mediaPlayer = new MediaPlayer();
    Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_finalizar_movimiento);
        getSupportActionBar().hide();
        animationLatido = AnimationUtils.loadAnimation(this,R.anim.escala); //cargamos la animacion pruebas de animacion de elementos en la pantalla
        animationLatido.setFillAfter(true);//para que se quede donde termina la anim
        animationLatido.setRepeatMode(Animation.REVERSE); //modo de repeticion, en el reverse se ejecuta la animacion y cuando termine de ejecutarse va  adar reversa
        animationLatido.setRepeatCount(Animation.INFINITE); //cuantas veces queremos que se repita la animacion, podria ser un numero entero 20 para 20 veces por ejemplo
        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);




        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));


        tvAdminNombreCliente                     = (TextView)findViewById(R.id.RealizarFinalizarMovimientoNombreCliente);


        tvDevDescripcionPiezasDevueltas          = (TextView) findViewById(R.id.RealizarFinalizarMovimientoDescripcionPiezasDevueltas);
        tvDevPiezasDevueltas                     = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPiezasDevueltas);
        tvDevImporteDevuelto                     = (TextView) findViewById(R.id.RealizarFinalizarMovimientoImporteDevuelt0);

        tvEntDescripcionCredito          = (TextView) findViewById(R.id.RealizarFinalizarMovimientoDescripcionPiezasEntregadasCredito);
        tvEntCantidadCredito                     = (TextView) findViewById(R.id.RealizarFinalizarMovimientoEntregasCredito);
        tvEntImporteCredito                    = (TextView) findViewById(R.id.RealizarFinalizarMovimientoImporteEntregasCredito);

        tvEntDescripcionRegalos         = (TextView) findViewById(R.id.RealizarFinalizarMovimientoDescripcionPiezasEntregadasRegalos);
        tvEntCantidadRegalos                     = (TextView) findViewById(R.id.RealizarFinalizarMovimientoEntregasRegalos);
        tvEntImporteRegalos                  = (TextView) findViewById(R.id.RealizarFinalizarMovimientoImporteEntregasRegalos);

        tvEntDescripcionContado         = (TextView) findViewById(R.id.RealizarFinalizarMovimientoDescripcionPiezasEntregadasContado);
        tvEntCantidadContado                     = (TextView) findViewById(R.id.RealizarFinalizarMovimientoEntregasContado);
        tvEntImporteContado                  = (TextView) findViewById(R.id.RealizarFinalizarMovimientoImporteEntregasContado);

        tvEntDescripcionCredito          = (TextView) findViewById(R.id.RealizarFinalizarMovimientoDescripcionPiezasEntregadasCredito);
        tvEntCantidadCredito                     = (TextView) findViewById(R.id.RealizarFinalizarMovimientoEntregasCredito);
        tvEntImporteCredito                    = (TextView) findViewById(R.id.RealizarFinalizarMovimientoImporteEntregasCredito);

        tvDevDescripcionPiezasDevueltas          = (TextView) findViewById(R.id.RealizarFinalizarMovimientoDescripcionPiezasDevueltas);
        tvDevPiezasDevueltas                     = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPiezasDevueltas);
        tvDevImporteDevuelto                     = (TextView) findViewById(R.id.RealizarFinalizarMovimientoImporteDevuelt0);


        tvPagPagoPorVentaCapturado               = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPagoPorVenta);
        tvPagNuevoAdeudoPorVenta                 = (TextView) findViewById(R.id.RealizarFinalizarMovimientoNuevoAdeudo);

        tvCieVentaGenerada                       = (TextView) findViewById(R.id.RealizarFinalizarMovimientoVentaGenerada);
        tvPagPagoPorVentaCapturado                       = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPagoPorVenta);
        tvPagNuevoAdeudoPorVenta                       = (TextView) findViewById(R.id.RealizarFinalizarMovimientoNuevoAdeudo);
        tvPagNuevoAdeudoPorVenta                      = (TextView) findViewById(R.id.RealizarFinalizarMovimientoNuevoAdeudo);
        tvPa2PagoDiferenciaRegalo                = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPagoRegalo);
        tvPa2PagoPorVentaContado                     = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPagoContado);
        tvEntDiferenciaExcesoCredito                     = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPagoDifCredito);
        tvPagosTotalesCapturados                     = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPagosTotales);


        tvAdminPuntosDisponibles                = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPuntosAnteriores);
        tvCiePuntosGanadosVenta                = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPuntosGanadosVenta);
        tvEntPuntosGanadosContado                = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPuntosGanadosContado);
        tvEntPuntosCanjeados                = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPuntosCanjeados);
        tvEntPuntosRestantes                = (TextView) findViewById(R.id.RealizarFinalizarMovimientoPuntosRestantes);



        tvMensajeFinalizar                       = (TextView) findViewById(R.id.RealizarFinalizarMovimientoMensaje);

        botonFinalizarMovimiento                 = (Button) findViewById(R.id.RealizarFinalizarMovimientoBotonFinalizarMovimiento);
        botonGenerarFirmaVoz = (Button) findViewById(R.id.RealizarFinalizarMovimientoBotonGenerarFirmaVoz);
        botonImprimirMovimiento = (Button) findViewById(R.id.RealizarFinalizarMovimientoBotonImprimirMovimiento);
        botonIrAmenu = (Button) findViewById(R.id.RealizarFinalizarMovimientoBotonIrMenu);
        scrollViewParent = (ScrollView) findViewById(R.id.RealizarFinalizarMovimientoScrollView);


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            numeroDeCuentaCliente = bundle.getString("NUMERO_CUENTA_ENVIADO");



            consultarDatos();

             cargarVistas();







        }






        botonGenerarFirmaVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearMensajeParaFirmaVoz();
            }
        });


        botonFinalizarMovimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RealizarFinalizarMovimiento.this);
                builder.setTitle("Estas seguro?");
                builder.setMessage("Si finalizas el movimiento ya no podras hacer mas ediciones \n¿Estas seguro?");
                builder.setPositiveButton("Si, Finalizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        guardarDatosClientes();
                    }
                });

                builder.create();
                builder.show();


            }
        });

        botonImprimirMovimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });

        botonIrAmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RealizarFinalizarMovimiento.this,VistaMovimientosClientes.class));
            }
        });






    }



    private void consultarDatos(){
        Cursor datosCliente = dataBaseHelper.clientes_dameClientePorCuentaCliente(numeroDeCuentaCliente);
        if (datosCliente.getCount() > 0) {
            datosCliente.moveToFirst();
            vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);


            /*String*/  adminNombreCliente                   = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE));
            /*String*/  adminTelefonos                       = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_TELEFONO));
            /*int   */  adminDiasDeCredito                   = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_DIAS_CREDITO));
            /*String*/  adminGradoCliente                    = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_GRADO_CLIENTE));
            /*int   */  adminLimiteCredito                   = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CREDITO_CLIENTE));
            /*String*/  adminEstadoCliente                   = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ESTADO_CLIENTE));
            /*long  */  adminLatitud                         = datosCliente.getLong(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_LATITUD_CLIENTE));
            /*long  */  adminLongitud                        = datosCliente.getLong(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_LONGITUD_CLIENTE));
            /*int   */  adminAdeudoAnterior                  = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE));
            /*int   */  adminAcargoCliente                   = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE));
            /*String*/  adminAcargoVencimiento               = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_VENCIMIENTO));
            /*String*/  adminHistoriales                     = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_HISTORIALES));
            /*int   */  adminPuntosDisponibles               = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_PUNTOS_DISPONIBLES));
            /*String*/  adminReporteVisitaAnterior           = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_REPORTE));
            /*String*/  adminIndicacionesDesdeAdministracion = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_INDICACIONES));
            /*String*/  adminClientesMercanciaAcargo         = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_MERCANCIA_ACARGO));
            /*int   */  adminPagoAnterior1                   = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_PAGO_ANTERIOR1));
            /*int   */  adminPagoAnterior2                   = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_PAGO_ANTERIOR2));
            /*int   */  adminPagoAnterior3                   = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_PAGO_ANTERIOR3));
            /*int   */  adminPagoAnterior4                   = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_PAGO_ANTERIOR4));
            /*int   */  adminPagoAnterior5                   = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_PAGO_ANTERIOR5));
            /*String*/  adminNombreZona                      = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_ZONA));


            //Clientes
            /*String*/  cliEstadoVisita                         = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA));
            /*int   */  cliCodigoEstadoFechas                   = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CLI_CODIGO_ESTADO_FECHAS));
            /*int   */  cliDiasDeVencimientoOfaltantesParaCorte = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE));
            /*int   */  cliPrioridadDeVisita                    = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CLI_PRIORIDAD_DE_VISITA));
            /*String*/  cliMensajeMostrarPorVisitar             = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CLI_MENSAJE_MOSTRAR_POR_VISITAR));

            //devoluciones
            /*int   */  devPiezasDevueltas                      = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_DEV_CANTIDAD_PIEZAS_DEVUELTAS));
            /*int   */  devImporteDevuelto                      = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_DEV_IMPORTE_DEVUELTO));
            /*String*/  devDescripcionPiezasDevueltas           = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS));

            //cierre
            /*int   */  cieVentaGenerada                        = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CIE_VENTA_GENERADA_LLENAR));
            /*int   */  ciePagoExtraGanarMasPuntos              = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CIE_PAGO_EXTRA_PARA_GANAR_PUNTOS_LLENAR));
            /*int   */  ciePuntosGanadosVenta                   = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CIE_PUNTOS_GANADOS_VENTA_LLENAR));
            /*long  */  cieLatitud                              = datosCliente.getLong(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CIE_LATITUD));
            /*long  */  cieLongitud                             = datosCliente.getLong(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CIE_LONGITUD));
            /*String*/  cieFecha                                = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_CIE_HORA));

            //pagos
            /*int   */  pagPagoPorVentaCapturado                = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_PAGO_POR_VENTA_CAPTURADO));
            /*int   */  pagNuevoAdeudoPorVenta                  = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA));
            /*int   */  pagMinimoParaEntregarMercancia          = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_PAGO_MINIMO_REQUERIDO_PARA_ENTREGA_MERCANCIA));
            /*boolean*/ pagCumplioConPagoMinimo                 = (datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_CUMPLIO_CON_PAGO_MINIMO)) == 1);
            /*long  */  pagLatitud                              = datosCliente.getLong(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_LATITUD));
            /*long  */  pagLongitud                             = datosCliente.getLong(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_LONGITUD));
            /*String*/  pagFecha                                = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_HORA));

            //entrega
            /*int    */ entCantidadCredito                      = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_CANTIDAD_CREDITO));
            /*int    */ entCantidadContado                      = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_CANTIDAD_CONTADO));
            /*int    */ entCantidadRegalos                      = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_CANTIDAD_REGALOS));
            /*int    */ entImporteCredito                       = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_IMPORTE_CREDITO));
            /*int    */ entImporteContado                       = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_IMPORTE_CONTADO));
            /*int    */ entImporteRegalos                       = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_IMPORTE_REGALOS));
            /*String */ entDescripcionTodos                     = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA));
            /*String */ entDescripcionCredito                   = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA_CREDITO));
            /*String */ entDescripcionContado                   = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA_CONTADO));
            /*String */ entDescripcionRegalos                   = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_DESCRIPCION_ENTREGA_REGALOS));
            /*int    */ entPuntosGanadosContado                 = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_PUNTOS_GANADOS_CONTADO));
            /*int    */ entpuntosTotales                        = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_PUNTOS_TOTALES));
            /*int    */ entPuntosCanjeados                      = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_PUNTOS_CANJEADOS));
            /*int    */ entPuntosRestantes                      = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_PUNTOS_RESTANTES));
            /*boolean*/ entProhibidoEntregarMercanciaCredito    = (datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_PROHIBIDO_ENTREGAR_MERCANCIA_CREDITO)) ==1);
            /*boolean*/ entProhibidoCanjearPuntos               = (datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_PROHIBIDO_CANJEAR_PUNTOS)) ==1);
            /*int    */ entNuevoCreditoCalculado                = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_NUEVO_CREDITO_CALCULADO));
            /*int    */ entNuevosDiasDeCreditoCalculados        = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_NUEVOS_DIAS_DE_CREDITO));
            /*int    */ entNuevoGradoCalculado                  = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_NUEVOS_GRADO_CALCULADO));
            /*String */ entfechaInicial                         = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_FECHA_INICIAL));
            /*String */ entFechaVencimiento                     = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_FECHA_VENCIMIENTO));
            /*String */ entDiferenciaExcesoCredito              = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_DIFERENCIA_EXCESO_CREDITO));

            //pagos2
            /*int   */  pa2PagoDiferenciaRegalo                 = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PA2_PAGO_DIFERENCIA_REGALO));
            /*int   */  pa2PagoPorVentaContado                  = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PA2_PAGO_POR_VENTA_CONTADO));
            /*int   */  pa2PagoCapturado                        = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PA2_PAGO_CAPTURADO));
            /*long  */  pa2Latitud                              = datosCliente.getLong(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PA2_LATITUD));
            /*long  */  pa2Longitud                             = datosCliente.getLong(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PA2_LONGITUD));
            /*String*/  pa2Fecha                                = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PA2_HORA));


            /*boolean*/ seGuardoMovimiento           = (datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_FIN_FINALIZADO)) ==1);
            /*boolean*/ seGeneroFirmaDeVoz           = (datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_FIN_FIRMA_DE_VOZ)) ==1);

                       String lat  = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_FIN_LATITUD));
                       String lon  = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_FIN_LONGITUD));
            //Toast.makeText(activity, "latitud en db: " + lat + "\n longitud en db " + lon, Toast.LENGTH_LONG).show();



            //Como ya estamos consultando los datos de las piezas devueltas ya no de la tabla de
            //devoluciones sino del String con formato json que esta guardado en la tabla clientes
            //lo sacamos y lo ordenamos para mostrarlo en un textview y ya no usar un listView
            //y crear adaptador para mostrarlo y ese show
            piezasOrdenadasDevueltas = "";
            try {
                Log.i("desc_devueltas_json", devDescripcionPiezasDevueltas);
                JSONArray jsonArray = new JSONArray(devDescripcionPiezasDevueltas);
                for (int i = 0; i<jsonArray.length();i++){
                    String cantidad = jsonArray.getJSONObject(i).getString(DataBaseHelper.DEVOLUCION_MERCANCIA_CANTIDAD);
                    String precio = jsonArray.getJSONObject(i).getString(DataBaseHelper.DEVOLUCION_MERCANCIA_PRECIO);
                    piezasOrdenadasDevueltas += cantidad + "     " + precio + "\n";
                }
                Log.i("desc_devueltas_ord", piezasOrdenadasDevueltas);
            } catch (JSONException e) {
                e.printStackTrace();
            }




            piezasOrdenadasCredito = "Grado Entrega: " + adminGradoCliente +
                    "\n\nCant Preci Dis Gan\n";
            try {
                Log.i("desc_credito_json", entDescripcionCredito);
                JSONArray jsonArray = new JSONArray(entDescripcionCredito);
                for (int i = 0; i<jsonArray.length();i++){
                    String cantidad = jsonArray.getJSONObject(i).getString(DataBaseHelper.ENTREGA_MERCANCIA_CANTIDAD);
                    String precio = jsonArray.getJSONObject(i).getString(DataBaseHelper.ENTREGA_MERCANCIA_PRECIO);
                    String distribucion = jsonArray.getJSONObject(i).getString(DataBaseHelper.ENTREGA_MERCANCIA_DISTRIBUCION);
                    String ganancia = jsonArray.getJSONObject(i).getString(DataBaseHelper.ENTREGA_MERCANCIA_GANANCIA);
                    piezasOrdenadasCredito += cantidad + "   " + precio +  "  " + distribucion + " " + ganancia +"\n";
                }
                Log.i("desc_credito_ord", piezasOrdenadasCredito);
            } catch (JSONException e) {
                e.printStackTrace();
            }



           piezasOrdenadasRegalo = "Cant Venta Ganancia\n";
            try {
                Log.i("desc_regalo_json", entDescripcionRegalos);
                JSONArray jsonArray = new JSONArray(entDescripcionRegalos);
                for (int i = 0; i<jsonArray.length();i++){
                    String cantidad = jsonArray.getJSONObject(i).getString(DataBaseHelper.ENTREGA_MERCANCIA_CANTIDAD);
                    String precio = jsonArray.getJSONObject(i).getString(DataBaseHelper.ENTREGA_MERCANCIA_PRECIO);
                    String ganancia = jsonArray.getJSONObject(i).getString(DataBaseHelper.ENTREGA_MERCANCIA_GANANCIA);
                    piezasOrdenadasRegalo += cantidad + "  " + precio +  "  " + ganancia +"\n";
                }
                Log.i("desc_regalo_ord", piezasOrdenadasRegalo);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            piezasOrdenadasContado = "Cant Venta Ganancia Grado\n";
            try {
                Log.i("desc_contado_json", entDescripcionContado);
                JSONArray jsonArray = new JSONArray(entDescripcionContado);
                for (int i = 0; i<jsonArray.length();i++){
                    String cantidad = jsonArray.getJSONObject(i).getString(DataBaseHelper.ENTREGA_MERCANCIA_CANTIDAD);
                    String precio = jsonArray.getJSONObject(i).getString(DataBaseHelper.ENTREGA_MERCANCIA_PRECIO);
                    String ganancia = jsonArray.getJSONObject(i).getString(DataBaseHelper.ENTREGA_MERCANCIA_GANANCIA);
                    String grado = jsonArray.getJSONObject(i).getString(DataBaseHelper.ENTREGA_MERCANCIA_GRADO_DE_ENTREGA);
                    piezasOrdenadasContado += cantidad + "    " + precio +  "   " + ganancia + "  " + grado.substring(0,3) + "\n";
                }
                Log.i("desc_contado_ord", piezasOrdenadasContado);
            } catch (JSONException e) {
                e.printStackTrace();
            }







            pagosTotalesCapturados = pagPagoPorVentaCapturado + pa2PagoCapturado;




            //(si la ap se detiene entre el whats app y la app kaliope es decir se preciono generar firma de voz y se detiene
            // cuando se vuelva a abrir y el agente llege hasta la parte de firma de voz la app
            // ya le permite continuar porque las variables se quedaron guardadas, pero obvio npo se guardo el movimiento
            // corremos el riesgo que el agente se de cuenta de esto y modifique antes de llegar a la firma. para evitarlo
            // checaremos si el movimiento no se guardo, si no se guardo entonces la firma de voz aunque de la base de datos se recuperare
            // en true la cambiamos a false.
            // lo puedes emular si generas la firma y cierras la app del multitareas)



            if (!seGuardoMovimiento){
                seGeneroFirmaDeVoz = false;
            }





        }
    }

    private void cargarVistas (){





        tvAdminNombreCliente.setText(adminNombreCliente);




        tvEntCantidadCredito.setText(String.valueOf(entCantidadCredito));
        tvPa2PagoDiferenciaRegalo.setText(String.valueOf(entCantidadContado));







        tvDevDescripcionPiezasDevueltas.setText(piezasOrdenadasDevueltas);
        tvDevPiezasDevueltas.setText(String.valueOf(devPiezasDevueltas));
        tvDevImporteDevuelto.setText(String.valueOf(devImporteDevuelto));





        tvEntDescripcionCredito.setText(piezasOrdenadasCredito);
        tvEntCantidadCredito.setText(String.valueOf(entCantidadCredito));
        tvEntImporteCredito.setText(String.valueOf(entImporteCredito));

        tvEntDescripcionRegalos.setText(piezasOrdenadasRegalo);
        tvEntCantidadRegalos.setText(String.valueOf(entCantidadRegalos));
        tvEntImporteRegalos.setText(String.valueOf(entImporteRegalos));

        tvEntDescripcionContado.setText(piezasOrdenadasContado);
        tvEntCantidadContado.setText(String.valueOf(entCantidadContado));
        tvEntImporteContado.setText(String.valueOf(entImporteContado));


        tvCieVentaGenerada.setText(String.valueOf(cieVentaGenerada));
        tvPagPagoPorVentaCapturado.setText(String.valueOf(pagPagoPorVentaCapturado));
        tvPagNuevoAdeudoPorVenta.setText(String.valueOf(pagNuevoAdeudoPorVenta));

        tvPa2PagoDiferenciaRegalo.setText(String.valueOf(pa2PagoDiferenciaRegalo));
        tvPa2PagoPorVentaContado.setText(String.valueOf(pa2PagoPorVentaContado));
        tvEntDiferenciaExcesoCredito.setText(String.valueOf(entDiferenciaExcesoCredito));

        tvPagosTotalesCapturados.setText(String.valueOf(pagosTotalesCapturados));

        tvAdminPuntosDisponibles.setText(String.valueOf(adminPuntosDisponibles));
        tvCiePuntosGanadosVenta.setText(String.valueOf(ciePuntosGanadosVenta));
        tvEntPuntosGanadosContado.setText(String.valueOf(entPuntosGanadosContado));
        tvEntPuntosCanjeados.setText(String.valueOf(entPuntosCanjeados));
        tvEntPuntosRestantes.setText(String.valueOf(entPuntosRestantes));







        Log.d("Finalizar1","seGeneroFirmaDeVoz" + seGeneroFirmaDeVoz);
        Log.d("Finalizar1","seGuardoMovimiento" + seGuardoMovimiento);
        if (!seGeneroFirmaDeVoz){
            botonGenerarFirmaVoz.setVisibility(View.VISIBLE);
            botonImprimirMovimiento.setVisibility(View.GONE);
            botonFinalizarMovimiento.setVisibility(View.GONE);
            botonFinalizarMovimiento.clearAnimation();
            botonIrAmenu.setVisibility(View.GONE);
            tvMensajeFinalizar.setText("Para finalizar, el cliente tiene que generarte la firma de voz");
            botonGenerarFirmaVoz.setText("Generar firma de Voz");

        }else{
            botonFinalizarMovimiento.setVisibility(View.VISIBLE);
            botonFinalizarMovimiento.startAnimation(animationLatido);
            tvMensajeFinalizar.setText("Se ha generado la firma \n\n Si quieres imprimir el ticket debes guardar el movimiento");
            botonGenerarFirmaVoz.setText("Volver a generar firma de voz");
            botonGenerarFirmaVoz.setBackgroundResource(R.drawable.border_gris);
            botonImprimirMovimiento.setVisibility(View.GONE);
            botonIrAmenu.setVisibility(View.GONE);


            if (seGuardoMovimiento){
                botonGenerarFirmaVoz.setVisibility(View.GONE);
                botonFinalizarMovimiento.setVisibility(View.GONE);
                botonFinalizarMovimiento.clearAnimation();
                botonImprimirMovimiento.setVisibility(View.VISIBLE);
                botonIrAmenu.setVisibility(View.VISIBLE);
                scrollViewParent.setBackgroundResource(R.color.colorVisitado);
                tvMensajeFinalizar.setText("Se ha guardado el movimiento ya no puedes hacer mas ediciones");

            }else{
                //si no se ha guardado el movimiento elimianmos
            }
        }



    }


    @Override
    public void onBackPressed() {
        if(seGuardoMovimiento){
            Toast.makeText(this, "Se ha finalizado el movimiento ya no puede volver", Toast.LENGTH_SHORT).show();
            scrollViewParent.scrollTo(0,0);
            botonIrAmenu.startAnimation(animationLatido);
            mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.error);
            mediaPlayer.start();
            vibrator.vibrate(100);
        }else{

            //si vuelve a la pantalla anterior mientras el movimiento aun no este finalizado eliminaremos los datos que se pudieron haber guardado
            //porque si ya guardamos se genero firma de movimiento al regresar y volver aqui el agente pudo editar algo y la firma de voz ya estaria validada
            //de esta manera si regresa tendra que volver a firmar
            if(seGeneroFirmaDeVoz){
                alertaSeguroRegresar();

            }else{
                eliminarDatosClientes();
                super.onBackPressed();
            }

        }
    }

    private void alertaSeguroRegresar (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aviso");
        builder.setMessage("Si vuelves a la pantalla anterior tendras que volver a generar la firma de voz");
        builder.setPositiveButton("Si, regresar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                seGeneroFirmaDeVoz = false;
                onBackPressed();
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



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            //si recibimos el foco
            cargarVistas();
            //Toast.makeText(this, "Recibo focus", Toast.LENGTH_SHORT).show();
        }
    }

    private void crearMensajeParaFirmaVoz(){

        seGeneroFirmaDeVoz = true;
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_FIN_FIRMA_DE_VOZ, seGeneroFirmaDeVoz); //lo guardamos porque ocurria un error si depronto se cerraba la app, ya asi al cargar denuevo funciona
        contentValues.put(DataBaseHelper.CLIENTES_FIN_HORA,utilidadesApp.dameHora());
        contentValues.put(DataBaseHelper.CLIENTES_FIN_LATITUD,Constant.INSTANCE_LATITUDE);
        contentValues.put(DataBaseHelper.CLIENTES_FIN_LONGITUD,Constant.INSTANCE_LONGITUDE);
        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);


        llamarWhatsApp(crearMensaje());


        //solo guardamos las coordenadas porque por ejemplo cuando se envia el mensaje de respaldo por medio del whatsapp
        //aun no se an guardado coordenadas en la tabla, entonces el mensaje llega con coordenadas 0
        //las coordenadas se guardan hasta que se guarda el movimiento. entonces este metodo lo llamamos
        //cuando se genere la firma de voz
    }

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

    private void guardarDatosClientes (){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_FIN_FINALIZADO,true);//si es ture guarda 1
        contentValues.put(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA,Constant.ESTADO_VISITADO);//movemos el cleinte a clientes visitados
        contentValues.put(DataBaseHelper.CLIENTES_FIN_FIRMA_DE_VOZ,true);// si es true guarda 1
        contentValues.put(DataBaseHelper.CLIENTES_FIN_HORA,utilidadesApp.dameHora());
        contentValues.put(DataBaseHelper.CLIENTES_FIN_LATITUD,Constant.INSTANCE_LATITUDE);
        contentValues.put(DataBaseHelper.CLIENTES_FIN_LONGITUD,Constant.INSTANCE_LONGITUDE);
        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);

        //AFECTAMOS INVENTARIOS

        dataBaseHelper.devolucion_afectaInventarioConDevoluciones(numeroDeCuentaCliente);
        dataBaseHelper.entrega_afectaInventarioConSalidas(numeroDeCuentaCliente);


        //LIMPIAMOS LAS TABLAS QUE ALMACENAN EL PRODUCTO DE DEVOLUCION Y ENTREGA DE LOS PRODUCTOS DEL CLIENTE
        //PORQUE APARTIR DE AQUI TODA LA DESCRIPCION DE LA MERCANCIA TANTO PARA EL TIKET COMO PARA ENVIARLO AL SERVIDOR
        //SE TOMARA DEL JSON ARRAY ALMACENADO EN LA TABLA CLIENTES
        dataBaseHelper.devolucion_mercnacia_eliminarDevolucion(numeroDeCuentaCliente);
        dataBaseHelper.entrega_mercnacia_eliminarEntrega(numeroDeCuentaCliente);

        seGuardoMovimiento = true;
        Constant.ULTIMOS_DATOS_SINCRONIZADOS = false;


        //SI EL CLIENTE ES NUEVO RECIEN INGRESADO ACTUALIZAMOS ESE NUMERO DE CUENTA AL FINALIZAR
        try {
            int numeroCuenta = Integer.valueOf(numeroDeCuentaCliente);
            if (numeroCuenta>=10 && numeroCuenta<=30){
                dataBaseHelper.actualizaNumerosCuenta(numeroCuenta);

                //para que al finalizar el movmiento el cliente nuevo ya se represente en el mapa
                ContentValues coordenadas = new ContentValues();
                coordenadas.put(DataBaseHelper.CLIENTES_ADMIN_LATITUD_CLIENTE,Constant.INSTANCE_LATITUDE);
                coordenadas.put(DataBaseHelper.CLIENTES_ADMIN_LONGITUD_CLIENTE,Constant.INSTANCE_LONGITUDE);
                dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(coordenadas,numeroDeCuentaCliente);


            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


    }

    private void eliminarDatosClientes (){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CLIENTES_FIN_FINALIZADO,false);//si es ture guarda 1
        contentValues.put(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA,Constant.ESTADO_VISITAR);//movemos el cleinte a Visitar
        contentValues.put(DataBaseHelper.CLIENTES_FIN_FIRMA_DE_VOZ,false);// si es true guarda 1
        contentValues.put(DataBaseHelper.CLIENTES_FIN_HORA,"");
        contentValues.put(DataBaseHelper.CLIENTES_FIN_LATITUD,"0");
        contentValues.put(DataBaseHelper.CLIENTES_FIN_LONGITUD,"0");
        dataBaseHelper.clientes_actualizaTablaClientesPorNumeroCuenta(contentValues,numeroDeCuentaCliente);
    }



    public String crearMensaje (){

        Log.i("en crear mensaje","1");
        String mensajeDevueltas = "";
        String mensajeEntregadasCredito = "";
        String mensajeEntregdasContado = "";
        String mensajePago= "";
        String mensajePago2= "";
        String mensajePagoTotal = "";
        String mensajePuntos = "";
        String mensajeSaldo = "";
        String nombre = adminNombreCliente;
        String piezaOpiezasDevueltas = "pieza";
        String piezaOpiezasEntregadas = "pieza";
        String inicioMensaje ="";

        boolean hayInicio = false;


        if (devPiezasDevueltas > 1){
            piezaOpiezasDevueltas = "piezas";
        }
        if (entCantidadCredito > 1){
            piezaOpiezasEntregadas = "piezas";
        }


        if (devPiezasDevueltas!= 0){

            if (!hayInicio) {
                inicioMensaje = "Yo "+ nombre +" entrego a " + ConfiguracionesApp.getNombreCorto(activity)+ "\n";
                hayInicio= true;
            }

            mensajeDevueltas = devPiezasDevueltas +" "+piezaOpiezasDevueltas+ " como devolucion\n";
        }





        if (entCantidadCredito!= 0){

            if (!hayInicio){
                inicioMensaje = "Yo "+ nombre +" recibo de "+ ConfiguracionesApp.getNombreCorto(activity)+ "\n";
                hayInicio= true;
                mensajeEntregadasCredito = entCantidadCredito +" " +piezaOpiezasEntregadas + " para venta\n";
            }else{
                mensajeEntregadasCredito = "recibo " + entCantidadCredito + " " + piezaOpiezasEntregadas + " para venta\n";

            }

        }



        if (entCantidadContado!= 0){

            String piezaOpiezaC  = entCantidadContado > 1 ? " piezas": " pieza";

            if (!hayInicio){
                inicioMensaje = "Yo "+ nombre +" hago una compra de contado a "+ ConfiguracionesApp.getNombreCorto(activity)+ "\n";
                hayInicio= true;
                mensajeEntregdasContado =  "por " + entCantidadContado +" " + piezaOpiezaC + " \n";
            }

            else
                mensajeEntregdasContado = "hago una compra de contado por " + entCantidadContado + " " + piezaOpiezaC + " \n";

        }





        if (pagosTotalesCapturados>0){
            if (!hayInicio){
                inicioMensaje = "Yo "+ nombre +" entrego a "+ConfiguracionesApp.getNombreCorto(activity)+ "\n";
                hayInicio= true;
                mensajePagoTotal = "un pago total de " + pagosTotalesCapturados + " pesos \n";
            }else{
                mensajePagoTotal = "\nRealizo un pago total de " + pagosTotalesCapturados + " pesos \n";

            }

        }


        if (pagPagoPorVentaCapturado != 0){
            //si se entergo un pago pero ni devolucion ni entrega han creado el inicio del mensaje, es decir que no se recibio ni devolucion ni entrega
            //pago crea el inicio con la palabra Entrego
            if (!hayInicio){
                inicioMensaje = "Yo "+ nombre +" entrego a "+ConfiguracionesApp.getNombreCorto(activity)+ "\n";
                hayInicio= true;
                mensajePago =  "un pago total de " + "$" + pagPagoPorVentaCapturado + "\n" ;
            }
            //si ya devolucion o entrega crearon el mensaje complementa con esta frase
            else{
                mensajePago =  " $"+ pagPagoPorVentaCapturado + " por venta\n" ;

            }
        }


        if (pa2PagoCapturado != 0){

            if (!hayInicio){
                inicioMensaje = "Yo "+ nombre +" entrego a "+ConfiguracionesApp.getNombreCorto(activity)+ " un pago de\n";
                hayInicio= true;

                if(pa2PagoDiferenciaRegalo > 0){
                    mensajePago2 +=  " $" + pa2PagoDiferenciaRegalo + " por diferencia de regalo,\n" ;
                }
                if(pa2PagoPorVentaContado > 0){
                    mensajePago2 +=  " $" + pa2PagoPorVentaContado + " por compra de contado,\n" ;
                }
                if(entDiferenciaExcesoCredito>0){
                    mensajePago2 +=  " $" + entDiferenciaExcesoCredito + " por exceso de credito\n" ;
                }
            }
            //si ya devolucion o entrega crearon el mensaje complementa con esta frase
            else{

                if(pa2PagoDiferenciaRegalo > 0){
                    mensajePago2 +=  "$" + pa2PagoDiferenciaRegalo + " por diferencia de regalo,\n" ;
                }
                if(pa2PagoPorVentaContado > 0){
                    mensajePago2 +=  "$" + pa2PagoPorVentaContado + " por compra de contado,\n" ;
                }
                 if(entDiferenciaExcesoCredito>0){
                    mensajePago2 +=  "$" + entDiferenciaExcesoCredito + " por exceso de credito,\n" ;
                }
            }

        }











        if (entImporteRegalos!=0){
            String piezaOpiezaReg  = entCantidadRegalos > 1 ? " regalos": " regalo";

            if (!hayInicio){
                inicioMensaje = "\nYo "+ nombre +" hago un canje de \n";
                hayInicio= true;
                mensajePuntos =  entImporteRegalos + " puntos, y recibo "  + entCantidadRegalos + piezaOpiezaReg + " me quedan " + entPuntosRestantes + "puntos en mi banco \n";

            }
            else
                mensajePuntos = "\nHago un canje de " + entImporteRegalos + " puntos, y recibo " + entCantidadRegalos + piezaOpiezaReg + " \n\nMe quedan " + entPuntosRestantes + " puntos en mi banco \n";

        }









        if (pagNuevoAdeudoPorVenta>0){
            //si devolucion o entrega o pagos o puntos no han generado el inicio de mensaje es decir que solo se captura saldo pendiente
            //entonces Saldo lo crea con el texto debo a kaliope
            if (!hayInicio){
                inicioMensaje = "Yo "+ nombre +" quedo a deber a Kaliope ";
                //hayInicio = true;  ya no pinemos hay inicio porque nadie mas lo vuelve a ocupar saldo es lo ultimo que se imprime
                mensajeSaldo = "$"+ pagNuevoAdeudoPorVenta + " porque .... \n";
            }
            //si devolucion o entrega o pagos o puntos ya generaron el inicio de mensaje se completa con esta frase
            else
                mensajeSaldo = "y quedo a deber " + "$"+ pagNuevoAdeudoPorVenta + " porque .... \n";
        }else{
            //si el adeudo nuevo esta en negativo es decir que se genero un pago a favor
            if (!hayInicio){
                inicioMensaje = "Yo "+ nombre +" doy un pago a favor de ";
                //hayInicio = true;  ya no pinemos hay inicio porque nadie mas lo vuelve a ocupar saldo es lo ultimo que se imprime
                mensajeSaldo = "$"+ (pagNuevoAdeudoPorVenta * -1) + " se me abonara para mi proximo cierre \n";
            }
            else if (pagNuevoAdeudoPorVenta !=0 ){
                mensajeSaldo = "\nMe queda un saldo a favor de " + "$"+ (pagNuevoAdeudoPorVenta * -1) + " se me abonara para mi proximo cierre\n";
                }
        }

        Log.i("en crear mensaje","2");




        //para enviar los mensajes por whats app lo mas facil que yo pense era consultar toda la base de datos de los clientes activos
        //(y de los cuales yua se finalizo el movimiento, de tal forma que nos retorne un json array con todos los objetos clientes dentro de el
        // eso funcionara en la parte donde se entregan los datos directo por la app y no por el whats app porque?
        // porque cuando envias la info por whats app el movimiento del cliente actual aun no esta finalizado ni guardado
        // no tengo manera de en cuando se genera la firma de voz consultar todos los clientes incluido este y devolverlo en un json array
        // entonces para eso armare el json array aqui,
        // Consultando primero los clientes que estan finalizados ordenados por hora de finalizacion,
        //despues usamos los numeros de cuenta de ese cursor para entregarselos al meotdo que consulta el jsonObjet por numero de cuenta
        //y le vamos metiendo cada objeto devuelto al array,
        //al final consultamos el json objet del cliente actual y lo metemos al array

        //cosnultamos los numeros de cuenta de todos los movimientos finalizados ordenados por hora de finalizacion
//        Cursor clientesFinalizados = dataBaseHelper.clientes_consultarClientesPorMovimientoFinalizado(true);
//        clientesFinalizados.moveToFirst();
//        JSONArray jsonArrayTodos = new JSONArray();
//        if (clientesFinalizados.getCount()>0){
//            do{
//
//                jsonArrayTodos.put(dataBaseHelper.clientes_dameMovimientosClienteJsonObjetPorNumeroCuenta(clientesFinalizados.getString(clientesFinalizados.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE))));
//
//
//
//                //esto me ayudara a entender futuros usos de getPosition,
//                //get coutn nos dice cual es el tamaño del cursor por ejemplo 5
//                //es decir tiene 5 datos adentro
//                //pero funciona como un array exctamente igual el inician desde 0 entonces tienen de 0 a 4 indices
//                //y el get position es lo que nos dice, en que posicion esta el cursor, si lo movemos con moveToNext
//                //podremos saber en que posicion esta el cursor, es util por ejemplo si necesito hacer algo en especifico como añadir una coma
//                //cuando comienza el primer valor y no añadirla en el ultimo valor
//                Log.i("conteoClientesFinali","Conteo clientes finalizados " + clientesFinalizados.getCount());
//                Log.i("conteoClientesFinali","Posicion clientes finalizados " + clientesFinalizados.getPosition());
//                if (clientesFinalizados.getPosition()<clientesFinalizados.getCount()){
//                    Log.i("conteoClientesFinali","Hola me active " + clientesFinalizados.getCount());
//                }
//            }while (clientesFinalizados.moveToNext());
//        }




        //ya que le colocamos al array todos los datos de los clientes ya finlizados ahora le ponemos el dato
        //del cliente en edicion actual que aun no estara como finalizado
        //jsonArrayTodos.put(dataBaseHelper.clientes_dameMovimientosClienteJsonObjetPorNumeroCuenta(numeroDeCuentaCliente));







        String mensajeFinal =
                inicioMensaje + " " + mensajeDevueltas + mensajeEntregadasCredito + mensajeEntregdasContado + mensajePagoTotal + mensajePago + mensajePago2   + mensajePuntos + mensajeSaldo +
                "\n\n\n\n\n\n\n\n\n"+
                        "[" + dataBaseHelper.clientes_dameMovimientosClienteJsonObjetPorNumeroCuenta(numeroDeCuentaCliente) +"]";



        utilidadesApp.ponerEnPortapapeles(mensajeFinal,activity);
        return mensajeFinal;




    }//fin de metodo crear mensaje






    //METODOS PARA IMPRIMIR

    /** Metodos para imprimir*/


    protected void connect() {


        if(btsocket_bit == null){
            Intent BTIntent = new Intent(this, DeviceList.class);
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

                //btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                //printText("A Hola1235 \n");
                //btoutputstream_bit.write(PrinterCommands.SELECT_FONT_B);
                //printText("B Hola1235 \n");
                //btoutputstream_bit.write(PrinterCommands.SELECT_FONT_C);
                //printText("C Hola1235 \n");
                //btoutputstream_bit.write(PrinterCommands.SELECT_FONT_D);
                //printText("D Hola1235 \n");
                //btoutputstream_bit.write(PrinterCommands.SELECT_FONT_E);
                //printText("E Hola1235 \n");
                //btoutputstream_bit.write(PrinterCommands.SELECT_FONT_F);
                //printText("F Hola1235 \n");



                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                printPhoto();








                printNewLine();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_C);
                printText("Comprobante" + "\n");

                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printText("Cuenta: " + numeroDeCuentaCliente + "\n\n" +
                                "Cliente: " + adminNombreCliente + "\n"+
                                "Telefono: " + adminTelefonos + "\n"+
                                "Dias Credito: " + adminDiasDeCredito + "\n"+
                                "Grado: " + adminGradoCliente + "\n"+
                                "Credito: " + adminLimiteCredito + "\n"+
                                "Estado: " + adminEstadoCliente + "\n"+
                                "Ruta: " + adminNombreZona);
                printNewLine();
                printNewLine();


                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText("La atendio el agente Kaliope: \n" + ConfiguracionesApp.getNombreEmpleado(activity).substring(0,16) + "\n" +
                                "Nombre usuario: " + ConfiguracionesApp.getUsuarioIniciado(activity) + "\n" +
                                "Si presenta algun problema\n por favor reportelo \nal telefono:\n");

                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_D);
                printText("712-159-07-29");
                printNewLine();
                printNewLine();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_B);
                btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                printText("Por su seguridad realice\n siempre su FIRMA DE VOZ \n esto nos ayudara a \n brindarle un mejor\n servicio");
                btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();




                if (devPiezasDevueltas>0){
                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_C);
                    printText( "----  Devolucion Realizada  ----\n");

                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("Usted Devolvio: \n" + piezasOrdenadasDevueltas + "\n" +
                            "Pz: " + devPiezasDevueltas + "\n\n"+
                            "total Devuelto: $" + devImporteDevuelto + "\n"+
                            "**********************");
                    printNewLine();
                    printNewLine();
                    printNewLine();
                    printNewLine();
                }


                //si el cliente tenia mercancia acargo
                if(adminAcargoCliente>0){
                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_C);
                    printText( "----  Cierre generado  ----\n");
                    printNewLine();

                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("Usted tenia acargo: $" + adminAcargoCliente + "\n" +
                                    "Devolvio un total de: $" + devImporteDevuelto + "\n" );

                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                    printText("Generando una venta total de: \n");
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_D);
                    printTitle("$" + cieVentaGenerada);

                    if(ciePuntosGanadosVenta>0){
                        btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                        btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                        printText("Por la cual gano: \n"+ ciePuntosGanadosVenta +" puntos \n********************");
                    }else{
                        btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                        btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                        printText("No genero una venta mayor a $500\n lo sentimos no ha ganado puntos\n incremente sus ventas\n" +
                                "para ganar puntos!\n********************");
                    }

                    printNewLine();
                    printNewLine();
                    printNewLine();
                    printNewLine();


                }



                //si el cliente Recibio mercancia a credito
                if(entCantidadCredito>0){
                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_C);
                    printText( "----  Mercancia recibida  ----\n");
                    printNewLine();


                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("Usted recibe mercancia a credito para venta \n" + piezasOrdenadasCredito + "\n" +
                            "Pz: " + entCantidadCredito + "\n"+
                            "Total: $" + entImporteCredito + "\n");

                    printText("Esta mercancia vence el: \n");
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_D);
                    printText(entFechaVencimiento);
                    printNewLine();
                    printNewLine();



                }



                if(entCantidadRegalos>0){
                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);

                    printText("Le entregaron los siguientes\n regalos por puntos: \n\n" + piezasOrdenadasRegalo + "\n" +
                            "Regalos: " + entCantidadRegalos + "\n"+
                            "Total: $" + entImporteRegalos + "\n" );
                    printNewLine();
                    printNewLine();


                }


                if(entCantidadContado>0){
                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);

                    printText("Realizo una compra de contado: \n" + piezasOrdenadasContado + "\n" +
                            "Pz: " + entCantidadContado + "\n"+
                            "Total: $" + entImporteContado + "\n");
                    printNewLine();
                    printNewLine();


                }
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printText("**********************\n");
                printNewLine();
                printNewLine();





                if(pagosTotalesCapturados>0){

                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_C);
                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    printText("----  Pagos realizados  ----\n");
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("Usted entrego los siguientes\n pagos en efectivo:\n");

                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);

                    if(pagPagoPorVentaCapturado > 0){
                        printText("Por venta o adeudo pendiente: \n$" + pagPagoPorVentaCapturado + "\n");
                    }

                    if(pa2PagoDiferenciaRegalo>0){
                        printText("Diferencia regalo: $" + pa2PagoDiferenciaRegalo + "\n");
                    }

                    if(pa2PagoPorVentaContado>0){
                        printText("Compra Contado: $" + pa2PagoPorVentaContado + "\n");
                    }

                    if(entDiferenciaExcesoCredito>0){
                        printText("Dif credito: $" + entDiferenciaExcesoCredito + "\n");
                    }

                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("\nTotal de pagos entregados\n al agente kaliope : " + ConfiguracionesApp.getNombreCorto(activity) + "\n");

                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_F);
                    printText("$" + pagosTotalesCapturados);
                    btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);

                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("\n******************* ");


                }



                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                    btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_C);
                    printText("\n\n----  Saldo pendiente  ----\n\n");

                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                        if(cieVentaGenerada > 0) {
                            printText("Venta generada: $" + cieVentaGenerada + "\n");
                        }


                    if(adminAdeudoAnterior > 0){
                        printText("Adeudo anterior: $" + adminAdeudoAnterior + "\n");
                    }else if (adminAdeudoAnterior<0){
                        //los casos donde hace 15 dias dejaron a favor
                        printText("El cierre pasado abono a favor" + adminAdeudoAnterior + "\n");
                    }

                    printText("Realizo un pago de \n$" + pagPagoPorVentaCapturado + "\n");




                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_D);
                if(pagNuevoAdeudoPorVenta>0){
                        btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                        printText("Nuevo adeudo\n$" + pagNuevoAdeudoPorVenta + "\n");
                        btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);

                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                        printText("Recuerde que al dejar un saldo pendiente no podra realizar cambios de puntos\n");

                    }else if (pagNuevoAdeudoPorVenta<0){
                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_D);
                    btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                        printText("Ha abonado\n a favor$\n" + pagNuevoAdeudoPorVenta + "\n");
                        btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);

                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("Muchas gracias por su pago, cu abono a favor estara disponible para su siguiente corte\n");
                    }else{
                        btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                        printText("Nuevo adeudo \n $" + pagNuevoAdeudoPorVenta + "\n");
                        btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);

                    btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                    printText("Muchas gracias por su pago completo, no ha quedado ningun adeudo pendiente!!\n");
                    }






                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_C);


                printText("*** Banco de puntos ***\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printText("Puntos anteriores:       +" + adminPuntosDisponibles + "\n" +
                                "Puntos ganados:         +" + ciePuntosGanadosVenta + "\n" +
                                "Putos gan. por contado: +" + entPuntosGanadosContado + "\n" +
                                "Puntos canjeados     : (-)" + entPuntosCanjeados + "\n" +
                                "");
                btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                printText("Puntos Restantes     : (=)" + entPuntosRestantes + "\n");
                btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);

                printText("Recuerde que los puntos solo pueden cambiarce en multiplos de 50");


                resetPrint();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();


                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printText("La atendio el agente kaliope:\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_D);
                printText(ConfiguracionesApp.getNombreEmpleado(activity).substring(0,15) + "\n\n");

                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printText("Usuario:");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_D);
                printText(ConfiguracionesApp.getUsuarioIniciado(activity) + "\n\n");

                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_B);
                btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                printText("Si presenta algun problema\n por favor reportelo al telefono:\n\n");

                btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_F);
                printTitle("712-159-07-29");

                printNewLine();
                printNewLine();
                btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printText("Realice siempre \nsu FIRMA DE VOZ eso la protege\n contra posibles\n eventualidaes");
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

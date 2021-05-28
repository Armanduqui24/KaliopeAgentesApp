package mx.greenmouse.kaliope;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ab on 09/04/2017.
 */
public class
DataBaseHelper extends SQLiteOpenHelper {

    public static final String LOG = "DataBaseHelper";

    public static String rutaDeDB="";

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "kaliopeApp.sqlite";


    //DEFINIMOS EL NOMBRE DE LAS TABLAS QUE VAMOS A CREAR
    static final String TABLE_PRICES = "precios";
    static final String TABLA_INVENTARIO = "inventario";
    static final String TABLE_MOVEMENTS = "movimientos";
    static final String TABLA_DETALLES = "detalles";
    static final String TABLA_PAGOS = "pagos";
    static final String TABLA_CLIENTES = "clientes"; //DOCUMENTACION CLIENTES
    static final String TABLA_NUMEROS_CUENTA = "numeroCuenta";
    static final String TABLA_MENSAJES_ADMINISTRACION = "msgAdministracion";
    static final String TABLA_IDENTIFICADOR_ZONA = "identificadorZona";


    private static final String TABLA_ENTREGA_MERCANCIA = "entregaMercancia";
    private static final String TABLA_DEVOLUCION_MERCANCIA = "devolucionMercancia";

    static final String KEY_ID = "_id";
    static final String DATE_UP = "fecha_alta";
    static final String ESTADO_DE_LA_COLUMNA = "estatus";

    static final String INVENTARIO_CODIGO_PRODUCTO = "codigo";


    static final String INVENTARIO_PRECIO_VENTA_PRODUCTO = "costo";
    static final String INVENTARIO_PRECIO_VENDEDORA = "grado_uno";
    static final String INVENTARIO_PRECIO_SOCIA = "grado_dos";
    static final String INVENTARIO_PRECIO_EMPRESARIA = "grado_tres";
    static final String INVENTORY_PRICE = "costo";
    static final String INVENTORY_ONE = "uno";
    static final String INVENTARIO_EXISTENCIAS = "existencias";



    static final String MOVEMENTS_ACCOUNT = "cuenta";
    static final String MOVEMENTS_NAME = "nombre";
    static final String MOVEMENTS_CREDIT_CODE = "codigo";
    static final String MOVEMENTS_REPORT = "reporte";
    static final String MOVEMENTS_EXPIRATION_DATE = "vencimiento";
    
    

    static final String DETALLES_ID_DEL_MOVIMIENTO = "id_mov";
    static final String DETALLES_CANTIDAD = "cantidad";
    static final String DETALLES_PRECIO_PRODUCTO = "precio";
    static final String DETALLES_PRECIO_DISTRIBUCION = "distribucion";
    static final String DETALLES_GANANCIA = "ganancia";
    static final String DETALLES_CODIGO_PRODUCTO = "codigo";
    static final String DETALLES_TIPO_MOVIMIENTO = "tipo";
    static final String DETALLES_LATITUD = "latitud";
    static final String DETALLES_LONGUITUD = "longitude";




    //TABLA ENTREGA MERCANCIA
    static final String ENTREGA_MERCANCIA_ROW_ID = "rowId";
    static final String ENTREGA_MERCANCIA_CUENTA_CLIENTE = "cuentaCliente";
    static final String ENTREGA_MERCANCIA_CANTIDAD = "cantidad";
    static final String ENTREGA_MERCANCIA_PRECIO = "precio";
    static final String ENTREGA_MERCANCIA_DISTRIBUCION = "distribucion";
    static final String ENTREGA_MERCANCIA_GANANCIA = "ganancia";
    static final String ENTREGA_MERCANCIA_IMPORTE_TOTAL_ENTREGA = "importeTotalEntrega";
    static final String ENTREGA_MERCANCIA_CODIGO = "codigo";
    static final String ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO = "tipo";
    static final String ENTREGA_MERCANCIA_GRADO_DE_ENTREGA = "gradoEntrega";
    static final String ENTREGA_MERCANCIA_LATITUD = "latitud";
    static final String ENTREGA_MERCANCIA_LONGUITUD = "longitud";
    static final String ENTREGA_MERCANCIA_HORA_CAPTURA = "horaCaptura";



    //TABLA DEVUELVE MERCANCIA
    static final String DEVOLUCION_MERCANCIA_ROW_ID = "rowId";
    static final String DEVOLUCION_MERCANCIA_CUENTA_CLIENTE = "cuentaCliente";
    static final String DEVOLUCION_MERCANCIA_CANTIDAD = "cantidad";
    static final String DEVOLUCION_MERCANCIA_PRECIO = "precio";
    static final String DEVOLUCION_MERCANCIA_DISTRIBUCION = "distribucion";
    static final String DEVOLUCION_MERCANCIA_GANANCIA = "ganancia";
    static final String DEVOLUCION_MERCANCIA_CODIGO = "codigo";
    static final String DEVOLUCION_MERCANCIA_LATITUD = "latitud";
    static final String DEVOLUCION_MERCANCIA_LONGUITUD = "longitud";
    static final String DEVOLUCION_MERCACIA_HORA_CAPTURA = "horaCaptura";





    static final String PAGOS_IDMOVIMIENTO = "id_mov";
    static final String PAGOS_PAGO = "pago";
    static final String PAGOS_PAGOS_HORA = "hora_pago";
    static final String PAGOS_DIFERENCIA = "diferencia";
    static final String PAGOS_HORA_DIFERENCIA = "hora_diferencia";
    static final String PAGOS_SALDO_PENDIENTE = "saldo_pendiente";
    static final String PAGOS_HORA_SALDO_PENDIENTE = "hora_saldo_pendiente";
    static final String PAGOS_OTRO = "otro";
    static final String PAGOS_HORA_OTRO = "hora_otro";
    static final String PAGOS_ADEUDO = "saldo";
    static final String PAGOS_HORA_ADEUDO = "hora_saldo";
    static final String PAGOS_PUNTOS = "puntos";
    static final String PAGOS_HORA_PUNTOS = "hora_puntos";
    static final String PAGOS_LATITUD = "latitud";
    static final String PAGOS_LONGITUD = "longitude";

    
    
    
    

    //CAMPOS DE LA BASE DE DATOS CLIENTES DOCUMENTACION CLIENTES
    static final String CLIENTES_ADMIN_CUENTA_CLIENTE                   = "cuenta";
    static final String CLIENTES_ADMIN_NOMBRE_CLIENTE                   = "nombre";
    static final String CLIENTES_ADMIN_TELEFONO                         = "telefono";
    static final String CLIENTES_ADMIN_DIAS_CREDITO                     = "dias";
    static final String CLIENTES_ADMIN_GRADO_CLIENTE                    = "grado";
    static final String CLIENTES_ADMIN_CREDITO_CLIENTE                  = "credito";
    static final String CLIENTES_ADMIN_ESTADO_CLIENTE                   = "estado";
    static final String CLIENTES_ADMIN_LATITUD_CLIENTE                  = "latitud";
    static final String CLIENTES_ADMIN_LONGITUD_CLIENTE                 = "longitud";
    static final String CLIENTES_ADMIN_ADEUDO_CLIENTE                   = "adeudo";
    static final String CLIENTES_ADMIN_ACARGO_CLIENTE                   = "acargo";
    static final String CLIENTES_ADMIN_VENCIMIENTO                      = "vencimineto";
    static final String CLIENTES_ADMIN_HISTORIALES                      = "historiales";
    static final String CLIENTES_ADMIN_PUNTOS_DISPONIBLES               = "puntos";
    static final String CLIENTES_ADMIN_REPORTE                          = "reporte";
    static final String CLIENTES_ADMIN_INDICACIONES                     = "indicaciones";
    static final String CLIENTES_ADMIN_MERCANCIA_ACARGO                 = "mercanciaAcargo";
    static final String CLIENTES_ADMIN_PAGO_ANTERIOR1                   = "pagoAnterior1";
    static final String CLIENTES_ADMIN_PAGO_ANTERIOR2                   = "pagoAnterior2";
    static final String CLIENTES_ADMIN_PAGO_ANTERIOR3                   = "pagoAnterior3";
    static final String CLIENTES_ADMIN_PAGO_ANTERIOR4                   = "pagoAnterior4";
    static final String CLIENTES_ADMIN_PAGO_ANTERIOR5                   = "pagoAnterior5";
    static final String CLIENTES_ADMIN_NOMBRE_ZONA                      = "nombreZona";
    static final String CLIENTES_ADMIN_FECHA_DE_CONSULTA                = "fechaDeConsulta";

    //clientes
    static final String CLIENTES_CLI_ESTADO_VISITA = "estadoVisita";      //VISITAR VISITADO REPASO
    static final String CLIENTES_CLI_CODIGO_ESTADO_FECHAS = "estadoFechas";
    static final String CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE = "diasDeVencimientoOFaltantesParaCorte";
    static final String CLIENTES_CLI_PRIORIDAD_DE_VISITA = "prioridadDeVisita";
    static final String CLIENTES_CLI_MENSAJE_MOSTRAR_POR_VISITAR = "mensajeAmostrar";


    //devoluciones
    static final String CLIENTES_DEV_CANTIDAD_PIEZAS_DEVUELTAS = "cantidadPiezasDevueltas";
    static final String CLIENTES_DEV_IMPORTE_DEVUELTO = "importeDevuelto";
    static final String CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS = "descripcionPiezasDevueltas";
    //en este no ponesmos latitud y longitud porque vienen incluidos en la tabla de devoluciones
    //cierre
    static final String CLIENTES_CIE_VENTA_GENERADA_LLENAR = "ventaGenerada";
    static final String CLIENTES_CIE_PAGO_EXTRA_PARA_GANAR_PUNTOS_LLENAR = "pagoExtraParaGanarPuntos";
    static final String CLIENTES_CIE_PUNTOS_GANADOS_VENTA_LLENAR = "puntosGanados";
    static final String CLIENTES_CIE_LATITUD = "latitudCierre";
    static final String CLIENTES_CIE_LONGITUD = "longitudCierre";
    static final String CLIENTES_CIE_HORA = "horaCierre";

    //pagos
    static final String CLIENTES_PAG_PAGO_POR_VENTA_CAPTURADO = "pagoPorVentaCapturado";
    static final String CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA = "nuevoAdeudoPorVenta";
    static final String CLIENTES_PAG_PAGO_MINIMO_REQUERIDO_PARA_ENTREGA_MERCANCIA = "pagoMinimoRequerido";
    static final String CLIENTES_PAG_CUMPLIO_CON_PAGO_MINIMO = "cunplioConPagoMinimo";
    static final String CLIENTES_PAG_LATITUD = "latitudPagos";
    static final String CLIENTES_PAG_LONGITUD = "longitudPagos";
    static final String CLIENTES_PAG_HORA = "horaPagos";

    //entrega
    static final String CLIENTES_ENT_CANTIDAD_CREDITO = "cantidadEntregasCredito";
    static final String CLIENTES_ENT_CANTIDAD_CONTADO = "cantidadEntregasContado";
    static final String CLIENTES_ENT_CANTIDAD_REGALOS = "cantidadEntregasRegalos";
    static final String CLIENTES_ENT_IMPORTE_CREDITO = "cantidadImporteCredito";
    static final String CLIENTES_ENT_IMPORTE_CONTADO = "cantidadImporteContado";
    static final String CLIENTES_ENT_IMPORTE_REGALOS = "cantidadImporteRegalos";
    static final String CLIENTES_ENT_DESCRIPCION_ENTREGA = "descripcionEntrega";
    static final String CLIENTES_ENT_DESCRIPCION_ENTREGA_CREDITO = "descripcionEntregaCredito";
    static final String CLIENTES_ENT_DESCRIPCION_ENTREGA_CONTADO = "descripcionEntregaContado";
    static final String CLIENTES_ENT_DESCRIPCION_ENTREGA_REGALOS = "descripcionEntregaRegalos";
    static final String CLIENTES_ENT_PUNTOS_GANADOS_CONTADO = "puntosGanadosContado";
    static final String CLIENTES_ENT_PUNTOS_TOTALES = "puntosTotales";
    static final String CLIENTES_ENT_PUNTOS_CANJEADOS = "puntosCanjeados";
    static final String CLIENTES_ENT_PUNTOS_RESTANTES = "puntosRestantes";
    static final String CLIENTES_ENT_DIFERENCIA_EXCESO_CREDITO = "difCredito";
    static final String CLIENTES_ENT_PROHIBIDO_ENTREGAR_MERCANCIA_CREDITO = "prohibidoMercanciaCredito";
    static final String CLIENTES_ENT_PROHIBIDO_CANJEAR_PUNTOS = "prohibidoCanjearPuntos";
    static final String CLIENTES_ENT_NUEVO_CREDITO_CALCULADO = "nuevoCredito";
    static final String CLIENTES_ENT_NUEVOS_DIAS_DE_CREDITO = "nuevoDiaCredito";
    static final String CLIENTES_ENT_NUEVOS_GRADO_CALCULADO = "nuevoGrado";
    static final String CLIENTES_ENT_FECHA_INICIAL = "fechaInicial";
    static final String CLIENTES_ENT_FECHA_VENCIMIENTO = "fechaVencimiento";
    //no colocamos ni coordenadas ni fecha porque vienen incluidas en la tabla entregas

    //pagos2
    static final String CLIENTES_PA2_PAGO_DIFERENCIA_REGALO = "pago2DiferenciaRegalo";
    static final String CLIENTES_PA2_PAGO_POR_VENTA_CONTADO = "pago2VentaContado";
    static final String CLIENTES_PA2_PAGO_CAPTURADO = "pago2Capturado";
    static final String CLIENTES_PA2_LATITUD = "latitudPagos2";
    static final String CLIENTES_PA2_LONGITUD = "longitudPagos2";
    static final String CLIENTES_PA2_HORA = "horaPagos2";

    //finalizar
    static final String CLIENTES_FIN_FINALIZADO = "estadoMovimiento";
    static final String CLIENTES_FIN_FIRMA_DE_VOZ = "firmaVoz";
    static final String CLIENTES_FIN_HORA = "horaDeFinalizacion";
    static final String CLIENTES_FIN_LATITUD = "latFinalizacion";
    static final String CLIENTES_FIN_LONGITUD = "lonFinalizacion";







    //Entrega


    //finalizar Movimiento




    //campos de la base de datos numeros de cuenta nuevos
    static final String ESTADO_DEL_NUMERO = "ocupado";       //en el se escribira siel numero que este en el id esta ocupado o disponible
    static final String NUMERO_CUENTA = "numero";



    //campos de mensajes de administracion
    static final String MENSAJES = "mensajes";               //en este campo se guardaran los mensajes
    static final String ID_ZONA = "id_zona";                 //en el se guardara el id de la zona
    static final String COORDENADAS_ZONA = "perimetro";









    private static final String CREATE_TABLE_NUMEROS_CUENTA = "CREATE TABLE "
            + TABLA_NUMEROS_CUENTA + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NUMERO_CUENTA + " INTEGER, "
            + ESTADO_DEL_NUMERO + " BIT)" ;

    private static final String CREATE_TABLE_IDENTIFICADOR_ZONA = "CREATE TABLE "
            + TABLA_IDENTIFICADOR_ZONA + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_ZONA + " TEXT,"
            + COORDENADAS_ZONA + " TEXT)";





    private static final String CREATE_TABLE_MENSAJES_ADMINISTRACION = "CREATE TABLE "
            + TABLA_MENSAJES_ADMINISTRACION + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MENSAJES + " TEXT)";



    private static final String CREATE_TABLE_ENTREGA_MERCANCIA = "CREATE TABLE "
            + TABLA_ENTREGA_MERCANCIA + " ("
            + ENTREGA_MERCANCIA_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ENTREGA_MERCANCIA_CUENTA_CLIENTE + " TEXT DEFAULT '',"
            + ENTREGA_MERCANCIA_CANTIDAD + " INTEGER DEFAULT 0,"
            + ENTREGA_MERCANCIA_PRECIO + " INTEGER DEFAULT 0,"
            + ENTREGA_MERCANCIA_DISTRIBUCION + " INTEGER DEFAULT 0,"
            + ENTREGA_MERCANCIA_GANANCIA + " INTEGER DEFAULT 0,"
            + ENTREGA_MERCANCIA_IMPORTE_TOTAL_ENTREGA + " INTEGER DEFAULT 0,"
            + ENTREGA_MERCANCIA_CODIGO + " TEXT DEFAULT '',"
            + ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO + " TEXT DEFAULT '',"
            + ENTREGA_MERCANCIA_GRADO_DE_ENTREGA + " TEXT DEFAULT '',"
            + ENTREGA_MERCANCIA_LATITUD + " TEXT DEFAULT '0',"
            + ENTREGA_MERCANCIA_LONGUITUD + " TEXT DEFAULT '0',"
            + ENTREGA_MERCANCIA_HORA_CAPTURA + " DATETIME DEFAULT '')";

    private static final String CREATE_TABLE_DEVUELVE_MERCANCIA = "CREATE TABLE "
            + TABLA_DEVOLUCION_MERCANCIA + " ("
            + DEVOLUCION_MERCANCIA_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DEVOLUCION_MERCANCIA_CUENTA_CLIENTE + " TEXT DEFAULT '',"
            + DEVOLUCION_MERCANCIA_CANTIDAD + " INTEGER DEFAULT 0,"
            + DEVOLUCION_MERCANCIA_PRECIO + " INTEGER DEFAULT 0,"
            + DEVOLUCION_MERCANCIA_DISTRIBUCION + " INTEGER DEFAULT 0,"
            + DEVOLUCION_MERCANCIA_GANANCIA + " INTEGER DEFAULT 0,"
            + DEVOLUCION_MERCANCIA_CODIGO + " TEXT DEFAULT '',"
            + DEVOLUCION_MERCANCIA_LATITUD + " TEXT DEFAULT '0',"
            + DEVOLUCION_MERCANCIA_LONGUITUD + " TEXT DEFAULT '0',"
            + DEVOLUCION_MERCACIA_HORA_CAPTURA + " DATETIME DEFAULT '')";




//DOCUMENTACION CLIENTES
    // NOT NULL esta sentencia desencadena un error si al actualizar los datos de la tabla ingresas null al campo
    //o no lo actualizas y se pone null desencadena el evento
    //DEFAULT con DATETIME si lo imprimes con getString y no has puesto valor por default y tampoco has puesto un valor entonces veras null
    //para evitar esto pon DEFAULT 0, o default 'hola', ambas entran
    //en campo TEXT es lo mismo DEFAULT 0 o DEFAULT 'HOLA'
    //en campo INTEGER DEFAULT 1000 o tambien DEFAULT '1000'
    //en campo NUMERIC es lo mismo numeric permite puntos 3500.5 si pones un getInt devuelve solo enteros

    //BOOLEAN DEFAULT '3500.5' mete los 3500.5 igual depende de si lo muestras con getInt getString
    //si le metes DEFAULT 'hola' lo mete si lo muestras string muestra el hola si lo muestras int devuelve 0
    //DEFAULT 'true' Y muestras getString muestra el true si getInt devuelve 0
    //los boolean les puedes meter lo que quieras solo es como una guia de que en ese campo quieres guardar boolean
    //pero realmente en bases de datos no existen datos boolean solo se almacenan como integer 1 o 0
    //si retornas int te devolvera 0 siempre si es algo que no es un numero ya sea null o hayas almacenado un texto
    //la forma correcta de manejarlo es poniendo DEFAULT 0 o 1 cualquiera de los dos valores 0 para falso y 1 para verdadero
    //no importa si lo pones DEFAULT 1 O DEFAULT '1'
    //lo que si ayudan es que si desde otra actividda le mandas una variable tipo boolean este lo almacenara 0 o 1 en automatico
    //si le mandas texto almacena el texto y con el get int no funciona

    //AHORA VEMOS SI UN CAMPO TEXT al enviarle una variable booleana desde otra actividad funciona igual que el bool
    //OO jaja no hacen nada las variables BOOLEAN, el capto tipo TEXT se comporto exactamente igual que el BOOLEAN
    //si pides que te retorne el getInt retorna 1 o 0, si pides getString retorna 1 o 0
    //aunque le mandes una variable boolean desde otra actividad, igual se almacena como 1 o 0 yo crei que si era
    //un campo TEXTO lo guardaria como false o true pero no jaja
    //comienzo a sospechar que el decir que tipo de campo sera es solo para guia del programador
    //asi pues entiendo que si el campo es INTEGER puedo almacenar tanto texto como numeros como long
    //asi lo que diferencia que datos entrega es el getInt o getLong o getString
    //y asi mismo el comportamiento de la informacion devuelta

    //GUARDANDO TEXTO EN CAMPO INTEGER si llamas a getString te muestra el texto guardado pero si llamas a getint te retorna 0
    //porque no tiene un numero que pueda convertir
    //LE METI un "2501.09" en get int mostro solo 2501 en get String mostro 2501.09 y en get long solo 2501
    //SI LO METES COMO NUMERO 2501.09 NO IMPORTA ES LO MISMO ESO QUIERE DECIR QUE EL INTEGER ALMACENA SOLO INTEGERS JAJA SI HACE ALGO

    //EN NUMERIC

    //LO QUE SI ES IMPORTATE ES QUE TRATEMOS QUE NINGUN CAMPO VALLA EN NULL PONGAMOSLE UN VALOR DEFAULT ACORDE A SU DATO
    //porque por ejemplo cuando usas un jsonobjet.put para enviar datos, si el campo va en null el json objet no se envia
    //y eso podria ocacionar problemas en el archivo que recibira esa informacion porque podria estar espérando el nombre de esa variable
    //si pones un default aunque sea DEFAULT '' vacio y en automatico se elimina el null y el jsonput envia el nombre de la variable con el valor vacio
    //LOS VALORES DEFAULT SE CARGAN CUANDO SE LLAMA A LA CENTENCIA CREATE TABLE EN EL ON CREATE por primera vez y cada que se agrege un registro nuevo
    //se llenaran con sus defaults


    private static final String CREATE_TABLE_CLIENTES = "CREATE TABLE "
            + TABLA_CLIENTES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CLIENTES_ADMIN_CUENTA_CLIENTE + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_NOMBRE_CLIENTE + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_TELEFONO + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_DIAS_CREDITO + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_GRADO_CLIENTE + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_CREDITO_CLIENTE + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_ESTADO_CLIENTE + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_LATITUD_CLIENTE + " TEXT DEFAULT '0',"//antes eran NUMERIC pero nos comenzaron a dar un problema no se si al guardarlo en la base de datos o al recuperarlo pero recortaba las coordenadas a 3 o 4 digitos despues del punto quitando presicion, aunque los recuperara con getstring yo creo que es porque cuando guardabamos las coordenadas estan guardadas en una cadena string, y al meterlas a este campo no eran numericas
            + CLIENTES_ADMIN_LONGITUD_CLIENTE + " TEXT DEFAULT '0', "//en el mapa de la app ocurria se recortaba  a 4 digitos y las chinchetas se proyectaban mal sobre el mapa, lo cheque visualmente sobre los datos de maps y estaban como a 50m alejadas o en otras calles, ahora ya quedo reparado
            + CLIENTES_ADMIN_ADEUDO_CLIENTE + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_ACARGO_CLIENTE + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_VENCIMIENTO + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_HISTORIALES + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_PUNTOS_DISPONIBLES + " TEXT DEFAULT '0', "//los que llegaron de administracion
            + CLIENTES_ADMIN_REPORTE + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_INDICACIONES + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_MERCANCIA_ACARGO + " TEXT DEFAULT '0', "
            + CLIENTES_ADMIN_PAGO_ANTERIOR1 + " INTEGER DEFAULT 0, "
            + CLIENTES_ADMIN_PAGO_ANTERIOR2 + " INTEGER DEFAULT 0, "
            + CLIENTES_ADMIN_PAGO_ANTERIOR3 + " INTEGER DEFAULT 0, "
            + CLIENTES_ADMIN_PAGO_ANTERIOR4 + " INTEGER DEFAULT 0, "
            + CLIENTES_ADMIN_PAGO_ANTERIOR5 + " INTEGER DEFAULT 0, "

            + CLIENTES_CLI_ESTADO_VISITA + " TEXT DEFAULT '0', "
            + CLIENTES_CLI_CODIGO_ESTADO_FECHAS + " INTEGER DEFAULT '', "                  //guardaremos el int de las constantes que indican si La fecha de cierre esta exata a su fecha, si ya vencio, si aun no vence
            + CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE + " INTEGER DEFAULT 0, "  //aqui guardaremos el numero de dias restadas de la fecha de vencimiento del cliente - la fecha actual, negativos son dias vencidos, positivo son dias que aun faltan para cierre si el cliente vence el 24-11-2019 y la fecha actual es 23-11-2019 nos dara 1 positivo es decir falta 1 dia para su corte, si vencimiento es 24-11-2019 y actual es 25-11-2019 nos dara -1 es decir que la cuenta lleva un dia vencida
            + CLIENTES_CLI_PRIORIDAD_DE_VISITA + " INTEGER DEFAULT '', "      //guardaremos de 1 en adelante la importancia de la visita del cliente, el guardarlo en numeros nos permitira ordenarlos de urgentes a menos urgentes siendo 1 urgente y 5 bajo
            + CLIENTES_CLI_MENSAJE_MOSTRAR_POR_VISITAR + " TEXT DEFAULT '0', "

            + CLIENTES_DEV_CANTIDAD_PIEZAS_DEVUELTAS + " INTEGER DEFAULT 0, "
            + CLIENTES_DEV_IMPORTE_DEVUELTO + " INTEGER DEFAULT 0, "
            + CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS + " TEXT NOT NULL DEFAULT '[]', "

            + CLIENTES_CIE_VENTA_GENERADA_LLENAR + " INTEGER DEFAULT 0, "
            + CLIENTES_CIE_PAGO_EXTRA_PARA_GANAR_PUNTOS_LLENAR + " INTEGER DEFAULT 0, "
            + CLIENTES_CIE_PUNTOS_GANADOS_VENTA_LLENAR + " INTEGER DEFAULT 0, "//los que gano por venta
            + CLIENTES_CIE_LATITUD + " TEXT DEFAULT '0', "
            + CLIENTES_CIE_LONGITUD + " TEXT DEFAULT '0', "
            + CLIENTES_CIE_HORA + " DATETIME DEFAULT '', "

            + CLIENTES_PAG_PAGO_POR_VENTA_CAPTURADO + " INTEGER DEFAULT 0, "
            + CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA + " INTEGER DEFAULT 0, "
            + CLIENTES_PAG_PAGO_MINIMO_REQUERIDO_PARA_ENTREGA_MERCANCIA + " INTEGER DEFAULT 0, "
            + CLIENTES_PAG_CUMPLIO_CON_PAGO_MINIMO + " BOOLEAN DEFAULT 0, "
            + CLIENTES_PAG_LATITUD + " TEXT DEFAULT '0', "
            + CLIENTES_PAG_LONGITUD + " TEXT DEFAULT '0', "
            + CLIENTES_PAG_HORA + " DATETIME DEFAULT '', "


            + CLIENTES_ENT_CANTIDAD_CREDITO + " INTEGER DEFAULT 0, "
            + CLIENTES_ENT_CANTIDAD_CONTADO + " INTEGER DEFAULT 0, "
            + CLIENTES_ENT_CANTIDAD_REGALOS + " INTEGER DEFAULT 0, "
            + CLIENTES_ENT_IMPORTE_CREDITO + " INTEGER DEFAULT 0, "
            + CLIENTES_ENT_IMPORTE_CONTADO + " INTEGER DEFAULT 0, "
            + CLIENTES_ENT_IMPORTE_REGALOS + " INTEGER DEFAULT 0, "
            + CLIENTES_ENT_DESCRIPCION_ENTREGA + " TEXT NOT NULL DEFAULT '', "
            + CLIENTES_ENT_DESCRIPCION_ENTREGA_CREDITO + " TEXT NOT NULL DEFAULT '[]', "
            + CLIENTES_ENT_DESCRIPCION_ENTREGA_CONTADO + " TEXT NOT NULL DEFAULT '[]', "
            + CLIENTES_ENT_DESCRIPCION_ENTREGA_REGALOS + " TEXT NOT NULL DEFAULT '[]', "
            + CLIENTES_ENT_PUNTOS_GANADOS_CONTADO + " INTEGER DEFAULT 0, "//los que gano por venta de contado
            + CLIENTES_ENT_PUNTOS_TOTALES + " INTEGER DEFAULT 0, "//la suma total de los disponibles mas los ganados
            + CLIENTES_ENT_PUNTOS_CANJEADOS + " INTEGER DEFAULT 0, "
            + CLIENTES_ENT_PUNTOS_RESTANTES + " INTEGER DEFAULT 0, "
            + CLIENTES_ENT_DIFERENCIA_EXCESO_CREDITO + " INTEGER DEFAULT 0, "
            + CLIENTES_ENT_PROHIBIDO_ENTREGAR_MERCANCIA_CREDITO + " BOOLEAN DEFAULT 0, "
            + CLIENTES_ENT_PROHIBIDO_CANJEAR_PUNTOS + " BOOLEAN DEFAULT 0, "
            + CLIENTES_ENT_NUEVO_CREDITO_CALCULADO + " INTEGER DEFAULT 0, "
            + CLIENTES_ENT_NUEVOS_DIAS_DE_CREDITO + " INTEGER DEFAULT 0, "
            + CLIENTES_ENT_NUEVOS_GRADO_CALCULADO + " TEXT DEFAULT 0, "
            + CLIENTES_ENT_FECHA_INICIAL + " DATETIME DEFAULT '', "   //si tu pones solo NOT NULL se desencadena un error cuando tratas de meter datos en un update
            + CLIENTES_ENT_FECHA_VENCIMIENTO + " DATETIME DEFAULT '', "

            + CLIENTES_PA2_PAGO_DIFERENCIA_REGALO + " INTEGER DEFAULT 0, "
            + CLIENTES_PA2_PAGO_POR_VENTA_CONTADO + " INTEGER DEFAULT 0, "
            + CLIENTES_PA2_PAGO_CAPTURADO + " INTEGER DEFAULT 0, "
            + CLIENTES_PA2_LATITUD + " TEXT DEFAULT '0', "
            + CLIENTES_PA2_LONGITUD + " TEXT DEFAULT '0', "
            + CLIENTES_PA2_HORA + " DATETIME DEFAULT '', "

            + CLIENTES_FIN_FINALIZADO + " BOOLEAN  DEFAULT 0, "        //los datos booleanos no los reconocen las bases de datos si no hay un metodo getBoolean pero se pueden usar si le mando a guardar una variable boleana que use en otro
            + CLIENTES_FIN_FIRMA_DE_VOZ + " BOOLEAN DEFAULT 0, "      //activity anque diga true or false aqui se guarda un 1 o 0, por eso cuando recupero un dato de aqui usas la comparacion directa
            + CLIENTES_FIN_HORA + " DATETIME DEFAULT '', "             //boolean activo = (cursor.getInt(cursor.getColumnIndex(DataBaseHelper.CLIENTES_PAG_CUMPLIO_CON_PAGO_MINIMO)) == 1) si es uno retornara true sino false y ya se guarda en la variable
            + CLIENTES_FIN_LATITUD + " TEXT DEFAULT '0', "                                                //es importante que al trabajar con datos booleanos solicites con getInt asi te devolvera 0 o 1 y aunque el campo nunca se haya llenado y este en null devolvera 0 en cambio si pides un string te devolvera null si no se ha llenado, o 1 y 0
            + CLIENTES_FIN_LONGITUD + " TEXT DEFAULT '0', "                                               //tambien podrias guardar el boolean si configuras el campo como text y escribir directamente true o false pero es lo mismo podrias obrtener un null

            + CLIENTES_ADMIN_FECHA_DE_CONSULTA + " DATETIME DEFAULT '',"
            + CLIENTES_ADMIN_NOMBRE_ZONA + " TEXT DEFAULT '')";







    private static final String CREATE_TABLE_INVENTARIO = "CREATE TABLE "
            + TABLA_INVENTARIO + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + INVENTARIO_CODIGO_PRODUCTO + " INTEGER,"
            + INVENTARIO_PRECIO_VENTA_PRODUCTO + " INTEGER,"
            + INVENTARIO_PRECIO_VENDEDORA + " INTEGER,"
            + INVENTARIO_EXISTENCIAS + " INTEGER,"
            + INVENTARIO_PRECIO_SOCIA + " INTEGER,"
            + INVENTARIO_PRECIO_EMPRESARIA + " INTEGER)";


    private static final String CREATE_TABLE_MOVEMENTS = "CREATE TABLE "
            + TABLE_MOVEMENTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + MOVEMENTS_ACCOUNT + " INTEGER,"
            + MOVEMENTS_NAME + " TEXT,"
            + MOVEMENTS_CREDIT_CODE + " INTEGER,"
            + MOVEMENTS_REPORT + " TEXT,"
            + ESTADO_DE_LA_COLUMNA + " TEXT,"
            + DATE_UP + " DATETIME, "
            + MOVEMENTS_EXPIRATION_DATE + " DATETIME)";

    private static final String CREATE_TABLE_DETALLES = "CREATE TABLE "
            + TABLA_DETALLES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DETALLES_ID_DEL_MOVIMIENTO + " INTEGER,"
            + DETALLES_CANTIDAD + " INTEGER,"
            + DETALLES_PRECIO_PRODUCTO + " INTEGER,"
            + DETALLES_PRECIO_DISTRIBUCION + " INTEGER,"
            + DETALLES_GANANCIA + " INTEGER,"
            + DETALLES_CODIGO_PRODUCTO + " INTEGER,"
            + DETALLES_TIPO_MOVIMIENTO + " TEXT,"
            + DETALLES_LATITUD + " NUMERIC,"
            + DETALLES_LONGUITUD + " NUMERIC,"
            + ESTADO_DE_LA_COLUMNA + " TEXT,"
            + DATE_UP + " DATETIME" + ")";

    private static final String CREATE_TABLE_PAGOS = "CREATE TABLE "
            + TABLA_PAGOS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PAGOS_IDMOVIMIENTO + " INTEGER,"
            + PAGOS_PAGO + " NUMERIC,"
            + PAGOS_PAGOS_HORA + " TIME,"
            + PAGOS_DIFERENCIA + " NUMERIC,"
            + PAGOS_HORA_DIFERENCIA + " TIME,"
            + PAGOS_SALDO_PENDIENTE + " NUMERIC,"
            + PAGOS_HORA_SALDO_PENDIENTE + " TIME,"
            + PAGOS_OTRO + " NUMERIC,"
            + PAGOS_HORA_OTRO + " TIME,"
            + PAGOS_ADEUDO + " NUMERIC,"
            + PAGOS_HORA_ADEUDO + " TIME,"
            + PAGOS_PUNTOS + " INTEGER,"
            + PAGOS_HORA_PUNTOS + " TIME,"
            + PAGOS_LATITUD + " NUMERIC,"
            + PAGOS_LONGITUD + " NUMERIC,"
            + ESTADO_DE_LA_COLUMNA + " TEXT,"
            + DATE_UP + " DATETIME" + ")";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //(eESTE METODO SOLO SE LLAMA CUANDO SE CREA DESDE 0 LA BASE DE DATOS, YO CREIA QUE
        // SE LLAMABA AL ABRIR LA APP O ALGO ENTONCES TUVE UN PROBLEMA MUY GRANDE JAJA
        // CAMBIE UNOS CAMPOS DE UNAS TABLAS Y LES AÑADI MAS CAMPOS, YO CREIA QUE EN AUTOMATICO
        // SE AÑADIRIAN LOS NUEVOS CAMPOS, PERO NO FUE ASI TARDE MUCHISISIMO TIEMPO EN DARME CUENTA
        // QUE PARA QUE SE VUELVA A LLAMAR AL METODO CREATE TABLE TIENES QUE BUSCAR LA MANERA DE
        // LLAMAR AL ON CREATE YA SEA LLAMANDOLO COMO LO HACIA ABHRAN EN EL ON UPGRADE QUE BORRABA TODAS LAS TABLAS
        // Y AL FINAL LLAMABA AL METODO oncreate() DIREACTAMENTE PARA QUE SE VOLVIERAN A CREAR, O PUEDES CREAR
        // UN METODO PUBLIC COMO CUALQUIER OTRO EN ESTA CLASE Y DENTRO DE EL LLAMAR AL ON CREATE Y EN CUALQUIER
        // OTRO ACTIVITI LLAMAR AL METODO PUBLICO.
        //
        // AL IGUAL COMO PUEDES ELIMNIAR TABLAS CON EL DROP TABLE IF EXISTS EN CUALQUIER METODO
        //
        // PORQUE EN EL METODO UPGRADE ESTE SOLAMENTE SE LLAMA CUANDO Y UNICAMENTE CUANDO LA VERSION DE LA BASE DE DATOS
        // LA AUMENTAS DE VALOR, YA NO PUEDES HACER DOWNGRADE, ENTONCES SOLO TIENES 1 OPORTUNIDAD POR ASI DECIRLO DE
        // CREAR LAS NUEVAS TABLAS EN EL UPDATE.
        //
        // LA OTRA OPCION MAS FACIL ES CON DEVICE FILE EXPLORER QUE ESTA EN LA BARRA DERECHA DEL ANDROID STUDIO
        // BUSCAR LA RUTA DE TU BASE DE DATOS QUE SE GUIARDA EN data-data-nombrePaqueteDeTuApp- databases y eliminar tu base de datos
        // en automatico cuando se abra la app nuevamente y crees cualquier instancia de la esta clase DataVaseHelper se llamara
        // su constructor que buscara si la base de datos yua esta creada y si no lo esta se llama en automatico al onCreate)
        db.execSQL(CREATE_TABLE_NUMEROS_CUENTA);
        db.execSQL(CREATE_TABLE_IDENTIFICADOR_ZONA);
        db.execSQL(CREATE_TABLE_MENSAJES_ADMINISTRACION);
        db.execSQL(CREATE_TABLE_CLIENTES);
        db.execSQL(CREATE_TABLE_INVENTARIO);
        db.execSQL(CREATE_TABLE_MOVEMENTS);
        db.execSQL(CREATE_TABLE_DETALLES);
        db.execSQL(CREATE_TABLE_PAGOS);
        db.execSQL(CREATE_TABLE_ENTREGA_MERCANCIA);
        db.execSQL(CREATE_TABLE_DEVUELVE_MERCANCIA);
        //db.execSQL(CREATE_TABLE_PAGOS_LUISDA);
        //db.execSQL(CREATE_TABLE_CIERRE);
        //db.execSQL(CREATE_TABLE_PUNTOS);

        rutaDeDB = db.getPath();

        Log.d("dbonCreate","creando tablas");

        Log.d("dbOnCreate: " , String.valueOf(DATABASE_VERSION));
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("dbUpgradeoldVersion: " , String.valueOf(oldVersion));
        Log.d("dbUpgradenewVersion: " , String.valueOf(newVersion));
        //el onUpgrade se ejecuta desde que se abre la app por primera ves estos logs los veremos en el main


        //loop para cada version cuando ocurre un upgrade https://riptutorial.com/es/android/example/3253/metodo-onupgrade---
        for (int version = oldVersion + 1; version <= newVersion; version++ ){
            switch (version){
                case 2:
                    //aply changes made in version 2 VersionNameLuisda6.5 añadimos la columna para guardar las coordenadas de la zona
                    db.execSQL("ALTER TABLE " + TABLA_IDENTIFICADOR_ZONA + " ADD COLUMN " + COORDENADAS_ZONA + " TEXT;");
                    Log.d("dbUpgradeSwichCase2: " , "añadida columna coordenadas zona");
                    break;

                case 3:
                    //aply changes made in version 3 VersionNameLuisda6.6 añadimos las columnas de precio 2 y 3 a la tabla de inventario que es de donde sacaremos
                    //los precios, la tabla de catalogo de precios la borraremos
                    //necesitamos renombrar 2 columnas, las queremos renombrar con las constantes que se usan en la tabla prices
                    //para ello usamos el procedimiento de este link https://tableplus.io/blog/2018/04/sqlite-rename-a-column.html, ya que poner RENAME COLUMN no funciona
                    //1.- renombrar la antigua tabla con otro nombre
                    db.execSQL("ALTER TABLE " + TABLA_INVENTARIO + " RENAME TO table_inventory_old");
                    Log.d("dbUpgradeSwichCase3: " , "TABLA RENOMBRADA");
                    //2.- crear la nueva tabla CON LOS NUEVOS NOMBRES DE CAMPOS
                    db.execSQL("CREATE TABLE "
                        + TABLA_INVENTARIO + "("
                        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + INVENTARIO_CODIGO_PRODUCTO + " INTEGER,"
                        + INVENTARIO_PRECIO_VENTA_PRODUCTO + " INTEGER,"
                        + INVENTARIO_PRECIO_VENDEDORA + " INTEGER,"
                        + INVENTARIO_EXISTENCIAS + " INTEGER)"
                        );
                    Log.d("dbUpgradeSwichCase3: " , "NUEVA TABLA CREADA");

                    //3.-copiar el contenido de la tabla original a la nueva tabla
                    db.execSQL("INSERT INTO " + TABLA_INVENTARIO + "(" + INVENTARIO_CODIGO_PRODUCTO + ", " + INVENTARIO_PRECIO_VENTA_PRODUCTO + ", " + INVENTARIO_PRECIO_VENDEDORA + ", " + INVENTARIO_EXISTENCIAS + ")" +
                    " SELECT " + INVENTARIO_CODIGO_PRODUCTO + ", " + INVENTORY_PRICE + ", " + INVENTORY_ONE + ", " + INVENTARIO_EXISTENCIAS +
                    " FROM " + "table_inventory_old");

                    Log.d("dbUpgradeSwichCase3: " , "POBLAR TABLA CON ANTIGUOS DATOS");

                    //4.-Eliminar la tabla antigua
                    db.execSQL("DROP TABLE table_inventory_old");
                    Log.d("dbUpgradeSwichCase3: " , "ELIMINAMOS VIEJA TABLA");

                    //AÑADIMOS LOS NUEVOS CAMPOS A LA TABLA, YA SE ESTO SE PODRIAN HABER AÑADIDO DESDE QUE
                    //CREAMOS LA NUEVA TABLA RRIBA, PERO QUERIA APRENDER COMO SE HACIA
                    db.execSQL("ALTER TABLE " + TABLA_INVENTARIO + " ADD COLUMN " + INVENTARIO_PRECIO_SOCIA + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_INVENTARIO + " ADD COLUMN " + INVENTARIO_PRECIO_EMPRESARIA + " INTEGER;");
                    Log.d("dbUpgradeSwichCase3: " , "añadir columnas a inventario");
                    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICES);
                    Log.d("dbUpgradeSwichCase3: " , "Eliminamos tabla de precios");
                    break;

                case 4:
                    Log.d("dbUpgradeSwichCase4: " , "conteo columnas");
                    //aply changes made in version 4 Apartir de VersionCode 19 añadimos la columna "nombreZona" a la tabla de clientes
                    //porque como ahora permitimos cargar 2 rutas, debemos saber a que ruta pertenece cada clienta
                    //AÑADIMOS LAS DEMAS COLUMNAS QUE PODRIAMOS UTILIZAR CUANDO CALCULEMOS LOS CREDITOS DE LAS CLIENTAS


                    //AÑADIMOS LOS NUEVOS CAMPOS A LA TABLA
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_ENT_PUNTOS_GANADOS_CONTADO + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_ADMIN_PAGO_ANTERIOR1 + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_ADMIN_PAGO_ANTERIOR2 + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_ADMIN_PAGO_ANTERIOR3 + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_ADMIN_PAGO_ANTERIOR4 + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_ADMIN_PAGO_ANTERIOR5 + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_ENT_NUEVO_CREDITO_CALCULADO + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_ENT_NUEVOS_DIAS_DE_CREDITO + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_ENT_NUEVOS_GRADO_CALCULADO + " TEXT;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_CLI_CODIGO_ESTADO_FECHAS + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_CLI_PRIORIDAD_DE_VISITA + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_CLI_MENSAJE_MOSTRAR_POR_VISITAR + " TEXT;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_DEV_CANTIDAD_PIEZAS_DEVUELTAS + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_DEV_IMPORTE_DEVUELTO + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS + " TEXT;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_CIE_VENTA_GENERADA_LLENAR + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_CIE_PAGO_EXTRA_PARA_GANAR_PUNTOS_LLENAR + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_PAG_PAGO_POR_VENTA_CAPTURADO + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_PAG_PAGO_MINIMO_REQUERIDO_PARA_ENTREGA_MERCANCIA + " INTEGER;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_PAG_CUMPLIO_CON_PAGO_MINIMO + " BOOLEAN;");
                    db.execSQL("ALTER TABLE " + TABLA_CLIENTES + " ADD COLUMN " + CLIENTES_ADMIN_NOMBRE_ZONA + " TEXT;");

                    Log.d("dbUpgradeSwichCase4: " , "añadir columnas a clientes");

                    Log.d("dbUpgradeSwichCase5: " , "eliminando tablas");





                    db.execSQL(CREATE_TABLE_ENTREGA_MERCANCIA);
                    db.execSQL(CREATE_TABLE_DEVUELVE_MERCANCIA);
                    //db.execSQL(CREATE_TABLE_PAGOS_LUISDA);
                    //db.execSQL(CREATE_TABLE_CIERRE);
                    //db.execSQL(CREATE_TABLE_PUNTOS);





                    break;






            }
        }




        /*
        Esto estaba en la programacion de abraham lo que entiendo es que al detectar que se actualizaba
        la base de datos de version, e invoca en automatico al onUpgrade, y estas sentencias que puso abraham
        eliminan completamente la tabla si existe y despues se llamaba al onCreate donde en el
        se creaban nuevamente las tablas,el problema es que la informacion de las tablas
        que existia se borraba.Ahora en la version de 6.5 se identifica que cambios vamos a realizar
        y añadir las columnas a las tablas, sin perder la informacion que en ellas esta


        db.execSQL("DROP TABLE IF EXISTS " + TABLA_NUMEROS_CUENTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_IDENTIFICADOR_ZONA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_MENSAJES_ADMINISTRACION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_CLIENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_REGALOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OWNER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICES);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_CODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_INVENTARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BINNACLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_DETALLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PAGOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_BLOQUEO_APP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PULSERAS);
        onCreate(db);
        */
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    void insertarNumerosCuenta(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int a=1 ; a<= 30 ; a++){
            cv.put(NUMERO_CUENTA,a);
            cv.put(ESTADO_DEL_NUMERO,0);
            db.insert(TABLA_NUMEROS_CUENTA,null,cv);
        }



        }

    public void eliminarNumerosCuenta (){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLA_NUMEROS_CUENTA,null,null);
    }

    void restablecerNumerosCuenta (){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put (ESTADO_DEL_NUMERO,0);
        db.update(TABLA_NUMEROS_CUENTA, cv, ESTADO_DEL_NUMERO + " = 1",null);
    }

    int actualizaNumerosCuenta (int numeroNuevo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ESTADO_DEL_NUMERO,1);
        return db.update(TABLA_NUMEROS_CUENTA,cv,NUMERO_CUENTA + " = " + numeroNuevo,null);
}

    Cursor obtenerNumeroCuenta(String tipo){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql;

        //(si le enviamos como parametro nuestro tipo como nuevo entonces nos consulta los numeros del 10 al 30
        // si no escribimos nuevo y neviamos cualquier otro nos consulta los numeros del 1 al 9 para movimientos de almacen)
        if (tipo.equals("NUEVO")){
            sql = "SELECT * FROM " + TABLA_NUMEROS_CUENTA + " WHERE ( " + NUMERO_CUENTA + " BETWEEN 10 AND 30 ) AND ( " + ESTADO_DEL_NUMERO + " = 0 )";
        }
        else{
            sql = "SELECT * FROM " + TABLA_NUMEROS_CUENTA + " WHERE ( " + NUMERO_CUENTA + " BETWEEN 1 AND 9 ) AND ( " + ESTADO_DEL_NUMERO + " = 0 )";
        }

        Cursor res = db.rawQuery(sql,null);
        return res;
    }

    Cursor obtenerTodosLosNumerosDeCuenta(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLA_NUMEROS_CUENTA,null);

        return res;
    }

    
    
    
    /**TABLA IDENTIFICADOR ZONA*/

    void tabla_identificador_zona_insertarIdZona(String zona, String perimetro){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID_ZONA,zona);
        cv.put(COORDENADAS_ZONA,perimetro);
        db.insert(TABLA_IDENTIFICADOR_ZONA,null,cv);
    }

    void tabla_identificador_zona_eliminarIdZona(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLA_IDENTIFICADOR_ZONA,null,null);
    }

    Cursor tabla_identificador_zona_obtenerIdZona(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLA_IDENTIFICADOR_ZONA,null);
        return res;
    }







    void insertarMensaje (String mensaje){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MENSAJES,mensaje);
        db.insert(TABLA_MENSAJES_ADMINISTRACION,null,cv);
    }

    Cursor  obtenerMensaje (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLA_MENSAJES_ADMINISTRACION,null);
        return res;
    }

    void eliminarMensajes(){
        SQLiteDatabase db = this.getWritableDatabase();
                db.delete(TABLA_MENSAJES_ADMINISTRACION,null,null);
    }














    /**Inventario*/

    Cursor inventario_dameInventarioConExistencias(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLA_INVENTARIO + " WHERE " + INVENTARIO_EXISTENCIAS + " != 0", null);
        return res;
    }

    Cursor inventario_dameInvenarioCompleto(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLA_INVENTARIO, null);
        return res;
    }

    long inventario_insertarInventario(ContentValues cv){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.insert(TABLA_INVENTARIO, null, cv);
    }

    int inventario_eliminaInventario(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA_INVENTARIO, null, null);
    }

    Cursor inventario_dameInformacionCompletaDelProducto(String codigoProducto) {
        SQLiteDatabase database = this.getWritableDatabase();
        String sql = "SELECT " + INVENTARIO_EXISTENCIAS + ", " + INVENTARIO_PRECIO_VENTA_PRODUCTO + ", " + INVENTARIO_PRECIO_VENDEDORA + ", " + INVENTARIO_PRECIO_SOCIA + ", " + INVENTARIO_PRECIO_EMPRESARIA + " " +
                "FROM " + TABLA_INVENTARIO + " WHERE " + INVENTARIO_CODIGO_PRODUCTO + " = " + codigoProducto;
        return database.rawQuery(sql, null);
    }

    int inventario_dameGananciaDelProducto(String codigoProducto, String gradoCliente) throws Exception{

        return inventario_dameCostoDelProducto(codigoProducto) - inventario_damePrecioDistribucionDelProducto(codigoProducto,gradoCliente);



    }

    int inventario_dameCostoDelProducto(String codigoProducto) throws Exception{

        Cursor informacionDelProducto = inventario_dameInformacionCompletaDelProducto(codigoProducto);

        if (informacionDelProducto.getCount() == 0) throw new Exception("No existe el codigo del producto");

        informacionDelProducto.moveToFirst();

        return informacionDelProducto.getInt(informacionDelProducto.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENTA_PRODUCTO));



    }

    int inventario_damePrecioDistribucionDelProducto(String codigoProducto, String gradoCliente) throws Exception{



        Cursor informacionDelProducto = inventario_dameInformacionCompletaDelProducto(codigoProducto);

        if (informacionDelProducto.getCount() == 0) throw new Exception("No existe el codigo del producto");

        informacionDelProducto.moveToFirst();


        if (gradoCliente.equals(Constant.SOCIA)){
            return informacionDelProducto.getInt(informacionDelProducto.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_SOCIA));
        }else if (gradoCliente.equals(Constant.EMPRESARIA)){
            return informacionDelProducto.getInt(informacionDelProducto.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_EMPRESARIA));
        }else {
            //si fuera vendedora
            return informacionDelProducto.getInt(informacionDelProducto.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENDEDORA));
        }



    }

    void inventario_incrementaInventario(String codigoProducto, String cantidad){

        Log.d("dbg-sumaStock: ", "UPDATE " + TABLA_INVENTARIO + " SET " + INVENTARIO_EXISTENCIAS + " = " + INVENTARIO_EXISTENCIAS + " + " +  cantidad + " WHERE " + INVENTARIO_CODIGO_PRODUCTO + " = " + codigoProducto);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery("UPDATE " + TABLA_INVENTARIO + " SET " + INVENTARIO_EXISTENCIAS + " = " + INVENTARIO_EXISTENCIAS + " + " +  cantidad + " WHERE " + INVENTARIO_CODIGO_PRODUCTO + " = " + codigoProducto, null);
        res.moveToFirst();

    }

    void inventario_decrementaInventario(String codigoProducto, String cantidad){

        Log.d("dbg-restaaStock: ", "UPDATE " + TABLA_INVENTARIO + " SET " + INVENTARIO_EXISTENCIAS + " = " + INVENTARIO_EXISTENCIAS + " - " +  cantidad + " WHERE " + INVENTARIO_CODIGO_PRODUCTO + " = " + codigoProducto);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery("UPDATE " + TABLA_INVENTARIO + " SET " + INVENTARIO_EXISTENCIAS + " = " + INVENTARIO_EXISTENCIAS + " - " +  cantidad + " WHERE " + INVENTARIO_CODIGO_PRODUCTO + " = " + codigoProducto, null);
        res.moveToFirst();

    }



    /**TABLA DETALLE */

    int detalles_eliminaEntrada(String idMovimiento, String codigo){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLA_DETALLES, DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento + " AND " + DETALLES_CODIGO_PRODUCTO + " = " + codigo + " AND " + DETALLES_TIPO_MOVIMIENTO + " = 'E'", null);

    }

    int detalles_actualizaDetalle(String idMovimiento, String codigo, String precio, String idRow){
        Log.d("dbg-funcion-cero",precio);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DETALLES_PRECIO_DISTRIBUCION, "0");
        values.put(DETALLES_GANANCIA, precio);

        return db.update(TABLA_DETALLES, values, DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento + " AND " + DETALLES_CODIGO_PRODUCTO + " = " + codigo + " AND " + KEY_ID + " = " + idRow, null);
    }

    Cursor detalles_dameEntradas(String idMovimiento){
        SQLiteDatabase db = this.getWritableDatabase();
        //String sql = "SELECT * FROM " + TABLA_DETALLES + " WHERE " + DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento + " AND " + DETALLES_TIPO_MOVIMIENTO + " = 'entrada'" ;
        String sql = "SELECT * FROM " + TABLA_DETALLES + " WHERE " + DETALLES_ID_DEL_MOVIMIENTO + " = ?" + " AND " + DETALLES_TIPO_MOVIMIENTO + " = ?";
        Log.d("holaaa",sql);
        return db.rawQuery(sql, new String[] {idMovimiento,Constant.TIPO_DE_MOVIMIENTO_ENTRADA_AL_INVENTARIO});
    }





    /**Tabla clientes*/

    Cursor clientes_dameTodosLosClientes(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLA_CLIENTES + " ORDER BY " + CLIENTES_CLI_PRIORIDAD_DE_VISITA + " ASC", null );
    }

    public int clientes_contarColumnasClientes (){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLA_CLIENTES, null ).getColumnCount();
    }

    void clientes_insertarClientes(ContentValues cv){
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLA_CLIENTES,null,cv);

    }

    void clientes_insertarClienteNuevoSiNoExiste(String cuentaCliente){
        //en este metodo primero vamos a revizar que el nmero de cuenta
        //que se quiere incertar no exista ya en la base de datos
        //si existe entonces ya no lo insertamos

        Cursor cursor = clientes_dameClientePorCuentaCliente(cuentaCliente);
        SQLiteDatabase db = this.getWritableDatabase();
        if (cursor.getCount()==0){
            ContentValues contentValues = new ContentValues();
            contentValues.put(CLIENTES_ADMIN_CUENTA_CLIENTE,cuentaCliente);
            db.insert(TABLA_CLIENTES,null,contentValues);
        }



    }

    int clientes_eliminaTablaClientes(){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLA_CLIENTES,null,null);
    }

    int clientes_eliminaClienteNumeroCuenta(String numeroCuenta){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLA_CLIENTES,CLIENTES_ADMIN_CUENTA_CLIENTE + " LIKE ?",new String[]{numeroCuenta});
    }

    Cursor clientes_dameClientesPorId(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLA_CLIENTES + " WHERE " + KEY_ID + " = " + id, null);
        return res;
    }

    Cursor clientes_dameClientePorCuentaCliente(String cuentaCliente){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLA_CLIENTES + " WHERE " + CLIENTES_ADMIN_CUENTA_CLIENTE + " LIKE " + cuentaCliente, null);

    }


    Cursor clientes_dameClientesPorEstadoVisita(String estado){
        String estado1 = "'" + estado + "%" + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLA_CLIENTES + " WHERE " + CLIENTES_CLI_ESTADO_VISITA + " LIKE " + estado1 + " ORDER BY " + CLIENTES_CLI_PRIORIDAD_DE_VISITA + " ASC",null);
        return res;
    }

    long clientes_actualizaTablaClientesPorKeyID(ContentValues cv, int id){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.update(TABLA_CLIENTES,cv, KEY_ID + " = " + id, null);
    }


    long clientes_actualizaTablaClientesPorNumeroCuenta(ContentValues cv, String cuenta){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.update(TABLA_CLIENTES,cv, CLIENTES_ADMIN_CUENTA_CLIENTE + " LIKE ?", new String[]{cuenta});
    }




    /**
     * @param movimientoFinalizado  true si quieres consultar todos los clientes con movimientos finalizados false, con no finalizados
     * @return Cursor con los clientes, ordenados por la hora de movimiento
     */
    public Cursor clientes_consultarClientesPorMovimientoFinalizado (boolean movimientoFinalizado){

        SQLiteDatabase db = this.getWritableDatabase();
        int finalizado = movimientoFinalizado?1:0; //convertimos nuestro valor booleano a 1 o 0 que es lo que puede manejar la base de datos //Usando el operardor ternario para no poner toda la clausula if

        return db.rawQuery(
                "SELECT * FROM " + TABLA_CLIENTES + " WHERE " + CLIENTES_FIN_FINALIZADO + " = " + finalizado + " ORDER BY " + CLIENTES_FIN_HORA + " ASC",
                null);

    }



    public JSONArray clientes_consultarMovimientosClientesFinalizados (){

        Cursor clientesFinalizados = clientes_consultarClientesPorMovimientoFinalizado(true);
        clientesFinalizados.moveToFirst();
        JSONArray jsonArrayTodos = new JSONArray();
        if (clientesFinalizados.getCount()>0){
            do{

                try {
                    jsonArrayTodos.put(clientes_dameMovimientosClienteJsonObjetPorNumeroCuenta(clientesFinalizados.getString(clientesFinalizados.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }while (clientesFinalizados.moveToNext());
        }
        return jsonArrayTodos;
    }

    /**
     * @param cuentaCliente       el numero de cuenta del cliente que quieres consultar
     *
     * @return jsonObjet, solo devuelve todos los campos del cliente consultado
     */
    //**
    public JSONObject clientes_dameMovimientosClienteJsonObjetPorNumeroCuenta (@NonNull String cuentaCliente){


        JSONObject jsonObject = new JSONObject();
        Cursor datosClientes = clientes_dameClientePorCuentaCliente(cuentaCliente);



        if (datosClientes.getCount() > 0){
            datosClientes.moveToFirst();




                try {

                    //AQUI NO IMPORTA QUE TIPO DE DATO QUIERO RECUPERAR DE LA BASE DE DATOS SI getInt o getString etc porque el JsonObjet no reconoce entre tipos de datos da igual
                    //JAJA CREI QUE ERA ASI, PERO TIENES QUE TENER CUIDADO POR EJEMPLO SI LE PIDES genInt y el campo en la base de datos no esta inicializado lo que hace es mandarte un 0
                    //y eso esta bien y si le pides un getString y el campo de la vace de datos esta vacio te manda un null, es especialmente delicado orque en el
                    //json objet si tu le mandas un null, este objet no se inicializa y no se manda adecuadamente a la cadena de texto, entonces por si las moscas vamos a pedir que nos
                    //retorne int donde es int string donde es string etc etc
                    //SI TU MANDAS CUALQUIER VALOR NULL A LOS JSONOBJET ESTOS NO SE REPRESENTAn

                    jsonObject.put(CLIENTES_ADMIN_CUENTA_CLIENTE, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_CUENTA_CLIENTE)));
                    jsonObject.put(CLIENTES_ADMIN_NOMBRE_CLIENTE, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_NOMBRE_CLIENTE)));
                    jsonObject.put(CLIENTES_ADMIN_TELEFONO, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_TELEFONO)));
                    //jsonObject.put(CLIENTES_ADMIN_DIAS_CREDITO       , datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ADMIN_DIAS_CREDITO       )));
                    //jsonObject.put(CLIENTES_ADMIN_GRADO_CLIENTE      , datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_GRADO_CLIENTE      )));
                    //jsonObject.put(CLIENTES_ADMIN_CREDITO_CLIENTE    , datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ADMIN_CREDITO_CLIENTE    )));
                    //jsonObject.put(CLIENTES_ADMIN_ESTADO_CLIENTE     , datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_ESTADO_CLIENTE     )));
                    //jsonObject.put(CLIENTES_ADMIN_LATITUD_CLIENTE    , datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_LATITUD_CLIENTE    )));
                    //jsonObject.put(CLIENTES_ADMIN_LONGITUD_CLIENTE   , datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_LONGITUD_CLIENTE   )));
                    //jsonObject.put(CLIENTES_ADMIN_ADEUDO_CLIENTE     , datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ADMIN_ADEUDO_CLIENTE     )));
                    //jsonObject.put(CLIENTES_ADMIN_ACARGO_CLIENTE     , datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ADMIN_ACARGO_CLIENTE     )));
                    //jsonObject.put(CLIENTES_ADMIN_VENCIMIENTO        , datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_VENCIMIENTO        )));
                    //jsonObject.put(CLIENTES_ADMIN_HISTORIALES        , datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_HISTORIALES        )));
                    //jsonObject.put(CLIENTES_ADMIN_PUNTOS_DISPONIBLES , datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ADMIN_PUNTOS_DISPONIBLES )));
                    //jsonObject.put(CLIENTES_ADMIN_REPORTE            , datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_REPORTE            )));
                    //jsonObject.put(CLIENTES_ADMIN_INDICACIONES       , datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_INDICACIONES       )));
                    //jsonObject.put(CLIENTES_ADMIN_MERCANCIA_ACARGO   , datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_MERCANCIA_ACARGO   )));
                    //jsonObject.put(CLIENTES_ADMIN_PAGO_ANTERIOR1     , datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ADMIN_PAGO_ANTERIOR1     )));
                    //jsonObject.put(CLIENTES_ADMIN_PAGO_ANTERIOR2     , datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ADMIN_PAGO_ANTERIOR2     )));
                    //jsonObject.put(CLIENTES_ADMIN_PAGO_ANTERIOR3     , datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ADMIN_PAGO_ANTERIOR3     )));
                    //jsonObject.put(CLIENTES_ADMIN_PAGO_ANTERIOR4     , datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ADMIN_PAGO_ANTERIOR4     )));
                    //jsonObject.put(CLIENTES_ADMIN_PAGO_ANTERIOR5     , datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ADMIN_PAGO_ANTERIOR5     )));
                    jsonObject.put(CLIENTES_ADMIN_NOMBRE_ZONA, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_NOMBRE_ZONA)));
                    jsonObject.put(CLIENTES_ADMIN_FECHA_DE_CONSULTA, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ADMIN_FECHA_DE_CONSULTA)));


                    jsonObject.put(CLIENTES_CLI_ESTADO_VISITA, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_CLI_ESTADO_VISITA)));
                    jsonObject.put(CLIENTES_CLI_CODIGO_ESTADO_FECHAS, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_CLI_CODIGO_ESTADO_FECHAS)));
                    jsonObject.put(CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE)));
                    jsonObject.put(CLIENTES_CLI_PRIORIDAD_DE_VISITA, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_CLI_PRIORIDAD_DE_VISITA)));
                    // jsonObject.put(CLIENTES_CLI_MENSAJE_MOSTRAR_POR_VISITAR                     , datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_CLI_MENSAJE_MOSTRAR_POR_VISITAR)));

                    jsonObject.put(CLIENTES_DEV_CANTIDAD_PIEZAS_DEVUELTAS, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_DEV_CANTIDAD_PIEZAS_DEVUELTAS)));
                    jsonObject.put(CLIENTES_DEV_IMPORTE_DEVUELTO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_DEV_IMPORTE_DEVUELTO)));


                    //jsonObject.put(CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS)));
                    //si yo retiro directamente el string de la base de datos que anteriormente guardamos en la tabla como string
                    // al recuperarlo aqui e imprimirlo en un Log a primera vista esta perfecto tiene la estructura del jsonArray
                    //
                    // String jsonArrayRecuperado = datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS));
                    // Log.d("StringjsonArrayRec",jsonArrayRecuperado);
                    //[{"codigo":"139","precio":"1","distribucion":"103","ganancia":"36","latitud":"","longitud":"","fechaCaptura":"14:40"},{"codigo":"139","precio":"1","distribucion":"103","ganancia":"36","latitud":"","longitud":"","fechaCaptura":"14:40"}]
                    //
                    // pero aunque paresca un JsonArray en realidad es un String con formato jsonArray porque el lugar donde guardamos esta info para meterla a la base de datos
                    // llamamos al metodo del JsonArray .toString lo cual nos devuelve la representacion string.
                    // bueno si tu pones directamente ese string en el jsonObjetct.put, lo que hace el json objet es que reconoce que estas enviando un string
                    // y cuando encuentra caracteres especiales como las "" comillas usadas para dividir los campos del jsonArray, las "escapa" usando la barra \
                    // para que cuando se imprima en pantalla este string esas barras de escape antes de las " hagan que esas comillas escapadas aparescan
                    // porque claro parte de la declaracion de un string son "hola" si quieres poner una " dentro de ese hola tienes que hacer asi "\"hola\""
                    // entonces al entregar el resultado el jsonObjet lo enviara asi:
                    // "descripcion_piezas_devueltas":"[{\"codigo\":\"139\",\"precio\":\"1\",\"distribucion\":\"103\",\"ganancia\":\"36\",\"latitud\":\"\",\"longitud\":\"\",\"fechaCaptura\":\"14:40\"},{\"codigo\":\"139\",\"precio\":\"1\",\"distribucion\":\"103\",\"ganancia\":\"36\",\"latitud\":\"\",\"longitud\":\"\",\"fechaCaptura\":\"14:40\"}]"
                    // y bueno no es correcto porque al recibir la info en el servidor php tendremos de alguna manera que limpiar esa info enviada para poderla meter
                    // en un json array. Esto ya tambien me habia ocurrido en PHP cuando queria enviar los datos del movimiento de almacen al movil
                    // para resolverlo lo unico que se tiene que hacer es enviar no una reprecentaicon string del jsonArray al jsonobjet.put sino un OBJETo JSONArray
                    // de esta manera el jsonObject.put reconoce que pondras un JsonArray e ingresa la informacion exactamente como se debe
                    // para hacer esto puedo crear un JsonArray apartir de un string y tambien puedo llamar al metodo que me consulta la tabla de devoluciones pero en lugar que me entrege
                    // el string del jsonarray me entrege directo el jsonarray.
                    // Para estos casos y seguir la metodologia de consultar los datos de la tabla clientes tomare la opcion 1 que es crear el objeto JsonArray apartir
                    // del String json que esta guardada en la base de datos , y listo!!!
                    //
                    // String jsonArrayRecuperado = datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS));
                    // JSONArray jsonArray  = new JSONArray(jsonArrayRecuperado);
                    //  en mi forma lo hare en una sola linea
                    //
                    //
                    //
                    // jsonArrayRecuperado = jsonArrayRecuperado.replace("\\","");
                    // Log.d("StringjsonArrayRec",jsonArrayRecuperado);)
                    //
                    // crei que de alguna manera podia remplazar las barras de escape pero jaja claro no se puede porque el que incluye las barras de escape es el jsonObjet.put
                    //  no que se haya guardado asi con las barras en la base de datos Daaaaaaa.
                    //
                    // )
                    //jsonObject.put(CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS                    , devolucion_mercancia_consultarDevolucionEnJsonArray(cuentaCliente));

                    JSONArray devolucionArray = new JSONArray();
                    try {
                        devolucionArray = new JSONArray(datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonObject.put(CLIENTES_DEV_DESCRIPCION_PIEZAS_DEVUELTAS, devolucionArray);


                    jsonObject.put(CLIENTES_CIE_VENTA_GENERADA_LLENAR, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_CIE_VENTA_GENERADA_LLENAR)));
                    jsonObject.put(CLIENTES_CIE_PAGO_EXTRA_PARA_GANAR_PUNTOS_LLENAR, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_CIE_PAGO_EXTRA_PARA_GANAR_PUNTOS_LLENAR)));
                    jsonObject.put(CLIENTES_CIE_PUNTOS_GANADOS_VENTA_LLENAR, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_CIE_PUNTOS_GANADOS_VENTA_LLENAR)));
                    jsonObject.put(CLIENTES_CIE_LATITUD, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_CIE_LATITUD)));
                    jsonObject.put(CLIENTES_CIE_LONGITUD, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_CIE_LONGITUD)));
                    jsonObject.put(CLIENTES_CIE_HORA, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_CIE_HORA)));

                    jsonObject.put(CLIENTES_PAG_PAGO_POR_VENTA_CAPTURADO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_PAG_PAGO_POR_VENTA_CAPTURADO)));
                    jsonObject.put(CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_PAG_NUEVO_ADEUDO_POR_VENTA)));
                    jsonObject.put(CLIENTES_PAG_PAGO_MINIMO_REQUERIDO_PARA_ENTREGA_MERCANCIA, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_PAG_PAGO_MINIMO_REQUERIDO_PARA_ENTREGA_MERCANCIA)));
                    jsonObject.put(CLIENTES_PAG_CUMPLIO_CON_PAGO_MINIMO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_PAG_CUMPLIO_CON_PAGO_MINIMO)));
                    jsonObject.put(CLIENTES_PAG_LATITUD, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_PAG_LATITUD)));
                    jsonObject.put(CLIENTES_PAG_LONGITUD, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_PAG_LONGITUD)));
                    jsonObject.put(CLIENTES_PAG_HORA, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_PAG_HORA)));


                    jsonObject.put(CLIENTES_ENT_CANTIDAD_CREDITO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_CANTIDAD_CREDITO)));
                    jsonObject.put(CLIENTES_ENT_CANTIDAD_CONTADO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_CANTIDAD_CONTADO)));
                    jsonObject.put(CLIENTES_ENT_CANTIDAD_REGALOS, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_CANTIDAD_REGALOS)));
                    jsonObject.put(CLIENTES_ENT_IMPORTE_CREDITO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_IMPORTE_CREDITO)));
                    jsonObject.put(CLIENTES_ENT_IMPORTE_CONTADO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_IMPORTE_CONTADO)));
                    jsonObject.put(CLIENTES_ENT_IMPORTE_REGALOS, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_IMPORTE_REGALOS)));


                    //ponemos estos trycatch independientes a cada uno porue si por ejemplo no se anezaron piezas de contado en el string ira en 0 entonces al crear el JSONArray() con el 0 como inicializador
                    //se crea una excepcion 2019-12-16 20:30:33.246 W/System.err: org.json.JSONException: End of input at character 0 of y entonces salta el trycatch exterior y no sale el objeto completo
                    //creamos un json array que se inicializa con un json array [] y solo si no salta excepcion se llena con los valores de las piezas, si salta la excepcion porque no hay productos
                    //se carga el array vacion en el json objet
                    //ESTO SE RESUELVE EVITANDO LOS VALORES NULL DENTRO DE LA BASE DE DATOS DECLARANDOLA CON UN VALOR DEFAULT YO CREI QUE FUNCIONARIA AUQNE EL CAMPO ESTUVIERA VACIO O FUERA CUALQUIER STRING
                    // PERO NO, AUN ASI DETONA EL ERROR ESO QUIERE DECIR QUE SI NO QUIERO USAR BLOQUES TRY CATCH EL VALOR POR DEFAULT
                    // DEBE D EVENIR EN FORMA DE UN JSONARRAY AUNQUE SEA VACIO Y LA FORMA ES '[]' AUNQUE NO SE LLENE AL CREAR EL JSON ARRAY SE CREARA VACIO EN AUTOMARTICO
                    //PERO IGUAL DEJO LOS TRY CATCH PORQUE SI EN ALGUN MOMENTO SE GUARDARA ALGO EN ESE CAMPO CON UN FORMATO INCORRECTO DE JSON ARRAY ENTONCES
                    //SE ACTIVARIA LA EXCEPCION AQUI PERO AL NO TENER TRY CATCH INTERIOR QUE LA MANEJE SALTARIA AL EXTERIOR QUE ES EL QUE VACIA TODOS LOS DATOS
                    //ENTONCES AL HACER ESO NO SE IMPRIMIRIA NINGUN DATO DEL CLIENTE AL SISTEMA. AL PONER EL TRY CATCH INTERIOR A CADA RENGLON QUE PODRIA DAR PROBLEMA
                    //SE ENCAPSULA EL ERROR SOLO DE ESE RENGLON Y LO DEMAS PUEDE IMPRIMIRSE

                    //PORQUE POR EJEMPLO EL METODO QUE CONVIERTE LA INFO EN JSON ARRAY PARA METERLA EN EL CAMPO, SI OCURRE UN ERROR NO LO HE MODIFICADO PARA QUE DEVUELVA UN VALOR
                    //CON FORMATO JSON VACIO, ENTONCES LO QUE PASSA ES QUE AUNQUE LA BASE TENGA EL DEFAULT '[{}]' EL METODO QUE CONSULTA LAS PIEZAS SI NO LAS ENCUENTRA DEVUELVE UN VALOR
                    //YA SEA NULL O "" STRING VACIO Y COMO SEA SE DARA EL ERROR AQUI ENTONCES DEJAMOS LOS TRY CATCH PARA CADA CONVERSION

                    JSONArray todosArray = new JSONArray();
                    try {
                        todosArray = new JSONArray(datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ENT_DESCRIPCION_ENTREGA)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    jsonObject.put(CLIENTES_ENT_DESCRIPCION_ENTREGA, todosArray);


                    JSONArray creditoArray = new JSONArray();
                    try {
                        creditoArray = new JSONArray(datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ENT_DESCRIPCION_ENTREGA_CREDITO)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    jsonObject.put(CLIENTES_ENT_DESCRIPCION_ENTREGA_CREDITO, creditoArray);


                    JSONArray contadoArray = new JSONArray();
                    try {
                        contadoArray = new JSONArray(datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ENT_DESCRIPCION_ENTREGA_CONTADO)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    jsonObject.put(CLIENTES_ENT_DESCRIPCION_ENTREGA_CONTADO, contadoArray);


                    JSONArray regaloArray = new JSONArray();
                    try {
                        regaloArray = new JSONArray(datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ENT_DESCRIPCION_ENTREGA_REGALOS)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    jsonObject.put(CLIENTES_ENT_DESCRIPCION_ENTREGA_REGALOS, regaloArray);

                    jsonObject.put(CLIENTES_ENT_PUNTOS_GANADOS_CONTADO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_PUNTOS_GANADOS_CONTADO)));
                    jsonObject.put(CLIENTES_ENT_PUNTOS_TOTALES, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_PUNTOS_TOTALES)));
                    jsonObject.put(CLIENTES_ENT_PUNTOS_CANJEADOS, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_PUNTOS_CANJEADOS)));
                    jsonObject.put(CLIENTES_ENT_PUNTOS_RESTANTES, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_PUNTOS_RESTANTES)));
                    jsonObject.put(CLIENTES_ENT_PROHIBIDO_ENTREGAR_MERCANCIA_CREDITO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_PROHIBIDO_ENTREGAR_MERCANCIA_CREDITO)));
                    jsonObject.put(CLIENTES_ENT_PROHIBIDO_CANJEAR_PUNTOS, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_PROHIBIDO_CANJEAR_PUNTOS)));
                    jsonObject.put(CLIENTES_ENT_NUEVO_CREDITO_CALCULADO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_NUEVO_CREDITO_CALCULADO)));
                    jsonObject.put(CLIENTES_ENT_NUEVOS_DIAS_DE_CREDITO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_NUEVOS_DIAS_DE_CREDITO)));
                    jsonObject.put(CLIENTES_ENT_NUEVOS_GRADO_CALCULADO, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ENT_NUEVOS_GRADO_CALCULADO)));
                    jsonObject.put(CLIENTES_ENT_FECHA_INICIAL, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ENT_FECHA_INICIAL)));
                    jsonObject.put(CLIENTES_ENT_FECHA_VENCIMIENTO, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_ENT_FECHA_VENCIMIENTO)));
                    jsonObject.put(CLIENTES_ENT_DIFERENCIA_EXCESO_CREDITO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_ENT_DIFERENCIA_EXCESO_CREDITO)));

                    jsonObject.put(CLIENTES_PA2_PAGO_DIFERENCIA_REGALO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_PA2_PAGO_DIFERENCIA_REGALO)));
                    jsonObject.put(CLIENTES_PA2_PAGO_POR_VENTA_CONTADO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_PA2_PAGO_POR_VENTA_CONTADO)));
                    jsonObject.put(CLIENTES_PA2_PAGO_CAPTURADO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_PA2_PAGO_CAPTURADO)));
                    jsonObject.put(CLIENTES_PA2_LATITUD, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_PA2_LATITUD)));
                    jsonObject.put(CLIENTES_PA2_LONGITUD, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_PA2_LONGITUD)));
                    jsonObject.put(CLIENTES_PA2_HORA, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_PA2_HORA)));

                    jsonObject.put(CLIENTES_FIN_FINALIZADO, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_FIN_FINALIZADO)));
                    jsonObject.put(CLIENTES_FIN_FIRMA_DE_VOZ, datosClientes.getInt(datosClientes.getColumnIndex(CLIENTES_FIN_FIRMA_DE_VOZ)));
                    jsonObject.put(CLIENTES_FIN_HORA, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_FIN_HORA)));
                    jsonObject.put(CLIENTES_FIN_LATITUD, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_FIN_LATITUD)));
                    jsonObject.put(CLIENTES_FIN_LONGITUD, datosClientes.getString(datosClientes.getColumnIndex(CLIENTES_FIN_LONGITUD)));

                    return jsonObject;

                } catch (JSONException e) {
                    e.printStackTrace();
                }


        }



        return jsonObject;

    }



    //PAGOS

    void clientes_pagosEliminaPagoExtraParaGanarPuntos(String cuentaCliente){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CLIENTES_CIE_PAGO_EXTRA_PARA_GANAR_PUNTOS_LLENAR,0);
        db.update(TABLA_CLIENTES,contentValues, CLIENTES_ADMIN_CUENTA_CLIENTE + " LIKE ?", new String[]{cuentaCliente});
        db.close();
    }



    int clientes_calcularPuntosGanadosVentaAlContado (String cuentaCliente){
        int importeEntregaContado = entrega_mercancia_calcularImporteEntrega(cuentaCliente, RealizarEntrega.CONTADO);

        int ventaInverior = 0;
        int ventaSuperior = 500;

        int puntosGanados = 50;

        while (puntosGanados<5000){

            if (importeEntregaContado>=ventaSuperior){
                puntosGanados+=50;
                ventaSuperior+=250;
            }else{
                if (importeEntregaContado<500){
                    return 0;
                }
                else{
                    return puntosGanados;
                }
            }

        }






       return 0;
    }






    int devolucion_mercancia_eliminaRenglonPorKeyID(int keyID){

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA_DEVOLUCION_MERCANCIA, DEVOLUCION_MERCANCIA_ROW_ID + " = " + keyID, null);
    }

    Cursor devolucion_mercancia_dameDevolucion(String cuentaCliente){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLA_DEVOLUCION_MERCANCIA + " WHERE " + DEVOLUCION_MERCANCIA_CUENTA_CLIENTE + " = ?";
        return db.rawQuery(sql, new String[] {cuentaCliente});
    }

    int devolucion_mercnacia_eliminarDevolucion(String idNumeroCuenta){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA_DEVOLUCION_MERCANCIA, DEVOLUCION_MERCANCIA_CUENTA_CLIENTE + " = ?", new String[]{idNumeroCuenta});
    }

    int devolucion_mercancia_calcularImporteDevuelto(String idCuenta){
        int totalImporte = 0;

        Cursor calculoEntradas = devolucion_mercancia_dameDevolucion(idCuenta);

        if (calculoEntradas.getCount()>0){
            calculoEntradas.moveToFirst();
            do{
                int piezas = calculoEntradas.getInt(calculoEntradas.getColumnIndex(DEVOLUCION_MERCANCIA_CANTIDAD));
                int importe = calculoEntradas.getInt(calculoEntradas.getColumnIndex(DEVOLUCION_MERCANCIA_DISTRIBUCION));
                totalImporte += importe * piezas;
            }while (calculoEntradas.moveToNext());
        }

        return totalImporte;
    }

    int devolucion_mercancia_calcularPiezasDevueltas(String idCuenta){
        int totalPiezasDevueltas = 0;

        Cursor calculoEntradas = devolucion_mercancia_dameDevolucion(idCuenta);

        if (calculoEntradas.getCount()>0){
            calculoEntradas.moveToFirst();
            do{
                int piezas = calculoEntradas.getInt(calculoEntradas.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_CANTIDAD));
                totalPiezasDevueltas += piezas;
            }while (calculoEntradas.moveToNext());

        }


        return totalPiezasDevueltas;
    }

    public long devolucion_mercancia_insertarProducto(ContentValues cv){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TABLA_DEVOLUCION_MERCANCIA, null, cv);
    }

    void devolucion_afectaInventarioConDevoluciones (String cuentaCliente){
        //lo usamos para afectar el inventario del agente con los movimientos capturados
        Cursor cursor = devolucion_mercancia_dameDevolucion(cuentaCliente);
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            do{

                String codigoProducto = cursor.getString(cursor.getColumnIndex(DEVOLUCION_MERCANCIA_CODIGO));
                String cantidad = cursor.getString(cursor.getColumnIndex(DEVOLUCION_MERCANCIA_CANTIDAD));

                inventario_incrementaInventario(codigoProducto,cantidad);

            }while (cursor.moveToNext());
        }
    }

    String devolucion_mercancia_consultarDevolucionEnJsonArrayString(String numeroDeCuentaCliente){

        JSONArray jsonArray = new JSONArray();



        Cursor descripcionPiezas = devolucion_mercancia_dameDevolucion(numeroDeCuentaCliente);


        if (descripcionPiezas.getCount()>0) {
            descripcionPiezas.moveToFirst();

            do {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(DataBaseHelper.DEVOLUCION_MERCANCIA_CODIGO, descripcionPiezas.getString(descripcionPiezas.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_CODIGO)));
                    jsonObject.put(DataBaseHelper.DEVOLUCION_MERCANCIA_CANTIDAD, descripcionPiezas.getString(descripcionPiezas.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_CANTIDAD)));
                    jsonObject.put(DataBaseHelper.DEVOLUCION_MERCANCIA_PRECIO, descripcionPiezas.getInt(descripcionPiezas.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_PRECIO)));
                    jsonObject.put(DataBaseHelper.DEVOLUCION_MERCANCIA_DISTRIBUCION, descripcionPiezas.getInt(descripcionPiezas.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_DISTRIBUCION)));
                    jsonObject.put(DataBaseHelper.DEVOLUCION_MERCANCIA_GANANCIA, descripcionPiezas.getInt(descripcionPiezas.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_GANANCIA)));
                    jsonObject.put(DataBaseHelper.DEVOLUCION_MERCANCIA_LATITUD, descripcionPiezas.getString(descripcionPiezas.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_LATITUD)));
                    jsonObject.put(DataBaseHelper.DEVOLUCION_MERCANCIA_LONGUITUD, descripcionPiezas.getString(descripcionPiezas.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCANCIA_LONGUITUD)));
                    jsonObject.put(DataBaseHelper.DEVOLUCION_MERCACIA_HORA_CAPTURA, descripcionPiezas.getString(descripcionPiezas.getColumnIndex(DataBaseHelper.DEVOLUCION_MERCACIA_HORA_CAPTURA)));

                    jsonArray.put(jsonObject);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (descripcionPiezas.moveToNext());





        }


        return jsonArray.toString();



    }



    Cursor entrega_mercancia_dameEntrega(String cuentaCliente, int creditoRegaloContadoTodos){
        SQLiteDatabase db = this.getWritableDatabase();

        String sql;

        if (creditoRegaloContadoTodos == RealizarEntrega.MOSTRAR_TODOS){
            //sql = "SELECT * FROM " + TABLA_ENTREGA_MERCANCIA + " WHERE " + ENTREGA_MERCANCIA_CUENTA_CLIENTE + " = ? ORDER BY " + ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO + " ASC";
            sql = "SELECT * FROM " + TABLA_ENTREGA_MERCANCIA + " WHERE " + ENTREGA_MERCANCIA_CUENTA_CLIENTE + " = ?";
        }else{
            sql = "SELECT * FROM " + TABLA_ENTREGA_MERCANCIA + " WHERE " + ENTREGA_MERCANCIA_CUENTA_CLIENTE + " = ? AND " + ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO + " = " + creditoRegaloContadoTodos;

        }


        return db.rawQuery(sql, new String[] {cuentaCliente});
    }

    Cursor entrega_mercancia_dameRenglon(int rowId){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLA_ENTREGA_MERCANCIA + " WHERE " + ENTREGA_MERCANCIA_ROW_ID + " = " + rowId;
        return db.rawQuery(sql,null);
    }

    public long entrega_mercancia_insertarProducto(ContentValues cv){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TABLA_ENTREGA_MERCANCIA, null, cv);
    }

    void entrega_mercnacia_eliminarEntrega(String idNumeroCuenta){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLA_ENTREGA_MERCANCIA, ENTREGA_MERCANCIA_CUENTA_CLIENTE + " = ?", new String[]{idNumeroCuenta});
    }

    void entrega_mercancia_eliminaRenglonPorKeyID(int keyID){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLA_ENTREGA_MERCANCIA, ENTREGA_MERCANCIA_ROW_ID + " = " + keyID, null);
    }

    void entrega_mercancia_actualizaRenglon (int keyId, ContentValues contentValues){
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLA_ENTREGA_MERCANCIA,contentValues,ENTREGA_MERCANCIA_ROW_ID + " = " + keyId,null);

    }

    int entrega_mercancia_calcularImporteEntrega(String idCuenta, int creditoRegaloContado){
        int totalImporte = 0;

        Cursor calculoEntregas = entrega_mercancia_dameEntrega(idCuenta, creditoRegaloContado);

        if (calculoEntregas.getCount()>0){
            calculoEntregas.moveToFirst();




            do{



                if (creditoRegaloContado == RealizarEntrega.CREDITO || creditoRegaloContado == RealizarEntrega.CONTADO){
                    int piezas = calculoEntregas.getInt(calculoEntregas.getColumnIndex(DEVOLUCION_MERCANCIA_CANTIDAD));
                    int importe = calculoEntregas.getInt(calculoEntregas.getColumnIndex(DEVOLUCION_MERCANCIA_DISTRIBUCION));
                    totalImporte += importe * piezas;

                }else if (creditoRegaloContado == RealizarEntrega.REGALO){
                    int piezas = calculoEntregas.getInt(calculoEntregas.getColumnIndex(DEVOLUCION_MERCANCIA_CANTIDAD));
                    int importe = calculoEntregas.getInt(calculoEntregas.getColumnIndex(DEVOLUCION_MERCANCIA_PRECIO));
                    totalImporte += importe * piezas;
                }



            }while (calculoEntregas.moveToNext());




        }

        return totalImporte;
    }

    int entrega_mercancia_calcularPiezasEntregadas(String idCuenta, int creditoContadoRegaloTodos){
        int totalPiezasEntregadas = 0;

        Cursor calculoEntradas = entrega_mercancia_dameEntrega(idCuenta,creditoContadoRegaloTodos);

        if (calculoEntradas.getCount()>0){
            calculoEntradas.moveToFirst();
            do{

                int piezas = calculoEntradas.getInt(calculoEntradas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CANTIDAD));
                totalPiezasEntregadas += piezas;
            }while (calculoEntradas.moveToNext());

        }


        return totalPiezasEntregadas;
    }


    void entrega_afectaInventarioConSalidas (String cuentaCliente){
        //lo usamos para afectar el inventario del agente con los movimientos capturados
        Cursor cursor = entrega_mercancia_dameEntrega(cuentaCliente,RealizarEntrega.MOSTRAR_TODOS);
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            do{

                String codigoProducto = cursor.getString(cursor.getColumnIndex(DEVOLUCION_MERCANCIA_CODIGO));
                String cantidad = cursor.getString(cursor.getColumnIndex(DEVOLUCION_MERCANCIA_CANTIDAD));

                inventario_decrementaInventario(codigoProducto,cantidad);

            }while (cursor.moveToNext());
        }
    }


    String entrega_mercancia_consultarEntregaEnJsonArrayString(String numeroDeCuentaCliente, int creditoRegaloContadoTodos){

        JSONArray jsonArray = new JSONArray();


        Cursor descripcionPiezas = entrega_mercancia_dameEntrega(numeroDeCuentaCliente, creditoRegaloContadoTodos);


        if (descripcionPiezas.getCount()>0) {
            descripcionPiezas.moveToFirst();

            do {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put(DataBaseHelper.ENTREGA_MERCANCIA_CODIGO, descripcionPiezas.getString(descripcionPiezas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CODIGO)));
                    jsonObject.put(DataBaseHelper.ENTREGA_MERCANCIA_CANTIDAD, descripcionPiezas.getInt(descripcionPiezas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CANTIDAD)));
                    jsonObject.put(DataBaseHelper.ENTREGA_MERCANCIA_PRECIO, descripcionPiezas.getInt(descripcionPiezas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_PRECIO)));
                    jsonObject.put(DataBaseHelper.ENTREGA_MERCANCIA_DISTRIBUCION, descripcionPiezas.getInt(descripcionPiezas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_DISTRIBUCION)));
                    jsonObject.put(DataBaseHelper.ENTREGA_MERCANCIA_GANANCIA, descripcionPiezas.getInt(descripcionPiezas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_GANANCIA)));
                    jsonObject.put(DataBaseHelper.ENTREGA_MERCANCIA_IMPORTE_TOTAL_ENTREGA, descripcionPiezas.getInt(descripcionPiezas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_IMPORTE_TOTAL_ENTREGA)));
                    jsonObject.put(DataBaseHelper.ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO, descripcionPiezas.getInt(descripcionPiezas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_CREDITO_CONTADO_REGALO)));
                    jsonObject.put(DataBaseHelper.ENTREGA_MERCANCIA_GRADO_DE_ENTREGA, descripcionPiezas.getString(descripcionPiezas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_GRADO_DE_ENTREGA)));
                    jsonObject.put(DataBaseHelper.ENTREGA_MERCANCIA_LATITUD, descripcionPiezas.getString(descripcionPiezas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_LATITUD)));
                    jsonObject.put(DataBaseHelper.ENTREGA_MERCANCIA_LONGUITUD, descripcionPiezas.getString(descripcionPiezas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_LONGUITUD)));
                    jsonObject.put(DataBaseHelper.ENTREGA_MERCANCIA_HORA_CAPTURA, descripcionPiezas.getString(descripcionPiezas.getColumnIndex(DataBaseHelper.ENTREGA_MERCANCIA_HORA_CAPTURA)));

                    jsonArray.put(jsonObject);



                } catch (JSONException e) {
                    e.printStackTrace();
                    //return "";
                }

            } while (descripcionPiezas.moveToNext());






        }


        return jsonArray.toString();



    }























    /*String sql = "SELECT m." + KEY_ID + ", m." + MOVEMENTS_ACCOUNT  +", m." + MOVEMENTS_NAME + ", p." + PAGOS_PAGO +
            ", p." + PAGOS_DIFERENCIA + ", p." + PAGOS_SALDO_PENDIENTE + ", p." + PAGOS_OTRO +
            " FROM " + TABLE_MOVEMENTS + " m, " + TABLA_PAGOS + " p " +
            "WHERE m." + KEY_ID + " = p." + PAGOS_IDMOVIMIENTO +
            " AND m." + ESTADO_DE_LA_COLUMNA + " = 'T'";*/






    Cursor dameMovimientos(){
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT m." + KEY_ID + ", m." + MOVEMENTS_ACCOUNT  +", m." + MOVEMENTS_NAME + ", p." + PAGOS_PAGO +
                      ", p." + PAGOS_DIFERENCIA + ", p." + PAGOS_SALDO_PENDIENTE + ", p." + PAGOS_OTRO +
                      " FROM " + TABLE_MOVEMENTS + " m, " + TABLA_PAGOS + " p " +
                      "WHERE m." + KEY_ID + " = p." + PAGOS_IDMOVIMIENTO +
                       " AND m." + ESTADO_DE_LA_COLUMNA + " = 'T'";

        Cursor res = db.rawQuery(sql, null);
        return res;
    }

    Cursor dameMovimientosComprobar (){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(" SELECT * FROM " + TABLE_MOVEMENTS, null);
        return res;

    }

    public long insertaFechaMovimiento(String fechaMovimiento){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DATE_UP, fechaMovimiento);

        return db.insert(TABLE_MOVEMENTS, null, values);

    }

    long terminaMovimiento(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ESTADO_DE_LA_COLUMNA, "T");

        return db.update(TABLE_MOVEMENTS, values, KEY_ID + " = " + Constant.TMPMOV_ID, null);
    }

    long insertaMovimeinto(String Cuenta, String Name, String FechaMovimiento, String FechaVencimiento, String parametrosMovimiento){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MOVEMENTS_ACCOUNT, Cuenta);
        values.put(MOVEMENTS_NAME, Name);
        values.put(MOVEMENTS_REPORT, "");
        values.put(ESTADO_DE_LA_COLUMNA, "A");
        values.put(DATE_UP, FechaMovimiento);
        values.put(MOVEMENTS_EXPIRATION_DATE, FechaVencimiento);
        values.put(MOVEMENTS_CREDIT_CODE, parametrosMovimiento);

        return db.insert(TABLE_MOVEMENTS, null, values);
    }

    int actualizaMovimiento(String id, String ReporteMovimiento){

        Log.d("dbg-qry", id + " : " + ReporteMovimiento);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MOVEMENTS_REPORT, ReporteMovimiento);

        return db.update(TABLE_MOVEMENTS, values, KEY_ID + " = " + id, null);
    }

    int eliminaMovimiento(String idMovimiento){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MOVEMENTS, KEY_ID + " = " + idMovimiento, null);
    }

    int eliminaTodosMovimientos(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MOVEMENTS, null, null);
    }

    int eliminaTodosDetalles(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA_DETALLES, null, null);
    }
    int eliminaTodosPagos(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA_PAGOS, null, null);
    }

    String dameMovimientosTotales(String tipo){
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT SUM(" + DETALLES_CANTIDAD + ") " +
                "FROM " + TABLA_DETALLES + " d, " + TABLE_MOVEMENTS + " m " +
                " WHERE d." + DETALLES_ID_DEL_MOVIMIENTO + " = m." + KEY_ID +
                " AND m." + ESTADO_DE_LA_COLUMNA + " = 'T'" +
                " AND d." + DETALLES_TIPO_MOVIMIENTO + " = '" + tipo + "'";

        Cursor res = db.rawQuery(sql, null);
        res.moveToNext();


        if(res.getString(0) != null){
            return res.getString(0);
        }
        else{
            return "0";
        }

    }

    String dameMovimientosTotalesId(String idMovimiento, String tipo){
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT SUM(" + DETALLES_CANTIDAD + ") " +
                "FROM " + TABLA_DETALLES + " d, " + TABLE_MOVEMENTS + " m " +
                " WHERE d." + DETALLES_ID_DEL_MOVIMIENTO + " = m." + KEY_ID +
                " AND m." + ESTADO_DE_LA_COLUMNA + " = 'T'" +
                " AND m." + KEY_ID + " = " + idMovimiento +
                " AND d." + DETALLES_TIPO_MOVIMIENTO + " = '" + tipo + "'";

        Log.d("dbg-sql", sql);

        Cursor res = db.rawQuery(sql, null);
        res.moveToNext();
        if(res.getString(0) != null) {
            return res.getString(0);
        }
        else{
            return "0";
        }
    }

    String dameUltimoId(String tabla){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT LAST_INSERT_ROWID() FROM " + tabla, null);
        res.moveToNext();
        return res.getString(0);
    }

    String dameUnicoDato(String tabla, String campo, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT " + campo + " FROM " + tabla + " WHERE " + KEY_ID + " = " + id, null);
        res.moveToNext();

        return res.getString(0);

    }

    public Cursor dameRenglon(String tabla, String donde, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + tabla + " WHERE " + donde + " = " + id;

        Log.e("dbg-sql-inventory", sql);

        Cursor res = db.rawQuery(sql, null);

        return res;

    }


    //este es el metodo que se utiliza para asignar los precios en:
    //alta de entrada
    //alta de salida
    //movimiento automatico de almacen en obtener datos desde estring para saber si existe el precio
    //movimiento automatico de almacen en afectar el inventario para añadir el movimiento
//    public Cursor dameInfoProducto(String id){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String sql = "SELECT i." + INVENTARIO_EXISTENCIAS + ", p." + INVENTARIO_PRECIO_VENTA_PRODUCTO + ", p." + INVENTARIO_PRECIO_VENDEDORA + ", p." + INVENTARIO_PRECIO_SOCIA + ", p." + INVENTARIO_PRECIO_EMPRESARIA + " "
//                   + "FROM " + TABLA_INVENTARIO + " AS i, " + TABLE_PRICES  + " AS p "
//                   + "WHERE i." + INVENTARIO_CODIGO_PRODUCTO + " = p." + INVENTARIO_CODIGO_PRODUCTO + " AND i." + INVENTARIO_CODIGO_PRODUCTO + " = " + id;
//
//        Log.e("dbg-sql", sql);
//
//        Cursor res = db.rawQuery(sql, null);
//
//        return res;
//
//    }



    public Cursor dameSalidas(String idMovimiento){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLA_DETALLES + " WHERE " + DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento + " AND " + DETALLES_TIPO_MOVIMIENTO + " = 'S'";

        Log.d("dbg-sal",sql);

        Cursor res = db.rawQuery(sql, null);

        return res;
    }


    public boolean hayRegalos(String idMovimiento){
        //creamos una consulta de la tabla de detalles de las piezas entregadas, filtraremos el campo
        //DETALLES_PRECIO_DISTRIBUCION que sea igual a 0 si la consulta devuelve algun registro significa que si
        //hay productos de regalo, si no retorna nada entonces no hay regalo

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLA_DETALLES + " WHERE " + DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento + " AND " + DETALLES_PRECIO_DISTRIBUCION + " = " + "0" + " AND " + DETALLES_TIPO_MOVIMIENTO + " = 'S'";
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }


    public int obtenerPuntosCanjeados(String idMovimiento){

        int quantity = 0;
        int code = 0;
        int puntosCanjeados = 0;

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLA_DETALLES + " WHERE " + DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento + " AND " + DETALLES_PRECIO_DISTRIBUCION + " = " + "0" + " AND " + DETALLES_TIPO_MOVIMIENTO + " = 'S'";

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0){
            //si hya registros en la base de regalos



            do{
                quantity = cursor.getInt(cursor.getColumnIndex(DETALLES_CANTIDAD));
                code = cursor.getInt(cursor.getColumnIndex(DETALLES_CODIGO_PRODUCTO));
                puntosCanjeados += quantity*code;

            }while(cursor.moveToNext());

            return puntosCanjeados;



        }else{
            return 0;
        }




    }

    public Cursor dameSalidaCodigo(String idMovimiento, String codigoProducto){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLA_DETALLES
                + " WHERE " + DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento
                + " AND " + DETALLES_TIPO_MOVIMIENTO + " = 'S'"
                + " AND " + DETALLES_CODIGO_PRODUCTO + " = " + codigoProducto;

        Log.e("dbg-sql",sql);

        Cursor res = db.rawQuery(sql, null);

        return res;
    }

    public Cursor dameEntradaCodigo(String idMovimiento, String codigoProducto){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLA_DETALLES
                + " WHERE " + DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento
                + " AND " + DETALLES_TIPO_MOVIMIENTO + " = 'E'"
                + " AND " + DETALLES_CODIGO_PRODUCTO + " = " + codigoProducto;

        Log.e("dbg-sql",sql);

        Cursor res = db.rawQuery(sql, null);

        return res;
    }

    public int eliminaPagos(String idMovimiento){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA_PAGOS, PAGOS_IDMOVIMIENTO + " = " + idMovimiento, null);
    }

    public String damePagosTotales(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT SUM(" + PAGOS_PAGO + " + " + PAGOS_DIFERENCIA + " + " + PAGOS_SALDO_PENDIENTE +" + " + PAGOS_OTRO + ") " +
                "FROM " + TABLA_PAGOS, null);
        res.moveToNext();

        if(res.getCount() >=1 ){
            return "$ " + String.valueOf(res.getString(0));
        }
        else{
            return "0";
        }
    }

    public long insertarDetalles(ContentValues cv){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TABLA_DETALLES, null, cv);
    }


    public int eliminaDetalles(String idMovimiento){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLA_DETALLES, DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento, null);
    }



    public int eliminaDetalleSalida(String idMovimiento, String codigo, String idRow){

        Log.d("dbg-funcion", DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento + " AND " + DETALLES_CODIGO_PRODUCTO + " = " + codigo);

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLA_DETALLES, DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento + " AND " + DETALLES_CODIGO_PRODUCTO + " = " + codigo + " AND " + DETALLES_TIPO_MOVIMIENTO + " = 'S' AND " + KEY_ID + " = " + idRow, null);

    }












    public Cursor damePagos(String idMovimiento){

        Log.d("dbg-daemPagos: ", "SELECT * FROM pagos WHERE id_mov = " + idMovimiento);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLA_PAGOS + " WHERE " + PAGOS_IDMOVIMIENTO + " = " + idMovimiento, null);
        return res;
    }

    public long insertaPagos(String idMovimiento){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PAGOS_IDMOVIMIENTO, idMovimiento);
        values.put(ESTADO_DE_LA_COLUMNA, "A");
        values.put(DATE_UP, utilidadesApp.dameFecha());

        return db.insert(TABLA_PAGOS, null, values);
    }

    public int actualizaPagos(String idMovimiento, String concepto, String valor, String registro, String hora){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(concepto, valor);
        values.put(registro, hora);
        values.put(PAGOS_LATITUD, Constant.INSTANCE_LATITUDE);
        values.put(PAGOS_LONGITUD, Constant.INSTANCE_LONGITUDE);

        return db.update(TABLA_PAGOS, values, PAGOS_IDMOVIMIENTO + " = " + idMovimiento, null);
    }



    public void dbgTabla(String tabla){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + tabla, null);
        res.moveToFirst();

        do{
            Log.d("dbg-select", res.getString(0) + " | " + res.getString(1) + " | " + res.getString(2) + " | " + res.getString(3) + " | " +
                    res.getString(4) + " | " + res.getString(5) + " | " + res.getString(6) + " | ");
        }
        while(res.moveToNext());


    }

    public Cursor dameCabeceraMovimiento(String idMovimiento, String cuentaCliente){
        SQLiteDatabase db = this.getWritableDatabase();
        //(CREAMOS UNA COPIA DE ESTE METODO ABAJO, A ESTE LE MODIFICAMOS DEL ORIGINAL LA BUSQUEDA DEL CODIGO DE CREDITO DE LA TABLA CREDIT CODE, YA QUE LO ESTAMOS QUITANDO
        // EL UNICO INCONVENIENTE ES QUE AL IMPRIMIR EL TIKET DEL CLIENTE NO PODEMOS SUSTRAER SU GRADO DE VENTA O CUANDO SE EXPORTEN LOS MOVIMIENTOS
        // DE IGUAL FORMA NO PODEMOS REPRECENTAR SU GRADO LIMITE Y DIAS DE CREDITO, ME APRECE QUE LA OPCION QUE NOS QUEDA ES BUSCAR Y MODIFICAR LA TABLA EN DONDE
        // SE GUARDE EL NOMBRE DEL CLIENTE Y EL NUMERO DE CUENTA Y AHI AÑADIR OTROS 3 CAMPOS MAS PARA AÑADIR EL GRADO ETC.)


        String sql = "SELECT m." + KEY_ID + ", m." + MOVEMENTS_ACCOUNT + ", m." + MOVEMENTS_NAME + ", m." + MOVEMENTS_CREDIT_CODE + ", m." + DATE_UP + ", m." + MOVEMENTS_EXPIRATION_DATE + "," +
                           " p." + PAGOS_PAGO + ", p." + PAGOS_DIFERENCIA + ", p." + PAGOS_SALDO_PENDIENTE + ", p." + PAGOS_OTRO + ", p." + PAGOS_ADEUDO + ", p." + PAGOS_PUNTOS + " " +
                       "FROM " + TABLE_MOVEMENTS + " m, " + TABLA_PAGOS + " p " +
                      "WHERE m." + KEY_ID + " = p." + PAGOS_IDMOVIMIENTO + " " +
                        "AND m." + MOVEMENTS_ACCOUNT + " = '" + cuentaCliente + "' " +
                        "AND m." + KEY_ID + " = " + idMovimiento + " " +
                        "AND m." + ESTADO_DE_LA_COLUMNA + " = 'T' ";

        Log.d("dbg-vista", sql);

        Cursor res =  db.rawQuery(sql , null);

        return res;


// respaldo antes de modificar para quitar el codigo
        //    public Cursor dameCabeceraMovimiento(String idMovimiento, String cuentaCliente){
//        SQLiteDatabase db = this.getWritableDatabase();
//
//
//        String sql = "SELECT m." + KEY_ID + ", m." + MOVEMENTS_ACCOUNT + ", m." + MOVEMENTS_NAME + ", m." + MOVEMENTS_CREDIT_CODE + ", m." + DATE_UP + ", m." + MOVEMENTS_EXPIRATION_DATE + "," +
//                " p." + PAGOS_PAGO + ", p." + PAGOS_DIFERENCIA + ", p." + PAGOS_SALDO_PENDIENTE + ", p." + PAGOS_OTRO + ", p." + PAGOS_ADEUDO + ", p." + PAGOS_PUNTOS + "," +
//                " c." + CUSTOMER_GRADE + ", c." + CREDIT_DAYS + " " +
//                "FROM " + TABLE_MOVEMENTS + " m, " + TABLA_PAGOS + " p, " + TABLE_CODES + " c " +
//                "WHERE m." + KEY_ID + " = p." + PAGOS_IDMOVIMIENTO + " " +
//                "AND m." +MOVEMENTS_CREDIT_CODE + " = c." + INVENTARIO_CODIGO_PRODUCTO + " " +
//                "AND m." + MOVEMENTS_ACCOUNT + " = '" + cuentaCliente + "' " +
//                "AND m." + KEY_ID + " = " + idMovimiento + " " +
//                "AND m." + ESTADO_DE_LA_COLUMNA + " = 'T' ";
//
//        Log.d("dbg-vista", sql);
//
//        Cursor res =  db.rawQuery(sql , null);
//
//        return res;
//    }
    }








    public Cursor dameMovimientosCompletos(){
        SQLiteDatabase db = this.getWritableDatabase();


        String sql = "SELECT m." + KEY_ID + ", m." + MOVEMENTS_ACCOUNT + ", m." + MOVEMENTS_NAME + ", m." + DATE_UP + ", m." + MOVEMENTS_EXPIRATION_DATE + "," +
                " p." + PAGOS_PAGO + ", p." + PAGOS_ADEUDO + ", p." + PAGOS_PUNTOS + "," +
                " m." + MOVEMENTS_REPORT + "," +
                " p." + PAGOS_DIFERENCIA + ", p." + PAGOS_SALDO_PENDIENTE + ", p." + PAGOS_OTRO + ", " +
                " p." + PAGOS_PAGOS_HORA + ", p." + PAGOS_HORA_DIFERENCIA + ", p." + PAGOS_HORA_SALDO_PENDIENTE + ", " +
                " p." + PAGOS_HORA_OTRO + ", p." + PAGOS_HORA_ADEUDO + ", p." + PAGOS_HORA_PUNTOS + " " +
                "FROM " + TABLE_MOVEMENTS + " m, " + TABLA_PAGOS + " p " +
                "WHERE m." + KEY_ID + " = p." + PAGOS_IDMOVIMIENTO + " " +
                "AND m." + ESTADO_DE_LA_COLUMNA + " = 'T' ";

        Log.d("dbg-vista", sql);

        Cursor res =  db.rawQuery(sql , null);

        return res;

        //Antes de eliminar el codigo
//    public Cursor dameMovimientosCompletos(){
//        SQLiteDatabase db = this.getWritableDatabase();
//
//
//        String sql = "SELECT m." + KEY_ID + ", m." + MOVEMENTS_ACCOUNT + ", m." + MOVEMENTS_NAME + ", m." + MOVEMENTS_CREDIT_CODE + ", m." + DATE_UP + ", m." + MOVEMENTS_EXPIRATION_DATE + "," +
//                " p." + PAGOS_PAGO + ", p." + PAGOS_ADEUDO + ", p." + PAGOS_PUNTOS + "," +
//                " c." + CUSTOMER_GRADE + ", c." + CREDIT_DAYS + " " + ", m." + MOVEMENTS_REPORT + "," +
//                " p." + PAGOS_DIFERENCIA + ", p." + PAGOS_SALDO_PENDIENTE + ", p." + PAGOS_OTRO + ", " +
//                " p." + PAGOS_PAGOS_HORA + ", p." + PAGOS_HORA_DIFERENCIA + ", p." + PAGOS_HORA_SALDO_PENDIENTE + ", " +
//                " p." + PAGOS_HORA_OTRO + ", p." + PAGOS_HORA_ADEUDO + ", p." + PAGOS_HORA_PUNTOS + " " +
//                "FROM " + TABLE_MOVEMENTS + " m, " + TABLA_PAGOS + " p, " + TABLE_CODES + " c " +
//                "WHERE m." + KEY_ID + " = p." + PAGOS_IDMOVIMIENTO + " " +
//                "AND m." +MOVEMENTS_CREDIT_CODE + " = c." + INVENTARIO_CODIGO_PRODUCTO + " " +
//                "AND m." + ESTADO_DE_LA_COLUMNA + " = 'T' ";
//
//        Log.d("dbg-vista", sql);
//
//        Cursor res =  db.rawQuery(sql , null);
//
//        return res;
//    }
    }





    public Cursor dameDetallesCompletos(String idMovimiento){
        SQLiteDatabase db = this.getWritableDatabase();


        String sql = "SELECT d.* " +
                "FROM " + TABLA_DETALLES + " d, " + TABLE_MOVEMENTS + " m " +
                "WHERE m." + KEY_ID + " = d." + DETALLES_ID_DEL_MOVIMIENTO + " " +
                "AND m." + ESTADO_DE_LA_COLUMNA + " = 'T' " +
                "AND d." + DETALLES_ID_DEL_MOVIMIENTO + " = " + idMovimiento + " " +
                "ORDER BY d." + DETALLES_TIPO_MOVIMIENTO + " DESC" ;

        Cursor res =  db.rawQuery(sql , null);
        Log.d("dbg-vista", String.valueOf(res.getCount()));
        return res;
    }





}


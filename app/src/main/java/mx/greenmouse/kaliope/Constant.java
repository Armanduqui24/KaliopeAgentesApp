package mx.greenmouse.kaliope;

/**
 * Created by Ab on 09/05/2017.
 */
public class Constant {
    public static final String FIRST_COLUMN = "First";
    public static final String SECOND_COLUMN = "Second";
    public static final String THIRD_COLUMN = "Third";
    public static final String FOURTH_COLUMN = "Fourth";
    public static final String FIVE_COLUMN = "Five";
    public static final String SIX_COLUMN = "Six";
    public static final String SEVEN_COLUMN = "Seven";
    public static final String EIGHT_COLUMN = "eight";
    public static final String NINE_COLUMN = "nine";
    public static final String TEN_COLUMN = "ten";
    public static final String ELEVEN_COLUMN = "eleven";
    public static final String TWELVE_COLUMN = "twelve";
    public static final String THIRTEEN_COLUMN = "thirteen";
    public static final String FOURTEEN_COLUMN = "fourteen";
    public static final String FIFTEEN_COLUMN = "fifteen";
    public static final String SIXTEEN_COLUMN = "sixteen";
    public static final String SEVENTEEN_COLUMN = "seventeen";
    public static final String EIGHTEEN_COLUMN = "eighteen";
    public static final String NINETEEN_COLUMN = "nineteen";
    public static final String TWENTY_NINE_COLUMN = "twentyNineColumn";
    public static final String THIRTY_COLUMN = "thirtyColumn";
    public static final String THIRTY_ONE_COLUMN = "thirtyOneColumn";
    public static final String THIRTY_SECOND_COLUMN = "thirtySecondColumn";
    public static final String THIRTY_THIRD_COLUMN = "thirtyTirdColumn";



    public static String INSTANCE_PATH = "";
    public static String INSTANCE_OWNER = "";
    public static String INSTANCE_ROUTE = "";
    public static String INSTANCE_DATE  = "";

    public static String INSTANCE_LATITUDE = "";
    public static String INSTANCE_LONGITUDE = "";
    public static Boolean INSTANCE_GPS = false;

    public static String INSTANCE_STOCK = null;
    public static String INSTANCE_AMOUNT = null;

    public static Boolean INSTANCE_DB = false;
    public static Boolean INSTANCE_DB_PRICES = false;
    public static Boolean INSTANCE_DB_CODES = false;

    public static String INTANCE_PRINT_SEPARATOR = " ";
    public static String INTANCE_PRINT_COMPANY = "Kaliope Distribuidora SA de CV\n";
    public static String INSTANCE_UBI = "K25695731";        //este sera el password unibersal para emergencias, lo hago solo por si alguna ves ocurre algun error con la app y no sabemos que pulsera es la que se espera o algo asi ingresar directo con este codigo. le ponemos un nombre extraño por si al apk le hacen ingenieria inversa, recuerdo que lo hice con la que me mando abraham y se podian ver las constantes


    public static Boolean MOVIMIENTO_INICIADO = false; // CAMBIA A TRUE DESPUES PARA INDICAR SI EL MOVIMIENTO SE INICIO CORRECTAMENTE
    public static String TMPMOV_ID = "";
    public static String TMPMOV_CREDIT_CODE = "";
    public static String TMPMOV_GRADE = "";
    public static String TMPMOV_DAYS = "";
    public static String TMPMOV_LIMIT = "";
    public static String TMPMOV_TOPAY = "";
    public static String TMPMOV_DATE = "";
    public static String TMPMOV_TOREFUND = "";
    public static String TMPMOV_EXPIRATION_DATE = "";
    public static String TMP_ADEUDO_CLIENTE = "";           //ALMACENARA EL ADEUDO PENDIENTE ALMACENADO EN LA BASE DE DATOS CLIENTES
    public static String TMP_ACARGO = "";                   //ALMACENA EL MONTO DE MERCANCIA QUE TIENE ACARGO EL CLIENTE ALMACENADO
    public static String TMP_PUNTOS_DISPONIBLES = "";        //ALMACENA LOS CLIENTES_ADMIN_PUNTOS_DISPONIBLES DISPONIBLES DEL CLIENTE ALMACENDO EN LA BASE DE DATOS CLIENTES
    public static String TMP_FECHA = "";


    public static String ESTADO_DE_LA_COLUMNA_ACTIVO = "activo";
    public static String ESTADO_DE_LA_COLUMNA_TERMINADO = "terminado";
    public static String TIPO_DE_MOVIMIENTO_ENTRADA_AL_INVENTARIO = "entrada";
    public static String TIPO_DE_MOVIMIENTO_SALIDA_DEL_INVENTARIO = "salida";



    public static boolean PERMISOS_NECESARIOS_OTORGADOS = false;


    public static int QUIEN_LLAMA = 0; //tiene 0 si lo llamo vista movimientos o 1 si lo llamo desde clientes a activyti altaMovimiento


    public static String TMPMOV_ACCOUNT = "";
    public static String TMPMOV_CLIENT = "";
    public static Boolean TMPMOV_REPORT = false;

    public static Boolean TMPMOV_INPUT = false;
    public static Boolean TMPMOV_OUTPUT = false;
    public static Boolean TMPMOV_PAYMENT = false;


    public static int TMPMOV_TICKET = 0;

    public static int ID_CLIENTE=0; //aqui guardaremos el id que envien las pantallas detalles cleintes


    /**
     * Version 6.0
     */

    public static final String CIERRE_A_TIEMPO = "aTiempo";
    public static final String CIERRE_ADELANTADO = "adelantado";
    public static final String CIERRE_TARDE = "tarde";
    public static final String CIERRE_INVALIDO = "invalido";


    public static int VENTA_GENERADA;//se guardara la venta generada en el activity Cierre.Java
    public static String MOMENTO_DE_CIERRE;//se guardara "aTiempo", "adelantado","tarde", "invalido" dependiendo del resultado del metodo compararFechas en cierre.java
    public static int PAGO_INGRESADO;//se guarda el pago ingresado en el activity PagosLuisda.java



    //contantes necesairas para construir el mensaje que se enviara en whats app luisda 22-05-2018
    //se utilizan en AltaEntrada
    public static int PIEZAS_DEVUELTAS = 0;
    public static String IMPORTE_DEVUELTO = "";
    public static int PIEZAS_DEVUELTAS2 = 0;
    public static boolean HAY_REGALOS = false;
    public static int PUNTOS_CANJEADOS = 0;


    //se utilizan en altaSalida
    public static int PIEZAS_ENTREGADAS = 0;
    public static int IMPORTE_ENTREGADO = 0;
    public static int PIEZAS_ENTREGADAS2 = 0;
    public static int IMPORTE_ENTREGADO2 =0;


    //Se utilizan en altaPagos
    public static int PAGO_TOTAL = 0;
    public static int SALDO_PENDIENTE = 0;
    public static int PUNTOS = 0;
    public static int PAGO_TOTAL2 = 0;
    public static int SALDO_PENDIENTE2 = 0;
    public static int PUNTOS2 = 0;


    //se creo la firma de voz
    public static boolean GENERO_FIRMA_VOZ = false;
    public static boolean MANTENER = false;


    //MENSAJE ADMINISTRACION MAS DETALLADOS
    public static String MENSAJE_ENTREGA = ",,,";
    public static String MENSAJE_DEVUELTO = ",,";
    public static String MENSAJE_PAGOS = "";
    public static String MENSAJE_ADMINISTRACION_TEMPORAL ="";


    public static final String ACTIVO = "ACTIVO";
    public static final String REACTIVAR = "REACTIVAR";
    public static final String LIO = "LIO";
    public static final String PROSPECTO = "PROSPECTO";
    public static final String VENDEDORA = "VENDEDORA";
    public static final String SOCIA = "SOCIA";
    public static final String EMPRESARIA = "EMPRESARIA";
    public static final int QUINCE_DIAS = 14;
    public static final int MES = 28;


    public static String CODIGO_BARRAS_PULSERA_CAMARA = "";



    //CONSTANTES PARA LA REALIZACION DEL ACOMODO DE LOS CLIENTES CREADO PORQUE EN VARIOS
    //ACTIVITYS SE EVALUAN PARA CAMBIAR COLORES O PARA FILTRAR LA BASE DE DATOS. DE ESTA
    //MANERA SOLO AL CAMBIAR AQUI EL STRING CAMBIAMOS EN TODOS LOS ACTIVITYES

    public static final String ESTADO_VISITAR = "VISITAR";
    public static final String ESTADO_REPASO = "REPASO";
    public static final String ESTADO_VISITADO = "VISITADO";
    public static final String ESTADO_NO_VISITAR = "NO VISITAR";


    // ESTA CONSTANTE A DIFERENCIA DE LAS DFE ARRIBA NO SE GUARDA EN BASE DE DATOS ES SOLO PARA LOS FILTROS
    public static final String ESTADO_TODOS = "TODOS";




    //FILTROS PARA QUE LA VISTA DE CLIENTES MUESTRE LA LISTA DONDE NOS QUEDAMOS
    //las usamos para "recordar" que lista estaba viendo el agente, para que al
    // regresar de DetallesClientes se actualice y muestre el listView en el mismo lugar
    public static String filtro = ESTADO_VISITAR; //las usamos para "recordar" que lista estaba viendo el agente, para que al regresar de DetallesClientes se actualice y muestre el listView en el mismo lugar



    public static boolean ULTIMOS_DATOS_SINCRONIZADOS = false; //cuando los datos se envien al servidor esta cambiara a true
    //si hay cambios tanto en movimientos como en inventario y clientes cambiara a false, y solo se pondra en true cuando
    //los datos se hayan sicronizado con el servidor, asi la app dejara de conectarse al servidor. se reconectara cuando cambie a false



    public static String[] TOKEN = new String[]{"5TDvEvMC7PoN",
                                                "LB6kLUma4VyD",
                                                 "sqWOi2wJxVSd",
                                                "pMswT7BRmdbL",
                                                "oNxR8e03DlH3",
                                                "3ZLu9K1ZABMA",
                                                "7OP6qFCePrDl",
                                                "EkashILYWWKk",
                                                "DDLoNkFER06c",
                                                "EY6Ra0T7bf7E",
                                                "EkoixZ8xvk87",
                                                "o2aOgjvBT0JR"};

    public static boolean token = true; //cambia a false cuando el token no se valida correctamente se unsa en mainActivity

}

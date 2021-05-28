package mx.greenmouse.kaliope;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MovimientoAutomaticoAlmacen extends AppCompatActivity implements View.OnClickListener{

    ImageButton btActivarEscaner;
    ImageButton btContinuar;

    TextView tvPiezasTotalesProcesadas, tvImporteTotalDelMovimiento, tvTipoDeMovimiento, tvDatosRecuperadosDelQR , tvInformacion;

    ListView listView;

    Animation animationButtonContinue;


    //Son los datos recuperados del codigo qr Escaneado
    int totalPiezasQR = 0;
    int totalImporteQR = 0;
    String tipoMovimientoQR = "";
    String idPulceraQR = "";
    String validadorDeQR = ""; //aqui se guardara el validador que se rescata del qr para este activity sera kaliopeQRA


    //creamos nuestros arrays que almacenaran los codigos y las piezas para poder afectar el inventario
    ArrayList <String> codigosCapturados = new ArrayList<String>();
    ArrayList <String> piezasCapturadas = new ArrayList<String>();

    Activity activity;



    //Son los datos que se calculan una ves que se leyo el qr
    int totalPiezasCalculadas = 0;
    int totalImporteCalculado = 0;



    DataBaseHelper dbHelper = new DataBaseHelper(this);


    private ArrayList <HashMap> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimiento_automatico_almacen);
        getSupportActionBar().hide();
        activity = this;

        btActivarEscaner =(ImageButton) findViewById(R.id.Button104);
        tvPiezasTotalesProcesadas = (TextView)findViewById(R.id.textView107);
        tvImporteTotalDelMovimiento = (TextView)findViewById(R.id.textView109);
        tvTipoDeMovimiento = (TextView) findViewById(R.id.textView111);
        tvDatosRecuperadosDelQR = (TextView)findViewById(R.id.textView112);
        tvInformacion = (TextView) findViewById(R.id.textView114);

        btContinuar = (ImageButton) findViewById(R.id.Button105);




        listView = (ListView) findViewById(R.id.listView1);

        btActivarEscaner.setOnClickListener(this);
        btContinuar.setOnClickListener(this);
        btContinuar.setVisibility(View.INVISIBLE);





        //nos aseguramos que al entrar a este activity la constante donde se guarda el codigo de barras
        //este vacia.
        Constant.CODIGO_BARRAS_PULSERA_CAMARA = "";
        Intent intent = new Intent(this,LectorBarrayQR.class);
        intent.putExtra("flash",false);
        startActivity(intent);  //enviamos directo al escaner en cuanto se crea la actividad para facilitar el trabajo al agente
        //en el onResume recogemos el dato de la constante, ya que al volver de la actividad de escanear
        //se activa el onresume, y ahi validaremos si ya hay algun dato en la constante de escaneo


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.Button104:
                //Activamos el escaner
                Constant.CODIGO_BARRAS_PULSERA_CAMARA = "";
                nuevoEscaneo();//borramos las vistas del activity
                Intent intent = new Intent(this,LectorBarrayQR.class);
                intent.putExtra("flash",false);
                startActivity(intent);
                break;

            case R.id.Button105:
                //boton Continuar
                confirmacion();
                //Toast.makeText(this,"boton continuar",Toast.LENGTH_SHORT).show();


                break;
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        if (!Constant.CODIGO_BARRAS_PULSERA_CAMARA.equals("")){
            obtenerDatosDesdeString(Constant.CODIGO_BARRAS_PULSERA_CAMARA);
        }


        //Mostramos la informacion improtante el numero de pulcera y el numero de semana
        int numeroSemana = utilidadesApp.getNumeroSemana();
        int valPulsera = ConfiguracionesApp.getNumeroDePulseraAsignada(this);
        //String info = "Pulsera:" + String.valueOf(valPulsera) + " Semana:" + String.valueOf(numeroSemana);// + " Cod Pulsera:" + obtenerCodigoPulsera();
        //tvInformacion.setText(info);

    }


    public void obtenerDatosDesdeString (String cadenaDatos){

        //una ves dentro a este metodo reiniciamos la cosntante que tiene el codigo escaneado
        //porque ya se creo una copia de ella al pasarla como parametro a este metodo
        //asi nos aseguramos que quedara vacia para un siguiente escaneo
        Constant.CODIGO_BARRAS_PULSERA_CAMARA = "";



        //validamos que la cadean no este vaciaa
        if (!cadenaDatos.equals("")){

            //el oden de los datos escaneados en el codigo de QR vendra
            //kaliopeQRA,TOtalpz,importe,id_pulsera,Salida o entrada (S-E),pz,precio,pz,precio,pz,precio.....
            //donde kaliopeQRA sera un validador para indicarle a este metodo que el qr pertenese a una
            //cadena de datos de kaliope generado por la app de Almacen, si no encuentra valido este
            //identificador saltaremos el metodo y mostraremos un mensaje.
            //ejemplo de cadena codificada en QR: kaliopeQRA,73,36067,2569A,S,2,369,1,199,5,499,8,699,3,139,8,129,1,139,20,399,25,699

            String [] dato = cadenaDatos.split(","); //partimos nuestra cadena de datos

            //validamos que en la posicion 0 este el validador de QR
            validadorDeQR = dato[0];


            //si el validador es correcto y se valido correctamente el tipo de movimiento continuamos
            if(validadorDeQR.equals("kaliopeQRA")){


                try {
                    //seteamos nuestras instancias con los datos que hay en el qr
                    totalPiezasQR = Integer.parseInt( dato [1]);
                    totalImporteQR = Integer.parseInt( dato [2]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                //seteamos nuestras instancias con los datos del qr
                idPulceraQR = dato[3];
                tipoMovimientoQR = dato[4];//seteamos el primer validador de movimiento


                //se calcularan y seran usados para setear la listView
                int piezas = 0;
                int precio = 0;
                int importe;


                //creamos el un nuevo objeto de Arrays de Hashmap
                list = new ArrayList<HashMap>();

                //(Ponemos los encabezados
                // en nuestra lista)
                HashMap map = new HashMap();
                map.put(Constant.FIRST_COLUMN,"Pz");
                map.put(Constant.SECOND_COLUMN,"Precio");
                map.put(Constant.THIRD_COLUMN,"Dist");
                map.put(Constant.FOURTH_COLUMN,"Total");
                list.add(map);


                //Apartir de aqui la pieza vendra primero y despues el precio, denuevo la pieza y luego el precio

                for(int posicion = 5; posicion < dato.length ; posicion++){

                    //recuperamos las piezas
                    try {
                        piezas = Integer.parseInt(dato[posicion]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }


                    posicion ++; //sumamos 1 a la posicion para saltar al siguiente dato que seria el codigo de producto

                    try {
                        precio = Integer.parseInt(dato[posicion]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    catch(IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                    //como aumentamos fuera del for el iterador ya ocurrio una vez, la cadena se modifico
                    //con otro elemento, se perdio por lo tanto la estructura de pares de las piezas y el precio
                    //y termino saliendo del indice del array deteniendo la app





                    //Obtenemos los detalles del producto, y verificamos que tenga un inventario cargado
                    //Obtenemos el precio de lista numero 1 del producto
                    Cursor getDetallesProducto = dbHelper.inventario_dameInformacionCompletaDelProducto(String.valueOf(precio));
                    getDetallesProducto.moveToFirst();



                    if (getDetallesProducto.getCount()==0){
                        Toast.makeText(this,"Imposible encontrar un codigo de producto en tu inventario, verifica inventario o catalogo de precios, error en: " + precio,Toast.LENGTH_LONG).show();
                        break;//salimos del bucle for para que deje de recorrer la lista
                    }else {


                        //calculamos el importe pero con el precio recuperado del catalogo de productos
                        //lista 1
                        int precioDistribucion = getDetallesProducto.getInt(getDetallesProducto.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENDEDORA));
                        importe = piezas * precioDistribucion;


                        //calculamos nuestros totales calculados
                        totalPiezasCalculadas += piezas;
                        totalImporteCalculado += importe;






                        //almacenamos en el arrayList los produtos y codigos capturados para asi afectar el inventario
                        //lo hacemos con array list para que sea mas ordenado el codigo y no meter aqui nada de base de datos
                        //porque esto se llevara acabo hasta que se valide que los datos sean correctos y que el usuario
                        //a aceptado afectar el inventario
                        codigosCapturados.add(String.valueOf(precio));
                        piezasCapturadas.add(String.valueOf(piezas));



                        //creamos un Hashmap, que es como una tabla con dos columnas, en 1 de ellas esta la clave
                        //y en la otra columna esta el valor
                        //cada que se utiliza el metodo .put se añade una nueva fila, con diferente clave y diferente valor
                        HashMap map1 = new HashMap();

                        //ponemos en el Hashmap la coleccion de los 4 datos 1 en cada columna
                        map1.put(Constant.FIRST_COLUMN,piezas);
                        map1.put(Constant.SECOND_COLUMN,precio);
                        map1.put(Constant.THIRD_COLUMN,precioDistribucion);
                        map1.put(Constant.FOURTH_COLUMN,importe);


                        //Ahora al arrayList le ponemos este map, el array ira creciendo hasta el numero de
                        //hashmap creados, que dependeran de el numero de elementos en el codigo QR
                        list.add(map1);




                    }

                }



                llenarVistas();

                if (validarDatosEscaneados()){

                    //una ves validados los datos escaneados activamos el boton continuar
                    //animacion para el boton continuar
                    animationButtonContinue = AnimationUtils.loadAnimation(this,R.anim.rotacion);
                    animationButtonContinue.setFillAfter(true);
                    animationButtonContinue.setRepeatMode(Animation.REVERSE);
                    animationButtonContinue.setRepeatCount(Animation.INFINITE);
                    btContinuar.startAnimation(animationButtonContinue);
                    btContinuar.setVisibility(View.VISIBLE);
                    btContinuar.setEnabled(true);

                    //Continuamos inmediatamente a la siguiente actividad
                    //confirmacion(); //este activa el dialogo para el ingreso de los codigos de validacion pero lo haremos mas facil para el agente
                    afectarInventario();//accion a realizar cuando se rpeciona el boton

                }


            }else{
                //si el validador de QR no pertenece a kaliopeQRA entonces mostramos un toast o no se valido correctamente el tipo de movimiento
                Toast.makeText(this,"Codigo QR no pertenece a KaliopeAlmacen se espera:kaliopeQRA   Validador obtenido:" + validadorDeQR,Toast.LENGTH_LONG).show();
            }

        }


    }

    private void afectarInventario () {
        String numeroCuenta;
        String mensajeEntradas = "";//usado pára los mensajes a administracion
        String mensajeSalidas = "";//usado pára los mensajes a administracion
        String mensajePagos = "0,0,0,0,0,0,";//usado pára los mensajes a administracion lo iniciamos en 0 segun documentacion con 6 comas
        String mensajeTemporal = "";
        String mensajeCompleto = "";


        //Obtenemos el id del movimiento de la base de datos numeros cuenta Ahora el id debera ser
        //creado con el metodo donde los movimientos de almacen obtienen los numeros
        //porque deberan ser progresivos, para que al tomar la informacion y enviarla
        //al documento de excel estos datos se pegen en diferentes columnas, es decir
        //no podemos repetir los id
        //tomamos este codigo desde el activity Alta de movimiento y se lo asignamos a la constante
        Cursor res = dbHelper.obtenerNumeroCuenta("ALMACEN");
        res.moveToFirst();
        if (res.getCount() > 0) {
            numeroCuenta = res.getString(res.getColumnIndex(DataBaseHelper.NUMERO_CUENTA));
        } else {
            numeroCuenta = "1";//si por alguna razon no hay datos en la tabla
        }


        //Creamos el movimiento copiado desde AltaMovimientoActivity
        long i = dbHelper.insertaMovimeinto(numeroCuenta,//la cuenta ya la obtubimos arriba
                ConfiguracionesApp.getUsuarioIniciado(activity),//el nombre del cliente ponemos el operador
                utilidadesApp.dameFehaHora(),//la fecha de creacion ponemos la de hoy
                utilidadesApp.dameFecha(),
                "vendedora,0,500000"
                ); //la fecha de vencimiento ponemos la de hoy

        if (i >= 1) {
            Constant.TMPMOV_ID = dbHelper.dameUltimoId(DataBaseHelper.TABLE_MOVEMENTS);
            Log.i("ultimo id",String.valueOf(dbHelper.dameUltimoId(DataBaseHelper.TABLE_MOVEMENTS)));
            Cursor res1 = dbHelper.dameMovimientosComprobar();
            Log.i("Tamaño",String.valueOf(res1.getCount()));
        }








        //creamos el mensaje de entradas o salidas aqui primero llenaremos el improte y las piezas
        //pero dependiendo del tipo de movimiento las pondremos en el mensaje correspondiente
        //despues en el bucle while del iterador llenaremos al descripcion de las piezas
        //recordar que si un mensaje no se utiliza hay que llenarlo para que se reciba correctamente
        //en administracion solo con comas, las entregas osea salidas deben ser 3 comas segun la doc
        //y en decoluciones o entradas deben ser 2 comas si no se utilizo ese mensaje

        if (tipoMovimientoQR.equals("E")){
            mensajeEntradas = totalPiezasCalculadas + "," + totalImporteCalculado + ",";
            mensajeSalidas = ",,";
        }else{
            mensajeSalidas = totalPiezasCalculadas + "," + totalImporteCalculado + ",";
            mensajeEntradas = ",,";
        }











        //Fuente: http://lineadecodigo.com/java/iterar-un-arraylist/
        //recorremos los array list mediante indices y los cargamos en la tabla movimientos
        //creamos los iteradores
        //(Para obtener el iterador sobre el ArrayList llamaremos al método .iterator() del ArrayList
        // y lo almacenaremos en un elemento Iterator.)
        Iterator<String> iterator = codigosCapturados.iterator();
        Iterator<String> iterator1 = piezasCapturadas.iterator();
        String codigo;
        String pieza;


        //mientras siga habiendo datos en amgbos arrays continuamos ingresando a la tabla details los productos

        //(Para iterar un ArrayList vamos a movernos por el iterador mediante los métodos .next().
        // El método .next() realiza dos operaciones, por un lado obtener el elemento sobre el
        // que está iterando y luego mover el puntero hasta el siguiente elemento.)
        //(Realizaremos la operación de iterar un ArrayList hasta que no haya más elementos.
        // Esto lo sabremos cuando el método .hasNext() nos devuelva false.)
        while (iterator.hasNext() && iterator1.hasNext()) {
            codigo = iterator.next();
            pieza = iterator1.next();




            //ingresamos estos datos a la tabla detalles movimientos esto lo haremos seguir la metodologia
            //de alta de salida y alta de entrada, en ellos los datos capturados se guardan en la tabla
            //detalles movimientos, para que al finalizarlos afecten el inventario y ademas
            //aparescan en el listView de todos los movimientos, ya se es darle vuelta en vano
            //pero queremos que aparescan reflejados en los movimientos


            //debemos llamar a la constante que almacena el id, con este id guardan los detalles
            //despues afectamos el inventario con entradas y salidas, y despues
            //se llama al metodo tremina movimiento que cambia A por T de terminado
            //y de esta manera ya semuestran en los movimientos. Ahora el id debera ser
            //creado con el metodo donde los movimientos de almacen obtienen los numeros
            //porque deberan ser progresivos, para que al tomar la informacion y enviarla
            //al documento de excel estos datos se pegen en diferentes columnas, es decir
            //no podemos repetir los id
            //tomamos este codigo desde el activity Alta de movimiento

            Cursor getDetallesProducto = dbHelper.inventario_dameInformacionCompletaDelProducto(String.valueOf(codigo));
            getDetallesProducto.moveToFirst();
            int precioDistribucion = getDetallesProducto.getInt(getDetallesProducto.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENDEDORA));



            //llenamos los mensajes con los datos de los iteradores, los detalles de procutos
            //de igual forma diferenciando que tipo de movimiento es
            if (tipoMovimientoQR.equals("E")){
                mensajeEntradas += pieza + "-" + codigo + " ";
            }else{
                mensajeSalidas += pieza + "-" + codigo + "-" + precioDistribucion + " ";
            }



            ContentValues cv = new ContentValues(10);

            cv.put(dbHelper.DETALLES_ID_DEL_MOVIMIENTO, Constant.TMPMOV_ID);
            cv.put(dbHelper.DETALLES_CANTIDAD, pieza);
            cv.put(dbHelper.DETALLES_PRECIO_PRODUCTO, getDetallesProducto.getString(getDetallesProducto.getColumnIndex(DataBaseHelper.INVENTARIO_PRECIO_VENTA_PRODUCTO)));
            cv.put(dbHelper.DETALLES_PRECIO_DISTRIBUCION, precioDistribucion);
            cv.put(dbHelper.DETALLES_GANANCIA, "0");
            cv.put(dbHelper.DETALLES_CODIGO_PRODUCTO, codigo);
            cv.put(dbHelper.DETALLES_TIPO_MOVIMIENTO, tipoMovimientoQR); //aqui entra "E" o "S" la informacion que entrega el QR
            cv.put(dbHelper.DETALLES_LATITUD, Constant.INSTANCE_LATITUDE);
            cv.put(dbHelper.DETALLES_LONGUITUD, Constant.INSTANCE_LONGITUDE);
            cv.put(dbHelper.ESTADO_DE_LA_COLUMNA, "A");
            cv.put(dbHelper.DATE_UP, utilidadesApp.dameHora());

            dbHelper.insertarDetalles(cv);
        }


        //Ahora decrementamos o incrementamos el inventario copiado desde AltaMovimientoActivity
        //claramente si el producto fue salida el metodo detalles_dameEntradas estara vacio y no se afectara el inventario
        //y vicebersa
        Log.i("afectaInventario", "inicio");
        Cursor resEntradasInv = dbHelper.detalles_dameEntradas(Constant.TMPMOV_ID);

        if (resEntradasInv.getCount() > 0) {

            resEntradasInv.moveToNext();

            do {

                Log.d("dbg-entrada", resEntradasInv.getString(2));

                dbHelper.inventario_incrementaInventario(resEntradasInv.getString(resEntradasInv.getColumnIndex(DataBaseHelper.DETALLES_CODIGO_PRODUCTO)),
                        resEntradasInv.getString(resEntradasInv.getColumnIndex(DataBaseHelper.DETALLES_CANTIDAD)));

            }
            while (resEntradasInv.moveToNext());
        }


        Cursor resSalidasInv = dbHelper.dameSalidas(Constant.TMPMOV_ID);
        resSalidasInv.moveToNext();

        if (resSalidasInv.getCount() > 0) {

            do {

                Log.d("dbg-entrada", resSalidasInv.getString(resSalidasInv.getColumnIndex(DataBaseHelper.DETALLES_CANTIDAD)));

                dbHelper.inventario_decrementaInventario(resSalidasInv.getString(resSalidasInv.getColumnIndex(DataBaseHelper.DETALLES_CODIGO_PRODUCTO)),
                        resSalidasInv.getString(resSalidasInv.getColumnIndex(DataBaseHelper.DETALLES_CANTIDAD)));

            }
            while (resSalidasInv.moveToNext());
        }
        Log.i("afectaInventario", "final");


        //Una ves terminamos de afectar el inventario, terminamos el movimiento de detalles lo pasamos de A a T
        //para que se muestre en todos los movimientos  copiado desde AltaMovimientoActivity
        dbHelper.terminaMovimiento();
        Log.i("terminaTMPMOV", "control1");


        //ahora el numero de cuenta que se utilizo para este movimieto lo pasamos a ocupado
        //(ponemos en ocupado el numero de cuenta que se rescato
        // de la tablanumeros de cuenta pero esto unicamente cuando}
        // llegmaos a este activity desde un cliuente nuevo o desde movimientos
        // de almacen)
        dbHelper.actualizaNumerosCuenta(Integer.parseInt(numeroCuenta));






        //Ahora debemos ingresar un pago pufff lo se es un fastidio pero lo tengo que hacer para que
        //se pueda mostrar este movimiento, tanto en impresion

        dbHelper.insertaPagos(Constant.TMPMOV_ID);

        dbHelper.actualizaPagos(Constant.TMPMOV_ID, dbHelper.PAGOS_PAGO,
                "0",
                dbHelper.PAGOS_PAGOS_HORA,
                utilidadesApp.dameHoraCompleta());

        dbHelper.actualizaPagos(Constant.TMPMOV_ID, dbHelper.PAGOS_DIFERENCIA,
                "0",
                dbHelper.PAGOS_PAGOS_HORA,
                utilidadesApp.dameHoraCompleta());

        dbHelper.actualizaPagos(Constant.TMPMOV_ID, dbHelper.PAGOS_SALDO_PENDIENTE,
                "0",
                dbHelper.PAGOS_PAGOS_HORA,
                utilidadesApp.dameHoraCompleta());

        dbHelper.actualizaPagos(Constant.TMPMOV_ID, dbHelper.PAGOS_OTRO,
                "0",
                dbHelper.PAGOS_PAGOS_HORA,
                utilidadesApp.dameHoraCompleta());

        dbHelper.actualizaPagos(Constant.TMPMOV_ID, dbHelper.PAGOS_ADEUDO,
                "0",
                dbHelper.PAGOS_PAGOS_HORA,
                utilidadesApp.dameHoraCompleta());

        dbHelper.actualizaPagos(Constant.TMPMOV_ID, dbHelper.PAGOS_PUNTOS,
                "0",
                dbHelper.PAGOS_PAGOS_HORA,
                utilidadesApp.dameHoraCompleta());




        //Paso 1: recuperamos todos los mensajes en la tabla para generar el mensaje de administraicon
        String todosLosMensajes = recuperarTodosLosMensajes();
        //Paso 2: recuperar id zona
        String idZona = recuperarIdZona();

        mensajeSalidas += "," + utilidadesApp.dameFecha(); //le ayadimos la fecha de vencimiento como fecha de hoy

        //creamos el mensaje uniendo los demas y llenandolo segun la documentacion
        mensajeTemporal = numeroCuenta + "," + ConfiguracionesApp.getUsuarioIniciado(activity) + "," + mensajeEntradas + "," +
                mensajePagos + "," + mensajeSalidas + "," + utilidadesApp.dameFehaHora() + "," + Constant.PUNTOS_CANJEADOS + ","+
                Constant.INSTANCE_LATITUDE + "," +Constant.INSTANCE_LONGITUDE;

        //creamos el mensaje completo
        mensajeCompleto = "MOVIMIENTO AUTOMATICO DE ALMACEN" +
                "\n\n\n\n\n\n\n\n\n"+
                "?\n" +
                idZona + "\n" +
                todosLosMensajes +
                mensajeTemporal + "\n" +
                "?";


        //(insertamos en nuestra tabla mensajes
        // el mensaje temporal del movimiento)
        dbHelper.insertarMensaje(mensajeTemporal);

        //llamamos a nuestro clipBoard portapapeles
        utilidadesApp.ponerEnPortapapeles(mensajeCompleto,this);









        Toast.makeText(getApplicationContext(), "Movimiento Realizado Correctamente", Toast.LENGTH_LONG).show();


        Log.i("reiniciaTMPMOV", "antes de enviar a vista de movimiento");
        Intent intent = new Intent(this, VistaMovimientoActivity.class);
        intent.putExtra("cuentaCliente", numeroCuenta);
        intent.putExtra("idMovimiento", Constant.TMPMOV_ID);

        //REINICIAMOS LA CONSTANTE
        Constant.TMPMOV_ID = "";
        Constant.ULTIMOS_DATOS_SINCRONIZADOS = false;
        startActivity(intent);
    }

    private boolean validarDatosEscaneados(){
        boolean valida;

        //validamos que los valores calculados por la app sean iguales a los que almacena el QR
        if(totalPiezasCalculadas == totalPiezasQR && totalImporteCalculado == totalImporteQR){

            //ahora validamos que elt ipo de movimiento corresponda a E o S
            if (tipoMovimientoQR.equals("S") || tipoMovimientoQR.equals("E")){

                //Ahora validamos que el codigo de pulcera capturado corresponda con el de la pulcera en curso
                if (idPulceraQR.equals(ConfiguracionesApp.getCodigoPulseraAsignada(activity))){
                    Toast.makeText(this,"Codigo QR Leido con Exito",Toast.LENGTH_LONG).show();
                    valida = true;
                }else{

                    if (ConfiguracionesApp.getCodigoPulseraAsignada(activity).equals("SinValor")){
                        valida = true;
                        //VersionNameLuisda6.5 si por alguna razon no se lograron conectar al servidor y retorna SinValor validamos la carga de mercancia con cualquier codigo de pulsera

                    }else{
                        Toast.makeText(this,"La pulsera de tu dispositivo no concuerda con el movimiento de almacen, revisa que no estes tomando otro Dispositivo ",Toast.LENGTH_LONG).show();
                        Toast.makeText(this,"Pulsera del movimineto almacen:" + idPulceraQR + "Pulsera del dispositivo: " + ConfiguracionesApp.getCodigoPulseraAsignada(activity),Toast.LENGTH_LONG).show();

                        valida = false;
                    }

                }

            }else {
                Toast.makeText(this,"Error de Escaneo Tipo de movimiento corrompido. Reintente!!",Toast.LENGTH_LONG).show();
                valida = false;
            }

    }else{
        Toast.makeText(this,"Error de Escaneo datos con errores, Reintente!!",Toast.LENGTH_LONG).show();
        valida = false;
    }

    return valida;
}


    private void llenarVistas(){

        tvPiezasTotalesProcesadas.setText(String.valueOf(totalPiezasCalculadas));
        tvImporteTotalDelMovimiento.setText(String.valueOf(totalImporteCalculado));

        if (tipoMovimientoQR.equals("E")){
            tvTipoDeMovimiento.setText("Entrada de mercancia a mi Auto");
        }
        if (tipoMovimientoQR.equals("S")){
            tvTipoDeMovimiento.setText("Salida de mercancia de mi Auto");
        }
        //tvTipoDeMovimiento.setText(tipoMovimientoQR);

        String mensaje = "Validador:" + validadorDeQR + " Total pz:" + totalPiezasQR + " ImporteTotal:" + totalImporteQR + " tipoMovimiento:" + tipoMovimientoQR + " idPulsera: " + idPulceraQR;
        tvDatosRecuperadosDelQR.setText(mensaje);

        //ya que acabamos de llenar la lista con los valores llenamos nuestra lista
        //pero primero creando el objeto Adapter que la llenara con el diseño personalizado

        ListViewAdapterDevolucionMercancia listViewAdapterMovimientoAlmacen = new ListViewAdapterDevolucionMercancia(this,list);
        listView.setAdapter(listViewAdapterMovimientoAlmacen);

    }


    private void nuevoEscaneo(){
        //esto para permitir hacer otro escaneo aqui reinicimaos las variables de instancia y seteamos las vistas vacias.
        //para que al volver del escaner los textView y listView esten vacios

        //Son los datos recuperados del codigo qr Escaneado
        totalPiezasQR = 0;
        totalImporteQR = 0;
        tipoMovimientoQR = "";
        idPulceraQR = "";
        validadorDeQR = ""; //aqui se guardara el validador que se rescata del qr para este activity sera kaliopeQRA


        //Son los datos que se calculan una ves que se leyo el qr
        totalPiezasCalculadas = 0;
        totalImporteCalculado = 0;
        codigosCapturados.clear();//borramos los items de los arrays
        piezasCapturadas.clear();
        list = new ArrayList<HashMap>();

        llenarVistas();


        //creamos una animacion de desaparecer y la dejamos ahi cuando el boton desaparese, ya que
        //por la animacion de bamboleo, ignora los setVisibility, y el boton se sigue mostrando

        animationButtonContinue = AnimationUtils.loadAnimation(this,R.anim.desaparecer);
        animationButtonContinue.setFillAfter(true);
        btContinuar.setVisibility(View.INVISIBLE);
        btContinuar.startAnimation(animationButtonContinue);
        btContinuar.setEnabled(false);
    }



    private void confirmacion (){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_confirma_movimiento_automatico,null);

        final TextView tvCodigo = (TextView) view.findViewById(R.id.tvCodigoParaHandHeld);
        final EditText etCodigoIngresado = (EditText) view.findViewById(R.id.etCapturaCodigoAlmacen);

        //(calculamos el codigo
        // para la handheld)
        double temporal = totalPiezasCalculadas * (totalImporteCalculado + totalImporteCalculado);
        int codigoParaHandHeld = (int) Math.ceil(Math.sqrt(temporal));
        tvCodigo.setText(String.valueOf(codigoParaHandHeld));

        //(Calculamos el codigo que
        // recibira de la HandHeld)
        double temoral2 = (totalPiezasCalculadas*totalImporteCalculado);
        final int codigoDesdeHandheld = (int) Math.ceil(Math.sqrt(temoral2));


        builder.setView(view)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int codigoCapturado;
                        //comprobamos que lo
                        // capturado noe ste vacio
                        if (TextUtils.isEmpty(etCodigoIngresado.getText().toString())){
                            Toast.makeText(getApplicationContext(),"El Codigo es Incorrecto",Toast.LENGTH_LONG).show();

                        }else {
                            codigoCapturado = Integer.parseInt(etCodigoIngresado.getText().toString());

                            if (codigoCapturado == codigoDesdeHandheld){
                                afectarInventario();//accion a realizar cuando se rpeciona el boton
                            }else {
                                Toast.makeText(getApplicationContext(),"El Codigo es Incorrecto se espera: " + codigoDesdeHandheld ,Toast.LENGTH_LONG).show();
                            }

                        }




                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.create();
        builder.show();


    }


    /**
     * metodos para obtener los mensajes de las bases de datos
     * @return
     */

    private String recuperarTodosLosMensajes () {
        String todosLosMensajes = "";
        Cursor res = dbHelper.obtenerMensaje();
        //(siempre que la base de datos de mensajes tenga valores
        // llenamos la variable todos los mensajes con todos los datos
        // obtenidos de la tabla mensajes dandole al final de cada mensaje un salto de linea)
        if (res.getCount()>0 ){
            res.moveToFirst();
            do {
                todosLosMensajes += res.getString(res.getColumnIndex(DataBaseHelper.MENSAJES)) + "\n";
            }while(res.moveToNext());
        }
        Log.i("en crear mensaje","3");

        return todosLosMensajes;

    }


    private String recuperarIdZona (){
        //paso 2 recuperamos el idzona
        String idZona = "";
        Cursor resIdZona = dbHelper.tabla_identificador_zona_obtenerIdZona();

        //rescatamos el identificador
        //de zona
        if (resIdZona.getCount()>0){
            resIdZona.moveToFirst();
            idZona = resIdZona.getString(resIdZona.getColumnIndex(DataBaseHelper.ID_ZONA));
        }


        Log.i("en crear mensaje","4");
        return idZona;
    }






}

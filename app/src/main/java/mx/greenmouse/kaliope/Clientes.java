
//DOC Clientes01
package mx.greenmouse.kaliope;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;



public class Clientes extends AppCompatActivity {

    DataBaseHelper dbHelper = new DataBaseHelper(this);
    File folder;
    File docCliente; //contendra el archivo txt de clientes

    private ArrayList<HashMap> clientes;


    private ListView listaClientes;
    Button visitar;
    Button repasos;
    Button visitados;
    Button todos;
    Button noVisitar;


    public static final int DIA_EXACTO_DE_CIERRE = 1;
    public static final int SE_PODRIA_HACER_CIERRE = 2;
    public static final int AUN_NO_TOCA_SU_CIERRE = 3;
    public static final int CUENTA_VENCIDA_MENOR_DE_3_DIAS = 43;
    public static final int CUENTA_VENCIDA_DE_4_A_13_DIAS = 4413;
    public static final int CUENTA_VENCIDA_EXACTO_14_DIAS = 414;
    public static final int CUENTA_VENCIDA_MAS_DE_15_DIAS = 415;

    public static final int URGENTE = 1;
    public static final int ALTO = 2;
    public static final int ATRASO_URGENTE = 3;
    public static final int LIO_URGENTE = 4;
    public static final int NORMAL = 5;
    public static final int LIO_NORMAL = 6;
    public static final int ATRASO = 7;
    public static final int BAJO = 8;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);

        importarDbClientes();
        crearListViewClientes(Constant.filtro); //filtro tiene la palabra "VISITAR" ENTONCES CUANDO SE CREE POR PRIMERA VEZ LA VISTA CLIENTES EL LISTVIEW MOSTRARA LOS CLIENTES POR VISITAR



        visitar = (Button) findViewById(R.id.visitarB);
        repasos = (Button) findViewById(R.id.repasosB);
        visitados = (Button) findViewById(R.id.visitadosB);
        todos = (Button) findViewById(R.id.todosB);
        noVisitar = (Button) findViewById(R.id.noVisitarB);


        visitar.setOnClickListener(mostrarPorVisitar);
        repasos.setOnClickListener(mostrarRepasos);
        visitados.setOnClickListener(mostrarVisitados);
        todos.setOnClickListener(mostrarTodos);
        noVisitar.setOnClickListener(mostrarNoVisitar);





        listaClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap temporal = new HashMap();
                temporal = clientes.get(i);
                Constant.ID_CLIENTE = Integer.parseInt(String.valueOf(temporal.get("id")));

                //Toast.makeText(getApplicationContext(),"clic en: "+ temporal.get("id"),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),DetallesClientes.class);
                intent.putExtra("CLIENTES_ADMIN_CUENTA_CLIENTE", String.valueOf(temporal.get(Constant.FIRST_COLUMN)));
                startActivity(intent);

            }
        });


        //CUANDO EL USUARIO DEJE PRESIONADO AL CLIENTE EN LA LISTA LO ENVIARA AL MAPA Y LE MOSTRARA SU UBICACION
        listaClientes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final HashMap temporal1 = (HashMap) listaClientes.getItemAtPosition(i);
                //Toast.makeText(Clientes.this, temporal1.get(Constant.FIRST_COLUMN).toString(), Toast.LENGTH_SHORT).show();
                //dialogIrMapa(temporal1.get(Constant.FIRST_COLUMN).toString());

                Intent intentViewOnMap = new Intent(getApplicationContext(), MapsActivity.class);
                intentViewOnMap.putExtra("CLIENTES_ADMIN_CUENTA_CLIENTE",temporal1.get(Constant.FIRST_COLUMN).toString());
                startActivity(intentViewOnMap);


                return true;//si dejabamos el false que viene por defecto se disparaba tanto el onLongClick como el OnClick
            }
        });





        Log.i("ON CREATE","3");
        evaluarEstadoDeFechasDeClientes("24-11-2019", "24-11-2019");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constant.ID_CLIENTE = 0; //REESTABLECEMOS EL ID SI SE PRECIONA HACIA ATRAS
        Intent intent = new Intent(this,MenuPrincipalActivity.class);
        startActivity(intent);
    }

    public void crearListViewClientes (String estadoVisita){

        Log.i("CrearList","1");
        clientes = new ArrayList<HashMap>();
        listaClientes = (ListView) findViewById(R.id.listaClientes);
        AdaptadorClientes adaptadorClientes = new AdaptadorClientes(this,clientes);
        listaClientes.setAdapter(adaptadorClientes);

        Cursor res;

        Log.i("CrearList","2");
        if (estadoVisita.equals(Constant.ESTADO_TODOS)){
            res = dbHelper.clientes_dameTodosLosClientes();

        }else{
            res = dbHelper.clientes_dameClientesPorEstadoVisita(estadoVisita);

        }

        Log.i("count resList",String.valueOf(res.getCount()));
        res.moveToFirst();
        Log.i("CrearList","3");

        if (res.getCount() != 0){
            Log.i("CrearList","5");

        do {

            //llenamos nuestro map con los datos que enviaremos al adaptador de clientes
            final HashMap map = new HashMap();
            map.put("id",res.getString(res.getColumnIndex(DataBaseHelper.KEY_ID)));                         //pones el id de la fila
            map.put(Constant.FIRST_COLUMN,res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE)));        //ponemos la cuenta
            map.put(Constant.SECOND_COLUMN,res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE)));       //ponemos el nombre
            map.put(Constant.EIGHT_COLUMN,res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ESTADO_CLIENTE)));        //ponemos el estado del cliente
            map.put(Constant.ELEVEN_COLUMN,res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE)));         //ponemos el adeudo del cliente
            map.put(Constant.THIRTEEN_COLUMN,res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_VENCIMIENTO)));      //ponemos el vencimiento
            map.put(Constant.EIGHTEEN_COLUMN,res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA)));     //ponemos el estado de la visita
            map.put(Constant.TWENTY_NINE_COLUMN, res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE)));
            map.put(Constant.THIRTY_COLUMN, res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE)));
            map.put(Constant.THIRTY_ONE_COLUMN, res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_CLI_PRIORIDAD_DE_VISITA)));
            map.put(Constant.THIRTY_THIRD_COLUMN, res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_CLI_MENSAJE_MOSTRAR_POR_VISITAR)));
            map.put(Constant.THIRTY_SECOND_COLUMN, res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_ZONA)));

            Log.i("CrearList6", res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE)));
            Log.i("CrearList6", res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE)));

            clientes.add(map);

        } while (res.moveToNext());

        }

        Log.i("CrearList","6");
        ponerTituloBar(estadoVisita, res.getCount());
    }


    public void importarDbClientes (){
        boolean continuarImportando = false;
        Cursor res = dbHelper.clientes_dameTodosLosClientes();
        Log.i("count res",String.valueOf(res.getCount()));

        if (res.getCount() == 0){//si la base de datos esta vacia
            folder = new File(Constant.INSTANCE_PATH, "/mx.4103.klp");
            docCliente = new File(folder,"/clientes.txt");
            Log.i("CONTROL","1");

            try{

            FileReader txtClientes = new FileReader(docCliente);
            BufferedReader buffer = new BufferedReader(txtClientes);
                Log.i("CONTROL","2");

            String line;





            if ((line = buffer.readLine())!= null){
                //(ESTO ES PARA IMPORTAR LA PRIMERA LINEA DEL DOCUMENTO DE TEXTO DE LSO CLIENTES
                // EL ID DE ZONA DEBERA VENIR EN LA PRIMERA LINEA DEL DOCUMENTO DE TEXTO DEBERA
                // VENIR el idZona una "#" y la palabra id ejemplo: chiquimequillas43399#id
                // POR ESO VALIDAMOS QUE LO OBTENIDO DESPUES DE PARTIR el primer renglon POR "#" SEA IGUAL A 2 O
                // QUE SOLO SE obtuvieron dos VALORes DELA DIVISION, PARA PODER CONTINUAR AL LLAMADOD EL METODO
                // DONDE AGREGAMOS EL ID DE ZONA A LA BASE DE DATOS)
                //VersionNameLuisda6.5 Cambio desaparese despues del gato la palabra id, ahora se recibiran las coordenadas
                //del perimetro de zona. ejemplo almoloya#-100,205,99.27,0




                String columnas1 [] = line.split("#");
                Log.i("numero de columnas1", String.valueOf(columnas1.length));
                Log.i("valor en columnas1[0]",String.valueOf(columnas1[0]));

                if (columnas1.length == 2){
                    dbHelper.tabla_identificador_zona_insertarIdZona(columnas1[0],columnas1[1]);
                    continuarImportando = true;
                }else{
                    Toast.makeText(this,"Error al obtener identificador de zona revisar el Archivo Clientes con SISTEMAS KALIOPE",Toast.LENGTH_LONG).show();
                    continuarImportando = false;
                    Intent intent = new Intent(this,Trabajar.class);
                    startActivity(intent);
                }

            }



            if (continuarImportando){
                while ((line = buffer.readLine()) != null){
                    String [] columnas = line.split("#");
                    Cursor res1 = dbHelper.clientes_dameTodosLosClientes();
                    Log.i("CONTROL","3");

                    Log.i("colums.lengh",String.valueOf(columnas.length));

                    //el metodo length devuelve la longuitud total de array en este caso es 18
                    //pero los indices van del 0 al 17
                    if (columnas.length != 18){
                        continue;//reiniciamos el bucle while que pasa al siguiente cliente
                    }

                    ContentValues cv = new ContentValues(17);

                    cv.put(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE,columnas [0]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE,columnas [1]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_TELEFONO,columnas[2]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_DIAS_CREDITO,columnas [3]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_GRADO_CLIENTE,columnas [4]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_CREDITO_CLIENTE,columnas [5]);
                    //cv.put(DataBaseHelper.CODIGO_CLIENTE,columnas[6]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_ESTADO_CLIENTE,columnas[6]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_LATITUD_CLIENTE,columnas[7]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_LONGITUD_CLIENTE,columnas[8]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE,columnas [9]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE,columnas [10]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_VENCIMIENTO,columnas [11]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_HISTORIALES,columnas [12]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_PUNTOS_DISPONIBLES,columnas [13]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_REPORTE,columnas [14]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_INDICACIONES,columnas [15]);
                    cv.put(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA,columnas [16]);
                    cv.put(DataBaseHelper.CLIENTES_ADMIN_MERCANCIA_ACARGO,columnas [17]);


                    dbHelper.clientes_insertarClientes(cv);
                    Log.i("CONTROL","4");
                    Log.i("count res1",String.valueOf(res1.getCount()));

                }//fin de while
                docCliente.delete();
                Log.i("CONTROL","5");
            }


            }catch (IOException e) {
                Log.i("CONTROL","CATCH");
                e.printStackTrace();

            }

        }
        Log.i("CONTROL","6");
    }


    public void cargarClientesDesdeJson(JSONArray jsonArray,DataBaseHelper dataBaseHelper){
        dataBaseHelper.clientes_eliminaTablaClientes();

        String fechaActual = utilidadesApp.getFecha();


        //en el array clientes tenemos esto, ver el archivo iniciar sesion.php en el servidor si hay mas dudas
        //[{"zona":"CANALEJAS","clientes":[{"cuenta":"1146","0":"1146","nombre":"ANA LAURA JUAREZ ARELLANO","1":"ANA LAURA JUAREZ ARELLANO","telefono":"55 39 60 97 72","2":"55 39 60 97 72","dias":"28","3":"28","grado":"EMPRESARIA","4":"EMPRESARIA","credito":"4000","5":"4000","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.021389875920317","7":"20.021389875920317","longitud_fija":"-99.66920361254584","8":"-99.66920361254584","adeudo_cargo":"0","9":"0","piezas_cargo":"13","10":"13","importe_cargo":"2687","11":"2687","fecha_vence_cargo":"12-09-2019","12":"12-09-2019","puntos_disponibles":"0","13":"0","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"1-159-0 1-279-0 1-159-0 4-299-239 2-309-247 1-339-271 2-369-295 1-459-376 ","16":"1-159-0 1-279-0 1-159-0 4-299-239 2-309-247 1-339-271 2-369-295 1-459-376 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 3099*Saldo: 0*Reporte: TODO BIEN****01-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****18-07-2019* Pago: 2700*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 3099*Saldo: 0*Reporte: TODO BIEN****01-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****18-07-2019* Pago: 2700*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"1148","0":"1148","nombre":"MANALI FLORES","1":"MANALI FLORES","telefono":"0","2":"0","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"1500","5":"1500","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.96900203","7":"19.96900203","longitud_fija":"-99.55851103","8":"-99.55851103","adeudo_cargo":"241","9":"241","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"15-08-2019","12":"15-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"LIQUIDA EN 15 DIAS","14":"LIQUIDA EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 200*Saldo: 241*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****18-07-2019* Pago: 77*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 250*Saldo: 77*Reporte: LIQUIDA EN 15 DIAS**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 200*Saldo: 241*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****18-07-2019* Pago: 77*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 250*Saldo: 77*Reporte: LIQUIDA EN 15 DIAS**"},{"cuenta":"1149","0":"1149","nombre":"YOLANDA SANTIAGO AGUILAR","1":"YOLANDA SANTIAGO AGUILAR","telefono":"0","2":"0","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"1400","5":"1400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":" 20.044472","7":" 20.044472","longitud_fija":"-99.655413","8":"-99.655413","adeudo_cargo":"0","9":"0","piezas_cargo":"5","10":"5","importe_cargo":"1345","11":"1345","fecha_vence_cargo":"29-08-2019","12":"29-08-2019","puntos_disponibles":"100","13":"100","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"0","15":"0","mercancia_cargo":"1-329-274 1-299-249 1-309-257 1-359-299 1-319-266 ","16":"1-329-274 1-299-249 1-309-257 1-359-299 1-319-266 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES, PERO DICE HIJO QUE NO ESTABA****04-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES DICEN VECINOS QUE NO ABRIO**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES, PERO DICE HIJO QUE NO ESTABA****04-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES DICEN VECINOS QUE NO ABRIO**"},{"cuenta":"1150","0":"1150","nombre":"ANTONIA GARCIA MIRANDA ","1":"ANTONIA GARCIA MIRANDA ","telefono":"0","2":"0","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"1700","5":"1700","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.94023779","7":"19.94023779","longitud_fija":"-99.56489413","8":"-99.56489413","adeudo_cargo":"639","9":"639","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"01-08-2019","12":"01-08-2019","puntos_disponibles":"550","13":"550","reporte_agente":"QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS","14":"QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS****15-08-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS****01-08-2019* Pago: 500*Saldo: 639*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 214*Saldo: 1372*Reporte: LIQUIDA EN 15 DIAS****04-07-2019* Pago: 928*Saldo: 214*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS****15-08-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINERO NO LE ACABARON DE PAGAR QUEDA DE PAGAR EN 15 DIAS****01-08-2019* Pago: 500*Saldo: 639*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 214*Saldo: 1372*Reporte: LIQUIDA EN 15 DIAS****04-07-2019* Pago: 928*Saldo: 214*Reporte: TODO BIEN**"},{"cuenta":"1153","0":"1153","nombre":"LIZBETH JIMENEZ CAMACHO","1":"LIZBETH JIMENEZ CAMACHO","telefono":"55 82 21 24 43","2":"55 82 21 24 43","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"0","5":"0","estado":"LIO","6":"LIO","latitud_fija":" 19.975901","7":" 19.975901","longitud_fija":"-99.610482","8":"-99.610482","adeudo_cargo":"374","9":"374","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"11-04-2019","12":"11-04-2019","puntos_disponibles":"150","13":"150","reporte_agente":"PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA","14":"PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA","reporte_administracion":"INVESTIGAR CON VECINOS","15":"INVESTIGAR CON VECINOS","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA****15-08-2019* Pago: 0*Saldo: 0*Reporte: PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA****01-08-2019* Pago: 0*Saldo: 0*Reporte: SE INVESTIGA CON VECINOS DICEN QUE NO ESTA QUE SALE A TRABAJAR A QUERETARO Y LLEGA DESPUES DE LAS 8****18-07-2019* Pago: 0*Saldo: 0*Reporte: SU ESPOSO JOSE ALBERTO SE COMPROMETE A LIQUIDAR LA CUENTA DE SU ESPOSA EN 15 DIAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO DICEN FAMILIARES QUE SE FUE A QUERETARO**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA****15-08-2019* Pago: 0*Saldo: 0*Reporte: PARECE QUE LA ESTAN NEGANDO SU FAMILIARES BUSCA AL DELEGADO QUE IVA A HABLAR CON ELLA****01-08-2019* Pago: 0*Saldo: 0*Reporte: SE INVESTIGA CON VECINOS DICEN QUE NO ESTA QUE SALE A TRABAJAR A QUERETARO Y LLEGA DESPUES DE LAS 8****18-07-2019* Pago: 0*Saldo: 0*Reporte: SU ESPOSO JOSE ALBERTO SE COMPROMETE A LIQUIDAR LA CUENTA DE SU ESPOSA EN 15 DIAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO DICEN FAMILIARES QUE SE FUE A QUERETARO**"},{"cuenta":"1154","0":"1154","nombre":"ELIZABET GARCIA GONZALES","1":"ELIZABET GARCIA GONZALES","telefono":"0","2":"0","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"1700","5":"1700","estado":"ACTIVO","6":"ACTIVO","latitud_fija":" 20.021470","7":" 20.021470","longitud_fija":"-99.669315","8":"-99.669315","adeudo_cargo":"0","9":"0","piezas_cargo":"6","10":"6","importe_cargo":"1405","11":"1405","fecha_vence_cargo":"29-08-2019","12":"29-08-2019","puntos_disponibles":"50","13":"50","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"0","15":"0","mercancia_cargo":"1-329-270 1-399-327 2-349-286 2-159-118 ","16":"1-329-270 1-399-327 2-349-286 2-159-118 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 412*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO DICE MAMA QUE SALIO QUE NO ESTABA SE DEJA RECADO****04-07-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA EL DINERO PENDIENTE EN 15 DIAS**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 412*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO DICE MAMA QUE SALIO QUE NO ESTABA SE DEJA RECADO****04-07-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA EL DINERO PENDIENTE EN 15 DIAS**"},{"cuenta":"1155","0":"1155","nombre":"ILSE ANDERIK HERNANDEZ MARTINEZ","1":"ILSE ANDERIK HERNANDEZ MARTINEZ","telefono":"56 11 07 48 99","2":"56 11 07 48 99","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1900","5":"1900","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.01093466745951","7":"20.01093466745951","longitud_fija":"-99.58897021733355","8":"-99.58897021733355","adeudo_cargo":"168","9":"168","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"15-08-2019","12":"15-08-2019","puntos_disponibles":"350","13":"350","reporte_agente":"LIQUIDA EN 15 DIAS","14":"LIQUIDA EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 300*Saldo: 168*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 850*Saldo: 177*Reporte: TODO BIEN****18-07-2019* Pago: 524*Saldo: 130*Reporte: TODO BIEN****04-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 300*Saldo: 168*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 850*Saldo: 177*Reporte: TODO BIEN****18-07-2019* Pago: 524*Saldo: 130*Reporte: TODO BIEN****04-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"1156","0":"1156","nombre":"MARIA LUISA HERNANDEZ MARTINEZ","1":"MARIA LUISA HERNANDEZ MARTINEZ","telefono":"0","2":"0","dias":"14","3":"14","grado":"SOCIA","4":"SOCIA","credito":"2400","5":"2400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.97242562551852","7":"19.97242562551852","longitud_fija":"-99.5655873412939","8":"-99.5655873412939","adeudo_cargo":"294","9":"294","piezas_cargo":"8","10":"8","importe_cargo":"2328","11":"2328","fecha_vence_cargo":"29-08-2019","12":"29-08-2019","puntos_disponibles":"200","13":"200","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"3-399-327 3-359-294 1-259-212 1-309-253 ","16":"3-399-327 3-359-294 1-259-212 1-309-253 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 278*Saldo: 294*Reporte: TODO BIEN****01-08-2019* Pago: 1038*Saldo: 294*Reporte: TODO BIEN****18-07-2019* Pago: 294*Saldo: 294*Reporte: TODO BIEN****04-07-2019* Pago: 270*Saldo: 0*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 278*Saldo: 294*Reporte: TODO BIEN****01-08-2019* Pago: 1038*Saldo: 294*Reporte: TODO BIEN****18-07-2019* Pago: 294*Saldo: 294*Reporte: TODO BIEN****04-07-2019* Pago: 270*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"1157","0":"1157","nombre":"ANTONIA HERNANDEZ CAMACHO","1":"ANTONIA HERNANDEZ CAMACHO","telefono":"55 49 69 62 44","2":"55 49 69 62 44","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"2200","5":"2200","estado":"REACTIVAR","6":"REACTIVAR","latitud_fija":"19.954782323839222","7":"19.954782323839222","longitud_fija":"-99.60686100794508","8":"-99.60686100794508","adeudo_cargo":"0","9":"0","piezas_cargo":"2","10":"2","importe_cargo":"0","11":"0","fecha_vence_cargo":"18-07-2019","12":"18-07-2019","puntos_disponibles":"0","13":"0","reporte_agente":"REACTIVAR EN 2 MESES EN AGOSTO","14":"REACTIVAR EN 2 MESES EN AGOSTO","reporte_administracion":"0","15":"0","mercancia_cargo":"1-399-0 1-159-0 ","16":"1-399-0 1-159-0 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****15-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****01-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****18-07-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****04-07-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****15-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****01-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****18-07-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO****04-07-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 2 MESES EN AGOSTO**"},{"cuenta":"1159","0":"1159","nombre":"EZPERANZA HERNANDEZ FLORENTINO","1":"EZPERANZA HERNANDEZ FLORENTINO","telefono":"0","2":"0","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"2700","5":"2700","estado":"ACTIVO","6":"ACTIVO","latitud_fija":" 19.993895","7":" 19.993895","longitud_fija":"-99.614844","8":"-99.614844","adeudo_cargo":"0","9":"0","piezas_cargo":"6","10":"6","importe_cargo":"1332","11":"1332","fecha_vence_cargo":"12-09-2019","12":"12-09-2019","puntos_disponibles":"0","13":"0","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"INVESTIGAR MAS CON VECINOS","15":"INVESTIGAR MAS CON VECINOS","mercancia_cargo":"1-299-0 1-299-245 1-339-278 1-309-253 1-279-229 1-399-327 ","16":"1-299-0 1-299-245 1-339-278 1-309-253 1-279-229 1-399-327 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 1757*Saldo: 0*Reporte: TODO BIEN****01-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES, QUE ESTA HOSPITALIZADA, SE DEJA RECADO CON HIJA****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 3 VECES SE DEJA RECADO CON HIJAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: DICEN FAMILIARES QUE NO HA REGRESADO DE SU EMERGENCIA DICEN FAMILIARES QUE ES SEGURO QUE PAGE EN 15 DIAS**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 1757*Saldo: 0*Reporte: TODO BIEN****01-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 2 VECES, QUE ESTA HOSPITALIZADA, SE DEJA RECADO CON HIJA****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA 3 VECES SE DEJA RECADO CON HIJAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: DICEN FAMILIARES QUE NO HA REGRESADO DE SU EMERGENCIA DICEN FAMILIARES QUE ES SEGURO QUE PAGE EN 15 DIAS**"},{"cuenta":"1408","0":"1408","nombre":"ORDO\u00d1EZ GARDU\u00d1O YOSELIN","1":"ORDO\u00d1EZ GARDU\u00d1O YOSELIN","telefono":"0","2":"0","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1700","5":"1700","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.96846161","7":"19.96846161","longitud_fija":"-99.56235819","8":"-99.56235819","adeudo_cargo":"145","9":"145","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"01-08-2019","12":"01-08-2019","puntos_disponibles":"200","13":"200","reporte_agente":"LIQUIDA EN 15 DIAS","14":"LIQUIDA EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 650*Saldo: 145*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 200*Saldo: 795*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 507*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 174*Saldo: 507*Reporte: LIQUIDA EN 15 DIAS**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 650*Saldo: 145*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 200*Saldo: 795*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 507*Saldo: 0*Reporte: TODO BIEN****04-07-2019* Pago: 174*Saldo: 507*Reporte: LIQUIDA EN 15 DIAS**"},{"cuenta":"3303","0":"3303","nombre":"CLAUDIA ISABEL S\u00c1NCHEZ ","1":"CLAUDIA ISABEL S\u00c1NCHEZ ","telefono":"0","2":"0","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"2000","5":"2000","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.04443258","7":"20.04443258","longitud_fija":"-99.6554648","8":"-99.6554648","adeudo_cargo":"0","9":"0","piezas_cargo":"7","10":"7","importe_cargo":"1398","11":"1398","fecha_vence_cargo":"29-08-2019","12":"29-08-2019","puntos_disponibles":"50","13":"50","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"0","15":"0","mercancia_cargo":"2-329-274 1-359-299 1-159-0 2-199-159 1-279-233 ","16":"2-329-274 1-359-299 1-159-0 2-199-159 1-279-233 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 1182*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO LE MARCO QUE SALIO A TRABAJAR, QUEDA DE ENTREGAR EN 15 DIAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****15-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****01-08-2019* Pago: 1182*Saldo: 0*Reporte: TODO BIEN****18-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO LE MARCO QUE SALIO A TRABAJAR, QUEDA DE ENTREGAR EN 15 DIAS****04-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"3433","0":"3433","nombre":"VIRGINIA VARGAS HERNANDEZ ","1":"VIRGINIA VARGAS HERNANDEZ ","telefono":"0","2":"0","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1700","5":"1700","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.97496327","7":"19.97496327","longitud_fija":"-99.61408679","8":"-99.61408679","adeudo_cargo":"285","9":"285","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"01-08-2019","12":"01-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"LIQUIDA EN 15 DIAS","14":"LIQUIDA EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 500*Saldo: 285*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 500*Saldo: 785*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS****15-08-2019* Pago: 500*Saldo: 285*Reporte: LIQUIDA EN 15 DIAS****01-08-2019* Pago: 500*Saldo: 785*Reporte: LIQUIDA EN 15 DIAS****18-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"3783","0":"3783","nombre":"GUILLERMINA MART\u00cdNEZ NAVARRETE ","1":"GUILLERMINA MART\u00cdNEZ NAVARRETE ","telefono":"0","2":"0","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1400","5":"1400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"19.98794683","7":"19.98794683","longitud_fija":"-99.65328267","8":"-99.65328267","adeudo_cargo":"0","9":"0","piezas_cargo":"6","10":"6","importe_cargo":"1453","11":"1453","fecha_vence_cargo":"29-08-2019","12":"29-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"1-279-233 1-259-216 1-309-257 3-299-249 ","16":"1-279-233 1-259-216 1-309-257 3-299-249 ","total_pagos":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**","17":"**29-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****15-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN**"}],"fechaClientesConsulta":"29-08-2019"},{"zona":"SAN JUAN DEL RIO","clientes":[{"cuenta":"2170","0":"2170","nombre":"MARIA ISABEL PICHARDO CAMACHO","1":"MARIA ISABEL PICHARDO CAMACHO","telefono":"427 103 42 23","2":"427 103 42 23","dias":"14","3":"14","grado":"EMPRESARIA","4":"EMPRESARIA","credito":"2500","5":"2500","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.33718862282818","7":"20.33718862282818","longitud_fija":"-99.94939814772276","8":"-99.94939814772276","adeudo_cargo":"0","9":"0","piezas_cargo":"10","10":"10","importe_cargo":"2343","11":"2343","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"50","13":"50","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"1-259-207 2-299-239 1-329-263 1-339-271 3-399-319 1-279-0 1-209-167 ","16":"1-259-207 2-299-239 1-329-263 1-339-271 3-399-319 1-279-0 1-209-167 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 529*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 1123*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 1050*Saldo: 2*Reporte: TODO BIEN****02-07-2019* Pago: 813*Saldo: 0*Reporte: TODO BIEN**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 529*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 1123*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 1050*Saldo: 2*Reporte: TODO BIEN****02-07-2019* Pago: 813*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"2172","0":"2172","nombre":"EZPERANZA MARCELO SINECIO","1":"EZPERANZA MARCELO SINECIO","telefono":"427 593 34 01","2":"427 593 34 01","dias":"28","3":"28","grado":"EMPRESARIA","4":"EMPRESARIA","credito":"2500","5":"2500","estado":"REACTIVAR","6":"REACTIVAR","latitud_fija":"20.4379060153793","7":"20.4379060153793","longitud_fija":"-99.94742208072816","8":"-99.94742208072816","adeudo_cargo":"0","9":"0","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"13-08-2019","12":"13-08-2019","puntos_disponibles":"300","13":"300","reporte_agente":"REACTIVAR EN 15 DIAS","14":"REACTIVAR EN 15 DIAS","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 15 DIAS****13-08-2019* Pago: 1396*Saldo: 0*Reporte: REACTIVAR EN 15 DIAS****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 822*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: REACTIVAR EN 15 DIAS****13-08-2019* Pago: 1396*Saldo: 0*Reporte: REACTIVAR EN 15 DIAS****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 822*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"2174","0":"2174","nombre":"ANA LAURA MENDOZA SUAREZ","1":"ANA LAURA MENDOZA SUAREZ","telefono":"266 61 72","2":"266 61 72","dias":"14","3":"14","grado":"SOCIA","4":"SOCIA","credito":"2400","5":"2400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.439199939147645","7":"20.439199939147645","longitud_fija":"-99.89563303688809","8":"-99.89563303688809","adeudo_cargo":"0","9":"0","piezas_cargo":"9","10":"9","importe_cargo":"1968","11":"1968","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"350","13":"350","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"2-159-118 2-209-171 2-279-229 1-339-278 2-399-327 ","16":"2-159-118 2-209-171 2-279-229 1-339-278 2-399-327 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 1145*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 800*Saldo: 1145*Reporte: LIQUIDA EN 15 DIAS****16-07-2019* Pago: 850*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: QUE SU SUEGRA ESTA ENFERMA Y LA ESTA ATENDIENDO DICE MAMA QUE NO DEJO DINERO SE DEJA RECADO**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 1145*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 800*Saldo: 1145*Reporte: LIQUIDA EN 15 DIAS****16-07-2019* Pago: 850*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: QUE SU SUEGRA ESTA ENFERMA Y LA ESTA ATENDIENDO DICE MAMA QUE NO DEJO DINERO SE DEJA RECADO**"},{"cuenta":"2175","0":"2175","nombre":"VICTORIA LOPEZ PEREZ","1":"VICTORIA LOPEZ PEREZ","telefono":"0","2":"0","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"2400","5":"2400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.3325333225939","7":"20.3325333225939","longitud_fija":"-99.98226281483967","8":"-99.98226281483967","adeudo_cargo":"0","9":"0","piezas_cargo":"9","10":"9","importe_cargo":"1635","11":"1635","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"0","15":"0","mercancia_cargo":"1-299-0 1-299-0 1-299-245 1-339-278 4-279-229 1-239-196 ","16":"1-299-0 1-299-0 1-299-245 1-339-278 4-279-229 1-239-196 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****30-07-2019* Pago: 1414*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****02-07-2019* Pago: 785*Saldo: 0*Reporte: TODO BIEN**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****30-07-2019* Pago: 1414*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****02-07-2019* Pago: 785*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"2179","0":"2179","nombre":"MARIA GUADALUPE RAMIREZ PEREZ","1":"MARIA GUADALUPE RAMIREZ PEREZ","telefono":"0","2":"0","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"1900","5":"1900","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.318255430494393","7":"20.318255430494393","longitud_fija":"-99.98400428016876","8":"-99.98400428016876","adeudo_cargo":"0","9":"0","piezas_cargo":"4","10":"4","importe_cargo":"1023","11":"1023","fecha_vence_cargo":"10-09-2019","12":"10-09-2019","puntos_disponibles":"350","13":"350","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"2-279-233 1-299-249 1-369-308 ","16":"2-279-233 1-299-249 1-369-308 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 815*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 1080*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 815*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 1080*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"2180","0":"2180","nombre":"GLORIA CRUZ CRUZ","1":"GLORIA CRUZ CRUZ","telefono":"427 273 13 87","2":"427 273 13 87","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"0","5":"0","estado":"LIO","6":"LIO","latitud_fija":"20.381798019241604","7":"20.381798019241604","longitud_fija":"-99.99014639932028","8":"-99.99014639932028","adeudo_cargo":"400","9":"400","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"02-01-2019","12":"02-01-2019","puntos_disponibles":"300","13":"300","reporte_agente":"NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO","14":"NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO****13-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO****30-07-2019* Pago: 100*Saldo: 400*Reporte: ABONA EN 15 DIAS****16-07-2019* Pago: 300*Saldo: 500*Reporte: ABONA EN 15 DIAS****02-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO SOLO SALE MAMA QUE NO LE DIJO NADA Y NO CONTESTA**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO****13-08-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO PASA SU MAMA QUE NO DEJO NADA QUEDA DE DEPOSIRAR EL SABADO****30-07-2019* Pago: 100*Saldo: 400*Reporte: ABONA EN 15 DIAS****16-07-2019* Pago: 300*Saldo: 500*Reporte: ABONA EN 15 DIAS****02-07-2019* Pago: 0*Saldo: 0*Reporte: NO ESTUBO SOLO SALE MAMA QUE NO LE DIJO NADA Y NO CONTESTA**"},{"cuenta":"2184","0":"2184","nombre":"MARIA DEL CARMEN PEREZ CASTA\u00d1EDA","1":"MARIA DEL CARMEN PEREZ CASTA\u00d1EDA","telefono":"0","2":"0","dias":"14","3":"14","grado":"SOCIA","4":"SOCIA","credito":"2400","5":"2400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.32174212","7":"20.32174212","longitud_fija":"-99.97660145","8":"-99.97660145","adeudo_cargo":"0","9":"0","piezas_cargo":"9","10":"9","importe_cargo":"2346","11":"2346","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"150","13":"150","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"0","15":"0","mercancia_cargo":"3-279-229 1-299-245 1-349-286 1-329-270 1-339-278 1-399-327 1-309-253 ","16":"3-279-229 1-299-245 1-349-286 1-329-270 1-339-278 1-399-327 1-309-253 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 818*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 1421*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 606*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 875*Saldo: 0*Reporte: TODO BIEN**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 818*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 1421*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 606*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 875*Saldo: 0*Reporte: TODO BIEN**"},{"cuenta":"2316","0":"2316","nombre":"MARIELA HERNANDEZ TREJO ","1":"MARIELA HERNANDEZ TREJO ","telefono":"0","2":"0","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"1900","5":"1900","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.32951403480396","7":"20.32951403480396","longitud_fija":"-99.95643287957716","8":"-99.95643287957716","adeudo_cargo":"0","9":"0","piezas_cargo":"10","10":"10","importe_cargo":"2346","11":"2346","fecha_vence_cargo":"10-09-2019","12":"10-09-2019","puntos_disponibles":"0","13":"0","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"*SE CAMBIA A SOCIA*","15":"*SE CAMBIA A SOCIA*","mercancia_cargo":"1-329-0 3-279-229 1-309-253 2-329-270 1-339-278 1-319-261 1-399-327 ","16":"1-329-0 3-279-229 1-309-253 2-329-270 1-339-278 1-319-261 1-399-327 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 506*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 1322*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 506*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****16-07-2019* Pago: 1322*Saldo: 0*Reporte: TODO BIEN****02-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"2948","0":"2948","nombre":"ADRIANA RANGEL ","1":"ADRIANA RANGEL ","telefono":"427 116 8549","2":"427 116 8549","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1400","5":"1400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.3880195","7":"20.3880195","longitud_fija":"-99.97540027","8":"-99.97540027","adeudo_cargo":"490","9":"490","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"13-08-2019","12":"13-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"LIQUIDA EN 15 DIAS QUE NO LE PAGARON","14":"LIQUIDA EN 15 DIAS QUE NO LE PAGARON","reporte_administracion":"PENDIENTE EN 15 DIAS PASAR ANTES DE LA 1","15":"PENDIENTE EN 15 DIAS PASAR ANTES DE LA 1","mercancia_cargo":"0","16":"0","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS QUE NO LE PAGARON****13-08-2019* Pago: 0*Saldo: 490*Reporte: LIQUIDA EN 15 DIAS QUE NO LE PAGARON****30-07-2019* Pago: 233*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 0*Saldo: 0*Reporte: SAN JUAN DEL R\u00edO SAN CAYETANO R\u00edO CLARO R\u00edO CLARO 7. CERCA DEL CENTRO M\u00e9DICO COSCAMI.****02-07-2019* Pago: 0*Saldo: 0*Reporte: SAN JUAN DEL R\u00edO SAN CAYETANO R\u00edO CLARO R\u00edO CLARO 7. CERCA DEL CENTRO M\u00e9DICO COSCAMI.**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: LIQUIDA EN 15 DIAS QUE NO LE PAGARON****13-08-2019* Pago: 0*Saldo: 490*Reporte: LIQUIDA EN 15 DIAS QUE NO LE PAGARON****30-07-2019* Pago: 233*Saldo: 0*Reporte: TODO BIEN****16-07-2019* Pago: 0*Saldo: 0*Reporte: SAN JUAN DEL R\u00edO SAN CAYETANO R\u00edO CLARO R\u00edO CLARO 7. CERCA DEL CENTRO M\u00e9DICO COSCAMI.****02-07-2019* Pago: 0*Saldo: 0*Reporte: SAN JUAN DEL R\u00edO SAN CAYETANO R\u00edO CLARO R\u00edO CLARO 7. CERCA DEL CENTRO M\u00e9DICO COSCAMI.**"},{"cuenta":"3417","0":"3417","nombre":"KARLA JUDITH JUAREZ RIVERA","1":"KARLA JUDITH JUAREZ RIVERA","telefono":"4272715432","2":"4272715432","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"1500","5":"1500","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.43945776","7":"20.43945776","longitud_fija":"-99.98591199","8":"-99.98591199","adeudo_cargo":"0","9":"0","piezas_cargo":"6","10":"6","importe_cargo":"1495","11":"1495","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"0","13":"0","reporte_agente":"TODO BIEN","14":"TODO BIEN","reporte_administracion":"PENDIENTE EN 15","15":"PENDIENTE EN 15","mercancia_cargo":"1-349-291 1-279-233 1-309-257 1-259-216 1-299-249 1-299-249 ","16":"1-349-291 1-279-233 1-309-257 1-259-216 1-299-249 1-299-249 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: SOY DE SAN PEDRO AHUACATLAN X LA \u00faNIDAD DEPORTIVA HAY UNA DESVIACI\u00f3N SAN JUAN DEL R\u00edO**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****13-08-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: SOY DE SAN PEDRO AHUACATLAN X LA \u00faNIDAD DEPORTIVA HAY UNA DESVIACI\u00f3N SAN JUAN DEL R\u00edO**"},{"cuenta":"2171","0":"2171","nombre":"MARIA ELENA NIETO SALASAR","1":"MARIA ELENA NIETO SALASAR","telefono":"414 105 50 79","2":"414 105 50 79","dias":"28","3":"28","grado":"SOCIA","4":"SOCIA","credito":"1500","5":"1500","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.47471218","7":"20.47471218","longitud_fija":"-99.93971997","8":"-99.93971997","adeudo_cargo":"0","9":"0","piezas_cargo":"11","10":"11","importe_cargo":"2551","11":"2551","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"200","13":"200","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"","15":"","mercancia_cargo":"1-279-0 6-279-229 1-309-253 1-329-270 2-399-327 ","16":"1-279-0 6-279-229 1-309-253 1-329-270 2-399-327 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****31-07-2019* Pago: 1013*Saldo: 254*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN SE REALIZO CIERRE EN SAN CLEMENTE****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****31-07-2019* Pago: 1013*Saldo: 254*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN SE REALIZO CIERRE EN SAN CLEMENTE****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"2173","0":"2173","nombre":"YAZMIN CRUZ TORRES","1":"YAZMIN CRUZ TORRES","telefono":"427 132 11 00","2":"427 132 11 00","dias":"28","3":"28","grado":"EMPRESARIA","4":"EMPRESARIA","credito":"1500","5":"1500","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.477873","7":"20.477873","longitud_fija":"-99.9341417","8":"-99.9341417","adeudo_cargo":"0","9":"0","piezas_cargo":"13","10":"13","importe_cargo":"2782","11":"2782","fecha_vence_cargo":"27-08-2019","12":"27-08-2019","puntos_disponibles":"100","13":"100","reporte_agente":"CUENTA DE MES","14":"CUENTA DE MES","reporte_administracion":"SE CAMBIA A EMPRESARIA","15":"SE CAMBIA A EMPRESARIA","mercancia_cargo":"1-349-0 1-279-0 1-239-0 1-309-247 2-299-239 3-349-279 1-329-263 3-399-319 ","16":"1-349-0 1-279-0 1-239-0 1-309-247 2-299-239 3-349-279 1-329-263 3-399-319 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****31-07-2019* Pago: 2216*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN SE REALIZO CIERRE EN SAN CLEMENTE****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****13-08-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES****31-07-2019* Pago: 2216*Saldo: 0*Reporte: TODO BIEN****30-07-2019* Pago: 0*Saldo: 0*Reporte: TODO BIEN SE REALIZO CIERRE EN SAN CLEMENTE****16-07-2019* Pago: 0*Saldo: 0*Reporte: CUENTA DE MES**"},{"cuenta":"2177","0":"2177","nombre":"ANA CAREN ALVARES","1":"ANA CAREN ALVARES","telefono":"0","2":"0","dias":"14","3":"14","grado":"VENDEDORA","4":"VENDEDORA","credito":"0","5":"0","estado":"LIO","6":"LIO","latitud_fija":" 20.475300","7":" 20.475300","longitud_fija":"-99.942300","8":"-99.942300","adeudo_cargo":"0","9":"0","piezas_cargo":"4","10":"4","importe_cargo":"1307","11":"1307","fecha_vence_cargo":"26-02-2019","12":"26-02-2019","puntos_disponibles":"0","13":"0","reporte_agente":"DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE","14":"DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE","reporte_administracion":"0","15":"0","mercancia_cargo":"2-399-333 1-399-333 1-369-308 ","16":"2-399-333 1-399-333 1-369-308 ","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE****13-08-2019* Pago: 0*Saldo: 0*Reporte: DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE****31-07-2019* Pago: 0*Saldo: 0*Reporte: SE ENCUENTRA CASA DE SU MAMA SE PLATICA CON MAMA DICE QUE VA A INVESTIGAR BUSCAR EN DIRECCION DE MAMA****30-07-2019* Pago: 0*Saldo: 0*Reporte: OSWALDO UBICA SU DOMICILIO SE TIENE QUE PASAR A VISITAR A SU MAMA ELLA QUEDO DE PAGAR EN 15 DIAS URGE BUSCAR****16-07-2019* Pago: 0*Saldo: 0*Reporte: SE PLATICO CON DELEGADO QUE NO LA HA PODIDO BUSCAR QUE SI LA V**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE****13-08-2019* Pago: 0*Saldo: 0*Reporte: DA CON DOMICILIO DE SU MAMA PERO NO SALE NADIE****31-07-2019* Pago: 0*Saldo: 0*Reporte: SE ENCUENTRA CASA DE SU MAMA SE PLATICA CON MAMA DICE QUE VA A INVESTIGAR BUSCAR EN DIRECCION DE MAMA****30-07-2019* Pago: 0*Saldo: 0*Reporte: OSWALDO UBICA SU DOMICILIO SE TIENE QUE PASAR A VISITAR A SU MAMA ELLA QUEDO DE PAGAR EN 15 DIAS URGE BUSCAR****16-07-2019* Pago: 0*Saldo: 0*Reporte: SE PLATICO CON DELEGADO QUE NO LA HA PODIDO BUSCAR QUE SI LA V**"},{"cuenta":"2178","0":"2178","nombre":"MARIA DANIELA MARTINEZ ALVAREZ","1":"MARIA DANIELA MARTINEZ ALVAREZ","telefono":"0","2":"0","dias":"28","3":"28","grado":"VENDEDORA","4":"VENDEDORA","credito":"1400","5":"1400","estado":"ACTIVO","6":"ACTIVO","latitud_fija":"20.479807339222255","7":"20.479807339222255","longitud_fija":"-99.93879139981577","8":"-99.93879139981577","adeudo_cargo":"573","9":"573","piezas_cargo":"0","10":"0","importe_cargo":"0","11":"0","fecha_vence_cargo":"16-07-2019","12":"16-07-2019","puntos_disponibles":"0","13":"0","reporte_agente":"QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR","14":"QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR","reporte_administracion":"0","15":"0","mercancia_cargo":"0","16":"0","total_pagos":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR****13-08-2019* Pago: 0*Saldo: 0*Reporte: QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR****31-07-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINEROQUE IVA A CONSEGUIR QUE PASARAMOS EN LA TARDE PERO NO CONSIGUIO QUE EN 15 DIAS****30-07-2019* Pago: 0*Saldo: 0*Reporte: PASA OSWALDO QUE NO TENIA DINERO QUEDA DE LIQUIDAR EN 15 DIAS EXIGIR SU PAGO****16-07-2019* Pago: 300*Saldo: 573*Reporte: NO LE LIQUIDARON LAS CLIENTAS**","17":"**27-08-2019* Pago: 0*Saldo: 0*Reporte: QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR****13-08-2019* Pago: 0*Saldo: 0*Reporte: QUE SE ENFERMO SU HIJO Y QUE NO TENIA DINERO QUEDA DE LIQUIDAR****31-07-2019* Pago: 0*Saldo: 0*Reporte: QUE NO TENIA DINEROQUE IVA A CONSEGUIR QUE PASARAMOS EN LA TARDE PERO NO CONSIGUIO QUE EN 15 DIAS****30-07-2019* Pago: 0*Saldo: 0*Reporte: PASA OSWALDO QUE NO TENIA DINERO QUEDA DE LIQUIDAR EN 15 DIAS EXIGIR SU PAGO****16-07-2019* Pago: 300*Saldo: 573*Reporte: NO LE LIQUIDARON LAS CLIENTAS**"}],"fechaClientesConsulta":"27-08-2019"}]
        //(clientes
        //    0
        //            zona        "CANALEJAS"
        //            clientes
        //                    0
        //                            0	"1146"
        //                            1	"ANA LAURA JUAREZ ARELLANO"
        //                            2	"55 39 60 97 72"
        //                            3	"28"
        //                            4	"EMPRESARIA"
        //                            5	"4000"
        //                            6	"ACTIVO"
        //                            7	"20.021389875920317"
        //                            8	"-99.66920361254584"
        //                            9	"0"
        //                            10	"13"
        //                            11	"2687"
        //                            12	"12-09-2019"
        //                            13	"0"
        //                            14	"TODO BIEN"
        //                            15	"0"
        //                            16	"1-159-0 1-279-0 1-159-0 …71 2-369-295 1-459-376 "
        //                            17	"**29-08-2019* Pago: 0*Sa…eporte: CUENTA DE MES**"
        //                            cuenta	"1146"
        //                            nombre	"ANA LAURA JUAREZ ARELLANO"
        //                            telefono	"55 39 60 97 72"
        //                            dias	"28"
        //                            grado	"EMPRESARIA"
        //                            credito	"4000"
        //                            estado	"ACTIVO"
        //                            latitud_fija	"20.021389875920317"
        //                            longitud_fija	"-99.66920361254584"
        //                            adeudo_cargo	"0"
        //                            piezas_cargo	"13"
        //                            importe_cargo	"2687"
        //                            fecha_vence_cargo	"12-09-2019"
        //                            puntos_disponibles	"0"
        //                            reporte_agente	"TODO BIEN"
        //                            reporte_administracion	"0"
        //                            mercancia_cargo	"1-159-0 1-279-0 1-159-0 …71 2-369-295 1-459-376 "
        //                            total_pagos	"**29-08-2019* Pago: 0*Sa…eporte: CUENTA DE MES**"
        //                    1	{…}
        //                    2	{…}
        //                    3	{…}
        //                    4	{…}
        //                    5	{…}
        //                    6	{…}
        //                    7	{…}
        //                    8	{…}
        //                    9	{…}
        //                    10	{…}
        //                    11	{…}
        //                    12	{…}
        //                    13	{…}
        //            fechaClientesConsulta	"29-08-2019"
        //    1
        //            zona        "SAN JUAN DEL RIO"
        //            clientes
        //                    0	{…}
        //                    1	{…}
        //                    2	{…}
        //                    3	{…}
        //                    4	{…}
        //                    5	{…}
        //                    6	{…}
        //                    7	{…}
        //                    8	{…}
        //                    9	{…}
        //                    10	{…}
        //                    11	{…}
        //                    12	{…}
        //                    13	{…}
        //            fechaClientesConsulta	"27-08-2019")

        //recorremos cada posible zona que haiga si hay 2 zonas que enviamos al mismo telefono
        for (int i = 0; i<jsonArray.length(); i++){


            try{
                String nombreZona = jsonArray.getJSONObject(i).getString("zona");
                String fechaConsulta = jsonArray.getJSONObject(i).getString("fechaClientesConsulta");
                JSONArray arrayClientes = jsonArray.getJSONObject(i).getJSONArray("clientes");


                //recorremos cada cliente
                for (int index = 0; i<arrayClientes.length(); index++){


                        ContentValues cv = new ContentValues(25);

                    String fechaVencimiento = arrayClientes.getJSONObject(index).getString("fecha_vence_cargo");
                    int diasCredito = arrayClientes.getJSONObject(index).getInt("dias");
                    String gradoCliente = arrayClientes.getJSONObject(index).getString("grado");
                    String estadoCliente = arrayClientes.getJSONObject(index).getString("estado");
                    int adeudoCliente = arrayClientes.getJSONObject(index).getInt("adeudo_cargo");
                    int acargoCliente = arrayClientes.getJSONObject(index).getInt("importe_cargo"); //piezas a cargo

                        cv.put(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE,arrayClientes.getJSONObject(index).getString("cuenta"));
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE,arrayClientes.getJSONObject(index).getString("nombre"));
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_TELEFONO,arrayClientes.getJSONObject(index).getString("telefono"));
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_DIAS_CREDITO,diasCredito);
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_GRADO_CLIENTE,gradoCliente);
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_CREDITO_CLIENTE,arrayClientes.getJSONObject(index).getString("credito"));
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_ESTADO_CLIENTE,estadoCliente);
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_LATITUD_CLIENTE,arrayClientes.getJSONObject(index).getString("latitud_fija"));
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_LONGITUD_CLIENTE,arrayClientes.getJSONObject(index).getString("longitud_fija"));
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE,adeudoCliente);
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE,acargoCliente);
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_VENCIMIENTO,fechaVencimiento);

                        //el historial del cliente lo tratamos de manera diferente, remplazamos los ** que llegan entre la division
                        //de los diferentes periodos del historial, pero no es muy legible para el usuario, entonces primero vamos a
                        //remplazar los asteriscos por saltos de linea para asi guardarlos en la base de datos, y cuando se proyecten en la
                        //vista del usuario esten acomodados por saltos de linea
                        String historiales = arrayClientes.getJSONObject(index).getString("total_pagos");
                        String historialTratado = historiales.replace('*','\n');
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_HISTORIALES,historialTratado);



                        cv.put(DataBaseHelper.CLIENTES_ADMIN_PUNTOS_DISPONIBLES,arrayClientes.getJSONObject(index).getString("puntos_disponibles"));
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_REPORTE,arrayClientes.getJSONObject(index).getString("reporte_agente"));
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_INDICACIONES,arrayClientes.getJSONObject(index).getString("reporte_administracion"));
                        cv.put(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA,"VISITAR");
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_MERCANCIA_ACARGO,arrayClientes.getJSONObject(index).getString("mercancia_cargo"));

                        cv.put(DataBaseHelper.CLIENTES_ADMIN_PAGO_ANTERIOR1,0);
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_PAGO_ANTERIOR2,0);
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_PAGO_ANTERIOR3,0);
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_PAGO_ANTERIOR4,0);
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_PAGO_ANTERIOR5,0);

                        int codigoEstadoFechas = evaluarEstadoDeFechasDeClientes(fechaActual,fechaVencimiento);
                        int diasDeVencimientoOfaltantesParaCorte = calcularDiasVencimientoOFaltantesParaCorte(fechaActual,fechaVencimiento);
                        //int prioridadDeVisita = calcularPrioridadDeVisita(codigoEstadoFechas,diasDeVencimientoOfaltantesParaCorte,estadoCliente,estadoCliente,diasCredito,adeudoCliente,acargoCliente);

                    Log.d("estadoDeFechasPriori", "Nombre: " + arrayClientes.getJSONObject(index).getString("nombre"));
                    Log.d("estadoDeFechasPriori", "Fecha Actual: " + fechaActual);
                    Log.d("estadoDeFechasPriori", "Vencimiento: " + fechaVencimiento);
                    Log.d("estadoDeFechasPriori","Codigo estado fechas: " + codigoEstadoFechas);


                    Log.d("estadoDeFechasPriori",
                            (codigoEstadoFechas == DIA_EXACTO_DE_CIERRE)?"DIA_EXACTO_DE_CIERRE":
                                (codigoEstadoFechas == SE_PODRIA_HACER_CIERRE)?"SE_PODRIA_HACER_CIERRE":
                                        (codigoEstadoFechas == AUN_NO_TOCA_SU_CIERRE)?"AUN_NO_TOCA_SU_CIERRE":
                                                (codigoEstadoFechas == CUENTA_VENCIDA_MENOR_DE_3_DIAS)?"CUENTA_VENCIDA_MENOR_DE_3_DIAS":
                                                        (codigoEstadoFechas == CUENTA_VENCIDA_DE_4_A_13_DIAS)?"CUENTA_VENCIDA_DE_4_A_13_DIAS":
                                                                (codigoEstadoFechas == CUENTA_VENCIDA_EXACTO_14_DIAS)?"CUENTA_VENCIDA_EXACTO_14_DIAS":
                                                                        (codigoEstadoFechas == CUENTA_VENCIDA_MAS_DE_15_DIAS)?"CUENTA_VENCIDA_MAS_DE_15_DIAS":""
                            );

                    Log.d("estadoDeFechasPriori", "DiasVencimientoFaltanteParaCorte: " + diasDeVencimientoOfaltantesParaCorte);
                    Log.d("estadoDeFechasPriori", "GradoCliente: " + gradoCliente);
                    Log.d("estadoDeFechasPriori", "EstadoCliente: " + estadoCliente);
                    Log.d("estadoDeFechasPriori", "DiasCredito: " + diasCredito);
                    Log.d("estadoDeFechasPriori", "AdeudoCLiente: " + adeudoCliente);
                    Log.d("estadoDeFechasPriori", "AcargoCLiente: " + acargoCliente);
                    //Log.d("estadoDeFechasPriori", "PrioridadDe Visita: " + prioridadDeVisita);
                    //Log.d("estadoDeFechasPriori",  (prioridadDeVisita == URGENTE)?"URGENTE":
                    //        (prioridadDeVisita == ALTO)?"ALTO":
                    //                (prioridadDeVisita == NORMAL)?"NORMAL":
                     //                       (prioridadDeVisita == BAJO)?"BAJO":
                     //                               (prioridadDeVisita == ATRASO_URGENTE)?"ATRASO_URGENTE":
                     //                                       (prioridadDeVisita == ATRASO)?"ATRASO":
                     //                                               (prioridadDeVisita == LIO_URGENTE)?"LIO_URGENTE":"");

                    Log.d("estadoDeFechasPriori", "_______________________________________________________________________");




                    cv.put(DataBaseHelper.CLIENTES_CLI_CODIGO_ESTADO_FECHAS, codigoEstadoFechas);
                        cv.put(DataBaseHelper.CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE, diasDeVencimientoOfaltantesParaCorte);
                    //    cv.put(DataBaseHelper.CLIENTES_CLI_PRIORIDAD_DE_VISITA, prioridadDeVisita);



                        cv.put(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_ZONA,nombreZona);
                        cv.put(DataBaseHelper.CLIENTES_ADMIN_FECHA_DE_CONSULTA, fechaConsulta);


                        dataBaseHelper.clientes_insertarClientes(cv);


                }
            }catch (JSONException e){
                e.printStackTrace();
            }



        }

        calcularMensajeDeVisita(dataBaseHelper);
    }



    private int evaluarEstadoDeFechasDeClientes (String fechaAhora,String fechaVencimiento){
        //(En este metodo crearemos la calificacion del cliente en base a las variables, le colocaremos el valor de un numero
        // entero cada numero significara diferente mensaje a mostrar en el lado de agente, esto nos permitira en diferentes areas de la ppa
        // mostrar diferentes mensajes y no solo uno, por ejemplo al colocar aqui un numero 2 podemos en una area indicar que es una cliente de mes es necesario hacer cierre
        // pero en otra area por ejemplo, esta clienta no le podemos dar quince dias mas porque es de mes, decesario hacer cierre ahora!!
        // entonces en este metodo realizaremos esas evaluaciones y guardando en la base de datos del cliente que calificacion le toco
        // para eventualmente poderla rescatar y mostrar los mensajes que deeseemos dependiendo del lugar de la app)

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");





        try {
            Date fechaActual = simpleDateFormat.parse(fechaAhora);
            Date fechaVence = simpleDateFormat.parse(fechaVencimiento);

            //(ahora como abra ocaciones donde cargemos clientes de fechas futuras O FECHAS PASADAS si algun promotor no paso a verla y la mandamos al dia siguiente presentara un atrazo de un dia
            // pero no debemos tomarlo como grave porque fue nuestra culpa por ejemplo si cargamos
            // 2 zonas diferentes en cada dia, tendremos que saber que ocurrira
            //ejemplo si hoy es 24-11 pero cargamos 2 rutas una qeu es normal el 24-11 pero otra que es de otro dia adelante es decir 27-11 vencimiento
            // si la fecha de vencimiento del cliente es igual o mayor por 3 dias a la fecha actual, lo tomaremos como si estuviera atiempo de hacer cierre
            // si la fecha de vencimineto es menor a la fecha actual lo tomaremos como que la clienta esta vencida
            // en el caso de las de mes si la fecha de vencimiento es mayor a 3 dias entonces no la subrayaremos
            //
            // )

            Date fechaConTolerancia = utilidadesApp.sumarRestarFecha(fechaActual, Calendar.DAY_OF_YEAR,3);


            Log.i("fechaVencimientoClie","Dia fecha Actual: " + fechaActual);
            Log.i("fechaVencimientoClie","Fecha de vencimiento del cliente: " + fechaVence);
            Log.i("fechaVencimientoClie","Dia fecha + 3 dias: " + fechaConTolerancia);



            //ES LA FECHA ACTUAL DEL CIERRE si la fecha de vencimiento esta exacto en el dia actual
            if (fechaVence.equals(fechaActual)){
                Log.i("fechaVencimientoClie","La cuenta esta en su dia exacto de visita");
                return DIA_EXACTO_DE_CIERRE;
            }

            //SE PODRIA HACER CIERRE si la fecha de vencimiento esta entre la fecha  actual y la fecha mas 3 dias
            if (fechaVence.after(fechaActual) && fechaVence.before(fechaConTolerancia) || fechaVence.equals(fechaConTolerancia)){
                Log.i("fechaVencimientoClie","La cuenta esta entre el rango de fecha cierre correcta");
                return SE_PODRIA_HACER_CIERRE;
            }

            //AUN NO LE TOCA CIERRE a la clienta la fecha de vencimiento es despues a la fecha actual mas 3 dias entonces
            if (fechaVence.after(fechaConTolerancia)){
                long resta = fechaActual.getTime() - fechaVence.getTime();
                long calculo = resta / 86400000;
                int diasParaSuCierre = (int) calculo;
                Log.i("fechaVencimientoClie","A la clienta aun no le toca cierre quedan: " + diasParaSuCierre + " dias para su cierre");
                return AUN_NO_TOCA_SU_CIERRE;

            }

            // CUENTA VENCIDA si la fecha de vencimiento es menor a la fecha actual entonces la cuenta ya esta vencida
            if (fechaVence.before(fechaActual)){
                long resta = fechaActual.getTime() - fechaVence.getTime();
                long calculo = resta / 86400000;
                //calculamos cuantos dias lleva vencida la cuenta 1 dia equivale a 86,400,000 Milisegundos
                int diasDeVencimiento = (int)calculo;


                Log.i("fechaVencimientoClie","La cuenta lleva " + diasDeVencimiento + " dias vencida");








                if (diasDeVencimiento <= 3){
                    Log.i("fechaVencimientoClie","La cuenta solo lleva " + diasDeVencimiento + " dia(s) de vencimiento no es grave puede que el promotor no haya pasado ayer y la mandamos otra vez");
                    return CUENTA_VENCIDA_MENOR_DE_3_DIAS;
                }

                if (diasDeVencimiento > 3 && diasDeVencimiento <= 13){
                    Log.i("fechaVencimientoClie","La cuenta presenta " + diasDeVencimiento + " dia(s) de vencimiento Es un atrazo conciderable");
                        return CUENTA_VENCIDA_DE_4_A_13_DIAS;
                }

                if (diasDeVencimiento == 14){
                    Log.i("fechaVencimientoClie","La cuenta presenta " + diasDeVencimiento + " dia(s) de vencimiento Hoy se vuelve a visitar es necesaria la accion inmediata del agente");

                    return CUENTA_VENCIDA_EXACTO_14_DIAS;

                }

                if (diasDeVencimiento >= 15){
                    Log.i("fechaVencimientoClie","La cuenta presenta " + diasDeVencimiento + " dia(s) de vencimiento se requiere maxima presion por parte del agente!!!");
                    return  CUENTA_VENCIDA_MAS_DE_15_DIAS;
                }

            }


        } catch (ParseException e) {
            e.printStackTrace();
        }



        return 0;


    }

    private int calcularDiasVencimientoOFaltantesParaCorte (String fechaAhora, String fechaVencimiento){
        //este metodo calcula cuantos dias lleva vencida la cuenta o cuantos dias aun faltan para su cuerre
        //si la fecha de vencimiento es 24-11-2019 y la fecha actual es 23-11-2019 el resultado es 1 positivo es decir
        //falta un dia para su corte,
        //si vence 24-11-2019 pero la actual es 25-11-2019 da -1 lleva 1 dia de vencimiento

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");


        try {
            Date fechaActual = simpleDateFormat.parse(fechaAhora);
            Date fechaVence = simpleDateFormat.parse(fechaVencimiento);

            //calculamos cuantos dias lleva vencida la cuenta 1 dia equivale a 86,400,000 Milisegundos
            long resta = fechaVence.getTime() - fechaActual.getTime();
            long calculo = resta / 86400000;
            return (int) calculo;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;


    }




    private void calcularMensajeDeVisita (DataBaseHelper dataBaseHelper){





        Cursor clientes = dataBaseHelper.clientes_dameTodosLosClientes();
        if (clientes.getCount()>0){
            clientes.moveToFirst();


            do{
                int codigoCalculadoDeFechas = clientes.getInt(clientes.getColumnIndex(DataBaseHelper.CLIENTES_CLI_CODIGO_ESTADO_FECHAS));
                int diasVencimientoOFaltanteParaCierre = clientes.getInt(clientes.getColumnIndex(DataBaseHelper.CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE));
                String gradoCliente = clientes.getString(clientes.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_GRADO_CLIENTE));
                String estadoCliente = clientes.getString(clientes.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ESTADO_CLIENTE));
                int dias = clientes.getInt(clientes.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_DIAS_CREDITO));
                int adeudo = clientes.getInt(clientes.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE));
                int acargo = clientes.getInt(clientes.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE));

                String fechaVence = clientes.getString(clientes.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_VENCIMIENTO));

                int id = clientes.getInt(clientes.getColumnIndex(DataBaseHelper.KEY_ID));


                int prioridadDeVisita = 0;
                String mensajeAmostrar = "";



                //calcularemos los mensajes y su prioridad de visita dependiendo de el estado del cliente
                //pongo tres saltos de linea para que este mensaje sea el unico que se vea en la lista de clientes,
                // porque el view tendra una medida definida para que solo entre este texto, el resto del mensaje se vera cuando entres a detalles
                //esto para que en la lista de cleitnes solo se muestre el mensaje superior por asi decirlo el mas importante y que la lista de cleintes
                //no se haga de un tamaño muy grande, despues cuando entres al detalle ahi si se vera porque el view tendra el parametro hight como wrapContent
                if (estadoCliente.equals(Constant.LIO)){

                    if (adeudo > 0 && adeudo <= 500 || acargo > 0 && acargo <= 500 ){
                        //Si tiene un adeudo o un cargo de mercancia menor a 500
                        prioridadDeVisita = LIO_NORMAL;
                        mensajeAmostrar = "Este lio ya tiene una cantidad baja de adeudo, tiene " + diasVencimientoOFaltanteParaCierre + " dias de atraso pasa a visitarla \n\n\n" +

                                "-Mete presion para sacarlo de nuestra cartera y no perder mas tiempo visitandolo\n\n" +

                                "-Pregunta con los vecinos si el lio se encuentra en su domicilio y aprovecha para decir que debe dinero (Quemarla con vecinos)\n\n" +
                                "-Deja el vehiculo alejado del domicilio para que el lio no lo vea y se esconda\n\n" +
                                "-Realiza una labor de cobranza EXIGENTE con el lio";

                    }else if (adeudo > 500 && adeudo <= 1000 || acargo > 500 && acargo <= 1000){
                        //si tiene un adeudo o mercancia acargo entre 500 y 1000
                        prioridadDeVisita = LIO_URGENTE;
                        mensajeAmostrar = "Es muy importante que visites a este lio presenta una cantidad alta de adeudo, y tiene " + diasVencimientoOFaltanteParaCierre + " dias de atraso\n\n\n" +
                                "-Este lio puede presentar una negativa de pago mas intensa\n\n" +

                                "-Pregunta con los vecinos si el lio se encuentra en su domicilio y aprovecha para decir que debe dinero (Quemarla con vecinos)\n\n" +
                                "-Deja el vehiculo alejado del domicilio del lio\n\n" +
                                "-Ejecuta una labor de cobranza muy EXIGENTE con el lio \n\n" +
                                "-Es recomendable que busques al DELEGADO de la comunidad";

                    }else if (adeudo > 1000 || acargo > 1000 ){
                        prioridadDeVisita = LIO_URGENTE;
                        mensajeAmostrar = "Tienes que visitar a este lio tiene un adeudo demaciado grande, tiene " + diasVencimientoOFaltanteParaCierre + " dias de atraso\n\n\n" +

                                "Tienes que visitarlo por fuerza entre mas tiempo pase mas dificil sera cobrarlo, presenta una cantidad muy grande de efectivo\n\n"+

                                "-Busca al DELEGADO de la comunidad esto es un requisito para este tipo de lios\n\n" +
                                "-Deja el vehiculo alejado del domicilio del Lio para que no se esconda\n\n" +
                                "-Ejecuta una labor de cobranza muy EXIGENTE \n\n" ;
                    }else if (adeudo == 0 && acargo == 0){
                        //(Este lo ponemos porque por ejemplo un lio que ya no deba nada esta en 0 tanto acargo como adeudo
                        // pero a las secretarias se les olvida mover ese cliente a "Baja" entonces el sistema lo sigue reciviendo
                        // y mostrandolo al agente, al igual con una clienta activa que ya no tenga mercancia ni adeudo y no la ayan
                        // movido al area de "Reactivar" o baja la pondremos como prioridad baja para que no confunda a los agentes)
                        prioridadDeVisita = BAJO;
                        mensajeAmostrar = "No visites a este lio, ya no tiene ningun adeudo pendiente\n\n\n" +
                                "El sistema lo envio por error no tardara mucho en darlo de baja";

                    }

                }

                if (estadoCliente.equals(Constant.PROSPECTO)){
                    prioridadDeVisita = NORMAL;
                    mensajeAmostrar = "Es importante visitar a este cliente porque se ha agendado una cita con ella si la dejamos pendiente demostramos que no somos una empresa seria \n\n\n" +

                            "-Debes investigar muy bien antes a estos clientes, no solo llegar a su domicilio y entregarle la mercancia, porque tienen altas probabilidades de hacerce lios\n\n" +


                            "-Antes de llegar a su domicilio preguntar con vecinos del prospecto de preferencia que no sean familiares de ella porque claro hablaran cosas buenas\n\n" +
                            "-Los vecinos muchas veces te indican si la señora es pagadora o le debe a todo mundo\n\n" +
                            "-Si depronto sale algun familiar del prospecto queriendo tambien mercancia evitalo a toda consta porque no tenemos historial crediticio de ninguna\n\n" +
                            "-Investigar adecuadamente a tu prospecto evitara una gran cantidad de lios y problemas futuros";
                }



                if (estadoCliente.equals(Constant.REACTIVAR)){

                    if (gradoCliente.equals(Constant.EMPRESARIA)){
                        prioridadDeVisita = ALTO;
                        mensajeAmostrar = "Este cliente es EMPRESARIA lo mejor es que pases a visitarla y reactivarla \n\n\n" +
                                "-Era una buena clienta cuando estaba activa entonces has lo imposible para que se reactive y vuelva a trabajar con la empresa\n\n" +
                                "-Te beneficiara porque sus ventas seran mas comisiones para ti";
                    }else{
                        //si son socias o vendedoras por reactivar
                        prioridadDeVisita = NORMAL;
                        mensajeAmostrar = "Lo mejor es tratar de reactivar a esta clienta ella es " + gradoCliente + "\n\n\n" +
                                "-Muchas veces aceptaran volver a trabajar con la empresa\n\n" +
                                "-Has lo posible por reactivarla, si se complica entonces invierte ese tiempo en encontrar clientas nuevas";
                    }

                }

                if (estadoCliente.equals(Constant.ACTIVO)) {



                    switch (codigoCalculadoDeFechas) {

                        case DIA_EXACTO_DE_CIERRE:
                            //(si es justo la fecha para hacer su cierre, lo mejor seria darle prioridad
                            // A las de MES:
                            //              si es empresaria URGENTE porque seguro que vende muy bien
                            //              si es socia     ALTA    seguro vende bien pero no tanto como empresaria
                            //              si es vendedora ALTA  porque podria no vender demaciado o podria si hacerlo pero lo malo es que es de mes
                            // A las de quincena
                            //              si es empresaria ALTA
                            //              socia            normal
                            //              vendedora        normal


                            if (dias == Constant.MES){

                                if (gradoCliente.equals(Constant.EMPRESARIA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "Hoy toca su cierre es de MES y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Si no la encuentras debes de hacer todos los Repasos posibles\n\n" +
                                            "-Muy seguramente realizara una buena venta entonces debemos pasar a visitarla\n\n" +
                                            "-Brindale un MUY BUEN SERVICIO y atencion ya que es EMPRESARIA\n\n" +
                                            "-Si la clienta te solicita 15 dias mas debes platicar con ella porque ya tiene mucho tiempo la mercancia\n\n" +
                                            "-Si no alcanzo a cobrar debes platicar en ella porque ya tuvo 1 mes completo para hacerlo y podria afectar su credito";
                                }

                                if (gradoCliente.equals(Constant.SOCIA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "Hoy toca su cierre es de MES y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Si no la encuentras debes de hacer todos los Repasos posibles\n\n" +
                                            "-Debes pasar a visitarla y procurar no dejarla pendiente\n\n" +
                                            "-Motivala para que suba de grado a EMPRESARIA\n\n" +
                                            "-Si la clienta te solicita 15 dias mas, debes platicar con ella porque con un mes ya es tiempo suficiente\n\n" +
                                            "-Si no alcanzo a pagarte debes platicar en ella porque ya tuvo 1 mes completo para hacerlo y podria afectar su credito";
                                }

                                if (gradoCliente.equals(Constant.VENDEDORA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "Hoy toca su cierre es de MES y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Si no la encuentras debes de hacer todos los Repasos posibles\n\n" +
                                            "-No la dejes pendiente es Vendedora y es de mes ya es mucho tiempo realiza su cierre\n\n" +
                                            "-Por su grado de Vendedora debemos encontrar a la clienta en su domicilio y que no nos haga dar muchos repasos\n\n" +
                                            "-Tiene que hacernos cierre y debe de tener su pago listo, o por lo menos la devolucion de producto, ya que tiene 1 mes completo la mercancia\n\n" +
                                            "-Si pide 15 dias mas tienes que hablar con ella,debe generar ventas a mes si no tendremos que bajarla a QUINCENA";
                                }
                            } else
                                {
                                //si la clienta es de quincena

                                if (gradoCliente.equals(Constant.EMPRESARIA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "Hoy toca su cierre es de QUINCENA y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Procura hacer todos los repasos posibles pero platica con la clienta para que te espere en su domicilio\n\n" +
                                            "-Es Empresaria, brindale una MUY BUEN SERVICIO si esta feliz aumentara sus ventas\n\n" +
                                            "Por ser empresaria le damos mayores ganancias entonces:\n\n" +
                                            "-La clienta debe estar lista para su cierre\n\n" +
                                            "-Debes platicar con ella para encontrarla en su domicilio cada dia de visita\n\n" +
                                            "-Deberia tener listo el pago por su venta generada\n\n" +
                                            "- Si es el caso y te pide quince dias mas platica con ella y recomiendale ser constante en sus ventas para que no se afecte su credito";
                                }

                                if (gradoCliente.equals(Constant.SOCIA)){
                                    prioridadDeVisita = NORMAL;
                                    mensajeAmostrar = "Hoy toca su cierre es de QUINCENA y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "Debes pasar a visitarla podria generarte una buena venta \n\n" +
                                            "-Si la clienta no esta en su domicilio trata de localizarla \n\n" +
                                            "-Si pide quince dias mas o no tiene el pago debemos platicar con ella y motivarla a que sea puntual en sus ventas\n\n" +
                                            "-Motiva a tu clienta para que incremente sus ventas y aumentarla de grado a Empresaria";
                                }

                                if (gradoCliente.equals(Constant.VENDEDORA)){
                                    prioridadDeVisita = NORMAL;
                                    mensajeAmostrar = "Hoy toca su cierre es de QUINCENA y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Motivarla para que eleve sus ventas y subirla de grado\n\n" +
                                            "-Por su grado de Vendedora debemos encontrar a la clienta en su domicilio\n\n" +
                                            "-No tiene que hacernos dar muchos repasos \n\n" +
                                            "-Debe tener listo su cierre y su pago\n\n" +
                                            "-Si la clienta te hace dar muchos repasos platica con ella\n\n" +
                                            "-Si no tuvo el pago completo o te pide quince dias tienes que ser mas exigente para que eleve sus ventas";
                                }
                            }


                            break;


                        case SE_PODRIA_HACER_CIERRE:
                            //(Si la clienta esta entre los 3 dias futuros de tolerancia es porque puede ser que hoy es 24-11 pero
                            // adelantamos una ruta del dia siguiente por algun dia festivo entonces esa ruta es del 25, por lo tanto lo tomaremos
                            // como si estuvieramos dentro de las fechas para hacer las visitas como el case de arriba exactamente igual solo modificamos el primer renglo par aindicar que adelatamos a la clienta)




                            if (dias == Constant.MES){

                                if (gradoCliente.equals(Constant.EMPRESARIA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "Adelantamos " + diasVencimientoOFaltanteParaCierre +" dias la visita de esta clienta hoy toca su cierre es de MES y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Si no la encuentras debes de hacer todos los Repasos posibles\n\n" +
                                            "-Muy seguramente realizara una buena venta entonces debemos pasar a visitarla\n\n" +
                                            "-Brindale un MUY BUEN SERVICIO y atencion ya que es EMPRESARIA\n\n" +
                                            "-Si la clienta te solicita 15 dias mas debes platicar con ella porque ya tiene mucho tiempo la mercancia\n\n" +
                                            "-Si no alcanzo a cobrar debes platicar en ella porque ya tuvo 1 mes completo para hacerlo y podria afectar su credito";
                                }

                                if (gradoCliente.equals(Constant.SOCIA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "Adelantamos " + diasVencimientoOFaltanteParaCierre +" dias la visita de esta clienta hoy toca su cierre es de MES y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Si no la encuentras debes de hacer todos los Repasos posibles\n\n" +
                                            "-Debes pasar a visitarla y procurar no dejarla pendiente\n\n" +
                                            "-Motivala para que suba de grado a EMPRESARIA\n\n" +
                                            "-Si la clienta te solicita 15 dias mas, debes platicar con ella porque con un mes ya es tiempo suficiente\n\n" +
                                            "-Si no alcanzo a pagarte debes platicar en ella porque ya tuvo 1 mes completo para hacerlo y podria afectar su credito";
                                }

                                if (gradoCliente.equals(Constant.VENDEDORA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "Adelantamos " + diasVencimientoOFaltanteParaCierre +" dias la visita de esta clienta hoy toca su cierre es de MES y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Si no la encuentras debes de hacer todos los Repasos posibles\n\n" +
                                            "-No la dejes pendiente es Vendedora y es de mes ya es mucho tiempo realiza su cierre\n\n" +
                                            "-Por su grado de Vendedora debemos encontrar a la clienta en su domicilio y que no nos haga dar muchos repasos\n\n" +
                                            "-Tiene que hacernos cierre y debe de tener su pago listo, o por lo menos la devolucion de producto, ya que tiene 1 mes completo la mercancia\n\n" +
                                            "-Si pide 15 dias mas tienes que hablar con ella,debe generar ventas a mes si no tendremos que bajarla a QUINCENA";
                                }
                            } else
                            {
                                //si la clienta es de quincena

                                if (gradoCliente.equals(Constant.EMPRESARIA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "Adelantamos " + diasVencimientoOFaltanteParaCierre +" dias la visita de esta clienta hoy toca su cierre es de QUINCENA y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Procura hacer todos los repasos posibles pero platica con la clienta para que te espere en su domicilio\n\n" +
                                            "-Es Empresaria, brindale una MUY BUEN SERVICIO si esta feliz aumentara sus ventas\n\n" +
                                            "Por ser empresaria le damos mayores ganancias entonces:\n\n" +
                                            "-La clienta debe estar lista para su cierre\n\n" +
                                            "-Debes platicar con ella para encontrarla en su domicilio cada dia de visita\n\n" +
                                            "-Deberia tener listo el pago por su venta generada\n\n" +
                                            "- Si es el caso y te pide quince dias mas platica con ella y recomiendale ser constante en sus ventas para que no se afecte su credito";
                                }

                                if (gradoCliente.equals(Constant.SOCIA)){
                                    prioridadDeVisita = NORMAL;
                                    mensajeAmostrar = "Adelantamos " + diasVencimientoOFaltanteParaCierre +" dias la visita de esta clienta hoy toca su cierre es de QUINCENA y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "Debes pasar a visitarla podria generarte una buena venta \n\n" +
                                            "-Si la clienta no esta en su domicilio trata de localizarla \n\n" +
                                            "-Si pide quince dias mas o no tiene el pago debemos platicar con ella y motivarla a que sea puntual en sus ventas\n\n" +
                                            "-Motiva a tu clienta para que incremente sus ventas y aumentarla de grado a Empresaria";
                                }

                                if (gradoCliente.equals(Constant.VENDEDORA)){
                                    prioridadDeVisita = NORMAL;
                                    mensajeAmostrar = "Adelantamos " + diasVencimientoOFaltanteParaCierre +" dias la visita de esta clienta hoy toca su cierre es de QUINCENA y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Motivarla para que eleve sus ventas y subirla de grado\n\n" +
                                            "-Por su grado de Vendedora debemos encontrar a la clienta en su domicilio\n\n" +
                                            "-No tiene que hacernos dar muchos repasos \n\n" +
                                            "-Debe tener listo su cierre y su pago\n\n" +
                                            "-Si la clienta te hace dar muchos repasos platica con ella\n\n" +
                                            "-Si no tuvo el pago completo o te pide quince dias tienes que ser mas exigente para que eleve sus ventas";
                                }
                            }







                            break;





                        case AUN_NO_TOCA_SU_CIERRE:

                            //aqui solo nos importaran las lcientas de mes, porque son las que podriamos visitar
                            //por ejemplo si la clienta vence el 24-11 y hoy es 10-11 el sistema entrara aqui porque son mas de 3 dias para que toque su fecha de corte,
                            //si es una cuenta de mes que tiene un adeudo pendiente aunque aun no le toque cierre
                            //seria bueno pasar a cobrarlos hoy y no esperar hasta otros 15 dias
                            //(en cambio si la clienta es de quince dias esto no funcionara porque obviamente hoy le toca cierre
                            // no se pueden desfasar 15 dias como las de mes cada que vallamos a la ruta las vamos a vicitar
                            //
                            // MES si la quienta hace 15 dias quedo a deber algo pero se le pudo dejar mercancia
                            //          evaluaremos si la clienta tiene un saldo pendiente pero de una cantidad que valga la pena pasar por el
                            //          porque a veces las clientas quedan a deber sus diferencias de regalos 50 y no vale la pena pasar por ello
                            //          mejor esperar hasta el mes, pero si por ejemplo quedo a deber 500 y tiene mercancia acargo pues podriamos pasar por ellos
                            // Si la clienta es de mes y hace 15 dias no pago su cierre, por lo tanto no se le dejo mercnacia pero tiene saldo pendiente marcarla como alta!
                            // no importa si es vendedora empresaria o socia porque son de mes
                            // )




                            if (dias == Constant.MES){

                                if (adeudo >= 999 && acargo < 200){
                                    //si tiene un adeudo pendiente de mas de 999 y no tiene mercancia acargo
                                    prioridadDeVisita = ATRASO_URGENTE;
                                    mensajeAmostrar = "Tienes que pasar a visitarla es de MES y pero presenta un atraso grande de $" + adeudo +" con"+ diasVencimientoOFaltanteParaCierre + " dias de atraso\n\n\n " +
                                            "-Has lo posible por cobrar ese atraso ya que si la dejamos mas tiempo podriamos perderla como cliente\n\n" +
                                            "-Procura ser amable y brindarle una buena atencion ya que no estas tratando con un LIO\n\n" +
                                            "-Si termina de pagarte puede continuar trabajando con la empresa, solo motivala para cuidar que no vuelva a ocurrir";
                                }else if (adeudo >= 300 && adeudo<999 && acargo> 500){
                                    //si tiene un adeudo mayor a 300 y tiene mercancia acargo
                                    prioridadDeVisita = ATRASO;
                                    mensajeAmostrar = "Hoy no le toca cierre pero presenta un adeudo pendiente de $" + adeudo + " pasa a visitarla por su saldo pendiente\n\n\n" +
                                            "-Tiene mercancia acargo preguntale que tal le esta llendo\n\n" +
                                            "-Es solo un pequeño atraso entonces manten una atencion amable ya que esta clienta continua trabajando\n\n" +
                                            "-Si llegas a cobrarle de manera inadecuada la clienta se podria molestar y dejar de vender\n\n" +
                                            "-Es muy importante mantener su historial limpio y su cuenta al corriente\n\n" +
                                            "-Si no cobramos los adeudos podrian acomularce y llegara un punto donde podriamos perder al cliente por un mal entendido\n\n";

                                }else if (adeudo >=300 && adeudo<=999 && acargo <200){
                                    //si no tiene mercancia acargo pero un saldo pendiente mayor a 300
                                    prioridadDeVisita = ATRASO;
                                    mensajeAmostrar = "Hoy no le toca cierre pero presenta un adeudo pendiente de $" + adeudo + " pasa a visitarla por su saldo pendiente\n\n\n" +
                                            "-Es solo un pequeño atraso entonces manten una atencion amable ya que esta clienta podra continuar trabajando\n\n" +
                                            "-Si llegas a cobrarle de manera inadecuada la clienta se podria molestar y dejar de vender con la empresa\n\n" +
                                            "-Es muy importante mantener su historial limpio y su cuenta al corriente\n\n" +
                                            "-Si no cobramos los adeudos podrian acomularce y llegara un punto donde podriamos perder al cliente por un mal entendido\n\n";
                                }

                                //si la clienta no debe mas de 300 sin mercancia
                                //o mas de 300 y tenga mercancia
                                // o deba mas de 999 pesos  sin mercnacia acargo
                                //entonces no deberiamos de pasar a ver a esta clienta en esta quincena

                                prioridadDeVisita = BAJO;
                                mensajeAmostrar = "No pases a visitarla es de MES hoy no le toca cierre\n\n\n" +
                                        "-Faltan " + diasVencimientoOFaltanteParaCierre + " dias para su visita Su cierre es hasta el " + fechaVence  + "\n\n"+
                                        "-Aprovecha el tiempo para traer clientes nuevos";




                            }





                            break;

                        case CUENTA_VENCIDA_MENOR_DE_3_DIAS:
                            //(Las cuentas que lleven menos de 3 dias vencidas pueden ser porque movimos una ruta de dia
                            // por ejemplo porque un agente el dia de ayer no alzanzo a visitarla y la volvimos a mandar el dia de hoy
                            // por lo tanto presentara -1 dia de atraso. entonces a estas clientas las trataremos como si estuvieran en su
                            // fecha de corte copiar lo mismo de se podria hacer cierre)



                            if (dias == Constant.MES){

                                if (gradoCliente.equals(Constant.EMPRESARIA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "HOY toca su cierre esta clienta es de MES y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Si no la encuentras debes de hacer todos los Repasos posibles\n\n" +
                                            "-Muy seguramente realizara una buena venta entonces debemos pasar a visitarla\n\n" +
                                            "-Brindale un MUY BUEN SERVICIO y atencion ya que es EMPRESARIA\n\n" +
                                            "-Si la clienta te solicita 15 dias mas debes platicar con ella porque ya tiene mucho tiempo la mercancia\n\n" +
                                            "-Si no alcanzo a cobrar debes platicar en ella porque ya tuvo 1 mes completo para hacerlo y podria afectar su credito";
                                }

                                if (gradoCliente.equals(Constant.SOCIA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "HOY toca su cierre es de MES y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Si no la encuentras debes de hacer todos los Repasos posibles\n\n" +
                                            "-Debes pasar a visitarla y procurar no dejarla pendiente\n\n" +
                                            "-Motivala para que suba de grado a EMPRESARIA\n\n" +
                                            "-Si la clienta te solicita 15 dias mas, debes platicar con ella porque con un mes ya es tiempo suficiente\n\n" +
                                            "-Si no alcanzo a pagarte debes platicar en ella porque ya tuvo 1 mes completo para hacerlo y podria afectar su credito";
                                }

                                if (gradoCliente.equals(Constant.VENDEDORA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "HOY toca su cierre es de MES y es " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Si no la encuentras debes de hacer todos los Repasos posibles\n\n" +
                                            "-No la dejes pendiente es Vendedora y es de mes ya es mucho tiempo realiza su cierre\n\n" +
                                            "-Por su grado de Vendedora debemos encontrar a la clienta en su domicilio y que no nos haga dar muchos repasos\n\n" +
                                            "-Tiene que hacernos cierre y debe de tener su pago listo, o por lo menos la devolucion de producto, ya que tiene 1 mes completo la mercancia\n\n" +
                                            "-Si pide 15 dias mas tienes que hablar con ella,debe generar ventas a mes si no tendremos que bajarla a QUINCENA";
                                }
                            } else
                            {
                                //si la clienta es de quincena

                                if (gradoCliente.equals(Constant.EMPRESARIA)){
                                    prioridadDeVisita = ALTO;
                                    mensajeAmostrar = "HOY toca su cierre es de QUINCENA y " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Procura hacer todos los repasos posibles pero platica con la clienta para que te espere en su domicilio\n\n" +
                                            "-Es Empresaria, brindale una MUY BUEN SERVICIO si esta feliz aumentara sus ventas\n\n" +
                                            "Por ser empresaria le damos mayores ganancias entonces:\n\n" +
                                            "-La clienta debe estar lista para su cierre\n\n" +
                                            "-Debes platicar con ella para encontrarla en su domicilio cada dia de visita\n\n" +
                                            "-Deberia tener listo el pago por su venta generada\n\n" +
                                            "-Si es el caso y te pide quince dias mas platica con ella y recomiendale ser constante en sus ventas para que no se afecte su credito";
                                }

                                if (gradoCliente.equals(Constant.SOCIA)){
                                    prioridadDeVisita = NORMAL;
                                    mensajeAmostrar = "HOY toca su cierre es de QUINCENA y " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "Debes pasar a visitarla podria generarte una buena venta \n\n" +
                                            "-Si la clienta no esta en su domicilio trata de localizarla \n\n" +
                                            "-Si pide quince dias mas o no tiene el pago debemos platicar con ella y motivarla a que sea puntual en sus ventas\n\n" +
                                            "-Motiva a tu clienta para que incremente sus ventas y aumentarla de grado a Empresaria";
                                }

                                if (gradoCliente.equals(Constant.VENDEDORA)){
                                    prioridadDeVisita = NORMAL;
                                    mensajeAmostrar = "HOY toca su cierre es de QUINCENA y " + gradoCliente + " pasa a visitarla \n\n\n" +
                                            "-Motivarla para que eleve sus ventas y subirla de grado\n\n" +
                                            "-Por su grado de Vendedora debemos encontrar a la clienta en su domicilio\n\n" +
                                            "-No tiene que hacernos dar muchos repasos \n\n" +
                                            "-Debe tener listo su cierre y su pago\n\n" +
                                            "Debemos hablar con ella si:\n\n" +
                                            "-No la encontramos en su domicilio y nos hace dar muchos repaso\n\n" +
                                            "-No genero venta\n\n" +
                                            "-No tuvo el pago completo o se gasto el dinero";
                                }
                            }






                            break;

                        case CUENTA_VENCIDA_DE_4_A_13_DIAS:
                            //(a estas cuentas que presentan vencimientos algo importantes las catalogaremos dependiendo de su saldo pendiente
                            // y si aun tienen mercnacia acargo asi no importa si es empresaria socia o vendedora de mes o de quincena
                            // actualmente presentan un atraso considerable en su entrega de mercancia
                            // si la clienta no tiene saldo)



                            prioridadDeVisita = ATRASO;



                        //break;

                        case CUENTA_VENCIDA_EXACTO_14_DIAS:

                            if (dias == Constant.MES) {


                            prioridadDeVisita = ATRASO_URGENTE;

                            mensajeAmostrar = "Hoy no le toca cierre pero presenta un adeudo pendiente de $" + adeudo + " pasa a visitarla por su saldo pendiente\n\n\n" +
                                    "-Es solo un atraso entonces manten una atencion amable ya que esta clienta continua trabajando\n\n" +
                                    "-Si llegas a cobrarle de manera inadecuada la clienta se podria molestar y dejar de vender\n\n" +
                                    "-Es muy importante mantener su historial limpio y su cuenta al corriente\n\n" +
                                    "-Si no cobramos los adeudos podrian acomularce y llegara un punto donde podriamos perder al cliente por un mal entendido\n\n";



                        }

                            if (dias == Constant.QUINCE_DIAS) {


                                prioridadDeVisita = ATRASO_URGENTE;

                                mensajeAmostrar = "Tienes que pasar a visitarla es de QUINCENA presenta un atraso " + " con" + diasVencimientoOFaltanteParaCierre + " dias de atraso\n\n\n" +
                                        "-Es solo un atraso entonces manten una atencion amable ya que esta clienta continua trabajando\n\n" +
                                        "-Si llegas a cobrarle de manera inadecuada la clienta se podria molestar y dejar de vender\n\n" +
                                        "-Es muy importante mantener su historial limpio y su cuenta al corriente\n\n" +
                                        "-Si no cobramos los adeudos podrian acomularce y llegara un punto donde podriamos perder al cliente por un mal entendido\n\n";



                            }



                        break;


                        case CUENTA_VENCIDA_MAS_DE_15_DIAS:

                            //cualquier clienta que entre en vencida mas de quince dias entrara como Urgente
                            //porque si es de mes significa que no estuvo en su visita, de ahi tuvo 14 dias
                            //a la siguiente visita y si tampoco estuvo ahora pasa a mas de 15 dias
                            //por lo tanto ya es URGENTE


                            prioridadDeVisita = ATRASO_URGENTE;
                            mensajeAmostrar = "Tienes que pasar a visitarla y localizarla presenta un atraso de" + diasVencimientoOFaltanteParaCierre + " dias\n\n\n" +
                                    "-Aun no estas tratando con un Lio entonces manten una atencion amable\n\n" +
                                    "-Has lo posible para que liquide su cuenta y que no pase mas tiempo\n\n" +
                                    "-Si pasa mas tiempo podria afectar directamente su historial\n\n" +
                                    "-Podriamos terminar perdiendo a la clienta porque no le exigimos su liquidacion\n\n" +
                                    "-Es por el bien de ella que debemos mantener limpio su historial ya que si pasa mas tiempo podriamos perderla como clienta\n\n" +
                                    "-Lo mejor es presionarla para que liquide su cuenta";




                        break;


                    }


                }

                ContentValues contentValues = new ContentValues(2);
                contentValues.put(DataBaseHelper.CLIENTES_CLI_PRIORIDAD_DE_VISITA,prioridadDeVisita);
                contentValues.put(DataBaseHelper.CLIENTES_CLI_MENSAJE_MOSTRAR_POR_VISITAR, mensajeAmostrar);
                dataBaseHelper.clientes_actualizaTablaClientesPorKeyID(contentValues, id);


            }while (clientes.moveToNext());






        }







    }



    public void cargaZonificacionDesdeJson (JSONArray jsonArray, DataBaseHelper dataBaseHelper){
        dataBaseHelper.tabla_identificador_zona_eliminarIdZona();

        //[{"zona":"CANALEJAS","zonificacion":"-99.6323495443894,19.93921032464321,0 -99.60848303176516,19.9479430714427,0 -99.59240270694212,19.95660140905689,0 -99.5751407982179,19.95659581871729,0 -99.52877487825697,19.99720081430345,0 -99.56509305729576,20.01590022179094,0 -99.5783381825041,20.02086692872206,0 -99.59716294841314,20.03425292013289,0 -99.60888307613377,20.04452049981353,0"}
        // ,{"zona":"SAN JUAN DEL RIO","zonificacion":"-99.89717659228742,20.37502814621686,0 -99.87817863289942,20.43404142269898,0 -99.88849910583497,20.45004231623238,0 -99.95476702721507,20.49148390033696,0 -99.98413855211652,20.44735925623536,0 -100.0077545860647,20.41461869873617,0 -100.0289065842632,20.3799446837511,0"}]

        //necesitamos recorrer el array para sacar cada objeto de ahi
        for(int i=0; i<jsonArray.length(); i++){




            try {
                String nombreZona = jsonArray.getJSONObject(i).getString("zona");
                String zonificacion = jsonArray.getJSONObject(i).getString("zonificacion");

                dataBaseHelper.tabla_identificador_zona_insertarIdZona(nombreZona,zonificacion);
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }




        private void miEliminar () {

                Integer del = dbHelper.clientes_eliminaTablaClientes();
                dbHelper.tabla_identificador_zona_eliminarIdZona();      ///ELIMINAMOS LA BASE DE DATOS DE LOS IDENTIFICADORES DE SONA
                if(del >= 1){
                    Toast.makeText(this,"La base de datos fue Eliminada", Toast.LENGTH_SHORT).show();
            }
            Intent i = new Intent(getApplicationContext(), Trabajar.class);
            startActivity(i);
    }


    private View.OnClickListener mostrarPorVisitar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            crearListViewClientes(Constant.ESTADO_VISITAR);
            Constant.filtro = Constant.ESTADO_VISITAR; //ponemos a filtro "VISITAR"
        }
    };

    private View.OnClickListener mostrarRepasos = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            crearListViewClientes(Constant.ESTADO_REPASO);
            Constant.filtro = Constant.ESTADO_REPASO; //ponemos a filtro "REPASO"
        }
    };

    private View.OnClickListener mostrarVisitados = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            crearListViewClientes(Constant.ESTADO_VISITADO);
            Constant.filtro = Constant.ESTADO_VISITADO; //ponemos a filtro "VISITADO"
        }
    };

    private View.OnClickListener mostrarTodos = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            crearListViewClientes(Constant.ESTADO_TODOS);
            Constant.filtro = Constant.ESTADO_TODOS; //ponemos a filtro "TODOS"
        }
    };


    private View.OnClickListener mostrarNoVisitar = (new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            crearListViewClientes(Constant.ESTADO_NO_VISITAR);
            Constant.filtro = Constant.ESTADO_NO_VISITAR; //ponemos filtro no visitar
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        //crearListViewClientes(Constant.filtro); //llamamos al metodo para que llene la listView y que muestre el filtro en donde se quedo

    }




    private void ponerTituloBar ( String estado , int conteo){

            //PONEMOS LA FECHA COMO
            //subttulo de la barra
        /*
        Cursor res = dbHelper.tabla_identificador_zona_obtenerIdZona();
        res.moveToFirst();
        if (res.getCount()!= 0 ){
            String subtitulo = ConfiguracionesApp.getFechaClientesConsulta1(Clientes.this) +"    "+ res.getString(res.getColumnIndex(DataBaseHelper.ID_ZONA));
                    getSupportActionBar().setSubtitle(subtitulo);

            //Toast.makeText(getApplicationContext(), res.getString(res.getColumnIndex(DataBaseHelper.COORDENADAS_ZONA)), Toast.LENGTH_LONG).show();
        }*/
        String subtitulo = ConfiguracionesApp.getZonaVisitar1(Clientes.this) + " " + ConfiguracionesApp.getZonaVisitar2(Clientes.this) + " "
                        +ConfiguracionesApp.getFechaClientesConsulta1(Clientes.this) + " "
                        +ConfiguracionesApp.getFechaClientesConsulta2(Clientes.this) ;
        getSupportActionBar().setSubtitle(subtitulo);




        if (estado.equals(Constant.ESTADO_VISITAR)){
            getSupportActionBar().setTitle("Clientes por VISITAR: " + conteo);
        }

        if (estado.equals(Constant.ESTADO_REPASO)){
            getSupportActionBar().setTitle("Clientes de REPASO: " + conteo);
        }

        if (estado.equals(Constant.ESTADO_VISITADO)){
            getSupportActionBar().setTitle("Clientes VISITADOS: " + conteo);
        }

        if (estado.equals(Constant.ESTADO_TODOS)){
            getSupportActionBar().setTitle("TODOS los clientes: " + conteo);
        }

        if (estado.equals (Constant.ESTADO_NO_VISITAR)){
            getSupportActionBar().setTitle("Clientes ya NO VISITAR: " + conteo);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //getMenuInflater().inflate(R.menu.menu_clientes,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.eliminarM:
                //confirmarEliminarClientes(this);
                return true;

                default:
                    return super.onOptionsItemSelected(item);

        }
    }


    private void dialogIrMapa (final String cuentaCliente){

        final AlertDialog.Builder builder = new AlertDialog.Builder(Clientes.this);


        TextView title = new TextView(Clientes.this);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("Volar alla?");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("Mostrar en el mapa cuenta" + cuentaCliente)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intentViewOnMap = new Intent(getApplicationContext(), MapsActivity.class);
                        intentViewOnMap.putExtra("CLIENTES_ADMIN_CUENTA_CLIENTE",cuentaCliente);
                        startActivity(intentViewOnMap);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.create();
        builder.show();

    }



    public void confirmarEliminarClientes (final Activity activity) {

        //(LLAMAMOS ESTE METODO PARA PEDIR LA CONFIRMACION
        // PARA ELIMINAR LA BASE DE DATOS DE CLIENTES)

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("PIENSALO DOS VECES");
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage("¿Estas seguro que quieres eliminar la base de datos Clientes?")
                .setPositiveButton("Si \n eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        miEliminar();
                    }
                })
                .setNegativeButton("NO \n ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.create();
        builder.show();


    }//METODO PARA CONFIRMAR E IR A MOVIMIENTOS



}

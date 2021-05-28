package mx.greenmouse.kaliope;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import luisda.fragments.DiaNominaFragment;
import luisda.fragments.FinalNominaFragment;
import mx.com.kaliope.luisda.KaliopeServerClient;


public class NominasKaliope extends AppCompatActivity implements DiaNominaFragment.OnFragmentInteractionListener, FinalNominaFragment.OnFragmentInteractionListener
       {

    Button BtnConsultar;
    Spinner spinnerAgentesExistentes, spinnerSemanasExistentes;
    EditText etCodigoConsulta;







    String pruebaDeConexion = "";
    String campoEmpleado = "";
    String campoSemana = "";



    private static final String PASSWORD = "klp2589";







    //creamos nuestras 7 instancias de nuestros dias que vamos a proyectar
    DiaNominaFragment diaNominaFragment1 = new DiaNominaFragment();
    DiaNominaFragment diaNominaFragment2 = new DiaNominaFragment();
    DiaNominaFragment diaNominaFragment3 = new DiaNominaFragment();
    DiaNominaFragment diaNominaFragment4 = new DiaNominaFragment();
    DiaNominaFragment diaNominaFragment5 = new DiaNominaFragment();
    DiaNominaFragment diaNominaFragment6 = new DiaNominaFragment();
    DiaNominaFragment diaNominaFragment7 = new DiaNominaFragment();
    FinalNominaFragment finalNominaFragment = new FinalNominaFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nominas_kaliope);
        getSupportActionBar().hide();

        BtnConsultar = (Button) findViewById(R.id.nominasKaliopeConsultarB);
        spinnerAgentesExistentes = (Spinner) findViewById(R.id.spinnerAgenteNominasKaliope);
        spinnerSemanasExistentes = (Spinner) findViewById(R.id.spinnerNumeroSemanaNominasKaliope);
        etCodigoConsulta = (EditText) findViewById(R.id.nominasKaliopeCodigoET);



        //en cuanto se abra la actividad me comunico con el servidor
        //le enviamos vacios el campo de empleado y numSemana para que el servidor
        //entienda que nos debe de devolver los empleados existentes
        //conectarKaliope(PASSWORD,"","");
        solicitarAgentesAlServidor(KaliopeServerClient.BASE_URL + "/retorno_agentes_app.php" + "?password=" + PASSWORD);



        etCodigoConsulta.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                    //respondemos al evento de la tecla enter
                    botonConsultarMetodo();
                    return true;
                }

                return false;
            }
        });



        spinnerAgentesExistentes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(), String.valueOf(spinnerAgentesExistentes.getSelectedItem()) , Toast.LENGTH_SHORT).show();
                campoEmpleado = String.valueOf(spinnerAgentesExistentes.getSelectedItem());

                //Una ves que se selecciona el empleado en automatico solicitamos al servidor la informacion de las semanas
                //enviando como parametro el Empleado
                solicitarSemanasAlServidor(KaliopeServerClient.BASE_URL + "/retorno_semana_app.php" + "?password=" + PASSWORD + "&empleado=" + campoEmpleado);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnerSemanasExistentes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                campoSemana = String.valueOf(spinnerSemanasExistentes.getSelectedItem());
                //Toast.makeText(NominasKaliope.this, campoSemana, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        BtnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                botonConsultarMetodo();



            }
        });


    }



    private void botonConsultarMetodo (){

        //movemos lo que estaba en el listener del boton consultar a un metodo porque tambien escucharemos el boton
        //del teclado "Done" cuando se ingrese el password
        solicitarNominasAlServidor(KaliopeServerClient.BASE_URL+"retorno_nominas_app.php" + "?password=" + PASSWORD + "&empleado=" + campoEmpleado + "&numeroSemana=" +campoSemana);



        if (DiaNominaFragment.contadorDeInstancias>0){
            //Toast.makeText(NominasKaliope.this, String.valueOf(DiaNominaFragment.contadorDeInstancias), Toast.LENGTH_SHORT).show();
            //PARA CERRAR EL FRAGMENT DESDE LA ACTIVIDAD
            //https://www.flipandroid.com/eliminar-un-fragmento-especfico-de-la-backstack-de-android.html
            FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
            fragmentTransaction1.remove(diaNominaFragment1);
            fragmentTransaction1.remove(diaNominaFragment2);
            fragmentTransaction1.remove(diaNominaFragment3);
            fragmentTransaction1.remove(diaNominaFragment4);
            fragmentTransaction1.remove(diaNominaFragment5);
            fragmentTransaction1.remove(diaNominaFragment6);
            fragmentTransaction1.remove(diaNominaFragment7);
            fragmentTransaction1.remove(finalNominaFragment);
            fragmentTransaction1.commit();
        }

    }



    private void cargarFragmentos (){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        fragmentTransaction.add (R.id.linearNominas,diaNominaFragment1);
        fragmentTransaction.add (R.id.linearNominas,diaNominaFragment2);
        fragmentTransaction.add (R.id.linearNominas,diaNominaFragment3);
        fragmentTransaction.add (R.id.linearNominas,diaNominaFragment4);
        fragmentTransaction.add (R.id.linearNominas,diaNominaFragment5);
        fragmentTransaction.add (R.id.linearNominas,diaNominaFragment6);
        fragmentTransaction.add (R.id.linearNominas,diaNominaFragment7);
        fragmentTransaction.add(R.id.linearNominas,finalNominaFragment);

        fragmentTransaction.commit();



    }



    private String conectarServidor(String URL){
               HttpClient httpClient = new DefaultHttpClient();
               HttpContext httpContext = new BasicHttpContext();
               //String parametros = "?password=" + password + "&empleado=" + empleado + "&numeroSemana=" + numeroSemana;

               HttpGet httpGet = new HttpGet(URL);
               String resultado = null;


               try {
                   HttpResponse response = httpClient.execute(httpGet,httpContext);
                   HttpEntity httpEntity = response.getEntity();
                   resultado = EntityUtils.toString(httpEntity,"UTF-8");
               } catch (Exception e) {
                   e.printStackTrace();
               }

               return resultado;
           }







    private void solicitarAgentesAlServidor(final String URL){

        new Thread(new Runnable() {
            @Override
            public void run() {

                final String resultado = conectarServidor(URL);



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    obtenerDatosJSONParaEmpleados(resultado);

                    }
                });


            }
        }).start();

    }
    private void obtenerDatosJSONParaEmpleados(String response){
        ArrayList<String> agentesExistentes = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(response);
            for(int i = 0; i<jsonArray.length();i++){
                //llenamos los arrays con los que llenaremos despues los spinner de agentes
                agentesExistentes.add(jsonArray.getJSONObject(i).getString("agente"));
            }

            int hola =1;







            ArrayAdapter<String> adapterAgentes = new ArrayAdapter<>(this,R.layout.simple_spinner_item_luisda,agentesExistentes);
            spinnerAgentesExistentes.setAdapter(adapterAgentes);


        }catch (Exception e){
            e.printStackTrace();
        }
    }






    private void solicitarSemanasAlServidor(final String URL){

               new Thread(new Runnable() {
                   @Override
                   public void run() {

                       final String resultado = conectarServidor(URL);



                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {

                               obtenerDatosJSONParaSemana(resultado);

                           }
                       });


                   }
               }).start();

           }
    private void obtenerDatosJSONParaSemana(String response){
          ArrayList<String> semanasExistentes = new ArrayList<>();
          try{
               JSONArray jsonArray = new JSONArray(response);
               for(int i = 0; i<jsonArray.length();i++){
                //llenamos los arrays con los que llenaremos despues los spinner de agentes
                semanasExistentes.add(jsonArray.getJSONObject(i).getString("semana"));
                }

                ArrayAdapter<String> adapterSemana = new ArrayAdapter<>(this,R.layout.simple_spinner_item_luisda,semanasExistentes);
                spinnerSemanasExistentes.setAdapter(adapterSemana);
          }catch (Exception e){
              e.printStackTrace();
          }
    }





    private void solicitarNominasAlServidor(final String URL){

               new Thread(new Runnable() {
                   @Override
                   public void run() {

                       final String resultado = conectarServidor(URL);



                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {

                               obtenerDatosJSONParaNominas(resultado);

                           }
                       });


                   }
               }).start();

           }
    private void obtenerDatosJSONParaNominas(String response){

        String pulcera = "";
               try{
                   JSONArray jsonArray = new JSONArray(response);

                  //a los fragmentos estoy enviando todos los campos de la tabla, que recibimos, ya dentro del fragment
                   //decidimos que campo usar y cual no
                   for(int i = 0; i<jsonArray.length();i++){

                       pulcera  = jsonArray.getJSONObject(i).getString("pulcera");

                       finalNominaFragment.recibirInformacion(
                               "Nomina de " + jsonArray.getJSONObject(i).getString("agente")
                               ,jsonArray.getJSONObject(i).getString("fecha_lunes")
                               ,jsonArray.getJSONObject(i).getString("fecha_domingo")
                               ,jsonArray.getJSONObject(i).getString("folio")
                               ,jsonArray.getJSONObject(i).getString("semana")
                               ,jsonArray.getJSONObject(i).getString("agente")
                               ,jsonArray.getJSONObject(i).getString("pulcera")
                               ,jsonArray.getJSONObject(i).getString("sueldo_base")
                               ,jsonArray.getJSONObject(i).getString("porcentaje_comicion")
                               ,jsonArray.getJSONObject(i).getString("comiciones")
                               ,jsonArray.getJSONObject(i).getString("porcentaje_lios")
                               ,jsonArray.getJSONObject(i).getString("bono_lios")
                               ,jsonArray.getJSONObject(i).getString("pago_nuevas")
                               ,jsonArray.getJSONObject(i).getString("bono_nuevas")
                               ,jsonArray.getJSONObject(i).getString("bono1")
                               ,jsonArray.getJSONObject(i).getString("descripcion_bono1")
                               ,jsonArray.getJSONObject(i).getString("bono2")
                               ,jsonArray.getJSONObject(i).getString("descripcion_bono2")
                               ,jsonArray.getJSONObject(i).getString("bono3")
                               ,jsonArray.getJSONObject(i).getString("descripcion_bono3")
                               ,jsonArray.getJSONObject(i).getString("bono4")
                               ,jsonArray.getJSONObject(i).getString("descripcion_bono4")
                               ,jsonArray.getJSONObject(i).getString("subtotal")
                               ,jsonArray.getJSONObject(i).getString("total_adelantos")
                               ,jsonArray.getJSONObject(i).getString("deuda1")
                               ,jsonArray.getJSONObject(i).getString("descuento1")
                               ,jsonArray.getJSONObject(i).getString("concepto_descuento1")
                               ,jsonArray.getJSONObject(i).getString("adeudo_restante1")
                               ,jsonArray.getJSONObject(i).getString("deuda2")
                               ,jsonArray.getJSONObject(i).getString("descuento2")
                               ,jsonArray.getJSONObject(i).getString("concepto_descuento2")
                               ,jsonArray.getJSONObject(i).getString("adeudo_restante2")
                               ,jsonArray.getJSONObject(i).getString("deuda3")
                               ,jsonArray.getJSONObject(i).getString("descuento3")
                               ,jsonArray.getJSONObject(i).getString("concepto_descuento3")
                               ,jsonArray.getJSONObject(i).getString("adeudo_restante3")
                               ,jsonArray.getJSONObject(i).getString("deuda4")
                               ,jsonArray.getJSONObject(i).getString("descuento4")
                               ,jsonArray.getJSONObject(i).getString("concepto_descuento4")
                               ,jsonArray.getJSONObject(i).getString("adeudo_restante4")
                               ,jsonArray.getJSONObject(i).getString("deuda5")
                               ,jsonArray.getJSONObject(i).getString("descuento5")
                               ,jsonArray.getJSONObject(i).getString("concepto_descuento5")
                               ,jsonArray.getJSONObject(i).getString("adeudo_restante5")
                               ,jsonArray.getJSONObject(i).getString("deuda6")
                               ,jsonArray.getJSONObject(i).getString("descuento6")
                               ,jsonArray.getJSONObject(i).getString("concepto_descuento6")
                               ,jsonArray.getJSONObject(i).getString("adeudo_restante6")
                               ,jsonArray.getJSONObject(i).getString("porcentaje_ahorro")
                               ,jsonArray.getJSONObject(i).getString("descuento_ahorro")
                               ,jsonArray.getJSONObject(i).getString("total_nomina")
                               ,jsonArray.getJSONObject(i).getString("caja_ahorros_disponible")
                               ,jsonArray.getJSONObject(i).getString("incremento_caja_ahorro")
                               ,jsonArray.getJSONObject(i).getString("retiro_caja_ahorro")
                               ,jsonArray.getJSONObject(i).getString("concepto_retiro_caja_ahorro")
                               ,jsonArray.getJSONObject(i).getString("total_caja")
                               ,jsonArray.getJSONObject(i).getString("bloqueo")
                               ,jsonArray.getJSONObject(i).getString("motivo")
                               ,jsonArray.getJSONObject(i).getString("cobro_total")
                               ,jsonArray.getJSONObject(i).getString("nuevas_totales")
                               ,jsonArray.getJSONObject(i).getString("lios_totales")
                       );


                       diaNominaFragment1.recibirParametros(
                               "Lunes " + jsonArray.getJSONObject(i).getString("agente"),
                               jsonArray.getJSONObject(i).getString(  "zona_lunes")
                               , jsonArray.getJSONObject(i).getString("fecha_lunes")
                               , jsonArray.getJSONObject(i).getString("cobro_lunes")
                               , jsonArray.getJSONObject(i).getString("nuevas_lunes")
                               , jsonArray.getJSONObject(i).getString("lios_lunes")
                               , jsonArray.getJSONObject(i).getString("sobrante_lunes")
                               , jsonArray.getJSONObject(i).getString("adelantos_lunes")
                               , jsonArray.getJSONObject(i).getString("gastos_lunes_1")
                               , jsonArray.getJSONObject(i).getString("concepto_lunes_1")
                               , jsonArray.getJSONObject(i).getString("gastos_lunes_2")
                               , jsonArray.getJSONObject(i).getString("concepto_lunes_2")
                               , jsonArray.getJSONObject(i).getString("gastos_lunes_3")
                               , jsonArray.getJSONObject(i).getString("concepto_lunes_3")
                               , jsonArray.getJSONObject(i).getString("gastos_lunes_4")
                               , jsonArray.getJSONObject(i).getString("concepto_lunes_4")
                               , jsonArray.getJSONObject(i).getString("gasolina_lunes")
                               , jsonArray.getJSONObject(i).getString("total_lunes")
                       );


                       diaNominaFragment2.recibirParametros(
                               "Martes " + jsonArray.getJSONObject(i).getString("agente"),
                               jsonArray.getJSONObject(i).getString(  "zona_martes")
                               , jsonArray.getJSONObject(i).getString("fecha_martes")
                               , jsonArray.getJSONObject(i).getString("cobro_martes")
                               , jsonArray.getJSONObject(i).getString("nuevas_martes")
                               , jsonArray.getJSONObject(i).getString("lios_martes")
                               , jsonArray.getJSONObject(i).getString("sobrante_martes")
                               , jsonArray.getJSONObject(i).getString("adelantos_martes")
                               , jsonArray.getJSONObject(i).getString("gastos_martes_1")
                               , jsonArray.getJSONObject(i).getString("concepto_martes_1")
                               , jsonArray.getJSONObject(i).getString("gastos_martes_2")
                               , jsonArray.getJSONObject(i).getString("concepto_martes_2")
                               , jsonArray.getJSONObject(i).getString("gastos_martes_3")
                               , jsonArray.getJSONObject(i).getString("concepto_martes_3")
                               , jsonArray.getJSONObject(i).getString("gastos_martes_4")
                               , jsonArray.getJSONObject(i).getString("concepto_martes_4")
                               , jsonArray.getJSONObject(i).getString("gasolina_martes")
                               , jsonArray.getJSONObject(i).getString("total_martes")
                       );

                       diaNominaFragment3.recibirParametros(
                               "Miercoles " + jsonArray.getJSONObject(i).getString("agente"),
                               jsonArray.getJSONObject(i).getString(  "zona_miercoles")
                               , jsonArray.getJSONObject(i).getString("fecha_miercoles")
                               , jsonArray.getJSONObject(i).getString("cobro_miercoles")
                               , jsonArray.getJSONObject(i).getString("nuevas_miercoles")
                               , jsonArray.getJSONObject(i).getString("lios_miercoles")
                               , jsonArray.getJSONObject(i).getString("sobrante_miercoles")
                               , jsonArray.getJSONObject(i).getString("adelantos_miercoles")
                               , jsonArray.getJSONObject(i).getString("gastos_miercoles_1")
                               , jsonArray.getJSONObject(i).getString("concepto_miercoles_1")
                               , jsonArray.getJSONObject(i).getString("gastos_miercoles_2")
                               , jsonArray.getJSONObject(i).getString("concepto_miercoles_2")
                               , jsonArray.getJSONObject(i).getString("gastos_miercoles_3")
                               , jsonArray.getJSONObject(i).getString("concepto_miercoles_3")
                               , jsonArray.getJSONObject(i).getString("gastos_miercoles_4")
                               , jsonArray.getJSONObject(i).getString("concepto_miercoles_4")
                               , jsonArray.getJSONObject(i).getString("gasolina_miercoles")
                               , jsonArray.getJSONObject(i).getString("total_miercoles")
                       );


                       diaNominaFragment4.recibirParametros(
                               "Jueves " + jsonArray.getJSONObject(i).getString("agente"),
                               jsonArray.getJSONObject(i).getString(  "zona_jueves")
                               , jsonArray.getJSONObject(i).getString("fecha_jueves")
                               , jsonArray.getJSONObject(i).getString("cobro_jueves")
                               , jsonArray.getJSONObject(i).getString("nuevas_jueves")
                               , jsonArray.getJSONObject(i).getString("lios_jueves")
                               , jsonArray.getJSONObject(i).getString("sobrante_jueves")
                               , jsonArray.getJSONObject(i).getString("adelantos_jueves")
                               , jsonArray.getJSONObject(i).getString("gastos_jueves_1")
                               , jsonArray.getJSONObject(i).getString("concepto_jueves_1")
                               , jsonArray.getJSONObject(i).getString("gastos_jueves_2")
                               , jsonArray.getJSONObject(i).getString("concepto_jueves_2")
                               , jsonArray.getJSONObject(i).getString("gastos_jueves_3")
                               , jsonArray.getJSONObject(i).getString("concepto_jueves_3")
                               , jsonArray.getJSONObject(i).getString("gastos_jueves_4")
                               , jsonArray.getJSONObject(i).getString("concepto_jueves_4")
                               , jsonArray.getJSONObject(i).getString("gasolina_jueves")
                               , jsonArray.getJSONObject(i).getString("total_jueves")
                       );

                       diaNominaFragment5.recibirParametros(
                               "Viernes " + jsonArray.getJSONObject(i).getString("agente"),
                               jsonArray.getJSONObject(i).getString(  "zona_viernes")
                               , jsonArray.getJSONObject(i).getString("fecha_viernes")
                               , jsonArray.getJSONObject(i).getString("cobro_viernes")
                               , jsonArray.getJSONObject(i).getString("nuevas_viernes")
                               , jsonArray.getJSONObject(i).getString("lios_viernes")
                               , jsonArray.getJSONObject(i).getString("sobrante_viernes")
                               , jsonArray.getJSONObject(i).getString("adelantos_viernes")
                               , jsonArray.getJSONObject(i).getString("gastos_viernes_1")
                               , jsonArray.getJSONObject(i).getString("concepto_viernes_1")
                               , jsonArray.getJSONObject(i).getString("gastos_viernes_2")
                               , jsonArray.getJSONObject(i).getString("concepto_viernes_2")
                               , jsonArray.getJSONObject(i).getString("gastos_viernes_3")
                               , jsonArray.getJSONObject(i).getString("concepto_viernes_3")
                               , jsonArray.getJSONObject(i).getString("gastos_viernes_4")
                               , jsonArray.getJSONObject(i).getString("concepto_viernes_4")
                               , jsonArray.getJSONObject(i).getString("gasolina_viernes")
                               , jsonArray.getJSONObject(i).getString("total_viernes")
                       );


                       diaNominaFragment6.recibirParametros(
                               "Sabado " + jsonArray.getJSONObject(i).getString("agente"),
                               jsonArray.getJSONObject(i).getString(  "zona_sabado")
                               , jsonArray.getJSONObject(i).getString("fecha_sabado")
                               , jsonArray.getJSONObject(i).getString("cobro_sabado")
                               , jsonArray.getJSONObject(i).getString("nuevas_sabado")
                               , jsonArray.getJSONObject(i).getString("lios_sabado")
                               , jsonArray.getJSONObject(i).getString("sobrante_sabado")
                               , jsonArray.getJSONObject(i).getString("adelantos_sabado")
                               , jsonArray.getJSONObject(i).getString("gastos_sabado_1")
                               , jsonArray.getJSONObject(i).getString("concepto_sabado_1")
                               , jsonArray.getJSONObject(i).getString("gastos_sabado_2")
                               , jsonArray.getJSONObject(i).getString("concepto_sabado_2")
                               , jsonArray.getJSONObject(i).getString("gastos_sabado_3")
                               , jsonArray.getJSONObject(i).getString("concepto_sabado_3")
                               , jsonArray.getJSONObject(i).getString("gastos_sabado_4")
                               , jsonArray.getJSONObject(i).getString("concepto_sabado_4")
                               , jsonArray.getJSONObject(i).getString("gasolina_sabado")
                               , jsonArray.getJSONObject(i).getString("total_sabado")
                       );

                       diaNominaFragment7.recibirParametros(
                               "Domingo " + jsonArray.getJSONObject(i).getString("agente"),
                               jsonArray.getJSONObject(i).getString(  "zona_domingo")
                               , jsonArray.getJSONObject(i).getString("fecha_domingo")
                               , jsonArray.getJSONObject(i).getString("cobro_domingo")
                               , jsonArray.getJSONObject(i).getString("nuevas_domingo")
                               , jsonArray.getJSONObject(i).getString("lios_domingo")
                               , jsonArray.getJSONObject(i).getString("sobrante_domingo")
                               , jsonArray.getJSONObject(i).getString("adelantos_domingo")
                               , jsonArray.getJSONObject(i).getString("gastos_domingo_1")
                               , jsonArray.getJSONObject(i).getString("concepto_domingo_1")
                               , jsonArray.getJSONObject(i).getString("gastos_domingo_2")
                               , jsonArray.getJSONObject(i).getString("concepto_domingo_2")
                               , jsonArray.getJSONObject(i).getString("gastos_domingo_3")
                               , jsonArray.getJSONObject(i).getString("concepto_domingo_3")
                               , jsonArray.getJSONObject(i).getString("gastos_domingo_4")
                               , jsonArray.getJSONObject(i).getString("concepto_domingo_4")
                               , jsonArray.getJSONObject(i).getString("gasolina_domingo")
                               , jsonArray.getJSONObject(i).getString("total_domingo")
                       );

                   }


                  if(etCodigoConsulta.getText().toString().equals(pulcera)){
                      cargarFragmentos();
                  }else {
                       new AlertDialog.Builder(NominasKaliope.this)
                               .setTitle("Ingresa un Codigo correcto")
                               .setMessage("Para consultar tus datos por favor ingresa tu codigo personal")
                               .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {
                                       dialogInterface.dismiss();
                                   }
                               }).create().show();
                  }



               }catch (Exception e){
                   e.printStackTrace();
               }
           }























    private void conectarKaliope (final String password, final String empleado, final String numSemana){

        //nos conectamos al servidor y enviamos el password para validar al usuario
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final String RES;
                    RES = enviaDatosAlServidor(password,empleado,numSemana);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NominasKaliope.this, RES, Toast.LENGTH_SHORT).show();
                        }
                    });


                }catch (Exception e){
                    e.printStackTrace();
                }



            }
        }).start();


    }

    private String enviaDatosAlServidor(String password, String empleado, String numSemana){



    HttpClient httpClient = new DefaultHttpClient();
    HttpContext httpContext = new BasicHttpContext();
    HttpPost httpPost = new HttpPost("https://www.kaliope.com.mx/prueva_conexion.php");

    HttpResponse httpResponse = null;
    try {
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("password",password));
        params.add(new BasicNameValuePair("empleado", empleado));
        params.add(new BasicNameValuePair("numeroSemana",numSemana));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        httpResponse = httpClient.execute(httpPost,httpContext);
    }catch (Exception e){
        e.printStackTrace();
    }

    return  httpResponse.toString();

}






    @Override
    public void onFragmentInteraction(Uri uri) {

    }





}

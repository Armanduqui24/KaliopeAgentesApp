package mx.greenmouse.kaliope;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cierre extends AppCompatActivity implements View.OnClickListener {
    Button termineCierre;
    int importeAcargo,
        importeDevuelto,
        ventaGenerada;

    String fechaVence;


    boolean continuar = true;

    String nombre;


    //array de imagenes para que se seleccionen al azar
    //private int [] imagenesBajo = {R.drawable.bajo1,R.drawable.bajo2,R.drawable.bajo3,R.drawable.bajo4,R.drawable.bajo5,R.drawable.bajo6};
    //private int [] imagenesExcelente = {R.drawable.excelente1,R.drawable.excelente2,R.drawable.excelente3,R.drawable.excelente4,R.drawable.excelente5,R.drawable.excelente6};
    //private int [] imagenesIntermedio = {R.drawable.intermedio1,R.drawable.intermedio2,R.drawable.intermedio3,R.drawable.intermedio4,R.drawable.intermedio5,R.drawable.intermedio6};
    //private int [] imagenesMal = {R.drawable.mal1,R.drawable.mal2,R.drawable.mal3,R.drawable.mal4,R.drawable.mal5,R.drawable.mal6};


    Animation translacion;


    Transition transition;
    DataBaseHelper dbHelper = new DataBaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        // Definimos la animacion de entrada al activity
        Slide slide = new Slide(Gravity.END);
        slide.setDuration(AltaEntradaActivity.DURATION_TRANSITION);
        slide.setInterpolator(new DecelerateInterpolator());
        getWindow().setEnterTransition(slide);
        getWindow().setAllowEnterTransitionOverlap(false);


        setContentView(R.layout.activity_cierre);


        termineCierre = (Button) findViewById(R.id.btnTermineCierre);
        termineCierre.setOnClickListener(this);

        obtenerDatosCliente();
        //mensajes();
        cargarVistas();



    }

    @Override
    public void onClick(View view) {
        if (continuar) {

            switch (view.getId()) {
                case R.id.btnTermineCierre:

                    transition = new Slide(Gravity.START);
                    iniciarActividadSiguiente();
            }
        }
    }



    @SuppressWarnings("unchecked")
    private void iniciarActividadSiguiente (){
        transition.setDuration(AltaEntradaActivity.DURATION_TRANSITION);
        transition.setInterpolator(new DecelerateInterpolator());
        getWindow().setExitTransition(transition);
        Intent intent = new Intent(this,PagosLuisda.class);
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }




     //Creamos nuestro Holder contenedor de vistas, esto no es tan necesario pero lo hacemos para
    //llevar un orden en nuestro programa

    private class Holder {
        TextView nombre,
                 acargo,
                 devolucion,
                 venta,
                 mensaje,
                 fechaVencimiento,
                 fechaActual;
    }


    //Rescatamos de la base de datos los nombres del cliente
    //y la cantidad de dinero acargo desde la base de datos

    private void obtenerDatosCliente (){



        Cursor res = dbHelper.clientes_dameClientesPorId(Constant.ID_CLIENTE);
        res.moveToFirst();

        if (res.getCount() == 1 ){
            nombre = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE));
            importeAcargo = Integer.parseInt(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE)));
            importeDevuelto = Integer.parseInt(Constant.TMPMOV_TOREFUND);
            fechaVence = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_VENCIMIENTO));
            ventaGenerada = calcular();
            Constant.VENTA_GENERADA = calcular();//guiardamos en la constante la venta obtenida


            compararFechas();

        }
    }


    //Comparar Fechas
    //fuente https://es.wikihow.com/comparar-dos-fechas-en-Java
    //https://es.stackoverflow.com/questions/42069/ayuda-en-funci%C3%B3n-para-comparar-la-fecha-del-sistema-con-una-fecha-elegida-en-un
    private void compararFechas()  {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");



        try {
            Date dateVence = simpleDateFormat.parse(fechaVence);
            Date dateActual = simpleDateFormat.parse(utilidadesApp.getFecha());
            String fecha1 = simpleDateFormat.format(dateVence);
            String fecha2 = simpleDateFormat.format(dateActual);
            Log.i("dateVence", fecha1 );
            Log.i("dateActual", fecha2 );

            //metodo compareTo devuelve -1 si la fecha a comparar es menor al argumento date
            //es decir si la fecha de vencimiento es anterior a la actual quiere decir que el documento
            //ya vencio.
            //devuelve 0 si ambas fechas son el mismo punto en el tiempo
            //significa que el vencimiento esta justo al dia con la fecha actual
            //devuelve 1 si la fecha a comparar es posterior o despues del argumento date
            //si la fecha de vencimiento es mayor a la actual es decir que se esta haceindo el
            //cierre antes de la fecha pactada
            int compare = dateVence.compareTo(dateActual);
            Log.i("valor de compare To",String.valueOf(compare));

            switch (compare){
                case -1:
                    Constant.MOMENTO_DE_CIERRE = Constant.CIERRE_TARDE;
                    Toast.makeText(this,"cierre tarde",Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    Constant.MOMENTO_DE_CIERRE = Constant.CIERRE_A_TIEMPO;
                    Toast.makeText(this,"cierre a tiempo",Toast.LENGTH_SHORT).show();

                    break;
                case 1:
                    Constant.MOMENTO_DE_CIERRE = Constant.CIERRE_ADELANTADO;
                    Toast.makeText(this,"cierre adelantado",Toast.LENGTH_SHORT).show();

                    break;
            }


        } catch (ParseException e) {
            //manejamos la excepcion de error si hay algun dato que no se puede pasar a fecha
            e.printStackTrace();
            Log.i("en catch","en catch");
            Constant.MOMENTO_DE_CIERRE = Constant.CIERRE_INVALIDO;
        }


    }



    private void cargarVistas (){

        Holder h = new Holder();

        translacion = AnimationUtils.loadAnimation(this,R.anim.escala); //cargamos la animacion pruebas de animacion de elementos en la pantalla
        translacion.setFillAfter(true);//para que se quede donde termina la anim
        translacion.setRepeatMode(Animation.REVERSE); //modo de repeticion, en el reverse se ejecuta la app y cuando termine de ejecutarse va  adar reversa
        translacion.setRepeatCount(Animation.INFINITE); //cuantas veces queremos que se repita la animacion, podria ser un numero entero 20 para 20 veces por ejemplo

        h.nombre = (TextView) findViewById(R.id.clienteCierreTV);
        h.acargo = (TextView) findViewById(R.id.textView19);
        h.devolucion = (TextView) findViewById(R.id.textView22);
        h.venta = (TextView) findViewById(R.id.textView26);
        h.mensaje = (TextView) findViewById(R.id.textView52);
        h.fechaVencimiento = (TextView) findViewById(R.id.textView54);
        h.fechaActual = (TextView) findViewById(R.id.textView56);

        h.nombre.setText(nombre);
        h.acargo.setText(String.valueOf(importeAcargo));
        h.devolucion.setText(String.valueOf(importeDevuelto));     //OBTENEMOS EL TOTAL DEVUELTO, ESTA GUARDADO EN ESA CONSTANTE DE AltaEntradaActivity

        h.fechaVencimiento.setText(fechaVence);
        h.fechaActual.setText(utilidadesApp.getFecha());
        h.venta.setText(String.valueOf(ventaGenerada));

        //mensaje de error
        if (ventaGenerada < 0){
            h.mensaje.setBackgroundResource(R.color.colorLio);
            h.mensaje.setTextColor(Color.BLACK);
            h.mensaje.startAnimation(translacion);//le definimos al elemento que ejecutara la animacion
            h.mensaje.setText("Error, la devolucion es mayor que la mercancia acargo, no podemos continuar. Por favor revise la devolución!");
        }else{
            h.mensaje.setBackgroundColor(Color.TRANSPARENT);
            //h.mensaje.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }


    //Calculamos los datos necesarios
    private int calcular (){
        int v;
        v =  importeAcargo - importeDevuelto;
        return v;
    }


//    //Mensajes para el cliente
//    private void mensajes () {
//        String msg2 = "La venta de tu cliente es:" + "$" + String.valueOf(ventaGenerada);
//        //validamos que la venta generada no salga en negativo por algun error de captura en la devolucion
//        if (ventaGenerada < 0 ){
//            continuar = false;
//            dialogoMotivacion("Hay algun error en la devolucion, lo devuelto es mayor a lo que el cliente tenia acargo revisar!!",msg2,seleccionarImagen(3));
//
//        } else{
//
//            //mostraremos los mensajes dependiendo de la cantidad de venta generada con referencia a la mercancia acargo
//            if (ventaGenerada > importeAcargo * 0.7) {
//                dialogoMotivacion("Tu cliente Realizo una muy buena venta se merece una felicitacion!!",msg2,seleccionarImagen(0));
//            }else {
//
//                if (ventaGenerada > importeAcargo * 0.3){
//                    dialogoMotivacion("Nuestro cliente tiene una venta promedio, puede vender mas!!! motivalo para elevar sus ventas",msg2,seleccionarImagen(1));
//                }else{
//                    dialogoMotivacion("Tu cliente ha tenido una venta baja, necesita motivacion para aumentar sus ventas ayudalo!!",msg2,seleccionarImagen(2));
//
//                }
//            }
//        }
//    }

    private void dialogoMotivacion (String mensaje, String mensaje2, int idImagen){

        //creamos el builder y le definimos el estilo de animacion para el cuadro emergente
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.PauseDialog);


        //inflamos la vista personalizada
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.aviso_felicitaciones,null);

        TextView msg = (TextView) v.findViewById(R.id.textView101);
        TextView msg2 = (TextView) v.findViewById(R.id.textView102);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView102);

        msg.setText(mensaje);
        msg2.setText(mensaje2);
        imageView.setImageResource(idImagen);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setView(v);

        Button yaFelicite = (Button)v.findViewById(R.id.Button100);
        yaFelicite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }

        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.create();
        alertDialog.show();
    }

//    private int seleccionarImagen (int tipoImagen){
//        //creamos un numero al azar de 0 a 5 es decir 6 posiciones que concuerdan con las posiciones de los array de imagenes
//        Random numeros = new Random();
//        int posicion = numeros.nextInt(6);
//
//        switch (tipoImagen){
//            case 0:
//                //BuenaVenta
//                return imagenesExcelente[posicion];
//
//            case 1:
//                //Intermedio
//                return imagenesIntermedio[posicion];
//
//            case 2:
//                //Venta Baja
//                return imagenesBajo[posicion];
//
//            case 3:
//                //Mal Trabajo
//                return imagenesMal[posicion];
//
//            default:
//                return imagenesIntermedio[posicion];
//        }
//
//    }




}



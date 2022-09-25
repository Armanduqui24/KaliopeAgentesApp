package mx.greenmouse.kaliope;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class DetallesClientes extends AppCompatActivity {

    DataBaseHelper db = new DataBaseHelper(this);


    //SE LLENA CON EL ESTADO DE VISITA SACADO DE LA BASE DE DATOS CLIENTE,
    //LA HACEMOS GOBAL PARA PODER USARLA (EN EL METODO DE CREACION DEL MENU
    // CONTEXTUAL PARA DE ESTA FORMA PODER MOSTRAR LAS OPCIONES
    // DEPENDIENDO DEL ESTADO DE LA VARIABLE DE VISITA)
    private String estado;
    private String cuentaClienteRecibida ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_clientes);
        getSupportActionBar().hide();


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            cuentaClienteRecibida = bundle.getString("CLIENTES_ADMIN_CUENTA_CLIENTE");

           //COMO LLEGAREMOS DEL MAPA a esta actividad, el mapa nos envia el numero de cuenta haremos una consulta para

        }

        llenarVista();






    }


    //creamos nuestro sostenedor de vistas
    private class holder{
        LinearLayout linearLayoutTarjeton;

        TextView cuenta;
        TextView nombre;

        TextView estado;
        TextView prioridadDeVisita;
        TextView diasEnAtraso;
        TextView aCargo1;
        TextView adeudo1;
        TextView nombreZona;
        TextView vencimiento1;
        TextView mensajeAmostrar;

        TextView diasCredito;
        TextView grado;
        TextView credito;

        TextView latitud;
        TextView longitud;
        TextView telefono;
        TextView coordenadas;


        TextView adeudo;
        TextView aCargo;
        TextView vencimiento;
        TextView historial;
        TextView puntos;
        TextView reporte;
        TextView indicaciones;
        TextView estadoVisita;
        TextView mercanciaAcargo;
        ImageButton imgBtnVerEnMapa;

    }


    //llenamos nuestra vista con los datos de la base de datos
    private void llenarVista (){

        final String cuentaCliente;
        String coordenadas="";
        Cursor res = db.clientes_dameClientePorCuentaCliente(cuentaClienteRecibida);
        res.moveToFirst();


        //Toast.makeText(this,"el ID_CLIENTE es:"+ID_CLIENTE + "conteo res:" + res.getCount(),Toast.LENGTH_SHORT).show();

        holder h = new holder();
        h.linearLayoutTarjeton = (LinearLayout) findViewById(R.id.layoutParent);
        h.cuenta = (TextView) findViewById(R.id.cuentaTVdetallesClientes);
        h.nombre = (TextView) findViewById(R.id.nombreTVdetallesClientes);


        h.estado = (TextView) findViewById(R.id.estadoTVdetallesClientes);
        h.prioridadDeVisita = (TextView) findViewById(R.id.prioridadDeVisitaTVdetallesClientes);
        h.diasEnAtraso = (TextView) findViewById(R.id.diasEnAtrasoTVdetallesClientes);
        h.aCargo1 = (TextView) findViewById(R.id.acargoTVdetallesClientes);
        h.adeudo1 = (TextView) findViewById(R.id.adeudoTVdetallesClientes);
        h.nombreZona = (TextView) findViewById(R.id.nombrezonaTVdetallesClientes);
        h.mensajeAmostrar = (TextView) findViewById(R.id.mensajeAmostrarVisitaTVdc);




        h.diasCredito = (TextView) findViewById(R.id.diasCreditoTVdetallesClientes);
        h.grado = (TextView) findViewById(R.id.gradoTVdetallesClientes);
        h.credito = (TextView) findViewById(R.id.creditoTVdetallesClientes);
        h.telefono = (TextView) findViewById(R.id.telefonoTVdc);
        h.coordenadas = (TextView) findViewById(R.id.coordenadasTVdc);

        h.adeudo = (TextView) findViewById(R.id.adeudoTVdc);
        h.aCargo = (TextView) findViewById(R.id.aCargoTVdc);
        h.vencimiento = (TextView) findViewById(R.id.vencimientoTVdc);
        h.historial = (TextView) findViewById(R.id.historialTVdc);
        h.puntos = (TextView) findViewById(R.id.puntosTVdc);
        h.reporte = (TextView) findViewById(R.id.reporteTVdc);
        h.indicaciones = (TextView) findViewById(R.id.indicacionesTVdc);
        h.estadoVisita = (TextView) findViewById(R.id.estadoVisitaTVdc);
        h.mercanciaAcargo = (TextView) findViewById(R.id.mercanciaTVdc);
        h.imgBtnVerEnMapa = (ImageButton) findViewById(R.id.detallesClientesVerMapaImageButton);

        registerForContextMenu(h.estadoVisita);



        h.cuenta.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE)));
        cuentaCliente = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE));
        h.nombre.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE)));



        //COPIADO DE ADAPTADOR CLIENTES
        String estadoCliente = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ESTADO_CLIENTE));
        h.estado.setText(estadoCliente);
        //pintamos el color de la vista dependiendo del estado del cliente
        if (estadoCliente.equals(Constant.ACTIVO) ){
            h.estado.setBackgroundResource(R.color.colorActivo);
        }

        if (estadoCliente.equals(Constant.LIO) ){
            h.estado.setBackgroundResource(R.color.colorLio);
        }

        if (estadoCliente.equals(Constant.REACTIVAR)){
            h.estado.setBackgroundResource(R.color.colorReactivar);
        }

        if (estadoCliente.equals(Constant.PROSPECTO)){
            h.estado.setBackgroundResource(R.color.colorProspecto);
        }


        //COPIADO DE ADAPTADOR CLIENTES
        int prioridadDeVisita = res.getInt(res.getColumnIndex(DataBaseHelper.CLIENTES_CLI_PRIORIDAD_DE_VISITA));
        h.prioridadDeVisita.setText(String.valueOf(prioridadDeVisita));
        try{


            if (prioridadDeVisita == Clientes.URGENTE){
                h.prioridadDeVisita.setText("URGENTE");
                h.prioridadDeVisita.setBackgroundColor(Color.GREEN);
            }

            if (prioridadDeVisita == Clientes.ALTO){
                h.prioridadDeVisita.setText("ALTO");
                h.prioridadDeVisita.setBackgroundColor(Color.GREEN);
            }

            if (prioridadDeVisita == Clientes.ATRASO_URGENTE){
                h.prioridadDeVisita.setText("ATRASO_URGENTE");
                h.prioridadDeVisita.setBackgroundColor(Color.YELLOW);
            }

            if (prioridadDeVisita == Clientes.LIO_URGENTE){
                h.prioridadDeVisita.setText("LIO_URGENTE");
                h.prioridadDeVisita.setBackgroundColor(Color.RED);
            }

            if (prioridadDeVisita == Clientes.NORMAL){
                h.prioridadDeVisita.setText("NORMAL");
                h.prioridadDeVisita.setBackgroundColor(Color.LTGRAY);
            }

            if (prioridadDeVisita == Clientes.LIO_NORMAL){
                h.prioridadDeVisita.setText("LIO_NORMAL");
                h.prioridadDeVisita.setBackgroundColor(Color.LTGRAY);
            }

            if (prioridadDeVisita == Clientes.ATRASO){
                h.prioridadDeVisita.setText("ATRASO");
                h.prioridadDeVisita.setBackgroundColor(Color.LTGRAY);
            }


            if (prioridadDeVisita == Clientes.BAJO){
                h.prioridadDeVisita.setText("BAJO");
                h.prioridadDeVisita.setBackgroundColor(Color.CYAN
                );
            }
        }catch (Exception e){
            e.printStackTrace();
        }






        //COPIADO DE ADAPTADOR CLIENTES
        int diasAtrasoInt = res.getInt(res.getColumnIndex(DataBaseHelper.CLIENTES_CLI_DIAS_DE_VENCIMIENTO_O_FALTANTES_PARA_CORTE));

        if (diasAtrasoInt<=0){

            if (estadoCliente.equals(Constant.REACTIVAR)){
                //(EN ALGUNOS CASOS CUANDO LA CLIETNA VA POR REACTIVAR MARCABA DIAS DE ATRAZO
                // QUE PODIAN COPNFUCNIR AL AGETNE)
                h.diasEnAtraso.setText("0");

            }else{
                h.diasEnAtraso.setText(String.valueOf(diasAtrasoInt));
                if (diasAtrasoInt < 0)h.diasEnAtraso.setTextColor(Color.RED);
            }


        }else {
            //si salen numeros mayores a 0 significa que aun faltan esos dias para su cierre en este caso
            //lo pondremos en 0 para no mostrarselos al usuario
            h.diasEnAtraso.setText("0");
        }






        h.aCargo1.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE)));
        h.adeudo1.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE)));
        h.nombreZona.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_ZONA)));
        h.mensajeAmostrar.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_CLI_MENSAJE_MOSTRAR_POR_VISITAR)));



        h.telefono.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_TELEFONO)));
        h.diasCredito.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_DIAS_CREDITO)));
        h.grado.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_GRADO_CLIENTE)));
        h.credito.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CREDITO_CLIENTE)));

        coordenadas = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_LATITUD_CLIENTE))+res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_LONGITUD_CLIENTE));
        h.coordenadas.setText(coordenadas);

        h.adeudo.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE)));
        h.aCargo.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE)));
        h.vencimiento.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_VENCIMIENTO)));
        h.historial.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_HISTORIALES)));
        h.puntos.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_PUNTOS_DISPONIBLES)));
        h.reporte.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_REPORTE)));
        h.indicaciones.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_INDICACIONES)));

        estado = res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA));

        h.estadoVisita.setText(estado);
        //PINTAMOS EL CUADRO y el tarjeton DEPENDIENDO DEL ESTADO DE LA VISITA
        if (estado.equals(Constant.ESTADO_VISITAR)){
            h.estadoVisita.setBackgroundResource(R.color.colorVisitar);
            h.linearLayoutTarjeton.setBackgroundResource(R.color.colorVisitar);
        }
        if (estado.equals(Constant.ESTADO_REPASO)){
            h.estadoVisita.setBackgroundResource(R.color.colorRepaso);
            h.linearLayoutTarjeton.setBackgroundResource(R.color.colorRepaso);
        }

        if (estado.equals(Constant.ESTADO_VISITADO)){
            h.estadoVisita.setBackgroundResource(R.color.colorVisitado);
            h.linearLayoutTarjeton.setBackgroundResource(R.color.colorVisitado);
        }
        if (estado.equals(Constant.ESTADO_NO_VISITAR)){
            h.estadoVisita.setBackgroundResource(R.color.colorNoVisitar);
            h.linearLayoutTarjeton.setBackgroundResource(R.color.colorNoVisitar);
        }


        h.mercanciaAcargo.setText(res.getString(res.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_MERCANCIA_ACARGO)));

        h.imgBtnVerEnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentViewOnMap = new Intent(getApplicationContext(), MapsActivity.class);
                intentViewOnMap.putExtra("CLIENTES_ADMIN_CUENTA_CLIENTE",cuentaCliente);
                startActivity(intentViewOnMap);
            }
        });


    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        final int ID_ITEM1 = 1;     // crear movimiento del cliente
        final int ID_ITEM2 = 2;     // Mover a repaso
        final int ID_ITEM3 = 3;     // Mover a vsitar
        final int ID_ITEM4 = 4;     // Mover a No visitar
        final int ID_ITEM5 = 5;     // Ya no podemos realizar mas acciones

        //(LO QUE HAREMOS ES CREAR LOS ITEMS DEL MENU CONTEXTUAL DEPENDIENDO DE EL ESTADO EN EL QUE SE ENCUENTRE

        // EL CLIENTE. ASI, SI EL CLIENTE ESTA POR VISITAR DEBERA MOSTRAR LA OPCION DE CREAR MOVIMIENTO
        // Y MOVER A REPASO Y MOVER A YA NO VISITAR

        // SI EL CLIENTE ES REPASO DEBE MOSTRAR CREAR MOVIMIENTO O YA NO VISITAR

        // SI EL CLIENTE ES VISITADO MOSTRARA UN ITEM CON MENSAJE DE EL CLIENTE YA ESTA VISITADO NO SE PUEDE
        // HACER NADA MAS

        // SI EL CLIENTE ESTA EN YA NO VISITAR LE DAMOS LA OPCION DE CREAR MOVIMIENTO)



        //estas lineas son para inflar un menu desde un recurso xml. ese menu ya esta creado por mi luisda
        //pero como queremos que se muestren diferentes opciones de menu dependiendo del estado de visita del
        //cliente, lo haremos por medio de codigo.
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.opciones_list_clientes,menu);

        //4 parametros DEL METODO ADD (id del grupo asociado a la opcion,
        // un ID unico para cada opcion que es recomendable declararlas como constantes de la clase pero para sencilles las declaro con
        //  un solo numero entero,el orden le la opcion que por ahora no se que hace,
        // texto de la pocion)
        //por ahora pondre como id numeros constantes
        menu.setHeaderTitle("¿Que hacemos con el cliente?");


        if (estado.equals(Constant.ESTADO_VISITAR)){
            menu.add(Menu.NONE,ID_ITEM1,Menu.NONE,"Crear MOVIMIENTO del cliente");
            menu.add(Menu.NONE,ID_ITEM2,Menu.NONE,"Mover a REPASO");
            menu.add(Menu.NONE,ID_ITEM4,Menu.NONE,"Mover a NO VISITAR");
        }


        if (estado.equals(Constant.ESTADO_REPASO)){
            menu.add(Menu.NONE,ID_ITEM1,Menu.NONE,"Crear MOVIMIENTO del cliente");
            menu.add(Menu.NONE,ID_ITEM4,Menu.NONE,"Mover a NO VISITAR");

            //COMENTAR ESTAS LINEAS SON PARA PODER REGRESAR AL CLIENTE A VISITAR PARA HACER
            //PRUEBAS DE FUNCIONAMIENTO CORRECTO, EN LAS DEL USUARIO YA NO DEBEN VENIR
            //menu.add(Menu.NONE,ID_ITEM3,Menu.NONE,"Mover a VISITAR ");
        }


        if (estado.equals(Constant.ESTADO_VISITADO)){
            menu.setHeaderTitle(" El Cliente esta como VISITADO ");
            menu.add(Menu.NONE,ID_ITEM5,Menu.NONE," Ya no podemos realizar mas acciones");

            //COMENTAR ESTAS LINEAS SON PARA PODER REGRESAR AL CLIENTE A VISITAR PARA HACER
            //PRUEBAS DE FUNCIONAMIENTO CORRECTO, EN LAS DEL USUARIO YA NO DEBEN VENIR
            //menu.add(Menu.NONE,ID_ITEM3,Menu.NONE,"Mover a VISITAR ");
        }

        if (estado.equals(Constant.ESTADO_NO_VISITAR)){
            menu.add(Menu.NONE,ID_ITEM1,Menu.NONE,"Crear MOVIMIENTO del cliente");

            //COMENTAR ESTAS LINEAS SON PARA PODER REGRESAR AL CLIENTE A VISITAR PARA HACER
            //PRUEBAS DE FUNCIONAMIENTO CORRECTO, EN LAS DEL USUARIO YA NO DEBEN VENIR
            //menu.add(Menu.NONE,ID_ITEM3,Menu.NONE,"Mover a VISITAR ");
        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ContentValues cv = new ContentValues(1);

        switch (item.getItemId()){

            case 1:
                //se preciono en Crear
                // movimiento del cliente

                confirmarCambioDeEstado(
                        this,
                        "MOVER A VISITADO" ,
                        "Para mover un cliente a visitado debes generar su movimiento ¿Quieres generar el Movimiento?",
                        "Si \n ir a movimientos",
                        "No",
                        1);

                return true;


            case 2:

                //en caso de que se presione
                //mover a repaso
                confirmarCambioDeEstado(
                        this,
                        "MOVER A REPASO",
                        "¿Estas seguro que quieres mover el cliente a REPASO?",
                        "Si \n mover a REPASO",
                        "No",
                        2 );

                return true;


            case 3:
                //en caso que se
                //presione mover a visitar
                confirmarCambioDeEstado(
                        this,
                        "MOVER A VISITAR",
                        "Estas tratando de mover un cliente nuevamente a Visitar ¿quieres continuar?",
                        "Si \n mover a VISITAR",
                        "No",
                        3
                );

                return true;

            case 4:
                //EN CASO QUE SE PRESIONE
                //MOVER A NO VISITAR
                confirmarCambioDeEstado(
                        this,
                        "MOVER A NO VISITAR",
                        "Queremos saber si estas seguro de mover al cliente a NO VISITAR",
                        "Si \n Mover a no visitar",
                        "No",
                        4
                );


                return true;

            case 5:
                //en caso que
                //se precione la opcion
                //no hay mas que hacer
                //pues no hacemos nada xD
                //Toast.makeText(this,"Volviendo", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onContextItemSelected(item);

    }


    private void calcularFlujoDeTrabajoParaVisitar (){



        Cursor datosCliente = db.clientes_dameClientePorCuentaCliente(cuentaClienteRecibida);
        if (datosCliente.getCount()>0) {
            datosCliente.moveToFirst();

            int keyid = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.KEY_ID));
            String cuentaCliente = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE));
            String nombreCliente = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE));
            int adeudoCliente = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ADEUDO_CLIENTE));
            int acargoCliente = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ACARGO_CLIENTE));
            String estadoCliente = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_ESTADO_CLIENTE));

            //si el cliente tiene mercancia acargo tenemos que llamar a generar la devolucion
            if (acargoCliente > 0){

                Intent intent = new Intent(getApplicationContext(),RealizarDevolucion.class);
                intent.putExtra("NUMERO_CUENTA_ENVIADO",cuentaClienteRecibida);
                startActivity(intent);

            }else if (adeudoCliente>0){

                //si el cliente no tiene mercancia acargo pero si tiene adeudo nos vamos directo a pago
                Intent intent = new Intent(getApplicationContext(),RealizarPago.class);
                intent.putExtra("NUMERO_CUENTA_ENVIADO",cuentaClienteRecibida);
                startActivity(intent);

            }else if(estadoCliente.equals(Constant.REACTIVAR)){

                //si el cliente esta marcado como reactivar lo mandamos a entrega mercancia
                Intent intent = new Intent(getApplicationContext(),RealizarEntrega.class);
                intent.putExtra("NUMERO_CUENTA_ENVIADO",cuentaClienteRecibida);
                startActivity(intent);

            }else if(estadoCliente.equals(Constant.PROSPECTO)){

                //si el cliente esta marcado como reactivar lo mandamos a entrega mercancia
                Intent intent = new Intent(getApplicationContext(),RealizarEntrega.class);
                intent.putExtra("NUMERO_CUENTA_ENVIADO",cuentaClienteRecibida);
                startActivity(intent);

            }



            //si el cliente viene por reactivar

        }




    }


    public void confirmarCambioDeEstado(final Activity activity, String titulo, String mensaje , String mensajePositivo, String mensajeNegativo, final int identificador ) {

        //(METODO CREADO PARA CONFIRMAR TODAS LAS ACCIONES QUE EL USUARIO VA A TOCAR EN EL MENU CONTEXTUAL
        // PARA CONFIRMAR SI DESE A HACER ESA ACCION. POR EJEMPLO SI CLICEA MOVER A VISITADO PREGUNTARA
        // Y REALIZARA LA ACCION DETERMINADA DEPENDIENDO DE EL NUMERO IDETIFICADOR QUE LE ENVIEMOS, PORQUE
        // POR EJEMPLO SI LO QUEREMOS MOVER A REPASOS HARA OTRA ACCION DIFERENTE DE MOVER A NO VISITAR.)

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle(titulo);
        builder.setIcon(R.drawable.icono_pregunta);

        builder.setMessage(mensaje)
                .setPositiveButton(mensajePositivo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ContentValues cv = new ContentValues(1);
                        Constant.ULTIMOS_DATOS_SINCRONIZADOS = false;


                        switch (identificador){

                            case 1:

                                //(EN CASO QUE SE PRESIONO MOVER A VISITADO
                                calcularFlujoDeTrabajoParaVisitar();

                                break;
                            case 2:
                                //(en caso que se presiono
                                // mover a repaso en el menu contextual
                                // le ponemos al contentValues el el nombre de la columna
                                // donde se haran los cambos y le enviamos el valor de ESTADO_REPASO
                                // despues la base de datos filtra el regstro por el ID del cliente
                                // y pone los datos del cv
                                // la constante filtro la cambiamos por ESTADO_REPASO
                                // para que al volver a la vista de clientes nos muestre los clientes
                                //repasados.
                                // mostramos un toast para confirmar al usuario
                                // y llamamos a llenar vista para que se actualice la vista de Detalles clientes
                                // y cambie de color el cuadro que indica el estado de la visita)
                                cv.put(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA,Constant.ESTADO_REPASO);
                                db.clientes_actualizaTablaClientesPorNumeroCuenta(cv, cuentaClienteRecibida);
                                Constant.filtro = Constant.ESTADO_REPASO;
                                Toast.makeText(activity,"Movimos el cliente a REPASO", Toast.LENGTH_SHORT).show();
                                llenarVista();
                                startActivity(new Intent(DetallesClientes.this,Clientes.class));
                                break;

                            case 3:
                                //(en caso que se presiono
                                // mover a VISITAR en el menu contextual
                                // le ponemos al contentValues el el nombre de la columna
                                // donde se haran los cambos y le enviamos el valor de ESTADO_VISITAR
                                // despues la base de datos filtra el regstro por el ID del cliente
                                // y pone los datos del cv
                                // la constante filtro la cambiamos por ESTADO_VISITAR
                                // para que al volver a la vista de clientes nos muestre los clientes
                                //visitar.
                                // mostramos un toast para confirmar al usuario
                                // y llamamos a llenar vista para que se actualice la vista de Detalles clientes
                                // y cambie de color el cuadro que indica el estado de la visita)

                                cv.put(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA,Constant.ESTADO_VISITAR);                                                       //Le enviamos al cv la columna que queremos que cambie el valor, y le enviamos el valor a poner
                                db.clientes_actualizaTablaClientesPorNumeroCuenta(cv, cuentaClienteRecibida);
                                Constant.filtro = Constant.ESTADO_VISITAR;
                                Toast.makeText(activity,"Movimos el cliente a VISITAR ", Toast.LENGTH_SHORT).show();
                                llenarVista();
                                startActivity(new Intent(DetallesClientes.this,Clientes.class));
                                break;

                            case 4:
                                //(en caso que se presiono
                                // mover a NO VISITAR en el menu contextual
                                // le ponemos al contentValues el el nombre de la columna
                                // donde se haran los cambos y le enviamos el valor de ESTADO_NO_VISITAR
                                // despues la base de datos filtra el regstro por el ID del cliente
                                // y pone los datos del cv
                                // la constante filtro la cambiamos por ESTADO_NO_VISITAR
                                // para que al volver a la vista de clientes nos muestre los clientes
                                //no visitar.
                                // mostramos un toast para confirmar al usuario
                                // y llamamos a llenar vista para que se actualice la vista de Detalles clientes
                                // y cambie de color el cuadro que indica el estado de la visita)

                                cv.put(DataBaseHelper.CLIENTES_CLI_ESTADO_VISITA,Constant.ESTADO_NO_VISITAR);                                                       //Le enviamos al cv la columna que queremos que cambie el valor, y le enviamos el valor a poner
                                db.clientes_actualizaTablaClientesPorNumeroCuenta(cv, cuentaClienteRecibida);
                                Constant.filtro = Constant.ESTADO_NO_VISITAR;
                                Toast.makeText(activity,"Movimos el cliente a No Visitar", Toast.LENGTH_SHORT).show();
                                llenarVista();
                                startActivity(new Intent(DetallesClientes.this,Clientes.class));
                                break;


                        }


                    }
                })
                .setNegativeButton(mensajeNegativo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.create();
        builder.show();


    }





}//fin de clase

package mx.greenmouse.kaliope;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class VistaMovimientosClientes extends AppCompatActivity {

    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

    TextView
    tvUsuario,
    tvNombreAgente,
    tvRuta,
    tvCobroFinal;

    String usurarioAgenteConsultar = "";
    String nombreCompletoAgente = "";
    String rutaConsultar = "";

    Button botonImprimir;
    ListView listaMovimientos;

    ArrayList<HashMap> listArray;




    final static String VISTA_MOV_ADAPTADOR_ROW_ID = "ROW_ID";
    final static String VISTA_MOV_ADAPTADOR_CUENTA_CLIENTE = "NUMERO_CUENTA";
    final static String VISTA_MOV_ADAPTADOR_NOMBRE = "NOMBRE";
    final static String VISTA_MOV_ADAPTADOR_RUTA= "RUTA";
    final static String VISTA_MOV_ADAPTADOR_PAGO_VENTA = "PAGO_VENTA";
    final static String VISTA_MOV_ADAPTADOR_PAGO_REGALO = "PAGO_REGALO";
    final static String VISTA_MOV_ADAPTADOR_PAGO_CONTADO = "PAGO_CONTADO";
    final static String VISTA_MOV_ADAPTADOR_PAGO_DIF_CREDITO = "DIF_CREDITO";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_movimientos_clientes);
        getSupportActionBar().hide();








        tvUsuario       = (TextView) findViewById(R.id.VistaMovimientosClientesUsuario);
        tvNombreAgente  = (TextView) findViewById(R.id.VistaMovimientosClientesNombreCompleto);
        tvRuta          = (TextView) findViewById(R.id.VistaMovimientosClientesRuta);
        tvCobroFinal    = (TextView) findViewById(R.id.VistaMovimientosClientesCobroFinal);
        botonImprimir   = (Button)   findViewById(R.id.VistaMovimientosClientesBotonImprimir);
        listaMovimientos = (ListView)findViewById(R.id.VistaMovimientosClientesLista);

        usurarioAgenteConsultar = ConfiguracionesApp.getUsuarioIniciado(this);
        nombreCompletoAgente =  ConfiguracionesApp.getNombreEmpleado(this);
        rutaConsultar =   ConfiguracionesApp.getZonaVisitar1(this) +" " + ConfiguracionesApp.getZonaVisitar2(this) ;

        tvUsuario.setText(usurarioAgenteConsultar);
        tvNombreAgente.setText(nombreCompletoAgente);
        tvRuta.setText(rutaConsultar);


        listarEntradas();

        listaMovimientos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap temporal = (HashMap) adapterView.getItemAtPosition(i);
                String cuentaCliente = temporal.get(VISTA_MOV_ADAPTADOR_CUENTA_CLIENTE).toString();
                Intent intent  = new Intent(VistaMovimientosClientes.this, RealizarFinalizarMovimiento.class);
                intent.putExtra("NUMERO_CUENTA_ENVIADO",cuentaCliente);
                startActivity(intent);
            }
        });



    }


    private void listarEntradas(){

        int cobroFinalCalculado = 0;
        listArray = new ArrayList<HashMap>();

        ListViewAdapterVistaMovimientos adapter = new ListViewAdapterVistaMovimientos(this,listArray);
        listaMovimientos.setAdapter(adapter);

        Cursor datosCliente = dataBaseHelper.clientes_consultarClientesPorMovimientoFinalizado(true);
        if(datosCliente.getCount()>0){
            datosCliente.moveToFirst();

            do{
                int rowIdConsultar           = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.KEY_ID));
                String nombreClienteConsultar   = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_CLIENTE));
                String cuentaClienteConsultar   = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_CUENTA_CLIENTE));
                String rutaClienteConsultar     = datosCliente.getString(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ADMIN_NOMBRE_ZONA));
                int pagoVentaConsultar       = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PAG_PAGO_POR_VENTA_CAPTURADO));
                int pago2CapturadoConsultar    = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PA2_PAGO_CAPTURADO));
                int pagoRegaloConsultar      = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PA2_PAGO_DIFERENCIA_REGALO));
                int pagoContadoConsultar     = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_PA2_PAGO_POR_VENTA_CONTADO));
                int pagoDiferenciaCredito     = datosCliente.getInt(datosCliente.getColumnIndex(DataBaseHelper.CLIENTES_ENT_DIFERENCIA_EXCESO_CREDITO));


                int pagosTotalesClienteCalcular = pagoVentaConsultar + pago2CapturadoConsultar;

                cobroFinalCalculado += pagosTotalesClienteCalcular;

                HashMap map = new HashMap();
                map.put(VISTA_MOV_ADAPTADOR_ROW_ID,rowIdConsultar);
                map.put(VISTA_MOV_ADAPTADOR_CUENTA_CLIENTE,cuentaClienteConsultar);
                map.put(VISTA_MOV_ADAPTADOR_NOMBRE,nombreClienteConsultar);
                map.put(VISTA_MOV_ADAPTADOR_RUTA,rutaClienteConsultar);
                map.put(VISTA_MOV_ADAPTADOR_PAGO_VENTA,pagoVentaConsultar);
                map.put(VISTA_MOV_ADAPTADOR_PAGO_REGALO,pagoRegaloConsultar);
                map.put(VISTA_MOV_ADAPTADOR_PAGO_CONTADO,pagoContadoConsultar);
                map.put(VISTA_MOV_ADAPTADOR_PAGO_DIF_CREDITO,pagoDiferenciaCredito);
                listArray.add(map);


            }while(datosCliente.moveToNext());

        }

        String mensaje = "$" + cobroFinalCalculado;
        tvCobroFinal.setText(mensaje);








    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent (VistaMovimientosClientes.this, MenuPrincipalActivity.class));
    }
}

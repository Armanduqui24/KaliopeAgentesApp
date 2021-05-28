package mx.greenmouse.kaliope;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapterVistaMovimientos extends BaseAdapter {



        private ArrayList<HashMap> list;
        Activity activity;

        public ListViewAdapterVistaMovimientos (Activity activity, ArrayList<HashMap> list){

            super();
            this.list = list;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            //inflamos nuestra vista para cada item
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            view = layoutInflater.inflate(R.layout.list_vista_movimientos,null);

            TextView cuenta = (TextView) view.findViewById(R.id.ListVistaMovimientoCuenta);
            TextView nombre = (TextView) view.findViewById(R.id.ListVistaMovimientoNombreCliente);
            TextView ruta = (TextView) view.findViewById(R.id.ListVistaMovimientoRuta);
            TextView pagVenta = (TextView) view.findViewById(R.id.ListVistaMovimientoPagoVenta);
            TextView pagReg = (TextView) view.findViewById(R.id.ListVistaMovimientoPagoRegalo);
            TextView pagCon = (TextView) view.findViewById(R.id.ListVistaMovimientoPagoContado);
            TextView pagCredito = (TextView) view.findViewById(R.id.ListVistaMovimientoPagoCredito);


            HashMap map = list.get(i);


            cuenta.setText(map.get(VistaMovimientosClientes.VISTA_MOV_ADAPTADOR_CUENTA_CLIENTE ).toString());
            nombre.setText(map.get(VistaMovimientosClientes.VISTA_MOV_ADAPTADOR_NOMBRE ).toString());
            ruta.setText(map.get(VistaMovimientosClientes.VISTA_MOV_ADAPTADOR_RUTA).toString());
            pagVenta.setText(map.get(VistaMovimientosClientes.VISTA_MOV_ADAPTADOR_PAGO_VENTA).toString());
            pagReg.setText(map.get(VistaMovimientosClientes.VISTA_MOV_ADAPTADOR_PAGO_REGALO).toString());
            pagCon.setText(map.get(VistaMovimientosClientes.VISTA_MOV_ADAPTADOR_PAGO_CONTADO).toString());
            pagCredito.setText(map.get(VistaMovimientosClientes.VISTA_MOV_ADAPTADOR_PAGO_DIF_CREDITO).toString());






            return view;
        }


}

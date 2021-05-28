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

public class ListViewAdapterEntregaMercancia extends BaseAdapter {


    private ArrayList<HashMap> list;
    Activity activity;

    public ListViewAdapterEntregaMercancia (Activity activity, ArrayList<HashMap> list){

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
        view = layoutInflater.inflate(R.layout.list_entrega_mercancia,null);

        TextView cantidad = (TextView) view.findViewById(R.id.ListEntregaMercanciaPiezas);
        TextView precio = (TextView) view.findViewById(R.id.ListEntregaMercanciaPrecio);
        TextView importeTotalEntrega = (TextView) view.findViewById(R.id.ListEntregaMercanciaDistribucion);
        TextView distribucionUnitario = (TextView) view.findViewById(R.id.ListEntregaMercanciaDistUnitario);
        TextView ganaciaUnitaria = (TextView) view.findViewById(R.id.ListEntregaMercanciaGananciaUnitaria);
        TextView grado = (TextView) view.findViewById(R.id.ListEntregaMercanciaGrado);
        TextView creditoContadoRegalo = (TextView) view.findViewById(R.id.ListEntregaMercanciaCreditoContadoRegalo);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ListEntregaMercanciaLayout);

        HashMap map = list.get(i);






        cantidad.setText(map.get(RealizarEntrega.ADAPTADOR_LISTA_CANTIDAD).toString());
        precio.setText(map.get(RealizarEntrega.ADAPTADOR_LISTA_PRECIO).toString());
        importeTotalEntrega.setText(map.get(RealizarEntrega.ADAPTADOR_LISTA_IMPORTE_TOTAL).toString());
        grado.setText(map.get(RealizarEntrega.ADAPTADOR_LISTA_GRADO).toString());
        distribucionUnitario.setText(map.get(RealizarEntrega.ADAPTADOR_LISTA_DISTRIBUCCION_UNITARIA).toString());
        ganaciaUnitaria.setText(map.get(RealizarEntrega.ADAPTADOR_LISTA_GANACIA_UNITARIA).toString());





        int tipoDeEntrega = Integer.valueOf(map.get(RealizarEntrega.ADAPTADOR_LISTA_CREDITO_CONTADO_REGALO).toString());

        if (tipoDeEntrega == RealizarEntrega.CONTADO){
            creditoContadoRegalo.setBackgroundResource(R.color.colorGreenLight);
            creditoContadoRegalo.setTextColor(creditoContadoRegalo.getResources().getColor(R.color.colorGreen));
            creditoContadoRegalo.setText("contado");
        }

        if (tipoDeEntrega == RealizarEntrega.CREDITO){
            creditoContadoRegalo.setBackgroundResource(R.color.colorBlueLight);
            creditoContadoRegalo.setTextColor(creditoContadoRegalo.getResources().getColor(R.color.colorPrimary));
            creditoContadoRegalo.setText("credito");
        }

        if (tipoDeEntrega == RealizarEntrega.REGALO){
            creditoContadoRegalo.setBackgroundResource(R.color.colorPinkLight);
            creditoContadoRegalo.setTextColor(creditoContadoRegalo.getResources().getColor(R.color.colorAccent));
            creditoContadoRegalo.setText("regalo");
        }



        return view;
    }
}

package mx.greenmouse.kaliope;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static mx.greenmouse.kaliope.Constant.FIRST_COLUMN;
import static mx.greenmouse.kaliope.Constant.FIVE_COLUMN;
import static mx.greenmouse.kaliope.Constant.FOURTH_COLUMN;
import static mx.greenmouse.kaliope.Constant.SECOND_COLUMN;
import static mx.greenmouse.kaliope.Constant.THIRD_COLUMN;


public class ListViewAdapter extends BaseAdapter
{
    public ArrayList<HashMap> list;
    Activity activity;
    boolean mostrarExistencias;

    //AÑADIMOS UN BOLEANO PARA SI ES FALSO PONER DE COLOR TRANSPARENTE EL TEXTO DONDE SE REPRESENTARAN LAS EXISTENCIAS
    //Y SI ES VERDADERO LO PONEMOS DE NEGRO

    public ListViewAdapter(Activity activity, ArrayList<HashMap> list, boolean mostrarExistencias) {
        super();
        this.activity = activity;
        this.list = list;
        this.mostrarExistencias = mostrarExistencias;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        TextView txtVendedora;
        TextView txtSocia;
        TextView txtEmpresaria;
        TextView txtCodigo;
        TextView txtExistencias;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater =  activity.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item_inventario, null);
            holder = new ViewHolder();
            holder.txtVendedora = (TextView) convertView.findViewById(R.id.FirstText);
            holder.txtSocia = (TextView) convertView.findViewById(R.id.SecondText);
            holder.txtEmpresaria = (TextView) convertView.findViewById(R.id.ThirdText);
            holder.txtCodigo = (TextView) convertView.findViewById(R.id.FourthText);
            holder.txtExistencias = (TextView) convertView.findViewById(R.id.FifthText);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap map = list.get(position);
        holder.txtVendedora.setText(map.get(FIRST_COLUMN).toString());
        holder.txtSocia.setText(map.get(SECOND_COLUMN).toString());
        holder.txtEmpresaria.setText(map.get(THIRD_COLUMN).toString());
        holder.txtCodigo.setText(map.get(FOURTH_COLUMN).toString());
        holder.txtExistencias.setText(map.get(FIVE_COLUMN).toString());

        if(mostrarExistencias){
            //si las existencias son diferentes de 0 las pintamos de negro
            //si son 0 las pintamos de gris claro
            if(map.get(FIVE_COLUMN).equals("0")){
                holder.txtExistencias.setTextColor(Color.LTGRAY);
            }else {
                holder.txtExistencias.setTextColor(Color.BLACK);
            }
        }else{
            holder.txtExistencias.setTextColor(Color.TRANSPARENT);
        }



        return convertView;
    }

}

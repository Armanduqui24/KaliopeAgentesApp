package mx.greenmouse.kaliope;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class AdaptadorClientes extends BaseAdapter {
    private ArrayList <HashMap> lista;
    Activity activity;

    public AdaptadorClientes (Activity activity, ArrayList<HashMap> lista){
        super();
        this.lista = lista;
        this.activity = activity;

    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int i) {
        return lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder{
        TextView cuenta;
        TextView nombre;
        TextView estadoCliente;
        TextView adeudo;
        TextView vencimiento;
        TextView estadoVisita;
        TextView nombreZona;
        TextView prioridadDeVisita;
        TextView diasEnAtraso;
        TextView acargo;
        TextView mensajeAmostrarVisita;

        LinearLayout layoutFondo;
        LinearLayout layoutEstado;
        LinearLayout layoutEstado2;

    }

    @Override
    public View getView(int posicion, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.activity_clientes_list,null);
        holder = new ViewHolder();
        holder.cuenta = (TextView) view.findViewById(R.id.cuentaTV);
        holder.nombre = (TextView) view.findViewById(R.id.nombreTV);
        holder.estadoCliente = (TextView) view.findViewById(R.id.estadoClienteTV);
        holder.adeudo = (TextView) view.findViewById(R.id.adeudoTV);
        holder.vencimiento = (TextView) view.findViewById(R.id.vencimientoTV);
        holder.estadoVisita = (TextView) view.findViewById(R.id.estadoVisitaTVcolor);
        holder.nombreZona = (TextView) view.findViewById(R.id.nombrezonaTV);
        holder.prioridadDeVisita = (TextView) view.findViewById(R.id.prioridadDeVisitaTV);
        holder.diasEnAtraso = (TextView) view.findViewById(R.id.diasEnAtrasoTV);
        holder.acargo = (TextView) view.findViewById(R.id.acargoTV);
        holder.mensajeAmostrarVisita = (TextView) view.findViewById(R.id.mensajeAmostrarVisitaTV);


        holder.layoutFondo = (LinearLayout) view.findViewById(R.id.layoutParent);
        holder.layoutEstado = (LinearLayout) view.findViewById(R.id.layoutEstado);
        holder.layoutEstado2 = (LinearLayout) view.findViewById(R.id.layoutEstado2);

        HashMap map = lista.get(posicion);
        holder.cuenta.setText (map.get(Constant.FIRST_COLUMN).toString());
        holder.nombre.setText(map.get(Constant.SECOND_COLUMN).toString());
        holder.nombreZona.setText(map.get(Constant.THIRTY_SECOND_COLUMN).toString());

        holder.mensajeAmostrarVisita.setText(map.get(Constant.THIRTY_THIRD_COLUMN).toString());





        try{
            int prioridadDeVisita = Integer.valueOf(map.get(Constant.THIRTY_ONE_COLUMN).toString());


            if (prioridadDeVisita == Clientes.URGENTE){
                holder.prioridadDeVisita.setText("URGENTE");
                holder.prioridadDeVisita.setBackgroundColor(Color.GREEN);
            }

            if (prioridadDeVisita == Clientes.ALTO){
                holder.prioridadDeVisita.setText("ALTO");
                holder.prioridadDeVisita.setBackgroundColor(Color.GREEN);
            }

            if (prioridadDeVisita == Clientes.ATRASO_URGENTE){
                holder.prioridadDeVisita.setText("ATRASO_URGENTE");
                holder.prioridadDeVisita.setBackgroundColor(Color.YELLOW);
            }

            if (prioridadDeVisita == Clientes.LIO_URGENTE){
                holder.prioridadDeVisita.setText("LIO_URGENTE");
                holder.prioridadDeVisita.setBackgroundColor(Color.RED);
            }

            if (prioridadDeVisita == Clientes.NORMAL){
                holder.prioridadDeVisita.setText("NORMAL");
                holder.prioridadDeVisita.setBackgroundColor(Color.LTGRAY);
            }

            if (prioridadDeVisita == Clientes.LIO_NORMAL){
                holder.prioridadDeVisita.setText("LIO_NORMAL");
                holder.prioridadDeVisita.setBackgroundColor(Color.LTGRAY);
            }

            if (prioridadDeVisita == Clientes.ATRASO){
                holder.prioridadDeVisita.setText("ATRASO");
                holder.prioridadDeVisita.setBackgroundColor(Color.LTGRAY);
            }


            if (prioridadDeVisita == Clientes.BAJO){
                holder.prioridadDeVisita.setText("BAJO");
                holder.prioridadDeVisita.setBackgroundColor(Color.CYAN
                );
            }
        }catch (Exception e){
            e.printStackTrace();
        }






        String estadoCliente =  map.get(Constant.EIGHT_COLUMN).toString();
        holder.estadoCliente.setText(estadoCliente);
        //pintamos el color de la vista dependiendo del estado del cliente
        if (estadoCliente.equals(Constant.ACTIVO) ){
            holder.estadoCliente.setBackgroundResource(R.color.colorActivo);
        }

        if (estadoCliente.equals(Constant.LIO) ){
            holder.estadoCliente.setBackgroundResource(R.color.colorLio);
        }

        if (estadoCliente.equals(Constant.REACTIVAR)){
            holder.estadoCliente.setBackgroundResource(R.color.colorReactivar);
        }

        if (estadoCliente.equals(Constant.PROSPECTO)){
            holder.estadoCliente.setBackgroundResource(R.color.colorProspecto);
        }


        String diasDeAtraso = map.get(Constant.TWENTY_NINE_COLUMN).toString();
        int diasAtrasoInt = Integer.valueOf(diasDeAtraso);
        Log.d("AdaptadorClientes" , map.get(Constant.SECOND_COLUMN).toString());
        Log.d("AdaptadorClientes" , diasDeAtraso);
        Log.d("AdaptadorClientes" , String.valueOf(diasAtrasoInt));

        if (diasAtrasoInt<=0){

            if (estadoCliente.equals(Constant.REACTIVAR)){
                //(EN ALGUNOS CASOS CUANDO LA CLIETNA VA POR REACTIVAR MARCABA DIAS DE ATRAZO
                // QUE PODIAN COPNFUCNIR AL AGETNE)
                holder.diasEnAtraso.setText("0");

            }else{
                holder.diasEnAtraso.setText(String.valueOf(diasAtrasoInt));
                if (diasAtrasoInt < 0)holder.diasEnAtraso.setTextColor(Color.RED);
            }


        }else {
            //si salen numeros mayores a 0 significa que aun faltan esos dias para su cierre en este caso
            //lo pondremos en 0 para no mostrarselos al usuario
            holder.diasEnAtraso.setText("0");
        }



        String acargo = map.get(Constant.THIRTY_COLUMN).toString();
        holder.acargo.setText(acargo);
        String adeudo = map.get(Constant.ELEVEN_COLUMN).toString();
        holder.adeudo.setText (adeudo);



        if (acargo.equals("0")){
            holder.acargo.setTextColor(Color.BLACK);
        }else{
            if(diasAtrasoInt<0)holder.acargo.setTextColor(Color.RED);
        }

        if (adeudo.equals("0")){
            holder.adeudo.setTextColor(Color.BLACK);
        }else{
            if(diasAtrasoInt<0)holder.adeudo.setTextColor(Color.RED);
        }

        holder.vencimiento.setText(map.get(Constant.THIRTEEN_COLUMN).toString());





        //pintamos el color dependiendo de si lo visito o se va a repaso
        String estadoVisita = map.get(Constant.EIGHTEEN_COLUMN).toString();



        if (estadoVisita.equals(Constant.ESTADO_VISITAR) ){
            holder.estadoVisita.setText("VISITAR");//ponemos el titulo en la etiquetita
            holder.layoutEstado.setBackgroundResource(R.color.colorEtiquetaVisitar);
            holder.layoutEstado2.setBackgroundResource(R.color.colorEtiquetaVisitar);
            holder.layoutFondo.setBackgroundResource(R.color.colorVisitar);
        }


        if (estadoVisita.equals(Constant.ESTADO_VISITADO) ){
            holder.estadoVisita.setText("VISITADO");
            holder.layoutEstado.setBackgroundResource(R.color.colorEtiquetaVisitado);
            holder.layoutEstado2.setBackgroundResource(R.color.colorEtiquetaVisitado);
            holder.layoutFondo.setBackgroundResource(R.color.colorVisitado);
        }
        if (estadoVisita.equals(Constant.ESTADO_REPASO) ){
            holder.estadoVisita.setText("REPASAR");
            holder.layoutEstado.setBackgroundResource(R.color.colorEtiquetaRepaso);
            holder.layoutEstado2.setBackgroundResource(R.color.colorEtiquetaRepaso);
            holder.layoutFondo.setBackgroundResource(R.color.colorRepaso);
        }
        if (estadoVisita.equals(Constant.ESTADO_NO_VISITAR)){
            holder.estadoVisita.setText("NO VISITAR");
            holder.layoutEstado.setBackgroundResource(R.color.colorEtiquetaNoVisitar);
            holder.layoutEstado2.setBackgroundResource(R.color.colorEtiquetaNoVisitar);
            holder.layoutFondo.setBackgroundResource(R.color.colorNoVisitar);
        }

        /*if (utilidadesApp.evaluarFechaVencimiento(map.get(Constant.THIRTEEN_COLUMN).toString())){
            holder.estadoVisita.setText("VISITAR");
        }else{
            holder.estadoVisita.setText("MES");
        }*/


        return view;
    }


}

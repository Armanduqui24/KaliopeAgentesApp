package mx.greenmouse.kaliope;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapterDevolucionMercancia extends BaseAdapter{

    private ArrayList <HashMap> list;
    Activity activity;



    //constructor para inicializar las variables con los datos pasados al cosntructor

    public ListViewAdapterDevolucionMercancia(Activity activity, ArrayList<HashMap> list){
        super();
        this.list = list;
        this.activity = activity;
    }


    @Override
    public int getCount() {
        return list.size();

        //metodo lo llama para conocer el tamaño de los elementos de list que va a dibujar, si el
        //array tiene 10 elementos de hasmap dentro dibujara 10 listitas
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
        //lo llama para entregarle un elemento de la lista
    }

    @Override
    public long getItemId(int i) {
        return i;
        //lo llama para concer el id de determinada lista
    }



    private class ViewHolder{
        TextView piezas, precio,precioDistribucion;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        //inflamos nuestra vista para cada item
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        view = layoutInflater.inflate(R.layout.list_devuelve_mercancia,null);

        ViewHolder holder = new ViewHolder();

        holder.piezas = (TextView) view.findViewById(R.id.ListAdapterDevuelveMercanciaCantidad);
        holder.precio = (TextView) view.findViewById(R.id.ListAtapterDevuelveMercanciaPrecio);
        holder.precioDistribucion = (TextView) view.findViewById(R.id.ListAtapterDevuelveMercanciaDistribucion);

        //como el arrayList pasado contiene en cada indice un hasmap aqui guardaremos el hashmap del indice en curso
        HashMap map = list.get(i);




        holder.piezas.setText(map.get("CANTIDAD").toString());//obtenemos el valor que esta guardado con el nombre de clave
        holder.precio.setText(map.get("PRECIO").toString());
        holder.precioDistribucion.setText(map.get("IMPORTE").toString());



        return view; //retornamos la vista inflada
    }
}

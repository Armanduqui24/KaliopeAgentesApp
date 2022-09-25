package mx.greenmouse.kaliope;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentParametrosClientes.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FragmentParametrosClientes extends android.app.Fragment {


    Activity activity = getActivity(); //declaramos una variable de tipo activity, esta tendra la referencia a la actividad de donde se esta mostrando el fragment

    private OnFragmentInteractionListener mListener;
    private Button botonAceptar;

    private String recibeDeActivity;

    private OnVariableCambiada mCallback;
    public FragmentParametrosClientes() {
        // Required empty public constructor


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_parametros_clientes, container, false);

        botonAceptar = (Button) view.findViewById(R.id.buttonAceptarParametrosClientes);

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // activity = getActivity(); //le pedimos que tome la actividad en donde esta en ese momento
                Toast.makeText(activity, recibeDeActivity, Toast.LENGTH_SHORT).show();
                mCallback.onVariableCambiada("en el activity");

                //https://www.flipandroid.com/eliminar-un-fragmento-especfico-de-la-backstack-de-android.html
                FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
                fragmentTransaction1.remove(FragmentParametrosClientes.this).commit();

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }



        // De esta forma te aseguras que la activity tenga la interfaz
        // Sino saltará una excepción
        //https://es.stackoverflow.com/questions/43985/c%C3%B3mo-comunicar-una-actividad-con-un-fragmento
        //https://developer.android.com/training/basics/fragments/communicating.html#Implement
        try {
            mCallback = (OnVariableCambiada) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " debes implementar OnVarChangedFromFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mCallback = null;

    }


    @Override
    public void onPause() {
        super.onPause();
        //https://www.flipandroid.com/eliminar-un-fragmento-especfico-de-la-backstack-de-android.html
        FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
        fragmentTransaction1.remove(FragmentParametrosClientes.this).commit();

    }


    public void reciboDeActivity (String info){
        recibeDeActivity = info;

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);



    }


    public interface OnVariableCambiada{
        public void onVariableCambiada(String palabra);
    }



}

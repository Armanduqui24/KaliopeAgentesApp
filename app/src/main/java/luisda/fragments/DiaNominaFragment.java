package luisda.fragments;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import mx.greenmouse.kaliope.Constant;
import mx.greenmouse.kaliope.DeviceList;
import mx.greenmouse.kaliope.PrinterCommands;
import mx.greenmouse.kaliope.R;
import mx.greenmouse.kaliope.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiaNominaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DiaNominaFragment extends android.app.Fragment {

    private OnFragmentInteractionListener mListener;

    public static int contadorDeInstancias;



    private static BluetoothSocket btsocket_bit;
    private static OutputStream btoutputstream_bit;



    private String
            titulo,
            rutaVisitada,
            fecha,
            lios,
            nuevas,
            cobroTotal,
            cambioCombustible,
            combustible,
            adelantos,
            gastoExtra1,
            conceptoGasto1,
            gastoExtra2,
            conceptoGasto2,
            gastoExtra3,
            conceptoGasto3,
            gastoExtra4,
            conceptoGasto4,
            cobroPorEntregar;



    EditText etNumero;

    TextView
            tvTitulo,
            tvRutaVisitada,
            tvFecha,
            tvLios,
            tvNuevas,
            tvCobroTotal,
            tvCambioCombustible,
            tvCombustible,
            tvAdelantos,
            tvGastoExtra1,
            tvConceptoGasto1,
            tvGastoExtra2,
            tvConceptoGasto2,
            tvGastoExtra3,
            tvConceptoGasto3,
            tvGastoExtra4,
            tvConceptoGasto4,
            tvCobroPorEntregar;

    Button buttonImprimir;

    public DiaNominaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dia_nomina, container, false);

        tvTitulo = (TextView) view.findViewById(R.id.FragmentDiaNominaTitulo);
        tvRutaVisitada = (TextView) view.findViewById(R.id.FragmentDiaNominaRuta);
        tvFecha = (TextView) view.findViewById(R.id.FragmentDiaNominaFecha);
        tvLios = (TextView) view.findViewById(R.id.FragmentDiaNominaLios);
        tvNuevas = (TextView) view.findViewById(R.id.FragmentDiaNominaNuevas);
        tvCobroTotal = (TextView) view.findViewById(R.id.FragmentDiaNominaCobroTotal);
        tvCambioCombustible = (TextView) view.findViewById(R.id.FragmentDiaNominaSobranteCombustible);
        tvCombustible = (TextView) view.findViewById(R.id.FragmentDiaNominaCombustibleDiaSiguiente);
        tvAdelantos = (TextView) view.findViewById(R.id.FragmentDiaNominaAdelantos);
        tvGastoExtra1 = (TextView) view.findViewById(R.id.FragmentDiaNominaGastoExtra1Importe);
        tvConceptoGasto1 = (TextView) view.findViewById(R.id.FragmentDiaNominaGastoExtra1Concepto);
        tvGastoExtra2      = (TextView) view.findViewById(R.id.FragmentDiaNominaGastoExtra2Importe) ;
        tvConceptoGasto2   = (TextView) view.findViewById(R.id.FragmentDiaNominaGastoExtra2Concepto);
        tvGastoExtra3      = (TextView) view.findViewById(R.id.FragmentDiaNominaGastoExtra3Importe) ;
        tvConceptoGasto3   = (TextView) view.findViewById(R.id.FragmentDiaNominaGastoExtra3Concepto);
        tvGastoExtra4      = (TextView) view.findViewById(R.id.FragmentDiaNominaGastoExtra4Importe) ;
        tvConceptoGasto4   = (TextView) view.findViewById(R.id.FragmentDiaNominaGastoExtra4Concepto);

        tvCobroPorEntregar = (TextView) view.findViewById(R.id.FragmentDiaNominaLibrePorEntregar);
        etNumero = (EditText) view.findViewById(R.id.numeroFragmentDiaNominaET);
        buttonImprimir = (Button) view.findViewById(R.id.FragmentDiaNominaBotonImprimir);


        tvTitulo.setText(titulo);
        tvRutaVisitada.setText(rutaVisitada);
        tvFecha.setText(fecha);
        tvLios.setText(lios);
        tvNuevas.setText(nuevas);
        tvCobroTotal.setText(cobroTotal);
        tvCombustible.setText(combustible);
        tvCambioCombustible.setText(cambioCombustible);
        tvAdelantos.setText(adelantos);
        tvGastoExtra1.setText(gastoExtra1);
        tvConceptoGasto1.setText(conceptoGasto1);
        tvGastoExtra2.setText(gastoExtra2);
        tvConceptoGasto2.setText(conceptoGasto2);
        tvGastoExtra3.setText(gastoExtra3);
        tvConceptoGasto3.setText(conceptoGasto3);
        tvGastoExtra4.setText(gastoExtra4);
        tvConceptoGasto4.setText(conceptoGasto4);
        tvCobroPorEntregar.setText(cobroPorEntregar);

        //Toast.makeText(getActivity(),  String.valueOf(contadorDeInstancias), Toast.LENGTH_SHORT).show();


        buttonImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "BotonImprimir", Toast.LENGTH_SHORT).show();
                connect();
            }
        });


        etNumero.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                    //respondemos al evento de la tecla enter, para el escaner de codigos de barras
                    //este era originalmente el fragmento encontrado en internet lo resumi a solo keycode_enter
                    //el problema con el resumido es que de alguna manera se llamaba 2 veces al evento porque tambien marcaba ActionDown entonces
                    //ejecutaba 2 veces el mismo metod y generaba errores, tube escribirlo de la manera original
                    //if (i == KeyEvent.KEYCODE_ENTER){
                    String codigo = etNumero.getText().toString();
                    etNumero.setText("");//limpiamos el editText
                    tvTitulo.setText(codigo);
                    return true;
                }
                return false;
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




        contadorDeInstancias ++;
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        contadorDeInstancias --;

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


//trate de pegar el orde y con el nombre de la tabla que nos enviaron
    public void recibirParametros (String title,
                                   String zona_,
                                   String fecha_,
                                   String cobro_,
                                   String nuevas_,
                                   String lios_,
                                   String sobrante_,
                                   String adelantos_,
                                   String gastos__1,
                                   String concepto__1,
                                   String gastos__2,
                                   String concepto__2,
                                   String gastos__3,
                                   String concepto__3,
                                   String gastos__4,
                                   String concepto__4,
                                   String gasolina_,
                                   String total_){

        titulo = title;
        rutaVisitada = zona_;
        fecha = fecha_;
        cobroTotal = cobro_;
        nuevas = nuevas_;
        lios = lios_;
        cambioCombustible = sobrante_;
        adelantos = adelantos_;
        gastoExtra1 = gastos__1;
        conceptoGasto1 = concepto__1;
        gastoExtra2     = gastos__2;
        conceptoGasto2  = concepto__2;
        gastoExtra3     = gastos__3;
        conceptoGasto3  = concepto__3;
        gastoExtra4     = gastos__4;
        conceptoGasto4  = concepto__4;
        combustible = gasolina_;
        cobroPorEntregar = total_;


    }















    /** Metodos para imprimir*/


    protected void connect() {


        if(btsocket_bit == null){
            Intent BTIntent = new Intent(getActivity(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        }
        else{
            OutputStream opstream = null;
            try {
                opstream = btsocket_bit.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btoutputstream_bit = opstream;

            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                btoutputstream_bit = btsocket_bit.getOutputStream();


                resetPrint();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(Constant.INTANCE_PRINT_COMPANY);
                printNewLine();

                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                printPhoto();

                printNewLine();
                //btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printTitle(titulo + "\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printNewLine();
                printTitle( rutaVisitada + "\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printTitle( fecha + "\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);



                printText("Nuevas Ingresadas:   " + nuevas + "\n");
                printText("Lios Cobrados:      " + lios + "\n");
                printText("Cobro Total:     $" + cobroTotal + "\n");
                printText("Cambio Combustible: +" + cambioCombustible + "\n");
                printText("Adelantos:         -" + adelantos + "\n");
                printText("Gasto extra1:       -" + gastoExtra1 + "\n");
                printText("Concepto1:" + conceptoGasto1 + "\n");
                printText("Gasto extra2:       -" + gastoExtra2 + "\n");
                printText("Concepto2:" + conceptoGasto2 + "\n");
                printText("Gasto extra3:       -" + gastoExtra3 + "\n");
                printText("Concepto3:" + conceptoGasto3 + "\n");
                printText("Gasto extra4:       -" + gastoExtra4 + "\n");
                printText("Concepto4:" + conceptoGasto4 + "\n");

                btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                printText("Combustible Dia Siguiente:- " + "\n");
                printTitle(combustible + "\n");

                printNewLine();
                printNewLine();
                printNewLine();
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
                printText("Por Entregar:" + "\n");
                printTitle("$" + cobroPorEntregar);
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);

                //printUnicode();
                printNewLine();
                printNewLine();
                btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                resetPrint();
                printNewLine();
                printNewLine();
                printNewLine();


                resetPrint();

                printUnicode();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText("Firma del Agente");

                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();


                btoutputstream_bit.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //print Title
    private void printTitle(String msg) {
        try {
            //Print config
            byte[] bb = new byte[]{0x1B,0x21,0x08};
            byte[] bb2 = new byte[]{0x1B,0x21,0x20};
            byte[] bb3 = new byte[]{0x1B,0x21,0x10};
            byte[] cc = new byte[]{0x1B,0x21,0x00};

            btoutputstream_bit.write(bb3);

            //set text into center
            btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
            btoutputstream_bit.write(msg.getBytes());
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print photo
    public void printPhoto() {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.logokaliopeticketjustificacionizq);

            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                printText(command);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    //print unicode
    public void printUnicode(){
        try {
            btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //print new line
    private void printNewLine() {
        try {
            btoutputstream_bit.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void resetPrint() {
        try{
            btoutputstream_bit.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            btoutputstream_bit.write(PrinterCommands.FS_FONT_ALIGN);
            btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_LEFT);
            btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
            btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //print text
    private void printText(String msg) {
        try {
            // Print normal text
            btoutputstream_bit.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            btoutputstream_bit.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket_bit = DeviceList.getSocket();
            if(btsocket_bit != null){
                /*printText(message.getText().toString());*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        contadorDeInstancias = 0;
        try {
            if(btsocket_bit!= null){
                btoutputstream_bit.close();
                btsocket_bit.close();
                btsocket_bit = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}

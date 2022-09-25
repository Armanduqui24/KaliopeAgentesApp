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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
 * {@link FinalNominaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FinalNominaFragment extends android.app.Fragment {

    private OnFragmentInteractionListener mListener;


    private static BluetoothSocket btsocket_bit;
    private static OutputStream btoutputstream_bit;


    String
    title,
    fechaInicial,
    fechaFinal,
    totalVentas,
    totalNuevas,
    totalLios,
    sueldoBase,
    comision,
    bonoLios,
    bonoNuevas,
            bono1Importe,
    bono1Concepto,
            bono2Importe,
    bono2Concepto,
            bono3Importe,
    bono3Concepto,
            bono4Importe,
    bono4Concepto,
            nominaTotalSinDescuentos,
    descuentoCajaAhorro,
    descuentoAdelantos,
            descuento1Importe,
    descuento1Restante,
    descuento1Concepto,
            descuento2Importe,
    descuento2Restante,
    descuento2Concepto,
            descuento3Importe,
    descuento3Restante,
    descuento3Concepto,
            descuento4Importe,
    descuento4Restante,
    descuento4Concepto,
            descuento5Importe,
    descuento5Restante,
    descuento5Concepto,
            descuento6Importe,
    descuento6Restante,
    descuento6Concepto,
    porCobrar,
    anteriorCajaAhorro,
    aumentoCajaAhorro,
    retiroCajaAhorro,
    conceptoRetiroCajaAhorro,
    disponibleCajaAhorro;




    TextView
    textViewTitle,
    textViewFechaInicial,
    textViewFechaFinal,
    textViewTotalVentas,
    textViewTotalNuevas,
    textViewTotalLios,
    textViewSueldoBase,
    textViewBonoLios,
    textViewBonoNuevas,
    textViewComision,
    textViewBono1,
    textViewBono1Concepto,
    textViewBono2,
    textViewBono2Concepto,
    textViewBono3,
    textViewBono3Concepto,
    textViewBono4,
    textViewBono4Concepto,
    textViewTotal,
    textViewDescuentoCajaAhorro,
    textViewDescuentoAdelantos,
    textViewDescuento1,
    textViewDescuento1Restante,
    textViewDescuento1Concepto,
    textViewDescuento2,
    textViewDescuento2Restante,
    textViewDescuento2Concepto,
    textViewDescuento3,
    textViewDescuento3Restante,
    textViewDescuento3Concepto,
    textViewDescuento4,
    textViewDescuento4Restante,
    textViewDescuento4Concepto,
    textViewDescuento5,
    textViewDescuento5Restante,
    textViewDescuento5Concepto,
    textViewDescuento6,
    textViewDescuento6Restante,
    textViewDescuento6Concepto,
    textViewPorCobrar,
    textViewAnteriorCajaAhorro,
    textViewAumentoCajaAhorro,
    textViewRetiroCajaAhorro,
    textViewConceptoRetiroCajaAhorro,
    textViewDisponibleCajaAhorro;


    Button buttonFirmarNomina;

    public FinalNominaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_final_nomina, container, false);

        textViewTitle = (TextView) view.findViewById(R.id.FragmentFinalNominaTitulo);
        textViewFechaInicial = (TextView) view.findViewById(R.id.FragmentFinalNominaFechaInicial);
        textViewFechaFinal = (TextView) view.findViewById(R.id.FragmentFinalNominaFechaFinal);
        textViewTotalVentas = (TextView) view.findViewById(R.id.FragmentFinalNominaVentasTotales);
        textViewTotalNuevas = (TextView) view.findViewById(R.id.FragmentFinalNominaNuevasTotales);
        textViewTotalLios = (TextView) view.findViewById(R.id.FragmentFinalNominaLiosTotales);
        textViewSueldoBase = (TextView) view.findViewById(R.id.FragmentFinalNominaSueldoBase);
        textViewBonoLios = (TextView) view.findViewById(R.id.FragmentFinalNominaLios);
        textViewBonoNuevas = (TextView) view.findViewById(R.id.FragmentFinalNominaNuevas);
        textViewComision = (TextView) view.findViewById(R.id.FragmentFinalNominaComision);
        textViewBono1 = (TextView) view.findViewById(R.id.FragmentFinalNominaBono1Importe);
        textViewBono1Concepto = (TextView) view.findViewById(R.id.FragmentFinalNominaBono1Concepto);
        textViewBono2          = (TextView) view.findViewById(R.id.FragmentFinalNominaBono2Importe);
        textViewBono2Concepto  = (TextView) view.findViewById(R.id.FragmentFinalNominaBono2Concepto);
        textViewBono3          = (TextView) view.findViewById(R.id.FragmentFinalNominaBono3Importe);
        textViewBono3Concepto  = (TextView) view.findViewById(R.id.FragmentFinalNominaBono3Concepto);
        textViewBono4          = (TextView) view.findViewById(R.id.FragmentFinalNominaBono4Importe);
        textViewBono4Concepto  = (TextView) view.findViewById(R.id.FragmentFinalNominaBono4Concepto);
        textViewTotal = (TextView) view.findViewById(R.id.FragmentFinalNominaTotalNomina);
        textViewDescuentoCajaAhorro = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuentoCajaAhorro);
        textViewDescuentoAdelantos = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuentoAdelantos);
        textViewDescuento1 = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento1Importe);
        textViewDescuento1Restante = (TextView) view.findViewById(R.id.FragmentFinalNominaConcepto1Restante);
        textViewDescuento1Concepto = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento1Concepto);
        textViewDescuento2 = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento2Importe);
        textViewDescuento2Restante = (TextView) view.findViewById(R.id.FragmentFinalNominaConcepto2Restante);
        textViewDescuento2Concepto = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento2Concepto);
        textViewDescuento3 = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento3Importe);
        textViewDescuento3Restante = (TextView) view.findViewById(R.id.FragmentFinalNominaConcepto3Restante);
        textViewDescuento3Concepto = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento3Concepto);
        textViewDescuento4 = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento4Importe);
        textViewDescuento4Restante = (TextView) view.findViewById(R.id.FragmentFinalNominaConcepto4Restante);
        textViewDescuento4Concepto = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento4Concepto);
        textViewDescuento5 = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento5Importe);
        textViewDescuento5Restante = (TextView) view.findViewById(R.id.FragmentFinalNominaConcepto5Restante);
        textViewDescuento5Concepto = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento5Concepto);
        textViewDescuento6 = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento6Importe);
        textViewDescuento6Restante = (TextView) view.findViewById(R.id.FragmentFinalNominaConcepto6Restante);
        textViewDescuento6Concepto = (TextView) view.findViewById(R.id.FragmentFinalNominaDescuento6Concepto);
        textViewPorCobrar = (TextView) view.findViewById(R.id.FragmentFinalNominaPorCobrarLibre);
        textViewAnteriorCajaAhorro = (TextView) view.findViewById(R.id.FragmentFinalNominaCajaAhorroDisponible);
        textViewAumentoCajaAhorro = (TextView) view.findViewById(R.id.FragmentFinalNominaCajaAhorroAumento);
        textViewRetiroCajaAhorro = (TextView) view.findViewById(R.id.FragmentFinalNominaCajaAhorroRetiro);
        textViewConceptoRetiroCajaAhorro = (TextView) view.findViewById(R.id.FragmentFinalNominaCajaAhorroRetiroConcepto);
        textViewDisponibleCajaAhorro = (TextView) view.findViewById(R.id.FragmentFinalNominaCajaAhorroNuevoDisponible);



        textViewTitle.setText(title);
        textViewFechaInicial.setText(fechaInicial);
        textViewFechaFinal.setText(fechaFinal);
        textViewTotalVentas.setText(totalVentas);
        textViewTotalNuevas.setText(totalNuevas);
        textViewTotalLios.setText(totalLios);
        textViewSueldoBase.setText(sueldoBase);
        textViewBonoLios.setText(bonoLios);
        textViewBonoNuevas.setText(bonoNuevas);
        textViewComision.setText(comision);
        textViewBono1.setText(bono1Importe);
        textViewBono1Concepto.setText(bono1Concepto);
        textViewBono2.setText(bono2Importe);
        textViewBono2Concepto.setText(bono2Concepto);
        textViewBono3.setText(bono3Importe);
        textViewBono3Concepto.setText(bono3Concepto);
        textViewBono4.setText(bono4Importe);
        textViewBono4Concepto.setText(bono4Concepto);
        textViewTotal.setText(nominaTotalSinDescuentos);
        textViewDescuentoCajaAhorro.setText(descuentoCajaAhorro);
        textViewDescuentoAdelantos.setText(descuentoAdelantos);
        textViewDescuento1.setText(descuento1Importe);
        textViewDescuento1Restante.setText(descuento1Restante);
        textViewDescuento1Concepto.setText(descuento1Concepto);
        textViewDescuento2.setText(descuento2Importe);
        textViewDescuento2Restante.setText(descuento2Restante);
        textViewDescuento2Concepto.setText(descuento2Concepto);
        textViewDescuento3.setText(descuento3Importe);
        textViewDescuento3Restante.setText(descuento3Restante);
        textViewDescuento3Concepto.setText(descuento3Concepto);
        textViewDescuento4.setText(descuento4Importe);
        textViewDescuento4Restante.setText(descuento4Restante);
        textViewDescuento4Concepto.setText(descuento4Concepto);
        textViewDescuento5.setText(descuento5Importe);
        textViewDescuento5Restante.setText(descuento5Restante);
        textViewDescuento5Concepto.setText(descuento5Concepto);
        textViewDescuento6.setText(descuento6Importe);
        textViewDescuento6Restante.setText(descuento6Restante);
        textViewDescuento6Concepto.setText(descuento6Concepto);
        textViewPorCobrar.setText(porCobrar);
        textViewAnteriorCajaAhorro.setText(anteriorCajaAhorro);
        textViewAumentoCajaAhorro.setText(aumentoCajaAhorro);
        textViewRetiroCajaAhorro.setText(retiroCajaAhorro);
        textViewConceptoRetiroCajaAhorro.setText(conceptoRetiroCajaAhorro);
        textViewDisponibleCajaAhorro.setText(disponibleCajaAhorro);





        buttonFirmarNomina = (Button) view.findViewById(R.id.finalNominaFirmarB);


        buttonFirmarNomina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Boton Firmar", Toast.LENGTH_SHORT).show();
                connect();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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


//trate de pegar el orde y con el nombre de la tabla que nos enviaron , recibimos todos los campos y aqui decidimos cual usar y cual no
    public void recibirInformacion (String titulo,
                                    String fInicial,
                                    String fFinal,
                                    String folio,
                                    String semana,
                                    String agente,
                                    String pulcera,
                                    String sueldo_base,
                                    String porcentaje_comicion,
                                    String comiciones,
                                    String porcentaje_lios,
                                    String bono_lios,
                                    String pago_nuevas,
                                    String bono_nuevas,
                                    String bono1,
                                    String descripcion_bono1,
                                    String bono2,
                                    String descripcion_bono2,
                                    String bono3,
                                    String descripcion_bono3,
                                    String bono4,
                                    String descripcion_bono4,
                                    String subtotal,
                                    String total_adelantos,
                                    String deuda1,
                                    String descuento1,
                                    String concepto_descuento1,
                                    String adeudo_restante1,
                                    String deuda2,
                                    String descuento2,
                                    String concepto_descuento2,
                                    String adeudo_restante2,
                                    String deuda3,
                                    String descuento3,
                                    String concepto_descuento3,
                                    String adeudo_restante3,
                                    String deuda4,
                                    String descuento4,
                                    String concepto_descuento4,
                                    String adeudo_restante4,
                                    String deuda5,
                                    String descuento5,
                                    String concepto_descuento5,
                                    String adeudo_restante5,
                                    String deuda6,
                                    String descuento6,
                                    String concepto_descuento6,
                                    String adeudo_restante6,
                                    String porcentaje_ahorro,
                                    String descuento_ahorro,
                                    String total_nomina,
                                    String caja_ahorros_disponible,
                                    String incremento_caja_ahorro,
                                    String retiro_caja_ahorro,
                                    String concepto_retiro_caja_ahorro,
                                    String total_caja,
                                    String bloqueo,
                                    String motivo,
                                    String cobro_total,
                                    String nuevas_totales,
                                    String lios_totales


    ){






        title = titulo;
        fechaInicial = fInicial;
        fechaFinal = fFinal;

                        //folio
                        //semana
                        //agente
                        //pulcera
        sueldoBase = sueldo_base;
                        //porcentaje_comicion;
        comision = comiciones;
                        //porcentaje_lios;
        bonoLios = bono_lios;
                         //pago_nuevas;
        bonoNuevas = bono_nuevas;
        bono1Importe = bono1;
        bono1Concepto  = descripcion_bono1;
        bono2Importe = bono2;
        bono2Concepto  = descripcion_bono2;
        bono3Importe = bono3;
        bono3Concepto  = descripcion_bono3;
        bono4Importe = bono4;
        bono4Concepto  = descripcion_bono4;
        nominaTotalSinDescuentos = subtotal;
        descuentoAdelantos = total_adelantos;
                        //deuda1;
        descuento1Importe  = descuento1;
        descuento1Concepto  = concepto_descuento1;
        descuento1Restante  = adeudo_restante1;
                        //deuda2;
        descuento2Importe = descuento2;
        descuento2Concepto = concepto_descuento2;
        descuento2Restante = adeudo_restante2;
                        //deuda3;
        descuento3Importe = descuento3;
        descuento3Concepto = concepto_descuento3;
        descuento3Restante = adeudo_restante3;
                        //deuda4;
        descuento4Importe  = descuento4;
        descuento4Concepto  = concepto_descuento4;
        descuento4Restante  = adeudo_restante4;
                        //deuda5;
        descuento5Importe  = descuento5;
        descuento5Concepto  = concepto_descuento5;
        descuento5Restante  = adeudo_restante5;
                        //deuda6;
        descuento6Importe = descuento6;
        descuento6Concepto = concepto_descuento6;
        descuento6Restante = adeudo_restante6;
                        //porcentaje_ahorro;
        descuentoCajaAhorro = descuento_ahorro;
        porCobrar = total_nomina;
        anteriorCajaAhorro = caja_ahorros_disponible;
        aumentoCajaAhorro = incremento_caja_ahorro;
        retiroCajaAhorro = retiro_caja_ahorro;
        conceptoRetiroCajaAhorro = concepto_retiro_caja_ahorro;
        disponibleCajaAhorro = total_caja;
        //bloqueo;
        //motivo;
        totalVentas = cobro_total;
        totalNuevas = nuevas_totales;
        totalLios = lios_totales;





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
                printTitle(title + "\n");
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);

                printText("Del:" + fechaInicial + "   Al:" + fechaFinal + "\n");
                printNewLine();
                printNewLine();
                printText( "****Resultados de la semana****\n");

                printText( "Ventas totales:   " + totalVentas + "\n");
                printText( "Nuevas Totales:   " + totalNuevas + "\n");
                printText( "  Lios Totales:   " + totalLios + "\n");

                printNewLine();
                printNewLine();
                printText( "******Calculo de Nomina******\n");
                printText( "Sueldo Base:   +" + sueldoBase + "\n");
                printText( "Comision:      +" + comision + "\n");
                printText( "Lios 21%:      +" + bonoLios + "\n");
                printText( "Nuevas  :      +" + bonoNuevas + "\n");
                printText( "Bono1:     +" + bono1Importe + "\n");
                //printText( "Concepto de bono1Importe:\n");
                printText( bono1Concepto + "\n");
                printNewLine();
                printText( "Bonos2:     +" + bono2Importe + "\n");
                //printText( "Concepto de bono2Importe:\n");
                printText( bono2Concepto + "\n");
                printNewLine();
                printText( "Bonos3:     +" + bono3Importe + "\n");
                //printText( "Concepto de bono3Importe:\n");
                printText( bono3Concepto + "\n");
                printNewLine();
                printText( "Bonos4:     +" + bono4Importe + "\n");
                //printText( "Concepto de bono4Importe:\n");
                printText( bono4Concepto + "\n");
                printNewLine();
                printText( "------------------\n");
                btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                printText( "Total de Nomina: $" + nominaTotalSinDescuentos + "\n");
                btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);

                printNewLine();
                printNewLine();
                printText( "********Descuentos********\n");

                printText( "Caja Ahorro: -" + descuentoCajaAhorro + "\n");
                printText( "Adelantos:   -" + descuentoAdelantos + "\n");
                printNewLine();
                printText( "Descuento 1:    -" + descuento1Importe + "\n");
                printText( "Adeudo Restante:"+ descuento1Restante +"\n");
                printText(  descuento1Concepto +"\n");
                printNewLine();
                printText( "Descuento 2:    -" + descuento2Importe + "\n");
                printText( "Adeudo Restante:"+ descuento2Restante +"\n");
                printText(  descuento2Concepto +"\n");
                printNewLine();
                printText( "Descuento 3:    -" + descuento3Importe + "\n");
                printText( "Adeudo Restante:"+ descuento3Restante + "\n");
                printText(  descuento3Concepto +"\n");
                printNewLine();
                printText( "Descuento 4:    -" + descuento4Importe + "\n");
                printText( "Adeudo Restante:"+ descuento4Restante + "\n");
                printText(  descuento4Concepto +"\n");
                printNewLine();
                printText( "Descuento 5:    -" + descuento5Importe + "\n");
                printText( "Adeudo Restante:"+ descuento5Restante + "\n");
                printText(  descuento5Concepto +"\n");
                printNewLine();
                printText( "Descuento 6:    -" + descuento6Importe + "\n");
                printText( "Adeudo Restante:"+ descuento6Restante + "\n");
                printText(  descuento6Concepto +"\n");
                printNewLine();
                printNewLine();
                printTitle("Por Cobrar: $" + porCobrar);
                printTitle(title);
                btoutputstream_bit.write(PrinterCommands.SELECT_FONT_A);


                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printNewLine();
                printText( "*****Caja de Ahorro*****\n");

                printText( "Anterior:    " + anteriorCajaAhorro + "\n");
                printText( "Aumento:   +" + aumentoCajaAhorro + "\n");
                printText( "Retiro :   -" + retiroCajaAhorro + "\n");
                printText( conceptoRetiroCajaAhorro + "\n");
                printNewLine();
                btoutputstream_bit.write(PrinterCommands.ESC_SETTING_BOLD);
                printText( "Disponible: $" + disponibleCajaAhorro + "\n");
                btoutputstream_bit.write(PrinterCommands.ESC_CANCEL_BOLD);
                printText( "**************************\n");


                resetPrint();
                printNewLine();
                printNewLine();
                printNewLine();


                resetPrint();

                printUnicode();
                btoutputstream_bit.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText("Firma del Agente\n");
                printText(title);

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

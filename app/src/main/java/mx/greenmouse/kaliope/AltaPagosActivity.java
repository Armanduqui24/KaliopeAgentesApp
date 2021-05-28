package mx.greenmouse.kaliope;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AltaPagosActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, TextView.OnEditorActionListener {
    private Transition transition;

    String horaPago;
    String horaDiferencia;
    String horaSaldoPendiente;
    String horaOtro;
    String horaSaldo;
    String horaPuntos;

    EditText txtPago;
    EditText txtDiferencia;
    EditText txtSaldoPendiente;
    EditText txtOtro;
    EditText txtSaldo;
    EditText txtPuntos;
    TextView txtPagoRecibido;

    /*TextView txtHoraPago;
    TextView txtHoraDiferencia;
    TextView txtHoraSaldoPendiente;
    TextView txtHoraOtro;
    TextView txtHoraSaldo;
    TextView txtHoraPuntos;*/

    TextView lblPago;
    TextView lblDiferencia;
    TextView lblSaldoPendiente;
    TextView lblOtro;
    TextView lblSaldo;
    TextView lblPuntos;

    Button btnTerminePagos;

    Integer iVenta, iDiferencia, iSaldoPendiente, iOtro;
    Integer ElPagoTotal;

    DataBaseHelper dbHelper = new DataBaseHelper(this);
    Constant c = new Constant();

    SoundPool soundPool;
    int carga;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //iniciamos el servicio de geolocalizacion
        startService(new Intent(this,LocationService.class));


        setContentView(R.layout.activity_alta_pagos);
        getSupportActionBar().hide();


        txtPago = (EditText) findViewById(R.id.txtPago);
        txtDiferencia = (EditText) findViewById(R.id.txtDiferencia);
        txtSaldoPendiente = (EditText) findViewById(R.id.txtSaldoPendiente);
        txtOtro = (EditText) findViewById(R.id.txtOtro);
        txtSaldo = (EditText) findViewById(R.id.txtSaldo);
        txtPuntos = (EditText) findViewById(R.id.txtPuntos);
        txtPagoRecibido = (TextView) findViewById(R.id.txtElPagoRecibido);

        /*//ingresamos un 0 a todas los edit text
        txtPago.setText("0");
        txtDiferencia.setText("0");
        txtSaldoPendiente.setText("0");
        txtOtro.setText("0");
        txtSaldo.setText("0");
        txtPuntos.setText("0");
        txtPagoRecibido.setText("0");*/

        /*txtHoraPago = (TextView) findViewById(R.id.txtHoraPago);
        txtHoraDiferencia = (TextView) findViewById(R.id.txtHoraDiferencia);
        txtHoraSaldoPendiente = (TextView) findViewById(R.id.txtHoraSaldoPendiente);
        txtHoraOtro = (TextView) findViewById(R.id.txtHoraOtro);
        txtHoraSaldo = (TextView) findViewById(R.id.txtHoraSaldo);
        txtHoraPuntos = (TextView) findViewById(R.id.txtHoraPuntos);*/

        lblPago = (TextView) findViewById(R.id.lblPago);
        lblDiferencia = (TextView) findViewById(R.id.lblDiferencia);
        lblSaldoPendiente = (TextView) findViewById(R.id.lblSaldoPendiente);
        lblOtro = (TextView) findViewById(R.id.lblOtro);
        lblSaldo = (TextView) findViewById(R.id.lblSaldo);
        lblPuntos = (TextView) findViewById(R.id.lblPuntosVM);

        lblPago.setOnLongClickListener(this);
        lblDiferencia.setOnLongClickListener(this);
        lblSaldoPendiente.setOnLongClickListener(this);
        lblOtro.setOnLongClickListener(this);
        lblSaldo.setOnLongClickListener(this);
        lblPuntos.setOnLongClickListener(this);

        lblPago.setLongClickable(true);
        lblDiferencia.setLongClickable(true);
        lblSaldoPendiente.setLongClickable(true);
        lblOtro.setLongClickable(true);
        lblSaldo.setLongClickable(true);
        lblPuntos.setLongClickable(true);

        txtPago.setOnEditorActionListener(this);
        txtDiferencia.setOnEditorActionListener(this);
        txtSaldoPendiente.setOnEditorActionListener(this);
        txtOtro.setOnEditorActionListener(this);
        txtSaldo.setOnEditorActionListener(this);
        txtPuntos.setOnEditorActionListener(this);

        btnTerminePagos = (Button) findViewById(R.id.btnTerminePagos);
        btnTerminePagos.setOnClickListener(this);


        txtPago.requestFocus();

   /*     txtPago.setText("0");
        txtDiferencia.setText("0");
        txtSaldoPendiente.setText("0");
        txtOtro.setText("0");
        txtSaldo.setText("0");
        txtPuntos.setText("0");*/

        v = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC,0);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        carga = soundPool.load(this, R.raw.harpsound,1);



        asignaValores();

    }

    public void asignaValores(){

        Cursor res = dbHelper.damePagos(Constant.TMPMOV_ID);

        if(res.getCount() >= 1){
            res.moveToNext();

            if (TextUtils.isEmpty(res.getString(3))) {
                /*horaPago = "HH:MM:SS";*/
                iVenta = 0;

            } else {
                /*horaPago = res.getString(3);*/

                iVenta = Integer.parseInt(res.getString(2));

                txtPago.setFocusable(false);
                txtPago.setClickable(false);
                txtPago.setBackgroundColor(Color.GREEN);
            }

            if (TextUtils.isEmpty(res.getString(5))) {
                /*horaDiferencia = "HH:MM:SS";*/
                iDiferencia = 0;
            } else {
                /*horaDiferencia = res.getString(3);*/

                iDiferencia = Integer.parseInt(res.getString(4));
                txtDiferencia.setFocusable(false);
                txtDiferencia.setClickable(false);
                txtDiferencia.setBackgroundColor(Color.GREEN);
            }

            if (TextUtils.isEmpty(res.getString(7))) {
                /*horaSaldoPendiente = "HH:MM:SS";*/
                iSaldoPendiente = 0;
            } else {
                /*horaSaldoPendiente = res.getString(3);*/
                iSaldoPendiente = Integer.parseInt(res.getString(6));
                txtSaldoPendiente.setFocusable(false);
                txtSaldoPendiente.setClickable(false);
                txtSaldoPendiente.setBackgroundColor(Color.GREEN);
            }

            if (TextUtils.isEmpty(res.getString(9))) {
                /*horaOtro = "HH:MM:SS";*/
                iOtro = 0;
            } else {
                /*horaOtro = res.getString(3);*/
                iOtro = Integer.parseInt(res.getString(8));
                txtOtro.setFocusable(false);
                txtOtro.setClickable(false);
                txtOtro.setBackgroundColor(Color.GREEN);
            }

            if (TextUtils.isEmpty(res.getString(10))) {
                /*horaSaldo = "HH:MM:SS";*/
            } else {
                /*horaSaldo = res.getString(5);*/
                txtSaldo.setFocusable(false);
                txtSaldo.setClickable(false);
                txtSaldo.setBackgroundColor(Color.GREEN);
            }

            if (TextUtils.isEmpty(res.getString(12))) {
                /*horaPuntos = "HH:MM:SS";*/
            } else {
                /*horaPuntos = res.getString(7);*/
                txtPuntos.setFocusable(false);
                txtPuntos.setClickable(false);
                txtPuntos.setBackgroundColor(Color.GREEN);
            }

            if(!TextUtils.isEmpty(res.getString(2)) && !TextUtils.isEmpty(res.getString(4)) && !TextUtils.isEmpty(res.getString(6)) && !TextUtils.isEmpty(res.getString(8))){
                Constant.TMPMOV_PAYMENT = true;
            }
            else{
                Constant.TMPMOV_PAYMENT = false;
            }


            txtPago.setText(res.getString(2));
           /* txtHoraPago.setText(horaPago);*/

            txtDiferencia.setText(res.getString(4));
            /*txtHoraDiferencia.setText(horaDiferencia);*/
            txtSaldoPendiente.setText(res.getString(6));
            /*txtHoraSaldoPendiente.setText(horaSaldoPendiente);*/
            txtOtro.setText(res.getString(8));
            /*txtHoraOtro.setText(horaOtro);*/

            txtSaldo.setText(res.getString(10));
            /*txtHoraSaldo.setText(horaSaldo);*/
            txtPuntos.setText(res.getString(12));
            /*txtHoraPuntos.setText(horaPuntos);*/
            ElPagoTotal = iVenta + iDiferencia + iSaldoPendiente + iOtro;

        }
        else {

            dbHelper.insertaPagos(c.TMPMOV_ID);
            Constant.TMPMOV_PAYMENT = false;
            ElPagoTotal = 0;
        }


        txtPagoRecibido.setText(" $ " + String.valueOf(ElPagoTotal));

        Log.d("dbg-payment",String.valueOf(Constant.TMPMOV_PAYMENT));

    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()){
            case R.id.lblPago:

                int registraPago = dbHelper.actualizaPagos( c.TMPMOV_ID,dbHelper.PAGOS_PAGO,
                        "",
                        dbHelper.PAGOS_PAGOS_HORA,
                        "");

                if(registraPago <1){
                    utilidadesApp.dialogoAviso(this,"Ocurrió un error al editar el pago. Intente nuevamente.");
                }
                else{

                    txtPago.setBackgroundColor(Color.TRANSPARENT);
                    txtPago.setFocusable(true);
                    txtPago.setFocusableInTouchMode(true);
                    txtPago.setClickable(true);
                    asignaValores();

                    txtPago.requestFocus();

                }

                break;

            case R.id.lblSaldoPendiente:

                int registraSaldoPendiente = dbHelper.actualizaPagos( c.TMPMOV_ID,dbHelper.PAGOS_SALDO_PENDIENTE,
                        "",
                        dbHelper.PAGOS_HORA_SALDO_PENDIENTE,
                        "");

                if(registraSaldoPendiente <1){
                    utilidadesApp.dialogoAviso(this,"Ocurrió un error al editar el saldo pendiente. Intente nuevamente.");
                }
                else{

                    txtSaldoPendiente.setBackgroundColor(Color.TRANSPARENT);
                    txtSaldoPendiente.setFocusable(true);
                    txtSaldoPendiente.setFocusableInTouchMode(true);
                    txtSaldoPendiente.setClickable(true);
                    asignaValores();

                    txtSaldoPendiente.requestFocus();

                }

                break;
            case R.id.lblDiferencia:

                int registraDiferencia = dbHelper.actualizaPagos( c.TMPMOV_ID,dbHelper.PAGOS_DIFERENCIA,
                        "",
                        dbHelper.PAGOS_HORA_DIFERENCIA,
                        "");

                if(registraDiferencia <1){
                    utilidadesApp.dialogoAviso(this,"Ocurrió un error al editar la diferencia. Intente nuevamente.");
                }
                else{

                    txtDiferencia.setBackgroundColor(Color.TRANSPARENT);
                    txtDiferencia.setFocusable(true);
                    txtDiferencia.setFocusableInTouchMode(true);
                    txtDiferencia.setClickable(true);
                    asignaValores();

                    txtDiferencia.requestFocus();

                }

                break;
            case R.id.lblOtro:

                int registraOtro = dbHelper.actualizaPagos( c.TMPMOV_ID,dbHelper.PAGOS_OTRO,
                        "",
                        dbHelper.PAGOS_HORA_OTRO,
                        "");

                if(registraOtro <1){
                    utilidadesApp.dialogoAviso(this,"Ocurrió un error al editar el pago de otros. Intente nuevamente.");
                }
                else{

                    txtOtro.setBackgroundColor(Color.TRANSPARENT);
                    txtOtro.setFocusable(true);
                    txtOtro.setFocusableInTouchMode(true);
                    txtOtro.setClickable(true);
                    asignaValores();

                    txtOtro.requestFocus();

                }

                break;

            case R.id.lblSaldo:

                int registraSaldo = dbHelper.actualizaPagos(c.TMPMOV_ID,dbHelper.PAGOS_ADEUDO,
                        "",
                        dbHelper.PAGOS_HORA_ADEUDO,
                        "");

                if(registraSaldo <1){
                    utilidadesApp.dialogoAviso(this,"Ocurrió un error al editar el saldo. Intente nuevamente.");
                }
                else{

                    txtSaldo.setBackgroundColor(Color.TRANSPARENT);
                    txtSaldo.setFocusable(true);
                    txtSaldo.setFocusableInTouchMode(true);
                    txtSaldo.setClickable(true);
                    asignaValores();

                    txtSaldo.requestFocus();

                }

                break;
            case R.id.lblPuntosVM:
                Log.d("dbg-pago:", "> " + txtPuntos.getText().toString());

                int registraPuntos = dbHelper.actualizaPagos(   c.TMPMOV_ID,dbHelper.PAGOS_PUNTOS,
                        "",
                        dbHelper.PAGOS_HORA_PUNTOS,
                        "");

                if(registraPuntos <1){
                    utilidadesApp.dialogoAviso(this,"Ocurrió un error al editar los puntos. Intente nuevamente.");
                }
                else{

                    txtPuntos.setBackgroundColor(Color.TRANSPARENT);
                    txtPuntos.setFocusable(true);
                    txtPuntos.setFocusableInTouchMode(true);
                    txtPuntos.setClickable(true);
                    asignaValores();

                    txtPuntos.requestFocus();

                }

                break;
        }

        return true;
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        switch (textView.getId()){

            case R.id.txtPago:
                 asignaPago();
                 break;


            case R.id.txtDiferencia:
                asignaDiferencia();
                break;
            case R.id.txtSaldoPendiente:
                asignaSaldoPendiente();
                break;
            case R.id.txtOtro:
                asignaOtro();
                break;

            case R.id.txtSaldo:
                asignaSaldo();
                break;
            case R.id.txtPuntos:
                asignaPuntos();
                break;

        }

        asignaValores();
        return false;//si retornamos true, no se mueve al siguiente textview de focus
    }
    public void asignaPago(){

        if(TextUtils.isEmpty(txtPago.getText().toString())){
            utilidadesApp.dialogoAvisoFuncion(this,"Debe asignar una cantidad a Pago, si no la hay asigne un 0 (cero).", txtPago);

        }
        else {

            int registraPago = dbHelper.actualizaPagos(c.TMPMOV_ID, dbHelper.PAGOS_PAGO,
                    txtPago.getText().toString(),
                    dbHelper.PAGOS_PAGOS_HORA,
                    utilidadesApp.dameHoraCompleta());

            if (registraPago < 1) {
                utilidadesApp.dialogoAviso(this, "Ocurrió un error al registra el pago. Intente nuevamente.");
            } else {
                soundPool.play(carga,1,1,0,0,1);
                v.vibrate(400);
                txtPago.setFocusable(false);
                txtPago.setClickable(false);
                txtPago.setBackgroundColor(Color.GREEN);

            }
        }

    }

    public void asignaDiferencia(){

        if(TextUtils.isEmpty(txtDiferencia.getText().toString())){
            utilidadesApp.dialogoAvisoFuncion(this,"Debe asignar una cantidad a Diferencia, si no la hay asigne un 0 (cero).", txtDiferencia);
        }
        else {

            int registraDiferencia = dbHelper.actualizaPagos(c.TMPMOV_ID, dbHelper.PAGOS_DIFERENCIA,
                    txtDiferencia.getText().toString(),
                    dbHelper.PAGOS_HORA_DIFERENCIA,
                    utilidadesApp.dameHoraCompleta());

            if (registraDiferencia < 1) {
                utilidadesApp.dialogoAviso(this, "Ocurrió un error al registra la diferencia. Intente nuevamente.");
            } else {
                soundPool.play(carga,1,1,0,0,1);
                v.vibrate(400);
                txtDiferencia.setFocusable(false);
                txtDiferencia.setClickable(false);
                txtDiferencia.setBackgroundColor(Color.GREEN);
            }
        }
    }

    public void asignaSaldoPendiente(){

        if(TextUtils.isEmpty(txtSaldoPendiente.getText().toString())){
            utilidadesApp.dialogoAvisoFuncion(this,"Debe asignar una cantidad a Saldo Pendiente, si no la hay asigne un 0 (cero).", txtSaldoPendiente);
        }
        else {

            int registraSaldoPendniente = dbHelper.actualizaPagos(c.TMPMOV_ID, dbHelper.PAGOS_SALDO_PENDIENTE,
                    txtSaldoPendiente.getText().toString(),
                    dbHelper.PAGOS_HORA_SALDO_PENDIENTE,
                    utilidadesApp.dameHoraCompleta());

            if (registraSaldoPendniente < 1) {
                utilidadesApp.dialogoAviso(this, "Ocurrió un error al registra el Saldo Pendiente. Intente nuevamente.");
            } else {
                soundPool.play(carga,1,1,0,0,1);
                v.vibrate(400);
                txtSaldoPendiente.setFocusable(false);
                txtSaldoPendiente.setClickable(false);
                txtSaldoPendiente.setBackgroundColor(Color.GREEN);
            }
        }
    }

    public void asignaOtro(){

        if(TextUtils.isEmpty(txtOtro.getText().toString())){
            utilidadesApp.dialogoAvisoFuncion(this,"Debe asignar una cantidad a pago de Otros, si no la hay asigne un 0 (cero).", txtOtro);
        }
        else {

            int registraOtro = dbHelper.actualizaPagos(c.TMPMOV_ID, dbHelper.PAGOS_OTRO,
                    txtOtro.getText().toString(),
                    dbHelper.PAGOS_HORA_OTRO,
                    utilidadesApp.dameHoraCompleta());

            if (registraOtro < 1) {
                utilidadesApp.dialogoAviso(this, "Ocurrió un error al registra el cargo de Otros. Intente nuevamente.");
            } else {
                soundPool.play(carga,1,1,0,0,1);
                v.vibrate(400);
                txtOtro.setFocusable(false);
                txtOtro.setClickable(false);
                txtOtro.setBackgroundColor(Color.GREEN);
            }
        }
    }

    public void asignaSaldo(){

        if(TextUtils.isEmpty(txtSaldo.getText())){
            //txtSaldo.setText(0);
            utilidadesApp.dialogoAvisoFuncion(this,"Debe asignar una cantidad a Saldo, si no la hay asigne un 0 (cero).", txtSaldo);
        }
        else {

            int registraSaldo = dbHelper.actualizaPagos(c.TMPMOV_ID, dbHelper.PAGOS_ADEUDO,
                    txtSaldo.getText().toString(),
                    dbHelper.PAGOS_HORA_ADEUDO,
                    utilidadesApp.dameHoraCompleta());

            if (registraSaldo < 1) {
                utilidadesApp.dialogoAviso(this, "Ocurrió un error al registra el saldo. Intente nuevamente.");
            } else {
                soundPool.play(carga,1,1,0,0,1);
                v.vibrate(400);
                txtSaldo.setFocusable(false);
                txtSaldo.setClickable(false);
                txtSaldo.setBackgroundColor(Color.GREEN);
            }
        }
    }

    public void asignaPuntos(){

        if(TextUtils.isEmpty(txtPuntos.getText())) {

            utilidadesApp.dialogoAvisoFuncion(this,"Debe asignar una cantidad a Saldo, si no la hay asigne un 0 (cero).", txtPuntos);
        }
        else {

            int registraPuntos = dbHelper.actualizaPagos(c.TMPMOV_ID, dbHelper.PAGOS_PUNTOS,
                    txtPuntos.getText().toString(),
                    dbHelper.PAGOS_HORA_PUNTOS,
                    utilidadesApp.dameHoraCompleta());

            if (registraPuntos < 1) {
                utilidadesApp.dialogoAviso(this, "Ocurrió un error al registra el pago. Intente nuevamente.");
            } else {
                soundPool.play(carga,1,1,0,0,1);
                v.vibrate(400);
                txtPuntos.setFocusable(false);
                txtPuntos.setClickable(false);
                txtPuntos.setBackgroundColor(Color.GREEN);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnTerminePagos:

                //Intent m = new Intent(this, AltaMovimientoActivity.class);
                //startActivity(m);
                regresar();
                onBackPressed();

                break;



        }
    }




    //REVIZAR DOCUMENTACION LUISDA Pg1
    public void regresar(){


        //(CREAMOS NUESTRO MENSAJE DE PAGOS que va  a ADMINISTRACION AL PRECIONAR LA FLECHA VOLVER
        // EL FORMATO DE ENTREGA SERA EL SIGUIENTE
        // PagoVenta,pagoSaldo,PagoOtro,PagoDiferencia,totalPagos,Puntos,historialDePagos)


        Constant.MENSAJE_PAGOS = iVenta + "," + iSaldoPendiente  + "," + iOtro + "," + iDiferencia + "," + Constant.PAGO_TOTAL+ "," +
                txtPuntos.getText().toString() + "," + Constant.TMP_FECHA + " P:"+ ElPagoTotal + " S:" + txtSaldo.getText().toString();

//        Constant.MENSAJE_PAGOS = "PAGOS" + "\n" + "Venta:" + "\n"+ iVenta + "\n" +
//                "Diferencia de regalo:" +"\n" + iDiferencia + "\n" +
//                "Otro pago:" + "\n" + iOtro + "\n"+
//                "Pago de adeudo:" + "\n" + iSaldoPendiente + "\n" +
//                "Puntos Entregados:" + "\n" + txtPuntos.getText().toString() + " \n" +
//                "Nuevo Adeudo:" + "\n" + txtSaldo.getText().toString() + "\n" +
//                "HISTORIAL DE PAGOS" + "\n" + Constant.TMP_FECHA +" P:"+ ElPagoTotal + " S:" + txtSaldo.getText().toString();

                //Toast.makeText(this,Constant.MENSAJE_PAGOS,Toast.LENGTH_LONG).show();






        //txtSaldo.getText().toString() +





                Constant.PAGO_TOTAL = ElPagoTotal;

            //revisamos que txt puntos no este vacio para asignarlo a la constante cpuntos porque sino generaba error fatal
            if (!TextUtils.isEmpty(txtPuntos.getText()))
            {
                Constant.PUNTOS = Integer.parseInt(txtPuntos.getText().toString());
            }

            if ( !TextUtils.isEmpty(txtSaldo.getText())){
                Constant.SALDO_PENDIENTE = Integer.parseInt(txtSaldo.getText().toString());
            }

        //Intent i = new Intent(this, AltaMovimientoActivity.class);
        //startActivity(i);
        //finishAfterTransition();
    }

}


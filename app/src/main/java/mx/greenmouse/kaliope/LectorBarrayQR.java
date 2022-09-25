package mx.greenmouse.kaliope;

import android.graphics.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;




public class LectorBarrayQR extends AppCompatActivity {

    EditText codigo;
    Button escanear;
    private ZXingScannerView vistaEscaner;

    boolean flash = false;


    //sonido y vibracion
    SoundPool soundPool;
    int pitido;

    Vibrator vibrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_barray_qr);

        escanear = (Button) findViewById(R.id.escanearCodigoBarrasButton);


        //cargamos el sonido
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC,0);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        pitido = soundPool.load(this,R.raw.pitido,1);

        //instanciamos la vibracion
        vibrar = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

            //enviar un putExtra boolean con true si se quiere activar el flash
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            flash = bundle.getBoolean("flash");
        }

        escanear(flash); //llamamos inmediatamente a escanear para que no se muestre el layout donde esta el boton y el textView



        escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escanear(flash);
            }
        });
    }

    public void escanear (boolean flash){
        vistaEscaner = new ZXingScannerView(this);
        vistaEscaner.setResultHandler(new escanerLuisda());
        setContentView(vistaEscaner);
        vistaEscaner.startCamera();
        vistaEscaner.setFlash(flash);
    }

    @Override
    public void onBackPressed (){
        super.onBackPressed();
        vistaEscaner.stopCamera();
        vistaEscaner.setFlash(false);
        //si precionamos la tecla volver y no escaneamos nada detenemos la camara
    }

    @Override
    public void onPause (){
        super.onPause();
        vistaEscaner.stopCamera();
        vistaEscaner.setFlash(false);
    }





    class escanerLuisda implements ZXingScannerView.ResultHandler{

        @Override
        public void handleResult(Result result) {
            String dato = result.getText();
            setContentView(R.layout.activity_lector_barray_qr);
            vistaEscaner.stopCamera();
            codigo = (EditText) findViewById(R.id.codigoBarrasET);
            codigo.setText(dato); //le enviamos el dato leido al TextView del layout
            codigo.setVisibility(View.INVISIBLE);//ocultamos el textView

            Constant.CODIGO_BARRAS_PULSERA_CAMARA = dato; //guardamos en una constate el codigo capturado para poder usarlo en cualquier activity
            //agregamos un pitido y vibracion al terminar de escanear
            soundPool.play(pitido,1,1,0,0,1);
            vibrar.vibrate(100);
            finish();//terminamos el activity para que nos devuelva en automatico al activity que lo llamo

        }
    }



}

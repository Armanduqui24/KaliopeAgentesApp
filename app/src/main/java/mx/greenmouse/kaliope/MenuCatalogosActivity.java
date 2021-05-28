package mx.greenmouse.kaliope;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;

public class MenuCatalogosActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txSesion;
    TextView txEstatus;

    ImageButton btPrecios;
    ImageButton btPulseras;
    //ImageButton btCreditos;

    utilidadesApp ua;
    DataBaseHelper dbHelper = new DataBaseHelper(this);
    Constant c = new Constant();
    Activity activity;
    VariablePassword variablePassword;
    //lo inicializamos en el on resume esto para que se cree nuevamente el objeto
    //cada que se se entra o se sale de la activydad, esto porque, si tu creas el objeto en esta parte
    //una ves que valides la contraseña por primera ves, te deja entrar al activyti de pulseras
    //sales de la activuidad de pulseras, en el objeto VariablePassword su instancia validacion sige estando en true
    //porque anteriormente se valido, esto significa que nos dejara entrar nuevamente a las pulseras,
    //al instanciar el objeto en el onResume, este se creara cada ves que se active el onresume
    //por lo tanto su variable de instancia validacion se inicializara por su constructor a false
    //obteniendose asi que al regresar a la actividad pida nuevamente la claveVariable




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_catalogos);
        getSupportActionBar().hide();
        activity = this;

        btPrecios   = (ImageButton)findViewById(R.id.btPrecios);
        btPulseras = (ImageButton) findViewById(R.id.btPulseras);
        //btCreditos  = (ImageButton)findViewById(R.id.btCreditos);

        btPrecios.setOnClickListener(this);
        btPulseras.setOnClickListener(this);
        //btCreditos.setOnClickListener(this);

        txSesion    = (TextView)findViewById(R.id.txSesion);
        txEstatus   = (TextView)findViewById(R.id.txEstaus);

        txSesion.setText("BIENVENIDO " + ConfiguracionesApp.getUsuarioIniciado(activity) );
        //txEstatus.setText("RUTA: " + c.INSTANCE_ROUTE + "_" + c.INSTANCE_DATE);
    }

    @Override
    public void onClick(View v) {

        File folder     = new File(Constant.INSTANCE_PATH, "/mx.4103.klp");

        switch (v.getId()){


            case R.id.btPulseras:

                if(!variablePassword.getValidacion()){
                    //si la varaible validacion del objeto variablePassword es falsa entonces llamamos al cuadro de dialogo, como el objeto cada que se crea
                    //su constructor inicializa a false la validacion, la primera ves entrara aqui.
                    variablePassword.alertDialogValidaPassword(this,this,"Para Acceder a este catalogo es necesario ingresar el codigo." +
                            " Por favor llame a sistemas Kaliope ");

                    //una ves que aparese el dialogo, si el usuario valida correctamente el codigo
                    //la variable validacion dentro de la clase VariablePassword
                    //cambia a True por lo tanto al presionar por segunda vez el boton pulseras
                    //ahora entrara directo al catalogo
                }else{
                    //Intent intent = new Intent(this, Pulseras.class);
                    //startActivity(intent);
                }

                break;







//            case R.id.btCreditos:

//                Intent f = new Intent(this, CatalogoCreditosActivity.class);
//                Cursor res_codigos = dbHelper.dameCodigos();
//
//                File codigos = new File(folder + "/catCodigos.txt");
//
//                if(codigos.isFile() || c.INSTANCE_DB_CODES || res_codigos.getCount() > 0) {
//                    startActivity(f);
//                }
//                else{
//                    Log.d("bgd-codes", String.valueOf(c.INSTANCE_DB_CODES));
//                    utilidadesApp.dialogoAviso(this,"No existe el catálogo de códigos.");
//                }
//                break;
        }
    }

    @Override
    public void onBackPressed(){

        Intent m = new Intent(this, MenuPrincipalActivity.class);
        startActivity(m);
    }

    @Override
    public void onResume (){
        super.onResume();
        variablePassword = new VariablePassword();

    }
}

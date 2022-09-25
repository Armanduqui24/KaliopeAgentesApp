package mx.greenmouse.kaliope;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ReporteDeVisita extends AppCompatActivity implements View.OnClickListener {

    DataBaseHelper dbHelper = new DataBaseHelper(this);
    Constant c = new Constant();

    TextView txtReporteVisita;
    Button btnGuardaReporteDeVisita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_de_visita);
        getSupportActionBar().hide();

        txtReporteVisita = (TextView) findViewById(R.id.txtReporteVisita);
        btnGuardaReporteDeVisita = (Button) findViewById(R.id.btnGuardarReporteDeVisita);

        btnGuardaReporteDeVisita.setOnClickListener(this);

        dameReporteDeVisita();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnGuardarReporteDeVisita:

                if(txtReporteVisita.getText().toString().equals("")){
                    utilidadesApp.dialogoAviso(this,"Detalle su reporte de visita.");
                }
                else{

                    int up = dbHelper.actualizaMovimiento(c.TMPMOV_ID,txtReporteVisita.getText().toString());

                    if(up < 1){
                        utilidadesApp.dialogoAviso(this,"Error al guardar el reporte.");
                    }
                    else{

                        Constant.TMPMOV_REPORT = true;

                        Intent s = new Intent(this, AltaMovimientoActivity.class);
                        startActivity(s);
                }

                }


                break;
        }

    }

    public void dameReporteDeVisita(){

        String reporte = dbHelper.dameUnicoDato(dbHelper.TABLE_MOVEMENTS,dbHelper.MOVEMENTS_REPORT,c.TMPMOV_ID);

        if(!reporte.equals("")){
            txtReporteVisita.setText(reporte);
        }

    }

}

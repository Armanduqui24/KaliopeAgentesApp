package mx.greenmouse.kaliope;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MovimientosAlmacen extends AppCompatActivity implements View.OnClickListener{

    ImageButton btMovimientoAutomatico, btMovimientoManual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos_almacen);
        getSupportActionBar().hide();

        btMovimientoAutomatico = (ImageButton) findViewById(R.id.Button103);
        btMovimientoManual = (ImageButton) findViewById(R.id.Button102);

        btMovimientoAutomatico.setOnClickListener(this);
        btMovimientoManual.setOnClickListener(this);








    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case  R.id.Button103:
                //generar movimiento automatico
                Intent intent = new Intent(this, MovimientoAutomaticoAlmacen.class);
                startActivity(intent);
                break;
            case R.id.Button102:
                break;
        }
    }
}

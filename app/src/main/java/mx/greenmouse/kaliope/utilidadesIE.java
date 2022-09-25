package mx.greenmouse.kaliope;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class utilidadesIE extends AppCompatActivity {



    DataBaseHelper dbHelper = new DataBaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilidades_ie);
    }




}

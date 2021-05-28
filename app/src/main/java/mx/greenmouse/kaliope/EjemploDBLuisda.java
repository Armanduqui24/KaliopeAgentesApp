package mx.greenmouse.kaliope;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class EjemploDBLuisda extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION= 1;
    public static final String DATABASE_NAME = "AGENTES.db";

    public static String TABLA_NOMBRES = "nombres";
    public static String COLUMNA_ID = "_id";
    public static String COLUMNA_NOMBRE = "nombre";

    private static final String SQL_CREAR = "create table "+ TABLA_NOMBRES +"("+COLUMNA_ID + "integer primary key autoincrement,"+ COLUMNA_NOMBRE
    +"text not null);";


    public EjemploDBLuisda (Context context){
        super(context, DATABASE_NAME, null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL (SQL_CREAR);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public int agregar (String nombre){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(COLUMNA_NOMBRE,nombre);

        sqLiteDatabase.insert(TABLA_NOMBRES,null, values);
        long newRowId;
        newRowId = sqLiteDatabase.insert(TABLA_NOMBRES,null,values);

        sqLiteDatabase.close();
        return (int)newRowId;
    }

    public String obtener (int id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String [] projection = {COLUMNA_ID,COLUMNA_NOMBRE};
        Cursor cursor =
                sqLiteDatabase.query(TABLA_NOMBRES,
                        projection,"_id = ?",
                        new String[] {String.valueOf(id)},
                        null,null,null,null);

        if(cursor!=null)
            cursor.moveToFirst();
        String mensaje = "El nombre es " + cursor.getString(id);



        //System.out.println("El nombre es " + cursor.getString(1));

        sqLiteDatabase.close();
        return mensaje;

    }


}

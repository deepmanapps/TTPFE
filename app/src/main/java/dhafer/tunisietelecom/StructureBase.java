package dhafer.tunisietelecom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhafer ZAHROUNI on 17/06/2015.
 */
public class StructureBase extends SQLiteOpenHelper {

private static final int versionDB=2;
private static final String nomDB="drivetest";
private static final String tableDB="infos";
    // les colonnes de la base de données
    private static final String tuple1="id";
    private static final String tuple2="operator";
    private static final String tuple3="idcell";
    private static final String tuple4="lac";
    private static final String tuple5="typenet";
    private static final String tuple6="sig";
    private static final String tuple7="longi";
    private static final String tuple8="lati";
    private static final String tuple9="mcc";
    private static final String tuple10="datetime";
    private static final String tuple11="altitude";

    public StructureBase(Context context) {
        super(context, nomDB, null, versionDB);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
   String requete="CREATE TABLE "+tableDB+"("+tuple1+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
           tuple2+" TEXT,"+tuple3+" TEXT,"+tuple4+" TEXT,"+tuple5+" TEXT,"+
           tuple6+" TEXT,"+tuple7+" TEXT,"+tuple8+" TEXT,"+tuple9+" TEXT,"+tuple10+" TEXT,"+tuple11+" TEXT"+");";
    db.execSQL(requete);
        Log.i("requete", requete);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          db.execSQL("DROP TABLE IF EXISTS "+tableDB);
        onCreate(db);
    }
    // ALL CRUD
    void addinfos(Infos infos){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues valeurs=new ContentValues();
      valeurs.put(tuple1,infos.getId());
        valeurs.put(tuple2,infos.getOperator());
        valeurs.put(tuple3,infos.getIdcell());
        valeurs.put(tuple4,infos.getLac());
        valeurs.put(tuple5,infos.getTypenet());
        valeurs.put(tuple6,infos.getSig());
        valeurs.put(tuple7,infos.getLongi());
        valeurs.put(tuple8,infos.getLati());
        valeurs.put(tuple9,infos.getMcc());
        valeurs.put(tuple10,infos.getDatetime());
        valeurs.put(tuple11,infos.getAltitude());
        db.insert(tableDB,null,valeurs);
        db.close();
    }
//get all infos
    public List<Infos> getallinfos(){
        List<Infos> tous=new ArrayList<Infos>();
        String rq="SELECT * FROM "+tableDB;
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor cursor=db.rawQuery(rq,null);
        if (cursor.moveToFirst()) {
            do {
                Infos infos = new Infos();
                infos.setId(Integer.parseInt(cursor.getString(0)));
                infos.setOperator(cursor.getString(1));
                infos.setIdcell(cursor.getString(2));
                infos.setLac(cursor.getString(3));
                infos.setTypenet(cursor.getString(4));
                infos.setSig(cursor.getString(5));
                infos.setLongi(cursor.getString(6));
                infos.setLati(cursor.getString(7));
                infos.setMcc(cursor.getString(8));
                infos.setDatetime(cursor.getString(9));
                infos.setAltitude(cursor.getString(10));
                // Adding infos to list
                tous.add(infos);
            } while (cursor.moveToNext());
        }
return tous;
    }
    public int getInfosCount() {
        String countQuery = "SELECT  * FROM " + tableDB;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }
//delete all
public void DeleteAll() {

    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(tableDB,null,null);
    db.close();
}



}

package dhafer.tunisietelecom;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;

import android.location.LocationManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.gsm.GsmCellLocation;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.opencsv.CSVWriter;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CellSettings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static String url = "https://maps.googleapis.com/maps/api/elevation/json?locations=";
    private static String url2="";
    private static final String resultjson = "results";
    private static final String tagelevation = "elevation";
     ProgressDialog pdialog;
    JSONArray user = null;
    TextView operator,cell_id,lac,mcc,net_type,signal_st,longit,latit,accur,alti,saved_timemonth,timee;
    StructureBase stdb = new StructureBase(this);
    double latitude;
    double longitude;
    double altitude;
    double accuracy;
    TextView allt,tserv;
ImageView play,uploadfile,recordtoserver;
    private TelephonyManager mTelephonyManager;
    TelephonyManager tManager ;
    boolean var=false;
    LocationManager lm;
    GPSTracker gps;
    int k=0;
    Location l;
    Thread t,save,tneighbor,tremove,tjson;
    int cid,lac_val,i;
    double ln,lat;
    Thread savefirebase;
   TableLayout tl ;
    List<Infos> info;
    File exportDir = new File(Environment.getExternalStorageDirectory(), "");
    File file ;
    CSVWriter writer;
    String server = "ftp.networksbox.net";
    int port = 21;
    String userftp = "networksca";
    String pass = "uNCSZKPx6qzq";
    public static final String ROOT_URL = "http://networksbox.net/tunisietelecom";
    // firebase ....
    Firebase mRef;

    // .............
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_settings);
        stdb.DeleteAll();
        tserv=(TextView)findViewById(R.id.titreserving);
        tserv.setTextColor(Color.rgb(0, 76, 153));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton ff=(FloatingActionButton)findViewById(R.id.fabcel);
        ff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii=new Intent(CellSettings.this,AddCellular.class);
                startActivity(ii);
            }
        });
        mRef=new Firebase("https://tunisie-telecom.firebaseio.com/");
        allt   = (TextView) findViewById(R.id.altitude);
        //******** Button *************
        play=(ImageView)findViewById(R.id.play);
        play.setImageResource(R.drawable.play);
        recordtoserver=(ImageView)findViewById(R.id.recordreal);
        uploadfile=(ImageView)findViewById(R.id.upload);
        uploadfile.setImageResource(R.drawable.expcloud);
        recordtoserver.setImageResource(R.drawable.recordd);
        //*****************************
        /**
         * Actionner les boutons -------------------------------------------------------------------------------------------------------
         *
         */
        recordtoserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                var=!var;
                if(var) {
                    recordtoserver.setImageResource(R.drawable.toserverload);
                    getServingcell(var);
                    gettimee(var);
                    SaveInFireBase(var);
                }else if(!var) {
                    recordtoserver.setImageResource(R.drawable.recordd);
                    savefirebase.interrupt();


                }
            }

        });
        uploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(var==false) {
                    new UploadFile().execute();
                }

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                var=!var;
                if (var) {
                    play.setImageResource(R.drawable.pause);
                    getServingcell(var);
                    gettimee(var);
                    SaveInBase(var);


                }else if(!var){
                    gettimee(false);
                    play.setImageResource(R.drawable.play);
                    t.interrupt();
                    getServingcell(false);
                    tjson.interrupt();
                    save.interrupt();
                }

            }
        });





        /**
         * -----------------------------------------------------------------------------------------------------------------------------
         */
        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//----------- onCreate Data Base et CSV -----------------

        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }
//--------------------------------------------------------
        tl = (TableLayout) findViewById(R.id.tableLayout1);
        TableRow tr_head = new TableRow(this);
        tr_head.setId(10);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setAlpha(0.9F);
        tr_head.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView label_date = new TextView(this);
        label_date.setId(20);
        label_date.setText("Lac");
        label_date.setTextColor(Color.WHITE);
        label_date.setPadding(5, 5, 5, 5);
        tr_head.addView(label_date);// add the column to the table row here

        TextView label_weight_kg = new TextView(this);
        label_weight_kg.setId(21);// define id that must be unique
        label_weight_kg.setText("Cell ID"); // set the text for the header
        label_weight_kg.setTextColor(Color.WHITE); // set the color
        label_weight_kg.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_weight_kg); // add the column to the table row here
        TextView label_weight_kg1 = new TextView(this);

        label_weight_kg1.setId(211);// define id that must be unique
        label_weight_kg1.setText("Level/Rx (dBm)"); // set the text for the header
        label_weight_kg1.setTextColor(Color.WHITE); // set the color
        label_weight_kg1.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_weight_kg1); // add the column to the table row here
        TextView label_weight_kg2 = new TextView(this);
        label_weight_kg2.setId(222);// define id that must be unique
        label_weight_kg2.setText("Type"); // set the text for the header
        label_weight_kg2.setTextColor(Color.WHITE); // set the color
        label_weight_kg2.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_weight_kg2); // add the column to the table row here
        tl.addView(tr_head, new TableLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //--------------------------------------------------------

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
//**********
void writecsvfile(List<String[]> test,String[] test2)throws Exception{
    file = new File(exportDir, "TunisieTelecom.csv");
    // writer = new CSVWriter(new FileWriter(file));
    writer = new CSVWriter(new FileWriter(file), ';');
    writer.writeNext(test2);
    writer.writeAll(test);
    writer.close();
    Toast.makeText(CellSettings.this, "Exportation vers: " + exportDir, Toast.LENGTH_LONG).show();
}
    //******-----
    @Override
    protected void onStart() {
        super.onStart();


//******************************************************
        String tn=networkType();
        if(tn=="EDGE") {
            getcellneibor();
        }



    }

    void gettimee(boolean cc){
if(cc){
        CountDownTimer newtimer = new CountDownTimer(1000000000, 1000) {
            public void onTick(long millisUntilFinished) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                saved_timemonth.setText(dateFormat.format(date));
            }
            public void onFinish() {
            }
        };
        newtimer.start();
    }}
    void getServingcell(boolean test){
        operator = (TextView) findViewById(R.id.operator);
        net_type = (TextView) findViewById(R.id.type);
        cell_id = (TextView) findViewById(R.id.cellid);
        lac = (TextView) findViewById(R.id.lac);
        mcc = (TextView) findViewById(R.id.mcc);
        signal_st = (TextView) findViewById(R.id.stsignal);
        saved_timemonth = (TextView) findViewById(R.id.monthyear);

        longit = (TextView) findViewById(R.id.longi);
        latit = (TextView) findViewById(R.id.lattitudee);
        accur = (TextView) findViewById(R.id.accuracy);

//---------------------------------------------------------
        if(test==true){
            var=true;
        mTelephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        //------------------*********
        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                i = i + 1;
                                tManager = (TelephonyManager) getBaseContext()
                                        .getSystemService(Context.TELEPHONY_SERVICE);
                                int networkType = tManager.getNetworkType();
                                String net_operator = tManager.getNetworkOperator();
                                mcc.setText(net_operator.substring(3));
                                String carrierName = tManager.getNetworkOperatorName();
                                operator.setText(carrierName);
                                GsmCellLocation cell_location = (GsmCellLocation) tManager.getCellLocation();
//----------
                                gps = new GPSTracker(CellSettings.this);
                                // check if GPS enabled
                                if(gps.canGetLocation()){
                                    latitude = gps.getLatitude();
                                    longitude = gps.getLongitude();
                                    accuracy=gps.getAccuracy();
                                    longit.setText(String.valueOf(longitude));
                                    latit.setText(String.valueOf(latitude));
                                    accur.setText(String.valueOf(accuracy));
                                }
                                //----
                                cid = cell_location.getCid();
                                cell_id.setText(String.valueOf(cid));
                                lac_val = cell_location.getLac();
                                lac.setText(String.valueOf(lac_val));

                                //----- net_type -----
                                switch (networkType)
                                {
                                    case 7:
                                        net_type.setText("1xRTT");
                                        break;
                                    case 4:
                                        net_type.setText("CDMA");
                                        break;
                                    case 2:
                                        net_type.setText("EDGE");
                                        break;
                                    case 14:
                                        net_type.setText("eHRPD");
                                        break;
                                    case 5:
                                        net_type.setText("EVDO rev. 0");
                                        break;
                                    case 6:
                                        net_type.setText("EVDO rev. A");
                                        break;
                                    case 12:
                                        net_type.setText("EVDO rev. B");
                                        break;
                                    case 1:
                                        net_type.setText("GPRS");
                                        break;
                                    case 8:
                                        net_type.setText("HSDPA");
                                        break;
                                    case 10:
                                        net_type.setText("HSPA");
                                        break;
                                    case 15:
                                        net_type.setText("HSPA+");
                                        break;
                                    case 9:
                                        net_type.setText("HSUPA");
                                        break;
                                    case 11:
                                        net_type.setText("iDen");
                                        break;
                                    case 13:
                                        net_type.setText("LTE");
                                        break;
                                    case 3:
                                        net_type.setText("UMTS");
                                        break;
                                    case 0:
                                        net_type.setText("Unknown");
                            break;
                        }
                    }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }else{
        t.interrupt();
        mTelephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_NONE);
        }
        tjson = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {

                        Thread.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
url2=url+latitude+","+longitude+"&key=AIzaSyCPs7uvfw7TWlvRgqyFPjAbXV_9yMqrS4I";
                                new RetrieveMessages().execute(url2);

                            }});}}catch (InterruptedException e) {
                }
            }
        };tjson.start();
    }
void getcellneibor() {

    //----------------------- list neighbors ------------------------
    tneighbor = new Thread() {
        @Override
        public void run() {
            try {
                while (!isInterrupted()) {

                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Integer count = 0;
                            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            List<NeighboringCellInfo> NeighboringList = telephonyManager.getNeighboringCellInfo();
                            for (int i = 0; i < NeighboringList.size(); i++) {
                                int rssi = (-113 + NeighboringList.get(i).getRssi() * 2);
                                int typee = NeighboringList.get(i).getNetworkType();
                                int lacc = NeighboringList.get(i).getLac();
                                int cellidd = NeighboringList.get(i).getCid();

                                Log.i("net type", "" + typee);


                                TableRow tr = new TableRow(CellSettings.this);
                                tr.setAlpha(0.6F);
                                if (count % 2 != 0) tr.setBackgroundColor(Color.GRAY);
                                tr.setId(100 + count);
                                tr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//Create two columns to add as table data
                                // Create a TextView to add date
                                TextView labelDATE = new TextView(CellSettings.this);
                                labelDATE.setId(210 + count);
                                labelDATE.setText(String.valueOf(lacc));
                                labelDATE.setPadding(2, 0, 5, 0);
                                labelDATE.setTextColor(Color.BLUE);
                                tr.addView(labelDATE);
                                TextView labelWEIGHT = new TextView(CellSettings.this);
                                labelWEIGHT.setId(310 + count);
                                labelWEIGHT.setText(String.valueOf(cellidd));
                                labelWEIGHT.setTextColor(Color.BLUE);
                                tr.addView(labelWEIGHT);
                                TextView labelrssi = new TextView(CellSettings.this);
                                labelrssi.setId(410 + count);
                                labelrssi.setText(String.valueOf(rssi));
                                labelrssi.setTextColor(Color.BLUE);
                                tr.addView(labelrssi);
                                TextView typ = new TextView(CellSettings.this);
                                typ.setId(510 + count);
                                switch (typee) {
                                    case 7:
                                        typ.setText("1xRTT");
                                        break;
                                    case 4:
                                        typ.setText("CDMA");
                                        break;
                                    case 2:
                                        typ.setText("EDGE");
                                        break;
                                    case 14:
                                        typ.setText("eHRPD");
                                        break;
                                    case 5:
                                        typ.setText("EVDO rev. 0");
                                        break;
                                    case 6:
                                        typ.setText("EVDO rev. A");
                                        break;
                                    case 12:
                                        typ.setText("EVDO rev. B");
                                        break;
                                    case 1:
                                        typ.setText("GPRS");
                                        break;
                                    case 8:
                                        typ.setText("HSDPA");
                                        break;
                                    case 10:
                                        typ.setText("HSPA");
                                        break;
                                    case 15:
                                        typ.setText("HSPA+");
                                        break;
                                    case 9:
                                        typ.setText("HSUPA");
                                        break;
                                    case 11:
                                        typ.setText("iDen");
                                        break;
                                    case 13:
                                        typ.setText("LTE");
                                        break;
                                    case 3:
                                        typ.setText("UMTS");
                                        break;
                                    case 0:
                                        typ.setText("Unknown");
                                        break;
                                }

                                typ.setTextColor(Color.BLUE);
                                tr.addView(typ);
                                tl.addView(tr,1, new TableLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                count++;
                            }

                        }

                    });

                }
            } catch (InterruptedException e) {
            }
        }
    };
    tneighbor.start();
//*************************** refresh *****************
    tremove = new Thread() {

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {

                    Thread.sleep(2000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(tl.isShown()) {tl.removeViews(1, 2);}


                        }});}}catch (InterruptedException e) {
                        }
                    }
                };tremove.start();
}
    private String networkType() {
        TelephonyManager teleMan = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = teleMan.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT: return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA: return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE: return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD: return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0: return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A: return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B: return "EVDO rev. B";
            case TelephonyManager.NETWORK_TYPE_GPRS: return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA: return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA: return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP: return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_HSUPA: return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN: return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE: return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS: return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN: return "Unknown";
        }
        throw new RuntimeException("New type of network");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        t.interrupt();
        tjson.interrupt();
       // tneighbor.interrupt();
//        tremove.interrupt();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cell_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.excsv) {
            if(var==false){
                List<String[]> l=new ArrayList<String[]>();
                info= stdb.getallinfos();

                String[] head = {"Id","Operator","Cell ID","Lac","Strength Signal","Type","long","lat","MNC","Save Time","Elevation"};
                for (Infos data : info) {

                    String[] cn={String.valueOf(data.getId()),data.getOperator(),data.getIdcell(),data.getLac(),data.getSig(),data.getTypenet(),data.getLongi(),data.getLati(),data.getMcc(),data.getDatetime(),data.getAltitude()};
                    l.add(cn);
                }
                try {
                    writecsvfile(l,head);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else{
                Toast.makeText(CellSettings.this,"déactivez les processus avant d'exporter !!",Toast.LENGTH_LONG).show();
            }
//latec,edraw,visio Microsoft,temporaire spacial tempporaire



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.voice) {
//            t.interrupt();
  //          tjson.interrupt();
            Intent it=new Intent(getApplicationContext(),VoiceTest.class);
            startActivity(it);
        } else if (id == R.id.data) {
         //   t.interrupt();
           // tjson.interrupt();
            Intent it=new Intent(getApplicationContext(),DataTest.class);
            startActivity(it);
        } else if (id == R.id.gmap) {
          //  t.interrupt();
            //tjson.interrupt();
            Intent it=new Intent(getApplicationContext(),CarteGoogleMap.class);
            startActivity(it);
        } else if (id == R.id.arcgis) {
//            t.interrupt();
          //  tjson.interrupt();
            Intent it=new Intent(getApplicationContext(),ArcGISMap.class);
            startActivity(it);

        } else if (id == R.id.cellsettings) {

        } else if (id == R.id.realtime) {
           // t.interrupt();
          //  tjson.interrupt();
            Intent it=new Intent(getApplicationContext(),RealTimePlot.class);
            startActivity(it);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        t.interrupt();
//tjson.interrupt();

    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            if(var){

                super.onSignalStrengthsChanged(signalStrength);
                if(mTelephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM){
                    String val_dbm=String.valueOf(signalStrength.getGsmSignalStrength()*2 - 113);
                    signal_st.setText(val_dbm);
                }
                else if(mTelephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA){
                    signal_st.setText("CDMA "+signalStrength.getCdmaDbm()+" dBm");
                }
                else{
                    signal_st.setText("Unknown PhoneType: "+mTelephonyManager.getPhoneType());
                }
            }
        }
    }

    private class RetrieveMessages extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {

            String jsont = "";

            JSONParser jParser = new JSONParser();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pid", "rien"));
            JSONObject json = jParser.makeHttpRequest(url2,"ALL",params);
            try {
                user = json.getJSONArray(resultjson);
                JSONObject c = user.getJSONObject(0);
                jsont = c.getString(tagelevation);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsont;
        }

        protected void onProgressUpdate(Void... progress) {

        }

        protected void onPostExecute(String result) {

            allt.setText(result);
        }
    }
    //--------------------------- set in data base
    void SaveInBase(boolean test) {

        if (test == true) {
            save = new Thread() {

                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    k = k + 1;

                                    stdb.addinfos(new Infos(k, operator.getText().toString(), cell_id.getText().toString(),
                                            lac.getText().toString(), net_type.getText().toString(), signal_st.getText().toString(),
                                            longit.getText().toString(), latit.getText().toString(), mcc.getText().toString(),saved_timemonth.getText().toString(),allt.getText().toString()));


                                }

                            });
                        }
                    } catch (InterruptedException e) {
                    }
                }
            };
            save.start();
        } else {

            save.interrupt();

        }

    }
    //************************************* Save FireBase *********************************
    void SaveInFireBase(boolean test) {

        if (test == true) {
            savefirebase = new Thread() {

                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    k = k + 1;

                                    mRef.child("cellid").setValue(cell_id.getText().toString());
                                    mRef.child("elevation").setValue(allt.getText().toString());
                                    mRef.child("id").setValue(k);
                                    mRef.child("lac").setValue(lac.getText().toString());
                                    mRef.child("lat").setValue(latit.getText().toString());
                                    mRef.child("long").setValue(longit.getText().toString());
                                    mRef.child("mcc").setValue(mcc.getText().toString());
                                    mRef.child("operator").setValue(operator.getText().toString());
                                    mRef.child("signal").setValue(signal_st.getText().toString());
                                    mRef.child("time").setValue(saved_timemonth.getText().toString());
                                    mRef.child("type").setValue(net_type.getText().toString());

                                }

                            });
                        }
                    } catch (InterruptedException e) {
                    }
                }
            };
            savefirebase.start();
        } else {

           // savefirebase.interrupt();

        }

    }



/**
 * Class Upload for CSV file
 */
private class UploadFile extends AsyncTask<Void, Integer, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        uploadfile.setImageResource(R.drawable.uploadload);
        pdialog=new ProgressDialog(CellSettings.this);
        pdialog.setIndeterminate(true);
        pdialog.setMessage("Uploading CSV file ....");
        pdialog.show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        File f = new File("/sdcard/TunisieTelecom.csv");
        //***************
        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(server, port);
            ftpClient.login(userftp, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory("/www/tunisietelecom/csvfiles");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            String firstRemoteFile = "TunisieTelecom.csv";
            InputStream inputStream = new FileInputStream(f);
            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
            inputStream.close();
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }}

        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        Toast.makeText(getApplicationContext(), " Upload TunisieTelecom.csv Done", Toast.LENGTH_LONG).show();
        uploadfile.setImageResource(R.drawable.expcloud);
pdialog.dismiss();

    }
}
        //*----------------------------------envoi de données-----------------------------
}

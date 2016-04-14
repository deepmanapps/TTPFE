package dhafer.tunisietelecom;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.speech.tts.Voice;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VoiceTest extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TODO = "";
    static String statee="";
    static int k = 0;
    String rstype = null;
    static int returntoapp=0;
    static String cc;
    EditText number0;
ImageButton launch,getreport;
    SimpleDateFormat dateFormat;
    Date date;
    String phNumber ;
    String callDuration;
    String Calltype;
    public static final String ROOT_URL = "http://networksbox.net/tunisietelecom";
    RestAdapter adapter;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add PhoneStateListener for monitoring
        MyPhoneListener phoneListener = new MyPhoneListener();
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        // receive notifications of telephony state changes
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        statee="";
        number0 = (EditText) findViewById(R.id.editnumb);
        launch =(ImageButton)findViewById(R.id.lch);
getreport =(ImageButton)findViewById(R.id.rp);
getreport.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent it=new Intent(getApplicationContext(),VoiceReport.class);
        startActivity(it);
    }
});
        launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // set the data
                    String uri = "tel:" + number0.getText().toString();
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Your call has failed...",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //*************************************************************************
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            String getName = (String) bd.get("state");
            //txtView.setText(getName);

        if(getName.equals("SUCCESS")){
            StringBuffer sb = new StringBuffer();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Cursor cur = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
            int number = cur.getColumnIndex(CallLog.Calls.NUMBER);
            int duration = cur.getColumnIndex(CallLog.Calls.DURATION);
            int typee = cur.getColumnIndex(CallLog.Calls.TYPE);
             dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
             date = new Date();
         final  String dt=dateFormat.format(date);
            final String ress=getName;

            while ( cur.moveToNext() ) {
                phNumber = cur.getString( number );
                 callDuration = cur.getString( duration );
                Calltype = cur.getString(typee);
                break;
            }
            cur.close();
           //envoi de données ............................

if(Calltype.equals("1")) rstype="incoming"; else if(Calltype.equals("2")) rstype="outgoing"; else if(Calltype.equals("3"))rstype="missed";

            final     ProgressDialog   pdialog=new ProgressDialog(VoiceTest.this);
            pdialog.setIndeterminate(true);
            pdialog.setMessage("Please Wait ....");
            pdialog.show();
            adapter = new RestAdapter.Builder().setEndpoint(ROOT_URL).build(); //Finally building the adapter
            RegisterApi api = adapter.create(RegisterApi.class);
            api.insertcalldata(phNumber,rstype, "00:"+callDuration, dt, ress, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    //On succes we will read the server's output using bufferedreaders
                    //Creating a bufferedreader object
                    BufferedReader reader = null;
                    //An string to store output from the server
                    String output = "";
                    try {
                        //Initializing buffered reader
                        reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                        //Reading the output in the string
                        output = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Displaying the output as a toast
                    Toast.makeText(VoiceTest.this, output, Toast.LENGTH_LONG).show();
                    if (pdialog.isShowing())
                        pdialog.dismiss();
                    Intent it=new Intent(getApplicationContext(),VoiceReport.class);
                   /* it.putExtra("number",phNumber);
                    it.putExtra("calltype",rstype);
                    it.putExtra("callduration","00:"+callDuration);
                    it.putExtra("date",dt);
                    it.putExtra("result",ress);*/
                   startActivity(it);
                }
                @Override
                public void failure(RetrofitError error) {
                }
            });





            //**********************************************



        }
        }
        //******************************************************************************************
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
        getMenuInflater().inflate(R.menu.voice_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            Intent it=new Intent(getApplicationContext(),VoiceTest.class);
            startActivity(it);
        } else if (id == R.id.data) {
            Intent it=new Intent(getApplicationContext(),DataTest.class);
            startActivity(it);
        } else if (id == R.id.gmap) {
            Intent it=new Intent(getApplicationContext(),CarteGoogleMap.class);
            startActivity(it);
        } else if (id == R.id.arcgis) {
            Intent it=new Intent(getApplicationContext(),ArcGISMap.class);
            startActivity(it);

        } else if (id == R.id.cellsettings) {
            Intent it=new Intent(getApplicationContext(),CellSettings.class);
            startActivity(it);
        } else if (id == R.id.realtime) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private class MyPhoneListener extends PhoneStateListener {

        private boolean onCall = false;

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // phone ringing...
                    Toast.makeText(VoiceTest.this, incomingNumber + " calls you",
                            Toast.LENGTH_LONG).show();
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // one call exists that is dialing, active, or on hold
                    Toast.makeText(VoiceTest.this, "on call...",
                            Toast.LENGTH_LONG).show();
                    //because user answers the incoming call
                    onCall = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // in initialization of the class and at the end of phone call

                    // detect flag from CALL_STATE_OFFHOOK
                    if (onCall == true) {
                        Toast.makeText(VoiceTest.this, "restart app after call", Toast.LENGTH_LONG).show();
                        statee = "SUCCESS";
                        // restart our application
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setComponent(new ComponentName("dhafer.tunisietelecom","dhafer.tunisietelecom.VoiceTest"));
                        intent.putExtra("state","SUCCESS");
                        startActivity(intent);
                        returntoapp=1;
                        onCall = false;
                    }
                    break;
                default:
                    statee = "FAILED";
                    break;
            }
        }
    }
}

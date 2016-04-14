package dhafer.tunisietelecom;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddCellular extends Activity {
    private TelephonyManager mTelephonyManager;
    TelephonyManager tManager ;
    EditText editcellid,editlat,editlong,editmcc,editmnc,editlac,editop,editkey;
    ImageView launch,addtower,statgps;
    GPSTracker gps;
    double longitude,latitude,mnc;
    int cid,lac_val;
    boolean possibility;
Thread tavgup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cellular);
        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
editcellid=(EditText)findViewById(R.id.editcellid);
        editlat=(EditText)findViewById(R.id.editlat);
        editlong=(EditText)findViewById(R.id.editlong);
        editmcc=(EditText)findViewById(R.id.editmcc);
        editmnc=(EditText)findViewById(R.id.editmnc);
        editlac=(EditText)findViewById(R.id.editlac);
        editop=(EditText)findViewById(R.id.editop);
        editkey=(EditText)findViewById(R.id.editkey);
        launch=(ImageView)findViewById(R.id.searchtower);
        addtower=(ImageView)findViewById(R.id.addingTower);
        addtower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(possibility){
                    int mncc;
                    String key,lat,longi,mccc,lac,cellidd;
                    key=editkey.getText().toString();
                    lat=editlat.getText().toString();
                    longi=editlong.getText().toString();
                    mccc=editmcc.getText().toString();
                    mncc=Integer.parseInt(editmnc.getText().toString());
                    lac=editlac.getText().toString();
                    cellidd=editcellid.getText().toString();

                    new RequestTask().execute("http://opencellid.org/measure/add?key="+key+"&lat="+lat+"&lon="+longi+"&mcc="+mccc+"&mnc="+mncc+"&lac="+lac+"&cellid="+cellidd+"&act=UMTS");


                    }
            }
        });
        statgps=(ImageView)findViewById(R.id.stategps);
        launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                //int networkType = tManager.getNetworkType();
                String net_operator = tManager.getNetworkOperator();
                editmnc.setText(net_operator.substring(3));
                editmcc.setText(net_operator.substring(0,3));
                String carrierName = tManager.getNetworkOperatorName();
                editop.setText(carrierName);
                GsmCellLocation cell_location = (GsmCellLocation) tManager.getCellLocation();
//-----------------------------
//************

                tavgup = new Thread() {
                    @Override
                    public void run() {
                        try {
                            while (!isInterrupted()) {

                                Thread.sleep(1000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        gps = new GPSTracker(AddCellular.this);
                                        // check if GPS enabled
                                        if(gps.canGetLocation()){
                                            latitude = gps.getLatitude();
                                            longitude = gps.getLongitude();
                                            editlong.setText(String.valueOf(longitude));
                                            editlat.setText(String.valueOf(latitude));
                                            if(latitude==0&&longitude==0){
                                                statgps.setImageResource(R.drawable.gps_statwrong);
                                                possibility=false;
                                            }else{
                                                statgps.setImageResource(R.drawable.gps_state_good);
                                                possibility=true;
                                            }
                                        }else{
                                            statgps.setImageResource(R.drawable.gps_statwrong);
                                            possibility=false;
                                        }

                                    }});}}catch (InterruptedException e) {
                        }
                    }
                };tavgup.start();

                //**********

                //---------------------------
                cid = cell_location.getCid();
                editcellid.setText(String.valueOf(cid));
                lac_val = cell_location.getLac();
                editlac.setText(String.valueOf(lac_val));

            }
        });
    }
    //***************************************************************************************************************************
    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
        }
    }





}

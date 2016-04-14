package dhafer.tunisietelecom;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ArcGISMap extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
String variableJSON="";
    private TelephonyManager mTelephonyManager;
    TelephonyManager tManager ;
    double longi,lati;
    private GoogleMap googleMap;
    GPSTracker gps;
    Thread tavgup;
    TextView inf;
    int cid,lac_val;
    private ArrayList<LatLng> arrayPoints=null;
    Polyline polylineOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arc_gismap);
        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inf=(TextView)findViewById(R.id.inftower);
        initilizeMap();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.coveragemap)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(35,10)).zoom(8).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//--------------------------------------
            tavgup = new Thread() {
                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {

                            Thread.sleep(3000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                                    String net_operator = tManager.getNetworkOperator();
                                    String mnc=net_operator.substring(3);
                                    String mcc=net_operator.substring(0,3);
                                    GsmCellLocation cell_location = (GsmCellLocation) tManager.getCellLocation();
                                    int cid=cell_location.getCid();
                                    lac_val = cell_location.getLac();
                                    new RequestTask().execute("http://opencellid.org/cell/get?key=d44327c9-111b-4ff0-9708-a3ced4e0dc64&mcc="+mcc+"&mnc="+mnc+"&lac="+lac_val+"&cellid="+String.valueOf(cid)+"&format=json");
                                    googleMap.clear();

                                }});}}catch (InterruptedException e) {
                    }
                }
            };tavgup.start();
            //***********************



        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }
    @Override
    protected void onPause() {
        super.onPause();

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
        getMenuInflater().inflate(R.menu.arc_gismap, menu);
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
            // Handle the camera action
        } else if (id == R.id.data) {

        } else if (id == R.id.gmap) {
            Intent it=new Intent(getApplicationContext(),CarteGoogleMap.class);
            startActivity(it);
        } else if (id == R.id.arcgis) {
            Intent it=new Intent(getApplicationContext(),ArcGISMap.class);
            startActivity(it);

        } else if (id == R.id.cellsettings) {

        } else if (id == R.id.realtime) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //******************************************************
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
            variableJSON=result;


            try {
            JSONObject jsonRootObject = new JSONObject(variableJSON);
                Double longitudeTower,latitudeTower;
                longitudeTower=Double.parseDouble(jsonRootObject.getString("lon"));
                latitudeTower=Double.parseDouble(jsonRootObject.getString("lat"));
               inf.setText("     Tower: "+jsonRootObject.getString("cid")+" Lac: "+jsonRootObject.getString("lac")+" \n Cell Id: "+jsonRootObject.getString("cellid")+"average Signal: "+jsonRootObject.getString("averageSignalStrength"));
             //  Toast.makeText(getApplicationContext(),jsonRootObject.getString("cid"),Toast.LENGTH_LONG).show();
                MarkerOptions markertower = new MarkerOptions().position(new LatLng(latitudeTower, longitudeTower)).title("Tower Position");
                markertower.icon(BitmapDescriptorFactory.fromResource(R.drawable.towermarker));
                googleMap.addMarker(markertower);
                gps = new GPSTracker(ArcGISMap.this);
                // check if GPS enabled
                if(gps.canGetLocation()){
                    lati = gps.getLatitude();
                    longi= gps.getLongitude();
                    MarkerOptions maposition = new MarkerOptions().position(new LatLng(lati, longi)).title("My position");
                    maposition.icon(BitmapDescriptorFactory.fromResource(R.drawable.maposition));
                    googleMap.addMarker(maposition);
                    polylineOptions = googleMap.addPolyline(new PolylineOptions().add(new LatLng(lati, longi),new LatLng(latitudeTower, longitudeTower)).width(5).color(Color.RED));
                }
            } catch (JSONException e) {e.printStackTrace();}


        }
    }



}

package dhafer.tunisietelecom;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
public class CarteGoogleMap extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnChartGestureListener, OnChartValueSelectedListener {
    private GoogleMap googleMap;
    private GPSTracker gps;
    private LineChart mChart;
    int i=0,j=0;
    Double latitude,longitude;
    TelephonyManager TelephonManager;
    myPhoneStateListener pslistener;
    Thread th,t;
    int SignalStrength = 0;
    int variable=0;
    private static String url = "https://maps.googleapis.com/maps/api/elevation/json?locations=";
    private static String url2="";
    private static final String resultjson = "results";
    private static final String tagelevation = "elevation";
    JSONArray user = null;
    ArrayList<String> xVals = new ArrayList<String>();
    ArrayList<Entry> yVals = new ArrayList<Entry>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelephonManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_carte_google_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");
        mChart.setNoDataTextDescription("Loading Wait ...");
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);
        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        LimitLine ll1 = new LimitLine(130f, "Elevation Profil - Tunisie Telecom-");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.setAxisMaxValue(250f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setDrawLimitLinesBehindData(true);
        mChart.setBackground(getResources().getDrawable(R.drawable.bg2tst));
        mChart.getAxisRight().setEnabled(false);
        setData();
        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initilizeMap();
    }

    private void initilizeMap() {
       if (googleMap == null) {
           googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
           gps = new GPSTracker(CarteGoogleMap.this);
           if (gps.canGetLocation()) {
               double lati = gps.getLatitude();
               double longi = gps.getLongitude();
               CameraPosition cameraPosition = new CameraPosition.Builder().target(
                       new LatLng(lati, longi)).zoom(12).build();
               googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
               googleMap.setMyLocationEnabled(true);
               googleMap.getUiSettings().setZoomControlsEnabled(true);
               googleMap.getUiSettings().setZoomGesturesEnabled(true);
               googleMap.getUiSettings().setRotateGesturesEnabled(true);
               googleMap.getUiSettings().setMyLocationButtonEnabled(true);
               googleMap.getUiSettings().setMapToolbarEnabled(true);
           }
           th = new Thread() {

               @Override
               public void run() {
                   try {
                       while (!isInterrupted()) {
                           Thread.sleep(1000);
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   pslistener = new myPhoneStateListener();
                                   TelephonManager.listen(pslistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                                   variable = SignalStrength;
                                   Log.i("variable:", "" + variable);
                                   gps = new GPSTracker(CarteGoogleMap.this);
                                   if (gps.canGetLocation()) {
                                       latitude = gps.getLatitude();
                                       longitude = gps.getLongitude();
                                   }
                                   //************** 90 **************************
                                   if (variable <= (-90)) {
                                       // ln=ln+0.2;
                                       MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Puissance du signal: " + variable + "dBm");
                                       marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.sig1));
                                       Log.i("position:", "" + latitude + "long" + latitude);
                                       googleMap.addMarker(marker);
                                       //******************* 80 *******************
                                   } else if (variable > (-90) && variable <= (-80)) {

                                       MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Puissance du signal: " + variable + "dBm");
                                       marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.sig2));
                                       Log.i("position:", "" + latitude + "long" + latitude);
                                       googleMap.addMarker(marker);
                                       //********* 70 ********************
                                   } else if (variable > (-80) && variable <= (-70)) {

                                       MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Puissance du signal: " + variable + "dBm");
                                       marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.sig3));
                                       Log.i("position:", "" + latitude + "long" + latitude);
                                       googleMap.addMarker(marker);
                                   }
                                   //*********** 60 *************
                                   else if (variable > (-70) && variable <= (-60)) {
                                       // ln=ln+0.2;
                                       MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Puissance du signal: " + variable + "dBm");
                                       marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.sig4));
                                       Log.i("position:", "" + latitude + "long" + latitude);
                                       googleMap.addMarker(marker);
                                   } else if (variable > (-60) && variable <= (-50)) {

                                       MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Puissance du signal: " + variable + "dBm");
                                       marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.sig5));
                                       Log.i("position:", "" + latitude + "long" + latitude);
                                       googleMap.addMarker(marker);
                                   }
                               }
                           });
                       }
                   } catch (InterruptedException e) {
                   }
               }
           };
           th.start();
       }
    }
    private void setData() {



        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                if(gps.canGetLocation()){

                                    latitude = gps.getLatitude();
                                    longitude = gps.getLongitude();
                                    url2=url+latitude+","+longitude+"&key=AIzaSyCPs7uvfw7TWlvRgqyFPjAbXV_9yMqrS4I";
                                    new RetrieveMessages().execute(url2);
                                }

                            }});}}catch (InterruptedException e) {
                }
            }
        };
        t.start();
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
        getMenuInflater().inflate(R.menu.carte_google_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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

        } else if (id == R.id.arcgis) {

        } else if (id == R.id.cellsettings) {
            t.interrupt();
            Intent it=new Intent(getApplicationContext(),CellSettings.class);
            startActivity(it);
        } else if (id == R.id.realtime) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
    //**********************
    class myPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            SignalStrength = signalStrength.getGsmSignalStrength();
            SignalStrength = (2 * SignalStrength) - 113; // -> dBm
            Log.i("signal:", "" + SignalStrength);

        }

    }
    //************************
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
            float vv=Float.parseFloat(result);
            xVals.add(i++ + "");
            yVals.add(new Entry(vv,i++));
            LineDataSet set1 = new LineDataSet(yVals, "Elevation Value");
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            Drawable drawable = ContextCompat.getDrawable(CarteGoogleMap.this, R.drawable.fade_red);
            set1.setFillDrawable(drawable);
            set1.setDrawFilled(true);
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1);
            LineData data = new LineData(xVals, dataSets);
            mChart.setData(data);
            mChart.moveViewToX(i);
           // mChart.moveViewToY(vv, YAxis.AxisDependency.RIGHT);


        }
    }
}

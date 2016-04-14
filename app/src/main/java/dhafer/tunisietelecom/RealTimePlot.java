package dhafer.tunisietelecom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class RealTimePlot extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TelephonyManager TelephonManager;
    myPhoneStateListener pslistener;
    int SignalStrength = 0;
    int i=1;
    TextView txtsig;
    private LineGraphSeries<DataPoint> series;
Thread getsignal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_plot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtsig=(TextView)findViewById(R.id.sig);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle("- Signal Strength real Time -");
        graph.setTitleTextSize(20);
        graph.setCameraDistance(50);
        series = new LineGraphSeries<DataPoint>();

        graph.addSeries(series);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-110);
        viewport.setMaxY(-50);
        viewport.scrollToEnd();

        viewport.setScrollable(true);



        pslistener = new myPhoneStateListener();
        TelephonManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TelephonManager.listen(pslistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    // add random data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        txtsig.setText(SignalStrength+" dBm");
        series.appendData(new DataPoint(i++, SignalStrength), true, 50);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.parseColor("#bcd4f2"));

    }

    @Override
    protected void onStart() {
        super.onStart();
        getsignal=new Thread() {
            @Override
            public void run() {

                for (int i = 0; i < 1000; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
              }
            }
        };
        getsignal.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getsignal.interrupt();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getsignal.interrupt();
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
        getMenuInflater().inflate(R.menu.real_time_plot, menu);
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
        int id = item.getItemId();

        if (id == R.id.voice) {
           getsignal.interrupt();
            Intent it=new Intent(getApplicationContext(),VoiceTest.class);
            startActivity(it);
        } else if (id == R.id.data) {
            getsignal.interrupt();
            Intent it=new Intent(getApplicationContext(),DataTest.class);
            startActivity(it);
        } else if (id == R.id.gmap) {
            getsignal.interrupt();
            Intent it=new Intent(getApplicationContext(),CarteGoogleMap.class);
            startActivity(it);
        } else if (id == R.id.arcgis) {
            getsignal.interrupt();
            Intent it=new Intent(getApplicationContext(),ArcGISMap.class);
            startActivity(it);

        } else if (id == R.id.cellsettings) {
            getsignal.interrupt();
            Intent it=new Intent(getApplicationContext(),CellSettings.class);
            startActivity(it);
        } else if (id == R.id.realtime) {
            getsignal.interrupt();
            Intent it=new Intent(getApplicationContext(),RealTimePlot.class);
            startActivity(it);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     *
     *
     * class listenn
     */
    class myPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            SignalStrength = signalStrength.getGsmSignalStrength();
            SignalStrength = (2 * SignalStrength) - 113; // -> dBm
            Log.i("signal:", "" + SignalStrength);

        }

    }


}

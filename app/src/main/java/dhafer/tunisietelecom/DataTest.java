package dhafer.tunisietelecom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import MyUtils.Constants;
import networkmonitor.NetMonitorService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DataTest extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private long total_rcv;
    private long total_send;
    private long prev_upload_speed;
    private long prev_download_speed;
    private Handler mHandler,mHandler2;
    private ImageView needle;
    private ImageView needle2;
    SimpleDateFormat dateFormat;
    Date date;
    Context cc;
    static double tt;
    private int last_degree=0,cur_degree,sumppp=0;
    private int last_degree2=0,cur_degree2;
    private Info info;
    double scounterup=0,avggh;
    private byte[] imageBytes;
    private boolean flag;
   Intent startIntent;
    RestAdapter adapter;
    String server = "ftp.networksbox.net";
    int port = 21;
    String user = "networksca";
    String pass = "uNCSZKPx6qzq";
    public static final String ROOT_URL = "http://networksbox.net/tunisietelecom";
Thread tavgup;
    TextView pingvalue,downvaleur,upvaleur;
    ImageButton launch,getreport;
    private SpeedometerGauge speedometer;
     private Handler handler=new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            // TODO Auto-generated method stub
            if(msg.what==0x123)
            {
                downvaleur.setText(""+msg.arg1);
               //upvaleur.setText(msg.arg2 + "");
                startAnimation(msg.arg1);
            }
            if(msg.what==0x100)
            {
              // downvaleur.setText("0KB/S");
              startAnimation(0);
             //   btn.setText("开始测试");
              //  btn.setEnabled(true);

            }
        }

    };
//******************* Handler 2
private Handler handler2=new Handler()
{

    @Override
    public void handleMessage(Message msg)
    {
        // TODO Auto-generated method stub
        if(msg.what==0x123)
        {
            //downvaleur.setText(""+msg.arg1);
          //  upvaleur.setText(msg.arg2 + "");
            startAnimation2(msg.arg1);
        }
        if(msg.what==0x100)
        {
            // downvaleur.setText("0KB/S");
           startAnimation2(0);
            //   btn.setText("开始测试");
            //  btn.setEnabled(true);

        }
    }

};



    //*****************




    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        flag=true;
        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        needle=(ImageView) findViewById(R.id.needle);
        needle2=(ImageView) findViewById(R.id.needle2);
        total_rcv = TrafficStats.getTotalRxBytes();
        total_send = TrafficStats.getTotalTxBytes();
        prev_download_speed = -1;
        prev_upload_speed = -1;
        mHandler = new Handler();
        downvaleur=(TextView)findViewById(R.id.valuedownlink);
        upvaleur=(TextView)findViewById(R.id.valueuplink);
        pingvalue=(TextView)findViewById(R.id.valueping);
        info=new Info();
        launch =(ImageButton)findViewById(R.id.lchdata);
        getreport =(ImageButton)findViewById(R.id.rpdata);
        getreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(getApplicationContext(),ReportData.class);
                startActivity(it);
            }
        });
        speedometer = (SpeedometerGauge)findViewById(R.id.speedometer);
launch.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        new PingTask().execute("www.tunisietelecom.tn", "80");
        info.hadfinishByte=0;
        info.speed=0;
        info.totalByte=1024;
        avgupp();
        new UploadFile().execute();
        new DownloadThread().start();
        new GetInfoThread().start();

    }
});
        speedometer.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });
        speedometer.setLabelTextSize(11);
        speedometer.setMaxSpeed(1200);
        speedometer.setMajorTickStep(100);
        speedometer.setMinorTicks(50);
        speedometer.addColoredRange(0, 250, Color.GREEN);
        speedometer.addColoredRange(250, 600, Color.YELLOW);
        speedometer.addColoredRange(600,1200, Color.RED);
        speedometer.setSpeed(0, 1000, 300);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onBackPressed() {
        flag=false;
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
        getMenuInflater().inflate(R.menu.data_test, menu);
        return true;
    }
//****** mesure de traffic
private Runnable runnable = new Runnable() {
    @Override
    public void run() {
        //do the job
        final long download_speed =  (TrafficStats.getTotalRxBytes()-total_rcv)/1024;
        final long upload_speed = (TrafficStats.getTotalTxBytes()-total_send)/1024;
        if((prev_download_speed!= download_speed || prev_upload_speed != upload_speed) ){
            prev_download_speed = download_speed;
            prev_upload_speed = upload_speed;
           // textView.setText( String.format("D :%4s KB/S%4sU :%4s KB/S", download_speed, "", upload_speed) );
        }
        total_rcv = TrafficStats.getTotalRxBytes();
        total_send = TrafficStats.getTotalTxBytes();
        if(mHandler!=null){
            mHandler.postDelayed(this,1000L*1); /*every 1 second*/
        }
    }
};
void avgupp(){
    tavgup = new Thread() {
        @Override
        public void run() {


            try
            {



                while (!isInterrupted()) {

                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final long upload_speed = (TrafficStats.getTotalTxBytes()-total_send)/1024;
                        scounterup++;
                            sumppp+=upload_speed;
                            avggh=sumppp/scounterup;
                            upvaleur.setText(""+(int)avggh);
                            total_send = TrafficStats.getTotalTxBytes();
                            Message msg=new Message();
                            msg.arg1=(int)upload_speed;
                            msg.what=0x123;
                            handler2.sendMessage(msg);



                        }});}}catch (InterruptedException e) {
            }
        }
    };tavgup.start();


    }


    //***********************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.trafficstat) {
            //********************** Traffic stats --------------------
            startIntent = new Intent(getApplicationContext(), NetMonitorService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
           startService(startIntent);

            return true;
        }
        else if (id == R.id.killts) {
            //********************** kill  Traffic stats --------------------
            stopService(startIntent);

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
    private class PingTask extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... params) {


            String url = params[0];
            int port =  Integer.parseInt(params[1]);
            boolean success = false;

            try {
                success = pingURL(url, port);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return success;
        }

        protected void onPostExecute(Boolean result) {
            Toast.makeText(getApplicationContext(),"Ping RTT: "+tt,Toast.LENGTH_LONG).show();
            speedometer.setSpeed(tt, 1000, 300);
            pingvalue.setText(""+tt);
        }
    }
    public boolean pingURL(String hostname, int port) throws UnknownHostException, IOException {
        boolean reachable = false;
        long finish =0;
        long start = new GregorianCalendar().getTimeInMillis();
        try (Socket socket = new Socket(hostname, port)) {
            socket.getLocalPort();
             finish =new GregorianCalendar().getTimeInMillis();
            tt=finish - start;
            reachable = true;
        }

        return reachable;
    }
    //*********************** Class Download ------
    class DownloadThread extends Thread {

        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            String url_string="http://networksbox.net/tunisietelecom/driver.zip";
            long start_time,cur_time;
            URL url;
            URLConnection connection;
            InputStream iStream;

            try
            {
                url=new URL(url_string);
                connection=url.openConnection();

                info.totalByte=connection.getContentLength();

                iStream=connection.getInputStream();
                start_time=System.currentTimeMillis();
                while(iStream.read()!=-1 && flag)
                {
                    info.hadfinishByte++;
                    cur_time=System.currentTimeMillis();
                    if(cur_time-start_time==0)
                    {
                        info.speed=1000;
                    }
                    else {
                        info.speed=info.hadfinishByte/(cur_time-start_time)*1000;
                    }
                }
                iStream.close();
            } catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    //---------------- second class download----
    class GetInfoThread extends Thread {

        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            double sum,counter,sumup,counterup;
            int cur_speed,ave_speed,avgup,curup;
            try
            {
                sum=0;
                counterup=0;
                sumup=0;
                counter=0;
                while(info.hadfinishByte<info.totalByte && flag)
                {
                    Thread.sleep(1000);
sumup+=info.speedup;
                    counterup++;

                    sum+=info.speed;
                    counter++;

                    cur_speed=(int) info.speed;
                    ave_speed=(int) (sum/counter);
                    avgup=(int)(sumup/counterup);
                    Log.e("Test", "cur_speed:" + info.speed / 1024 + "KB/S ave_speed:" + ave_speed / 1024);
                    //downvaleur.setText(""+info.speed / 1024);
                    final long download_speed =  (TrafficStats.getTotalRxBytes()-total_rcv)/1024;
                    final long upload_speed = (TrafficStats.getTotalTxBytes()-total_send)/1024;
                    if((prev_download_speed!= download_speed || prev_upload_speed != upload_speed) ){
                        prev_download_speed = download_speed;
                        prev_upload_speed = upload_speed;
                        // textView.setText( String.format("D :%4s KB/S%4sU :%4s KB/S", download_speed, "", upload_speed) );
                    }

                    Message msg=new Message();


                    msg.arg1=((int)info.speed/1024);
                   // msg.arg2=((int)ave_speed/1024);
                    msg.arg2=(int)upload_speed;
                    msg.what=0x123;
                    handler.sendMessage(msg);
                    total_rcv = TrafficStats.getTotalRxBytes();
                    total_send = TrafficStats.getTotalTxBytes();
                }
                if(info.hadfinishByte==info.totalByte && flag)
                {
                    handler.sendEmptyMessage(0x100);
                }
            } catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }
/****
 *
 * UploadClass -----------------------------------------------------*******************-----------------------------------------------------------------------------------------------------------------------
 */
private class UploadFile extends AsyncTask<Void, Integer, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(getApplicationContext(), "Début upload", Toast.LENGTH_LONG).show();
    }



    @Override
    protected Void doInBackground(Void... arg0) {

        File f = new File("/sdcard/apache.jar");
        //***************
        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory("/www/tunisietelecom/uploadfolder");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            String firstRemoteFile = "apache.jar";
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
        Toast.makeText(getApplicationContext(), " terminé", Toast.LENGTH_LONG).show();
        tavgup.interrupt();
        //*----------------------------------envoi de données-----------------------------
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        date = new Date();
        final  String dt=dateFormat.format(date);
        final ProgressDialog pdialog=new ProgressDialog(DataTest.this);
        pdialog.setIndeterminate(true);
        pdialog.setMessage("Please Wait ....");
        pdialog.show();
        adapter = new RestAdapter.Builder().setEndpoint(ROOT_URL).build(); //Finally building the adapter
        DataRegistreApi api = adapter.create(DataRegistreApi.class);
api.insertdatatest(dt, pingvalue.getText(), downvaleur.getText(), upvaleur.getText(), new Callback<Response>() {
    @Override
    public void success(Response response, Response response2) {
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
        Toast.makeText(DataTest.this, output, Toast.LENGTH_LONG).show();
        if (pdialog.isShowing())
            pdialog.dismiss();
       //Intent it=new Intent(getApplicationContext(),ReportData.class);
        //startActivity(it);
    }

    @Override
    public void failure(RetrofitError error) {

    }
});
        //*------------------------- fin envoi ---------------------------------------------
    }
}
// ***********  move needle 1-------------------------------------------------------------
private void startAnimation(int cur_speed)
{
    cur_degree=getDegree(cur_speed);

    RotateAnimation rotateAnimation=new RotateAnimation(last_degree, cur_degree, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
    rotateAnimation.setFillAfter(true);
    rotateAnimation.setDuration(1000);
    last_degree=cur_degree;
    needle.startAnimation(rotateAnimation);
}
private int getDegree(double cur_speed)
{
    int ret=0;
    if(cur_speed>=0 && cur_speed<=512)
    {
        ret=(int) (15.0*cur_speed/128.0);
    }
    else if(cur_speed>=512 && cur_speed<=1024)
    {
        ret=(int) (60+15.0*cur_speed/256.0);
    }
    else if(cur_speed>=1024 && cur_speed<=10*1024)
    {
        ret=(int) (90+15.0*cur_speed/1024.0);
    }else {
        ret=180;
    }
    return ret;
}
    // ***********  move needle 2-------------------------------------------------------------
    private void startAnimation2(int cur_speed)
    {
        cur_degree2=getDegree2(cur_speed);

        RotateAnimation rotateAnimation=new RotateAnimation(last_degree2, cur_degree2, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(1000);
        last_degree2=cur_degree2;
        needle2.startAnimation(rotateAnimation);
    }
    private int getDegree2(double cur_speed)
    {
        int ret=0;
        if(cur_speed>=0 && cur_speed<=512)
        {
            ret=(int) (15.0*cur_speed/128.0);
        }
        else if(cur_speed>=512 && cur_speed<=1024)
        {
            ret=(int) (60+15.0*cur_speed/256.0);
        }
        else if(cur_speed>=1024 && cur_speed<=10*1024)
        {
            ret=(int) (90+15.0*cur_speed/1024.0);
        }else {
            ret=180;
        }
        return ret;
    }


}

package networkmonitor;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.firebase.client.Firebase;

import MyUtils.Constants;

import dhafer.tunisietelecom.DataTest;
import dhafer.tunisietelecom.R;
import screenlistnerpkg.ScreenListner;


import static MyUtils.Constants.NOTIFICATION_ID.FOREGROUND_SERVICE;


public class NetMonitorService extends Service {
    public NetMonitorService() {
    }

    private WindowManager windowManager;
    LinearLayout layout;
    private TextView textView,title;
    private Handler mHandler;
    private long total_rcv;
    private long total_send;
    private long prev_upload_speed;
    private long prev_download_speed;
    private SeekBar seekBar;
    ImageView imgtt;
    Firebase mRef;
    //screen parameters
    final WindowManager.LayoutParams myParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    @Override
    public void onCreate() {
        super.onCreate();
        mRef=new Firebase("https://datapfe.firebaseio.com/");
        //55555555555555555555555
         layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setBackgroundResource(R.drawable.corner);
        layout.setAlpha(0.8f);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//*****

        imgtt=new ImageView(getApplicationContext());
        imgtt.setImageResource(R.mipmap.logotelecom);
        imgtt.setMaxWidth(400);
        imgtt.setPadding(2, 2, 2, 2);
        title = new TextView(getApplicationContext());
        title.setTextSize(12);

        title.setText("Traffics State");
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.CYAN);
        textView = new TextView(getApplicationContext());
        textView.setTextSize(13);
        textView.setPadding(5,5,0,1);
        textView.setTextColor(Color.parseColor("#303F9F"));
        textView.setMaxWidth(200);
        textView.setTypeface(null, Typeface.BOLD);
        //*********
        TextView titleView = new TextView(this);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleView.setLayoutParams(lparams);
        //  titleView.setTextAppearance(this, android.R.attr.textAppearanceLarge);
        titleView.setText("Hallo Welt!");
        layout.addView(imgtt);
        layout.addView(textView);
        //99999999999999999999999


        mHandler = new Handler();
        total_rcv = TrafficStats.getTotalRxBytes();
        total_send = TrafficStats.getTotalTxBytes();
        prev_download_speed = -1;
        prev_upload_speed = -1;

        windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        myParams.gravity = Gravity.TOP | Gravity.CENTER;
        windowManager.addView(layout, myParams);
        // windowManager.addView(imgtt, myParams);
        try {
            layout.setOnTouchListener(onTouchListener);


        } catch (Exception e) {
            e.printStackTrace();
        }


        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenListner();
        registerReceiver(mReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION) || intent.getAction().equals(Constants.ACTION.BOOT_RECEIVE)) {

            if(mHandler!=null){
                mHandler.removeCallbacks(runnable);
                mHandler.post(runnable);
            }

            Intent notificationIntent = new Intent(this, DataTest.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.logotelecom);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("Tunisie Telecom")
                    .setTicker("Tunisie Telecom")
                    .setContentText("Traffic Stats")
                    .setSmallIcon(R.mipmap.logotelecom)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent);

            Notification cur_notification = builder.build();
            startForeground(FOREGROUND_SERVICE, cur_notification);
        }else if(intent.getAction().equals(Constants.ACTION.START_REPEAT)){
            if(mHandler!=null){
                mHandler.post(runnable);
            }
        }else if(intent.getAction().equals(Constants.ACTION.STOP_REAP)){
            if(mHandler!=null){
                mHandler.removeCallbacks(runnable);
            }
        }else if(intent.getAction().equals(Constants.ACTION.UPDATE_FONT_SIZE)){
            // textView.setTextSize( Preference.getInstance(getApplicationContext()).getFont_size() );
        }
        return START_STICKY;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //do the job
            final long download_speed =  (TrafficStats.getTotalRxBytes()-total_rcv)/1024;
            final long upload_speed = (TrafficStats.getTotalTxBytes()-total_send)/1024;
            if(textView!=null && (prev_download_speed!= download_speed || prev_upload_speed != upload_speed) ){
                prev_download_speed = download_speed;
                prev_upload_speed = upload_speed;
                textView.setText( String.format("Downlink :%4s KB/S%4s Uplink :%4s KB/S", download_speed, "", upload_speed) );
                mRef.child("downlink").setValue(download_speed);
                mRef.child("uplink").setValue(upload_speed);
            }
            total_rcv = TrafficStats.getTotalRxBytes();
            total_send = TrafficStats.getTotalTxBytes();
            if(mHandler!=null){
                mHandler.postDelayed(this,1000L*1); /*every 1 second*/
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textView != null) {
            if (windowManager != null) {
                windowManager.removeView(layout);
                windowManager = null;
            }
            textView = null;
        }
        if(mHandler!=null){
            mHandler.removeCallbacks(runnable);
            mHandler=null;
        }
    }

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;
        private long touchStartTime = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (System.currentTimeMillis() - touchStartTime > ViewConfiguration.getLongPressTimeout() && initialTouchX == event.getX()) {
                windowManager.removeView(textView);
                stopSelf();
                return false;

            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStartTime = System.currentTimeMillis();
                    initialX = myParams.x;
                    initialY = myParams.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    myParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                    myParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(v, myParams);
                    break;
            }
            return false;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

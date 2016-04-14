package dhafer.tunisietelecom;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VoiceReport extends AppCompatActivity {
    TableLayout tl ;
    public static final String ROOT_URL = "http://networksbox.net/tunisietelecom";

    private List<CallReport> allreport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//--------------------------------------------------------
        tl = (TableLayout) findViewById(R.id.tablevoicerep);
        TableRow tr_head = new TableRow(this);
        tr_head.setId(10);
        tr_head.setBackgroundColor(R.color.couleur4);
        tr_head.setAlpha(0.9F);
        tr_head.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView label_date = new TextView(this);
        label_date.setTextSize(12);
        label_date.setId(20);
        label_date.setText("Number");
        label_date.setTextColor(Color.WHITE);
        label_date.setPadding(5, 5, 5, 5);
        tr_head.addView(label_date);// add the column to the table row here

        TextView label_weight_kg = new TextView(this);
        label_weight_kg.setTextSize(12);
        label_weight_kg.setId(21);// define id that must be unique
        label_weight_kg.setText("Type"); // set the text for the header
        label_weight_kg.setTextColor(Color.WHITE); // set the color
        label_weight_kg.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_weight_kg); // add the column to the table row here
        TextView label_weight_kg1 = new TextView(this);
label_weight_kg1.setTextSize(12);
        label_weight_kg1.setId(211);// define id that must be unique
        label_weight_kg1.setText("Duration"); // set the text for the header
        label_weight_kg1.setTextColor(Color.WHITE); // set the color
        label_weight_kg1.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_weight_kg1); // add the column to the table row here
        TextView label_weight_kg2 = new TextView(this);
label_weight_kg2.setTextSize(12);
        label_weight_kg2.setId(222);// define id that must be unique
        label_weight_kg2.setText("Date"); // set the text for the header
        label_weight_kg2.setTextColor(Color.WHITE); // set the color
        label_weight_kg2.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_weight_kg2); // add the column to the table row here
        //******
        TextView resul = new TextView(this);
resul.setTextSize(12);
        resul.setId(333);// define id that must be unique
        resul.setText("Result"); // set the text for the header
        resul.setTextColor(Color.WHITE); // set the color
        resul.setPadding(5,5,2,5);
        tr_head.addView(resul); // add the column to the table row here
        tl.addView(tr_head, new TableLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //-------------------------------------------------------------
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Other Test", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //****************************************************************************************************************************************************************************
        //While the app fetched data we are displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Fetching Data","Please wait...",false,false);

        //Creating a rest adapter
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(ROOT_URL).build();

        //Creating an object of our api interface
        GetVoiceCallApi api = adapter.create(GetVoiceCallApi.class);
        api.getreports(new Callback<List<CallReport>>() {
            @Override
            public void success(List<CallReport> callReports, Response response) {
                loading.dismiss();
                allreport = callReports;
                showreport();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }
    void showreport(){

        Integer count = 0;

String nb,tp,dr,dt,rs;

        for(int i=0; i<allreport.size(); i++) {
             nb =allreport.get(i).getNumber();
             tp = allreport.get(i).getType();
             dr =allreport.get(i).getDuration();
             dt =allreport.get(i).getDate();
             rs = allreport.get(i).getResult();
            Toast.makeText(getApplicationContext(),nb,Toast.LENGTH_LONG).show();
            //***************************************************
            TableRow tr = new TableRow(VoiceReport.this);
            tr.setAlpha(0.6F);
            if (count % 2 != 0) tr.setBackgroundColor(Color.GRAY);
            tr.setId(100 + count);
            tr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView numbertab = new TextView(VoiceReport.this);
            numbertab.setId(210 + count);
            numbertab.setText(nb);
            numbertab.setPadding(2, 0, 5, 0);
            numbertab.setTextColor(Color.BLUE);
            numbertab.setTextSize(9);
            tr.addView(numbertab);
            TextView typetab = new TextView(VoiceReport.this);
            typetab.setId(310 + count);
            typetab.setText(tp);
            typetab.setTextSize(9);
            typetab.setTextColor(Color.BLUE);
            tr.addView(typetab);
            TextView durationtab = new TextView(VoiceReport.this);
            durationtab.setId(410 + count);
            durationtab.setText(dr);
            durationtab.setTextSize(9);
            durationtab.setTextColor(Color.BLUE);
            tr.addView(durationtab);
            TextView datetab = new TextView(VoiceReport.this);
            datetab.setId(510 + count);
            datetab.setTextColor(Color.BLUE);
            datetab.setTextSize(9);
            datetab.setText(dt);
            tr.addView(datetab);
            TextView resultab = new TextView(VoiceReport.this);
            resultab.setId(610 + count);
            resultab.setTextSize(9);
            resultab.setTextColor(Color.BLUE);
            resultab.setText(rs);
            tr.addView(resultab);
            tl.addView(tr,1, new TableLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            count++;

        }
        //**********************************************************




    }
}

package dhafer.tunisietelecom;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ReportData extends AppCompatActivity implements OnChartValueSelectedListener {
    TableLayout tl ;
    private LineChart mChart;
    public static final String ROOT_URL = "http://networksbox.net/tunisietelecom";

    private List<DataReportObject> allreport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_data);
        mChart = (LineChart) findViewById(R.id.chartdata);
//---------------------------------------------------------
        tl = (TableLayout) findViewById(R.id.tabledatarep);
        TableRow tr_head = new TableRow(this);
        tr_head.setId(10);
        tr_head.setBackgroundColor(Color.parseColor("#FF37383B"));
        tr_head.setAlpha(0.8F);
        tr_head.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView label_date = new TextView(this);
        label_date.setTextSize(15);
        label_date.setId(20);
        label_date.setText("Date");
        label_date.setTextColor(Color.WHITE);
        label_date.setPadding(5, 5, 5, 5);
        tr_head.addView(label_date);// add the column to the table row here
        TextView label_weight_kg = new TextView(this);
        label_weight_kg.setTextSize(15);
        label_weight_kg.setId(21);// define id that must be unique
        label_weight_kg.setText("Ping(ms)"); // set the text for the header
        label_weight_kg.setTextColor(Color.WHITE); // set the color
        label_weight_kg.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_weight_kg); // add the column to the table row here
        TextView label_weight_kg1 = new TextView(this);
        label_weight_kg1.setTextSize(15);
        label_weight_kg1.setId(211);// define id that must be unique
        label_weight_kg1.setText("Downlink"); // set the text for the header
        label_weight_kg1.setTextColor(Color.WHITE); // set the color
        label_weight_kg1.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_weight_kg1); // add the column to the table row here
        TextView label_weight_kg2 = new TextView(this);
        label_weight_kg2.setTextSize(15);
        label_weight_kg2.setId(222);// define id that must be unique
        label_weight_kg2.setText("Uplink"); // set the text for the header
        label_weight_kg2.setTextColor(Color.WHITE); // set the color
        label_weight_kg2.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_weight_kg2); // add the column to the table row here
        //******
        tl.addView(tr_head, new TableLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//**********---------------***************----------***********---------***********--------********---------*********---------*****------
        //While the app fetched data we are displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Fetching Data","Please wait...",false,false);

        //Creating a rest adapter
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(ROOT_URL).build();

        //Creating an object of our api interface
        GetDataApi api = adapter.create(GetDataApi.class);
        api.getreports(new Callback<List<DataReportObject>>() {
            @Override
            public void success(List<DataReportObject> callReports, Response response) {
                loading.dismiss();
                allreport = callReports;
                showreport();
                //***************
                setData();

                mChart.animateX(2500);

                Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

                // get the legend (only possible after setting data)
                Legend l = mChart.getLegend();

                // modify the legend ...
                // l.setPosition(LegendPosition.LEFT_OF_CHART);
                l.setForm(Legend.LegendForm.LINE);
                l.setTypeface(tf);
                l.setTextSize(11f);
                l.setTextColor(Color.WHITE);
                l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
//        l.setYOffset(11f);

                XAxis xAxis = mChart.getXAxis();
                xAxis.setTypeface(tf);
                xAxis.setTextSize(12f);
                xAxis.setTextColor(Color.WHITE);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setSpaceBetweenLabels(1);

                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setTypeface(tf);
                leftAxis.setTextColor(ColorTemplate.getHoloBlue());
                leftAxis.setAxisMaxValue(1000f);
                leftAxis.setAxisMinValue(0f);
                leftAxis.setDrawGridLines(true);

                YAxis rightAxis = mChart.getAxisRight();
                rightAxis.setTypeface(tf);
                rightAxis.setTextColor(Color.RED);
                rightAxis.setAxisMaxValue(1000);
                rightAxis.setAxisMinValue(0);
                rightAxis.setDrawGridLines(false);
                rightAxis.setDrawZeroLine(false);

                //*****************
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
//******************** chart ****
     mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);
        mChart.setAlpha(0.8f);
        // add data
   //!!!!!!!!!!!!!
    }
    //********** Data for Chart ******************
    /**
     * Méthode data for chart
     */
    private void setData() {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
        String downrepp2,uprepp2;

        for(int i=0; i<allreport.size(); i++) {
            downrepp2 =allreport.get(i).getDownlink();
            uprepp2 =allreport.get(i).getUplink();
        //-**************************-****************
            xVals.add((i) + "");
            float val = Float.parseFloat(downrepp2) ;
            yVals1.add(new Entry(val, i));
            float val2 = Float.parseFloat(uprepp2) ;
            yVals2.add(new Entry(val2, i));
        }


        LineDataSet set1 = new LineDataSet(yVals1, "Download Values");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.WHITE);
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        LineDataSet set2 = new LineDataSet(yVals2, "Upload Values");
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.WHITE);
        set2.setLineWidth(2f);
        set2.setCircleRadius(3f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircleHole(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set2);
        dataSets.add(set1);
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        mChart.setData(data);


    }



        //********** End DATA for CHART ********
    //******** show report methode *********
    void showreport(){
        Integer count = 0;
        String datee,pingg,downrepp,uprepp;
        for(int i=0; i<allreport.size(); i++) {
            datee =allreport.get(i).getDate();
            pingg = allreport.get(i).getPing();
            downrepp =allreport.get(i).getDownlink();
            uprepp =allreport.get(i).getUplink();
            //***************************************************
            TableRow tr = new TableRow(ReportData.this);
            tr.setAlpha(0.6F);
            if (count % 2 != 0) tr.setBackgroundColor(Color.GRAY);
            tr.setId(100 + count);
            tr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView datetab = new TextView(ReportData.this);
            datetab.setId(210 + count);
            datetab.setText(datee);
            datetab.setPadding(2, 0, 5, 0);
            datetab.setTextColor(Color.parseColor("#303F9F"));
            datetab.setTextSize(11);
            tr.addView(datetab);
            TextView pingtab = new TextView(ReportData.this);
            pingtab.setId(310 + count);
            pingtab.setText(pingg);
            pingtab.setTextSize(11);
            pingtab.setTextColor(Color.parseColor("#303F9F"));
            tr.addView(pingtab);
            TextView downtab = new TextView(ReportData.this);
            downtab.setId(410 + count);
            downtab.setText(downrepp);
            downtab.setTextSize(11);
            downtab.setTextColor(Color.parseColor("#303F9F"));
            tr.addView(downtab);
            TextView uptab = new TextView(ReportData.this);
            uptab.setId(510 + count);
            uptab.setTextColor(Color.parseColor("#303F9F"));
            uptab.setTextSize(11);
            uptab.setText(uprepp);
            tr.addView(uptab);
            tl.addView(tr,1, new TableLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            count++;
        }
    //**************************************

}

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        mChart.centerViewToAnimated(e.getXIndex(), e.getVal(), mChart.getData().getDataSetByIndex(dataSetIndex).getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() {

    }
}

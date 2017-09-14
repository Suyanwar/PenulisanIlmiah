package com.example.root.penulisanilmiah;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticActivity extends AppCompatActivity {
    public String API_KEY, varID;
    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public LineChart chart;
    public String [] campur;
    TextView tv_judul;
    public int cahayamin, cahayamax;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        Intent a = getIntent();
        API_KEY = a.getStringExtra("api");
        varID = a.getStringExtra("varID");
        campur = a.getStringArrayExtra("campur");
        cahayamin = a.getIntExtra("cahayamin", 0);
        cahayamax = a.getIntExtra("cahayamax", 100);
        chart = (LineChart)findViewById(R.id.chartss);
        tv_judul = (TextView)findViewById(R.id.tv_judul);
        tv_judul.setText("CAHAYA");
        initChart(chart);
        (new UbidotsClient()).handleUbidots(varID, API_KEY, new UbidotsClient.UbiListener() {
            @Override
            public void onDataReady(List<UbidotsClient.Value> result) {
                List<Entry> entries = new ArrayList();
                List<String> labels = new ArrayList<>();
                for (int i = 0; i < result.size(); i++){
                    Entry be = new Entry(result.get(i).value, i);
                    entries.add(be);
                    Date d = new Date(result.get(i).timestamp);
                    labels.add(sdf.format(d));
                }
                LineDataSet lse = new LineDataSet(entries, "CAHAYA");
                lse.setDrawHighlightIndicators(false);
                lse.setDrawValues(false);
                lse.setColor(Color.RED);
                lse.setCircleColor(Color.RED);
                lse.setLineWidth(1f);
                lse.setCircleSize(3f);
                lse.setDrawCircleHole(false);
                lse.setFillAlpha(65);
                lse.setFillColor(Color.RED);

                LineData ld = new LineData(labels, lse);
                chart.setData(ld);
                Handler handler = new Handler(StatisticActivity.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        chart.invalidate();
                    }
                });
            }
        });
    }
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
    private void initChart(LineChart chart) {
        chart.setTouchEnabled(true);
        chart.setDrawGridBackground(true);
        chart.getAxisRight().setEnabled(false);
        chart.setDrawGridBackground(true);
        LimitLine ll1 = new LimitLine(cahayamax, "Cahaya Maximum");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        LimitLine ll2 = new LimitLine(cahayamin, "Cahaya Minimum");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll2.setTextSize(10f);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMaxValue(1000F);
        leftAxis.setAxisMinValue(0F);
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisLineWidth(2);
        leftAxis.addLimitLine(ll2);
        leftAxis.addLimitLine(ll1);
        leftAxis.setDrawGridLines(true);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
    }
    @Override
    public void onBackPressed() {
        Intent back = new Intent(StatisticActivity.this, MainActivity.class);
        back.putExtra("key", campur[0]);
        back.putExtra("token", campur[1]);
        back.putExtra("id_device", campur[2]);
        finish();
        startActivity(back);
    }
}

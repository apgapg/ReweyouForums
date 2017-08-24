package in.reweyou.reweyouforums.civil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.R;

public class SieveAnalysis extends AppCompatActivity {

    private EditText[] e;
    private List<Float> list = new ArrayList<>();
    private float totalweight;

    private DecimalFormat df = new DecimalFormat("#.0");
    private LineChart chart;
    private Float temp = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sieve_analysis);
        chart = (LineChart) findViewById(R.id.chart);

        e = new EditText[8];

        e[0] = (EditText) findViewById(R.id.e1);
        e[1] = (EditText) findViewById(R.id.e2);
        e[2] = (EditText) findViewById(R.id.e3);
        e[3] = (EditText) findViewById(R.id.e4);
        e[4] = (EditText) findViewById(R.id.e5);
        e[5] = (EditText) findViewById(R.id.e6);
        e[6] = (EditText) findViewById(R.id.e7);
        e[7] = (EditText) findViewById(R.id.e8);
        findViewById(R.id.plot_graph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                list.clear();
                chart.setVisibility(View.VISIBLE);

                for (int i = 0; i < e.length; i++) {
                    totalweight = totalweight + Float.valueOf(e[i].getText().toString());
                }

                for (int i = 0; i < e.length; i++) {
                    if (e[i].getText().toString().trim().length() == 0) {
                        Toast.makeText(SieveAnalysis.this, "One or more field is empty!", Toast.LENGTH_SHORT).show();
                        list.clear();
                        break;
                    } else {
                        temp = temp + Float.valueOf(e[i].getText().toString());
                        list.add((1 - (temp / totalweight)) * 100);
                    }
                }


                if (!list.isEmpty()) {


                    List<Entry> entries = new ArrayList<Entry>();

                    // turn your data into Entry objects
                    entries.add(new Entry((float) 1.8, list.get(7)));
                    entries.add(new Entry((float) 2.1, list.get(6)));
                    entries.add(new Entry((float) 2.3, list.get(5)));
                    entries.add(new Entry((float) 2.6, list.get(4)));
                    entries.add(new Entry((float) 2.7, list.get(3)));
                    entries.add(new Entry((float) 3.0, list.get(2)));
                    entries.add(new Entry((float) 3.3, list.get(1)));
                    entries.add(new Entry((float) 3.6, list.get(0)));

                    LineDataSet dataSet = new LineDataSet(entries, "GSD");
                    dataSet.setColor(getResources().getColor(R.color.colorAccent));
                    dataSet.setLineWidth(2.0f);
                    dataSet.setDrawFilled(true);
                    dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    LineData lineData = new LineData(dataSet);
                    XAxis xAxis = chart.getXAxis();
                    xAxis.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return df.format(Math.pow(10, value) / 1000);
                        }
                    });
                    chart.setData(lineData);
                    chart.invalidate();
                }
            }
        });

    }
}

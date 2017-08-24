package in.reweyou.reweyouforums.civil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.R;

public class BeamDesignActivity extends AppCompatActivity {
    private float b;
    private float D;
    private float d;
    private float l_ef;
    private float deadLoad, liveLoad, beamselfweight;
    private float w;
    private float Mu;
    private float fck, fy;
    private float Ast;
    private float Ast_rqd;
    private float R_value;
    private float pt;
    private Spinner spinnerconcrete;
    private Spinner spinnersteel;
    private DecimalFormat df = new DecimalFormat("#.0");
    private String TAG = BeamDesignActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam_design);

        spinnerconcrete = (Spinner) findViewById(R.id.spinnerconcrete);
        spinnersteel = (Spinner) findViewById(R.id.spinnersteel);

        List<String> categoriesconcrete = new ArrayList<>();
        categoriesconcrete.add("20");
        categoriesconcrete.add("25");
        categoriesconcrete.add("30");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterconcrete = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesconcrete);

        // Drop down layout style - list view with radio button
        dataAdapterconcrete.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerconcrete.setAdapter(dataAdapterconcrete);
        spinnerconcrete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fck = Float.parseFloat(adapterView.getItemAtPosition(i).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        List<String> categoriessteel = new ArrayList<>();
        categoriessteel.add("250");
        categoriessteel.add("415");
        categoriessteel.add("500");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdaptersteel = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriessteel);

        // Drop down layout style - list view with radio button
        dataAdaptersteel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnersteel.setAdapter(dataAdaptersteel);
        spinnersteel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fy = Float.parseFloat(adapterView.getItemAtPosition(i).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        findViewById(R.id.calculate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b = Float.parseFloat(((EditText) findViewById(R.id.editbeamwidth)).getText().toString());
                D = Float.parseFloat(((EditText) findViewById(R.id.editbeamdepth)).getText().toString());
                l_ef = Float.parseFloat(((EditText) findViewById(R.id.editbeamspan)).getText().toString());
                deadLoad = Float.parseFloat(((EditText) findViewById(R.id.editbeamdeadload)).getText().toString());
                liveLoad = Float.parseFloat(((EditText) findViewById(R.id.editbeamliveload)).getText().toString());
                liveLoad = Float.parseFloat(((EditText) findViewById(R.id.editbeamliveload)).getText().toString());

                calculatebeamselfweight();
                calculatefactoredload();
                calculatefactoredMoment();

                calculate_Ast_reqd();
            }
        });


    }


    private void calculate_Ast_reqd() {
        d = D - 50;
        Log.d(TAG, "calculate_Ast_reqd: d: " + d);
        R_value = Mu / (b * d * d);
        Log.d(TAG, "calculate_Ast_reqd: " + R_value);
       /* float tempa = 0.87f * fy * fy / fck;
        float tempb = -0.87f * fy;
        float tempc = R_value;
        Log.d(TAG, "calculate_Ast_reqd: b: "+b);
        float tempd = (b * b) - (4 * tempa * tempc);
        Log.d(TAG, "calculate_Ast_reqd: tempd"+tempd);
        pt = (float) (((-tempb + Math.sqrt(tempd)) / (2 * tempa)) / 100);
*/

        double a = 1 - Math.sqrt(1 - (4.598 * R_value / fck));
        Log.d(TAG, "calculate_Ast_reqd: a: " + a);
        pt = (float) ((fck / (2 * fy)) * (a));
        Log.d(TAG, "calculate_Ast_reqd: pt: " + pt);
        Ast_rqd = pt * b * d;

        Log.d(TAG, "calculate_Ast_reqd: " + Ast_rqd);


        TextView steelarea = (TextView) findViewById(R.id.area);
        steelarea.setText("Area of steel required: " + df.format(Ast_rqd) + " sq.mm.");

        int bars = (int) (Ast_rqd / (3.14 * 20 * 20 / 4));
        TextView steelbars = (TextView) findViewById(R.id.bar);
        steelbars.setText("No. of bars of 20mm diameter: " + bars);

    }


    private void calculatebeamselfweight() {
        beamselfweight = 25 * (b / 1000) * (D / 1000);
        Log.d(TAG, "calculatebeamselfweight: " + beamselfweight);
    }

    private void calculatefactoredload() {
        w = 1.5f * (deadLoad + liveLoad + beamselfweight);
        Log.d(TAG, "calculatefactoredload: " + w);
    }

    private void calculatefactoredMoment() {
        Mu = w * l_ef * l_ef / 8;
        Log.d(TAG, "calculatefactoredMoment: " + Mu);
        Mu = Mu * 1000000;

    }


}

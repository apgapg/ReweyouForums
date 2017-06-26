package in.reweyou.reweyouforums;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class InsertTextActivity extends AppCompatActivity {

    private EditText editText;
    private boolean isEdittextEmpty;
    private TextView btnok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_text);


        btnok = (TextView) findViewById(R.id.btnok);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();


                if (isEdittextEmpty) {
                    i.putExtra("add", false);
                    i.putExtra("text", "");
                    setResult(RESULT_CANCELED);

                } else {
                    i.putExtra("add", true);
                    i.putExtra("text", editText.getText().toString().trim());
                    setResult(RESULT_OK, i);

                }
                finish();

            }
        });

        editText = (EditText) findViewById(R.id.inserttextedittext);
        editText.setText(getIntent().getStringExtra("text"));
        editText.setSelection(getIntent().getStringExtra("text").length());


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEdittextEmpty = s.toString().trim().length() == 0;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent();


        setResult(RESULT_CANCELED);
        finish();


    }
}

package com.example.wearable.position;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    EditText editText, editText2, editText3;
    Button go_main_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editTex2);
        editText3 = findViewById(R.id.editTex3);

        go_main_activity = findViewById(R.id.go_main_activity);
        go_main_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.putExtra("coff1", editText.getText().toString());
                intent.putExtra("coff2", editText2.getText().toString());
                intent.putExtra("period", editText3.getText().toString());
                startActivity(intent);
            }
        });
    }

}

package com.miqt.datacontrol;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.miqt.multiprogresskv.DataControl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText ed_key = findViewById(R.id.ed_key);
        final EditText ed_value = findViewById(R.id.ed_value);
        final DataControl control = new DataControl(this, DataControl.SaveType.DB);

        Button button = findViewById(R.id.btn_commit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = ed_key.getText().toString();
                String value = ed_value.getText().toString();
                control.putString(key, value);
            }
        });
    }
}
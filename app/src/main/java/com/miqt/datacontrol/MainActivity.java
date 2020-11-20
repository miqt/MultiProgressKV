package com.miqt.datacontrol;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.miqt.multiprogresskv.DataControl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataControl control  = new DataControl(this, DataControl.SaveType.DB);
        control.putString("321","321");
        control.putString("321","qqq");
        control.putString("321","bbb");
    }
}
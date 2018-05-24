package com.adans.app_10;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.adans.app_10.Cowtech54.CowTabActivity;

public class VehicleRegAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_reg);
    }

    public void ClickReg(View view) {
        Intent mint= new Intent(getApplicationContext(), CowTabActivity.class);
        startActivity(mint);
    }
}

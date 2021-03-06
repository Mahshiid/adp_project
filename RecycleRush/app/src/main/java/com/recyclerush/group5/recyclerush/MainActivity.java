package com.recyclerush.group5.recyclerush;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    HashMap<String, ItemObject> map = new HashMap<String, ItemObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onResume() {
        super.onResume();
        openScanner();
    }

    private void openScanner() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setCaptureActivity(CustomScannerActivity.class);
        scanIntegrator.initiateScan();

    }


    private void display(String id) {
        if(ItemObject.doesExist(id)){
            //open display activity
            CurrentUser.getInstance().recycle(ItemObject.getScannedItem(id));
            Intent displayInfo = new Intent(this, DisplayItemInfoActivity.class);
            displayInfo.putExtra("scanId", id);
            startActivity(displayInfo);
        }
        else {
            Toast.makeText(getApplicationContext(), "Barcode not found.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent in) {
        super.onActivityResult(requestCode, resultCode, in);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, in);
        if (scanningResult != null) {
            try {
                display(in.getStringExtra("SCAN_RESULT"));
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }
}
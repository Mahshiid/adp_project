package com.recyclerush.group5.recyclerush;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.util.HashMap;

import android.view.Menu;
import android.view.MenuItem;


public class BarcodeReaderActivity extends AppCompatActivity {
    HashMap<String, ItemObject> map = new HashMap<String, ItemObject>();
    CurrentUser currentUser = CurrentUser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDummyObjects();
        setContentView(R.layout.activity_barcode_reader);
    }

    private void initDummyObjects(){
        ItemObject redbull = new ItemObject("7340131610000","Redbull", true, "metal" );
        map.put(redbull.getScanId(), redbull);
        ItemObject snus = new ItemObject("7311250004360", "Snus", true, "plastic, paper");
        map.put(snus.getScanId(), snus);
        ItemObject tom = new ItemObject("5901234123457", "Tom", true, "paper");
        map.put(tom.getScanId(), tom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_camera:
                openScanner();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openScanner() {
        currentUser = CurrentUser.getInstance();

        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setCaptureActivity(CustomScannerActivity.class);
        scanIntegrator.addExtra("name", currentUser.getUserName());
        scanIntegrator.addExtra("score", currentUser.getScore());
        scanIntegrator.initiateScan();

    }

    String message = "";
    public void barcodeRead(View view){
        EditText usersBarcode = (EditText) findViewById(R.id.editText_1);
        // Toast.makeText(MainActivity.this, usersBarcode.getText().toString(), Toast.LENGTH_SHORT).show();
        if (usersBarcode.getText().toString().isEmpty()) {
            // message = "Please enter a barcode";
            Toast.makeText(BarcodeReaderActivity.this, "Please enter a barcode", Toast.LENGTH_SHORT).show();
        } else {
            display(usersBarcode.getText().toString());
        }
    }

    private void display(ItemObject obj) {
        if(obj==null){
            String message = "Barcode not found.";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            return;
        }

        Intent displayInfo = new Intent(this, SecondActivity.class);
        displayInfo.putExtra("scanId", obj.getScanId());
        displayInfo.putExtra("name", obj.getName());
        displayInfo.putExtra("materials", obj.getMaterials());

        if (obj.isRecyclable()) {
            currentUser.recycle(obj);
            displayInfo.putExtra("recyc", "Recycable!");
        } else {
            displayInfo.putExtra("recyc", "Not Recycable!");
        }
        startActivity(displayInfo);
    }


    private void display(String barcode){
        display(getScannedItem(barcode));
    }

    private ItemObject getScannedItem(String id){
        return map.get(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent in) {
        super.onActivityResult(requestCode, resultCode, in);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, in);
        if (scanningResult != null) {
            try {
                Log.i("barcode", in.getStringExtra("SCAN_RESULT"));
                display(in.getStringExtra("SCAN_RESULT"));
            }
            catch (NullPointerException e){}
        }
    }
}
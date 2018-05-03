package com.recyclerush.group5.recyclerush;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;

import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    boolean first = true;
    //map for the uesrs
    HashMap<String, userClass> userMap = new HashMap<String, userClass>();
    // The current user of the app, unknown as login-state
    userClass currentUser = new userClass("unknown");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("mainactivity", "test");
        super.onCreate(savedInstanceState);
        //start loginscreen, and wait for a loginresult
        ItemObject.initDummyObjects();
        openScanner();
        setContentView(R.layout.activity_main);

        Button backButton = findViewById(R.id.backbuttonmanual);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                openScanner();
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.action_camera:
                    openScanner();
                   // IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                    // scanIntegrator.initiateScan();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }



    private void openScanner() {

        try{
            String user = getIntent().getExtras().getString("user");
            addMember(user);
            currentUser = getUser(user);
        }catch(Exception e){
        }

        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setCaptureActivity(CustomScannerActivity.class);
        scanIntegrator.addExtra("name", currentUser.getUser());
        scanIntegrator.addExtra("points", currentUser.getPoints());
        scanIntegrator.initiateScan();

        }

    // use functions in the itemobjectclass to retrieve information about each object.
    //String snusname = snus.getName();
    //String snusID = snus.getScanId();
    String message = "";
    public void searchBarcode(View view){
        EditText usersBarcode = (EditText) findViewById(R.id.editText1);
            // Toast.makeText(MainActivity.this, usersBarcode.getText().toString(), Toast.LENGTH_SHORT).show();
        if (usersBarcode.getText().toString().isEmpty()) {
           // message = "Please enter a barcode";
            Toast.makeText(MainActivity.this, "Please enter a barcode", Toast.LENGTH_SHORT).show();
        } else {
            display(ItemObject.getScannedItem(usersBarcode.getText().toString()));
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

        if (obj.isRecycleable()) {
            displayInfo.putExtra("recyc", "Recycable!");
        } else {
            displayInfo.putExtra("recyc", "Not Recycable!");
        }
        startActivity(displayInfo);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent in) {
        super.onActivityResult(requestCode, resultCode, in);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, in);
        if (scanningResult != null) {
            try {
                Log.i("barcode", in.getStringExtra("SCAN_RESULT"));
                display(ItemObject.getScannedItem(in.getStringExtra("SCAN_RESULT")));
            }
            catch (NullPointerException e){}
        }
    }
    private void addMember(String user){
        if (getUser(user) == null){
            //user not in list
            userMap.put(user, new userClass(user));
        }
    }
    private userClass getUser(String id){
        return userMap.get(id);
    }
}
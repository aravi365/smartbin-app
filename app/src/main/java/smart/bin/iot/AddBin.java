package smart.bin.iot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.brain.wastebin.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddBin extends Activity {
    String  scannedResult;
    Button scan,addBin;
    EditText qrscan,abemail,addBinCapacity;
    Spinner addBinlocationSpinner;
    String selectedBinLocation;

    String [] binLocationMain;
    String MY_PREFS_NAME ="MAILID";

    String persistedEmail=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bin);

        //receiving email saved through intent using Bundle
/*
        Bundle bundle = getIntent().getExtras();
        persistedEmail = (String) bundle.get("persistedEmail");
*/


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        persistedEmail = prefs.getString("email",null);

        //final List<String> locationListMain= getIntent().getStringArrayListExtra("bin");

        //edittext showing scaaned qr code using the scanner
        qrscan = (EditText) findViewById(R.id.addbinscancodes);
        qrscan.setEnabled(false);
        qrscan.setTextColor(Color.DKGRAY);

        //edittext showing email saved and received from bundle
        abemail = (EditText) findViewById(R.id.addbinemail);
        abemail.setEnabled(false);
        abemail.setText(persistedEmail);
        qrscan.setTextColor(Color.DKGRAY);

        //edittext showing bincapacity of bin
        addBinCapacity = (EditText) findViewById(R.id.addbincapacity);
        addBinCapacity.setText("0");
        addBinCapacity.setEnabled(false);
        qrscan.setTextColor(Color.DKGRAY);

        //Linking scan button for scanning qr codes
        scan = (Button) findViewById(R.id.scann);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQrcodes();
            }
        });

        //Linking and setting Listner for adding Bins using addbin Button
        addBin = (Button) findViewById(R.id.abadd);
        addBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate() ) {

                    addBin();
                }else
                {

                }

            }
        });

        //Linking spinner which contains d locations of the bins
        addBinlocationSpinner = (Spinner) findViewById(R.id.binlocSpin);

        ArrayList<String> binLocationList = new ArrayList<>();


        List<String> binLocationList1 =new ArrayList<>(MainActivity.dynamicLocationsList);

        for (int i=2; i<binLocationList1.size();i++)
        {
            binLocationList.add(binLocationList1.get(i));
        }



       /* ArrayAdapter<String> binLocationsAdapter= new ArrayAdapter<String>(getApplicationContext(),
                R.layout.dropdown,binLocationList);*/

        //setting adapter for showing the places of bins in spinner
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.dropdown, binLocationList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.DKGRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

      /*  Animation shake = AnimationUtils.loadAnimation(this, R.anim.);
        spnMySpinner.startAnimation(shake);*/
        addBinlocationSpinner.setAdapter(spinnerArrayAdapter);

        //setting listener for spinner
        addBinlocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedBinLocation = parent.getItemAtPosition(position).toString();
                //addBinlocationSpinner.setEnabled(false);
                Log.e("out",selectedBinLocation);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Toast.makeText(getApplicationContext(),"unselected",Toast.LENGTH_SHORT).show();

            }
        });

    }


    public boolean validate() {
        boolean valid = true;



        if (qrscan.getText().toString().isEmpty()) {
            qrscan.setError("field cannot be blank");

            valid = false;
        }/* else {
            qrscan.setError(null);
            valid = false;
        }*/

        if (selectedBinLocation.isEmpty() || selectedBinLocation.equalsIgnoreCase("Select Your Bin Place")) {
            Log.e("in","inside in 2ndif");
            TextView errorText = (TextView) addBinlocationSpinner.getSelectedView();
            errorText.setError("invalid field");
            errorText.setTextColor(Color.GRAY);
            errorText.setText("no field selected");
            //just to highlight that this is an error
            //changes the selected item text to this
            Toast.makeText(getApplicationContext(),"locationnotselected", Toast.LENGTH_SHORT).show();
            valid = false;
        }









        return valid;
    }



    //method for adding bins to the server using volley
    private void addBin() {

        SharedPreferences ipID = getSharedPreferences(MainActivity.MY_PREFS_NAME1, MODE_PRIVATE);
        String ip= ipID.getString("ip",null);
        //String clearUrl ="http://192.168.1.30:8080/atm_security/controller";
        String clearUrl ="http://"+ip+":8080/garbagecollector/add";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, clearUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String result = response.trim();
                // Alert dialog box forshowing result from server with OKAY button
                AlertDialog.Builder builder = new AlertDialog.Builder(AddBin.this);
                builder.setMessage(result)
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //passing intent inside alertdialogee box
                                Intent homeIntent = new Intent(AddBin.this,HOMMMMme.class);
                                startActivity(homeIntent);
                                finish();
                                AddBin.this.finish();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();


                Log.e("res",result);


                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley error", error.toString());
                showNotification(error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("task","pling1");
                params.put("qrCode",scannedResult);
                params.put("email",persistedEmail);
                params.put("location",selectedBinLocation);
                params.put("currentCapacity","0");

                Log.e("d",params.toString());
                return params;

            }
            private Map<String, String> checkParams(Map<String, String> map){
                Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
                    if(pairs.getValue()==null){
                        map.put(pairs.getKey(), "");
                    }
                }
                return map;
            }
        };




        Log.e("before server request", "success");
        Log.e("url",clearUrl);
        Volley.newRequestQueue(getBaseContext()).add(stringRequest);
        Log.e("after server request", "success");

    }

    //method for scanning qr codes using library
    private void scanQrcodes()
    {


        IntentIntegrator obj = new  IntentIntegrator(AddBin.this);
        obj.setDesiredBarcodeFormats( IntentIntegrator.QR_CODE_TYPES);
        obj.setPrompt("scan");
        obj.setCameraId(0);
        obj.setBeepEnabled(false);
        obj.setBarcodeImageEnabled(false);
        obj.initiateScan();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancceled the scanning", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                scannedResult= result.getContents().toString();

                qrscan.setText(scannedResult);
                Log.e("scan",qrscan.toString());
                //qrscan.setError(null);
                Log.e("scannedResult",scannedResult);

            }
        }else

            super.onActivityResult(requestCode, resultCode, data);
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {


            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            // Log.e("garbage",garbageBinLocations.toString());
            /*ArrayAdapter<String>adapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.dropdown,garbageBinLocations);
            binSpinner.setAdapter(adapter);*/

        }
    }


    public  void  showNotification(String hello){

        CoordinatorLayout snackbarCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);
        Snackbar snackbar = Snackbar.make(
                snackbarCoordinatorLayout,
                "Distance",
                Snackbar.LENGTH_INDEFINITE);

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);  //set the max lines for textview to show multiple lines


        textView.setText(hello);
        textView.setTextColor(Color.RED);


        snackbar.show();

    }


}

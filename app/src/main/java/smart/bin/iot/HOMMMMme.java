package smart.bin.iot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HOMMMMme extends AppCompatActivity {

    String binIDselected;
    LinkedHashMap<String,String> garbageBinData;
    List<String> garbageBinLocations;
    Spinner binSpinner;
    Button addBin,clearBin;
    EditText binlocation;
    String MY_PREFS_NAME = "MAILID";
    String ip;
    String persistedEmail=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hommmmme);

        //ConnectivityCheck.mContext=getApplicationContext();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        persistedEmail = prefs.getString("email", null);

        SharedPreferences ipID = getSharedPreferences(MainActivity.MY_PREFS_NAME1, MODE_PRIVATE);
         ip= ipID.getString("ip",null);





        new AsyncTaskRunner().execute();

        binlocation = (EditText) findViewById(R.id.binLocations);
        binlocation.setEnabled(false);

        //linking buttons in home page
        addBin = (Button) findViewById(R.id.addBin1);
        clearBin= (Button) findViewById(R.id.clearBin2);

        //passing intent to addBin activity on addBin button click
        addBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(HOMMMMme.this,AddBin.class);
                intent.putExtra("persistedEmail",persistedEmail);
                startActivity(intent);



            }
        });

        //passing intent to clearBin activity on clearBin button click
        clearBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    clearBin();


            }
        });

        binSpinner = (Spinner) findViewById(R.id.binSpinner);




    }


    /*@Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HOMMMMme.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }*/

    private void clearBin() {

        String clearUrl ="http://"+ip+":8080/garbagecollector/status/clear";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, clearUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String result = response.trim();
                Log.e("res",result);

                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley error", error.toString());
                //showNotification(error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("binID",binIDselected);
                return params;

            }
        };
        Log.e("before server request", "success");
        Log.e("url",clearUrl);
        Volley.newRequestQueue(getBaseContext()).add(stringRequest);
        Log.e("after server request", "success");

    }




    //async task

    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... params) {


                loginCheck();

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            // Log.e("garbage",garbageBinLocations.toString());
            /*ArrayAdapter<String>adapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.dropdown,garbageBinLocations);
            binSpinner.setAdapter(adapter);*/

        }

        private void loginCheck() {



            String loginUrl ="http://"+ip+":8080//garbagecollector/binlocations";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    String result = response.trim();
                    Log.e("res",result);




                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        //linked hashmap for fetching data from server
                        garbageBinData= objectMapper.readValue(result,LinkedHashMap.class);

                        //defining list for saving map
                        garbageBinLocations= new ArrayList<>(garbageBinData.keySet());

                        //Setting Array adapter for showing contents from
                        ArrayAdapter<String>adapter = new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.dropdown,garbageBinLocations);
                        binSpinner.setAdapter(adapter);
                        binSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                binIDselected= parent.getItemAtPosition(position).toString();
                                binlocation.setText(garbageBinData.get(garbageBinLocations.get(position)));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });





                        Log.e("linked",garbageBinData.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("volley error", error.toString());
                    //showNotification(error.toString());
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                protected Map<String, String> getParams() {

               /* String loginJson="";
                GarbageBin garbageBin = new GarbageBin();
                GarbageSecurity garbageSecurity = new GarbageSecurity();
                garbageSecurity.setSalt(password.getText().toString().getBytes());
                garbageBin.setEmail(email);
                garbageBin.setGarbageSecurity(garbageSecurity);

                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    loginJson=objectMapper.writeValueAsString(garbageBin);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }*/

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email",persistedEmail);

                    return params;


                }


            };
            Log.e("before server request", "success");
            Log.e("url",loginUrl);
            Volley.newRequestQueue(getBaseContext()).add(stringRequest);
            Log.e("after server request", "success");

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

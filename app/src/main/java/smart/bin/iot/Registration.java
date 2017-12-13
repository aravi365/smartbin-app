package smart.bin.iot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.brain.wastebin.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import garbage.disposal.model.GarbageBin;

import garbage.disposal.model.GarbageCollector;
import garbage.disposal.model.GarbageSecurity;

public class Registration extends Activity  {
    String placeSelected,scannedResult;
EditText firstname,lastname,mobile,username,password,curentcapacity,email,QrResult;
    Activity activity;
    ImageButton scanQr;
    Button register;
    String QrResults;
    List<GarbageBin> gbList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        activity=this;
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        mobile= (EditText) findViewById(R.id.mobile);

        password= (EditText) findViewById(R.id.passwordd);
        email= (EditText) findViewById(R.id.email);



        scanQr= (ImageButton) findViewById(R.id.scanQr);
        scanQr.setEnabled(false);
        scanQr.setVisibility(View.INVISIBLE);


        scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQrcodes();


            }
        });






        register= (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBin();
            }
        });
    }


    private void registerBin() {
gbList = new ArrayList<>();
        GarbageBin qrCode = new GarbageBin();
        qrCode.setQrCode("hkjfhdkhg125");
        qrCode.setLocation("ant");
        qrCode.setCurrentCapacity(100);

       GarbageBin qrCode1 = new GarbageBin();
        qrCode1.setQrCode("hkhdfrg57487");
        qrCode1.setLocation("brain");
        qrCode1.setCurrentCapacity(100);
      /*  gbList.add(qrCode);
        gbList.add(qrCode1)*/;

        String regUrl ="http://192.168.43.119:8080/garbagecollector/register";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, regUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String result = response.trim();
                Log.e("result", response.toString());

                if (result.equalsIgnoreCase("User registered successfully")){

                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Registration.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();


                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley error", error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {

                String regJson="";
                GarbageCollector garbageCollector = new GarbageCollector();
                GarbageSecurity garbageSecurity = new GarbageSecurity();
                GarbageBin garbageBin = new GarbageBin();



               // garbageBin.setCurrentCapacity(100);
                garbageSecurity.setPassword(password.getText().toString().toCharArray());


            garbageCollector.setGarbageCollectorID(0);
                garbageCollector.setFirstName(firstname.getText().toString());
                garbageCollector.setLastName(lastname.getText().toString());
                garbageCollector.setEmail(email.getText().toString());
               garbageCollector.setMobileNumber(mobile.getText().toString());

                garbageCollector.setGarbageBins(gbList);


                garbageCollector.setGarbageSecurity(garbageSecurity);


                ObjectMapper objectMapper =new ObjectMapper();

                try {
                    Log.e("log1","before mapping");
                    regJson=objectMapper.writeValueAsString(garbageCollector);
                    Log.e("log2","before mapping");
                } catch (JsonProcessingException e) {
                    e.getMessage();
                    e.printStackTrace();
                }


                Map<String, String> params = new HashMap<String, String>();
                params.put("register",regJson);

               /* params.put("firstName",firstname.getText().toString());
                params.put("lastName",lastname.getText().toString());
                params.put("mobileNumber",mobile.getText().toString());
                params.put("location",placeSelected);
                params.put("currentCapacity","100");
                params.put("email",email.getText().toString());
                params.put("garbageSecurity.salt",password.getText().toString());
                params.put("email",email.getText().toString());
                params.put("qrCodes",qrList.toString());
*/
                Log.e("register",regJson);

                return params;

            }
          /*  @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }*/
        };
        Log.e("before server request", "success");
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        Log.e("after server request", "success");
    }


   /* public void register(){


        String regJson="";
        garbageCollector garbageCollector = new garbageCollector();
        GarbageSecurity garbageSecurity = new GarbageSecurity();
        garbageSecurity.setPassword(password.getText().toString().toCharArray());

        garbageCollector.setgarbageCollectorID(0);
        garbageCollector.setFirstName(firstname.getText().toString());
        garbageCollector.setLastName(lastname.getText().toString());
        garbageCollector.setEmail(email.getText().toString());
        garbageCollector.setMobileNumber(mobile.getText().toString());
        garbageCollector.setLocation(placeSelected);
        garbageCollector.setQrCodes(qrList);
        garbageCollector.setCurrentCapacity(100);

        garbageCollector.setGarbageSecurity(garbageSecurity);


        ObjectMapper objectMapper =new ObjectMapper();

        try {
            Log.e("log1","before mapping");
            regJson=objectMapper.writeValueAsString(garbageCollector);
            Log.e("log2","before mapping");
        } catch (JsonProcessingException e) {
            e.getMessage();
            e.printStackTrace();
        }

        String regUrl ="http://192.168.1.53:8080/garbagecollector/register";

        JSONObject js = new JSONObject(); try {

            js.put("json",regJson);
            Log.e("json",regJson.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, regUrl, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("respose", response.toString()+"i am queen");
                        Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("error", "Error: " + error.getMessage());
            }
        }) {

            *//**
             * Passing some request headers
             *//*
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        // Adding request to request queue
        Volley.newRequestQueue(this).add(jsonObjReq);

    }
*/









//method for scanning qr Code


    private void scanQrcodes()
    {
        scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator obj = new  IntentIntegrator(activity);
                obj.setDesiredBarcodeFormats( IntentIntegrator.QR_CODE_TYPES);
                obj.setPrompt("scan");
                obj.setCameraId(0);
                obj.setBeepEnabled(false);
                obj.setBarcodeImageEnabled(false);
                obj.initiateScan();
            }
        });
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
                Log.e("scannedResult",scannedResult);

                QrResult.setText(scannedResult);
            }
        }else

            super.onActivityResult(requestCode, resultCode, data);
    }

}

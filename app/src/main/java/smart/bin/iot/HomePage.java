package smart.bin.iot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.brain.wastebin.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity {
    Button addBin,clearBin,getBinLOcation,binstatus;
    String  persistedEmail;
    String MY_PREFS_NAME ="MAILID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        persistedEmail = prefs.getString("email",null);

        addBin= (Button) findViewById(R.id.addBin);
        addBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomePage.this,AddBin.class);
                startActivity(intent);

            }
        });

        clearBin= (Button) findViewById(R.id.clearBin);
        clearBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomePage.this,HOMMMMme.class);
                startActivity(intent);

            }
        });

        

        binstatus= (Button) findViewById(R.id.binstatus);
        binstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 binStatusChecker();


            }
        });
    }

    private void binStatusChecker() {

        SharedPreferences ipID = getSharedPreferences(MainActivity.MY_PREFS_NAME1, MODE_PRIVATE);
        String ip= ipID.getString("ip",null);
        //email isused as username
        //String loginUrl ="http://192.168.1.30:8080/atm_security/controller";
       String loginUrl ="http://"+ip+":8080/garbagecollector/map";

       // String loginUrl ="http://192.168.1.23:8080/HomeAutomation/Controller";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String result = response.trim();
                LatLng first = null, second = null;
                if (result.equals("All bins are below the required limit")){

                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                }else {
                    String[] locations = {"Ponkunnam", "Kanjirappally", "26thMile", "Koovappally"};

                    List<LatLng> myLat = new ArrayList<>();
                    myLat.add(new LatLng(9.565266, 76.755936));
                    myLat.add(new LatLng(9.558281, 76.791658));
                    myLat.add(new LatLng(9.5608080, 76.8111970));
                    myLat.add(new LatLng(9.542858, 76.820125));

                    String[] results = result.trim().split(" ");
                    Log.e("maplocations", Arrays.toString(results));
                    Log.e("maplocations", Arrays.toString(results));

                    for (int i = 0; i < 4; i++) {

                        if (results[0].trim().equalsIgnoreCase(locations[i])) {
                            first = myLat.get(i);
                        }
                        if (results[2].trim().equalsIgnoreCase(locations[i])) {
                            second = myLat.get(i);
                        }

                    }
                    String firstBin = results[1].trim().toString();
                    String secondBin = results[3].trim().toString();


                    Intent intent = new Intent(HomePage.this, MapsActivity.class);
                    intent.putExtra("first", first);
                    intent.putExtra("second", second);
                    intent.putExtra("firstBin", firstBin);
                    intent.putExtra("secondBin", secondBin);
                    startActivity(intent);


                    Log.e("mylats", first + " " + second);



                   /* String []binLocationMap=  result.split(" ");
                    Log.e("map", Arrays.toString(binLocationMap));*/
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley error", error.toString());

                //inorder to show messges on snackbar u need to define cordinator
                // laytout in the layout of the corresponding activity which is defined in some acitivities;

                //showNotification(error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("email",persistedEmail);
                return params;
            }
        };
        Log.e("before server request", "success");
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        Log.e("after server request", "success");
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

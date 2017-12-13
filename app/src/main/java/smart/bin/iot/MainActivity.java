package smart.bin.iot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.brain.wastebin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.example.brain.wastebin.R.color.green_500;

public class MainActivity extends AppCompatActivity {
    EditText username,password;
    Button login;
    static ArrayList<String> dynamicLocationsList;
    String email,pass;
    TextView configuation;
    String MY_PREFS_NAME ="MAILID";
    public static final String MY_PREFS_NAME1 = "MyPrefsFile";
    static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username= (EditText) findViewById(R.id.usename);
        password= (EditText) findViewById(R.id.password);

        configuation= (TextView) findViewById(R.id.configuration);
        configuation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,IPchooser.class));
            }
        });


        login= (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                loginCheck();
                Toast.makeText(getApplicationContext(),"Logging in...",Toast.LENGTH_SHORT).show();

            }
        });




    }
    private void loginCheck() {

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME1, MODE_PRIVATE);
        String ip= prefs.getString("ip",null);
        //email isused as username
        email=username.getText().toString();
        pass=password.getText().toString();

        //String loginUrl ="http://192.168.1.30:8080/atm_security/controller";
        String loginUrl ="http://"+ip+":8080/garbagecollector/validate";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String result = response.trim();
                Log.e("res",result);

                //reciving data from server once log in is completed sucessfully
                dynamicLocationsList =new ArrayList<>(Arrays.asList(response.trim().split(" ")));

                Log.e("dyn",dynamicLocationsList.toString());

                if (dynamicLocationsList.get(0).toString().equals("VERIFIED")){
                    // String []binLocations=    result.trim().split(" ");
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("email",email);
                    editor.commit();


                    Log.e("if","inside if");
                    Intent login = new Intent(MainActivity.this,HomePage.class);
                    login.putExtra("list",dynamicLocationsList);
                    startActivity(login);
                    finish();

                }else
                {
                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                    username.setError(result);
                    password.setError(result);
                }





            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley error", error.toString());
                if(error.toString().split(":")[0].equalsIgnoreCase("com.android.volley.TimeoutError")){

                    showNotification("Connection TimedOut!");
                }

            }
        }) {
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();
                params.put("email",email);
                params.put("password",pass);



                return params;


            }


        };
        Log.e("before server request", "success");
        Volley.newRequestQueue(getBaseContext()).add(stringRequest);
        Log.e("after server request", "success");
    }



    public  void  showNotification(String hello){

        CoordinatorLayout snackbarCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);
        Snackbar snackbar = Snackbar.make(
                snackbarCoordinatorLayout,
                "Distance",
                Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);  //set the max lines for textview to show multiple lines

        snackbarView.setBackgroundColor(getResources().getColor(green_500));
        textView.setText(hello);
        textView.setTextColor(Color.WHITE);


        snackbar.show();

    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,"Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }
}

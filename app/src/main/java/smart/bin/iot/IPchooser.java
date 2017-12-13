package smart.bin.iot;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.brain.wastebin.R;

public class IPchooser extends AppCompatActivity {

    EditText ipDetails;
    Button save,reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipchooser);


        final SharedPreferences.Editor editor = getSharedPreferences(MainActivity.MY_PREFS_NAME1, MODE_PRIVATE).edit();

        SharedPreferences prefs = getSharedPreferences(MainActivity.MY_PREFS_NAME1, MODE_PRIVATE);

            String ip = prefs.getString("ip", null);//"No name defined" is the default value.



        ipDetails= (EditText) findViewById(R.id.ipfield);
        ipDetails.setText(ip);

        reset= (Button) findViewById(R.id.reset);
        save= (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("ip", ipDetails.getText().toString());
                editor.commit();

                Toast.makeText(IPchooser.this, "Ip saved", Toast.LENGTH_SHORT).show();




            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.clear();
                editor.commit();

                ipDetails.setHint("enter Ip here");

            }
        });
    }
}

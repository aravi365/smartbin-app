package smart.bin.iot;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brain.wastebin.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.brain.wastebin.R.color.green_500;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    String distance; LatLng currentLatLng,firstLatLng,secondLatLng;
    LatLng myPlace;
    LatLng point1,point2,point3;
    Marker park1,park2,park3;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Marker mCurrLocationMarker ;
    Marker mCurrLocationMarker1;
    Location mLastLocation;
    Button path;
    String duration;
    String firstBinStatus=null;
    String secondBinStatus=null;
    List<LatLng> lat= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Bundle bundle =getIntent().getExtras();

        if (bundle!=null) {
            firstLatLng = (LatLng) bundle.get("first");
            secondLatLng = (LatLng) bundle.get("second");
            firstBinStatus= (String) bundle.get("firstBin");
            secondBinStatus= (String) bundle.get("secondBin");
        }

        Log.e("crackatova",firstLatLng.toString());
        Log.e("fbin"+" "+secondBinStatus,firstBinStatus);
        path= (Button) findViewById(R.id.getshortestpath);
        path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = getUrl(firstLatLng,secondLatLng);
               // FetchUrl fetchUrl = new FetchUrl();
                // Start downloading json data from Google Directions API
                new FetchUrl().execute(url);
            }
        });




    }



    @Override

    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);

            }
        } else {
            buildGoogleApiClient();
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.1f));

        binAreas();
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }


    }
    private void getDistance(LatLng s,LatLng p){

        float[] results = new float[1];
        Location.distanceBetween(s.latitude,s.longitude,
                p.latitude,p.longitude, results);

        Toast.makeText(this, results+"", Toast.LENGTH_SHORT).show();

    }

    private void getCity(String message){

        String location=message;
        String inputLine = "";
        String result = "";
        location=location.replaceAll(" ", "%20");
        String myUrl="http://maps.google.com/maps/geo?q="+location+"&output=csv";
        try{
            URL url=new URL(myUrl);
            URLConnection urlConnection=url.openConnection();
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(urlConnection.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                result=inputLine;
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
            String lat = result.substring(6, result.lastIndexOf(","));
            String longi = result.substring(result.lastIndexOf(",") + 1);

            Log.e("latter",lat+" "+longi);
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }




    private void getLatlong(String city1,String city2 ){

        Toast.makeText(this, "inside my method", Toast.LENGTH_SHORT).show();
        try {
            String location1 = city1;
            String location2 =  city2;
            Geocoder gc = new Geocoder(this);
            List<Address> addresses1= gc.getFromLocationName(location1, 5); // get the found Address Objects
            List<Address> addresses2= gc.getFromLocationName(location2, 5);
            List<LatLng> lat1 = new ArrayList<LatLng>(addresses1.size()); // A list to save the coordinates if they are available
            List<LatLng> lat2 = new ArrayList<LatLng>(addresses2.size());
            for(Address a : addresses1){
                int i=0;
                if(a.hasLatitude() && a.hasLongitude()){
                    lat.add(new LatLng(a.getLatitude(), a.getLongitude()));
                }
            }

            for(Address a : addresses2){
                int i=0;
                if(a.hasLatitude() && a.hasLongitude()){
                    lat.add(new LatLng(a.getLatitude(), a.getLongitude()));
                }
                Toast.makeText(this, lat.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // handle the exception
        }
    }


    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.e("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        Log.e("km",kmInDec+"");
        Log.e("radious", Radius * c+"");


        return Radius * c;

    }


    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            Log.e("Data", data);
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            ParserTask parserTask = new ParserTask();
            Log.e("Result", result+"");
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            Log.e("Route", routes + "");
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            List<LatLng> points = null;
            PolylineOptions lineOptions = null;

            Log.e("ListPath1", result + "");
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();


                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                Log.e("ListPath2", result + "");
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    Log.e("ListPoint", point + "");

                    if (j == 0) { // Get distance from the list
                        distance = point.get("distance");
                        Log.e("Dis", distance);
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = point.get("duration");
                        Log.e("Dis", distance);
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);

                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }
            if (distance != null && duration != null) {
                snackbar();
            }
            //tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }



        public void snackbar() {
            CoordinatorLayout snackbarCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);
            final Snackbar snackbar = Snackbar.make(snackbarCoordinatorLayout, "Distance",
                    Snackbar.LENGTH_INDEFINITE);

            View snackbarView = snackbar.getView();
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setMaxLines(5);  //set the max lines for textview to show multiple lines
            textView.setTextSize(17);
            textView.setTextAlignment(1);
            snackbarView.setBackgroundColor(getResources().getColor(green_500));
            textView.setTextColor(Color.WHITE);

            textView.setText("Distance:  " + distance + "  and  " + "Duration:  " + duration);

            snackbar.show();

        }


    }


    public void binAreas() {

        point1 = firstLatLng;
        park1 = mMap.addMarker(new MarkerOptions()
                .position(point1)
                .title("Bin Location1")
                .snippet("Current status is  "+firstBinStatus)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bin_lev)));

        point2 = secondLatLng;
        park2 = mMap.addMarker(new MarkerOptions()
                .position(point2)
                .title("Bin Location2")
                .snippet("Current status is  "+secondBinStatus)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bin_lev)));

    }

}


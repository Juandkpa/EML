package com.example.juan.eml;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;


public class SearchableActivity extends ListActivity implements LocationListener {

    double mLatitude=0;
    double mLongitude=0;

    private static final String API_KEY = "AIzaSyBw6xFrZ1IznT6m1wkY9S74zXfR737D8_U";

    private static String namePlace ="";
    private static final String NAME = "name";
    private static final String VECINITY = "vicinity";
    private static final String RATING = "rating";
    private static final String REFERENCE = "reference";
    private static final String ISOPEN = "isOpen";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        lv = getListView();

        Log.d("Searchable Activity:", "ENTRO!!!");
        System.out.println("ENtro!!!! Searchable!!");

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        Log.d("Localitation:", location.toString());

        Intent intent = getIntent();
        if(intent.ACTION_SEARCH.equals(intent.getAction())){
            namePlace = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(namePlace);
        }
    }

    public void photoSearch(String photoref){
        //StringBuilder sb = new StringBuilder(());


    }
    public void doMySearch(String query){
        String type = query;
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location="+mLatitude+","+mLongitude);
        sb.append("&radius=3000");
        sb.append("&types=restaurant");
        sb.append("&sensor=true");
        //sb.append("&key="+R.string.googlePlaces_key);
        sb.append("&key="+API_KEY);
        // Creating a new non-ui thread task to download json data
        PlacesTask placesTask = new PlacesTask();
        Log.d("ENTRO A DOMY SEARCH ","ENTRO LUEGO DE PLACESTASK!!!!!");
        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());
    }

    private class PlacesTask extends AsyncTask<String, Integer,String>{
        String data = null;
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
                Log.d("DOWUNLOADURL","ESTAAQUI");
            }catch(Exception e){
                Log.d("Backgraound Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            Log.d("MADE URL:", "HACIENDO URL");
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            data = sb.toString();
            Log.v("EL JSON: ",data);
            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }


    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);
                Log.d("JSONoBJECT",places.toString());
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){

            // Clears all the existing markers
           // mGoogleMap.clear();
            TextView nameMain = (TextView) findViewById(R.id.nombreMain);
            TextView vecinityMain = (TextView) findViewById(R.id.vecinityMain);
            RatingBar ratingMain =  (RatingBar) findViewById(R.id.MainratingBar);

            Log.v("CANTIDAD LUGARES:",String.valueOf(list.size()));
            for(int i=0; i<list.size();i++){
                if(list.get(i).get("name").contentEquals(namePlace)){
                    nameMain.setText(list.get(i).get("name"));
                    vecinityMain.setText(list.get(i).get("vicinity"));
                    ratingMain.setRating(Float.parseFloat(list.get(i).get("rating")));
                    list.remove(i);
                }
            }
            //nameMain.setText(list.get(0).get("name"));
            //vecinityMain.setText(list.get(0).get("vicinity"));
            //Log.d("RESULTADOS",list.toString());
            SimpleAdapter adapter = new SimpleAdapter(
               SearchableActivity.this, list, R.layout.list_item, new String[]{NAME, VECINITY, RATING, REFERENCE, ISOPEN, LAT, LNG}, new int[]{R.id.name,R.id.vecinity,R.id.rating, R.id.reference, R.id.isOpen, R.id.lat, R.id.lng});
               adapter.setViewBinder(new MyBinder());
               setListAdapter(adapter);

            listClicker(list);


           /* for(int i=0;i<list.size();i++){

                // Creating a marker
             //   MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);
                Log.d("NAME PLACE:",name);

                // Setting the position for the marker
               // markerOptions.position(latLng);

                // Setting the title for the marker.
                //This will be displayed on taping the marker
               // markerOptions.title(name + " : " + vicinity);

                // Placing a marker on the touched position
             //   mGoogleMap.addMarker(markerOptions);
            }*/
        }
    }

    public void listClicker(final List<HashMap<String,String>> list){

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String name = ((TextView) view.findViewById(R.id.name))
                        .getText().toString();
                String vecinity = ((TextView) view.findViewById(R.id.vecinity))
                        .getText().toString();
                String  reference = ((TextView) view.findViewById(R.id.reference))
                        .getText().toString();
                String isOpen = ((TextView) view.findViewById(R.id.isOpen))
                        .getText().toString();

                float rating = ((RatingBar) view.findViewById(R.id.rating)).getRating();

                String lat = ((TextView) view.findViewById(R.id.lat))
                        .getText().toString();

                String lng = ((TextView) view.findViewById(R.id.lng))
                        .getText().toString();
                /*String reference ="";
                for(int i=0; i<list.size();i++){
                    if(list.get(i).get(NAME).equals(name)){
                        reference = list.get(i).get(REFERENCE);
                    }
                }*/
                //Crear lista de lugares genera?
                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),SearchDetails.class);
                in.putExtra(NAME, name);
                in.putExtra(REFERENCE, reference);
                in.putExtra(VECINITY, vecinity);
                in.putExtra(ISOPEN, isOpen);
                in.putExtra(RATING, rating);
                in.putExtra(LAT, lat);
                in.putExtra(LNG, lng);
                startActivity(in);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.searchable_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

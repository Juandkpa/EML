package com.example.juan.eml;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by juan on 24/11/14.
 */
public class SearchDetails extends ListActivity implements LocationListener {

    private static final String API_KEY = "AIzaSyBw6xFrZ1IznT6m1wkY9S74zXfR737D8_U";

    private static final String NAME = "name";
    private static final String VECINITY = "vicinity";
    private static final String REFERENCE = "reference";
    private static final String AUTHOR_NAME = "author_name";
    private static final String TEXT = "text";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        Intent in = getIntent();

        String name = in.getStringExtra(NAME);
        String horario = in.getStringExtra(VECINITY);
        String reference = in.getStringExtra(REFERENCE);

        TextView lblName = (TextView) findViewById(R.id.place_Name);
        TextView lblHorario = (TextView) findViewById(R.id.horario);


        lblName.setText(name);
        lblHorario.setText(reference);
        doMySearch(reference);
    }

    public void doMySearch(String reference){

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        sb.append("reference="+reference);
        sb.append("&sensor=true");
        sb.append("&key="+ API_KEY);
        // Creating a new non-ui thread task to download json data
        PlaceDetailTask placesTask = new PlaceDetailTask();
        Log.d("ENTRO A DOMY SEARCH ", "ENTRO LUEGO DE PLACESTASK!!!!!");
        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());

    }

    private class PlaceDetailTask extends AsyncTask<String, Integer,String> {
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

            //List<HashMap<String, String>> places = null;
            List<HashMap<String, String>> reviews = null;
            PlaceDetailsJSONParser detailsJsonParser = new PlaceDetailsJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                reviews = detailsJsonParser.parseReviews(jObject);
               // Log.d("JSONoBJECT",places.toString());
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return reviews;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){

            // Clears all the existing markers
            // mGoogleMap.clear();
          /*  TextView name_author = (TextView) findViewById(R.id.nombreMain);
            TextView text = (TextView) findViewById(R.id.vecinityMain);
            RatingBar ratingMain =  (RatingBar) findViewById(R.id.MainratingBar);*/

            Log.v("CANTIDAD REVIEWS:",String.valueOf(list.size()));
          /*  for(int i=0; i<list.size();i++){
                if(list.get(i).get("name").equals(namePlace)){
                    nameMain.setText(list.get(i).get("name"));
                    vecinityMain.setText(list.get(i).get("vicinity"));
                    ratingMain.setRating(Float.parseFloat(list.get(i).get("rating")));
                    list.remove(i);
                }
            }*/
            //nameMain.setText(list.get(0).get("name"));
            //vecinityMain.setText(list.get(0).get("vicinity"));
            //Log.d("RESULTADOS",list.toString());

            SimpleAdapter adapter = new SimpleAdapter(
                    SearchDetails.this, list, R.layout.list_item_comments, new String[]{AUTHOR_NAME, TEXT}, new int[]{R.id.author_name,R.id.text});

            setListAdapter(adapter);
            //listClicker(list);


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




    @Override
    public void onLocationChanged(Location location) {

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

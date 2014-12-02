package com.example.juan.eml;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
    private static final String ISOPEN = "isOpen";
    private static final String RATING = "rating";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String PHOTO ="photo_reference";
    private static final String MAXWIDTH ="maxWidth";
    private Bitmap img;
    private String param1="";
    private String param2="";
    private ImageView photoDetail;



    private ImageButton btmShowMap;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        photoDetail =(ImageView) findViewById(R.id.photoRest);
        btmShowMap = (ImageButton) findViewById(R.id.toMap);
        Intent in = getIntent();


        final String name = in.getStringExtra(NAME);
        String isOpen = in.getStringExtra(ISOPEN);
        String reference = in.getStringExtra(REFERENCE);
        float rating = in.getFloatExtra(RATING,0);
        final String lat = in.getStringExtra(LAT);
        final String lng = in.getStringExtra(LNG);

        param1= in.getStringExtra(MAXWIDTH);
        param2= in.getStringExtra(PHOTO);

        Log.v("MAXWID",param1);
        Log.v("PHOTO",param2);

        TextView lblName = (TextView) findViewById(R.id.place_Name);
        TextView lblisOpen = (TextView) findViewById(R.id.horario);
        RatingBar barRating = (RatingBar) findViewById(R.id.ratingBarDet);


        lblName.setText(name);
        barRating.setRating(rating);

        if(isOpen.contentEquals("true"))
            lblisOpen.setText("Abierto");
        else
            lblisOpen.setText("Cerrado");
        doMySearch(reference);

        btmShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),MapsActivity.class);
                in.putExtra(LAT,lat);
                in.putExtra(LNG, lng);
                in.putExtra(NAME, name);
                startActivity(in);
            }
        });
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
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            //List<HashMap<String, String>> places = null;
            List<HashMap<String, String>> reviews = null;
            PlaceDetailsJSONParser detailsJsonParser = new PlaceDetailsJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                reviews = detailsJsonParser.parseReviews(jObject);
                // Log.d("JSONoBJECT",places.toString());
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return reviews;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            Log.v("CANTIDAD REVIEWS:", String.valueOf(list.size()));


            SimpleAdapter adapter = new SimpleAdapter(
                    SearchDetails.this, list, R.layout.list_item_comments, new String[]{AUTHOR_NAME, TEXT}, new int[]{R.id.author_name, R.id.text});

            setListAdapter(adapter);
            searchPhoto(param1, param2);
        }
    }

        public void searchPhoto(String... url){

            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
            sb.append("maxwidth="+url[0]);
            sb.append("&photoreference="+url[1]);
            sb.append("&sensor=true");
            sb.append("&key="+API_KEY);
            // Creating a new non-ui thread task to download json data
            PhotoTask photoTask = new PhotoTask();
            Log.d("ENTRO A SEARCH PHOTO ", "ENTRO LUEGO DE LA BUSQUEDA!!!");
            // Invokes the "doInBackground()" method of the class PlaceTask
            photoTask.execute(sb.toString());
        }



        private class PhotoTask extends AsyncTask<String, Integer,String> {
            @Override
            protected String doInBackground(String... url) {
                try{
                    downloadPhoto(url[0]);
                    Log.d("DOWUNLOADURL PHOTO",url[0]);
                }catch(Exception e){
                    Log.d("Backgraound TaskpHOTO",e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result){
            }
        }


        private void downloadPhoto(String strUrl) throws IOException {

            InputStream iStream = null;
            HttpURLConnection urlConnection = null;

            try{
                URL url = new URL(strUrl);
                Log.d("MADE URL:", "HACIENDO URL PHOTO");
                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();
                img = BitmapFactory.decodeStream(iStream);
                photoDetail.setImageBitmap(img);
                Log.v("IMAGE", img.toString());
                iStream.close();

            }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
            }finally{
                iStream.close();
                urlConnection.disconnect();
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

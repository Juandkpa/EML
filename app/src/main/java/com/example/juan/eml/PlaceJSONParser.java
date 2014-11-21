package com.example.juan.eml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by juan on 11/10/14.
 */
public class PlaceJSONParser {

    public List<HashMap<String,String>> parse(JSONObject jObject){

        JSONArray jPlaces = null;

        try{
            jPlaces = jObject.getJSONArray("results");

        }catch(JSONException e){
            e.printStackTrace();
        }

        return getPlaces(jPlaces);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jPlaces){
        int placesCount = jPlaces.length();
        List<HashMap<String,String>> placeList = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> place = null;

        for(int i=0;i<placesCount;i++){
            try{
                place = getPlace((JSONObject)jPlaces.get(i));
                placeList.add(place);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return placeList;
    }


    private HashMap<String, String> getPlace(JSONObject jPlace){

        HashMap<String, String> place = new HashMap<String, String>();

        String placeName = "-NA-";
        String vicinity="-NA-";
        String latitude="";
        String longitude="";

        try{

            //extraer el nombre del lugar si esta disponible
            if(!jPlace.isNull("name"))
                placeName = jPlace.getString("name");

            //Extraer la dirección del lubar si esta disponible
            if(!jPlace.isNull("vicinity"))
                vicinity = jPlace.getString("vicinity");

            latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");


            place.put("name",placeName);
            place.put("vicinity",vicinity);
            place.put("lat",latitude);
            place.put("lng",longitude);

        }catch(JSONException e){
            e.printStackTrace();
        }
        return place;
    }

}

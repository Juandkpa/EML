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
        String rating ="0";
        String isOpen = "0";
        String reference ="-NA-";
        String latitude= "";
        String longitude= "";
        String referencePhoto = "0";
        String maxwidth = "0";

        try{

            //extraer el nombre del lugar si esta disponible
            if(!jPlace.isNull("name"))
                placeName = jPlace.getString("name");

            //Extraer la dirección del lubar si esta disponible
            if(!jPlace.isNull("vicinity"))
                vicinity = jPlace.getString("vicinity");

            if(!jPlace.isNull("rating"))
                rating = jPlace.getString("rating");


            if(!jPlace.isNull("opening_hours")) {
                if (!jPlace.getJSONObject("opening_hours").isNull("open_now"))
                    isOpen = jPlace.getJSONObject("opening_hours").getString("open_now");
            }
            //Extraer la reference para busqueda de detalles especificos
            if(!jPlace.isNull("reference"))
                reference = jPlace.getString("reference");

            if(!jPlace.isNull("photos")) {
                JSONObject photo = (JSONObject) jPlace.getJSONArray("photos").get(0);
                referencePhoto = photo.getString("photo_reference");
                maxwidth = photo.getString("width");
            }

            latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");


            place.put("name",placeName);
            place.put("vicinity",vicinity);
            place.put("rating",rating);
            place.put("isOpen",isOpen);
            place.put("reference",reference);
            place.put("lat",latitude);
            place.put("lng",longitude);
            place.put("photo_reference",referencePhoto);
            place.put("maxWidth", maxwidth);

        }catch(JSONException e){
            e.printStackTrace();
        }
        return place;
    }

}

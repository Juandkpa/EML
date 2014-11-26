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
public class PlaceDetailsJSONParser  {

    public List<HashMap<String,String>> parseReviews(JSONObject jObject) {

        JSONArray jReviews = null;

        try{
            jReviews = jObject.getJSONObject("result").getJSONArray("reviews");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getReviews(jReviews);
    }

    private List<HashMap<String, String>> getReviews(JSONArray jReviews){
        int reviewsCount = jReviews.length();
        List<HashMap<String,String>> reviewList = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> review = null;

        for(int i=0;i<reviewsCount;i++){
            try{
                review = getReview((JSONObject)jReviews.get(i));
                reviewList.add(review);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return reviewList;
    }

    private HashMap<String, String> getReview(JSONObject jReview){

        HashMap<String, String> review = new HashMap<String, String>();

        String author_Name = "-NA-";
        String text="-NA-";

        try{

            //extraer el nombre del author del comentario si está disponible
            if(!jReview.isNull("author_name"))
                author_Name = jReview.getString("author_name");

            //Extraer la dirección del lubar si esta disponible
            if(!jReview.isNull("text"))
                text = jReview.getString("text");

            review.put("author_name",author_Name);
            review.put("text",text);

        }catch(JSONException e){
            e.printStackTrace();
        }
        return review;
    }

}

package com.example.juan.eml;

import android.media.Rating;
import android.view.View;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;

/**
 * Created by juan on 25/11/14.
 */
public class MyBinder implements SimpleAdapter.ViewBinder {

    @Override
    public boolean setViewValue(View view, Object o, String s) {
        if(view.getId() == R.id.rating){
            String stringval = (String) o;
            float ratingValue = Float.parseFloat(stringval);
            RatingBar ratingBar = (RatingBar) view;
            ratingBar.setRating(ratingValue);
            return true;
        }
        return false;
    }
}

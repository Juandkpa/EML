package com.example.juan.eml;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;


public class MyActivity extends Activity  {

    private ShowCamera cameraView; //cameraSurfaceView
    private ImageView imageResult;
    private FrameLayout frameNew;
    private Button snapPhoto;
    private boolean takePicture = false;
    //private Bitmap image = null;
    //private Camera cameraObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        setupCamera();


        Log.d("LA PRINCIPAL", "ENTRO!!!");
        //Para hacer busqueda
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.searchWidget);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
    }

    public void setupCamera(){

        cameraView = new ShowCamera(getApplicationContext());
        imageResult = new ImageView(getApplicationContext());

        imageResult.setBackgroundColor(Color.GRAY);

        frameNew = (FrameLayout) findViewById(R.id.camera_preview);
        snapPhoto = (Button) findViewById(R.id.button_capture);

        frameNew.addView(imageResult);
        frameNew.addView(cameraView);
        frameNew.bringChildToFront(imageResult);
    }

    public void captureHandler(View view){

        if(takePicture){
            cameraView.snapIt(jpegHandler);
        }else{
            takePicture = true;
            frameNew.bringChildToFront(cameraView);
            imageResult.setImageBitmap(null);
            snapPhoto.setText("capture");
        }
    }


    private Camera.PictureCallback jpegHandler = new PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data, Camera camera){

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageResult.setImageBitmap(bitmap);
            frameNew.bringChildToFront(imageResult);
            snapPhoto.setText("Take it");
            takePicture = false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);

        return super.onCreateOptionsMenu(menu);
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

  /*  public static Camera isCameraAvailiable(){
        Camera object = null;
        try{
            object = Camera.open();
        }catch(Exception e){}
        return object;
    }*/





}

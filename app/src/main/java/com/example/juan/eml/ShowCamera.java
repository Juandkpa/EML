package com.example.juan.eml;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by juan on 11/10/14.
 */


public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback{



    public static int STATE_PREVIEW;
    private SurfaceHolder mHolder;
    private Camera camera = null;

    public ShowCamera(Context context) {
        super(context);
        //theCamera = camera;

        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if(camera == null) {
            camera = camera.open();
        }
        try {
            camera.setPreviewDisplay(holder);

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

        Camera.Size result = null;
        for (int i=0;i<previewSizes.size();i++){
            result = (Camera.Size) previewSizes.get(i);
            Log.i("PictureSize", "Supported Size.Width: " + result.width + "height: " + result.height);
        }
        Camera.Size previewSize = previewSizes.get(0);
        parameters.setPreviewSize(previewSize.width,previewSize.height);

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);


        if (parameters.getMaxNumMeteringAreas() > 0){ // check that metering areas are supported
            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();

            Rect areaRect1 = new Rect(-100, -100, 100, 100);    // specify an area in center of image
            meteringAreas.add(new Camera.Area(areaRect1, 600)); // set weight to 60%
            Rect areaRect2 = new Rect(800, -1000, 1000, -800);  // specify an area in upper right of image
            meteringAreas.add(new Camera.Area(areaRect2, 400)); // set weight to 40%
            parameters.setMeteringAreas(meteringAreas);
        }
        camera.setParameters(parameters);
        camera.startPreview();
        /*Camera.Parameters params = camera.getParameters();


        params.setPreviewSize(width,height);
        camera.setParameters(params);
        camera.startPreview();*/
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera = null;
    }

    public void snapIt(Camera.PictureCallback jpegHandler){
           camera.takePicture(null, null, jpegHandler);
    }

}

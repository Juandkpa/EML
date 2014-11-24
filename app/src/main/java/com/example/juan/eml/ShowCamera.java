package com.example.juan.eml;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
//            theCamera.startPreview();


        }catch(Exception e){}

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Camera.Parameters params = camera.getParameters();
        params.setPreviewSize(width,height);
        camera.setParameters(params);
        camera.startPreview();



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

package com.example.juan.eml;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import org.apache.http.protocol.HTTP;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MyActivity extends Activity  {

    private ShowCamera cameraView; //cameraSurfaceView
    private ImageView imageResult;
    private FrameLayout frameNew;
    private Button snapPhoto;
    private boolean takePicture = false;
    private Context mContext = this;
    private SearchView searchView;
    //private Bitmap image = null;
    //private Camera cameraObject;

    private static final String TAG = "EMLMyActivity";
    private final String SERVERURL = "http://107.170.146.82/testTess.php";


    private final static String INPUT_IMG_FILENAME = "/temp.jpg"; //name for storing image captured by camera view


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        setupCamera();


        Log.d("LA PRINCIPAL", "ENTRO!!!");
        //Para hacer busqueda
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.searchWidget);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        //searchView.setQuery("hola",false);
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
            if(data != null){
                Intent mIntent = new Intent();
                //Compress image
                compressByteImage(mContext, data, 75);
                setResult(0, mIntent);

                //** Send image and offload image processing task  to server by starting async task **
                ServerTask task = new ServerTask();
                task.execute( Environment.getExternalStorageDirectory().toString() +INPUT_IMG_FILENAME);
                Log.v("FILE NAME: ", INPUT_IMG_FILENAME);
            }

            //Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            //
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


    //store the image as a jpeg image
    public  boolean compressByteImage(Context mContext, byte[] imageData,
                                      int quality) {
        File sdCard = Environment.getExternalStorageDirectory();
        FileOutputStream fileOutputStream = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;  	//no downsampling
            Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,imageData.length,options);
            imageResult.setImageBitmap(myImage);
            fileOutputStream = new FileOutputStream(sdCard.toString() +INPUT_IMG_FILENAME);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //compress image to jpeg
            myImage.compress(Bitmap.CompressFormat.JPEG, quality, bos);

            bos.flush();
            bos.close();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            e.printStackTrace();
        }
        return true;
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


    //*******************************************************************************
    //Push image processing task to server
    //*******************************************************************************

    public class ServerTask  extends AsyncTask<String, Integer , Void> {

        public byte[] dataToServer;
        private String result;

        //Task state
        private final int UPLOADING_PHOTO_STATE = 0;
        private final int SERVER_PROC_STATE = 1;

        private ProgressDialog dialog;

        HttpURLConnection uploadPhoto(FileInputStream fileInputStream){

            final String serverFileName = "test"+ (int) Math.round(Math.random()*1000)+".jpg";
            final String lineEnd ="\r\n";
            final String twoHyphens = "--";
            final String boundary ="*****";

            try{
                URL url =  new URL(SERVERURL);
                // Open a HTTP connection to the URL
                final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // Allow inputs
                conn.setDoInput(true);
                // Allow outputs
                conn.setDoOutput(true);
                // Don't use a cached copy.
                conn.setUseCaches(false);

                //use a post method.
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);

                DataOutputStream dos = new DataOutputStream( conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""+ serverFileName + "\"" +lineEnd);
                dos.writeBytes(lineEnd);

                //create a buffer of maximum size
                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte [bufferSize];

                //read file and write it into form
                int bytesRead = fileInputStream.read(buffer, 0 , bufferSize);

                while(bytesRead > 0){

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0 , bufferSize);

                }

                // send multipart form data after file data
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                publishProgress(SERVER_PROC_STATE);
                //close streams
                dos.flush();
                fileInputStream.close();

                return conn;

            } catch (MalformedURLException e) {
                Log.e(TAG, "error" + e.getMessage(), e);
                return null;
            } catch (ProtocolException e) {
                Log.e(TAG, "error" + e.getMessage(), e);
                return null;
            } catch (IOException e) {
                Log.e(TAG, "error" + e.getMessage(), e);
                return null;
            }
        }

        //get text result from server
        String getResultText(HttpURLConnection conn){
            InputStream is;
            String data = "";
            try{
                    is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuffer sb  = new StringBuffer();
                    String line = "";

                    while( ( line = br.readLine())  != null){
                    sb.append(line);
                    }
                    data = sb.toString();

                    Log.v("RESULTADO",data.toString());



                    //como sacar texto del txt???
                    is.close();
             return data.toString();

            }catch (IOException e) {
                Log.e(TAG,e.toString());
                e.printStackTrace();
                return null;
            }
        }

        //Main code for processing image algorithm on the server

        void imageOCR(String inputImageFilePath){
            publishProgress(UPLOADING_PHOTO_STATE);
            File inputFile = new File(inputImageFilePath);

            try {
                //create file stream for captured image file
                FileInputStream fileInputStream = new FileInputStream(inputFile);

                //upload photo
                final HttpURLConnection conn = uploadPhoto(fileInputStream);
                   Log.v("FILE PHONETA", inputFile.toString());
                //get processed photo from server

                if(conn != null){
                    result = getResultText(conn);}
                        fileInputStream.close();

            } catch (FileNotFoundException e) {
                Log.e(TAG, e.toString());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }

        public ServerTask(){
            dialog = new ProgressDialog(mContext);
        }

        protected void onPreExecute(){
            this.dialog.setMessage("Photo captured");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            String uploadFilePath = strings[0];
            imageOCR(uploadFilePath);
            //release camera when previous image is processed ***Falta hacerlo**
            return null;
        }
        //progres update, display dialogs
        @Override
        protected void onProgressUpdate(Integer ... progress){
            if(progress[0] == UPLOADING_PHOTO_STATE){
                dialog.setMessage("uploading");
                dialog.show();

            }else if(progress[0] == SERVER_PROC_STATE){
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                dialog.setMessage("Processing");
                dialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void param){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            searchView.setQuery(result,false);

            Log.v("RESULTADO EN POST:", result);
        }

    }


}

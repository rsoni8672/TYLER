package com.example.android.tyler;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonObject;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
Button srtbutton, stopbtn , playbtn, pausenbtn;
String savingpath ="", id;
MediaRecorder mediaRecorder;
MediaPlayer mediaPlayerl;

String Key;
String filepath;
TextView textView;
Spinner spinner;
List<String> spinnerValues;
JsonObject jsonObject;
    String Hello_World;
private  FileUploadService fileUploadService;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
srtbutton = (Button) findViewById(R.id.start_playing);
stopbtn = (Button) findViewById(R.id.stop);

playbtn = (Button) findViewById(R.id.play);
pausenbtn = (Button) findViewById(R.id.pause);
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();}

                    else{
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();}
                break;
            }

        }
    }

    final int REQUEST_PERMISSION_CODE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayerl = new MediaPlayer();
        textView = (TextView) findViewById(R.id.test_view);

            if(check_device_permission()){
            Toast.makeText(MainActivity.this, "STOP Speaking", Toast.LENGTH_SHORT).show();
         }
        else{
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION_CODE);
        }

        spinner = (Spinner) findViewById(R.id.emotion_spinner);
        spinnerValues = new ArrayList<String>();
        spinnerValues.add("Happy");
        spinnerValues.add("Sad");
        spinnerValues.add("Depressed");
        spinnerValues.add("Angry");
        spinnerValues.add("Anxious");
        spinnerValues.add("Frustrated");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerValues);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    private boolean check_device_permission(){
        int write  = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
       int record = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
       int read  = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
     return write == PackageManager.PERMISSION_GRANTED && record == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
    }

    public void setupMediaRecorder()
    {
mediaRecorder = new MediaRecorder();
mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
mediaRecorder.setOutputFile(savingpath);


    }

    public void startrecording(View view)
//       startActivity(new Intent(MainActivity.this, Popup.class));
    {      savingpath  = Environment.getExternalStorageDirectory().getAbsolutePath()
            +"/"+ UUID.randomUUID().toString() + "audio_record.wav";


        setupMediaRecorder();
        Toast.makeText(MainActivity.this, "Start Speaking", Toast.LENGTH_SHORT).show();
        try
        {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
            startActivityForResult(intent, 100);


        }
        catch (ActivityNotFoundException a) {
        Toast.makeText(getApplicationContext(),
                "Sorry your device not supported",
                Toast.LENGTH_SHORT).show();}
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                    Hello_World = result.get(0);
                    Log.i("Sting", Hello_World);


              stoprecording();
                break;
        }
    }


    }
    public  void stoprecording(){
        mediaRecorder.stop();
        Toast.makeText(MainActivity.this, "Saved as "+ savingpath, Toast.LENGTH_SHORT).show();
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(300, TimeUnit.SECONDS)
                    .build();
          Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                    .baseUrl("https://74a4b8a8.ngrok.io/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            fileUploadService = retrofit.create(FileUploadService.class);
            uploadFile();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }




    public  void startplying(View view){
//        Log.d("PATH", savingpath);


try {


    mediaPlayerl.setDataSource(savingpath);
    mediaPlayerl.prepare();
    mediaPlayerl.start();
}catch (IOException e )
{
    e.printStackTrace();
}

    }
    public  void stoplaying(View view){
        if(mediaPlayerl!= null) {

            mediaPlayerl.stop();
            mediaPlayerl.release();
            setupMediaRecorder();
        }

    }


    private void uploadFile() {
        File file = new File(savingpath);
        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        Call<Audio> call = fileUploadService.uploadFile(fileToUpload, filename,Hello_World );
        call.enqueue(new Callback<Audio>() {
            @Override
            public void onResponse(Call<Audio> call, retrofit2.Response<Audio> response) {
                retrofit2.Response<Audio> audio = response;
                Log.d("Success" , "Message" + audio.toString());
                try {
                    Audio audio1 = response.body();
                    JsonObject json = audio1.getData();

                    JsonObject post = response.body().getData();
                    filepath = post.get("filepath").getAsString();

                           Log.d("Correct" ,post.toString() + "File Path" + filepath);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                textView.append("Done " + response.message());
            }

            @Override
            public void onFailure(Call<Audio> call, Throwable t) {
                Log.d("From Failure", t.getMessage());
                textView.append("Message"+t.getMessage().toString());
            }
        });
    }

    public  void  submitresponse(View view){
        Toast.makeText(MainActivity.this, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
    .connectTimeout(2, TimeUnit.MINUTES)
    .readTimeout(20, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build();
    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://74a4b8a8.ngrok.io")
            .addConverterFactory(GsonConverterFactory.create()).build();

fileUploadService = retrofit.create(FileUploadService.class);

final Call<ResponseClass> final_response = fileUploadService.save_audio("Rahul", filepath,spinner.getSelectedItem().toString());
final_response.enqueue(new Callback<ResponseClass>() {
    @Override
    public void onResponse(Call<ResponseClass> call, retrofit2.Response<ResponseClass> response) {
        if(!response.isSuccessful())
        {
            Log.d("Not Success" , response.toString());
            return;
        }
        Log.d("Success", response.message());

    }

    @Override
    public void onFailure(Call<ResponseClass> call, Throwable t) {
Log.d("Failure", t.getMessage());
    }
});
    }
}

package com.sourcey.materiallogindemo;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class Trends extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_FILE_REQUEST = 234;

    private Button buttonChoose;
    private Button buttonUpload;
    private Button button3;
    private TextView path;
    private TextView textView9;
    private Uri filePath;
    String stringUri;
    String temp;

    public WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trends);

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        button3 = (Button)findViewById(R.id.button3);
        path = (TextView) findViewById(R.id.path);
        textView9 = (TextView)findViewById(R.id.textView9);
        //attaching listener
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        button3.setOnClickListener(this);




    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("text/csv");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select .csv File"), PICK_FILE_REQUEST);
        buttonUpload.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            stringUri = path.toString();

            path.setText("CSV file selected! Click the Upload button Below");


        }
    }

    @Override
    public void onClick(View view) {
        //if the clicked button is choose
        if (view == buttonChoose) {
            showFileChooser();
        }
        //if the clicked button is upload
        else if (view == buttonUpload) {
            uploadFile();

        }
        //if the button clicked is Generate
        else if (view == button3) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://voluncheer-d330e.appspot.com");
            StorageReference trendsRef = storageRef.child("Trends/Trends1.csv");
            trendsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri downloadUrl) {
                   temp= downloadUrl.toString();
                    String url = "http://datacopia.com?data=" + Uri.encode(temp);
                    setContentView(R.layout.generate_trends);
                    mWebView = (WebView) findViewById(R.id.newwebview);
                    mWebView.loadUrl(url);

                    // Enable Javascript
                    WebSettings webSettings = mWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);

                    // Force links and redirects to open in the WebView instead of in a browser
                    mWebView.setWebViewClient(new WebViewClient());
                    ProgressDialog dialog = new ProgressDialog(Trends.this); // this = YourActivity
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Loading Trends... \nPlease wait for a minute...");
                    dialog.setIndeterminate(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    timerDelayRemoveDialog(60000,dialog);


                }

            });
        }


    }
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://voluncheer-d330e.appspot.com");
            StorageReference trendsRef = storageRef.child("Trends/Trends1.csv");

            trendsRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.setMessage("Uploading selected CSV File...");
                            textView9.setText("File Successfully Uploaded! Click 'Generate Trends' Button below");
                            button3.setVisibility(View.VISIBLE);
                        }
                    });
        }
        //if there is not any file
        else {
            Toast.makeText(getApplicationContext(), "Error : File not Selected!", Toast.LENGTH_LONG).show();
        }
    }
    public void timerDelayRemoveDialog(long time, final Dialog d){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }

}

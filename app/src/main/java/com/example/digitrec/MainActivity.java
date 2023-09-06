package com.example.digitrec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    // Declare UI components and variables
    ImageView clear,getImage,copy;
    EditText recgText;
    Uri imageUri;

    TextRecognizer textRecognizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        clear=findViewById(R.id.Clear);
        getImage=findViewById(R.id.camera);
        copy=findViewById(R.id.Copy);
        recgText=findViewById(R.id.Text);
        // Initialize TextRecognizer with default options
        textRecognizer=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        // Event Listener for  buttons
        getImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ImagePicker.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=recgText.getText().toString();
                if(text.isEmpty()){
                    Toast.makeText(MainActivity.this,"There is no text",Toast.LENGTH_LONG);
                }else{
                    ClipboardManager clipboardManager=(ClipboardManager) getSystemService(MainActivity.this.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("Data",recgText.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(MainActivity.this,"Text copy to Clipboard",Toast.LENGTH_LONG);
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text= recgText.getText().toString();
                if(text.isEmpty())
                {
                    Toast.makeText(MainActivity.this,"there is no text to clear",Toast.LENGTH_LONG);
                }
                else{
                    recgText.setText("");
                }
            }
        });
    }

    // Handle the result from the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {  // resultCode ile kontrol edilmeli
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                Toast.makeText(this, "Image selected", Toast.LENGTH_LONG).show();
                recognizeText();
            } else {
                Toast.makeText(this, "Data or data URI is null", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_LONG).show();
        }
    }
    // Function to recognize text from the image URI using ML Kit
    private void recognizeText() {
        if(imageUri!=null){
            try{
                // Create InputImage object from URI
                InputImage inputImage=InputImage.fromFilePath(MainActivity.this,imageUri);
                // Process image using TextRecognizer
                Task<Text> result=textRecognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>(){
                            @Override
                            public void onSuccess(Text text){
                                String recognizetext=text.getText();
                                recgText.setText(recognizetext);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
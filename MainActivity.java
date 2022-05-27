package com.abhirathore.chatbot_with_dialogflow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.widget.Space;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener {

    private static final int PERMISSION_REQUEST_AUDIO = 0;
    private static final String TAG = "MainActivity";
    private FloatingActionButton listenButton;
    private AIConfiguration config;
    private AIService aiService;
    LinearLayout linearLayout;
    LinearLayout.LayoutParams layoutParams1;
    LinearLayout.LayoutParams layoutParams2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout=findViewById(R.id.linear_layout);
        layoutParams1=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams2=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        listenButton = findViewById(R.id.mic_button);

        config = new AIConfiguration("b169b91cf0384532a5dcac61b897de2c",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenButton.setImageResource(R.drawable.listen);
                listen();
            }
        });
    }

    private void listen() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            aiService.startListening();
        } else {
            // Permission is missing and must be requested.
            requestAudioPermission();
        }
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();

        TextView textView_user=new TextView(this);
        layoutParams1.gravity=Gravity.END;
        textView_user.setLayoutParams(layoutParams1);
        textView_user.setText(result.getResolvedQuery());
        textView_user.setFocusable(false);
        textView_user.setTextSize(20);
        textView_user.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.user, 0);
        this.linearLayout.addView(textView_user);

        TextView response_textview= new TextView(this);
        layoutParams2.gravity=Gravity.START;
        response_textview.setLayoutParams(layoutParams2);
        response_textview.setText(result.getFulfillment().getSpeech());
        response_textview.setFocusable(false);
        response_textview.setTextSize(20);
        response_textview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.chatbot, 0, 0, 0);
        this.linearLayout.addView(response_textview);


    }

    @Override
    public void onError(AIError error) {
        Log.d(TAG, "Listening error: " + error.getMessage());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {}

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {
        listenButton.setImageResource(R.drawable.mic);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_AUDIO) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                aiService.startListening();
            } else {
                Log.d(TAG, "permission denied");
            }
        }
    }

    private void requestAudioPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(findViewById(R.id.main_container), getString(R.string.permission_text_audio),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.ok), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            PERMISSION_REQUEST_AUDIO);
                }
            }).show();

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_AUDIO);
        }
    }

}
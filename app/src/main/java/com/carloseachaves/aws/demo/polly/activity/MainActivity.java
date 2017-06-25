package com.carloseachaves.aws.demo.polly.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import com.carloseachaves.aws.demo.polly.util.Media;
import com.carloseachaves.aws.demo.polly.util.MediaInterface;
import com.carloseachaves.aws.demo.polly.util.Polly;
import com.carloseachaves.aws.demo.polly.R;
import com.carloseachaves.aws.demo.polly.adpter.SpinnerVoiceAdapter;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.Voice;
import java.util.List;

public class MainActivity extends Activity  implements View.OnClickListener,MediaInterface {

    private static final String TAG = "PollyDemo";

    private ProgressBar progressBar;
    private Spinner voicesSpinner;
    private EditText sampleEditText;
    private Button readButton;

    private int selectedPosition;
    private Polly polly;
    private Media media;
    private AmazonPollyPresigningClient client;
    private List<Voice> voices;

    private class GetPollyVoices extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (voices == null) {

                DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();
                DescribeVoicesResult describeVoicesResult;
                try {
                    // Synchronously ask the Polly Service to describe available TTS voices.
                    describeVoicesResult = client.describeVoices(describeVoicesRequest);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Unable to get available voices. " + e.getMessage());
                    return null;
                }

                // Get list of voices from the result.
                voices = describeVoicesResult.getVoices();

                // Log a message with a list of available TTS voices.
                Log.i(TAG, "Available Polly voices: " + voices);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (voices != null) {
                voicesSpinner.setAdapter(new SpinnerVoiceAdapter(MainActivity.this, voices));
                progressBar.setVisibility(View.INVISIBLE);
                voicesSpinner.setVisibility(View.VISIBLE);
                readButton.setEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById();

        polly = new Polly(getApplicationContext());
        client = polly.initPollyClient();

        media = new Media(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getVoices();
    }

    private void getVoices() {

        progressBar.setVisibility(View.VISIBLE);

        // Asynchronously get available Polly voices.
        new GetPollyVoices().execute();
    }

    private void play(String textToRead){
        readButton.setEnabled(false);

        Voice selectedVoice = (Voice) voicesSpinner.getSelectedItem();

        // Use voice's sample text if user hasn't provided any text to read.
        if (textToRead.trim().isEmpty()) {
            textToRead = sampleEditText.getHint().toString();
        }

        String url = polly.getUrlAudioStream(textToRead, selectedVoice);
        media.play(url);
    }

    private void findViewById(){

        voicesSpinner = (Spinner) findViewById(R.id.voicesSpinner);
        sampleEditText = (EditText) findViewById(R.id.sampleText);
        progressBar = (ProgressBar) findViewById(R.id.voicesProgressBar);

        readButton = (Button) findViewById(R.id.readButton);
        readButton.setOnClickListener(this);

        voicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    Voice selectedVoice = (Voice) voicesSpinner.getSelectedItem();
                    setHint(selectedVoice);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setHint(Voice selectedVoice) {
        if (selectedVoice != null) {
            String sampleText = polly.getSampleText(selectedVoice);
            sampleEditText.setHint(sampleText);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.readButton:
                String textToRead = sampleEditText.getText().toString();
                play(textToRead);
                break;
        }
    }

    @Override
    public void callbackMedia() {
        readButton.setEnabled(true);
    }

}

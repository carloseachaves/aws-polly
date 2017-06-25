package com.carloseachaves.aws.demo.polly.util;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;
import java.net.URL;

/**
 * Created by carloseachaves on 24/06/2017.
 */

public class Polly {
    private static final String TAG = Polly.class.getName();

    private Context context;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonPollyPresigningClient client;

    // Cognito pool ID. For this app, pool needs to be unauthenticated pool with Amazon Polly permissions.
    private static final String COGNITO_POOL_ID = "";
    private static final Regions MY_REGION = Regions.US_EAST_1;

    public Polly(Context context) {
        this.context = context;
    }

    public AmazonPollyPresigningClient initPollyClient() {

        // Initialize the Amazon Cognito credentials provider.
        credentialsProvider = new CognitoCachingCredentialsProvider(context, COGNITO_POOL_ID, MY_REGION);

        // Create a client that supports generation of presigned URLs.
        client = new AmazonPollyPresigningClient(credentialsProvider);

        return client;
    }

    public String getUrlAudioStream(String textToRead, Voice selectedVoice){

        // Create speech synthesis request.
        SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                new SynthesizeSpeechPresignRequest()
                        .withText(textToRead)
                        .withVoiceId(selectedVoice.getId())
                        .withOutputFormat(OutputFormat.Mp3);

        // Get the presigned URL for synthesized speech audio stream.
        URL presignedSynthesizeSpeechUrl =
                client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);

        Log.i(TAG, "Playing speech from presigned URL: " + presignedSynthesizeSpeechUrl);

        return presignedSynthesizeSpeechUrl.toString();
    }

    public String getSampleText(Voice voice) {

        if (voice == null) {
            return "";
        }

        String resourceName = "sample_" +
                voice.getLanguageCode().replace("-", "_").toLowerCase() + "_" +
                voice.getId().toLowerCase();
        int sampleTextResourceId =
                context.getResources().getIdentifier(resourceName, "string", context.getPackageName());
        if (sampleTextResourceId == 0)
            return "";

        return context.getString(sampleTextResourceId);
    }

}

package com.hotmail.ghazaljaved1993.ai_practice;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.gson.JsonElement;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AIListener, TextToSpeech.OnInitListener{

    Button btnRequest;
    Button btnStop;
    TextView txtResult;
    TextView txtInput;
//    private AIService aiService;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    AIRequest aiRequest;
    AIDataService aiDataService;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRequest = (Button) findViewById(R.id.btnRequest);
        btnStop = (Button) findViewById(R.id.btnStop);
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtInput = (TextView) findViewById(R.id.txtInput);

        tts = new TextToSpeech(this, this);


        final AIConfiguration config = new AIConfiguration("6a755bf4f7a24df894794c74a0b1b67f",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

//        aiService = AIService.getService(this, config);
//        aiService.setListener(this);
        aiDataService = new AIDataService(config);

        aiRequest = new AIRequest();
//        aiRequest.setQuery("What is the weather in karachi tomorrow");
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
//                aiService.startListening();
//                new AsyncTask<AIRequest, Void, AIResponse>() {
//                    @Override
//                    protected AIResponse doInBackground(AIRequest... requests) {
//                        final AIRequest request = requests[0];
//                        try {
//                            final AIResponse response = aiDataService.request(aiRequest);
//                            return response;
//                        } catch (AIServiceException e) {
//                        }
//                        return null;
//                    }
//                    @Override
//                    protected void onPostExecute(AIResponse aiResponse) {
//                        if (aiResponse != null) {
//                            // process aiResponse here
////                            Toast.makeText(getApplicationContext(), aiResponse+"", Toast.LENGTH_SHORT).show();
//                            Result result = aiResponse.getResult();
//
//                            // Get parameters
//                            String parameterString = "";
//                            if (result.getParameters() != null && !result.getParameters().isEmpty()) {
//                                for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
//                                    parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
//                                }
//                            }
//
//                            // Show results in TextView.
//                            txtResult.setText("Query:" + result.getResolvedQuery() +
//                                    "\nAction: " + result.getAction() +
//                                    "\nParameters: " + parameterString);
//
//                        }
//                    }
//                }.execute(aiRequest);
            }
        });

    }

    @Override
    public void onResult(AIResponse response) {

        Result result = response.getResult();

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        // Show results in TextView.
        txtResult.setText("Query:" + result.getResolvedQuery() +
                "\nAction: " + result.getAction() +
                "\nParameters: " + parameterString);

    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    /**
     * START - FOR SPEECH TO TEXT
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech_not_supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtInput.setText(getString(R.string.text_input, result.get(0)));
//                    if(result.get(0).equals("close"))
//                    {
//                        finish();
//                    }
                    aiRequest.setQuery(result.get(0));
                    new AsyncTask<AIRequest, Void, AIResponse>() {
                        @Override
                        protected AIResponse doInBackground(AIRequest... requests) {
                            final AIRequest request = requests[0];
                            try {
                                final AIResponse response = aiDataService.request(aiRequest);
                                return response;
                            } catch (AIServiceException e) {
                            }
                            return null;
                        }
                        @Override
                        protected void onPostExecute(AIResponse aiResponse) {
                            if (aiResponse != null) {
                                // process aiResponse here
                                Result result = aiResponse.getResult();

                                // Get parameters
                                String parameterString = "";
                                if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                                    for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                                        parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
                                    }
                                }

                                txtResult.setText("Query:" + result.getResolvedQuery() +
                                        "\nAction: " + result.getAction() +
                                        "\nParameters: " + parameterString);
                                String speechResponse = result.getFulfillment().getSpeech();
                                speakTextToUser(speechResponse);

                            }
                        }
                    }.execute(aiRequest);
                }
                break;
            }

        }
    }

    /*
    *
    * START - FOR TEXT TO SPEECH
    *
    */
    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
//                btnSpeak.setEnabled(true);
                speakTextToUser("Hello there");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private  void speakTextToUser(String text)
    {

//        tts.setPitch(0.6f);
        tts.setLanguage(Locale.CHINESE);
//        tts.setSpeechRate(2);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    /*
     
     * END - FOR TEXT TO SPEECH
     
    */

}

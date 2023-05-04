package com.speechrecognition;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.Locale;
import java.util.ArrayList;;

public class SpeechRecognitionModule extends ReactContextBaseJavaModule {
    private Promise mPickerPromise;
    private ReactApplicationContext mContext ;
    public static final Integer PERMISSION_RECORD_AUDIO_REQUEST = 1;
    private static final String E_ACTIVITY_DOES_NOT_EXIST = "ERROR";
    private static final String E_FAILED_TO_START_SPEECH = "E_FAILED_TO_START_SPEECH";
    private SpeechRecognizer speechRecognizer = null;
    private Intent speechRecognizerIntent = null;
    private ArrayList<String> data = null;
    String speackedString = "";
  
    SpeechRecognitionModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }
  
    @Override
    public String getName() {
        return "SpeechRecognitionModule";
    }

    @ReactMethod
    public void startListening(final Promise promise) {
        Activity currentActivity = getCurrentActivity();
    
        if (currentActivity == null) {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }
        // Store the promise to resolve/reject when picker returns data
        mPickerPromise = promise;
    
        try {
            // Get a handler that can be used to post to the main thread
            Handler mainHandler = new Handler(mContext.getMainLooper());
            
            Runnable myRunnable = new Runnable() {
                @Override 
                public void run() {
                    try{
                        if((speechRecognizer==null && speechRecognizerIntent==null)){
                            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
                            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                            //speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
    
                            if(!speechRecognizer.isRecognitionAvailable(mContext)){
                                speechRecognizer = null;
                                speechRecognizerIntent = null;
                                mPickerPromise.reject(E_FAILED_TO_START_SPEECH,"Error => Speech recognition is not available.");
                                mPickerPromise = null;
                                return;
                            }
    
                            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                                @Override
                                public void onReadyForSpeech(Bundle bundle) {
                    
                                }
                    
                                @Override
                                public void onBeginningOfSpeech() {
                                }
                    
                                @Override
                                public void onRmsChanged(float v) {
                    
                                }
                    
                                @Override
                                public void onBufferReceived(byte[] bytes) {
                    
                                }
                    
                                @Override
                                public void onEndOfSpeech() {
                                
                                }
                    
                                @Override
                                public void onError(int i) {
                                    speechRecognizer.stopListening();
                                    speechRecognizer.destroy();
                                    speechRecognizer = null;
                                    speechRecognizerIntent = null;
                                    data = null;
                                    mPickerPromise.reject(E_FAILED_TO_START_SPEECH,"Error => "+getErrorText(i)+","+speackedString);
                                    speackedString = "";
                                    mPickerPromise = null;
                                }
                    
                                @Override
                                public void onResults(Bundle result) {
                                    data = result.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                                    speackedString += data.get(0);
                                    speechRecognizer.stopListening();
                                    speechRecognizer.destroy();
                                    speechRecognizer = null;
                                    speechRecognizerIntent = null;
                                    data = null;
                                    mPickerPromise.resolve(speackedString);
                                    speackedString = "";
                                    mPickerPromise = null;
                                }
                    
                                @Override
                                public void onPartialResults(Bundle partialResults) {
                                
                                }
                    
                                @Override
                                public void onEvent(int i, Bundle bundle) {
                    
                                }
                            });
    
                            speechRecognizer.startListening(speechRecognizerIntent);
                        }
                    }
                    catch(Exception e){
                        mPickerPromise.reject(E_FAILED_TO_START_SPEECH,e);
                        mPickerPromise = null;
                    }
                } // This is your code
            };
            mainHandler.post(myRunnable);    
        } catch (Exception e) {
            mPickerPromise.reject(E_FAILED_TO_START_SPEECH,e);
            mPickerPromise = null;
        }
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
           case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                 message = "Didn't understand, please try again.";
            break;
        }
        return message;
    }
}

package com.example.fuelid;

import java.util.Locale;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;


public class VoiceHelper extends Service implements
TextToSpeech.OnInitListener, OnUtteranceCompletedListener {

    /** Called when the activity is first created. */
	  public String voz;
      private TextToSpeech tts;
      @Override
      public void onStart(Intent intent, int startId) {
          // TODO Auto-generated method stub
          super.onStart(intent, startId);
          if(intent!=null){      

          voz="";
          voz = intent.getStringExtra("voz");
          tts = new TextToSpeech(this, this);
          // button on click event
          speakOut(voz);
          }else{ this.stopSelf();}
      }


    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
        	 Locale spanish = new Locale("es", "ES");
        	 tts.setOnUtteranceCompletedListener((OnUtteranceCompletedListener) this);
        	 int result = tts.setLanguage(spanish);
            if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(voz);
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
    public void onUtteranceCompleted(String utteranceId) {
    	   tts.stop();
           tts.shutdown();
           this.stopSelf();
    	   }
    private void speakOut(String voz) {
        tts.speak(voz, TextToSpeech.QUEUE_FLUSH, null);   
       
     
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}


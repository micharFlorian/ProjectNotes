package com.example.projectnotes.complements;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 *Clase que convierte un String a Voz
 */
public class TtsManager {

    private TextToSpeech mTts = null;
    private boolean isLoaded = false;

    /**
     * Inicializa el atributo mTts
     */
    public void init(Context context) {
        try {
            mTts = new TextToSpeech(context, onInitListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga el Idioma (castellano) y reproduce a voz un string
     */
    private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            Locale spanish = new Locale("es", "ES");
            if (status == TextToSpeech.SUCCESS) {
                int result = mTts.setLanguage(spanish);
                isLoaded = true;

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("error", "Este Lenguaje no esta permitido");
                }
            } else {
                Log.e("error", "Fallo al Inicilizar");
            }
        }
    };

    /**
     * Apaga el objeto para aue no consuma recursos del sistema
     */
    public void shutDown() {
        mTts.shutdown();
    }

    /**
     * Detiene la reproducción
     */
    public void stop() {
        mTts.stop();
    }

    /**
     * Carga el string que se va reproducir a voz
     */
    public void initQueue(String text) {

        if (isLoaded)
            mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        else
            Log.e("error", "Fallo al Inicilizar");
    }
}

package com.example.testingapplication;
import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Vocabulary {
    private Map<String, Integer> vocabulary;

    public Vocabulary(Context context) {
        this.vocabulary = loadVocabulary(context, "vocab.txt");
    }

    private Map<String, Integer> loadVocabulary(Context context, String fileName) {
        Map<String, Integer> vocab = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                vocab.put(line, index++);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vocab;
    }

    public Map<String, Integer> getVocabulary() {
        return vocabulary;
    }
}

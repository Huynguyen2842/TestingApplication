package com.example.testingapplication;

import android.content.Context;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Tokenizer {
    private Map<String, Integer> vocab;
    private int oovTokenId;
    private int clsTokenId;
    private int sepTokenId;
    private int padTokenId;

    public Tokenizer(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("tokenizer.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonString);

            // Extract the vocabulary from the JSON object
            JSONObject vocabJson = jsonObject.getJSONObject("model").getJSONObject("vocab");
            vocab = new HashMap<>();
            Iterator<String> keys = vocabJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                vocab.put(key, vocabJson.getInt(key));
            }

            // Handle the special tokens
            oovTokenId = vocab.getOrDefault("[UNK]", -1);
            clsTokenId = vocab.getOrDefault("[CLS]", -1);
            sepTokenId = vocab.getOrDefault("[SEP]", -1);
            padTokenId = vocab.getOrDefault("[PAD]", -1);
        } catch (Exception e) {
            e.printStackTrace();
            vocab = new HashMap<>(); // Ensure vocab is not null even if an error occurs
        }
    }

    public int[] encode(String text, boolean addSpecialTokens) {
        String[] tokens = text.toLowerCase().split(" "); // Simple space-based tokenization, convert to lowercase
        int[] tokenIds = new int[tokens.length + (addSpecialTokens ? 2 : 0)]; // Add space for special tokens if needed
        int index = 0;

        if (addSpecialTokens) {
            tokenIds[index++] = clsTokenId; // Add CLS token at the beginning
        }

        for (String token : tokens) {
            tokenIds[index++] = vocab.getOrDefault(token, oovTokenId);
        }

        if (addSpecialTokens) {
            tokenIds[index] = sepTokenId; // Add SEP token at the end
        }

        return tokenIds;
    }
}

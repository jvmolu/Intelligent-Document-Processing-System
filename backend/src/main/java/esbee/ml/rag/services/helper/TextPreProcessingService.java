package esbee.ml.rag.services.helper;

import java.util.ArrayList;
import java.util.Arrays;

public class TextPreProcessingService {

    public static ArrayList<String> getChunks(String text, int chunkSize) {

        text = text.toLowerCase();
        text = removePunctuations(text);

        ArrayList<String> chunks = new ArrayList<>();

        ArrayList<String> words = new ArrayList<>(Arrays.asList(text.split(" ")));
        words.removeIf(String::isEmpty);

        for (int i = 0; i < words.size(); i += chunkSize) {
            int end = Math.min(words.size(), i + chunkSize);
            chunks.add(String.join(" ", words.subList(i, end)));
        }

        return chunks;
    }

    public static String removePunctuations(String sent) {

        sent = sent.toLowerCase();
        String[] punctuations = new String[]{",", ".", ";", ":", "?", "!", "\"", "'"};

        for (String punt : punctuations) {
            sent = sent.replace(punt, " ");
        }

        return sent;
    }

}

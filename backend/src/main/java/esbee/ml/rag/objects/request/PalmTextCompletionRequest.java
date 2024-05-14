package esbee.ml.rag.objects.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@ToString
public class PalmTextCompletionRequest {
    private PalmPrompt prompt;
    private ArrayList<SafetySetting> safetySettings;
    private ArrayList<String> stopSequences;
    private float temperature;
    private int candidateCount;
    private int maxOutputTokens;
    private float topP;
    private int topK;

    public PalmTextCompletionRequest(String text) {
        this.prompt = new PalmPrompt(text);
        this.safetySettings = new ArrayList<>();
        this.safetySettings.add(new SafetySetting("HARM_CATEGORY_TOXICITY", "BLOCK_ONLY_HIGH"));
        this.stopSequences = new ArrayList<>();
        this.stopSequences.add("BYE");
        this.temperature = 0.0f;
        this.candidateCount = 1;
        this.maxOutputTokens = 800;
        this.topP = 0.8f;
        this.topK = 10;
    }

    @Data
    @AllArgsConstructor
    public static class PalmPrompt {
        private String text;
    }

    @Data
    @AllArgsConstructor
    public static class SafetySetting {
        private String category;
        private String threshold;
    }
}

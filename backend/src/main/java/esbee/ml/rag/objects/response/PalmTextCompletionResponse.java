package esbee.ml.rag.objects.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PalmTextCompletionResponse {

    private ArrayList<Candidate> candidates;

    public PalmTextCompletionResponse(String responseMessage) {
        this.candidates = new ArrayList<>();
        this.candidates.add(new Candidate(responseMessage, new ArrayList<>()));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Candidate {
        private String output;
        private ArrayList<SafetyRating> safetyRatings;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class SafetyRating {
            private String category;
            private String probability;
        }
    }

}

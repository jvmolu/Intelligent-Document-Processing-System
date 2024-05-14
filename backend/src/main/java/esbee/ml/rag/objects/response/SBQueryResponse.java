package esbee.ml.rag.objects.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@NoArgsConstructor
public class SBQueryResponse {

    private String response;
    private ArrayList<Citation> citations;

    public SBQueryResponse(String response, ArrayList<Citation> citations) {
        this.response = response;
        this.citations = citations;
    }

    public void addCitation(String chunk, String fileName) {
        if (citations == null) {
            citations = new ArrayList<>();
        }
        citations.add(new Citation(chunk, fileName));
    }

    @Data
    @ToString
    private static class Citation {
        private String chunk;
        private String fileName;
        public Citation(String chunk, String fileName) {
            this.chunk = chunk;
            this.fileName = fileName;
        }
    }
}

package esbee.ml.rag.services.helper;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PromptService {
    public String getPromptFromChunksAndQuery(ArrayList<String> chunks, String query) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Given the following text:\n");
        for (String chunk : chunks) {
            prompt.append(chunk).append("\n");
        }
        prompt.append("\n");
        prompt.append("Please provide the answer to the following query and at the end of your answer, please write BYE to indicate the end of your answer:\n");
        prompt.append(query).append("\n");
        return prompt.toString();
    }
}

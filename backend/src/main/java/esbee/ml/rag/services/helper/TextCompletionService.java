package esbee.ml.rag.services.helper;

import esbee.ml.rag.objects.request.PalmTextCompletionRequest;
import esbee.ml.rag.objects.response.PalmTextCompletionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@Service
public class TextCompletionService {

    private static final Logger logger = LoggerFactory.getLogger(TextCompletionService.class);

    @Value("${text.completion.api.key}")
    public String apiKey;

    @Value("${text.completion.api.url}")
    public String apiUrl;

    @Autowired
    public PromptService promptService;

    @Autowired
    public JsonService jsonService;


    private String getResponse(HttpURLConnection connection) throws Exception {

        int status = connection.getResponseCode();
        Reader streamReader = null;

        if (status > 299) {
            streamReader = new InputStreamReader(connection.getErrorStream());
        } else {
            streamReader = new InputStreamReader(connection.getInputStream());
        }

        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        return content.toString();
    }

    public PalmTextCompletionResponse getTextCompletion(ArrayList<String> chunks, String query) throws Exception {

        String prompt = promptService.getPromptFromChunksAndQuery(chunks, query);
        PalmTextCompletionRequest requestBody = new PalmTextCompletionRequest(prompt);

        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl + ":generateText?key=" + apiKey).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        logger.info("Request body: " + jsonService.serialize(requestBody));

        // Write the request body
        connection.getOutputStream().write(jsonService.serialize(requestBody).getBytes());

        int status = connection.getResponseCode();
        String response = getResponse(connection);

        logger.info("Response: " + response);

        try {
            PalmTextCompletionResponse palmTextCompletionResponse = jsonService.deserialize(response, PalmTextCompletionResponse.class);

            if(palmTextCompletionResponse.getCandidates() == null) {
                logger.error("Failed to get response from the API: Candidates are null");
                return new PalmTextCompletionResponse("Failed to find answers for this query from your provided texts." +
                        " Please provide more information about this using file upload");
            }

            if (status == HttpURLConnection.HTTP_OK) {
                return palmTextCompletionResponse;
            } else {
                logger.error("Failed to get response from the API: {}", status);
                logger.info("response: {}", response);
                throw new Exception("Failed to get response from the API");
            }
        } catch (Exception e) {
            logger.error("Failed to deserialize response from the API: Probably Cold Start issue");
            return new PalmTextCompletionResponse("Please wait for the Cold Start Model Initialisation" +
                    "which only happens once a day since I am using the Free Version. This api will start to function under 2 minutes");
        }

    }

    private String getJokes() throws Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL("https://icanhazdadjoke.com/").openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        int status = connection.getResponseCode();
        String response = getResponse(connection);

        if (status == HttpURLConnection.HTTP_OK) {
            return response;
        } else {
            logger.error("Failed to get response from the API: {}", status);
            logger.info("response: {}", response);
            throw new Exception("Failed to get response from the API");
        }
    }
}
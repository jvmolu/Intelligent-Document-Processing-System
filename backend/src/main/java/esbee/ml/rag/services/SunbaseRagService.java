package esbee.ml.rag.services;

import com.google.protobuf.Struct;
import esbee.ml.rag.objects.request.FileUploadRequest;
import esbee.ml.rag.objects.response.PalmTextCompletionResponse;
import esbee.ml.rag.objects.response.SBFileUploadResponse;
import esbee.ml.rag.objects.response.SBQueryResponse;
import esbee.ml.rag.services.helper.GloveService;
import esbee.ml.rag.services.helper.PineconeService;
import esbee.ml.rag.services.helper.TextCompletionService;
import esbee.ml.rag.services.helper.TextPreProcessingService;
import io.pinecone.proto.UpsertResponse;
import io.pinecone.unsigned_indices_model.QueryResponseWithUnsignedIndices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SunbaseRagService {

    private static final Logger logger = LoggerFactory.getLogger(SunbaseRagService.class);

    @Autowired
    private PineconeService pineconeService;

    @Autowired
    private GloveService gloveService;

    @Autowired
    private TextCompletionService textCompletionService;

    public SBFileUploadResponse uploadFile(FileUploadRequest fileUploadRequest, String userId) {

        String text = fileUploadRequest.getFileContent();
        String fileName = fileUploadRequest.getFileName();

        logger.info("Uploading file: " + fileName);

        // Get chunks of text
        ArrayList<String> chunks = TextPreProcessingService.getChunks(text, 100);

        logger.info("Number of chunks: " + chunks.size());

        // Get embeddings for each chunk
        ArrayList<ArrayList<Float>> embeddings = new ArrayList<>();
        chunks.forEach(chunk -> {
            ArrayList<Float> embedding = new ArrayList<>(List.of(gloveService.encodeDocument(chunk)));
            embeddings.add(embedding);
        });

        logger.info("Number of embeddings: " + embeddings.size());

        // Assign each chunk a unique chunkId
        ArrayList<String> chunkIds = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            chunkIds.add(fileName + "_" + i + "_" + userId);
        }

        logger.info("Number of chunkIds: " + chunkIds.size());

        // Upload embeddings to Pinecone
        List<UpsertResponse> uploadResponse = pineconeService.upsertChunksOfFile(chunkIds, embeddings, userId, fileName, chunks);

        int countSuccess = 0;

        for (int i = 0; i < uploadResponse.size(); i++) {
            if(uploadResponse.get(i).getUpsertedCount() == 0) {
                logger.error("Failed to upload chunk: " + chunkIds.get(i));
            } else {
                countSuccess++;
            }
        }

        logger.info("Number of chunks successfully uploaded: " + countSuccess);

        if(countSuccess == chunkIds.size()) {
            return new SBFileUploadResponse("SUCCESS", 200);
        } else if (countSuccess == 0) {
            return new SBFileUploadResponse("FAILED", 500);
        } else {
            return new SBFileUploadResponse("PARTIAL_SUCCESS, Try Re-Uploading File for Better Results", 206);
        }
    }

    public SBQueryResponse query(String query, String userId) throws Exception {

        logger.info("Querying for: " + query);

        // Get embeddings for query
        ArrayList<Float> embedding = new ArrayList<>(List.of(gloveService.encodeDocument(query)));
        logger.info("Embedding for query: " + embedding);

        // Query Pinecone
        QueryResponseWithUnsignedIndices response = pineconeService.queryRecord(embedding, 10, userId);

        // Pick All Chunks with score > 0.7
        ArrayList<String> chunks = new ArrayList<>();
        ArrayList<String> fileNames = new ArrayList<>();

        response.getMatchesList().stream().filter(match -> match.getScore() > 0.7).forEach(match -> {
            Struct metaData = match.getMetadata();
            for (String key : metaData.getFieldsMap().keySet()) {
                if (key.equals("chunk"))
                    chunks.add(metaData.getFieldsMap().get(key).getStringValue());
                if (key.equals("file")) {
                    fileNames.add(metaData.getFieldsMap().get(key).getStringValue());
            }
        }});

        logger.info("Chunks and FileNames: " + chunks + " " + fileNames);

        SBQueryResponse sbQueryResponse = new SBQueryResponse();
        for (int i = 0; i < chunks.size(); i++) {
            sbQueryResponse.addCitation(chunks.get(i), fileNames.get(i));
        }

        PalmTextCompletionResponse textCompletionResponse = textCompletionService.getTextCompletion(chunks, query);
        sbQueryResponse.setResponse(textCompletionResponse.getCandidates().get(0).getOutput());

        return sbQueryResponse;
    }
}

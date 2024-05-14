package esbee.ml.rag.services.helper;

import com.google.protobuf.Struct;
import io.pinecone.clients.Index;
import io.pinecone.clients.Pinecone;
import io.pinecone.proto.UpsertResponse;
import io.pinecone.unsigned_indices_model.QueryResponseWithUnsignedIndices;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PineconeService {

    private static final Logger logger = LoggerFactory.getLogger(PineconeService.class);

    @Value("${vectordb.pinecone.api.key}")
    public String PINECONE_API_KEY;

    @Value("${vectordb.pinecone.api.index}")
    public String PINECONE_API_INDEX;

    private static Index syncIndex;

    @PostConstruct
    void init() {
        logger.info("Initializing Pinecone client with API key: {}", PINECONE_API_KEY);
        syncIndex = new Pinecone.Builder(PINECONE_API_KEY).build().getIndexConnection(PINECONE_API_INDEX);
        try {
            syncIndex.describeIndexStats();
            logger.info("Pinecone index connection successful");
        } catch (Exception e) {
            logger.error("Pinecone index connection failed: {}", e.getMessage());
            throw new RuntimeException("Pinecone index connection failed");
        }
    }

    public UpsertResponse upsertRecord(String c_id, ArrayList<Float> embedding, String userId, String fileName, String chunk) {
        Struct metadata = Struct.newBuilder()
                        .putFields("file", com.google.protobuf.Value.newBuilder().setStringValue(fileName).build())
                        .putFields("chunk", com.google.protobuf.Value.newBuilder().setStringValue(chunk).build())
                        .build();
        return syncIndex.upsert(c_id, embedding, null, null, metadata, userId);
    }

    public List<UpsertResponse> upsertChunksOfFile(List<String> c_ids, List<ArrayList<Float>> embeddings, String userId, String fileName, ArrayList<String> chunks) {
        List<UpsertResponse> upsertResponses = new ArrayList<>();
        for (int i = 0; i < c_ids.size(); i++) {
            UpsertResponse resp = upsertRecord(c_ids.get(i), embeddings.get(i), userId, fileName, chunks.get(i));
            if (resp.getUpsertedCount() == 0) {
                logger.error("Failed to upload chunk: {} of file: {} for user: {}", c_ids.get(i), fileName, userId);
            }
            upsertResponses.add(resp);
        }
        return upsertResponses;
    }

    public QueryResponseWithUnsignedIndices queryRecord(ArrayList<Float> embedding, int topK, String userId) {
        return syncIndex.query(topK, embedding, null, null, null, userId, null, true, true);
    }
}
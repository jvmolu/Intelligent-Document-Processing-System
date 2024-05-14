package esbee.ml.rag.services.helper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GloveService {

    private static final Logger logger = LoggerFactory.getLogger(GloveService.class);
    private final Map<String, float[]> word2em = new HashMap<>();
    private int dimension = -1;

    @Value("${glove.embeddings.directory.path}")
    private String gloveEmbeddingsDirectoryPath;

    @Value("${glove.embeddings.dimension}")
    private int gloveEmbeddingsDimension;

    public GloveService() {
    }

    @PostConstruct
    public void init() {
        this.load(gloveEmbeddingsDirectoryPath, gloveEmbeddingsDimension);
        logger.info("GloVe embeddings loaded successfully");
    }

    private static String getGloVeTextFileName(int dimension) {
        return "glove.6B." + dimension + "d.txt";
    }

    public float[] encodeWord(String word) {
        word = word.toLowerCase();
        return this.word2em.containsKey(word) ? (float[])this.word2em.get(word) : null;
    }

    public Float[] encodeDocument(String sentence) {

        sentence = TextPreProcessingService.removePunctuations(sentence);

        ArrayList<String> words = new ArrayList<>(Arrays.asList(sentence.split(" ")));
        words.removeIf(String::isEmpty);

        Float[] embedding = new Float[this.dimension];
        int cntWords = 0;

        for (String word : words) {
            float[] word2vec = this.encodeWord(word);
            if (word2vec != null) {
                cntWords++;
                for (int i = 0; i < this.dimension; ++i) {
                    if(embedding[i] == null) {
                        embedding[i] = 0.0f;
                    }
                    embedding[i] += word2vec[i];
                }
            }
        }

        if (cntWords > 0) {
            for (int i = 0; i < this.dimension; ++i) {
                embedding[i] /= cntWords;
            }
        }

        return embedding;
    }

    public void load(String dirPath, int dimension) {

        this.dimension = -1;
        this.word2em.clear();

        String sourceFile100 = getGloVeTextFileName(dimension);
        String filePath = dirPath + "/" + sourceFile100;

        File file = new File(filePath);
        if (!file.exists()) {
            logger.info("File {} does not exist", filePath);
            return;
        }

        logger.info("loading {} into word2em", filePath);

        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    String word = parts[0];
                    float[] vec = new float[dimension];
                    for (int i = 1; i <= dimension; ++i) {
                        vec[i - 1] = Float.parseFloat(parts[i]);
                    }
                    this.word2em.put(word, vec);
                }
            }
        } catch (IOException exception) {
            logger.error("Failed to read file " + filePath, exception);
            this.word2em.clear();
            return;
        }

        this.dimension = dimension;
    }
}

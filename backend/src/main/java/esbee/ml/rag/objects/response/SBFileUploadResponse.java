package esbee.ml.rag.objects.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SBFileUploadResponse {

    private String body;
    private int status;

    public SBFileUploadResponse(String body, int status) {
        this.body = body;
        this.status = status;
    }
}

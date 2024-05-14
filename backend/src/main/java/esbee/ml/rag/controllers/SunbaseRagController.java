package esbee.ml.rag.controllers;

import esbee.ml.rag.objects.request.FileUploadRequest;

import esbee.ml.rag.objects.response.SBFileUploadResponse;
import esbee.ml.rag.objects.response.SBQueryResponse;
import esbee.ml.rag.services.SunbaseRagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/sunbaseRag")
public class SunbaseRagController {

    private static final Logger logger = LoggerFactory.getLogger(SunbaseRagController.class);

    @Autowired
    private SunbaseRagService sunbaseRagService;

    @RequestMapping(value="/uploadText", method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(@RequestBody FileUploadRequest fileUploadRequest, @RequestHeader("userId") String userId) {
        try {
            SBFileUploadResponse response = sunbaseRagService.uploadFile(fileUploadRequest, userId);
            return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getStatus()));
        } catch (Exception e) {
            logger.error("Error uploading text", e);
            return ResponseEntity.internalServerError().body("Error uploading text");
        }
    }

    @RequestMapping(value="/query", method = RequestMethod.GET)
    public ResponseEntity<Object> query(@RequestParam("query") String query, @RequestHeader("userId") String userId) {
        try {
            SBQueryResponse response = sunbaseRagService.query(query, userId);
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error querying", e);
            return ResponseEntity.internalServerError().body("Error querying");
        }
    }

}

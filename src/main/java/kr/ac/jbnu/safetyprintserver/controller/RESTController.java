package kr.ac.jbnu.safetyprintserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class RESTController {
    private static final String TAG = "RESTController";

    @PostMapping(value = "/scan/test", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> postScanTestValues() {
        ResponseEntity<?> responseEntity = null;

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

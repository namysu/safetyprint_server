package kr.ac.jbnu.safetyprintserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.jbnu.safetyprintserver.model.GlobalStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

@RestController
@Slf4j
public class RESTController {
    private static final String TAG = "RESTController";

    private GlobalStorage globalStorage;

    public RESTController() {
        this.globalStorage = GlobalStorage.getInstance();
    }

    @GetMapping(value = "/v1/get/user")
    public ResponseEntity<?> getAllUserDataResponseValues(HttpServletRequest request) {
        ResponseEntity<?> responseEntity = null;

        if (globalStorage.getUserDataHashMap().isEmpty()) {
            responseEntity = new ResponseEntity<>("NO DATA SAVE", HttpStatus.NOT_FOUND);

        } else {
            responseEntity = new ResponseEntity<>(globalStorage.getUserDataHashMap(), HttpStatus.OK);
        }

        makeLogMessage(request, responseEntity, null);
        return responseEntity;
    }

    @GetMapping(value = "/v1/get/document")
    public ResponseEntity<?> getAllDocumentResponseValues(HttpServletRequest request) {
        ResponseEntity<?> responseEntity = null;

        if (globalStorage.getDocumentDataHashMap().isEmpty()) {
            responseEntity = new ResponseEntity<>("NO DATA SAVE", HttpStatus.NOT_FOUND);

        } else {
            responseEntity = new ResponseEntity<>(globalStorage.getDocumentDataHashMap(), HttpStatus.OK);
        }

        makeLogMessage(request, responseEntity, null);
        return responseEntity;
    }

    @GetMapping(value = "/v1/get/user/{id}")
    public ResponseEntity<?> getUserInfoResponseValues(HttpServletRequest request, @PathVariable String id) {
        ResponseEntity<?> responseEntity = null;

        if (globalStorage.getUserDataHashMap().isEmpty()) {
            responseEntity = new ResponseEntity<>("NO DATA SAVE", HttpStatus.NOT_FOUND);

        } else {
            if (globalStorage.getUserDataHashMap().get(id).isEmpty()) {
                responseEntity = new ResponseEntity<>("NOT FIND USER", HttpStatus.NOT_FOUND);

            } else {
                responseEntity = new ResponseEntity<>(globalStorage.getUserDataHashMap().get(id), HttpStatus.OK);
            }
        }

        makeLogMessage(request, responseEntity, null);
        return responseEntity;
    }

    @GetMapping(value = "/v1/get/document/{code}")
    public ResponseEntity<?> getDocumentInfoResponseValues(HttpServletRequest request, @PathVariable String code) {
        ResponseEntity<?> responseEntity = null;

        if (globalStorage.getDocumentDataHashMap().isEmpty()) {
            responseEntity = new ResponseEntity<>("NO DATA SAVE", HttpStatus.NOT_FOUND);

        } else {
            if (globalStorage.getDocumentDataHashMap().get(code).isEmpty()) {
                responseEntity = new ResponseEntity<>("NOT FIND DOCUMENT", HttpStatus.NOT_FOUND);

            } else {
                responseEntity = new ResponseEntity<>(globalStorage.getDocumentDataHashMap().get(code), HttpStatus.OK);
            }
        }

        makeLogMessage(request, responseEntity, null);
        return responseEntity;
    }

    @PostMapping(value = "/v1/post/user/login")
    public ResponseEntity<?> postUserLogin(HttpServletRequest request, @RequestBody HashMap<String, Object> requestMap) {
        ResponseEntity<?> responseEntity = null;

        if (globalStorage.getUserDataHashMap().isEmpty()) {
            responseEntity = new ResponseEntity<>("NO DATA SAVE", HttpStatus.NOT_FOUND);

        } else {
            if (globalStorage.getUserDataHashMap().get((String) requestMap.get("id")) == null) {
                responseEntity = new ResponseEntity<>("ID FAIL", HttpStatus.NOT_FOUND);

            } else {
                if (String.valueOf(globalStorage.getUserDataHashMap()
                        .get((String) requestMap.get("id")).get("password"))
                        .equals(String.valueOf(requestMap.get("password")))) {
                    responseEntity = new ResponseEntity<>("LOGIN OK", HttpStatus.OK);

                } else {
                    responseEntity = new ResponseEntity<>("PASSWORD FAIL", HttpStatus.BAD_REQUEST);
                }
            }
        }

        makeLogMessage(request, responseEntity, null);
        return responseEntity;
    }

    @PostMapping(value = "/v1/post/user/register")
    public ResponseEntity<?> postUserRegisterResponseValues(HttpServletRequest request, @RequestBody HashMap<String, Object> requestMap) {
        ResponseEntity<?> responseEntity = null;

        if (globalStorage.getUserDataHashMap().get((String) requestMap.get("id")) == null) {
            if ((requestMap.get("id") != null && !requestMap.get("id").equals(""))
                    && (requestMap.get("password") != null && !requestMap.get("password").equals(""))) {

                globalStorage.getUserDataHashMap().put((String) requestMap.get("id"), requestMap);
                responseEntity = new ResponseEntity<>("REGISTER OK", HttpStatus.OK);

            } else {
                responseEntity = new ResponseEntity<>("ID/PASSWORD FAIL", HttpStatus.BAD_REQUEST);
            }

        } else {
            responseEntity = new ResponseEntity<>("ID EXISTS", HttpStatus.FORBIDDEN);
        }

        makeLogMessage(request, responseEntity, requestMap);
        return responseEntity;
    }

    @PostMapping(value = "/v1/post/document/create")
    public ResponseEntity<?> postDocumentCreateResponseValues(HttpServletRequest request, @RequestBody HashMap<String, Object> requestMap) {
        ResponseEntity<?> responseEntity = null;
        return responseEntity;
    }

    @PostMapping(value = "/v1/post/document/addcontactuser")
    public ResponseEntity<?> postDocumentAddContactUserResponseValues(HttpServletRequest request, @RequestBody HashMap<String, Object> requestMap) {
        ResponseEntity<?> responseEntity = null;
        return responseEntity;
    }

    @PostMapping(value = "/v1/post/file/{code}/upload")
    public ResponseEntity<?> postFileUploadResponseValues(HttpServletRequest request, @PathVariable String code, @RequestBody HashMap<String, Object> requestMap) {
        ResponseEntity<?> responseEntity = null;
        return responseEntity;
    }

    @PostMapping(value = "/v1/post/document/{code}/scanner/add")
    public ResponseEntity<?> postDocumentScannerAddResponseValues(HttpServletRequest request, @PathVariable String code, @RequestBody HashMap<String, Object> requestMap) {
        ResponseEntity<?> responseEntity = null;
        return responseEntity;
    }

    @PostMapping(value = "/v1/post/scanner/{code}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> postScannerDataResponseValues(HttpServletRequest request, @PathVariable String code) {
        ResponseEntity<?> responseEntity = null;
        return responseEntity;
    }

    @PostMapping(value = "/scan/test", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> postHandleScanData(HttpServletRequest request) {
/*        String boundary = extractBoundary(request);

        try {
            MultipartStream multipartStream = new MultipartStream(request.getInputStream(), boundary.getBytes(), 104857600, null);
            boolean nextPart = multipartStream.skipPreamble();
            while (nextPart) {
                String header = multipartStream.readHeaders();
                System.out.println(header);

                OutputStream outputStream = new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        System.out.print(Integer.toHexString(b) + " ");
                    }
                };

                multipartStream.readBodyData(outputStream);
                outputStream.flush();
                outputStream.close();

                nextPart = multipartStream.readBoundary();
            }


        } catch (ClientAbortException e) {
            e.printStackTrace();

            return new ResponseEntity<>("", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();

            return new ResponseEntity<>("", HttpStatus.OK);
        }*/

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    private String extractBoundary(HttpServletRequest request) {
        String boundaryHeader = "boundary=";
        int i = request.getContentType().indexOf(boundaryHeader) + boundaryHeader.length();

        return request.getContentType().substring(i);
    }

    private void makeLogMessage(HttpServletRequest request, ResponseEntity<?> responseEntity, HashMap<String, Object> requestMap) {
        if (request != null && responseEntity != null) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("-- Request Method : ")
                    .append(request.getMethod())
                    .append(" -- Request Addr : ")
                    .append(request.getRemoteAddr())
                    .append(" -- Request URI : ")
                    .append(request.getRequestURI())
                    .append(" -- Response Entity : ")
                    .append(responseEntity.getStatusCode());

            if (requestMap != null) {
                logBuilder.append(" -- Response Body : ")
                        .append(requestMap.toString());
            }

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                log.info(logBuilder.toString());
            } else {
                log.warn(logBuilder.toString());
            }
        }
    }

}

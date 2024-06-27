package kr.ac.jbnu.safetyprintserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.jbnu.safetyprintserver.model.GlobalStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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

        makeLogMessage(request, responseEntity, requestMap);
        return responseEntity;
    }

    @PostMapping(value = "/v1/post/user/register")
    public ResponseEntity<?> postUserRegisterResponseValues(HttpServletRequest request, @RequestBody HashMap<String, Object> requestMap) {
        ResponseEntity<?> responseEntity = null;

        if (globalStorage.getUserDataHashMap().get((String) requestMap.get("id")) == null) {
            if ((requestMap.get("id") != null && !requestMap.get("id").equals(""))
                    && (requestMap.get("password") != null && !requestMap.get("password").equals(""))) {

                HashMap<String, Object> userDataHashMap = new HashMap<String, Object>() {{
                    put("id", requestMap.get("id"));
                    put("password", requestMap.get("password"));
                    put("name", requestMap.get("name"));
                    put("division", requestMap.get("division"));
                    put("documentlist", new ArrayList<String>());
                }};

                globalStorage.getUserDataHashMap().put((String) requestMap.get("id"), userDataHashMap);
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

        if ((requestMap.get("openuser") != null && !requestMap.get("openuser").equals(""))
                && (requestMap.get("opendate") != null && !requestMap.get("opendate").equals(""))
                && (requestMap.get("closedate") != null && !requestMap.get("closedate").equals(""))
                && (requestMap.get("description") != null && !requestMap.get("description").equals(""))) {

            String code = getRandomCode(6);

            HashMap<String, Object> documentDataHashMap = new HashMap<String, Object>() {{
                put("code", code);
                put("openuser", requestMap.get("openuser"));
                put("contactuser", "");
                put("opendate", requestMap.get("opendate"));
                put("closedate", requestMap.get("closedate"));
                put("description", requestMap.get("description"));
                put("scanneremail", "");
                put("scanname", "");
                put("data", new ArrayList<String>());
            }};

            globalStorage.getDocumentDataHashMap().put(code, documentDataHashMap);

            if (globalStorage.getUserDataHashMap().get((String) requestMap.get("openuser")).get("documentlist") != null) {
                ArrayList<String> documentList = (ArrayList<String>) globalStorage.getUserDataHashMap().get((String) requestMap.get("openuser")).get("documentlist");

                documentList.add(code);
                globalStorage.getUserDataHashMap().get((String) requestMap.get("openuser")).put("documentlist", documentList);

            } else {
                globalStorage.getUserDataHashMap().get((String) requestMap.get("openuser")).put("documentlist", new ArrayList<String>() {{
                    add(code);
                }});
            }

            responseEntity = new ResponseEntity<>(code, HttpStatus.OK);

        } else {
            responseEntity = new ResponseEntity<>("MISS DOCUMENT DATA", HttpStatus.BAD_REQUEST);
        }

        makeLogMessage(request, responseEntity, requestMap);

        return responseEntity;
    }

    @PostMapping(value = "/v1/post/document/addcontactuser")
    public ResponseEntity<?> postDocumentAddContactUserResponseValues(HttpServletRequest request, @RequestBody HashMap<String, Object> requestMap) {
        ResponseEntity<?> responseEntity = null;

        if (globalStorage.getDocumentDataHashMap().isEmpty()) {
            responseEntity = new ResponseEntity<>("NO DATA SAVE", HttpStatus.NOT_FOUND);

        } else {
            if ((requestMap.get("id") != null && !requestMap.get("id").equals(""))
                    && (requestMap.get("code") != null && !requestMap.get("code").equals(""))) {

                if (globalStorage.getDocumentDataHashMap().get((String) requestMap.get("code")) != null) {
                    globalStorage.getDocumentDataHashMap().get((String) requestMap.get("code")).put("contactuser", requestMap.get("id"));

                    if (globalStorage.getUserDataHashMap().get((String) requestMap.get("id")).get("documentlist") != null) {
                        ArrayList<String> documentList = (ArrayList<String>) globalStorage.getUserDataHashMap().get((String) requestMap.get("id")).get("documentlist");

                        documentList.add((String) requestMap.get("code"));
                        globalStorage.getUserDataHashMap().get((String) requestMap.get("id")).put("documentlist", documentList);

                    } else {
                        globalStorage.getUserDataHashMap().get((String) requestMap.get("id")).put("documentlist", new ArrayList<String>() {{
                            add((String) requestMap.get("code"));
                        }});
                    }

                    responseEntity = new ResponseEntity<>("ADD CONTACT USER", HttpStatus.OK);

                } else {
                    responseEntity = new ResponseEntity<>("MISSMATCH DOCUMENT DATA", HttpStatus.NOT_FOUND);
                }

            } else {
                responseEntity = new ResponseEntity<>("MISS CONTACT USER DATA", HttpStatus.BAD_REQUEST);
            }
        }

        makeLogMessage(request, responseEntity, requestMap);

        return responseEntity;
    }

    @PostMapping(value = "/v1/post/file/{code}/upload")
    public ResponseEntity<?> postFileUploadResponseValues(HttpServletRequest request, @PathVariable String code, @RequestBody HashMap<String, Object> requestMap) {
        ResponseEntity<?> responseEntity = null;

        if (globalStorage.getDocumentDataHashMap().isEmpty()) {
            responseEntity = new ResponseEntity<>("NO DATA SAVE", HttpStatus.NOT_FOUND);

        } else {
            if ((requestMap.get("filename") != null && !requestMap.get("filename").equals(""))
                    && (requestMap.get("filedata") != null && !requestMap.get("filedata").equals(""))) {

                if (globalStorage.getDocumentDataHashMap().get(code).get("data") != null) {
                    ArrayList<String> dataList = (ArrayList<String>) globalStorage.getUserDataHashMap().get(code).get("data");

                    dataList.add((String) requestMap.get("filename"));
                    globalStorage.getDocumentDataHashMap().get(code).put("data", dataList);

                } else {
                    globalStorage.getDocumentDataHashMap().get(code).put("data", new ArrayList<String>() {{
                        add((String) requestMap.get("filename"));
                    }});
                }

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream((String) requestMap.get("filedata"));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                responseEntity = new ResponseEntity<>("ADD DOCUMENT DATA", HttpStatus.OK);

            } else {
                responseEntity = new ResponseEntity<>("MISS CONTACT USER DATA", HttpStatus.BAD_REQUEST);
            }
        }

        makeLogMessage(request, responseEntity, requestMap);

        return responseEntity;
    }

    @PostMapping(value = "/v1/post/document/{code}/scanner/add")
    public ResponseEntity<?> postDocumentScannerAddResponseValues(HttpServletRequest request, @PathVariable String code, @RequestBody HashMap<String, Object> requestMap) {
        ResponseEntity<?> responseEntity = null;

        if (globalStorage.getDocumentDataHashMap().isEmpty()) {
            responseEntity = new ResponseEntity<>("NO DATA SAVE", HttpStatus.NOT_FOUND);

        } else {
            if (globalStorage.getDocumentDataHashMap().get(code).isEmpty()) {
                responseEntity = new ResponseEntity<>("NOT FIND DOCUMENT", HttpStatus.NOT_FOUND);

            } else {
                String token = getEPSONConnectToken((String) requestMap.get("email"));

                if (token != null && !token.equals("null/null")) {
                    String[] tokenSplit = token.split("/");
                    String uidToken = tokenSplit[0];
                    String deviceID = tokenSplit[1];

                    if (setEPSONConnectScanDevice(uidToken, deviceID, (String) requestMap.get("name"), code)) {
                        String scannerDescShowName = requestMap.get("name") + "_" + code;

                        globalStorage.getDocumentDataHashMap().get(code).put("scanneremail", requestMap.get("email"));
                        globalStorage.getDocumentDataHashMap().get(code).put("scanname", scannerDescShowName);

                        responseEntity = new ResponseEntity<>(scannerDescShowName, HttpStatus.OK);

                    } else {
                        responseEntity = new ResponseEntity<>("EPSON ERROR", HttpStatus.BAD_REQUEST);
                    }

                } else {
                    responseEntity = new ResponseEntity<>("EPSON ERROR", HttpStatus.BAD_REQUEST);
                }
            }
        }

        makeLogMessage(request, responseEntity, requestMap);
        return responseEntity;
    }

    @PostMapping(value = "/v1/post/scanner/{code}")
    public ResponseEntity<?> postScannerDataResponseValues(HttpServletRequest request, @PathVariable String code) {
        ResponseEntity<?> responseEntity = null;

        if (globalStorage.getDocumentDataHashMap().isEmpty()) {
            responseEntity = new ResponseEntity<>("NO DATA SAVE", HttpStatus.NOT_FOUND);

        } else {
            if (globalStorage.getDocumentDataHashMap().get(code).isEmpty()) {
                responseEntity = new ResponseEntity<>("NOT FIND DOCUMENT", HttpStatus.NOT_FOUND);

            } else {
                String boundary = extractBoundary(request);

                try {
                    MultipartStream multipartStream = new MultipartStream(request.getInputStream(), boundary.getBytes(), 1024, null);
                    boolean nextPart = multipartStream.skipPreamble();
                    int i = 0;
                    while (nextPart) {
                        String header = multipartStream.readHeaders();
                        System.out.println(header);

                        String path = "C:/Users/namyounsu/Desktop/test_" + i + ".jpg";

                        if (globalStorage.getDocumentDataHashMap().get(code).get("data") != null) {
                            ArrayList<String> dataList = (ArrayList<String>) globalStorage.getDocumentDataHashMap().get(code).get("data");

                            dataList.add(path);
                            globalStorage.getDocumentDataHashMap().get(code).put("data", dataList);

                        } else {
                            globalStorage.getDocumentDataHashMap().get(code).put("data", new ArrayList<String>() {{
                                add(path);
                            }});
                        }

                        OutputStream outputStream = new FileOutputStream(new File(path));

                        multipartStream.readBodyData(outputStream);
                        nextPart = multipartStream.readBoundary();
                        i++;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                responseEntity = new ResponseEntity<>("OK", HttpStatus.OK);
            }
        }

        return responseEntity;
    }

    public String getEPSONConnectToken(String printerEmail) {
        WebClient webClient = WebClient.builder().build();
        String url = "https://api.epsonconnect.com/api/1/printing/oauth2/auth/token?subject=printer&grant_type=password&username=" + printerEmail + "&password=";

        String response = webClient.post()
                .uri(url)
                .header("Authorization", "")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (response != null && !response.isEmpty()) {
            try {
                JSONObject tokenObject = new JSONObject(response);

                System.out.println(tokenObject.get("access_token") + "/" + tokenObject.get("subject_id"));
                return tokenObject.get("access_token") + "/" + tokenObject.get("subject_id");

            } catch (JSONException e) {
                return "null/null";
            }

        } else {
            return "null/null";
        }
    }

    public boolean setEPSONConnectScanDevice(String token, String printerID, String userName, String code) {
        WebClient webClient = WebClient.builder().build();
        String url = "https://api.epsonconnect.com/api/1/scanning/scanners/" + printerID + "/destinations";

        String res = webClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(BodyInserters.fromValue("{\n" +
                        "    \"alias_name\": \"" +  userName + "_" + code + "\",\n" +
                        "    \"type\": \"url\",\n" +
                        "    \"destination\": \"/" + code + "\"\n" +
                        "}"))
                .exchange()
                .map(response -> response.statusCode())
                .block()
                .toString();

        System.out.println(res);

        if (res != null && !res.isEmpty()) {
            return res.equals("201 CREATED");

        } else {
            return false;
        }
    }

    private String extractBoundary(HttpServletRequest request) {
        String boundaryHeader = "boundary=";
        int i = request.getContentType().indexOf(boundaryHeader) + boundaryHeader.length();

        return request.getContentType().substring(i);
    }

    private String getRandomCode(int numChars){
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();

        while(stringBuffer.length() < numChars){
            stringBuffer.append(String.format("%08x", random.nextInt()));
        }

        return stringBuffer.toString().substring(0, numChars);
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

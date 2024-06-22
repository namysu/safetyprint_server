package kr.ac.jbnu.safetyprintserver.model;

import java.util.HashMap;

public class GlobalStorage {
    private static final String TAG = "GlobalStorage";

    private static GlobalStorage globalStorage = null;

    private HashMap<String, HashMap<String, Object>> userDataHashMap;
    private HashMap<String, HashMap<String, Object>> documentDataHashMap;


    private GlobalStorage() {
        this.userDataHashMap = new HashMap<String, HashMap<String, Object>>();
        this.documentDataHashMap = new HashMap<String, HashMap<String, Object>>();
    }

    public static GlobalStorage getInstance() {
        if (globalStorage == null) {
            globalStorage = new GlobalStorage();
        }

        return globalStorage;
    }


    public HashMap<String, HashMap<String, Object>> getUserDataHashMap() {
        return userDataHashMap;
    }

    public void setUserDataHashMap(HashMap<String, HashMap<String, Object>> userDataHashMap) {
        this.userDataHashMap = userDataHashMap;
    }

    public HashMap<String, HashMap<String, Object>> getDocumentDataHashMap() {
        return documentDataHashMap;
    }

    public void setDocumentDataHashMap(HashMap<String, HashMap<String, Object>> documentDataHashMap) {
        this.documentDataHashMap = documentDataHashMap;
    }
}

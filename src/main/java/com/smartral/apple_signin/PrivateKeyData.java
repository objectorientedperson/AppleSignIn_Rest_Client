package com.smartral.apple_signin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by @author ali on 12/2/19
 */
class PrivateKeyData {

    private String keyFileOrPrivateKey;
    private Map<String, Object> payload = new HashMap<>();
    private Map<String, Object> header = new HashMap<>();

    public String getKeyFileOrPrivateKey() {
        return keyFileOrPrivateKey;
    }

    public void setKeyFileOrPrivateKey(String keyFileOrPrivateKey) {
        this.keyFileOrPrivateKey = keyFileOrPrivateKey;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public Map<String, Object> getHeader() {
        return header;
    }
}

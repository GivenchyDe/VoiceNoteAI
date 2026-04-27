package org.example.vo_ai_service.xfyun.sign;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SignatureException;

public abstract class AbstractSignature {
    private String id;
    private String key;
    private String url;
    private String encryptType;
    private String originSign;
    protected String signa;
    private String ts;
    protected String requestMethod = "GET";

    public AbstractSignature(String id, String key, String url) {
        this.id = id;
        this.key = key;
        this.url = url;
        this.ts = generateTs();
    }

    public AbstractSignature(String id, String key, String url, boolean isPost) {
        this.id = id;
        this.key = key;
        this.url = url;
        if (isPost) {
            this.requestMethod = "POST";
        } else {
            this.requestMethod = "GET";
        }
        this.ts = generateTs();
    }

    public String generateTs() {
        return String.valueOf(System.currentTimeMillis() / 1000L);
    }

    public abstract String getSigna() throws SignatureException;

    public String generateOriginSign() throws SignatureException {
        try {
            URL url = new URL(this.getUrl());
            return "host: " + url.getHost() + "\n" +
                    "date: " + this.getTs() + "\n" +
                    "GET " + url.getPath() + " HTTP/1.1";
        } catch (MalformedURLException e) {
            throw new SignatureException("MalformedURLException:" + e.getMessage());
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getOriginSign() { return originSign; }
    public void setOriginSign(String originSign) { this.originSign = originSign; }
    public String getTs() { return ts; }
    public void setTs(String ts) { this.ts = ts; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getEncryptType() { return encryptType; }
    public void setEncryptType(String encryptType) { this.encryptType = encryptType; }
}

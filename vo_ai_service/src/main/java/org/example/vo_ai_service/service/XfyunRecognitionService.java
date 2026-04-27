package org.example.vo_ai_service.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.vo_ai_service.xfyun.sign.LfasrSignature;
import org.example.vo_ai_service.xfyun.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.security.SignatureException;
import java.util.HashMap;

@Service
public class XfyunRecognitionService {

    private static final String HOST = "https://raasr.xfyun.cn";
    private static final Gson gson = new Gson();

    @Value("${xfyun.appid:}")
    private String appid;

    @Value("${xfyun.secretKey:}")
    private String secretKey;

    /**
     * 上传音频并获取识别结果
     */
    public String recognize(File audioFile) throws Exception {
        if (appid == null || appid.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            throw new RuntimeException("讯飞配置未填写");
        }

        // 1. 上传音频
        String uploadResp = upload(audioFile);
        System.out.println("[Xfyun upload] response=" + uploadResp);
        if (uploadResp == null || uploadResp.isEmpty()) {
            throw new RuntimeException("上传失败：网络错误");
        }
        JsonObject respJson = JsonParser.parseString(uploadResp).getAsJsonObject();
        String code = respJson.get("code").getAsString();
        if (!"000000".equals(code)) {
            throw new RuntimeException("上传失败：" + uploadResp);
        }
        String orderId = respJson.getAsJsonObject("content").get("orderId").getAsString();

        // 2. 轮询获取结果
        return getResult(orderId);
    }

    private String upload(File audioFile) throws SignatureException, Exception {
        HashMap<String, Object> map = new HashMap<>(16);
        String fileName = audioFile.getName();
        long fileSize = audioFile.length();
        map.put("appId", appid);
        map.put("fileSize", fileSize);
        map.put("fileName", fileName);
        map.put("duration", "200");
        LfasrSignature lfasrSignature = new LfasrSignature(appid, secretKey);
        map.put("signa", lfasrSignature.getSigna());
        map.put("ts", lfasrSignature.getTs());

        String paramString = HttpUtil.parseMapToPathParam(map);
        String url = HOST + "/v2/api/upload" + "?" + paramString;

        try (FileInputStream fis = new FileInputStream(audioFile)) {
            return HttpUtil.iflyrecUpload(url, fis);
        }
    }

    private String getResult(String orderId) throws SignatureException, InterruptedException {
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("orderId", orderId);
        LfasrSignature lfasrSignature = new LfasrSignature(appid, secretKey);
        map.put("signa", lfasrSignature.getSigna());
        map.put("ts", lfasrSignature.getTs());
        map.put("appId", appid);
        map.put("resultType", "transfer");
        String paramString = HttpUtil.parseMapToPathParam(map);
        String url = HOST + "/v2/api/getResult" + "?" + paramString;

        int maxRetry = 30; // 最多轮询30次
        for (int i = 0; i < maxRetry; i++) {
            String response = HttpUtil.iflyrecGet(url);
            System.out.println("[Xfyun getResult] response=" + response);
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            int code = json.get("code").getAsInt();
            if (code != 0) {
                return "识别失败：" + response;
            }
            JsonObject content = json.getAsJsonObject("content");
            JsonObject orderInfo = content.getAsJsonObject("orderInfo");
            int status = orderInfo.get("status").getAsInt();
            if (status == 4) {
                // 完成，解析识别结果（orderResult 是 JSON 字符串，需二次解析）
                String orderResultStr = content.get("orderResult").getAsString();
                JsonObject orderResult = JsonParser.parseString(orderResultStr).getAsJsonObject();
                return parseResult(orderResult);
            } else if (status == -1) {
                int failType = orderInfo.get("failType").getAsInt();
                // 如果没有predict权限导致的失败，尝试解析已有的orderResult
                if (failType == 11 && content.has("orderResult")) {
                    String orderResultStr = content.get("orderResult").getAsString();
                    if (orderResultStr != null && !orderResultStr.isEmpty() && !"\"\"".equals(orderResultStr)) {
                        JsonObject orderResult = JsonParser.parseString(orderResultStr).getAsJsonObject();
                        return parseResult(orderResult);
                    }
                }
                return "识别失败(failType=" + failType + ")";
            }
            Thread.sleep(3000);
        }
        return "识别超时";
    }

    private String parseResult(JsonObject orderResult) {
        if (orderResult == null) return "";
        // 讯飞返回的识别结果结构：lattice -> json_1best -> st -> rt -> ws -> cw -> w
        try {
            StringBuilder sb = new StringBuilder();
            var latticeArray = orderResult.getAsJsonArray("lattice");
            if (latticeArray == null) return "";
            for (var latticeElem : latticeArray) {
                JsonObject lattice = latticeElem.getAsJsonObject();
                String json1bestStr = lattice.get("json_1best").getAsString();
                JsonObject json1best = JsonParser.parseString(json1bestStr).getAsJsonObject();
                var rtArray = json1best.getAsJsonObject("st").getAsJsonArray("rt");
                for (var rtElem : rtArray) {
                    var wsArray = rtElem.getAsJsonObject().getAsJsonArray("ws");
                    for (var wsElem : wsArray) {
                        var cwArray = wsElem.getAsJsonObject().getAsJsonArray("cw");
                        for (var cwElem : cwArray) {
                            String w = cwElem.getAsJsonObject().get("w").getAsString();
                            sb.append(w);
                        }
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return orderResult.toString();
        }
    }
}

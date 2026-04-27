package org.example.vo_ai_service.xfyun.sign;

import cn.hutool.core.util.ObjectUtil;
import org.example.vo_ai_service.xfyun.utils.CryptTools;
import java.security.SignatureException;

public class LfasrSignature extends AbstractSignature {

    public LfasrSignature(String appId, String keySecret) {
        super(appId, keySecret, null);
    }

    @Override
    public String getSigna() throws SignatureException {
        if (ObjectUtil.isEmpty(this.signa)) {
            this.setOriginSign(generateOriginSign());
            this.signa = generateSignature();
        }
        return this.signa;
    }

    public String generateSignature() throws SignatureException {
        return CryptTools.hmacEncrypt(CryptTools.HMAC_SHA1, this.getOriginSign(), this.getKey());
    }

    @Override
    public String generateOriginSign() throws SignatureException {
        return CryptTools.md5Encrypt(this.getId() + this.getTs());
    }
}

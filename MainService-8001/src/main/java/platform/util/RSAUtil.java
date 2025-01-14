package platform.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

@Slf4j
public class RSAUtil {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;


    /**
     * 管理平台-RSA私钥
     */
    private static final String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIocpIFnxXgcPg0C5Mx9KGhmAExvPS/" +
            "CMfirkjm4aAS6S/V65+Fe/gglml83moSS6/biVxGpa/DfwAxiBYXjIY1zatVbbrnn2RzEOecsTOy1bvPE+QWbptG2Z3P+DIp8YUtxbJN31XQ/LrN3c" +
            "TfjcyHlQm5AlUDTYuVd3SrBEJHhAgMBAAECgYAgw3i3BiIPQ0vpFEWoyQwb8A6kE0OFn+Dw5+qxbLCoZnV8j6KQkVNcJWeArRUn3B7EG5+iHfnwU3Qgu" +
            "+Qphd9AbtCg268Gmbee1TW7b8Sy6SJlB1CwB4wZBy0WhRgl6wtzqh6Nmw1YyumykjG8Es5OmofnZJ2JeebgNw5+lCqfHQJBAOlywH7YXxQywvm9Gsq/UIo" +
            "pjTE8dGYfnSlmHiPp12mv3vFnhFZwTJ37E8faZK3r3K+OORTH1vwffMVwdkK8I6sCQQCXdDFPZD5T955sxOBx84ZN/kfOeegIEBiFVH6iyBouYhP36BVHYikX" +
            "Rc+atHnro2eC9Hn9w5kbrLEIBVYU9ZSjAkEA12YwQ1n18LcdvD1GWUjJUZIhWwrDA++rnaVBrjV3s2a5ONkg/HjF2QbwK3lRaEC28a0y8f+qWBvdjnfERrM93" +
            "QJAR68UI7qTkZSS5HJutSCJQeMHw5+Jhj9wC7NJWOyTD78WKnErmSTJxB0jvNqNFk26EY57KvPoROQAyoYUD0mJNwJAU/x/Q2hUYsrzd/1U34yUpMM1iPhWhC" +
            "8v6Lksb+dX7J3eOlOZ4jj0qrNfboVWdTqIpfWi/Xke9WYx/7ZiIeYTQA==";
    /**
     * 管理平台-RSA公钥
     */
    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKHKSBZ8V4HD4NAuTMfShoZgBMbz0vwjH4q5I5uGgEukv1eufhXv4IJ" +
            "ZpfN5qEkuv24lcRqWvw38AMYgWF4yGNc2rVW26559kcxDnnLEzstW7zxPkFm6bRtmdz/gyKfGFLcWyTd9V0Py6zd3E343Mh5UJuQJVA02LlXd0qwRCR4QIDAQAB";


    /**
     * RSA加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     * @return
     */
    public static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        // 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
        // 加密后的字符串
        return Base64.encodeBase64String(encryptedData);
    }

    /**
     * RSA解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     * @return
     */
    public static String decrypt(String data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataBytes = Base64.decodeBase64(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        out.close();
        // 解密后的内容
        return out.toString(StandardCharsets.UTF_8);
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     * @return 签名
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * 验签
     *
     * @param srcData   原始字符串
     * @param publicKey 公钥
     * @param sign      签名
     * @return 是否验签通过
     */
    public static boolean verify(String srcData, PublicKey publicKey, String sign) throws Exception {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(key);
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }


    /**
     * 获取私钥
     *
     * @param
     * @return
     */
    public static PrivateKey getPrivateKey() throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取公钥
     *
     * @param
     * @return
     */
    public static PublicKey getPublicKey() throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }


    public static void main(String[] args) {
        try {
            // 生成密钥对
            System.out.println("私钥:" + privateKey);
            System.out.println("公钥:" + publicKey);
            // RSA加密
            String data = "15000945702";
            String encryptData = encrypt("123456@56zzx", getPublicKey());
            System.out.println("加密后内容:" + encryptData);
            // RSA解密
            String decryptData = decrypt(encryptData, getPrivateKey());
            System.out.println("解密后内容:" + decryptData);

            // RSA签名
            String sign = sign(data, getPrivateKey());
            // RSA验签
            boolean result = verify(data, getPublicKey(), sign);
            System.out.print("验签结果:" + result);
        } catch (Exception e) {
            log.error(e.getMessage());
            System.out.print("加解密异常");
        }
    }
}

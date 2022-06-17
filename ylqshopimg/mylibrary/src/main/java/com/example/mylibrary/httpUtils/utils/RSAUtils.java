package com.example.mylibrary.httpUtils.utils;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
/**
 * description ： TODO:类的作用
 * author : 黄先生
 * date : 2019/4/24 15:34
 */
public class RSAUtils {

    /*CRLF 这个参数看起来比较眼熟，它就是Win风格的换行符，意思就是使用CR LF这一对作为一行的结尾而不是Unix风格的LF
    DEFAULT 这个参数是默认，使用默认的方法来加密
    NO_PADDING 这个参数是略去加密字符串最后的”=”
    NO_WRAP 这个参数意思是略去所有的换行符（设置后CRLF就没用了）
    URL_SAFE 这个参数意思是加密时不使用对URL和文件名有特殊意义的字符来作为加密字符，具体就是以-和_取代+和/*/
    static private int flag = Base64.DEFAULT;


    //RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;
    //RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;
    public static final String KEY_ALGORITHM = "RSA";
    private static KeyFactory keyFactory = null;
    public static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";//加密填充方式
    static private String PrivateKey="";
    static private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDbsKf2pm/A3KqzQbk14/NlQ3rI\n" +
            "1hXtRiPF7HwdgA8Xgecd8syvt1JxIrgkg+Jze9I1wXe4yAwvjMG8nPb3QeCgwGob\n" +
            "dXSEbusG6VMnHcirgtMMKzKbeTApEZXcAyFujr9UTLZu6n8EegaB2QNrKqKoRpNf\n" +
            "3SJROnwI5SvGHdi9kQIDAQAB";
    static {
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 解密方法
     * @param dataStr 要解密的数据
     * @return 解密后的原数据
     * @throws Exception
     */
    public static String decrypt(String dataStr) throws Exception{
        //要加密的数据
        System.out.println("要解密的数据:"+dataStr);
        //对私钥解密
        Key decodePrivateKey = getPrivateKeyFromBase64KeyEncodeStr(PrivateKey);
        //Log.i("机密",""+decodePrivateKey);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, decodePrivateKey);
        byte[] encodedData = Base64.decode(dataStr,flag);
        byte[] decodedData = cipher.doFinal(encodedData);
        String decodedDataStr = new String(decodedData,"utf-8");
        System.out.println("私钥解密后的数据:"+decodedDataStr);
        return decodedDataStr;
    }

    public  static Key getPrivateKeyFromBase64KeyEncodeStr(String keyStr) {
        byte[] keyBytes = Base64.decode(keyStr,flag);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        Key privateKey=null;
        try {
            privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * 获取base64加密后的字符串的原始公钥
     * @param keyStr
     * @return
     */
    public static Key getPublicKeyFromBase64KeyEncodeStr(String keyStr) {
        byte[] keyBytes = Base64.decode(keyStr,flag);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        Key publicKey = null;
        try {
            publicKey = keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return publicKey;
    }

    /**
     * 公钥加密方法
     * @param dataStr 要加密的数据
     * @param dataStr 公钥base64字符串
     * @return 加密后的base64字符串
     * @throws Exception
     */
    public static String encryptPublicKey(String dataStr) throws Exception{
        //要加密的数据
        System.out.println("要加密的数据:"+dataStr);
        byte[] data = dataStr.getBytes();
        // 对公钥解密
        Key decodePublicKey = getPublicKeyFromBase64KeyEncodeStr(publicKey);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, decodePublicKey);
        byte[] encodedData = cipher.doFinal(data);
        String encodedDataStr = new String(Base64.encode(encodedData,flag));
        System.out.println("公钥加密后的数据:"+encodedDataStr);
        return encodedDataStr;
    }

    /**
     * 使用公钥进行分段加密
     * @param dataStr 要加密的数据
     * @return 公钥base64字符串
     * @throws Exception
     */
    public static String encryptByPublicKey(String dataStr)
            throws Exception {
        //要加密的数据
        System.out.println("要加密的数据:"+dataStr);
        byte[] data = dataStr.getBytes();
        // 对公钥解密
        Key decodePublicKey = getPublicKeyFromBase64KeyEncodeStr(publicKey);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, decodePublicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        String encodedDataStr = new String(Base64.encode(encryptedData,flag));
        System.out.println("公钥加密后的数据:"+encodedDataStr);
        return encodedDataStr;
    }

    /**
     * 使用私钥进行分段解密
     * @param dataStr 使用base64处理过的密文
     * @return 解密后的数据
     * @throws Exception
     */
    public static String decryptByPrivateKey(String dataStr)
            throws Exception {

        byte[] encryptedData = Base64.decode(dataStr,flag);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key decodePrivateKey = getPrivateKeyFromBase64KeyEncodeStr(PrivateKey);

        Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, decodePrivateKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        String decodedDataStr = new String(decryptedData,"utf-8");
        System.out.println("私钥解密后的数据:"+decodedDataStr);
        return decodedDataStr;
    }



    /**
     * 使用公钥进行分段解密
     * @param dataStr 使用base64处理过的密文
     * @return 解密后的数据
     * @throws Exception
     */
    public static String decryptByPublicKey(String dataStr)
            throws Exception {

        byte[] encryptedData = Base64.decode(dataStr,flag);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key decodePrivateKey = getPublicKeyFromBase64KeyEncodeStr(publicKey);

        Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, decodePrivateKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        String decodedDataStr = new String(decryptedData,"utf-8");
        System.out.println("公钥解密后的数据:"+decodedDataStr);
        return decodedDataStr;
    }

    public static String getEscapeRSAINFO(String info){
        StringBuffer buffer=new StringBuffer();
        buffer.append( info.replace("-","+").replace("_","/"));
        return buffer.toString();
    }

    public static String setEscapeRSAINFO(String info){
        StringBuffer buffer=new StringBuffer();
        buffer.append( info.replace("+","-").replace("/","_"));
        return buffer.toString();
    }

}

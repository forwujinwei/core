package com.core.aop.bean.security;


import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import com.core.aop.bean.Encoding;
import com.core.aop.bean.Strings;
import com.core.aop.bean.error.BusinessException;
import com.core.aop.bean.error.CommonErrorCode;

public class AsymmetricKeys
{
  private static final int KEY_LENGTH = 512;
  private static final String ALGORITHM_RSA = "RSA";
  private static volatile KeyFactory keyFactory = null;
  private static volatile KeyPairGenerator keyPairGen = null;
  private RSAPublicKey publicKey;
  private RSAPrivateKey privateKey;
  private Cipher publicCipher;
  private Cipher privateCipher;
  
  private static final KeyFactory getKeyFactory()
    throws BusinessException
  {
    if (keyFactory != null) {
      return keyFactory;
    }
    synchronized (AsymmetricKeys.class)
    {
      if (keyFactory != null) {
        return keyFactory;
      }
      try
      {
        keyFactory = KeyFactory.getInstance("RSA");
      }
      catch (NoSuchAlgorithmException e)
      {
        throw 
        
          new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).put("algorithm", "RSA").setMessage("No Such Algorithm: ${algorithm}");
      }
      return keyFactory;
    }
  }
  
  private static final KeyPairGenerator getKeyPairGenerator()
    throws BusinessException
  {
    if (keyPairGen != null) {
      return keyPairGen;
    }
    synchronized (AsymmetricKeys.class)
    {
      if (keyPairGen != null) {
        return keyPairGen;
      }
      try
      {
        keyPairGen = KeyPairGenerator.getInstance("RSA");
      }
      catch (NoSuchAlgorithmException e)
      {
        throw 
        
          new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).put("algorithm", "RSA").setMessage("No Such Algorithm: ${algorithm}");
      }
      keyPairGen.initialize(512, new SecureRandom());
      return keyPairGen;
    }
  }
  
  public AsymmetricKeys()
    throws BusinessException
  {
    KeyPair keyPair = getKeyPairGenerator().generateKeyPair();
    this.publicKey = ((RSAPublicKey)keyPair.getPublic());
    this.privateKey = ((RSAPrivateKey)keyPair.getPrivate());
    this.publicCipher = initCipher(this.publicKey);
    this.privateCipher = initCipher(this.privateKey);
  }
  
  public AsymmetricKeys(String publicKeyText, String privateKeyText)
    throws BusinessException
  {
    this.publicKey = loadPublicKey(publicKeyText);
    this.privateKey = loadPrivateKey(privateKeyText);
    this.publicCipher = initCipher(this.publicKey);
    this.privateCipher = initCipher(this.privateKey);
  }
  
  private String encode(byte[] key)
  {
    return Base64.toBase64String(key);
  }
  
  private byte[] decode(String key)
  {
    return Base64.decode(key);
  }
  
  public String getPublicKey()
  {
    return encode(this.publicKey.getEncoded());
  }
  
  public String getPrivateKey()
  {
    return encode(this.privateKey.getEncoded());
  }
  
  private Cipher initCipher(RSAPrivateKey privateKey)
    throws BusinessException
  {
    try
    {
      Cipher cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
      cipher.init(2, privateKey);
      return cipher;
    }
    catch (NoSuchAlgorithmException e)
    {
      throw 
      
        new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).put("algorithm", "RSA").setMessage("No Such Algorithm: ${algorithm}");
    }
    catch (NoSuchPaddingException e)
    {
      throw 
        new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).setMessage("No Such Padding");
    }
    catch (InvalidKeyException e)
    {
      throw 
        new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).setMessage("解密私钥非法,请检查");
    }
  }
  
  private Cipher initCipher(RSAPublicKey publicKey)
    throws BusinessException
  {
    try
    {
      Cipher cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
      cipher.init(1, publicKey);
      return cipher;
    }
    catch (NoSuchAlgorithmException e)
    {
      throw 
      
        new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).put("algorithm", "RSA").setMessage("No Such Algorithm: ${algorithm}");
    }
    catch (NoSuchPaddingException e)
    {
      throw 
        new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).setMessage("No Such Padding");
    }
    catch (InvalidKeyException e)
    {
      throw 
        new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).setMessage("加密公私非法,请检查");
    }
  }
  
  private RSAPublicKey loadPublicKey(String publicKeyText)
    throws BusinessException
  {
    try
    {
      byte[] data = decode(publicKeyText);
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
      return (RSAPublicKey)getKeyFactory().generatePublic(keySpec);
    }
    catch (InvalidKeySpecException e)
    {
      throw 
      
        new BusinessException(CommonErrorCode.INVALID_PARAM, e).put("publicKey", publicKeyText).setMessage("公钥非法");
    }
  }
  
  private RSAPrivateKey loadPrivateKey(String privateKeyText)
    throws BusinessException
  {
    try
    {
      byte[] data = decode(privateKeyText);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
      return (RSAPrivateKey)getKeyFactory().generatePrivate(keySpec);
    }
    catch (InvalidKeySpecException e)
    {
      throw 
      
        new BusinessException(CommonErrorCode.INVALID_PARAM, e).put("privateKey", privateKeyText).setMessage("私钥非法");
    }
  }
  
  private byte[] encrypt(byte[] data)
    throws Exception
  {
    try
    {
      return this.publicCipher.doFinal(data);
    }
    catch (IllegalBlockSizeException e)
    {
      throw new Exception("明文长度非法", e);
    }
    catch (BadPaddingException e)
    {
      throw new Exception("明文数据已损坏", e);
    }
  }
  
  public String encrypt(String plainText)
    throws BusinessException
  {
    try
    {
      byte[] data = plainText.getBytes(Encoding.UTF8.getName());
      byte[] encryptedData = encrypt(data);
      return Strings.toHexString(encryptedData);
    }
    catch (Exception e)
    {
      throw 
      
        new BusinessException(CommonErrorCode.INVALID_PARAM, e).put("plainText", plainText).setMessage("非法的明文数据");
    }
  }
  
  public String decrypt(String cipherText)
    throws BusinessException
  {
    try
    {
      byte[] data = Strings.fromHexString(cipherText);
      byte[] decryptedData = decrypt(data);
      return new String(decryptedData, Encoding.UTF8.getName());
    }
    catch (Exception e)
    {
      throw 
      
        new BusinessException(CommonErrorCode.INVALID_PARAM, e).put("cipherText", cipherText).setMessage("非法的密文");
    }
  }
  
  private byte[] decrypt(byte[] data)
    throws Exception
  {
    if (this.privateKey == null) {
      throw new Exception("解密私钥为空, 请设置");
    }
    try
    {
      return this.privateCipher.doFinal(data);
    }
    catch (IllegalBlockSizeException e)
    {
      throw new Exception("密文长度非法", e);
    }
    catch (BadPaddingException e)
    {
      throw new Exception("密文数据已损坏", e);
    }
  }
}


/* Location:           C:\Users\wujinwei\.m2\repository\terran4j\terran4j-commons-util\1.0.0\terran4j-commons-util-1.0.0.jar
 * Qualified Name:     com.terran4j.commons.util.security.AsymmetricKeys
 * JD-Core Version:    0.7.0.1
 */
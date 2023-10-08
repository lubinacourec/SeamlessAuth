package anon.seamlessauth.util;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import anon.seamlessauth.SeamlessAuth;
import cpw.mods.fml.common.FMLCommonHandler;

public class CryptoInstances {
    public static MessageDigest sha;
    public static KeyFactory rsaFactory;
    public static KeyPairGenerator rsaGenerator;
    public static Cipher rsaCipher;
    
    static {
        try {
            sha = MessageDigest.getInstance("SHA-256");
            rsaFactory = KeyFactory.getInstance("RSA");
            rsaGenerator = KeyPairGenerator.getInstance("RSA");
            rsaCipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            SeamlessAuth.LOG.fatal("failed to get a crypto instance", e);
            FMLCommonHandler.instance()
                .exitJava(1, false);
        }
    }
}

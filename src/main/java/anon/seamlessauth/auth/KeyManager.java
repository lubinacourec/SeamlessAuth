package anon.seamlessauth.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import anon.seamlessauth.SeamlessAuth;
import cpw.mods.fml.common.FMLCommonHandler;

public class KeyManager {

    public PublicKey pubKey;
    public PrivateKey prvKey;

    private Cipher cipher;

    public KeyManager(String pubKeyPath, String prvKeyPath) {
        SeamlessAuth.LOG.info("Loading keys at [(" + pubKeyPath + "), (" + prvKeyPath + ")]...");
        try {
            byte[] pubData = Files.readAllBytes(Paths.get(pubKeyPath));
            byte[] prvData = Files.readAllBytes(Paths.get(prvKeyPath));

            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubData);
            PKCS8EncodedKeySpec prvSpec = new PKCS8EncodedKeySpec(prvData);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            pubKey = factory.generatePublic(pubSpec);
            prvKey = factory.generatePrivate(prvSpec);

            SeamlessAuth.LOG.info("Successfully loaded stored keys!");
        } catch (NoSuchFileException e) {
            SeamlessAuth.LOG.info("No existing keys found, generating new pair...");

            KeyPairGenerator kpg;
            try {
                kpg = KeyPairGenerator.getInstance("RSA");
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                SeamlessAuth.LOG.fatal("failed to get RSA generator", noSuchAlgorithmException);
                FMLCommonHandler.instance()
                    .exitJava(1, false);
                return;
            }

            kpg.initialize(4096);

            KeyPair pair = kpg.generateKeyPair();
            pubKey = pair.getPublic();
            prvKey = pair.getPrivate();

            try {
                Files.write(Paths.get(pubKeyPath), pubKey.getEncoded());
                Files.write(Paths.get(prvKeyPath), prvKey.getEncoded());
            } catch (IOException ioException) {
                SeamlessAuth.LOG
                    .fatal("failed to write keys, crashing to prevent usage of unrecoverable keys", ioException);
                FMLCommonHandler.instance()
                    .exitJava(1, false);
            }
        } catch (Exception e) {
            SeamlessAuth.LOG.fatal("failed to load existing keys", e);
            FMLCommonHandler.instance()
                .exitJava(1, false);
        }

        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, prvKey);
        } catch (Exception e) {
            SeamlessAuth.LOG.fatal("failed to initialise RSA cipher", e);
            FMLCommonHandler.instance()
                .exitJava(1, false);
        }
    }

    public byte[] decrypt(byte[] in) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(in);
    }
}

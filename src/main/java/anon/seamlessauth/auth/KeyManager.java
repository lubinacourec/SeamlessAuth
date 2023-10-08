package anon.seamlessauth.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.util.CryptoInstances;
import cpw.mods.fml.common.FMLCommonHandler;

public class KeyManager {

    public PublicKey pubKey;
    public PrivateKey prvKey;

    public KeyManager(String pubKeyPath, String prvKeyPath) {
        SeamlessAuth.LOG.info("Loading keys at [(" + pubKeyPath + "), (" + prvKeyPath + ")]...");
        try {
            byte[] pubData = Files.readAllBytes(Paths.get(pubKeyPath));
            byte[] prvData = Files.readAllBytes(Paths.get(prvKeyPath));

            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubData);
            PKCS8EncodedKeySpec prvSpec = new PKCS8EncodedKeySpec(prvData);

            pubKey = CryptoInstances.rsaFactory.generatePublic(pubSpec);
            prvKey = CryptoInstances.rsaFactory.generatePrivate(prvSpec);

            SeamlessAuth.LOG.info("Successfully loaded stored keys!");
        } catch (NoSuchFileException e) {
            SeamlessAuth.LOG.info("No existing keys found, generating new pair...");

            CryptoInstances.rsaGenerator.initialize(4096);

            KeyPair pair = CryptoInstances.rsaGenerator.generateKeyPair();
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
    }

    public byte[] decrypt(byte[] in) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        CryptoInstances.rsaCipher.init(Cipher.DECRYPT_MODE, prvKey);
        return CryptoInstances.rsaCipher.doFinal(in);
    }
}

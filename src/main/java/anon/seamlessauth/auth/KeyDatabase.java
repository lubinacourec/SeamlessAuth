package anon.seamlessauth.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Charsets;

import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.util.CryptoInstances;
import anon.seamlessauth.util.Pair;
import cpw.mods.fml.common.FMLCommonHandler;

public class KeyDatabase {

    /* username -> (uuid, key) */
    public Map<String, Pair<UUID, PublicKey>> authorized = new HashMap<String, Pair<UUID, PublicKey>>();

    public String path;

    public KeyDatabase(String databasePath) {
        path = databasePath;

        try {
            List<String> lines = Files.readAllLines(Paths.get(path), Charsets.UTF_8);

            for (String line : lines) {
                String[] components = line.split(" ");
                if (components.length != 3) {
                    SeamlessAuth.LOG.warn("invalid entry in key database");
                    continue;
                }

                UUID uuid = UUID.fromString(components[1]);
                byte[] decoded = Base64.getDecoder()
                    .decode(components[2]);
                PublicKey key;
                try {
                    key = CryptoInstances.rsaFactory.generatePublic(new X509EncodedKeySpec(decoded));
                } catch (InvalidKeySpecException e) {
                    SeamlessAuth.LOG.warn("invalid key in entry for: " + components[0]);
                    continue;
                }

                authorized.put(components[0], new Pair<UUID, PublicKey>(uuid, key));
            }
        } catch (NoSuchFileException e) {
            SeamlessAuth.LOG.info("no existing key database found");
        } catch (IOException e) {
            SeamlessAuth.LOG.fatal("failed to read key database", e);
            FMLCommonHandler.instance()
                .exitJava(1, false);
        }
    }

    public void addUser(String username, UUID uuid, PublicKey key) {
        authorized.put(username, new Pair<UUID, PublicKey>(uuid, key));

        String encoded = Base64.getEncoder()
            .encodeToString(key.getEncoded());
        try {
            Files.write(
                Paths.get(path),
                String.format("%s %s %s\n", username, uuid.toString(), encoded)
                    .getBytes(Charsets.UTF_8),
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
        } catch (IOException e) {
            SeamlessAuth.LOG.warn("failed to write to key database", e);
        }
    }
}

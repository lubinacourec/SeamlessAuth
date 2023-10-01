package anon.seamlessauth.skin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import anon.seamlessauth.Config;
import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.skin.network.PacketDispatcher;
import anon.seamlessauth.skin.network.SkinQuery;
import anon.seamlessauth.skin.network.SkinRequest;
import anon.seamlessauth.util.Bytes;
import anon.seamlessauth.util.Pair;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientSkinHandler {

    public static final ClientSkinHandler instance = new ClientSkinHandler();

    private MessageDigest shaInstance;

    private Map<UUID, List<QueryCallback>> queries = new HashMap<>();
    private Map<Bytes, List<SkinCallback>> requests = new HashMap<>();

    public Map<UUID, Pair<byte[], byte[]>> queryCache = new HashMap<>();

    public byte[] skinData;
    public byte[] capeData;

    public byte[] skinHash;
    public byte[] capeHash;

    public ClientSkinHandler() {
        try {
            shaInstance = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            SeamlessAuth.LOG.fatal("failed to get SHA instance", e);
            FMLCommonHandler.instance()
                .exitJava(1, false);
        }

        reload();
    }

    public void reload() {
        try {
            skinData = Files.readAllBytes(Paths.get(Config.expandPath(Config.skinPath)));
            skinHash = shaInstance.digest(skinData);
        } catch (IOException e) {
            skinData = null;
            skinHash = null;
            SeamlessAuth.LOG.warn("Failed to load skin!");
        }

        try {
            capeData = Files.readAllBytes(Paths.get(Config.expandPath(Config.capePath)));
            capeHash = shaInstance.digest(capeData);
        } catch (IOException e) {
            capeData = null;
            capeHash = null;
            SeamlessAuth.LOG.warn("Failed to load cape!");
        }
    }

    public byte[] getDataFromHash(byte[] hash) {
        if (Arrays.equals(hash, skinHash)) return skinData;
        if (Arrays.equals(hash, capeHash)) return capeData;
        return null;
    }

    public void querySkin(UUID uuid, QueryCallback callback) {
        if (callback != null) {
            queries.putIfAbsent(uuid, new ArrayList<>());

            List<QueryCallback> list = queries.get(uuid);
            list.add(callback);

            if (queryCache.containsKey(uuid)) queryCompleted(uuid, queryCache.get(uuid));
            else if (list.size() == 1) PacketDispatcher.dispatcher.sendToServer(new SkinQuery(uuid));
        } else PacketDispatcher.dispatcher.sendToServer(new SkinQuery(uuid));
    }

    public void queryCompleted(UUID uuid, Pair<byte[], byte[]> available) {
        if (!queries.containsKey(uuid)) return;
        queries.get(uuid)
            .forEach(cb -> cb.queryCompleted(available));
        queries.remove(uuid);

        queryCache.put(uuid, available);
    }

    public void requestSkin(byte[] hash, SkinCallback callback) {
        Bytes key = new Bytes(hash);
        requests.putIfAbsent(key, new ArrayList<>());

        List<SkinCallback> list = requests.get(key);
        list.add(callback);

        if (list.size() == 1) PacketDispatcher.dispatcher.sendToServer(new SkinRequest(hash));
    }

    public void requestCompleted(byte[] hash, byte[] data) {
        Bytes key = new Bytes(hash);
        if (!requests.containsKey(key)) return;
        requests.get(key)
            .forEach(cb -> cb.requestCompleted(data));
        requests.remove(key);
    }

    public interface QueryCallback {

        public void queryCompleted(Pair<byte[], byte[]> available);
    }

    public interface SkinCallback {

        public void requestCompleted(byte[] data);
    }
}

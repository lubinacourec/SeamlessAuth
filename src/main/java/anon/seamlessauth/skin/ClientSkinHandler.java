package anon.seamlessauth.skin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;

import anon.seamlessauth.Config;
import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.skin.network.PacketDispatcher;
import anon.seamlessauth.skin.network.SkinAnswer;
import anon.seamlessauth.skin.network.SkinQuery;
import anon.seamlessauth.skin.network.SkinRequest;
import anon.seamlessauth.util.Bytes;
import anon.seamlessauth.util.CryptoInstances;
import anon.seamlessauth.util.Pair;

public class ClientSkinHandler {

    public static final ClientSkinHandler instance = new ClientSkinHandler();

    private Map<UUID, List<QueryCallback>> queryCallbacks = new HashMap<>();
    private Map<Bytes, List<SkinCallback>> textureCallbacks = new HashMap<>();

    public Map<UUID, Pair<byte[], byte[]>> queryCache = new HashMap<>();

    public byte[] skinData;
    public byte[] capeData;

    public byte[] skinHash;
    public byte[] capeHash;

    public ClientSkinHandler() {
        reload();
    }

    public void reload() {
        try {
            skinData = Files.readAllBytes(Paths.get(Config.expandPath(Config.skinPath)));
            skinHash = CryptoInstances.sha.digest(skinData);
        } catch (IOException e) {
            skinData = null;
            skinHash = null;
            SeamlessAuth.LOG.warn("Failed to load skin!");
        }

        try {
            capeData = Files.readAllBytes(Paths.get(Config.expandPath(Config.capePath)));
            capeHash = CryptoInstances.sha.digest(capeData);
        } catch (IOException e) {
            capeData = null;
            capeHash = null;
            SeamlessAuth.LOG.warn("Failed to load cape!");
        }

        if (Config.enableSkinSharing && !Minecraft.getMinecraft()
            .isSingleplayer()) PacketDispatcher.sendToServer(new SkinAnswer(null, skinHash, capeHash));
    }

    public byte[] getDataFromHash(byte[] hash) {
        if (Arrays.equals(hash, skinHash)) return skinData;
        if (Arrays.equals(hash, capeHash)) return capeData;
        return null;
    }

    public void querySkin(UUID uuid, QueryCallback callback) {
        if (!Config.enableSkinSharing) return;
        if (callback != null) {
            queryCallbacks.putIfAbsent(uuid, new ArrayList<>());

            List<QueryCallback> list = queryCallbacks.get(uuid);
            list.add(callback);

            if (queryCache.containsKey(uuid)) queryCompleted(uuid, queryCache.get(uuid));
            else PacketDispatcher.sendToServer(new SkinQuery(uuid));
        } else PacketDispatcher.sendToServer(new SkinQuery(uuid));
    }

    public void queryCompleted(UUID uuid, Pair<byte[], byte[]> available) {
        queryCache.put(uuid, available);

        if (!queryCallbacks.containsKey(uuid)) return;
        queryCallbacks.get(uuid)
            .forEach(cb -> cb.queryCompleted(available));
    }

    public void requestSkin(byte[] hash, SkinCallback callback) {
        Bytes key = new Bytes(hash);
        textureCallbacks.putIfAbsent(key, new ArrayList<>());

        List<SkinCallback> list = textureCallbacks.get(key);
        list.add(callback);

        if (list.size() == 1) PacketDispatcher.sendToServer(new SkinRequest(hash));
    }

    public void requestCompleted(byte[] hash, byte[] data) {
        Bytes key = new Bytes(hash);
        if (!textureCallbacks.containsKey(key)) return;
        textureCallbacks.get(key)
            .forEach(cb -> cb.requestCompleted(data));
        textureCallbacks.remove(key);
    }

    public interface QueryCallback {

        public void queryCompleted(Pair<byte[], byte[]> available);
    }

    public interface SkinCallback {

        public void requestCompleted(byte[] data);
    }
}

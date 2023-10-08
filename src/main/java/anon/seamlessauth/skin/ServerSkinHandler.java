package anon.seamlessauth.skin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.io.FileUtils;

import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.skin.network.PacketDispatcher;
import anon.seamlessauth.skin.network.SkinAnswer;
import anon.seamlessauth.skin.network.SkinQuery;
import anon.seamlessauth.skin.network.SkinRequest;
import anon.seamlessauth.skin.network.SkinResponse;
import anon.seamlessauth.util.Bytes;
import anon.seamlessauth.util.CryptoInstances;
import anon.seamlessauth.util.Pair;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ServerSkinHandler {

    public static final ServerSkinHandler instance = new ServerSkinHandler();
    private final static File cacheDir = new File("skin-cache");

    private Map<UUID, List<EntityPlayerMP>> queryRequesters = new HashMap<>();
    private Map<Bytes, List<EntityPlayerMP>> textureRequesters = new HashMap<>();

    private Map<UUID, Pair<byte[], byte[]>> queryCache = new HashMap<>();
    private Map<Bytes, List<UUID>> ownerMap = new HashMap<>();

    @EventHandler
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.player.getUniqueID();
        queryRequesters.remove(uuid);
        Pair<byte[], byte[]> hashes = queryCache.get(uuid);
        if (hashes != null) {
            if (hashes.first != null) ownerMap.get(new Bytes(hashes.first))
                .remove(uuid);
            if (hashes.second != null) ownerMap.get(new Bytes(hashes.second))
                .remove(uuid);
        }
    }

    public void querySkin(UUID uuid, EntityPlayerMP requester) {
        queryRequesters.putIfAbsent(uuid, new ArrayList<>());

        List<EntityPlayerMP> list = queryRequesters.get(uuid);
        list.add(requester);

        Pair<byte[], byte[]> cached = queryCache.get(uuid);
        if (cached != null) PacketDispatcher.sendTo(new SkinAnswer(uuid, cached.first, cached.second), requester);
        else {
            EntityPlayerMP target = getPlayerFromUUID(uuid);
            if (target == null) {
                queryCompleted(uuid, new Pair<>(null, null));
                return;
            }

            PacketDispatcher.sendTo(new SkinQuery(uuid), target);
        }
    }

    public void queryCompleted(UUID uuid, Pair<byte[], byte[]> available) {
        if (queryCache.containsKey(uuid) && queryCache.get(uuid)
            .equals(available)) return;

        queryCache.put(uuid, available);
        if (available.first != null) {
            Bytes key = new Bytes(available.first);
            ownerMap.putIfAbsent(key, new ArrayList<>());
            ownerMap.get(key)
                .add(uuid);
        }

        if (available.second != null) {
            Bytes key = new Bytes(available.second);
            ownerMap.putIfAbsent(key, new ArrayList<>());
            ownerMap.get(key)
                .add(uuid);
        }

        if (!queryRequesters.containsKey(uuid)) return;
        IMessage packet = new SkinAnswer(uuid, available.first, available.second);
        queryRequesters.get(uuid)
            .forEach(player -> PacketDispatcher.sendTo(packet, player));
    }

    public void requestSkin(byte[] hash, EntityPlayerMP requester) {
        Bytes key = new Bytes(hash);
        textureRequesters.putIfAbsent(key, new ArrayList<>());

        List<EntityPlayerMP> list = textureRequesters.get(key);
        list.add(requester);

        try {
            requestCompleted(hash, FileUtils.readFileToByteArray(getCacheFile(hash)), true);
            return;
        } catch (IOException e) {}

        EntityPlayerMP target;
        try {
            target = getPlayerFromUUID(
                ownerMap.get(key)
                    .get(0));
        } catch (IndexOutOfBoundsException e) {
            target = null;
        }

        if (target == null) {
            requestCompleted(hash, null, false);
            return;
        }

        PacketDispatcher.sendTo(new SkinRequest(hash), target);
    }

    public void requestCompleted(byte[] hash, byte[] data, boolean fromCache) {
        if (data != null) {
            byte[] computed = CryptoInstances.sha.digest(data);
            if (!Arrays.equals(hash, computed)) {
                SeamlessAuth.LOG.warn("computed hash differed from expected hash");
                return;
            }
        }

        Bytes key = new Bytes(hash);
        try {
            if (data != null && !fromCache) FileUtils.writeByteArrayToFile(getCacheFile(hash), data);
        } catch (IOException e) {
            SeamlessAuth.LOG.warn(
                "failed to copy file to cache: " + Base64.getUrlEncoder()
                    .encodeToString(hash),
                e);
        }

        if (!textureRequesters.containsKey(key)) return;
        textureRequesters.get(key)
            .forEach(player -> PacketDispatcher.sendTo(new SkinResponse(hash, data), player));
        textureRequesters.remove(key);
    }

    @SuppressWarnings("unchecked")
    private static EntityPlayerMP getPlayerFromUUID(UUID uuid) {
        if (uuid == null) return null;

        List<EntityPlayerMP> players = MinecraftServer.getServer()
            .getConfigurationManager().playerEntityList;
        for (EntityPlayerMP player : players) if (player.getUniqueID()
            .equals(uuid)) return player;
        return null;
    }

    public static File getCacheFile(byte[] hash) {
        return new File(
            cacheDir,
            Base64.getUrlEncoder()
                .encodeToString(hash));
    }
}

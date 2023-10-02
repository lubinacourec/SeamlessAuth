package anon.seamlessauth.skin;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import anon.seamlessauth.util.Pair;
import cpw.mods.fml.common.FMLCommonHandler;
import scala.actors.threadpool.Arrays;

public class ServerSkinHandler {

    public static final ServerSkinHandler instance = new ServerSkinHandler();
    private final static File cacheDir = new File("skin-cache");

    private MessageDigest shaInstance;

    private Map<UUID, List<EntityPlayerMP>> queries = new HashMap<>();
    private Map<Bytes, List<EntityPlayerMP>> requests = new HashMap<>();

    private Map<UUID, Pair<byte[], byte[]>> queryCache = new HashMap<>();
    private Map<Bytes, UUID> ownerMap = new HashMap<>();

    public ServerSkinHandler() {
        try {
            shaInstance = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            SeamlessAuth.LOG.fatal("failed to get SHA instance", e);
            FMLCommonHandler.instance()
                .exitJava(1, false);
        }
    }

    public void querySkin(UUID uuid, EntityPlayerMP requester) {
        queries.putIfAbsent(uuid, new ArrayList<>());

        List<EntityPlayerMP> list = queries.get(uuid);
        list.add(requester);

        if (queryCache.containsKey(uuid)) queryCompleted(uuid, queryCache.get(uuid));
        else if (list.size() == 1) {
            EntityPlayerMP target = getPlayerFromUUID(uuid);
            if (target == null) {
                queryCompleted(uuid, new Pair<>(null, null));
                return;
            }

            PacketDispatcher.sendTo(new SkinQuery(uuid), target);
        }
    }

    public void queryCompleted(UUID uuid, Pair<byte[], byte[]> available) {
        if (!queries.containsKey(uuid)) return;
        queries.get(uuid)
            .forEach(
                player -> PacketDispatcher.sendTo(new SkinAnswer(uuid, available.first, available.second), player));
        queries.remove(uuid);

        queryCache.put(uuid, available);
        if (available.first != null) ownerMap.put(new Bytes(available.first), uuid);
        if (available.second != null) ownerMap.put(new Bytes(available.second), uuid);
    }

    public void requestSkin(byte[] hash, EntityPlayerMP requester) {
        Bytes key = new Bytes(hash);
        requests.putIfAbsent(key, new ArrayList<>());

        List<EntityPlayerMP> list = requests.get(key);
        list.add(requester);

        try {
            requestCompleted(hash, FileUtils.readFileToByteArray(getCacheFile(hash)), true);
            return;
        } catch (IOException e) {}

        EntityPlayerMP target = getPlayerFromUUID(ownerMap.get(key));
        if (target == null) {
            requestCompleted(hash, null, false);
            return;
        }

        PacketDispatcher.sendTo(new SkinRequest(hash), target);
    }

    public void requestCompleted(byte[] hash, byte[] data, boolean fromCache) {
        if (data != null) {
            byte[] computed = shaInstance.digest(data);
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

        if (!requests.containsKey(key)) return;
        requests.get(key)
            .forEach(player -> PacketDispatcher.sendTo(new SkinResponse(hash, data), player));
        requests.remove(key);
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

package anon.seamlessauth.util;

import java.util.Base64;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import anon.seamlessauth.skin.ClientSkinHandler;

// exists because mixin spergs out when you have nested anonymous classes/whatever
// or maybe because the nested classes are in the mixins package? (this class broke too while under
// anon.seamlessauth.mixins)
// dunno, anyway this works so I'm not touching it anymore
public class SkinMixinHelper implements ClientSkinHandler.QueryCallback {

    private SkinManager parent;
    private SkinManager.SkinAvailableCallback callback;

    public SkinMixinHelper(SkinManager parent, SkinManager.SkinAvailableCallback callback) {
        this.parent = parent;
        this.callback = callback;
    }

    public static MinecraftProfileTexture profileFromPath(String path) {
        return new MinecraftProfileTexture("local://" + path, null);
    }

    public static MinecraftProfileTexture profileFromHash(byte[] hash) {
        return new MinecraftProfileTexture(
            "remote://" + Base64.getUrlEncoder()
                .encodeToString(hash),
            null);
    }

    @Override
    public void queryCompleted(Pair<byte[], byte[]> available) {
        // ensures we run on the main thread (need an opengl context)
        Minecraft.getMinecraft()
            .func_152344_a(new Runnable() {

                public void run() {
                    if (available.first != null)
                        parent.func_152789_a(profileFromHash(available.first), Type.SKIN, callback);
                    if (available.second != null)
                        parent.func_152789_a(profileFromHash(available.second), Type.CAPE, callback);
                }
            });
    }
}

package anon.seamlessauth.mixins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import anon.seamlessauth.Config;
import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.Tags;
import anon.seamlessauth.skin.ClientSkinHandler;
import anon.seamlessauth.skin.client.FileTexture;
import anon.seamlessauth.skin.client.ServerTexture;
import anon.seamlessauth.util.Pair;
import anon.seamlessauth.util.SkinMixinHelper;

@Mixin(SkinManager.class)
// this mixin performs 2 jobs: replacing any calls to MinecraftSessionService.getTextures, and changing
// ThreadDownloadImageData to ServerTexture
public class SkinMixin {

    @Shadow
    private TextureManager field_152795_c;

    @Overwrite
    public ResourceLocation func_152789_a(MinecraftProfileTexture skin, final Type kind,
        final SkinManager.SkinAvailableCallback callback) {

        String url = skin.getUrl();
        boolean local;

        if (url.startsWith("remote://")) {
            local = false;
        } else if (url.startsWith("local://")) {
            local = true;
        } else {
            SeamlessAuth.LOG.warn("invalid profile texture passed to SkinManager, other mods may be interfering");
            return SkinManager.field_152793_a;
        }
        String path = url.substring(url.indexOf("://") + 3);

        ResourceLocation resourceLocation = new ResourceLocation(Tags.MODID, path);
        ITextureObject iTextureObject = field_152795_c.getTexture(resourceLocation);

        if (iTextureObject != null) {
            if (callback != null) callback.func_152121_a(kind, resourceLocation);
        } else {
            ImageBufferDownload imageBufferDownload = kind == Type.SKIN ? new ImageBufferDownload() : null;
            IImageBuffer imageBuffer = new IImageBuffer() {

                public BufferedImage parseUserSkin(BufferedImage image) {
                    if (imageBufferDownload != null) image = imageBufferDownload.parseUserSkin(image);
                    return image;
                }

                public void func_152634_a() {
                    if (imageBufferDownload != null) imageBufferDownload.func_152634_a();
                    if (callback != null) callback.func_152121_a(kind, resourceLocation);
                }
            };
            ITextureObject textureObject;

            if (!local) {
                byte[] hash;
                try {
                    hash = Base64.getUrlDecoder()
                        .decode(path);
                } catch (IllegalArgumentException e) {
                    SeamlessAuth.LOG.warn("invalid remote skin hash: " + url, e);
                    return SkinManager.field_152793_a;
                }

                File cacheFile = new File(new File(Minecraft.getMinecraft().mcDataDir, "skin-cache"), path);

                textureObject = new ServerTexture(hash, cacheFile, SkinManager.field_152793_a, imageBuffer);
            } else textureObject = new FileTexture(new File(path), SkinManager.field_152793_a, imageBuffer);

            field_152795_c.loadTexture(resourceLocation, textureObject);
        }

        return resourceLocation;
    }

    @Overwrite
    public void func_152790_a(final GameProfile user, final SkinManager.SkinAvailableCallback callback,
        final boolean _secure) {
        if (user.getId()
            .equals(
                Minecraft.getMinecraft()
                    .getSession()
                    .func_148256_e()
                    .getId())
            || Minecraft.getMinecraft()
                .isSingleplayer()) {
            if (ClientSkinHandler.instance.skinData != null)
                func_152789_a(SkinMixinHelper.profileFromPath(Config.expandPath(Config.skinPath)), Type.SKIN, callback);
            if (ClientSkinHandler.instance.capeData != null)
                func_152789_a(SkinMixinHelper.profileFromPath(Config.expandPath(Config.capePath)), Type.CAPE, callback);
            return;
        }

        if (Config.enableSkinSharing) ClientSkinHandler.instance
            .querySkin(user.getId(), new SkinMixinHelper((SkinManager) (Object) this, callback));
    }

    @Overwrite
    // only used for skull rendering (in vanilla at least)
    public Map<Type, MinecraftProfileTexture> func_152788_a(GameProfile user) {
        Map<Type, MinecraftProfileTexture> map = new HashMap<Type, MinecraftProfileTexture>();
        Pair<byte[], byte[]> pair = ClientSkinHandler.instance.queryCache.get(user.getId());
        if (pair == null) return map;

        if (pair.first != null) map.put(Type.SKIN, SkinMixinHelper.profileFromHash(pair.first));
        if (pair.second != null) map.put(Type.CAPE, SkinMixinHelper.profileFromHash(pair.second));
        return map;
    }
}

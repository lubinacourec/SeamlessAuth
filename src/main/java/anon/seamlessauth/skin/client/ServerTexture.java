package anon.seamlessauth.skin.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.FileUtils;

import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.skin.ClientSkinHandler;

public class ServerTexture extends FileTexture {

    private final byte[] hash;

    private boolean downloadQueued = false;
    private BufferedImage bufferedImage;

    public ServerTexture(byte[] hash, File cache, ResourceLocation placeholder, IImageBuffer imageBuffer) {
        super(cache, placeholder, imageBuffer);
        this.hash = hash;
    }

    private void ensureTextureUploaded() {
        if (bufferedImage == null) return;

        if (textureLocation != null) deleteGlTexture();
        TextureUtil.uploadTextureImage(super.getGlTextureId(), bufferedImage);
        bufferedImage = null;
    }

    public int getGlTextureId() {
        ensureTextureUploaded();
        return super.getGlTextureId();
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException {
        super.loadTexture(resourceManager);
        if (!fileLoaded) requestTexture();
    }

    protected void requestTexture() {
        if (downloadQueued) return;
        downloadQueued = true;

        ClientSkinHandler.instance.requestSkin(hash, new ClientSkinHandler.SkinCallback() {

            @Override
            public void requestCompleted(byte[] data) {
                BufferedImage image;

                try {
                    FileUtils.copyInputStreamToFile(new ByteArrayInputStream(data), file);
                    image = ImageIO.read(file);
                    if (image == null) throw new IOException("Failed to load image.");
                } catch (IOException e) {
                    SeamlessAuth.LOG.warn(
                        "failed to read data for resource: " + Base64.getUrlEncoder()
                            .encodeToString(hash),
                        e);
                    return;
                }

                if (imageBuffer != null) {
                    image = imageBuffer.parseUserSkin(image);
                    imageBuffer.func_152634_a();
                }
                bufferedImage = image;

                downloadQueued = false;
            }
        });
    }
}

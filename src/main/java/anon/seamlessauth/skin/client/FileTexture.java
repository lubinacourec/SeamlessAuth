package anon.seamlessauth.skin.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class FileTexture extends SimpleTexture {

    public File file;
    public IImageBuffer imageBuffer;
    public boolean fileLoaded = false;

    public FileTexture(File file, ResourceLocation placeholder, IImageBuffer imageBuffer) {
        super(placeholder);
        this.file = file;
        this.imageBuffer = imageBuffer;
    }

    public void loadTexture(IResourceManager manager) throws IOException {
        fileLoaded = false;
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            if (bufferedImage == null) throw new IOException("Failed to load image.");

            if (imageBuffer != null) {
                bufferedImage = imageBuffer.parseUserSkin(bufferedImage);
                imageBuffer.func_152634_a();
            }

            deleteGlTexture();
            TextureUtil.uploadTextureImage(getGlTextureId(), bufferedImage);
            fileLoaded = true;
        } catch (IOException e) {
            if (textureLocation != null) super.loadTexture(manager);
        }
    }
}

package anon.seamlessauth.skin.network;

import java.io.IOException;
import java.util.Base64;

import net.minecraft.network.Packet;

import anon.seamlessauth.Config;
import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.skin.ClientSkinHandler;
import anon.seamlessauth.skin.ServerSkinHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class SkinResponse implements IMessage, IMessageHandler<SkinResponse, IMessage> {

    public byte[] hash;
    public byte[] data;

    public SkinResponse() {}

    public SkinResponse(byte[] hash, byte[] data) {
        this.hash = hash;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buf.readBytes(hash = new byte[32]);
        try {
            data = Packet.readBlob(buf);
            if (data.length == 0) data = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBytes(hash);
        if (data != null) Packet.writeBlob(buf, data);
        else buf.writeShort(0);
    }

    @Override
    public IMessage onMessage(SkinResponse message, MessageContext ctx) {
        SeamlessAuth.debug(
            "recieved SkinResponse for {}",
            Base64.getUrlEncoder()
                .encodeToString(message.hash));
        if (!Config.enableSkinSharing) return null;

        if (ctx.side == Side.CLIENT) ClientSkinHandler.instance.requestCompleted(message.hash, message.data);
        if (ctx.side == Side.SERVER) ServerSkinHandler.instance.requestCompleted(message.hash, message.data, false);

        return null;
    }
}

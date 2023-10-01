package anon.seamlessauth.skin.network;

import java.util.UUID;

import anon.seamlessauth.skin.ClientSkinHandler;
import anon.seamlessauth.skin.ServerSkinHandler;
import anon.seamlessauth.util.Pair;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class SkinAnswer implements IMessage, IMessageHandler<SkinAnswer, IMessage> {

    UUID uuid;
    byte[] skinHash;
    byte[] capeHash;

    public SkinAnswer() {}

    public SkinAnswer(UUID uuid, byte[] skinHash, byte[] capeHash) {
        this.uuid = uuid;
        this.skinHash = skinHash;
        this.capeHash = capeHash;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        uuid = new UUID(buf.readLong(), buf.readLong());
        if (buf.readBoolean()) buf.readBytes(skinHash = new byte[32]);
        if (buf.readBoolean()) buf.readBytes(capeHash = new byte[32]);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());

        buf.writeBoolean(skinHash != null);
        if (skinHash != null) buf.writeBytes(skinHash);

        buf.writeBoolean(capeHash != null);
        if (capeHash != null) buf.writeBytes(capeHash);
    }

    @Override
    public IMessage onMessage(SkinAnswer message, MessageContext ctx) {
        Pair<byte[], byte[]> pair = new Pair<>(message.skinHash, message.capeHash);
        if (ctx.side == Side.CLIENT) ClientSkinHandler.instance.queryCompleted(message.uuid, pair);
        if (ctx.side == Side.SERVER) ServerSkinHandler.instance.queryCompleted(message.uuid, pair);

        return null;
    }
}

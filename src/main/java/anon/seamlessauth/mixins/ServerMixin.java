package anon.seamlessauth.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.network.packet.KeyRequest;
import anon.seamlessauth.network.server.NetHandlerAuthServer;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;

@Mixin(NetHandlerLoginServer.class)
public abstract class ServerMixin {
    @Shadow
    public abstract void func_147322_a(String reason);
    @Shadow
    private NetHandlerLoginServer.LoginState field_147328_g;
    @Shadow
    public MinecraftServer field_147327_f;
    @Shadow
    public NetworkManager field_147333_a;
    @Shadow
    public GameProfile field_147337_i ;

    @Inject(at = @At("RETURN"), method = "processLoginStart(Lnet/minecraft/network/login/client/C00PacketLoginStart;)V")
    public void processLoginStart(C00PacketLoginStart packetIn, CallbackInfo ci) {
        if (field_147328_g != NetHandlerLoginServer.LoginState.READY_TO_ACCEPT ||
            field_147333_a.isLocalChannel()) return;
        
        SeamlessAuth.LOG.info("Sending KeyRequest to client...");
        field_147328_g = NetHandlerLoginServer.LoginState.AUTHENTICATING;
        field_147333_a.setNetHandler(new NetHandlerAuthServer(field_147327_f, field_147333_a, field_147337_i));
        field_147333_a.scheduleOutboundPacket(new KeyRequest(), new GenericFutureListener[0]);
    }
}

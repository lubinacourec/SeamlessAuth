package anon.seamlessauth.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import anon.seamlessauth.SeamlessAuth;
import anon.seamlessauth.auth.network.client.NetHandlerAuthClient;

@Mixin(NetHandlerLoginClient.class)
public abstract class ClientMixin {

    @Shadow
    private NetworkManager field_147393_d;
    @Shadow
    private Minecraft field_147394_b;
    @Shadow
    private GuiScreen field_147395_c;

    @Inject(
        at = @At("HEAD"),
        method = "onConnectionStateTransition(Lnet/minecraft/network/EnumConnectionState;Lnet/minecraft/network/EnumConnectionState;)V")
    public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState,
        CallbackInfo ci) {
        /* KeyRequest will call us with (null, null), switch to our nethandler when this happens */
        if (oldState != null || newState != null) return;

        SeamlessAuth.LOG.info("KeyRequest recieved, proceeding with custom auth...");
        field_147393_d
            .setNetHandler(new NetHandlerAuthClient(this.field_147393_d, this.field_147394_b, this.field_147395_c));
        /* after this, KeyRequest will get the new nethandler from the old one and call handleKeyRequest */
    }
}

package anon.seamlessauth.mixins;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import anon.seamlessauth.network.packet.ChallengeRequest;
import anon.seamlessauth.network.packet.ChallengeResponse;
import anon.seamlessauth.network.packet.KeyRequest;
import anon.seamlessauth.network.packet.KeyResponse;

@Mixin(EnumConnectionState.class)
public class ConnectionStateMixin {

    @Inject(
        at = @At("HEAD"),
        method = "func_150752_a(Lnet/minecraft/network/Packet;)Lnet/minecraft/network/EnumConnectionState;",
        cancellable = true)
    private static void func_150752_a(Packet packetIn, CallbackInfoReturnable<EnumConnectionState> ci) {
        if (packetIn instanceof KeyRequest || packetIn instanceof KeyResponse
            || packetIn instanceof ChallengeRequest
            || packetIn instanceof ChallengeResponse) ci.setReturnValue(EnumConnectionState.LOGIN);
    }
}

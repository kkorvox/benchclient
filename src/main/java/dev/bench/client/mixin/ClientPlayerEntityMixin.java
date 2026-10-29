package dev.bench.client.mixin;

import dev.bench.client.core.Client;
import dev.bench.client.events.game.MoveEvent;
import dev.bench.client.events.game.TickEvent;
import dev.bench.client.util.RotationUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity)(Object)this;

        // fire tick event
        Client.get().events().post(new TickEvent());

        // fire move event
        MoveEvent mv = new MoveEvent(
            self.getVelocity().x,
            self.getVelocity().y,
            self.getVelocity().z
        );
        Client.get().events().post(mv);
        if (mv.isModified()) {
            self.setVelocity(mv.getX(), mv.getY(), mv.getZ());
        }

        // apply server-side rotations
        if (RotationUtil.isDirty()) {
            self.setYaw  (RotationUtil.getServerYaw());
            self.setPitch(RotationUtil.getServerPitch());
            RotationUtil.clean();
        }
    }
}

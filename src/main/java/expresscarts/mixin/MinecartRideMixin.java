package expresscarts.mixin;

import expresscarts.ExpressCart;
import expresscarts.RideMilestones;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Express Minecarts track their own ride distance directly in {@code ExpressMinecartEntity.tick()},
 * since it's our own class. Plain vanilla minecarts are not our class, so this mixin adds the same
 * tracking to the vanilla {@link Minecart} type instead, skipping over Express Minecarts (which
 * also happen to be a {@link Minecart} subclass) to avoid tracking the same ride twice.
 */
@Mixin(Minecart.class)
public abstract class MinecartRideMixin {
    @Unique
    private double expresscarts$rideDistance = 0.0;
    @Unique
    private Vec3 expresscarts$lastRidePos = null;
    @Unique
    private boolean expresscarts$longRideMilestoneAwarded = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void expresscarts$trackVanillaRideDistance(CallbackInfo ci) {
        Minecart self = (Minecart) (Object) this;

        if (self instanceof ExpressCart) {
            return;
        }

        if (self.getFirstPassenger() instanceof ServerPlayer player) {
            if (this.expresscarts$lastRidePos == null) {
                this.expresscarts$lastRidePos = self.position();
                return;
            }

            this.expresscarts$rideDistance += self.position().distanceTo(this.expresscarts$lastRidePos);
            this.expresscarts$lastRidePos = self.position();

            if (!this.expresscarts$longRideMilestoneAwarded && this.expresscarts$rideDistance >= RideMilestones.MILESTONE_DISTANCE) {
                this.expresscarts$longRideMilestoneAwarded = true;
                RideMilestones.awardVanillaLongRide(player, this.expresscarts$rideDistance);
            }
        } else {
            this.expresscarts$rideDistance = 0.0;
            this.expresscarts$lastRidePos = null;
            this.expresscarts$longRideMilestoneAwarded = false;
        }
    }
}

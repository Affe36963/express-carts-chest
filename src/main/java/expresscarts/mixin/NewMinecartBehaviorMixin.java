package expresscarts.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import expresscarts.ExpressCartsConfig;
import expresscarts.ExpressChestMinecartEntity;
import expresscarts.ExpressMinecartEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;


@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {
    protected NewMinecartBehaviorMixin(AbstractMinecart minecart) {
        super(minecart);
    }

    @Inject(method = "getMaxSpeed", at = @At("HEAD"), cancellable = true)
    // If we're using this controller for an Express Carts entity, use the configured speed for that type, including any multipliers
    private void onGetMaxSpeed(CallbackInfoReturnable<Double> cir) {
        if (this.minecart instanceof ExpressMinecartEntity) {
            cir.setReturnValue(computeMaxSpeed(
                    ExpressCartsConfig.maxMinecartSpeed,
                    ExpressCartsConfig.waterSpeedMultiplier,
                    ExpressCartsConfig.blockSpeedMultipliers));
        } else if (this.minecart instanceof ExpressChestMinecartEntity) {
            cir.setReturnValue(computeMaxSpeed(
                    ExpressCartsConfig.chestMaxMinecartSpeed,
                    ExpressCartsConfig.chestWaterSpeedMultiplier,
                    ExpressCartsConfig.chestBlockSpeedMultipliers));
        }
    }

    private double computeMaxSpeed(double baseSpeed, double waterSpeedMultiplierConfig, Map<Block, Double> blockSpeedMultipliers) {
        var waterSpeedMultiplier = this.minecart.isInWater() ? waterSpeedMultiplierConfig : 1.0;
        var blockMultiplier = 1.0;

        if (!blockSpeedMultipliers.isEmpty()) {
            if (this.minecart.isOnRails()) {
                var railPos = this.minecart.getCurrentBlockPosOrRailBelow();
                // check the block below the rail
                var blockPos = railPos.below();
                var blockState = this.level().getBlockState(blockPos);

                blockMultiplier = blockSpeedMultipliers.getOrDefault(blockState.getBlock(), 1.0);
            }
        }

        return (baseSpeed * blockMultiplier * waterSpeedMultiplier) / 20.0;
    }

    @WrapOperation(method = "calculateHaltTrackSpeed(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/phys/Vec3;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
    // Modify the scale applied to the cart's velocity when passing over an unpowered powered rail
    private Vec3 unpoweredPoweredRailVelocityScale(Vec3 instance, double scale, Operation<Vec3> original) {
        boolean useFastSlowdown =
                (this.minecart instanceof ExpressMinecartEntity && ExpressCartsConfig.fastUnpoweredSlowdown)
                || (this.minecart instanceof ExpressChestMinecartEntity && ExpressCartsConfig.chestFastUnpoweredSlowdown);

        if (useFastSlowdown) {
            return original.call(instance, 0.4); // seems reasonable in testing. stops similar or faster than vanilla carts (0.5) even at high max speeds (>128) with a long runway
        } else {
            return original.call(instance, scale);
        }
    }

}

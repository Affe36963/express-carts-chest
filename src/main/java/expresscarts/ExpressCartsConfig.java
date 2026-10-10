package expresscarts;

import dev.xpple.betterconfig.api.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class ExpressCartsConfig {
    // ------------------------------------------------------------------
    // Express Minecart settings.
    // ------------------------------------------------------------------

    @Config(comment = "maxMinecartSpeedComment")
    public static double maxMinecartSpeed = 16;
    public static Component maxMinecartSpeedComment() {
        return Component.translatable("expresscarts.config.section.minecart")
                .append("\n")
                .append(Component.translatable("expresscarts.config.comment.maxMinecartSpeed"));
    }

    @Config(comment = "waterSpeedMultiplierComment")
    public static double waterSpeedMultiplier = 0.5;
    public static Component waterSpeedMultiplierComment() {
        return Component.translatable("expresscarts.config.comment.waterSpeedMultiplier");
    }

    @Config(comment = "brakingEnabledComment")
    public static boolean brakingEnabled = true;
    public static Component brakingEnabledComment() {
        return Component.translatable("expresscarts.config.comment.brakingEnabled");
    }

    @Config(comment = "brakeSlowdownComment")
    public static double brakeSlowdown = 0.8;
    public static Component brakeSlowdownComment() {
        return Component.translatable("expresscarts.config.comment.brakeSlowdown");
    }

    @Config(comment = "blockSpeedMultipliersComment")
    public static Map<Block, Double> blockSpeedMultipliers = new HashMap<>();
    public static Component blockSpeedMultipliersComment() {
        return Component.translatable("expresscarts.config.comment.blockSpeedMultipliers");
    }

    @Config(comment = "fastUnpoweredSlowdownComment")
    public static boolean fastUnpoweredSlowdown = false;
    public static Component fastUnpoweredSlowdownComment() {
        return Component.translatable("expresscarts.config.comment.fastUnpoweredSlowdown");
    }

    @Config(comment = "loadChunksComment")
    public static boolean loadChunks = false;
    public static Component loadChunksComment() {
        return Component.translatable("expresscarts.config.comment.loadChunks");
    }

    @Config(comment = "fallDamageMultiplierComment")
    public static float fallDamageMultiplier = 1f;
    public static Component fallDamageMultiplierComment() {
        return Component.translatable("expresscarts.config.comment.fallDamageMultiplier");
    }

    // ------------------------------------------------------------------
    // Express Chest Minecart settings. These mirror the settings above,
    // but apply only to Express Chest Minecarts and can be tuned independently.
    // (There is no chest-specific fall damage setting: chest minecarts use
    // vanilla fall damage.)
    // ------------------------------------------------------------------

    @Config(comment = "chestMaxMinecartSpeedComment")
    public static double chestMaxMinecartSpeed = 16;
    public static Component chestMaxMinecartSpeedComment() {
        return Component.literal("")
                .append("\n")
                .append(Component.translatable("expresscarts.config.section.chestMinecart"))
                .append("\n")
                .append(Component.translatable("expresscarts.config.comment.chestMaxMinecartSpeed"));
    }

    @Config(comment = "chestWaterSpeedMultiplierComment")
    public static double chestWaterSpeedMultiplier = 0.5;
    public static Component chestWaterSpeedMultiplierComment() {
        return Component.translatable("expresscarts.config.comment.chestWaterSpeedMultiplier");
    }

    @Config(comment = "chestBrakingEnabledComment")
    public static boolean chestBrakingEnabled = true;
    public static Component chestBrakingEnabledComment() {
        return Component.translatable("expresscarts.config.comment.chestBrakingEnabled");
    }

    @Config(comment = "chestBrakeSlowdownComment")
    public static double chestBrakeSlowdown = 0.8;
    public static Component chestBrakeSlowdownComment() {
        return Component.translatable("expresscarts.config.comment.chestBrakeSlowdown");
    }

    @Config(comment = "chestBlockSpeedMultipliersComment")
    public static Map<Block, Double> chestBlockSpeedMultipliers = new HashMap<>();
    public static Component chestBlockSpeedMultipliersComment() {
        return Component.translatable("expresscarts.config.comment.chestBlockSpeedMultipliers");
    }

    @Config(comment = "chestFastUnpoweredSlowdownComment")
    public static boolean chestFastUnpoweredSlowdown = false;
    public static Component chestFastUnpoweredSlowdownComment() {
        return Component.translatable("expresscarts.config.comment.chestFastUnpoweredSlowdown");
    }

    @Config(comment = "chestLoadChunksComment")
    public static boolean chestLoadChunks = false;
    public static Component chestLoadChunksComment() {
        return Component.translatable("expresscarts.config.comment.chestLoadChunks");
    }
}

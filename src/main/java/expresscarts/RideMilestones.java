package expresscarts;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Awards the "first ride" / "first opened" / "long ride" advancements, and sends the accompanying
 * chat message for the two distance-based milestones. The "time saved" figures are computed live
 * from {@link ExpressCartsConfig}, so they reflect this server's actual configured speeds rather
 * than a value baked in at build time.
 * <p>
 * The estimate is necessarily a simplification: it assumes both cart types spend the whole ride at
 * their configured maximum speed on ordinary rails (ignoring acceleration, water, braking, and any
 * per-block speed multipliers), and compares against vanilla's normal 8 blocks/second minecart cap.
 */
public final class RideMilestones {
    public static final double MILESTONE_DISTANCE = 10000.0;

    // Vanilla's own maximum minecart speed on ordinary powered rails, in blocks per second.
    private static final double VANILLA_MINECART_SPEED = 8.0;

    private RideMilestones() {
    }

    public static void awardFirstRide(ServerPlayer player) {
        award(player, "express_minecart");
    }

    public static void awardFirstChestOpened(ServerPlayer player) {
        award(player, "express_chest_minecart");
    }

    public static void awardVanillaLongRide(ServerPlayer player, double distance) {
        if (award(player, "vanilla_long_ride")) {
            double timeSaved = estimateTimeSaved(distance);
            player.sendSystemMessage(Component.translatable(
                    "expresscarts.milestone.vanilla_ride.message",
                    Math.round(distance),
                    formatSeconds(timeSaved),
                    roundedMinutes(timeSaved)));
        }
    }

    public static void awardExpressLongRide(ServerPlayer player, double distance) {
        if (award(player, "express_long_ride")) {
            double timeSaved = estimateTimeSaved(distance);
            player.sendSystemMessage(Component.translatable(
                    "expresscarts.milestone.express_ride.message",
                    Math.round(distance),
                    formatSeconds(timeSaved),
                    roundedMinutes(timeSaved)));
        }
    }

    private static double estimateTimeSaved(double distance) {
        double vanillaTime = distance / VANILLA_MINECART_SPEED;
        double expressTime = distance / ExpressCartsConfig.maxMinecartSpeed;
        return Math.max(0.0, vanillaTime - expressTime);
    }

    private static String formatSeconds(double seconds) {
        return String.format("%.1f", seconds);
    }

    private static long roundedMinutes(double seconds) {
        return Math.round(seconds / 60.0);
    }

    private static boolean award(ServerPlayer player, String advancementPath) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        var server = serverLevel.getServer();
        if (server == null) {
            return false;
        }

        AdvancementHolder holder = server.getAdvancements().get(Identifier.fromNamespaceAndPath(ExpressCarts.MOD_ID, advancementPath));
        if (holder == null) {
            return false;
        }

        return player.getAdvancements().award(holder, "milestone");
    }
}

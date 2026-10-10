package expresscarts;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import expresscarts.mixin.AbstractMinecartAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.EntityType;
//? >= 26.2
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
//? if < 26.1 {
/*import xyz.nucleoid.packettweaker.PacketContext;
*///?} else
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import java.util.List;
import java.util.Optional;

/**
 * The chest-carrying counterpart to {@link ExpressMinecartEntity}. Behaves identically in shape,
 * but every tunable (speed, braking, chunk loading, fall damage, etc.) is read from its own set of
 * {@link ExpressCartsConfig} fields (the {@code chest*} ones) so it can be tuned independently of
 * the passenger Express Minecart.
 */
public class ExpressChestMinecartEntity extends MinecartChest implements PolymerEntity, ExpressCart {
    private ChunkPos ticketChunkPos;
    private Long ticketTimer = 0L;

    public ExpressChestMinecartEntity(EntityType<? extends MinecartChest> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        //? if >=26.2 {
        return EntityTypes.CHEST_MINECART;
        //?} else
        //return EntityType.CHEST_MINECART;
    }

    @Override
    protected @NotNull Item getDropItem() {
        return ModItems.EXPRESS_CHEST_MINECART;
    }

    @Override
    // Vanilla's MinecartChest hardcodes its container screen title to the vanilla
    // "Minecart with Chest" translation regardless of the entity's actual registered type,
    // so without this override the container title either shows that generic vanilla text
    // or (on some clients) an untranslated raw key. Overriding it here keeps it consistent everywhere.
    public Component getDisplayName() {
        return Component.translatable("entity.expresscarts.chest_minecart");
    }

    @Override
    public @NotNull ItemStack getPickResult() {
        return new ItemStack(ModItems.EXPRESS_CHEST_MINECART);
    }

    @Override
    // Award the "Cargo Class" advancement the first time a player successfully opens this cart's
    // storage. super.interact(...) is what actually opens the container screen (when the click
    // is accepted, i.e. not sneaking, not holding an item that does something else first).
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 hitPos) {
        InteractionResult result = super.interact(player, hand, hitPos);
        if (result.consumesAction() && player instanceof ServerPlayer serverPlayer) {
            RideMilestones.awardFirstChestOpened(serverPlayer);
        }
        return result;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        data.removeIf(element ->
            element.id() == AbstractMinecartAccessor.getCustomBlockOffset().id() || element.id() == AbstractMinecartAccessor.getCustomDisplayBlock().id()
        );

        // The client sees this as a vanilla chest minecart, so we have to explicitly send our default values.
        data.add(SynchedEntityData.DataValue.create(AbstractMinecartAccessor.getCustomDisplayBlock(), Optional.of(this.getDefaultDisplayBlockState())));
        data.add(SynchedEntityData.DataValue.create(AbstractMinecartAccessor.getCustomBlockOffset(), this.getDefaultDisplayOffset()));
    }

    @Override
    public @NonNull BlockState getDefaultDisplayBlockState() {
        // Used so clients can see that this is different to a vanilla chest minecart:
        // a trapped chest has the same shape as a normal chest, but with a reddish/copper latch,
        // which reads as "the red express variant" while still clearly being a chest.
        return Blocks.TRAPPED_CHEST.defaultBlockState();
    }

    @Override
    public int getDefaultDisplayOffset() {
        // A full chest block sits flush with the floor of the minecart, unlike the thin
        // carpet used by ExpressMinecartEntity (which needs a small offset to not clip through).
        return 0;
    }

    @Override
    // apply the brakes when the controlling player holds back while riding the cart
    protected @NotNull Vec3 applyNaturalSlowdown(Vec3 velocity) {
        Vec3 vel = super.applyNaturalSlowdown(velocity);

        if (ExpressCartsConfig.chestBrakingEnabled && this.getFirstPassenger() instanceof ServerPlayer player && player.getLastClientInput().backward()) {
            // stop completely if going slowly. otherwise, slow down (but not as quickly as an unpowered powered rail)
            return vel.length() < 0.03 ? Vec3.ZERO : vel.scale(ExpressCartsConfig.chestBrakeSlowdown);
        }

        return vel;
    }

    @Override
    public void tick() {
        super.tick();
        if (ExpressCartsConfig.chestLoadChunks) {
            if (this.level() instanceof ServerLevel serverLevel) {
                ChunkPos chunkPos = this.chunkPosition();
                if (--this.ticketTimer <= 0L || chunkPos != this.ticketChunkPos) {
                    this.ticketChunkPos = chunkPos;
                    this.ticketTimer = placeTicket(serverLevel, chunkPos);
                }
            }
        }
    }

    private long placeTicket(ServerLevel serverLevel, ChunkPos chunkPos) {
        serverLevel.getChunkSource().addTicketWithRadius(TicketType.ENDER_PEARL, chunkPos, 2);
        return TicketType.ENDER_PEARL.timeout();
    }
}

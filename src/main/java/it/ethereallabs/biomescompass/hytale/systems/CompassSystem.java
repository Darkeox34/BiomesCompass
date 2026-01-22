package it.ethereallabs.biomescompass.hytale.systems;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.ethereallabs.biomescompass.BiomesCompass;
import it.ethereallabs.biomescompass.core.CompassUtils;
import it.ethereallabs.biomescompass.hud.EmptyHUD;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CompassSystem extends EntityTickingSystem<EntityStore> {

    private final Query<EntityStore> query =
            Query.and(Player.getComponentType());

    private boolean isCompass(@Nullable ItemStack stack) {
        return stack != null && (
                stack.getItemId().startsWith(BiomesCompass.COMPASS_ITEM_ID)
                        || stack.getItemId().startsWith("*" + BiomesCompass.COMPASS_ITEM_ID)
        );
    }

    private boolean hasCompass(@Nullable Inventory inventory) {
        if (inventory == null) return false;

        return isCompass(inventory.getActiveHotbarItem())
                || isCompass(inventory.getUtilityItem());
    }

    @Override
    public void tick(
            float dt,
            int index,
            @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer
    ) {
        var holder = EntityUtils.toHolder(index, archetypeChunk);

        PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (playerRef == null) return;

        var uuid = playerRef.getUuid();
        var currentData = BiomesCompass.getCompassManager().getPlayersTracking().get(uuid);
        if (currentData == null) return;

        Player player = holder.getComponent(Player.getComponentType());
        boolean isHolding = player != null && hasCompass(player.getInventory());

        if (currentData.usingCompass() != isHolding) {

            if (isHolding) {
                CompassUtils.setHUD(playerRef, player, "BiomesCompass_HUD", currentData.hud());
            } else if (player != null) {
                CompassUtils.hideHUD(playerRef, player, "BiomesCompass_HUD", new EmptyHUD(playerRef));
            }

            BiomesCompass.getCompassManager().getPlayersTracking().put(
                    uuid,
                    currentData.copy(isHolding)
            );
        }

        if (isHolding) {
            var data = BiomesCompass.getCompassManager().getPlayersTracking().get(playerRef.getUuid());
            if (data != null && data.hud() != null) {
                data.hud().tickHudTracking();
            }

            /*
            int frame = getCompassFrame(
                    playerRef.transform.position.x,
                    playerRef.transform.position.z,
                    data.hud.targetX,
                    data.hud.targetZ,
                    playerRef.transform.rotation.yaw
            );

            ItemStack updated = player.inventory.activeHotbarItem
                    .withState("dir_" + frame);

            short slot = (short) player.inventory.activeHotbarSlot;
            player.inventory.hotbar.setItemStackForSlot(slot, updated);
            */
        }
    }

    @Override
    public boolean isParallel(int archetypeChunkSize, int taskCount) {
        return maybeUseParallel(archetypeChunkSize, taskCount);
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return query;
    }
}

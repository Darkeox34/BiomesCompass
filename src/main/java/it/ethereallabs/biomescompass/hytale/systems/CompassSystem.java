package it.ethereallabs.biomescompass.hytale.systems;

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
import it.ethereallabs.biomescompass.hud.HUD;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CompassSystem extends EntityTickingSystem<EntityStore> {

    private final Query<EntityStore> query = Query.and(Player.getComponentType());

    private static final float SMOOTH_FACTOR = 0.2f;

    private boolean isCompass(@Nullable ItemStack stack) {
        return stack != null && (
                stack.getItemId().startsWith(BiomesCompass.COMPASS_ITEM_ID)
                        || stack.getItemId().startsWith("*" + BiomesCompass.COMPASS_ITEM_ID)
        );
    }

    private boolean hasCompass(@Nullable Inventory inventory) {
        if (inventory == null) return false;
        return isCompass(inventory.getActiveHotbarItem()) || isCompass(inventory.getUtilityItem());
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
        if (player == null) return;

        boolean isHolding = hasCompass(player.getInventory());
        if (currentData.usingCompass() != isHolding) {
            if (isHolding) {
                CompassUtils.setHUD(playerRef, player, "BiomesCompass_HUD", currentData.hud());
            } else {
                CompassUtils.hideHUD(playerRef, player, "BiomesCompass_HUD", new EmptyHUD(playerRef));
            }
            BiomesCompass.getCompassManager().getPlayersTracking().put(uuid, currentData.copy(isHolding));
        }

        if (isHolding && currentData.hud() != null) {
            updateNeedleRotation(playerRef, currentData.hud());
            currentData.hud().tickHudTracking();
        }
    }

    private void updateNeedleRotation(PlayerRef player, HUD hud) {
        var pos = player.getTransform().getPosition();

        double dx = hud.getTargetX() - pos.getX();
        double dz = hud.getTargetZ() - pos.getZ();

        double targetAngle = Math.atan2(dx, dz);

        float playerYaw = player.getHeadRotation().getYaw();

        float relativeAngle = (float) (playerYaw - targetAngle);

        float lastAngle = hud.getLastAngle();
        float diff = relativeAngle - lastAngle;

        while (diff < -Math.PI) diff += (float) (2 * Math.PI);
        while (diff > Math.PI) diff -= (float) (2 * Math.PI);

        float smoothedAngle = lastAngle + (diff * SMOOTH_FACTOR);
        hud.setLastAngle(smoothedAngle);

        float normalized = (float) (smoothedAngle / (2 * Math.PI)) + 0.5f;

        int frameIndex = Math.round(normalized * 64) % 64;
        if (frameIndex < 0) frameIndex += 64;

        hud.updateNeedleTexture(frameIndex);
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
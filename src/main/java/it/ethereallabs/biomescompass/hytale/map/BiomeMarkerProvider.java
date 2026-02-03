package it.ethereallabs.biomescompass.hytale.map;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
import com.hypixel.hytale.server.core.util.PositionUtil;
import it.ethereallabs.biomescompass.BiomesCompass;
import it.ethereallabs.biomescompass.core.models.PlayerData;
import it.ethereallabs.biomescompass.hud.HUD;
import javax.annotation.Nonnull;

public class BiomeMarkerProvider implements WorldMapManager.MarkerProvider {

    private static final String BIOME_ICON = "biome.png";

    @Override
    public void update(
            @Nonnull World world,
            @Nonnull MapMarkerTracker tracker,
            int chunkViewRadius,
            int playerChunkX,
            int playerChunkZ
    ) {
        Player player = tracker.getPlayer();
        if (player == null) return;

        PlayerData data = BiomesCompass.getCompassManager().getPlayersTracking().get(player.getUuid());

        if (data != null && data.usingCompass() && data.hud() != null) {
            HUD hud = data.hud();
            Vector3d targetPos = new Vector3d(hud.getTargetX(), 180, hud.getTargetZ());

            com.hypixel.hytale.math.vector.Transform biomeTransform = new com.hypixel.hytale.math.vector.Transform();
            biomeTransform.setPosition(targetPos);

            String markerId = "BiomeTrack-" + player.getUuid();
            String displayName = "Biome: " + hud.getBiomeName();

            tracker.trySendMarker(
                    chunkViewRadius,
                    playerChunkX,
                    playerChunkZ,
                    targetPos,
                    0f,
                    markerId,
                    displayName,
                    hud,
                    (id, name, context) -> new MapMarker(
                            id,
                            name,
                            BIOME_ICON,
                            PositionUtil.toTransformPacket(biomeTransform),
                            null
                    )
            );
        }
    }
}
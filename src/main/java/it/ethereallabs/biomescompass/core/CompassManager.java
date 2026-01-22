package it.ethereallabs.biomescompass.core;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import it.ethereallabs.biomescompass.core.models.PlayerData;
import it.ethereallabs.biomescompass.hud.EmptyHUD;
import it.ethereallabs.biomescompass.hud.HUD;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CompassManager {

    private final Map<UUID, PlayerData> playersTracking = new HashMap<>();

    public void startTracking(PlayerRef playerRef, String biomeName, int x, int z, int dist) {
        HUD hud = new HUD(playerRef, biomeName, x, z, dist);

        playersTracking.put(playerRef.getUuid(), new PlayerData(false, hud));
    }

    public void stopTracking(PlayerRef playerRef) {
        playersTracking.remove(playerRef.getUuid());

        if (playerRef.getReference() != null) {
            var player = playerRef.getReference().getStore().getComponent(playerRef.getReference(), Player.getComponentType());
            if (player != null) {
                CompassUtils.hideHUD(playerRef, player, "BiomesCompass_HUD", new EmptyHUD(playerRef));
            }
        }
    }

    public Map<UUID, PlayerData> getPlayersTracking() {
        return playersTracking;
    }
}
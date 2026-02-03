package it.ethereallabs.biomescompass.utils;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.worldgen.zone.ZonePatternGenerator;
import it.ethereallabs.biomescompass.BiomesCompass;
import it.ethereallabs.biomescompass.hud.EmptyHUD;

import java.awt.*;

import static java.lang.Math.*;

public class CompassUtils {
    /**
     * @return int[] {x, z, distance} or null if not found
     */
    public static int[] findBiomeSpiral(
            int startX,
            int startZ,
            int step,
            int maxRadius,
            String target,
            ZonePatternGenerator zoneGen,
            int worldSeed
    ) {
        int x = 0;
        int z = 0;
        int dx = 0;
        int dz = -1;

        int maxSteps = (int) pow((double) maxRadius / step, 2.0);

        for (int i = 0; i < maxSteps; i++) {
            int checkX = startX + x * step;
            int checkZ = startZ + z * step;

            var zoneResult = zoneGen.generate(worldSeed, (double) checkX, (double) checkZ);

            var biome = zoneResult.getZone().biomePatternGenerator()
                    .generateBiomeAt(zoneResult, worldSeed, checkX, checkZ);

            if (biome.getName().equalsIgnoreCase(target)) {
                double diffX = (double) checkX - startX;
                double diffZ = (double) checkZ - startZ;
                int dist = (int) Math.sqrt(diffX * diffX + diffZ * diffZ);

                return new int[]{checkX, checkZ, dist};
            }

            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                int temp = dx;
                dx = -dz;
                dz = temp;
            }

            x += dx;
            z += dz;
        }

        return null;
    }

    public static void performSearch(PlayerRef playerRef, Player player, String targetName) {
        var playerRefObj = Universe.get().getPlayer(playerRef.getUuid());
        World world = player.getWorld();
        if (world == null || playerRefObj == null) return;

        player.sendMessage(
                Message.raw("Searching for " + targetName + "...")
                        .color(Color.YELLOW)
        );

        try {
            var zoneGen = BiomeUtils.getZoneGen(world);
            int seed = (int) world.getWorldConfig().getSeed();
            var transform = playerRefObj.getTransform();

            int[] result = CompassUtils.findBiomeSpiral(
                    (int) transform.getPosition().x,
                    (int) transform.getPosition().z,
                    128,
                    10_000,
                    targetName,
                    zoneGen,
                    seed
            );

            if (result != null) {
                int x = result[0];
                int z = result[1];
                int dist = result[2];

                player.sendMessage(
                        Message.raw(
                                "Found! " + targetName +
                                        " at " + dist +
                                        " meters (X: " + x + ", Z: " + z + ")"
                        ).color(Color.GREEN)
                );

                BiomesCompass.getCompassManager().startTracking(playerRef, targetName, x, z, dist);

            } else {
                player.sendMessage(
                        Message.raw("Biome " + targetName + " not found within 10000 blocks range.")
                                .color(Color.RED)
                );
            }

        } catch (Exception e) {
            player.sendMessage(
                    Message.raw("Error searching biome: " + e.getMessage())
                            .color(Color.RED)
            );
        }
    }

    public static void setHUD(PlayerRef playerRef, Player player, String hudName, CustomUIHud hud) {
        if (BiomesCompass.getInstance().isMultipleHUD()) {
            BiomesCompass.getInstance().getLogger().atInfo().log("Showing HUD to " + player.getDisplayName());
            MultipleHUD.getInstance().setCustomHud(player, playerRef, hudName, hud);
        } else
            player.getHudManager().setCustomHud(playerRef, hud);
    }

    public static void hideHUD(PlayerRef playerRef, Player player, String hudName, EmptyHUD hud) {
        if (BiomesCompass.getInstance().isMultipleHUD()) {
            BiomesCompass.getInstance().getLogger().atInfo().log("Hiding HUD to " + player.getDisplayName());
            MultipleHUD.getInstance().hideCustomHud(player, playerRef, hudName);
        } else
            player.getHudManager().setCustomHud(playerRef, hud);
    }
}

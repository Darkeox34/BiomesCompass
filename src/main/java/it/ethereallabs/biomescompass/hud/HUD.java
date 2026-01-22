package it.ethereallabs.biomescompass.hud;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import it.ethereallabs.biomescompass.BiomesCompass;

import javax.annotation.Nonnull;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class HUD extends CustomUIHud {

    private static final String UI_FILE = "HUD/HUD.ui";

    private final String biomeName;
    private final int targetX;
    private final int targetZ;
    private int distance;

    public HUD(PlayerRef playerRef, String biomeName, int targetX, int targetZ, int distance) {
        super(playerRef);
        this.biomeName = biomeName;
        this.targetX = targetX;
        this.targetZ = targetZ;
        this.distance = distance;
    }

    @Override
    public void build(@Nonnull UICommandBuilder builder) {
        builder.append(UI_FILE);

        builder.set("#TrackingLabel.Text", "Tracking: " + biomeName);
        builder.set("#CoordsLabel.Text", "Target: " + targetX + ", " + targetZ);
        builder.set("#DistLabel.Text", "Distance: " + distance + "m");
    }

    public void tickHudTracking() {
        Player player = null;
        if (getPlayerRef().getReference() != null) {
            player = getPlayerRef().getReference().getStore().getComponent(getPlayerRef().getReference(), Player.getComponentType());
        }

        if (player != null) {
            double dx = getPlayerRef().getTransform().getPosition().getX() - targetX;
            double dz = getPlayerRef().getTransform().getPosition().getZ() - targetZ;

            this.distance = (int) sqrt(pow(dx, 2.0) + pow(dz, 2.0));
            if(BiomesCompass.getCompassManager().getPlayersTracking().containsKey(getPlayerRef().getUuid())) {
                this.show();
            }
        }
    }

    public int getDistance() {
        return distance;
    }
}

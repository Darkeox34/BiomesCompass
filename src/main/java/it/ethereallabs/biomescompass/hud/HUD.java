package it.ethereallabs.biomescompass.hud;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.PatchStyle;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import it.ethereallabs.biomescompass.BiomesCompass;

import javax.annotation.Nonnull;

public class HUD extends CustomUIHud {

    private static final String UI_FILE = "HUD/HUD.ui";

    private final String biomeName;
    private final int targetX;
    private final int targetZ;
    private int distance;

    private float lastAngle = 0.0f;

    private int lastNeedleIndex = -1;

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
            double dx = targetX - getPlayerRef().getTransform().getPosition().getX();
            double dz = targetZ - getPlayerRef().getTransform().getPosition().getZ();
            this.distance = (int) Math.sqrt(dx * dx + dz * dz);

            if(BiomesCompass.getCompassManager().getPlayersTracking().containsKey(getPlayerRef().getUuid())) {
                UICommandBuilder update = new UICommandBuilder();
                update.set("#DistLabel.Text", "Distance: " + distance + "m");
                this.update(false, update);
            }
        }
    }

    public void updateNeedleTexture(int frameIndex) {
        if (this.lastNeedleIndex == frameIndex) return;

        this.lastNeedleIndex = frameIndex;
        String texturePath = String.format("HUD/needles/needle_%02d.png", frameIndex);

        UICommandBuilder update = new UICommandBuilder();

        PatchStyle style = new PatchStyle();

        style.setTexturePath(Value.of(texturePath));
        style.setBorder(Value.of(0));

        update.setObject("#Needle.Background", style);

        this.update(false, update);
    }

    public float getLastAngle() {
        return lastAngle;
    }

    public void setLastAngle(float lastAngle) {
        this.lastAngle = lastAngle;
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetZ() {
        return targetZ;
    }
}
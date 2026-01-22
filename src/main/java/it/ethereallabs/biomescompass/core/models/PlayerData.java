package it.ethereallabs.biomescompass.core.models;

import it.ethereallabs.biomescompass.hud.HUD;

import javax.annotation.Nonnull;

public record PlayerData(boolean usingCompass, HUD hud) {

    public PlayerData copy(boolean usingCompass) {
        return new PlayerData(usingCompass, this.hud);
    }

    public PlayerData copy(HUD hud) {
        return new PlayerData(this.usingCompass, hud);
    }

    @Nonnull
    @Override
    public String toString() {
        return "PlayerData{" +
                "usingCompass=" + usingCompass +
                ", hud=" + hud +
                '}';
    }
}

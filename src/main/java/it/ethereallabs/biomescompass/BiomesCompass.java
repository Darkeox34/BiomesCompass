package it.ethereallabs.biomescompass;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.events.AllWorldsLoadedEvent;
import it.ethereallabs.biomescompass.core.CompassManager;
import it.ethereallabs.biomescompass.hytale.interactions.CustomInteraction;
import it.ethereallabs.biomescompass.hytale.map.BiomeMarkerProvider;
import it.ethereallabs.biomescompass.hytale.systems.CompassSystem;
import it.ethereallabs.biomescompass.utils.BiomeUtils;

import java.util.*;

public class BiomesCompass extends JavaPlugin {

    public static final Map<World, List<String>> biomesPerWorld = new HashMap<>();

    public static final String COMPASS_ITEM_ID = "Biomes_Compass";

    private static BiomesCompass instance;
    private static CompassManager compassManager;

    private static Boolean multipleHUD = false;

    public BiomesCompass(JavaPluginInit init) {
        super(init);
        instance = this;
        compassManager = new CompassManager();
    }

    @Override
    public void setup() {
        getEventRegistry().register(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
        getEventRegistry().register(AllWorldsLoadedEvent.class, this::onAllWorldsLoaded);

        getEntityStoreRegistry().registerSystem(new CompassSystem());

        BiomeMarkerProvider markerProvider = new BiomeMarkerProvider();
        getEventRegistry().registerGlobal(AddWorldEvent.class, event -> {
            World world = event.getWorld();
            world.getWorldMapManager().getMarkerProviders().put("biomeCompassMarker", markerProvider);
        });

        getCodecRegistry(Interaction.CODEC).register(
                "compass_open_ui_interaction",
                CustomInteraction.class,
                CustomInteraction.CODEC
        );
    }

    @Override
    public void start() {
        PluginBase mHUD = PluginManager.get().getPlugin(new PluginIdentifier("Buuz135","MultipleHUD"));
        if(mHUD != null && MultipleHUD.getInstance() != null){
            getLogger().atInfo().log("MultipleHUD hooked!");
            multipleHUD = true;
        }
        else{
            getLogger().atWarning().log("MultipleHUD not found, using Hytale's default HUD system.");
        }
    }

    @Override
    public void shutdown() {
    }

    public Map<World, List<String>> getBiomesPerWorld() {
        return biomesPerWorld;
    }

    public void onAllWorldsLoaded(AllWorldsLoadedEvent event) {
        BiomeUtils.loadBiomes();
    }

    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        var player = event.getPlayerRef();
        compassManager.getPlayersTracking().remove(player.getUuid());
    }

    public boolean isMultipleHUD() {
        return multipleHUD;
    }

    public static BiomesCompass getInstance() {
        return instance;
    }

    public static CompassManager getCompassManager() {
        return compassManager;
    }
}

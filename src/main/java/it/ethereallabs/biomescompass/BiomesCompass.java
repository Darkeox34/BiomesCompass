package it.ethereallabs.biomescompass;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.AllWorldsLoadedEvent;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
import com.hypixel.hytale.server.worldgen.biome.Biome;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.zone.Zone;
import com.hypixel.hytale.server.worldgen.zone.ZonePatternGenerator;
import it.ethereallabs.biomescompass.core.CompassManager;
import it.ethereallabs.biomescompass.hytale.interactions.CustomInteraction;
import it.ethereallabs.biomescompass.hytale.systems.CompassSystem;

import java.util.*;

public class BiomesCompass extends JavaPlugin {

    private final Map<World, List<String>> biomesPerWorld = new HashMap<>();

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

    public void loadBiomes(){
        getLogger().atInfo().log("Loading worlds biomes");

        Collection<World> worlds = Universe.get().getWorlds().values();

        if (worlds.isEmpty()) {
            getLogger().atSevere().log("No worlds to load");
            return;
        }

        for (World world : worlds) {
            getLogger().atInfo().log("Loading " + world.getName());
        }

        for (World world : worlds) {
            if (world == null) continue;

            ZonePatternGenerator zoneGen;
            try {
                zoneGen = getZoneGen(world);
            } catch (WorldGenLoadException e) {
                e.printStackTrace();
                return;
            }
            Set<String> biomeSet = new HashSet<>();
            for (Zone zone : zoneGen.getZones()) {
                for (Biome biome : zone.biomePatternGenerator().getBiomes()) {
                    biomeSet.add(biome.getName());
                }
            }

            getLogger().atInfo().log(biomeSet.size() + " biomes found for world " + world.getName());

            List<String> sortedBiomes = new ArrayList<>(biomeSet);
            Collections.sort(sortedBiomes);

            biomesPerWorld.put(world, sortedBiomes);
        }
    }

    public ZonePatternGenerator getZoneGen(World world) throws WorldGenLoadException {
        int worldSeedInt = (int) world.getWorldConfig().getSeed();

        Object gen;
        try {
            gen = world.getWorldConfig().getWorldGenProvider().getGenerator();
        } catch (WorldGenLoadException e) {
            throw new WorldGenLoadException("World gen load exception", e);
        }

        if (!(gen instanceof ChunkGenerator chunkGen)) {
            throw new WorldGenLoadException("World gen load exception: generator is not a ChunkGenerator");
        }

        return chunkGen.getZonePatternGenerator(worldSeedInt);
    }

    public Map<World, List<String>> getBiomesPerWorld() {
        return biomesPerWorld;
    }

    public void onAllWorldsLoaded(AllWorldsLoadedEvent event) {
        loadBiomes();
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

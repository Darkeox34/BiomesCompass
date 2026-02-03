package it.ethereallabs.biomescompass.utils;

import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
import com.hypixel.hytale.server.worldgen.biome.Biome;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.zone.Zone;
import com.hypixel.hytale.server.worldgen.zone.ZonePatternGenerator;
import it.ethereallabs.biomescompass.BiomesCompass;

import java.util.*;

import static com.hypixel.hytale.logger.HytaleLogger.getLogger;

public class BiomeUtils {
    public static void loadBiomes(){
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

            BiomesCompass.biomesPerWorld.put(world, sortedBiomes);
        }
    }

    public static ZonePatternGenerator getZoneGen(World world) throws WorldGenLoadException {
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
}

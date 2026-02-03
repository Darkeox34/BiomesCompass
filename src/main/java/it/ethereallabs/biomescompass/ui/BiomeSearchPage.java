package it.ethereallabs.biomescompass.ui;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.ethereallabs.biomescompass.BiomesCompass;
import it.ethereallabs.biomescompass.utils.CompassUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class BiomeSearchPage extends InteractiveCustomUIPage<EditorEventData> {

    private static final String UI_FILE = "Pages/BiomeSearch.ui";
    private static final String ENTRY_FILE = "Pages/BiomeEntry.ui";

    private final Player player;

    private String searchQuery = "";
    private String selectedBiome = null;

    private final List<String> allBiomes;
    private List<String> filteredBiomes;

    public BiomeSearchPage(Player player, PlayerRef playerRef, CustomPageLifetime lifetime) {
        super(playerRef, lifetime, EditorEventData.CODEC);
        this.player = player;

        World world = player.getWorld();
        List<String> globalBiomes;

        if (world != null) {
            globalBiomes = BiomesCompass.getInstance()
                    .getBiomesPerWorld()
                    .getOrDefault(world, Collections.emptyList());
        } else {
            globalBiomes = Collections.emptyList();
        }

        this.allBiomes = List.copyOf(globalBiomes);
        this.filteredBiomes = this.allBiomes;
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder commands,
            @Nonnull UIEventBuilder events,
            @Nonnull Store<EntityStore> store
    ) {
        commands.append(UI_FILE);

        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                "#SearchInput",
                createData("FILTER").append("@Value", "#SearchInput.Value")
        );

        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#SearchButton",
                createData("SEARCH")
        );

        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#CloseButton",
                createData("CLOSE")
        );

        refreshList(commands, events);
    }

    private EventData createData(String action) {
        return createData(action, "");
    }

    private EventData createData(String action, String targetName) {
        return new EventData()
                .append("Action", action)
                .append("TargetName", targetName);
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull EditorEventData data
    ) {
        UICommandBuilder update = new UICommandBuilder();
        UIEventBuilder events = new UIEventBuilder();

        switch (data.action) {
            case "FILTER" -> {
                searchQuery = data.value.toLowerCase();
                filteredBiomes = allBiomes.stream()
                        .filter(b -> b.toLowerCase().contains(searchQuery))
                        .toList();
                refreshList(update, events);
            }

            case "SELECT" -> {
                selectedBiome = data.targetName;
                refreshList(update, events);
            }

            case "SEARCH" -> {
                if (selectedBiome != null) {
                    close();
                    CompassUtils.performSearch(playerRef, player, selectedBiome);
                }
            }

            case "CLOSE" -> close();
        }

        sendUpdate(update, events, false);
    }

    private void refreshList(UICommandBuilder builder, UIEventBuilder events) {
        builder.clear("#ListContainer");

        if (selectedBiome != null) {
            builder.set("#SelectedBiomeLabel.Text", "Selected: " + selectedBiome);
        } else {
            builder.set("#SelectedBiomeLabel.Text", "Selected: None");
        }

        for (int i = 0; i < filteredBiomes.size(); i++) {
            String biomeName = filteredBiomes.get(i);

            builder.append("#ListContainer", ENTRY_FILE);
            String rowSelector = "#ListContainer[" + i + "]";

            builder.set(rowSelector + ".Text", biomeName);
            builder.set(rowSelector + ".Disabled", biomeName.equals(selectedBiome));

            events.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    rowSelector,
                    createData("SELECT", biomeName)
            );
        }

        if (selectedBiome == null) {
            builder.set("#SearchButton.Disabled", true);
            builder.set("#SearchButton.Text", "Select Biome");
        } else {
            builder.set("#SearchButton.Disabled", false);
            builder.set("#SearchButton.Text", "SEARCH");
        }
    }
}

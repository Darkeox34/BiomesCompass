package it.ethereallabs.biomescompass.hytale.interactions;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.ethereallabs.biomescompass.BiomesCompass;
import it.ethereallabs.biomescompass.core.CompassUtils;
import it.ethereallabs.biomescompass.hud.EmptyHUD;
import it.ethereallabs.biomescompass.ui.BiomeSearchPage;

import javax.annotation.Nonnull;
import java.awt.Color;

public class CustomInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<CustomInteraction> CODEC =
            BuilderCodec.builder(
                    CustomInteraction.class,
                    CustomInteraction::new,
                    SimpleInstantInteraction.CODEC
            ).build();

    @Override
    public void firstRun(
            @Nonnull InteractionType interactionType,
            @Nonnull InteractionContext interactionContext,
            @Nonnull CooldownHandler cooldownHandler
    ) {
        var commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) {
            interactionContext.getState().state = InteractionState.Failed;
            BiomesCompass.getInstance().getLogger().atInfo().log("CommandBuffer is null");
            return;
        }

        Store<EntityStore> store = commandBuffer.getExternalData().getStore();
        if (store == null) return;

        Ref<EntityStore> ref = interactionContext.getEntity();

        MovementStatesComponent moveComp = store.getComponent(ref, MovementStatesComponent.getComponentType());
        if (moveComp == null) return;

        var states = moveComp.getMovementStates();

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        if (states.crouching) {
            //BiomesCompass.getCompassManager().getPlayersTracking().remove(playerRef.getUuid());
            //CompassUtils.hideHUD(playerRef, player, "BiomesCompass_EmptyHUD", new EmptyHUD(playerRef));
            BiomesCompass.getCompassManager().stopTracking(playerRef);
            player.sendMessage(Message.raw("Research canceled!").color(Color.GREEN));
            return;
        }

        BiomeSearchPage page = new BiomeSearchPage(player, playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction);
        player.getPageManager().openCustomPage(ref, store, page);
    }
}

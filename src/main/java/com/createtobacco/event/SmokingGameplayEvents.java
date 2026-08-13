package com.createtobacco.event;

import com.createtobacco.CreateTobacco;
import com.createtobacco.attachment.SmokingData;
import com.createtobacco.registry.ModAttachments;
import com.createtobacco.registry.ModEffects;
import com.createtobacco.smoking.CoughingSystem;
import com.createtobacco.smoking.WithdrawalSystem;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateTobacco.MOD_ID)
public final class SmokingGameplayEvents {
    public static final float NICOTINE_RUSH_DAMAGE_MULTIPLIER = 0.95F;

    private SmokingGameplayEvents() {
    }

    @SubscribeEvent
    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            tickSmokingData(serverPlayer);
        }
    }

    @SubscribeEvent
    private static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer
                && serverPlayer.hasEffect(ModEffects.NICOTINE_RUSH)) {
            event.setAmount(event.getAmount() * NICOTINE_RUSH_DAMAGE_MULTIPLIER);
        }
    }

    private static void tickSmokingData(ServerPlayer player) {
        SmokingData data = player.getData(ModAttachments.SMOKING_DATA);
        data.tickActive();
        CoughingSystem.tick(player, data);
        WithdrawalSystem.tick(player, data);
    }
}

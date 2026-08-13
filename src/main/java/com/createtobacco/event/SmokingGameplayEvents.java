package com.createtobacco.event;

import com.createtobacco.CreateTobacco;
import com.createtobacco.attachment.SmokingData;
import com.createtobacco.attachment.WithdrawalTier;
import com.createtobacco.registry.ModAttachments;
import com.createtobacco.registry.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

        WithdrawalTier tier = WithdrawalTier.fromDependence(data.dependence());
        if (tier == WithdrawalTier.NONE) {
            data.clearWithdrawalSchedule();
            player.removeEffect(ModEffects.WITHDRAWAL);
            return;
        }

        if (data.activeTicksSinceSatisfied() < tier.safeIntervalTicks()) {
            data.clearWithdrawalSchedule();
            player.removeEffect(ModEffects.WITHDRAWAL);
            return;
        }

        if (!data.withdrawalEpisodeIsScheduled()) {
            data.scheduleWithdrawalEpisode(tier.randomEpisodeIntervalTicks(player.getRandom()));
            return;
        }

        if (data.withdrawalEpisodeIsDue() && !player.hasEffect(ModEffects.WITHDRAWAL)) {
            startWithdrawalEpisode(player, data, tier);
        }
    }

    private static void startWithdrawalEpisode(ServerPlayer player, SmokingData data, WithdrawalTier tier) {
        data.beginWithdrawalEpisode();
        data.scheduleWithdrawalEpisode(tier.randomEpisodeIntervalTicks(player.getRandom()));
        player.addEffect(new MobEffectInstance(
                ModEffects.WITHDRAWAL,
                tier.episodeDurationTicks(),
                tier.amplifier(),
                false,
                true,
                true
        ));

        if (player.getRandom().nextFloat() < tier.nauseaChance()) {
            int nauseaDuration = player.getRandom().nextIntBetweenInclusive(3 * 20, 5 * 20);
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, nauseaDuration, 0, false, true, true));
        }
    }
}

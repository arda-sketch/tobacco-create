package com.createtobacco.smoking;

import com.createtobacco.attachment.SmokingData;
import com.createtobacco.attachment.WithdrawalTier;
import com.createtobacco.registry.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class WithdrawalSystem {
    private WithdrawalSystem() {
    }

    public static void tick(ServerPlayer player, SmokingData data) {
        WithdrawalTier tier = WithdrawalTier.fromDependence(data.dependence());
        if (tier == WithdrawalTier.NONE) {
            clear(player, data);
            return;
        }

        if (data.activeTicksSinceSatisfied() < tier.safeIntervalTicks()) {
            clear(player, data);
            return;
        }

        if (!data.withdrawalEpisodeIsScheduled()) {
            data.scheduleWithdrawalEpisode(tier.randomEpisodeIntervalTicks(player.getRandom()));
            return;
        }

        if (data.withdrawalEpisodeIsDue() && !player.hasEffect(ModEffects.WITHDRAWAL)) {
            trigger(player, data, tier, true);
        }
    }

    public static void trigger(ServerPlayer player, SmokingData data, WithdrawalTier tier, boolean rollNausea) {
        data.beginWithdrawalEpisode();
        data.scheduleWithdrawalEpisode(tier.randomEpisodeIntervalTicks(player.getRandom()));
        player.removeEffect(ModEffects.WITHDRAWAL);
        player.addEffect(new MobEffectInstance(
                ModEffects.WITHDRAWAL,
                tier.episodeDurationTicks(),
                tier.amplifier(),
                false,
                true,
                true
        ));

        if (rollNausea && player.getRandom().nextFloat() < tier.nauseaChance()) {
            int nauseaDuration = player.getRandom().nextIntBetweenInclusive(3 * 20, 5 * 20);
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, nauseaDuration, 0, false, true, true));
        }
    }

    public static void clear(ServerPlayer player, SmokingData data) {
        data.clearWithdrawalSchedule();
        player.removeEffect(ModEffects.WITHDRAWAL);
    }
}

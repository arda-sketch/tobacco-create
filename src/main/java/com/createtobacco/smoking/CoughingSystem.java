package com.createtobacco.smoking;

import com.createtobacco.attachment.SmokingData;
import com.createtobacco.attachment.WithdrawalTier;
import com.createtobacco.registry.ModEffects;
import com.createtobacco.registry.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class CoughingSystem {
    private CoughingSystem() {
    }

    public static void tick(ServerPlayer player, SmokingData data) {
        WithdrawalTier tier = WithdrawalTier.fromDependence(data.dependence());
        if (tier == WithdrawalTier.NONE) {
            data.clearCoughSchedule();
            return;
        }

        if (!data.coughCheckIsScheduled()) {
            data.scheduleCoughCheck(SmokingBalance.randomCoughInterval(player.getRandom()));
            return;
        }

        if (!data.coughCheckIsDue()) {
            return;
        }

        data.scheduleCoughCheck(SmokingBalance.randomCoughInterval(player.getRandom()));
        if (player.getRandom().nextFloat() < SmokingBalance.withdrawal(tier).coughChance()) {
            trigger(player);
        }
    }

    public static void trigger(ServerPlayer player) {
        if (player.isUsingItem()) {
            player.stopUsingItem();
        }

        ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PANDA_SNEEZE, SoundSource.PLAYERS, 0.65F, 0.75F);
        level.sendParticles(ModParticles.TOBACCO_SMOKE.get(),
                player.getX(), player.getEyeY() - 0.15D, player.getZ(),
                5, 0.18D, 0.08D, 0.18D, 0.012D);
        player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                SmokingBalance.COUGH_SLOWNESS_TICKS,
                0,
                false,
                true,
                true
        ));
    }
}

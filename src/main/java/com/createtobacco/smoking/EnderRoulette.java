package com.createtobacco.smoking;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public final class EnderRoulette {
    public enum Outcome {
        TELEPORT,
        SLOW_FALLING,
        JUMP_BOOST,
        INVISIBILITY,
        LEVITATION
    }

    private EnderRoulette() {
    }

    public static Outcome randomOutcome(ServerPlayer player) {
        int roll = player.getRandom().nextInt(SmokingBalance.KEND_TOTAL_WEIGHT);
        int threshold = SmokingBalance.KEND_TELEPORT_WEIGHT;
        if (roll < threshold) return Outcome.TELEPORT;
        threshold += SmokingBalance.KEND_SLOW_FALLING_WEIGHT;
        if (roll < threshold) return Outcome.SLOW_FALLING;
        threshold += SmokingBalance.KEND_JUMP_BOOST_WEIGHT;
        if (roll < threshold) return Outcome.JUMP_BOOST;
        threshold += SmokingBalance.KEND_INVISIBILITY_WEIGHT;
        if (roll < threshold) return Outcome.INVISIBILITY;
        return Outcome.LEVITATION;
    }

    public static void trigger(ServerPlayer player, Outcome outcome) {
        switch (outcome) {
            case TELEPORT -> safeChorusTeleport(player);
            case SLOW_FALLING -> addEffect(player, MobEffects.SLOW_FALLING,
                    SmokingBalance.KEND_SLOW_FALLING_TICKS, SmokingBalance.KEND_STANDARD_EFFECT_AMPLIFIER);
            case JUMP_BOOST -> addEffect(player, MobEffects.JUMP,
                    SmokingBalance.KEND_JUMP_BOOST_TICKS, SmokingBalance.KEND_JUMP_BOOST_AMPLIFIER);
            case INVISIBILITY -> addEffect(player, MobEffects.INVISIBILITY,
                    SmokingBalance.KEND_INVISIBILITY_TICKS, SmokingBalance.KEND_STANDARD_EFFECT_AMPLIFIER);
            case LEVITATION -> addEffect(player, MobEffects.LEVITATION,
                    SmokingBalance.KEND_LEVITATION_TICKS, SmokingBalance.KEND_STANDARD_EFFECT_AMPLIFIER);
        }
    }

    public static boolean safeChorusTeleport(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        for (int attempt = 0; attempt < SmokingBalance.KEND_TELEPORT_ATTEMPTS; attempt++) {
            double x = player.getX() + (player.getRandom().nextDouble() * 2.0D - 1.0D)
                    * SmokingBalance.KEND_TELEPORT_RADIUS;
            double y = Mth.clamp(
                    player.getY() + player.getRandom().nextInt(SmokingBalance.KEND_TELEPORT_VERTICAL_RANGE * 2)
                            - SmokingBalance.KEND_TELEPORT_VERTICAL_RANGE,
                    level.getMinBuildHeight(),
                    level.getMinBuildHeight() + level.getLogicalHeight() - 1
            );
            double z = player.getZ() + (player.getRandom().nextDouble() * 2.0D - 1.0D)
                    * SmokingBalance.KEND_TELEPORT_RADIUS;
            Vec3 origin = player.position();

            var event = EventHooks.onChorusFruitTeleport(player, x, y, z);
            if (event.isCanceled()) {
                return false;
            }

            if (player.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
                level.gameEvent(GameEvent.TELEPORT, origin, GameEvent.Context.of(player));
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS);
                player.resetFallDistance();
                player.resetCurrentImpulseContext();
                return true;
            }
        }
        return false;
    }

    private static void addEffect(ServerPlayer player, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect,
                                  int duration, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, true, true));
    }
}

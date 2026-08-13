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
        int roll = player.getRandom().nextInt(100);
        if (roll < 40) return Outcome.TELEPORT;
        if (roll < 60) return Outcome.SLOW_FALLING;
        if (roll < 75) return Outcome.JUMP_BOOST;
        if (roll < 90) return Outcome.INVISIBILITY;
        return Outcome.LEVITATION;
    }

    public static void trigger(ServerPlayer player, Outcome outcome) {
        switch (outcome) {
            case TELEPORT -> safeChorusTeleport(player);
            case SLOW_FALLING -> addEffect(player, MobEffects.SLOW_FALLING, 20 * 20, 0);
            case JUMP_BOOST -> addEffect(player, MobEffects.JUMP, 20 * 20, 1);
            case INVISIBILITY -> addEffect(player, MobEffects.INVISIBILITY, 10 * 20, 0);
            case LEVITATION -> addEffect(player, MobEffects.LEVITATION, 2 * 20, 0);
        }
    }

    public static boolean safeChorusTeleport(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        for (int attempt = 0; attempt < 16; attempt++) {
            double x = player.getX() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;
            double y = Mth.clamp(
                    player.getY() + player.getRandom().nextInt(16) - 8,
                    level.getMinBuildHeight(),
                    level.getMinBuildHeight() + level.getLogicalHeight() - 1
            );
            double z = player.getZ() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;
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

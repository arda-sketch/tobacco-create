package com.createtobacco.smoking;

import com.createtobacco.attachment.SmokingData;
import com.createtobacco.registry.ModAttachments;
import com.createtobacco.registry.ModEffects;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.phys.Vec3;

public final class SmokingEffects {
    private SmokingEffects() {
    }

    public static void onSuccessfulPuff(ServerPlayer player, SmokingProduct product) {
        switch (product) {
            case MARLBORE_RED -> rollAndTrigger(player, product, SmokingBalance.MARLBORE_PROC_CHANCE);
            case WINSTONE_BLUE -> rollAndTrigger(player, product, SmokingBalance.WINSTONE_PROC_CHANCE);
            case CREPERFIELD -> rollAndTrigger(player, product, SmokingBalance.CREPERFIELD_PROC_CHANCE);
            case CHUNKMAN -> rollAndTrigger(player, product, SmokingBalance.CHUNKMAN_PROC_CHANCE);
            case KEND -> rollAndTrigger(player, product, SmokingBalance.KEND_PROC_CHANCE);
            case PIGLIAMENT -> rollAndTrigger(player, product, SmokingBalance.PIGLIAMENT_PROC_CHANCE);
            case BEDROMORKANAL -> rollAndTrigger(player, product, SmokingBalance.BEDROMORKANAL_PROC_CHANCE);
            case STONEO_Y_GLOWLIETA -> rollAndTrigger(player, product, SmokingBalance.STONEO_PROC_CHANCE);
            default -> {
            }
        }
    }

    public static void triggerPuffEffect(ServerPlayer player, SmokingProduct product) {
        switch (product) {
            case MARLBORE_RED -> addEffect(player, MobEffects.DIG_SPEED, SmokingBalance.MARLBORE_HASTE_TICKS, 0);
            case WINSTONE_BLUE -> player.giveExperiencePoints(player.getRandom().nextIntBetweenInclusive(1, 2));
            case CREPERFIELD -> microblast(player);
            case CHUNKMAN -> feed(player);
            case KEND -> EnderRoulette.trigger(player, EnderRoulette.randomOutcome(player));
            case PIGLIAMENT -> addEffect(player, MobEffects.DAMAGE_RESISTANCE, SmokingBalance.PIGLIAMENT_RESISTANCE_TICKS, 0);
            case ROTHMINES -> addEffect(player, MobEffects.DIG_SPEED, SmokingBalance.ROTHMINES_HASTE_TICKS, 0);
            case BEDROMORKANAL -> heal(player);
            case STONEO_Y_GLOWLIETA -> {
                addEffect(player, MobEffects.NIGHT_VISION, SmokingBalance.STONEO_NIGHT_VISION_TICKS, 0);
                addEffect(player, MobEffects.GLOWING, SmokingBalance.STONEO_GLOWING_TICKS, 0);
            }
            default -> {
            }
        }
    }

    public static void onFullyConsumed(ServerPlayer player, SmokingProduct product) {
        SmokingProfile profile = SmokingBalance.profile(product);
        player.getData(ModAttachments.SMOKING_DATA).markSatisfied();
        player.removeEffect(ModEffects.WITHDRAWAL);
        player.removeEffect(ModEffects.NICOTINE_RUSH);
        player.addEffect(new MobEffectInstance(
                ModEffects.NICOTINE_RUSH,
                profile.nicotineRushDurationTicks(),
                profile.nicotineRushAmplifier(),
                false,
                true,
                true
        ));
        player.causeFoodExhaustion(profile.completionExhaustion());

        if (product == SmokingProduct.ROTHMINES) {
            addEffect(player, MobEffects.DIG_SPEED, SmokingBalance.ROTHMINES_HASTE_TICKS, 0);
        }
    }

    private static void rollAndTrigger(ServerPlayer player, SmokingProduct product, float chance) {
        if (player.getRandom().nextFloat() < chance) {
            triggerPuffEffect(player, product);
        }
    }

    private static void microblast(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.65F, 1.25F);
        level.sendParticles(ParticleTypes.EXPLOSION,
                player.getX(), player.getY() + 1.0D, player.getZ(),
                2, 0.25D, 0.25D, 0.25D, 0.0D);
        level.sendParticles(ParticleTypes.POOF,
                player.getX(), player.getY() + 0.8D, player.getZ(),
                18, 0.7D, 0.45D, 0.7D, 0.04D);

        List<LivingEntity> nearby = level.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(SmokingBalance.MICROBLAST_RADIUS),
                entity -> entity != player && entity.isAlive()
                        && entity.distanceToSqr(player) <= SmokingBalance.MICROBLAST_RADIUS * SmokingBalance.MICROBLAST_RADIUS
        );
        for (LivingEntity entity : nearby) {
            Vec3 away = entity.position().subtract(player.position());
            Vec3 horizontal = new Vec3(away.x, 0.0D, away.z);
            if (horizontal.lengthSqr() < 1.0E-4D) {
                horizontal = new Vec3(0.0D, 0.0D, 1.0D);
            }
            Vec3 push = horizontal.normalize().scale(SmokingBalance.MICROBLAST_KNOCKBACK);
            entity.push(push.x, 0.12D, push.z);
        }

        addEffect(player, MobEffects.MOVEMENT_SPEED, SmokingBalance.MICROBLAST_BUFF_TICKS, 1);
        addEffect(player, MobEffects.DIG_SPEED, SmokingBalance.MICROBLAST_BUFF_TICKS, 1);
    }

    private static void feed(ServerPlayer player) {
        FoodData food = player.getFoodData();
        int newFoodLevel = Math.min(20, food.getFoodLevel() + SmokingBalance.CHUNKMAN_FOOD);
        food.setFoodLevel(newFoodLevel);
        food.setSaturation(Math.min(newFoodLevel, food.getSaturationLevel() + SmokingBalance.CHUNKMAN_SATURATION));
    }

    private static void heal(ServerPlayer player) {
        if (player.getHealth() >= player.getMaxHealth()) {
            return;
        }
        player.heal(SmokingBalance.BEDROMORKANAL_HEALING);
        player.serverLevel().sendParticles(ParticleTypes.HEART,
                player.getX(), player.getY() + 1.0D, player.getZ(),
                4, 0.35D, 0.4D, 0.35D, 0.02D);
    }

    private static void addEffect(ServerPlayer player, Holder<MobEffect> effect, int duration, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, true, true));
    }
}

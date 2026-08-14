package com.createtobacco.smoking;

import com.createtobacco.attachment.WithdrawalTier;
import net.minecraft.util.RandomSource;

/**
 * Single source of truth for provisional V1 smoking, product, cough, and
 * Withdrawal balance. Visual-only particle offsets intentionally stay with
 * their renderer/item code.
 */
public final class SmokingBalance {
    public static final int TICKS_PER_SECOND = 20;
    public static final float MIN_DEPENDENCE = 0.0F;
    public static final float MAX_DEPENDENCE = 100.0F;
    public static final float MILD_DEPENDENCE = 20.0F;
    public static final float MODERATE_DEPENDENCE = 40.0F;
    public static final float HIGH_DEPENDENCE = 60.0F;
    public static final float SEVERE_DEPENDENCE = 80.0F;
    public static final long DEPENDENCE_DECAY_INTERVAL_TICKS = hours(1);
    public static final float DEPENDENCE_DECAY_PER_INTERVAL = 2.5F;
    public static final int PUFF_USE_DURATION_TICKS = 24;
    // A lit cigarette/cigar smoulders even when the player is not puffing it.
    // Natural burn consumes one puff at each interval but grants no benefits.
    public static final int CIGARETTE_NATURAL_BURN_INTERVAL_TICKS = seconds(60);
    public static final int CIGAR_NATURAL_BURN_INTERVAL_TICKS = seconds(90);

    public static final float STANDARD_RUSH_DAMAGE_REDUCTION = 0.05F;
    public static final float PREMIUM_RUSH_DAMAGE_REDUCTION = 0.05F;

    public static final SmokingProfile MARLBORE_RED = cigarette(0.9F, minutes(5), 0, 0.05D, 0.6F, 0.25F);
    public static final SmokingProfile WINSTONE_BLUE = cigarette(0.9F, minutes(5), 0, 0.05D, 0.6F, 0.35F);
    public static final SmokingProfile CREPERFIELD = cigarette(0.9F, minutes(5), 0, 0.05D, 0.6F, 0.10F);
    public static final SmokingProfile CRAFTMEL = cigarette(0.7F, minutes(4), 0, 0.05D, 0.5F, 0.0F);
    public static final SmokingProfile CHUNKMAN = cigarette(0.9F, minutes(5), 0, 0.05D, 0.6F, 0.25F);
    public static final SmokingProfile KEND = cigarette(0.9F, minutes(5), 0, 0.05D, 0.6F, 0.18F);
    public static final SmokingProfile PIGLIAMENT = cigarette(0.9F, minutes(5), 0, 0.05D, 0.6F, 0.15F);
    public static final SmokingProfile ROTHMINES = cigarette(0.9F, minutes(5), 0, 0.05D, 0.6F, 0.0F);
    public static final SmokingProfile BEDROMORKANAL = cigarette(0.9F, minutes(5), 0, 0.05D, 0.6F, 0.15F);
    public static final SmokingProfile MINECRISTO_NO_1 = cigar(1.6F, minutes(8), 1, 0.10D, 1.0F, 0.0F);
    public static final SmokingProfile STONEO_Y_GLOWLIETA = cigar(1.4F, minutes(7), 0, 0.05D, 0.9F, 0.30F);

    public static final int MARLBORE_HASTE_TICKS = seconds(20);
    public static final int MARLBORE_HASTE_AMPLIFIER = 0;
    public static final int WINSTONE_MIN_RAW_XP = 1;
    public static final int WINSTONE_MAX_RAW_XP = 2;
    public static final int MICROBLAST_BUFF_TICKS = seconds(10);
    public static final int MICROBLAST_BUFF_AMPLIFIER = 1;
    public static final double MICROBLAST_RADIUS = 5.0D;
    public static final double MICROBLAST_KNOCKBACK = 0.60D;
    public static final double MICROBLAST_VERTICAL_KNOCKBACK = 0.16D;
    public static final int CHUNKMAN_FOOD = 1;
    public static final float CHUNKMAN_SATURATION = 0.5F;
    public static final int PIGLIAMENT_RESISTANCE_TICKS = seconds(10);
    public static final int PIGLIAMENT_RESISTANCE_AMPLIFIER = 0;
    public static final int ROTHMINES_HASTE_TICKS = seconds(60);
    public static final int ROTHMINES_HASTE_AMPLIFIER = 0;
    public static final float BEDROMORKANAL_HEALING = 2.0F;
    public static final int STONEO_NIGHT_VISION_TICKS = seconds(45);
    public static final int STONEO_GLOWING_TICKS = seconds(20);
    public static final int STONEO_EFFECT_AMPLIFIER = 0;

    public static final int KEND_TELEPORT_WEIGHT = 40;
    public static final int KEND_SLOW_FALLING_WEIGHT = 20;
    public static final int KEND_JUMP_BOOST_WEIGHT = 15;
    public static final int KEND_INVISIBILITY_WEIGHT = 15;
    public static final int KEND_LEVITATION_WEIGHT = 10;
    public static final int KEND_TOTAL_WEIGHT = KEND_TELEPORT_WEIGHT + KEND_SLOW_FALLING_WEIGHT
            + KEND_JUMP_BOOST_WEIGHT + KEND_INVISIBILITY_WEIGHT + KEND_LEVITATION_WEIGHT;
    public static final int KEND_SLOW_FALLING_TICKS = seconds(20);
    public static final int KEND_JUMP_BOOST_TICKS = seconds(20);
    public static final int KEND_INVISIBILITY_TICKS = seconds(10);
    public static final int KEND_LEVITATION_TICKS = seconds(2);
    public static final int KEND_STANDARD_EFFECT_AMPLIFIER = 0;
    public static final int KEND_JUMP_BOOST_AMPLIFIER = 1;
    public static final int KEND_TELEPORT_ATTEMPTS = 16;
    public static final double KEND_TELEPORT_RADIUS = 8.0D;
    public static final int KEND_TELEPORT_VERTICAL_RANGE = 8;

    public static final int COUGH_CHECK_MIN_TICKS = minutes(2);
    public static final int COUGH_CHECK_MAX_TICKS = minutes(4);
    public static final int COUGH_SLOWNESS_TICKS = seconds(5);
    public static final int WITHDRAWAL_NAUSEA_MIN_TICKS = seconds(3);
    public static final int WITHDRAWAL_NAUSEA_MAX_TICKS = seconds(5);

    public static final WithdrawalProfile NO_WITHDRAWAL =
            new WithdrawalProfile(0L, 0, 0, 0, 0, 0.0F, 0.0D, 0.0D, 0.0F);
    public static final WithdrawalProfile MILD_WITHDRAWAL =
            new WithdrawalProfile(minutes(40), minutes(6), minutes(10), seconds(30), 1,
                    0.05F, -0.03D, -0.05D, 0.05F);
    public static final WithdrawalProfile MODERATE_WITHDRAWAL =
            new WithdrawalProfile(minutes(30), minutes(4), minutes(7), seconds(40), 2,
                    0.15F, -0.05D, -0.08D, 0.10F);
    public static final WithdrawalProfile HIGH_WITHDRAWAL =
            new WithdrawalProfile(minutes(20), minutes(3), minutes(5), seconds(50), 3,
                    0.25F, -0.07D, -0.12D, 0.18F);
    public static final WithdrawalProfile SEVERE_WITHDRAWAL =
            new WithdrawalProfile(minutes(15), minutes(2), minutes(4), seconds(60), 4,
                    0.35F, -0.10D, -0.15D, 0.28F);

    private SmokingBalance() {
    }

    public static SmokingProfile profile(SmokingProduct product) {
        return switch (product) {
            case MARLBORE_RED -> MARLBORE_RED;
            case WINSTONE_BLUE -> WINSTONE_BLUE;
            case CREPERFIELD -> CREPERFIELD;
            case CRAFTMEL -> CRAFTMEL;
            case CHUNKMAN -> CHUNKMAN;
            case KEND -> KEND;
            case PIGLIAMENT -> PIGLIAMENT;
            case ROTHMINES -> ROTHMINES;
            case BEDROMORKANAL -> BEDROMORKANAL;
            case MINECRISTO_NO_1 -> MINECRISTO_NO_1;
            case STONEO_Y_GLOWLIETA -> STONEO_Y_GLOWLIETA;
        };
    }

    public static WithdrawalProfile withdrawal(WithdrawalTier tier) {
        return switch (tier) {
            case NONE -> NO_WITHDRAWAL;
            case MILD -> MILD_WITHDRAWAL;
            case MODERATE -> MODERATE_WITHDRAWAL;
            case HIGH -> HIGH_WITHDRAWAL;
            case SEVERE -> SEVERE_WITHDRAWAL;
        };
    }

    public static float nicotineRushDamageReduction(int amplifier) {
        return amplifier > 0
                ? MINECRISTO_NO_1.nicotineRushDamageReduction()
                : MARLBORE_RED.nicotineRushDamageReduction();
    }

    public static double nicotineRushMovementModifier(int amplifier) {
        return amplifier > 0
                ? MINECRISTO_NO_1.nicotineRushMovementModifier()
                : MARLBORE_RED.nicotineRushMovementModifier();
    }

    public static long randomCoughInterval(RandomSource random) {
        return random.nextIntBetweenInclusive(COUGH_CHECK_MIN_TICKS, COUGH_CHECK_MAX_TICKS);
    }

    public static int seconds(int value) {
        return value * TICKS_PER_SECOND;
    }

    public static int minutes(int value) {
        return seconds(value * 60);
    }

    public static long hours(int value) {
        return (long) minutes(value * 60);
    }

    private static SmokingProfile cigarette(
            float dependence,
            int rushDuration,
            int rushAmplifier,
            double movementModifier,
            float exhaustion,
            float procChance
    ) {
        return new SmokingProfile(5, dependence, rushDuration, rushAmplifier, movementModifier,
                rushAmplifier > 0 ? PREMIUM_RUSH_DAMAGE_REDUCTION : STANDARD_RUSH_DAMAGE_REDUCTION,
                exhaustion, procChance);
    }

    private static SmokingProfile cigar(
            float dependence,
            int rushDuration,
            int rushAmplifier,
            double movementModifier,
            float exhaustion,
            float procChance
    ) {
        return new SmokingProfile(8, dependence, rushDuration, rushAmplifier, movementModifier,
                rushAmplifier > 0 ? PREMIUM_RUSH_DAMAGE_REDUCTION : STANDARD_RUSH_DAMAGE_REDUCTION,
                exhaustion, procChance);
    }

    public record WithdrawalProfile(
            long safeIntervalTicks,
            int minimumEpisodeIntervalTicks,
            int maximumEpisodeIntervalTicks,
            int episodeDurationTicks,
            int reliefPuffsRequired,
            float nauseaChance,
            double movementPenalty,
            double miningPenalty,
            float coughChance
    ) {
        public long randomEpisodeIntervalTicks(RandomSource random) {
            return random.nextIntBetweenInclusive(minimumEpisodeIntervalTicks, maximumEpisodeIntervalTicks);
        }
    }
}

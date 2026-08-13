package com.createtobacco.smoking;

import com.createtobacco.attachment.WithdrawalTier;
import net.minecraft.util.RandomSource;

public final class SmokingBalance {
    public static final SmokingProfile STANDARD_CIGARETTE = new SmokingProfile(5, 0.9F, 6_000, 0, 0.6F);
    public static final SmokingProfile LIGHT_CIGARETTE = new SmokingProfile(5, 0.7F, 4_800, 0, 0.5F);
    public static final SmokingProfile STANDARD_CIGAR = new SmokingProfile(8, 1.4F, 8_400, 0, 0.9F);
    public static final SmokingProfile PREMIUM_CIGAR = new SmokingProfile(8, 1.6F, 9_600, 1, 1.0F);

    public static final int COUGH_CHECK_MIN_TICKS = minutes(2);
    public static final int COUGH_CHECK_MAX_TICKS = minutes(4);
    public static final int COUGH_SLOWNESS_TICKS = seconds(5);

    private SmokingBalance() {
    }

    public static SmokingProfile profile(SmokingProduct product) {
        return switch (product) {
            case CRAFTMEL -> LIGHT_CIGARETTE;
            case MINECRISTO_NO_1 -> PREMIUM_CIGAR;
            case STONEO_Y_GLOWLIETA, COBBLIBA_MADURO -> STANDARD_CIGAR;
            default -> STANDARD_CIGARETTE;
        };
    }

    public static float coughChance(WithdrawalTier tier) {
        return switch (tier) {
            case MILD -> 0.05F;
            case MODERATE -> 0.10F;
            case HIGH -> 0.18F;
            case SEVERE -> 0.28F;
            case NONE -> 0.0F;
        };
    }

    public static long randomCoughInterval(RandomSource random) {
        return random.nextIntBetweenInclusive(COUGH_CHECK_MIN_TICKS, COUGH_CHECK_MAX_TICKS);
    }

    public static int seconds(int seconds) {
        return seconds * 20;
    }

    public static int minutes(int minutes) {
        return seconds(minutes * 60);
    }
}

package com.createtobacco.attachment;

import net.minecraft.util.RandomSource;

public enum WithdrawalTier {
    NONE(0L, 0, 0, 0.0F),
    MILD(minutes(30), minutes(6), minutes(10), 0.05F),
    MODERATE(minutes(20), minutes(4), minutes(7), 0.15F),
    HIGH(minutes(15), minutes(3), minutes(5), 0.25F),
    SEVERE(minutes(10), minutes(2), minutes(4), 0.35F);

    private final long safeIntervalTicks;
    private final int minimumEpisodeIntervalTicks;
    private final int maximumEpisodeIntervalTicks;
    private final float nauseaChance;

    WithdrawalTier(long safeIntervalTicks, int minimumEpisodeIntervalTicks,
                   int maximumEpisodeIntervalTicks, float nauseaChance) {
        this.safeIntervalTicks = safeIntervalTicks;
        this.minimumEpisodeIntervalTicks = minimumEpisodeIntervalTicks;
        this.maximumEpisodeIntervalTicks = maximumEpisodeIntervalTicks;
        this.nauseaChance = nauseaChance;
    }

    public static WithdrawalTier fromDependence(float dependence) {
        if (dependence < 20.0F) return NONE;
        if (dependence < 40.0F) return MILD;
        if (dependence < 60.0F) return MODERATE;
        if (dependence < 80.0F) return HIGH;
        return SEVERE;
    }

    public static WithdrawalTier fromAmplifier(int amplifier) {
        return switch (amplifier) {
            case 0 -> MILD;
            case 1 -> MODERATE;
            case 2 -> HIGH;
            default -> SEVERE;
        };
    }

    public int amplifier() {
        return Math.max(0, ordinal() - 1);
    }

    public long safeIntervalTicks() {
        return safeIntervalTicks;
    }

    public long randomEpisodeIntervalTicks(RandomSource random) {
        return random.nextIntBetweenInclusive(minimumEpisodeIntervalTicks, maximumEpisodeIntervalTicks);
    }

    public int episodeDurationTicks() {
        return switch (this) {
            case MILD -> seconds(30);
            case MODERATE -> seconds(40);
            case HIGH -> seconds(50);
            case SEVERE -> seconds(60);
            case NONE -> 0;
        };
    }

    public int reliefPuffsRequired() {
        return switch (this) {
            case MILD -> 2;
            case MODERATE -> 3;
            case HIGH -> 4;
            case SEVERE -> 5;
            case NONE -> 0;
        };
    }

    public float nauseaChance() {
        return nauseaChance;
    }

    private static int seconds(int seconds) {
        return seconds * 20;
    }

    private static int minutes(int minutes) {
        return seconds(minutes * 60);
    }
}

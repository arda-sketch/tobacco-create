package com.createtobacco.attachment;

import com.createtobacco.smoking.SmokingBalance;
import net.minecraft.util.RandomSource;

public enum WithdrawalTier {
    NONE,
    MILD,
    MODERATE,
    HIGH,
    SEVERE;

    public static WithdrawalTier fromDependence(float dependence) {
        if (dependence < SmokingBalance.MILD_DEPENDENCE) return NONE;
        if (dependence < SmokingBalance.MODERATE_DEPENDENCE) return MILD;
        if (dependence < SmokingBalance.HIGH_DEPENDENCE) return MODERATE;
        if (dependence < SmokingBalance.SEVERE_DEPENDENCE) return HIGH;
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
        return SmokingBalance.withdrawal(this).safeIntervalTicks();
    }

    public long randomEpisodeIntervalTicks(RandomSource random) {
        return SmokingBalance.withdrawal(this).randomEpisodeIntervalTicks(random);
    }

    public int episodeDurationTicks() {
        return SmokingBalance.withdrawal(this).episodeDurationTicks();
    }

    public float nauseaChance() {
        return SmokingBalance.withdrawal(this).nauseaChance();
    }
}

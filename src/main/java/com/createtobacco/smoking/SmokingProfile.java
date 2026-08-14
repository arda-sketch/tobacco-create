package com.createtobacco.smoking;

public record SmokingProfile(
        int puffs,
        float totalDependence,
        int nicotineRushDurationTicks,
        int nicotineRushAmplifier,
        double nicotineRushMovementModifier,
        float nicotineRushDamageReduction,
        float completionExhaustion,
        float specialProcChance
) {
    public SmokingProfile {
        if (puffs <= 0 || totalDependence < 0.0F || nicotineRushDurationTicks < 0 || nicotineRushAmplifier < 0) {
            throw new IllegalArgumentException("Invalid core smoking profile values");
        }
        if (nicotineRushMovementModifier < 0.0D
                || nicotineRushDamageReduction < 0.0F || nicotineRushDamageReduction > 1.0F
                || completionExhaustion < 0.0F
                || specialProcChance < 0.0F || specialProcChance > 1.0F) {
            throw new IllegalArgumentException("Invalid smoking profile modifier values");
        }
    }
}

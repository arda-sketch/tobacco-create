package com.createtobacco.smoking;

public record SmokingProfile(
        int puffs,
        float totalDependence,
        int nicotineRushDurationTicks,
        int nicotineRushAmplifier,
        float completionExhaustion
) {
}

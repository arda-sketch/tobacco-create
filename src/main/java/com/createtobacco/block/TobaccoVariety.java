package com.createtobacco.block;

public enum TobaccoVariety {
    VIRGINIA(1.0F),
    BURLEY(0.9F),
    HAVANA(0.7F);

    private final float growthSpeedMultiplier;

    TobaccoVariety(float growthSpeedMultiplier) {
        if (growthSpeedMultiplier <= 0.0F) {
            throw new IllegalArgumentException("Growth speed multiplier must be positive");
        }
        this.growthSpeedMultiplier = growthSpeedMultiplier;
    }

    public float growthSpeedMultiplier() {
        return growthSpeedMultiplier;
    }
}

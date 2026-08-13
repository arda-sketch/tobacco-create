package com.createtobacco.effect;

import com.createtobacco.CreateTobacco;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class WithdrawalEffect extends MobEffect {
    private static final double[] MOVEMENT_PENALTIES = {-0.03D, -0.05D, -0.07D, -0.10D};
    private static final double[] MINING_PENALTIES = {-0.05D, -0.08D, -0.12D, -0.15D};

    public WithdrawalEffect() {
        super(MobEffectCategory.HARMFUL, 0x6E665B);
        addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(CreateTobacco.MOD_ID, "withdrawal_movement_speed"),
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                amplifier -> MOVEMENT_PENALTIES[Mth.clamp(amplifier, 0, MOVEMENT_PENALTIES.length - 1)]
        );
        addAttributeModifier(
                Attributes.BLOCK_BREAK_SPEED,
                ResourceLocation.fromNamespaceAndPath(CreateTobacco.MOD_ID, "withdrawal_block_break_speed"),
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                amplifier -> MINING_PENALTIES[Mth.clamp(amplifier, 0, MINING_PENALTIES.length - 1)]
        );
    }
}

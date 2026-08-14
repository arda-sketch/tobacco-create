package com.createtobacco.effect;

import com.createtobacco.CreateTobacco;
import com.createtobacco.attachment.WithdrawalTier;
import com.createtobacco.smoking.SmokingBalance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class WithdrawalEffect extends MobEffect {
    public WithdrawalEffect() {
        super(MobEffectCategory.HARMFUL, 0x6E665B);
        addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(CreateTobacco.MOD_ID, "withdrawal_movement_speed"),
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                amplifier -> SmokingBalance.withdrawal(WithdrawalTier.fromAmplifier(amplifier)).movementPenalty()
        );
        addAttributeModifier(
                Attributes.BLOCK_BREAK_SPEED,
                ResourceLocation.fromNamespaceAndPath(CreateTobacco.MOD_ID, "withdrawal_block_break_speed"),
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                amplifier -> SmokingBalance.withdrawal(WithdrawalTier.fromAmplifier(amplifier)).miningPenalty()
        );
    }
}

package com.createtobacco.effect;

import com.createtobacco.CreateTobacco;
import com.createtobacco.smoking.SmokingBalance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class NicotineRushEffect extends MobEffect {
    private static final ResourceLocation MOVEMENT_SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(CreateTobacco.MOD_ID, "nicotine_rush_movement_speed");

    public NicotineRushEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xD8C27A);
        addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                MOVEMENT_SPEED_MODIFIER_ID,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                SmokingBalance::nicotineRushMovementModifier
        );
    }
}

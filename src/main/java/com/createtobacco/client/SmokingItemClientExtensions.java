package com.createtobacco.client;

import com.createtobacco.item.AbstractSmokingItem;
import com.createtobacco.smoking.SmokingBalance;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/** Smooth hand-to-mouth transform without reusing a vanilla eating/drinking pose. */
public final class SmokingItemClientExtensions implements IClientItemExtensions {
    public static final SmokingItemClientExtensions INSTANCE = new SmokingItemClientExtensions();

    private SmokingItemClientExtensions() {
    }

    @Override
    public boolean applyForgeHandTransform(
            PoseStack poseStack,
            LocalPlayer player,
            HumanoidArm arm,
            ItemStack itemInHand,
            float partialTick,
            float equipProcess,
            float swingProcess
    ) {
        if (!(itemInHand.getItem() instanceof AbstractSmokingItem)
                || !player.isUsingItem()
                || player.getUseItem().getItem() != itemInHand.getItem()) {
            return false;
        }

        HumanoidArm usingArm = player.getUsedItemHand() == InteractionHand.MAIN_HAND
                ? player.getMainArm()
                : player.getMainArm().getOpposite();
        if (usingArm != arm) {
            return false;
        }

        float elapsed = SmokingBalance.PUFF_USE_DURATION_TICKS - player.getUseItemRemainingTicks() + partialTick;
        float progress = smoothstep(Mth.clamp(elapsed / 6.0F, 0.0F, 1.0F));
        float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;

        // Move toward the mouth, then rotate the originally upright generated-item
        // silhouette into a cigarette-like horizontal orientation. After the first
        // six ticks progress remains 1, so the held pose is completely stable.
        poseStack.translate(-0.17F * side * progress, 0.19F * progress, 0.27F * progress);
        poseStack.mulPose(Axis.XP.rotationDegrees(-15.0F * progress));
        poseStack.mulPose(Axis.YP.rotationDegrees(22.0F * side * progress));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-78.0F * side * progress));

        // Keep the normal first-person base/equip transform; our use transform is
        // layered on top and UseAnim.NONE prevents vanilla drinking movement.
        return false;
    }

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        if (stack.getItem() instanceof AbstractSmokingItem
                && entity.isUsingItem()
                && entity.getUsedItemHand() == hand
                && entity.getUseItemRemainingTicks() > 0) {
            return SmokingArmPoseParameters.SMOKING_POSE.getValue();
        }
        return null;
    }

    private static float smoothstep(float value) {
        return value * value * (3.0F - 2.0F * value);
    }
}

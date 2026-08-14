package com.createtobacco.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

/** Parameters used by NeoForge's runtime enum extension for the smoking arm pose. */
public final class SmokingArmPoseParameters {
    public static final EnumProxy<HumanoidModel.ArmPose> SMOKING_POSE = new EnumProxy<>(
            HumanoidModel.ArmPose.class,
            false,
            (IArmPoseTransformer) (model, entity, arm) -> {
                var modelArm = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
                float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;

                // Bring one hand naturally inward and slightly below the mouth.
                // Small head-follow terms keep the pose from looking detached when
                // the player looks around, without turning it into the horn pose.
                modelArm.xRot = -1.18F + model.head.xRot * 0.25F;
                modelArm.yRot = -0.34F * side + model.head.yRot * 0.12F;
                modelArm.zRot = 0.10F * side;
            }
    );

    private SmokingArmPoseParameters() {
    }
}

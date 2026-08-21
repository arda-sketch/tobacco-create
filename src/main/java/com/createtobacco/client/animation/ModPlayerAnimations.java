package com.createtobacco.client.animation;

import com.createtobacco.CreateTobacco;
import com.createtobacco.item.AbstractSmokingItem;
import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.RawAnimation;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

/**
 * PAL animation layer for active cigarette/cigar use.
 *
 * Motion lives in assets/create_tobacco/player_animations and can be edited
 * in Blockbench independently from gameplay code.
 */
public final class ModPlayerAnimations {

        public static final ResourceLocation SMOKING_LAYER = id("smoking");

        public static final ResourceLocation SMOKING_RAISE_RIGHT = id("smoking_raise_right");

        public static final ResourceLocation SMOKING_HOLD_RIGHT = id("smoking_hold_right");

        public static final ResourceLocation SMOKING_RAISE_LEFT = id("smoking_raise_left");

        public static final ResourceLocation SMOKING_HOLD_LEFT = id("smoking_hold_left");

        public static final ResourceLocation SMOKING_RAISE_RIGHT_FP = id("smoking_raise_right_fp");

        public static final ResourceLocation SMOKING_HOLD_RIGHT_FP = id("smoking_hold_right_fp");

        public static final ResourceLocation SMOKING_RAISE_LEFT_FP = id("smoking_raise_left_fp");

        public static final ResourceLocation SMOKING_HOLD_LEFT_FP = id("smoking_hold_left_fp");

        private static final int SMOKING_PRIORITY = 1600;

        private ModPlayerAnimations() {
        }

        public static void register() {
                PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                                SMOKING_LAYER,
                                SMOKING_PRIORITY,
                                player -> {
                                        AnimationCache cache = new AnimationCache();
                                        boolean[] wasSmoking = { false };

                                        PlayerAnimationController controller = new PlayerAnimationController(
                                                        player,
                                                        (animationController, animationData, animationSetter) -> {
                                                                boolean isSmoking = player.isUsingItem()
                                                                                && player.getUseItem()
                                                                                                .getItem() instanceof AbstractSmokingItem;

                                                                if (!isSmoking) {
                                                                        wasSmoking[0] = false;
                                                                        return PlayState.STOP;
                                                                }

                                                                // New continuous smoking action:
                                                                // restart raise -> hold from the beginning.
                                                                if (!wasSmoking[0]) {
                                                                        animationController.forceAnimationReset();
                                                                        wasSmoking[0] = true;
                                                                }

                                                                boolean rightArm = physicalUsedArm(
                                                                                player.getUsedItemHand(),
                                                                                player.getMainArm()) == HumanoidArm.RIGHT;

                                                                boolean firstPersonSmoking = player == Minecraft.getInstance().player
                                                                                && Minecraft.getInstance()
                                                                                                .options
                                                                                                .getCameraType()
                                                                                                .isFirstPerson();

                                                                RawAnimation animation;
                                                                if (rightArm) {
                                                                        animation = firstPersonSmoking
                                                                                        ? cache.resolve(
                                                                                                        SMOKING_RAISE_RIGHT_FP,
                                                                                                        SMOKING_HOLD_RIGHT_FP)
                                                                                        : cache.resolve(
                                                                                                        SMOKING_RAISE_RIGHT,
                                                                                                        SMOKING_HOLD_RIGHT);
                                                                } else {
                                                                        animation = firstPersonSmoking
                                                                                        ? cache.resolve(
                                                                                                        SMOKING_RAISE_LEFT_FP,
                                                                                                        SMOKING_HOLD_LEFT_FP)
                                                                                        : cache.resolve(
                                                                                                        SMOKING_RAISE_LEFT,
                                                                                                        SMOKING_HOLD_LEFT);
                                                                }

                                                                return animation == null
                                                                                ? PlayState.STOP
                                                                                : animationSetter.setAnimation(
                                                                                                animation);
                                                        });

                                        controller.setFirstPersonMode(
                                                        FirstPersonMode.THIRD_PERSON_MODEL);

                                        // PAL renders only the physical hand which actually contains
                                        // the smoking item. The other hand/item (for example flint
                                        // and steel) remains outside the smoking animation.
                                        controller.setFirstPersonConfigurationHandler(animationController -> {
                                                boolean isSmoking = player.isUsingItem()
                                                                && player.getUseItem()
                                                                                .getItem() instanceof AbstractSmokingItem;

                                                if (!isSmoking) {
                                                        return new FirstPersonConfiguration(
                                                                        false,
                                                                        false,
                                                                        false,
                                                                        false,
                                                                        false);
                                                }

                                                HumanoidArm usedArm = physicalUsedArm(
                                                                player.getUsedItemHand(),
                                                                player.getMainArm());

                                                boolean rightHandSmoking = usedArm == HumanoidArm.RIGHT;

                                                boolean leftHandSmoking = usedArm == HumanoidArm.LEFT;

                                                return new FirstPersonConfiguration(
                                                                rightHandSmoking,
                                                                leftHandSmoking,
                                                                rightHandSmoking,
                                                                leftHandSmoking,
                                                                false);
                                        });

                                        controller.setFirstPersonFollowsCamera(true);
                                        controller.setFirstPersonTransitionLength(4);

                                        return controller;
                                });
        }

        /**
         * Converts Minecraft's MAIN_HAND/OFF_HAND into the actual physical
         * right/left arm. This also handles players whose main arm is left.
         */
        private static HumanoidArm physicalUsedArm(
                        InteractionHand hand,
                        HumanoidArm mainArm) {
                return hand == InteractionHand.MAIN_HAND
                                ? mainArm
                                : mainArm.getOpposite();
        }

        private static ResourceLocation id(String path) {
                return ResourceLocation.fromNamespaceAndPath(
                                CreateTobacco.MOD_ID,
                                path);
        }

        /**
         * PAL recommends reusing RawAnimation rather than constructing it
         * continuously. It is rebuilt only if the resource itself changes,
         * for example after a resource reload.
         */
        private static final class AnimationCache {
                private ResourceLocation raiseId;
                private ResourceLocation holdId;

                private Animation raiseSource;
                private Animation holdSource;

                private RawAnimation raw;

                private RawAnimation resolve(
                                ResourceLocation requestedRaiseId,
                                ResourceLocation requestedHoldId) {
                        Animation requestedRaise = PlayerAnimResources.getAnimation(requestedRaiseId);

                        Animation requestedHold = PlayerAnimResources.getAnimation(requestedHoldId);

                        if (requestedRaise == null || requestedHold == null) {
                                clear();
                                return null;
                        }

                        if (!requestedRaiseId.equals(raiseId)
                                        || !requestedHoldId.equals(holdId)
                                        || requestedRaise != raiseSource
                                        || requestedHold != holdSource
                                        || raw == null) {

                                raiseId = requestedRaiseId;
                                holdId = requestedHoldId;

                                raiseSource = requestedRaise;
                                holdSource = requestedHold;

                                raw = RawAnimation.begin()
                                                .thenPlay(requestedRaise)
                                                .thenLoop(requestedHold);
                        }

                        return raw;
                }

                private void clear() {
                        raiseId = null;
                        holdId = null;

                        raiseSource = null;
                        holdSource = null;

                        raw = null;
                }
        }
}

package com.createtobacco.item;

import com.createtobacco.component.SmokingItemState;
import com.createtobacco.attachment.SmokingData;
import com.createtobacco.attachment.WithdrawalTier;
import com.createtobacco.registry.ModAttachments;
import com.createtobacco.registry.ModDataComponents;
import com.createtobacco.registry.ModEffects;
import com.createtobacco.registry.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

public abstract class AbstractSmokingItem extends Item {
    private static final int USE_DURATION_TICKS = 24;

    // Phase 8 visual tuning. These offsets are measured from the entity's eye position.
    public static final double MOUTH_FORWARD_OFFSET = 0.45D;
    public static final double HELD_SMOKE_FORWARD_OFFSET = 0.38D;
    public static final double HELD_SMOKE_SIDE_OFFSET = 0.18D;
    public static final double HELD_SMOKE_DOWN_OFFSET = 0.16D;
    public static final int HELD_SMOKE_INTERVAL_TICKS = 7;

    private final SmokingItemState defaultState;
    private final float totalDependence;
    private final int nicotineRushDurationTicks;
    private final float completionExhaustion;

    protected AbstractSmokingItem(
            Properties properties,
            SmokingItemState defaultState,
            float totalDependence,
            int nicotineRushDurationTicks,
            float completionExhaustion
    ) {
        super(properties.stacksTo(1).component(ModDataComponents.SMOKING_ITEM_STATE.get(), defaultState));
        this.defaultState = defaultState;
        this.totalDependence = totalDependence;
        this.nicotineRushDurationTicks = nicotineRushDurationTicks;
        this.completionExhaustion = completionExhaustion;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack smokingStack = player.getItemInHand(usedHand);
        SmokingItemState state = getState(smokingStack);

        if (!state.lit()) {
            InteractionHand ignitionHand = usedHand == InteractionHand.MAIN_HAND
                    ? InteractionHand.OFF_HAND
                    : InteractionHand.MAIN_HAND;
            ItemStack ignitionStack = player.getItemInHand(ignitionHand);
            if (!ignitionStack.is(Items.FLINT_AND_STEEL)) {
                return InteractionResultHolder.fail(smokingStack);
            }

            if (level instanceof ServerLevel serverLevel) {
                smokingStack.set(ModDataComponents.SMOKING_ITEM_STATE.get(), state.withLit(true));
                ignitionStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(ignitionHand));
                playIgnitionEffects(serverLevel, player);
            }

            return InteractionResultHolder.sidedSuccess(smokingStack, level.isClientSide());
        }

        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(smokingStack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return stack;
        }

        SmokingItemState state = getState(stack);
        if (!state.lit() || state.remainingPuffs() <= 0) {
            return stack;
        }

        int remainingPuffs = state.remainingPuffs() - 1;
        if (livingEntity instanceof Player player) {
            SmokingData smokingData = player.getData(ModAttachments.SMOKING_DATA);
            smokingData.addDependence(totalDependence / defaultState.remainingPuffs());
            relieveWithdrawal(player, smokingData);
        }
        playPuffEffects(serverLevel, livingEntity);
        if (remainingPuffs == 0) {
            if (livingEntity instanceof Player player) {
                completeSmoking(player);
            }
            stack.shrink(1);
        } else {
            stack.set(ModDataComponents.SMOKING_ITEM_STATE.get(), state.withRemainingPuffs(remainingPuffs));
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getState(stack).lit();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        SmokingItemState state = getState(stack);
        return Math.round(13.0F * state.remainingPuffs() / defaultState.remainingPuffs());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        SmokingItemState state = getState(stack);
        float remainingRatio = (float) state.remainingPuffs() / defaultState.remainingPuffs();
        return Mth.hsvToRgb(remainingRatio / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        SmokingItemState state = getState(stack);
        tooltipComponents.add(Component.translatable(
                "tooltip.create_tobacco.remaining_puffs",
                state.remainingPuffs(),
                defaultState.remainingPuffs()
        ));
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        SmokingItemState state = getState(stack);
        if (!level.isClientSide() || !state.lit() || remainingUseDuration % HELD_SMOKE_INTERVAL_TICKS != 0) {
            return;
        }

        Vec3 position = heldItemSmokePosition(livingEntity);
        level.addParticle(
                ModParticles.TOBACCO_SMOKE.get(),
                position.x,
                position.y,
                position.z,
                0.0D,
                0.008D,
                0.0D
        );
    }

    private SmokingItemState getState(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.SMOKING_ITEM_STATE.get(), defaultState);
    }

    private static void playIgnitionEffects(ServerLevel level, LivingEntity entity) {
        Vec3 position = effectPosition(entity);
        level.playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                SoundEvents.FLINTANDSTEEL_USE,
                SoundSource.PLAYERS,
                0.7F,
                0.9F + level.getRandom().nextFloat() * 0.2F
        );
        level.sendParticles(ParticleTypes.SMALL_FLAME, position.x, position.y, position.z, 2, 0.03, 0.03, 0.03, 0.005);
        level.sendParticles(ModParticles.TOBACCO_SMOKE.get(), position.x, position.y, position.z,
                3, 0.04, 0.04, 0.04, 0.008);
    }

    private static void playPuffEffects(ServerLevel level, LivingEntity entity) {
        Vec3 position = effectPosition(entity);
        level.playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                SoundEvents.FIRE_AMBIENT,
                SoundSource.PLAYERS,
                0.25F,
                1.2F
        );
        level.sendParticles(ModParticles.TOBACCO_SMOKE.get(), position.x, position.y, position.z,
                7, 0.08, 0.05, 0.08, 0.012);
    }

    private static Vec3 effectPosition(LivingEntity entity) {
        return entity.getEyePosition().add(entity.getLookAngle().scale(MOUTH_FORWARD_OFFSET));
    }

    private static Vec3 heldItemSmokePosition(LivingEntity entity) {
        Vec3 look = entity.getLookAngle();
        Vec3 horizontalRight = new Vec3(-look.z, 0.0D, look.x).normalize();
        double handSign = entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? 1.0D : -1.0D;
        return entity.getEyePosition()
                .add(look.scale(HELD_SMOKE_FORWARD_OFFSET))
                .add(horizontalRight.scale(HELD_SMOKE_SIDE_OFFSET * handSign))
                .add(0.0D, -HELD_SMOKE_DOWN_OFFSET, 0.0D);
    }

    private void completeSmoking(Player player) {
        player.getData(ModAttachments.SMOKING_DATA).markSatisfied();
        player.removeEffect(ModEffects.WITHDRAWAL);
        player.removeEffect(ModEffects.NICOTINE_RUSH);
        player.addEffect(new MobEffectInstance(
                ModEffects.NICOTINE_RUSH,
                nicotineRushDurationTicks,
                0,
                false,
                true,
                true
        ));
        player.causeFoodExhaustion(completionExhaustion);
    }

    private static void relieveWithdrawal(Player player, SmokingData data) {
        MobEffectInstance withdrawal = player.getEffect(ModEffects.WITHDRAWAL);
        if (withdrawal == null) {
            return;
        }

        WithdrawalTier tier = WithdrawalTier.fromAmplifier(withdrawal.getAmplifier());
        if (data.recordWithdrawalReliefPuff(tier.reliefPuffsRequired())) {
            player.removeEffect(ModEffects.WITHDRAWAL);
        }
    }
}

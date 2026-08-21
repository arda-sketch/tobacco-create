package com.createtobacco.item;

import com.createtobacco.component.SmokingItemState;
import com.createtobacco.attachment.SmokingData;
import com.createtobacco.attachment.WithdrawalTier;
import com.createtobacco.registry.ModAttachments;
import com.createtobacco.registry.ModDataComponents;
import com.createtobacco.registry.ModEffects;
import com.createtobacco.registry.ModParticles;
import com.createtobacco.smoking.SmokingBalance;
import com.createtobacco.smoking.SmokingEffects;
import com.createtobacco.smoking.SmokingProduct;
import com.createtobacco.smoking.SmokingProfile;
import net.minecraft.core.particles.ParticleTypes;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

public abstract class AbstractSmokingItem extends Item {
    // Phase 8 visual tuning. These offsets are measured from the entity's eye position.
    public static final double MOUTH_FORWARD_OFFSET = 0.45D;
    public static final double HELD_SMOKE_FORWARD_OFFSET = 0.38D;
    public static final double HELD_SMOKE_SIDE_OFFSET = 0.18D;
    public static final double HELD_SMOKE_DOWN_OFFSET = 0.16D;
    public static final int HELD_SMOKE_INTERVAL_TICKS = 5;

    private final SmokingItemState defaultState;
    private final SmokingProduct product;
    private final SmokingProfile profile;

    protected AbstractSmokingItem(Properties properties, SmokingProduct product) {
        this(properties, product, SmokingBalance.profile(product));
    }

    private AbstractSmokingItem(Properties properties, SmokingProduct product, SmokingProfile profile) {
        super(properties.stacksTo(1).component(
                ModDataComponents.SMOKING_ITEM_STATE.get(),
                new SmokingItemState(profile.puffs(), false, 0)
        ));
        this.defaultState = new SmokingItemState(profile.puffs(), false, 0);
        this.product = product;
        this.profile = profile;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() == newStack.getItem();
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
                smokingStack.set(ModDataComponents.SMOKING_ITEM_STATE.get(), state.ignite(naturalBurnIntervalTicks()));
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
        // Continuous smoking completes individual puffs from onUseTick(). Reaching
        // the very long vanilla use duration should not award an extra puff.
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return SmokingBalance.CONTINUOUS_SMOKING_USE_DURATION_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
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
        if (state.lit() && state.burnTicksRemaining() > 0) {
            tooltipComponents.add(Component.translatable(
                    "tooltip.create_tobacco.burn_time",
                    Math.max(1, (state.burnTicksRemaining() + 19) / 20)
            ));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || entity.tickCount % SmokingBalance.TICKS_PER_SECOND != 0) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity
                && livingEntity.isUsingItem()
                && livingEntity.getUseItem() == stack) {
            return;
        }
        tickNaturalBurn(stack, SmokingBalance.TICKS_PER_SECOND);
    }

    /**
     * Advances passive smouldering without awarding dependence relief, brand
     * procs or completion rewards. Returns true when the stack state changed.
     */
    public boolean tickNaturalBurn(ItemStack stack, int elapsedTicks) {
        SmokingItemState state = getState(stack);
        if (!state.lit() || state.remainingPuffs() <= 0 || stack.isEmpty()) {
            return false;
        }

        int interval = naturalBurnIntervalTicks();
        int burnTicks = state.burnTicksRemaining();
        if (burnTicks <= 0) {
            stack.set(ModDataComponents.SMOKING_ITEM_STATE.get(), state.withBurnTicksRemaining(interval));
            return true;
        }

        if (burnTicks > elapsedTicks) {
            stack.set(ModDataComponents.SMOKING_ITEM_STATE.get(), state.withBurnTicksRemaining(burnTicks - elapsedTicks));
            return true;
        }

        int remainingPuffs = state.remainingPuffs() - 1;
        if (remainingPuffs <= 0) {
            stack.shrink(1);
        } else {
            stack.set(ModDataComponents.SMOKING_ITEM_STATE.get(), state.afterPuff(remainingPuffs, interval));
        }
        return true;
    }

    private int naturalBurnIntervalTicks() {
        return profile.puffs() > 5
                ? SmokingBalance.CIGAR_NATURAL_BURN_INTERVAL_TICKS
                : SmokingBalance.CIGARETTE_NATURAL_BURN_INTERVAL_TICKS;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        SmokingItemState state = getState(stack);
        if (!state.lit() || state.remainingPuffs() <= 0 || stack.isEmpty()) {
            return;
        }

        int elapsedUseTicks = SmokingBalance.CONTINUOUS_SMOKING_USE_DURATION_TICKS - remainingUseDuration;
        if (level instanceof ServerLevel serverLevel
                && elapsedUseTicks > 0
                && elapsedUseTicks % SmokingBalance.PUFF_USE_DURATION_TICKS == 0) {
            completePuff(serverLevel, livingEntity, stack);
            return;
        }

        if (!level.isClientSide() || remainingUseDuration % HELD_SMOKE_INTERVAL_TICKS != 0) {
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

    /** Completes exactly one server-authoritative puff during a continuous use. */
    private void completePuff(ServerLevel level, LivingEntity livingEntity, ItemStack stack) {
        SmokingItemState state = getState(stack);
        if (!state.lit() || state.remainingPuffs() <= 0 || stack.isEmpty()) {
            return;
        }

        int remainingPuffs = state.remainingPuffs() - 1;
        if (livingEntity instanceof ServerPlayer player) {
            SmokingData smokingData = player.getData(ModAttachments.SMOKING_DATA);
            smokingData.addDependence(profile.totalDependence() / profile.puffs());
            relieveWithdrawal(player, smokingData);
            SmokingEffects.onSuccessfulPuff(player, product);

            int rapidPuffStreak = smokingData.recordRapidPuff(level.getGameTime());
            maybeApplyRapidSmokingNausea(player, rapidPuffStreak);
        }

        playPuffEffects(level, livingEntity);
        if (remainingPuffs == 0) {
            if (livingEntity instanceof ServerPlayer player) {
                SmokingEffects.onFullyConsumed(player, product);
            }
            stack.shrink(1);
            livingEntity.stopUsingItem();
        } else {
            stack.set(
                    ModDataComponents.SMOKING_ITEM_STATE.get(),
                    state.afterPuff(remainingPuffs, naturalBurnIntervalTicks())
            );
        }
    }

    private static void maybeApplyRapidSmokingNausea(ServerPlayer player, int streak) {
        float chance;
        int duration;
        int amplifier;

        if (streak >= 5) {
            chance = SmokingBalance.RAPID_PUFF_5_PLUS_NAUSEA_CHANCE;
            duration = SmokingBalance.RAPID_PUFF_5_PLUS_NAUSEA_TICKS;
            amplifier = SmokingBalance.RAPID_PUFF_5_PLUS_NAUSEA_AMPLIFIER;
        } else if (streak == 4) {
            chance = SmokingBalance.RAPID_PUFF_4_NAUSEA_CHANCE;
            duration = SmokingBalance.RAPID_PUFF_4_NAUSEA_TICKS;
            amplifier = SmokingBalance.RAPID_PUFF_4_NAUSEA_AMPLIFIER;
        } else if (streak == 3) {
            chance = SmokingBalance.RAPID_PUFF_3_NAUSEA_CHANCE;
            duration = SmokingBalance.RAPID_PUFF_3_NAUSEA_TICKS;
            amplifier = SmokingBalance.RAPID_PUFF_3_NAUSEA_AMPLIFIER;
        } else {
            return;
        }

        if (player.getRandom().nextFloat() < chance) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.CONFUSION,
                    duration,
                    amplifier,
                    false,
                    true,
                    true
            ));
        }
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
                12, 0.11, 0.07, 0.11, 0.016);
        level.sendParticles(ParticleTypes.POOF, position.x, position.y, position.z,
                3, 0.08, 0.05, 0.08, 0.01);
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

    private static void relieveWithdrawal(ServerPlayer player, SmokingData data) {
        var withdrawal = player.getEffect(ModEffects.WITHDRAWAL);
        if (withdrawal == null) {
            return;
        }

        data.recordWithdrawalReliefPuff();
        int currentAmplifier = withdrawal.getAmplifier();
        int remainingDuration = withdrawal.getDuration();
        player.removeEffect(ModEffects.WITHDRAWAL);

        if (currentAmplifier <= 0) {
            data.resetWithdrawalReliefPuffs();
            return;
        }

        // Every successful puff immediately lowers the active episode by one tier.
        // Keep the remaining duration instead of restarting a fresh full episode.
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                ModEffects.WITHDRAWAL,
                remainingDuration,
                currentAmplifier - 1,
                false,
                true,
                true
        ));
    }
}

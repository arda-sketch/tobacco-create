package com.createtobacco.item;

import com.createtobacco.component.SmokingItemState;
import com.createtobacco.registry.ModDataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractSmokingItem extends Item {
    private static final int USE_DURATION_TICKS = 24;

    private final SmokingItemState defaultState;

    protected AbstractSmokingItem(Properties properties, SmokingItemState defaultState) {
        super(properties.stacksTo(1).component(ModDataComponents.SMOKING_ITEM_STATE.get(), defaultState));
        this.defaultState = defaultState;
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
        playPuffEffects(serverLevel, livingEntity);
        if (remainingPuffs == 0) {
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
        level.sendParticles(ParticleTypes.SMOKE, position.x, position.y, position.z, 3, 0.04, 0.04, 0.04, 0.01);
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
        level.sendParticles(ParticleTypes.SMOKE, position.x, position.y, position.z, 5, 0.08, 0.05, 0.08, 0.015);
    }

    private static Vec3 effectPosition(LivingEntity entity) {
        return entity.getEyePosition().add(entity.getLookAngle().scale(0.45));
    }
}

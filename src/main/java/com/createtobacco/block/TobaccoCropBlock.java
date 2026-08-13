package com.createtobacco.block;

import com.mojang.serialization.MapCodec;
import com.createtobacco.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;

public final class TobaccoCropBlock extends CropBlock {
    private static final MapCodec<TobaccoCropBlock> VIRGINIA_CODEC =
            simpleCodec(properties -> new TobaccoCropBlock(TobaccoVariety.VIRGINIA, properties));
    private static final MapCodec<TobaccoCropBlock> BURLEY_CODEC =
            simpleCodec(properties -> new TobaccoCropBlock(TobaccoVariety.BURLEY, properties));
    private static final MapCodec<TobaccoCropBlock> HAVANA_CODEC =
            simpleCodec(properties -> new TobaccoCropBlock(TobaccoVariety.HAVANA, properties));

    private final TobaccoVariety variety;

    public TobaccoCropBlock(TobaccoVariety variety, BlockBehaviour.Properties properties) {
        super(properties);
        this.variety = variety;
    }

    public TobaccoVariety variety() {
        return variety;
    }

    @Override
    public MapCodec<TobaccoCropBlock> codec() {
        return switch (variety) {
            case VIRGINIA -> VIRGINIA_CODEC;
            case BURLEY -> BURLEY_CODEC;
            case HAVANA -> HAVANA_CODEC;
        };
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1) || level.getRawBrightness(pos, 0) < 9) {
            return;
        }

        int age = getAge(state);
        if (age >= getMaxAge()) {
            return;
        }

        float growthSpeed = getGrowthSpeed(state, level, pos) * variety.growthSpeedMultiplier();
        boolean shouldGrow = random.nextInt((int) (25.0F / growthSpeed) + 1) == 0;
        if (CommonHooks.canCropGrow(level, pos, state, shouldGrow)) {
            level.setBlock(pos, getStateForAge(age + 1), 2);
            CommonHooks.fireCropGrowPost(level, pos, state);
        }
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return switch (variety) {
            case VIRGINIA -> ModItems.VIRGINIA_SEEDS.get();
            case BURLEY -> ModItems.BURLEY_SEEDS.get();
            case HAVANA -> ModItems.HAVANA_SEEDS.get();
        };
    }
}

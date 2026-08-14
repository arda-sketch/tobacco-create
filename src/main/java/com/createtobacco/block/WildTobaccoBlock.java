package com.createtobacco.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Naturally generated, two-block-tall tobacco. It stays separate from the
 * cultivated CropBlock so wild plants can have their own silhouette, drops,
 * placement rules and world-generation frequency.
 */
public final class WildTobaccoBlock extends DoublePlantBlock {
    public static final MapCodec<WildTobaccoBlock> CODEC = simpleCodec(WildTobaccoBlock::new);

    public WildTobaccoBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<WildTobaccoBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT);
    }
}

package com.createtobacco.worldgen;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Places exactly one complete wild tobacco plant at the local surface. */
public final class WildTobaccoPlantFeature extends Feature<NoneFeatureConfiguration> {
    private static final int WORLDGEN_UPDATE_FLAGS = Block.UPDATE_CLIENTS;
    private final Supplier<? extends DoublePlantBlock> plant;

    public WildTobaccoPlantFeature(Supplier<? extends DoublePlantBlock> plant) {
        super(NoneFeatureConfiguration.CODEC);
        this.plant = plant;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos candidate = context.origin();

        // RandomPatch supplies X/Z candidates. Re-resolve Y for every candidate
        // so a patch follows terrain instead of placing some plants one block
        // down in shallow depressions relative to the patch origin.
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate.getX(), candidate.getZ());
        BlockPos lowerPos = new BlockPos(candidate.getX(), surfaceY, candidate.getZ());
        BlockPos upperPos = lowerPos.above();

        if (!level.isEmptyBlock(lowerPos) || !level.isEmptyBlock(upperPos)) {
            return false;
        }

        DoublePlantBlock block = plant.get();
        BlockState lower = block.defaultBlockState()
                .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
        if (!lower.canSurvive(level, lowerPos)) {
            return false;
        }

        BlockState upper = block.defaultBlockState()
                .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
        level.setBlock(lowerPos, lower, WORLDGEN_UPDATE_FLAGS);
        level.setBlock(upperPos, upper, WORLDGEN_UPDATE_FLAGS);
        return true;
    }
}

package com.createtobacco.registry;

import com.createtobacco.CreateTobacco;
import com.createtobacco.block.TobaccoCropBlock;
import com.createtobacco.block.TobaccoVariety;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateTobacco.MOD_ID);

    public static final DeferredBlock<TobaccoCropBlock> VIRGINIA_TOBACCO = registerCrop(
            "virginia_tobacco", TobaccoVariety.VIRGINIA);
    public static final DeferredBlock<TobaccoCropBlock> BURLEY_TOBACCO = registerCrop(
            "burley_tobacco", TobaccoVariety.BURLEY);
    public static final DeferredBlock<TobaccoCropBlock> HAVANA_TOBACCO = registerCrop(
            "havana_tobacco", TobaccoVariety.HAVANA);

    private ModBlocks() {
    }

    private static DeferredBlock<TobaccoCropBlock> registerCrop(String name, TobaccoVariety variety) {
        return BLOCKS.registerBlock(
                name,
                properties -> new TobaccoCropBlock(variety, properties),
                BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT));
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}

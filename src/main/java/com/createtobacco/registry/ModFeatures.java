package com.createtobacco.registry;

import com.createtobacco.CreateTobacco;
import com.createtobacco.worldgen.WildTobaccoPlantFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Feature types used by data-driven wild tobacco RandomPatch configurations. */
public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, CreateTobacco.MOD_ID);

    public static final DeferredHolder<Feature<?>, WildTobaccoPlantFeature> WILD_VIRGINIA_TOBACCO_PLANT =
            FEATURES.register("wild_virginia_tobacco_plant",
                    () -> new WildTobaccoPlantFeature(ModBlocks.WILD_VIRGINIA_TOBACCO));
    public static final DeferredHolder<Feature<?>, WildTobaccoPlantFeature> WILD_BURLEY_TOBACCO_PLANT =
            FEATURES.register("wild_burley_tobacco_plant",
                    () -> new WildTobaccoPlantFeature(ModBlocks.WILD_BURLEY_TOBACCO));
    public static final DeferredHolder<Feature<?>, WildTobaccoPlantFeature> WILD_HAVANA_TOBACCO_PLANT =
            FEATURES.register("wild_havana_tobacco_plant",
                    () -> new WildTobaccoPlantFeature(ModBlocks.WILD_HAVANA_TOBACCO));

    private ModFeatures() {
    }

    public static void register(IEventBus modEventBus) {
        FEATURES.register(modEventBus);
    }
}

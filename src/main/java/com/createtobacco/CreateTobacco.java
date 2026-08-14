package com.createtobacco;

import com.createtobacco.registry.ModAttachments;
import com.createtobacco.registry.ModBlocks;
import com.createtobacco.registry.ModCreativeTabs;
import com.createtobacco.registry.ModCriteriaTriggers;
import com.createtobacco.registry.ModDataComponents;
import com.createtobacco.registry.ModEffects;
import com.createtobacco.registry.ModFeatures;
import com.createtobacco.registry.ModItems;
import com.createtobacco.registry.ModMenus;
import com.createtobacco.registry.ModParticles;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CreateTobacco.MOD_ID)
public final class CreateTobacco {
    public static final String MOD_ID = "create_tobacco";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateTobacco(IEventBus modEventBus) {
        ModCriteriaTriggers.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModItems.register(modEventBus);
        ModMenus.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModEffects.register(modEventBus);
        ModParticles.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        LOGGER.info("Initializing {}", MOD_ID);
    }
}

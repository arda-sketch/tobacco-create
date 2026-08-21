package com.createtobacco.client;

import com.createtobacco.CreateTobacco;
import com.createtobacco.client.animation.ModPlayerAnimations;
import com.createtobacco.client.screen.CigaretteCaseScreen;
import com.createtobacco.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = CreateTobacco.MOD_ID, value = Dist.CLIENT)
public final class CreateTobaccoClient {
    private CreateTobaccoClient() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModPlayerAnimations.register();
            ModItemProperties.register();
        });
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.CIGARETTE_CASE.get(), CigaretteCaseScreen::new);
    }
}

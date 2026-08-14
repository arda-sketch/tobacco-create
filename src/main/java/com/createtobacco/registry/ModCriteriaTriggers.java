package com.createtobacco.registry;

import com.createtobacco.CreateTobacco;
import com.createtobacco.advancement.SimplePlayerTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, CreateTobacco.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, SimplePlayerTrigger> FULL_CIGARETTE_PACK =
            TRIGGERS.register("full_cigarette_pack", SimplePlayerTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, SimplePlayerTrigger> SMOKING_ITEM_FULLY_CONSUMED =
            TRIGGERS.register("smoking_item_fully_consumed", SimplePlayerTrigger::new);

    private ModCriteriaTriggers() {
    }

    public static void register(IEventBus modEventBus) {
        TRIGGERS.register(modEventBus);
    }
}

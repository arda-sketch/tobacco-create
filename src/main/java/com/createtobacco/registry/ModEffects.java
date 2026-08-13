package com.createtobacco.registry;

import com.createtobacco.CreateTobacco;
import com.createtobacco.effect.NicotineRushEffect;
import com.createtobacco.effect.WithdrawalEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, CreateTobacco.MOD_ID);

    public static final DeferredHolder<MobEffect, NicotineRushEffect> NICOTINE_RUSH =
            EFFECTS.register("nicotine_rush", NicotineRushEffect::new);
    public static final DeferredHolder<MobEffect, WithdrawalEffect> WITHDRAWAL =
            EFFECTS.register("withdrawal", WithdrawalEffect::new);

    private ModEffects() {
    }

    public static void register(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
    }
}

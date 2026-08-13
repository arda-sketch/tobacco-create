package com.createtobacco.registry;

import com.createtobacco.CreateTobacco;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, CreateTobacco.MOD_ID);

    private ModParticles() {
    }

    public static void register(IEventBus modEventBus) {
        PARTICLES.register(modEventBus);
    }
}


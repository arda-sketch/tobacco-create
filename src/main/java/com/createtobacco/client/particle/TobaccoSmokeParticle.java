package com.createtobacco.client.particle;

import com.createtobacco.CreateTobacco;
import com.createtobacco.registry.ModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = CreateTobacco.MOD_ID, value = Dist.CLIENT)
public final class TobaccoSmokeParticle extends BaseAshSmokeParticle {
    private TobaccoSmokeParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            SpriteSet sprites
    ) {
        super(level, x, y, z, 0.04F, 0.04F, 0.04F, xSpeed, ySpeed, zSpeed,
                0.7F, sprites, 0.35F, 18, -0.025F, false);
        setColor(0.58F, 0.56F, 0.52F);
        setAlpha(0.48F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @SubscribeEvent
    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.TOBACCO_SMOKE.get(), Provider::new);
    }

    private static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        private Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            return new TobaccoSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}

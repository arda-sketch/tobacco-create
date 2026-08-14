package com.createtobacco.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Persistent per-stack state for cigarettes and cigars. */
public record SmokingItemState(int remainingPuffs, boolean lit, int burnTicksRemaining) {
    public static final int MAX_SERIALIZED_PUFFS = 64;
    public static final int MAX_SERIALIZED_BURN_TICKS = 20 * 60 * 30;

    public static final Codec<SmokingItemState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, MAX_SERIALIZED_PUFFS).fieldOf("remaining_puffs").forGetter(SmokingItemState::remainingPuffs),
            Codec.BOOL.fieldOf("lit").forGetter(SmokingItemState::lit),
            Codec.intRange(0, MAX_SERIALIZED_BURN_TICKS).optionalFieldOf("burn_ticks", 0)
                    .forGetter(SmokingItemState::burnTicksRemaining)
    ).apply(instance, SmokingItemState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SmokingItemState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SmokingItemState::remainingPuffs,
            ByteBufCodecs.BOOL,
            SmokingItemState::lit,
            ByteBufCodecs.VAR_INT,
            SmokingItemState::burnTicksRemaining,
            SmokingItemState::new
    );

    public SmokingItemState {
        if (remainingPuffs < 0 || remainingPuffs > MAX_SERIALIZED_PUFFS) {
            throw new IllegalArgumentException("remainingPuffs must be in range 0.." + MAX_SERIALIZED_PUFFS);
        }
        if (burnTicksRemaining < 0 || burnTicksRemaining > MAX_SERIALIZED_BURN_TICKS) {
            throw new IllegalArgumentException("burnTicksRemaining must be in range 0.." + MAX_SERIALIZED_BURN_TICKS);
        }
    }

    public static SmokingItemState cigarette() {
        return new SmokingItemState(5, false, 0);
    }

    public static SmokingItemState cigar() {
        return new SmokingItemState(8, false, 0);
    }

    public SmokingItemState ignite(int burnIntervalTicks) {
        return new SmokingItemState(remainingPuffs, true, burnIntervalTicks);
    }

    public SmokingItemState afterPuff(int puffs, int burnIntervalTicks) {
        return new SmokingItemState(puffs, true, burnIntervalTicks);
    }

    public SmokingItemState withRemainingPuffs(int puffs) {
        return new SmokingItemState(puffs, lit, burnTicksRemaining);
    }

    public SmokingItemState withLit(boolean isLit) {
        return new SmokingItemState(remainingPuffs, isLit, isLit ? burnTicksRemaining : 0);
    }

    public SmokingItemState withBurnTicksRemaining(int ticks) {
        return new SmokingItemState(remainingPuffs, lit, ticks);
    }
}

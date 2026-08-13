package com.createtobacco.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SmokingItemState(int remainingPuffs, boolean lit) {
    public static final Codec<SmokingItemState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 64).fieldOf("remaining_puffs").forGetter(SmokingItemState::remainingPuffs),
            Codec.BOOL.fieldOf("lit").forGetter(SmokingItemState::lit)
    ).apply(instance, SmokingItemState::new));

    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, SmokingItemState> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    SmokingItemState::remainingPuffs,
                    ByteBufCodecs.BOOL,
                    SmokingItemState::lit,
                    SmokingItemState::new
            );

    public SmokingItemState {
        if (remainingPuffs < 0) {
            throw new IllegalArgumentException("remainingPuffs must not be negative");
        }
    }

    public static SmokingItemState cigarette() {
        return new SmokingItemState(5, false);
    }

    public static SmokingItemState cigar() {
        return new SmokingItemState(8, false);
    }

    public SmokingItemState withRemainingPuffs(int puffs) {
        return new SmokingItemState(puffs, lit);
    }

    public SmokingItemState withLit(boolean isLit) {
        return new SmokingItemState(remainingPuffs, isLit);
    }
}

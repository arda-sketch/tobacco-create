package com.createtobacco.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Remaining cigarettes in a brand-specific pack. */
public record CigarettePackState(int count) {
    public static final int CAPACITY = 10;

    public static final Codec<CigarettePackState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, CAPACITY).fieldOf("count").forGetter(CigarettePackState::count)
    ).apply(instance, CigarettePackState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CigarettePackState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            CigarettePackState::count,
            CigarettePackState::new
    );

    public CigarettePackState {
        if (count < 1 || count > CAPACITY) {
            throw new IllegalArgumentException("Cigarette pack count must be 1.." + CAPACITY);
        }
    }

    public static CigarettePackState full() {
        return new CigarettePackState(CAPACITY);
    }

    public CigarettePackState withCount(int newCount) {
        return new CigarettePackState(newCount);
    }
}

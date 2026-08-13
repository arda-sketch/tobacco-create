package com.createtobacco.component;

import com.createtobacco.smoking.SmokingProduct;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record CigarettePackContents(ResourceLocation cigaretteId, int count) {
    public static final int CAPACITY = 10;

    private static final Codec<ResourceLocation> CIGARETTE_ID_CODEC = ResourceLocation.CODEC.validate(id ->
            SmokingProduct.isPackableCigaretteId(id)
                    ? DataResult.success(id)
                    : DataResult.error(() -> "Not an allowed Create Tobacco cigarette: " + id));

    public static final Codec<CigarettePackContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CIGARETTE_ID_CODEC.fieldOf("cigarette").forGetter(CigarettePackContents::cigaretteId),
            Codec.intRange(1, CAPACITY).fieldOf("count").forGetter(CigarettePackContents::count)
    ).apply(instance, CigarettePackContents::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CigarettePackContents> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            CigarettePackContents::cigaretteId,
            ByteBufCodecs.VAR_INT,
            CigarettePackContents::count,
            CigarettePackContents::new
    );

    public CigarettePackContents {
        if (!SmokingProduct.isPackableCigaretteId(cigaretteId)) {
            throw new IllegalArgumentException("Not an allowed finished cigarette: " + cigaretteId);
        }
        if (count < 1 || count > CAPACITY) {
            throw new IllegalArgumentException("Cigarette pack count must be 1.." + CAPACITY);
        }
    }

    public CigarettePackContents withCount(int newCount) {
        return new CigarettePackContents(cigaretteId, newCount);
    }
}

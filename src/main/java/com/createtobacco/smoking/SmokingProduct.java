package com.createtobacco.smoking;

import com.createtobacco.CreateTobacco;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;

public enum SmokingProduct {
    MARLBORE_RED("marlbore_red", true),
    WINSTONE_BLUE("winstone_blue", true),
    CREPERFIELD("creperfield", true),
    CRAFTMEL("craftmel", true),
    CHUNKMAN("chunkman", true),
    KEND("kend", true),
    PIGLIAMENT("pigliament", true),
    ROTHMINES("rothmines", true),
    BEDROMORKANAL("bedromorkanal", true),
    MINECRISTO_NO_1("minecristo_no_1", false),
    STONEO_Y_GLOWLIETA("stoneo_y_glowlieta", false);

    private final ResourceLocation itemId;
    private final boolean packableCigarette;

    SmokingProduct(String path, boolean packableCigarette) {
        this.itemId = ResourceLocation.fromNamespaceAndPath(CreateTobacco.MOD_ID, path);
        this.packableCigarette = packableCigarette;
    }

    public ResourceLocation itemId() {
        return itemId;
    }

    public boolean isPackableCigarette() {
        return packableCigarette;
    }

    public boolean isActiveSmokingProduct() {
        return true;
    }

    public static Optional<SmokingProduct> fromId(ResourceLocation id) {
        return Arrays.stream(values()).filter(product -> product.itemId.equals(id)).findFirst();
    }

    public static Optional<SmokingProduct> fromItem(Item item) {
        return fromId(BuiltInRegistries.ITEM.getKey(item));
    }

    public static boolean isPackableCigaretteId(ResourceLocation id) {
        return fromId(id).map(SmokingProduct::isPackableCigarette).orElse(false);
    }
}

package com.createtobacco.client;

import com.createtobacco.CreateTobacco;
import com.createtobacco.component.SmokingItemState;
import com.createtobacco.registry.ModDataComponents;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

/** Client-only item model properties shared by smoking products. */
public final class ModItemProperties {
    private static final ResourceLocation LIT =
            ResourceLocation.fromNamespaceAndPath(CreateTobacco.MOD_ID, "lit");

    private ModItemProperties() {
    }

    public static void register() {
        ItemProperties.registerGeneric(
                LIT,
                (stack, level, entity, seed) -> {
                    SmokingItemState state = stack.get(ModDataComponents.SMOKING_ITEM_STATE.get());
                    return state != null && state.lit() ? 1.0F : 0.0F;
                }
        );
    }
}

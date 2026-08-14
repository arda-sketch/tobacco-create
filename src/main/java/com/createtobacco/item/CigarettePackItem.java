package com.createtobacco.item;

import com.createtobacco.component.CigarettePackState;
import com.createtobacco.registry.ModCriteriaTriggers;
import com.createtobacco.registry.ModDataComponents;
import com.createtobacco.registry.ModItems;
import com.createtobacco.smoking.SmokingProduct;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * A pack is a real brand-specific item. Only the remaining count is dynamic;
 * the contained cigarette type is defined by the item itself.
 */
public final class CigarettePackItem extends Item {
    private final SmokingProduct product;

    public CigarettePackItem(Properties properties, SmokingProduct product) {
        super(properties.stacksTo(1).component(ModDataComponents.CIGARETTE_PACK_STATE.get(), CigarettePackState.full()));
        if (!product.isPackableCigarette()) {
            throw new IllegalArgumentException("Only cigarettes can have packs: " + product);
        }
        this.product = product;
    }

    public SmokingProduct product() {
        return product;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player) || player.tickCount % 20 != 0) {
            return;
        }

        CigarettePackState state = stack.get(ModDataComponents.CIGARETTE_PACK_STATE);
        if (state != null && state.count() == CigarettePackState.CAPACITY) {
            ModCriteriaTriggers.FULL_CIGARETTE_PACK.get().trigger(player);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack pack = player.getItemInHand(usedHand);
        CigarettePackState state = pack.get(ModDataComponents.CIGARETTE_PACK_STATE);
        if (state == null) {
            return InteractionResultHolder.fail(pack);
        }

        if (!level.isClientSide()) {
            Item cigarette = BuiltInRegistries.ITEM.get(product.itemId());
            ItemStack extracted = new ItemStack(cigarette);
            if (!player.getInventory().add(extracted) || !extracted.isEmpty()) {
                return InteractionResultHolder.fail(pack);
            }

            if (state.count() == 1) {
                ItemStack emptyPack = new ItemStack(ModItems.EMPTY_CIGARETTE_PACK.get());
                player.setItemInHand(usedHand, emptyPack);
                return InteractionResultHolder.success(emptyPack);
            }

            pack.set(ModDataComponents.CIGARETTE_PACK_STATE.get(), state.withCount(state.count() - 1));
        }

        return InteractionResultHolder.sidedSuccess(pack, level.isClientSide());
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        CigarettePackState state = stack.get(ModDataComponents.CIGARETTE_PACK_STATE);
        if (state == null) {
            tooltipComponents.add(Component.translatable("tooltip.create_tobacco.invalid_pack"));
            return;
        }

        tooltipComponents.add(Component.translatable(
                "tooltip.create_tobacco.pack_count",
                state.count(),
                CigarettePackState.CAPACITY
        ));
    }
}

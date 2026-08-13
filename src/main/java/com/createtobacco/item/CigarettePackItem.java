package com.createtobacco.item;

import com.createtobacco.component.CigarettePackContents;
import com.createtobacco.registry.ModDataComponents;
import com.createtobacco.registry.ModItems;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class CigarettePackItem extends Item {
    public CigarettePackItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack pack = player.getItemInHand(usedHand);
        CigarettePackContents contents = pack.get(ModDataComponents.CIGARETTE_PACK_CONTENTS);
        if (contents == null) {
            return InteractionResultHolder.fail(pack);
        }

        if (!level.isClientSide()) {
            Item cigarette = BuiltInRegistries.ITEM.get(contents.cigaretteId());
            ItemStack extracted = new ItemStack(cigarette);
            if (!player.getInventory().add(extracted) || !extracted.isEmpty()) {
                return InteractionResultHolder.fail(pack);
            }

            if (contents.count() == 1) {
                ItemStack emptyPack = new ItemStack(ModItems.EMPTY_CIGARETTE_PACK.get());
                player.setItemInHand(usedHand, emptyPack);
                return InteractionResultHolder.success(emptyPack);
            }

            pack.set(ModDataComponents.CIGARETTE_PACK_CONTENTS, contents.withCount(contents.count() - 1));
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
        CigarettePackContents contents = stack.get(ModDataComponents.CIGARETTE_PACK_CONTENTS);
        if (contents == null) {
            tooltipComponents.add(Component.translatable("tooltip.create_tobacco.invalid_pack"));
            return;
        }

        Item cigarette = BuiltInRegistries.ITEM.get(contents.cigaretteId());
        tooltipComponents.add(cigarette.getDescription());
        tooltipComponents.add(Component.translatable(
                "tooltip.create_tobacco.pack_count",
                contents.count(),
                CigarettePackContents.CAPACITY
        ));
    }
}

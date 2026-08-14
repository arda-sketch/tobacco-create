package com.createtobacco.item;

import com.createtobacco.menu.CigaretteCaseMenu;
import com.createtobacco.smoking.SmokingProduct;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

/** Portable 15-slot case which accepts only active cigarettes and cigars. */
public final class CigaretteCaseItem extends Item {
    public CigaretteCaseItem(Properties properties) {
        super(properties.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    }

    public static boolean isAllowedContent(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof AbstractSmokingItem
                && SmokingProduct.fromItem(stack.getItem()).map(SmokingProduct::isActiveSmokingProduct).orElse(false);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            boolean mainHand = usedHand == InteractionHand.MAIN_HAND;
            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (containerId, inventory, menuPlayer) ->
                                    new CigaretteCaseMenu(containerId, inventory, stack, usedHand),
                            Component.translatable("container.create_tobacco.cigarette_case")
                    ),
                    buffer -> buffer.writeBoolean(mainHand)
            );
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}

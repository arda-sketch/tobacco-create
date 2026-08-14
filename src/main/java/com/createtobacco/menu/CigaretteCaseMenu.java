package com.createtobacco.menu;

import com.createtobacco.item.CigaretteCaseContainer;
import com.createtobacco.item.CigaretteCaseItem;
import com.createtobacco.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class CigaretteCaseMenu extends AbstractContainerMenu {
    public static final int CASE_SLOTS = CigaretteCaseContainer.SIZE;
    private static final int PLAYER_INVENTORY_START = CASE_SLOTS;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final Container caseContainer;
    private final ItemStack sourceCaseStack;
    private final InteractionHand sourceHand;

    /** Client constructor used by IContainerFactory. */
    public CigaretteCaseMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, new SimpleContainer(CASE_SLOTS), ItemStack.EMPTY,
                buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    /** Server constructor used when the item is opened. */
    public CigaretteCaseMenu(int containerId, Inventory playerInventory, ItemStack caseStack, InteractionHand sourceHand) {
        this(containerId, playerInventory, new CigaretteCaseContainer(caseStack), caseStack, sourceHand);
    }

    private CigaretteCaseMenu(
            int containerId,
            Inventory playerInventory,
            Container caseContainer,
            ItemStack sourceCaseStack,
            InteractionHand sourceHand
    ) {
        super(ModMenus.CIGARETTE_CASE.get(), containerId);
        checkContainerSize(caseContainer, CASE_SLOTS);
        this.caseContainer = caseContainer;
        this.sourceCaseStack = sourceCaseStack;
        this.sourceHand = sourceHand;

        // 5 x 3 case slots, centered above the normal player inventory.
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 5; column++) {
                int index = column + row * 5;
                addSlot(new CopyingCaseSlot(caseContainer, index, 44 + column * 18, 18 + row * 18));
            }
        }

        // Player inventory.
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 86 + row * 18));
            }
        }

        // Hotbar. Lock the source slot while a main-hand case is open so the
        // storage item cannot be moved out from under its own menu.
        for (int column = 0; column < 9; column++) {
            int index = column;
            if (sourceHand == InteractionHand.MAIN_HAND && index == playerInventory.selected) {
                addSlot(new LockedSourceSlot(playerInventory, index, 8 + column * 18, 144));
            } else {
                addSlot(new Slot(playerInventory, index, 8 + column * 18, 144));
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (sourceCaseStack.isEmpty()) {
            return true; // Client-side dummy menu.
        }
        ItemStack current = player.getItemInHand(sourceHand);
        return current == sourceCaseStack
                || (!current.isEmpty() && current.getItem() instanceof CigaretteCaseItem
                && ItemStack.isSameItemSameComponents(current, sourceCaseStack));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack source = slot.getItem();
        ItemStack original = source.copy();

        if (slotIndex < CASE_SLOTS) {
            if (!moveItemStackTo(source, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (CigaretteCaseItem.isAllowedContent(source)) {
            if (!moveItemStackTo(source, 0, CASE_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
        } else if (slotIndex < PLAYER_INVENTORY_END) {
            if (!moveItemStackTo(source, HOTBAR_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(source, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
            return ItemStack.EMPTY;
        }

        if (source.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, source);
        return original;
    }

    private static final class CopyingCaseSlot extends Slot {
        private CopyingCaseSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return CigaretteCaseItem.isAllowedContent(stack);
        }

        @Override
        public void set(ItemStack stack) {
            super.set(stack.copy());
        }

        @Override
        public ItemStack remove(int amount) {
            return super.remove(amount).copy();
        }
    }

    private static final class LockedSourceSlot extends Slot {
        private LockedSourceSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}

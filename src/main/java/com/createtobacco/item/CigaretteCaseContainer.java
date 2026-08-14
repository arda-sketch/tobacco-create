package com.createtobacco.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

/** ItemStack-backed 15-slot inventory for the cigarette case. */
public final class CigaretteCaseContainer extends SimpleContainer {
    public static final int SIZE = 15;
    private final ItemStack caseStack;

    public CigaretteCaseContainer(ItemStack caseStack) {
        super(SIZE);
        this.caseStack = caseStack;
        caseStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(getItems());
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return CigaretteCaseItem.isAllowedContent(stack);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        caseStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(getItems()));
    }
}

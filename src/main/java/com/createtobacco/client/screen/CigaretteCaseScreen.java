package com.createtobacco.client.screen;

import com.createtobacco.menu.CigaretteCaseMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Lightweight screen that does not require a custom GUI texture. */
public final class CigaretteCaseScreen extends AbstractContainerScreen<CigaretteCaseMenu> {
    private static final int PANEL = 0xFF2B261F;
    private static final int PANEL_INNER = 0xFF4A4034;
    private static final int SLOT_BORDER = 0xFF1A1713;
    private static final int SLOT_FILL = 0xFF6A5B48;

    public CigaretteCaseScreen(CigaretteCaseMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 168;
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = 74;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, PANEL);
        graphics.fill(x + 4, y + 14, x + imageWidth - 4, y + imageHeight - 4, PANEL_INNER);

        // 15 case slots.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                drawSlot(graphics, x + 43 + col * 18, y + 17 + row * 18);
            }
        }

        // Player inventory + hotbar slots.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlot(graphics, x + 7 + col * 18, y + 85 + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawSlot(graphics, x + 7 + col * 18, y + 143);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int labelColor = 0xFFE8D8B7;
        graphics.drawString(font, title, titleLabelX, titleLabelY, labelColor, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, labelColor, false);
    }

    private static void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, SLOT_BORDER);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT_FILL);
    }
}

package net.tomocraft.mixins.client.gui;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.tomocraft.talismancounter.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Mixin(value = GuiChest.class, priority = 9999)
public abstract class MixinGuiChest extends GuiContainer {

    @Shadow
    private IInventory lowerChestInventory;

    private final int DEFAULT_BLUE_COLOR = new Color(160, 225, 229, 255).getRGB();

    // Never be used
    public MixinGuiChest(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Compute the count of talismans
        EnumMap<Rarity, Integer> talismans = new EnumMap<>(Rarity.class);

        for (int i = 0; i < this.lowerChestInventory.getSizeInventory(); i++) {
            ItemStack item = this.lowerChestInventory.getStackInSlot(i);
            if (item == null) continue;

            List<String> tooltip = item.getTooltip(null, false);
            if (tooltip.size() == 0) continue;
            String[] itemType = StringUtils.stripControlCodes(tooltip.get(tooltip.size() - 1)).split(" ");
            if (itemType.length != 2 || !itemType[1].equals("ACCESSORY")) {
                continue;
            }
            Rarity rarity = Rarity.valueOf(itemType[0]);// COMMON ACCESSORY -> COMMON -> Enum.valueOf("COMMON")

            int def = 0;
            if (talismans.containsKey(rarity)) {
                def = talismans.get(rarity);
            }
            talismans.put(rarity, def + 1);
        }

        GlStateManager.pushMatrix();
        float scale = 0.75F;
        GlStateManager.scale(scale, scale, 1);
        int x = this.guiLeft - 160;
        if (x < 0) {
            x = 20;
        }
        GlStateManager.color(1F, 1F, 1F);
        int i = 0;
        for (Map.Entry<Rarity, Integer> entry : talismans.entrySet()) {
            Rarity rarity = entry.getKey();
            int count = entry.getValue();
            mc.ingameGUI.drawString(mc.fontRendererObj, rarity + ": " + count, Math.round(x / scale), Math.round((guiTop + (40 + (i * 10))) / scale), DEFAULT_BLUE_COLOR);
            i++;
        }
        GlStateManager.popMatrix();
    }
}

package screret.sas.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import screret.sas.Util;
import screret.sas.container.container.PotionDistilleryMenu;

public class PotionDistilleryScreen extends AbstractContainerScreen<PotionDistilleryMenu> {
    private static final ResourceLocation TEXTURE_LOCATION = Util.resource("textures/gui/container/potion_distillery.png");

    public PotionDistilleryScreen(PotionDistilleryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);

        this.blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.isLit()) {
            int fuelAmount = this.menu.getLitProgress();
            this.blit(pPoseStack, this.leftPos + 56 + PotionDistilleryMenu.FUEL_PROGRESS_BAR_X_SIZE - 1 - fuelAmount, this.topPos + 44, PotionDistilleryMenu.FUEL_PROGRESS_BAR_X_SIZE - 1 - fuelAmount, 39, fuelAmount + 1, 4);
        }

        int progress = this.menu.getBurnProgress();
        this.blit(pPoseStack, this.leftPos + 97, this.topPos + 16, 176, 0, 9, progress + 1);
    }
}
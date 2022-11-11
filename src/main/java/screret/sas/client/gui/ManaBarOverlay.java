package screret.sas.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.api.capability.mana.ManaProvider;
import screret.sas.config.SASConfig;

public class ManaBarOverlay implements IGuiOverlay {

    public static final ResourceLocation MANA_BAR_LOCATION = Util.resource("textures/gui/mana_bar.png");

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        RenderSystem.setShaderTexture(0, MANA_BAR_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        if (gui.shouldDrawSurvivalElements() && gui.getMinecraft().player != null)
        {
            gui.getMinecraft().getProfiler().push("manaBar");

            if(gui.getMinecraft().player.getCapability(ManaProvider.MANA).isPresent()){
                var capability = gui.getMinecraft().player.getCapability(ManaProvider.MANA).resolve().get();
                int left = screenWidth / 2 + SASConfig.Client.manaBarX.get();
                int top = screenHeight - SASConfig.Client.manaBarY.get();

                int progress = (int) ((capability.getManaStored() / (float)capability.getMaxManaStored()) * 80);
                gui.blit(poseStack, left, top, 0, 0, 80, 5);
                if (progress > 0) {
                    gui.blit(poseStack, left, top, 0, 5, progress, 5);
                }

                gui.getMinecraft().getProfiler().pop();
            }

        }
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}

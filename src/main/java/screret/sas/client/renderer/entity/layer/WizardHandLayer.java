package screret.sas.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import screret.sas.Util;
import screret.sas.entity.entity.WizardEntity;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class WizardHandLayer extends GeoLayerRenderer<WizardEntity> {
    private static final ResourceLocation LAYER = Util.resource("textures/entity/wizard/coat.png");
    private static final ResourceLocation MODEL = Util.resource("geo/wizard.geo.json");

    private final ItemInHandRenderer itemInHandRenderer;

    public WizardHandLayer(IGeoRenderer<WizardEntity> entityRendererIn, EntityRendererProvider.Context renderManager) {
        super(entityRendererIn);
        this.itemInHandRenderer = renderManager.getItemInHandRenderer();
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn, WizardEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStack.pushPose();

        matrixStack.translate(0.06F, 0.27F, -0.5D);

        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));

        ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.MAINHAND);
        //this.itemInHandRenderer.renderItem(pLivingEntity, itemstack, ItemTransforms.TransformType.GROUND, false, matrixStack, pBuffer, pPackedLight);
        matrixStack.popPose();
    }
}

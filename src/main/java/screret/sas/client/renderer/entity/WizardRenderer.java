package screret.sas.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import screret.sas.Util;
import screret.sas.client.model.entity.WizardModel;
import screret.sas.entity.entity.WizardEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WizardRenderer extends GeoEntityRenderer<WizardEntity> {
    public WizardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WizardModel());

        //addLayer(new WizardHandLayer(this, renderManager));
    }

    @Override
    public RenderType getRenderType(WizardEntity animatable, float partialTick, PoseStack poseStack,
                                    MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight,
                                    ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(GeoModel model, WizardEntity animatable, float partialTick, RenderType type,
                       PoseStack poseStack, MultiBufferSource bufferSource, VertexConsumer buffer,
                       int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if(model.getBone("rightArm").isPresent() && Util.getMainAbilityFromStack(animatable.getMainHandItem()).isPresent()){
            var handWorldPos = model.getBone("rightArm").get().getWorldPosition();
            if(animatable.isCastingSpell()){
                animatable.getCommandSenderWorld().addParticle(Util.getMainAbilityFromStack(animatable.getMainHandItem()).get().getAbility().getParticle(),
                        handWorldPos.x,
                        handWorldPos.y,
                        handWorldPos.z,
                        (animatable.getRandom().nextDouble() - 0.5D), -animatable.getRandom().nextDouble(),
                        (animatable.getRandom().nextDouble() - 0.5D));
            }
            poseStack.pushPose();
            poseStack.translate(handWorldPos.x, handWorldPos.y, handWorldPos.z);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(model.getBone("rightArm").get().getRotationX()));
            Minecraft.getInstance().getItemRenderer().renderStatic(animatable.getMainHandItem(), ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, packedOverlay, poseStack, bufferSource, 0);
            poseStack.popPose();
        }
        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

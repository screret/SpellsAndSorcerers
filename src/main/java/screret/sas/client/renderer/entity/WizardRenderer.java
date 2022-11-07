package screret.sas.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import screret.sas.Util;
import screret.sas.client.model.entity.WizardModel;
import screret.sas.client.renderer.entity.layer.WizardCoatLayer;
import screret.sas.entity.entity.WizardMob;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class WizardRenderer extends GeoEntityRenderer<WizardMob> {
    private int currentTick = -1;

    public WizardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WizardModel());

        //addLayer(new WizardCoatLayer(this));
    }

    @Override
    public RenderType getRenderType(WizardMob animatable, float partialTick, PoseStack poseStack,
                                    MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight,
                                    ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(GeoModel model, WizardMob animatable, float partialTick, RenderType type,
                       PoseStack poseStack, MultiBufferSource bufferSource, VertexConsumer buffer,
                       int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (currentTick < 0 || currentTick != animatable.tickCount) {
            this.currentTick = animatable.tickCount;
            if(animatable.isCastingSpell()){
                if(model.getBone("rightArm").isPresent() && Util.getMainAbilityFromStack(animatable.getMainHandItem()).isPresent()){
                    var handWorldPos = model.getBone("rightArm").get().getWorldPosition();
                    animatable.getCommandSenderWorld().addParticle(Util.getMainAbilityFromStack(animatable.getMainHandItem()).get().getAbility().getParticle(),
                            handWorldPos.x,
                            handWorldPos.y,
                            handWorldPos.z,
                            (animatable.getRandom().nextDouble() - 0.5D), -animatable.getRandom().nextDouble(),
                            (animatable.getRandom().nextDouble() - 0.5D));
                }
            }
        }
        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

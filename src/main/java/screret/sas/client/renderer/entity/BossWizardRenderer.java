package screret.sas.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import screret.sas.Util;
import screret.sas.client.model.entity.BossWizardModel;
import screret.sas.client.model.entity.WizardModel;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.entity.entity.WizardEntity;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BossWizardRenderer extends GeoEntityRenderer<BossWizardEntity> {
    public BossWizardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BossWizardModel());

        //addLayer(new WizardHandLayer(this, renderManager));
    }

    @Override
    public RenderType getRenderType(BossWizardEntity animatable, float partialTick, PoseStack poseStack,
                                    MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight,
                                    ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(GeoModel model, BossWizardEntity animatable, float partialTick, RenderType type,
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
        }
        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

package screret.sas.client.model.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import screret.sas.Util;
import screret.sas.entity.entity.boss.cthulhu.CthulhuEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class CthulhuModel extends AnimatedGeoModel<CthulhuEntity> {
    private static final ResourceLocation MODEL_RESOURCE = Util.resource("geo/boss_wizard.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = Util.resource("textures/entity/wizard/boss_wizard.png");
    private static final ResourceLocation ANIMATION_RESOURCE = Util.resource("animations/boss_wizard.animation.json");

    @Override
    public ResourceLocation getModelResource(CthulhuEntity object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(CthulhuEntity object) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(CthulhuEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void setCustomAnimations(CthulhuEntity animatable, int instanceId, AnimationEvent event) {
        super.setCustomAnimations(animatable, instanceId, event);

        if(animatable.getInvulnerableTicks() <= 0){
            IBone head = this.getAnimationProcessor().getBone("head");

            EntityModelData extraData = (EntityModelData) event.getExtraDataOfType(EntityModelData.class).get(0);

            AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
            int unpausedMultiplier = !Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused ? 1 : 0;

            head.setRotationX(head.getRotationX() + extraData.headPitch * Mth.DEG_TO_RAD * unpausedMultiplier);
            head.setRotationY(head.getRotationY() + extraData.netHeadYaw * Mth.DEG_TO_RAD * unpausedMultiplier);
        }
    }
}

package screret.sas.client.model.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import screret.sas.Util;
import screret.sas.entity.entity.WizardEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class WizardModel extends AnimatedGeoModel<WizardEntity> {
    private static final ResourceLocation MODEL_RESOURCE = Util.resource("geo/wizard.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = Util.resource("textures/entity/wizard/wizard.png");
    private static final ResourceLocation ANIMATION_RESOURCE = Util.resource("animations/wizard.animation.json");

    @Override
    public ResourceLocation getModelResource(WizardEntity object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(WizardEntity object) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(WizardEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void setCustomAnimations(WizardEntity animatable, int instanceId, AnimationEvent event) {
        super.setCustomAnimations(animatable, instanceId, event);

        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) event.getExtraDataOfType(EntityModelData.class).get(0);

        AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
        int unpausedMultiplier = !Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused ? 1 : 0;

        head.setRotationX(head.getRotationX() + extraData.headPitch * Mth.HALF_PI * unpausedMultiplier);
        head.setRotationY(head.getRotationY() + extraData.netHeadYaw * Mth.HALF_PI * unpausedMultiplier);
    }
}

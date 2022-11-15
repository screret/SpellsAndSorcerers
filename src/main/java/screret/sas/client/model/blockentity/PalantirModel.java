package screret.sas.client.model.blockentity;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import screret.sas.Util;
import screret.sas.blockentity.blockentity.PalantirBE;
import screret.sas.blockentity.blockentity.SummonSignBE;
import screret.sas.entity.entity.WizardEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class PalantirModel extends AnimatedGeoModel<PalantirBE> {
    private static final ResourceLocation MODEL_RESOURCE = Util.resource("geo/palantir.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = Util.resource("textures/block/palantir.png");
    private static final ResourceLocation ANIMATION_RESOURCE = Util.resource("animations/palantir.animation.json");

    @Override
    public ResourceLocation getModelResource(PalantirBE object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(PalantirBE object) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(PalantirBE animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void setCustomAnimations(PalantirBE animatable, int instanceId, AnimationEvent event) {
        super.setCustomAnimations(animatable, instanceId, event);

        IBone eye = this.getAnimationProcessor().getBone("eye");
        eye.setRotationX(animatable.xRot * Mth.DEG_TO_RAD);
        eye.setRotationY(animatable.yRot * Mth.DEG_TO_RAD);
    }
}

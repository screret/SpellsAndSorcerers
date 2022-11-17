package screret.sas.client.model.blockentity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import screret.sas.Util;
import screret.sas.blockentity.blockentity.PalantirBE;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PalantirModel extends AnimatedGeoModel {
    private static final ResourceLocation MODEL_RESOURCE = Util.resource("geo/palantir.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = Util.resource("textures/block/palantir.png");
    private static final ResourceLocation ANIMATION_RESOURCE = Util.resource("animations/palantir.animation.json");

    @Override
    public ResourceLocation getModelResource(Object object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(Object object) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(Object animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void setCustomAnimations(IAnimatable animatable, int instanceId, AnimationEvent event) {
        super.setCustomAnimations(animatable, instanceId, event);

        if(animatable instanceof PalantirBE blockEntity){
            IBone eye = this.getAnimationProcessor().getBone("eye");
            eye.setRotationX(blockEntity.xRot * Mth.DEG_TO_RAD);
            eye.setRotationY(blockEntity.yRot * Mth.DEG_TO_RAD);
        }

    }
}

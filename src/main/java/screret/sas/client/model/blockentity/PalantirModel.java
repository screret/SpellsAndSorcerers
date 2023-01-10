package screret.sas.client.model.blockentity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import screret.sas.Util;
import screret.sas.blockentity.blockentity.PalantirBE;
import software.bernie.example.block.entity.GeckoHabitatBlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PalantirModel extends DefaultedBlockGeoModel<PalantirBE> {

    public PalantirModel() {
        super(Util.resource("palantir"));
    }

    @Override
    public RenderType getRenderType(PalantirBE animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public void setCustomAnimations(PalantirBE animatable, long instanceId, AnimationState<PalantirBE> animationState) {
        CoreGeoBone eye = this.getAnimationProcessor().getBone("eye");
        eye.setRotX(animatable.xRot * Mth.DEG_TO_RAD);
        eye.setRotY(animatable.yRot * Mth.DEG_TO_RAD);
    }
}

package screret.sas.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import screret.sas.Util;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.entity.entity.WizardEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BossWizardModel extends AnimatedGeoModel<BossWizardEntity> {
    private static final ResourceLocation MODEL_RESOURCE = Util.resource("geo/boss_wizard.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = Util.resource("textures/entity/wizard/boss_wizard.png");
    private static final ResourceLocation ANIMATION_RESOURCE = Util.resource("animations/boss_wizard.animation.json");

    @Override
    public ResourceLocation getModelResource(BossWizardEntity object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(BossWizardEntity object) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(BossWizardEntity animatable) {
        return ANIMATION_RESOURCE;
    }
}

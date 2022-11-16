package screret.sas.client.model.armor;

import net.minecraft.resources.ResourceLocation;
import screret.sas.Util;
import screret.sas.item.item.ModArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SoulsteelArmorModel extends AnimatedGeoModel<ModArmorItem> {
    private static final ResourceLocation MODEL_RESOURCE = Util.resource("geo/soulsteel_armor.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = Util.resource("textures/entity/armor/soulsteel_armor.png");
    private static final ResourceLocation ANIMATION_RESOURCE = Util.resource("animations/soulsteel_armor.animation.json");

    @Override
    public ResourceLocation getModelResource(ModArmorItem object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(ModArmorItem object) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(ModArmorItem animatable) {
        return ANIMATION_RESOURCE;
    }
}

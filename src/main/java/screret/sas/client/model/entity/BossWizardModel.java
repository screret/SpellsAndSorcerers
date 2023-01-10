package screret.sas.client.model.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import screret.sas.Util;
import screret.sas.blockentity.blockentity.PalantirBE;
import screret.sas.entity.entity.BossWizardEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BossWizardModel extends DefaultedEntityGeoModel<BossWizardEntity> {
    public BossWizardModel() {
        super(Util.resource("boss_wizard"), true);
    }
}

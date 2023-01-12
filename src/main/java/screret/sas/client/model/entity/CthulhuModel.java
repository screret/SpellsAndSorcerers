package screret.sas.client.model.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import screret.sas.Util;
import screret.sas.entity.entity.boss.cthulhu.CthulhuEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CthulhuModel extends DefaultedEntityGeoModel<CthulhuEntity> {
    public CthulhuModel() {
        super(Util.resource("cthulhu"), true);
    }
}

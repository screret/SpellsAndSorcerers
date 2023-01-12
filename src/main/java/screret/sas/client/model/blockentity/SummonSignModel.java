package screret.sas.client.model.blockentity;

import net.minecraft.resources.ResourceLocation;
import screret.sas.Util;
import screret.sas.blockentity.blockentity.SummonSignBE;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class SummonSignModel extends DefaultedBlockGeoModel<SummonSignBE> {

    public SummonSignModel() {
        super(Util.resource("summon_sign"));
    }
}

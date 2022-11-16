package screret.sas.client.renderer.armor;

import screret.sas.client.model.armor.SoulsteelArmorModel;
import screret.sas.item.item.ModArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class SoulsteelArmorRenderer extends GeoArmorRenderer<ModArmorItem> {
    public SoulsteelArmorRenderer() {
        super(new SoulsteelArmorModel());
    }
}

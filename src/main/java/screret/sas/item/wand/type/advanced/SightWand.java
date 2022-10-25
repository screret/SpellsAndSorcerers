package screret.sas.item.wand.type.advanced;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import screret.sas.item.wand.Wand;
import screret.sas.item.wand.power.IIsHoldable;
import screret.sas.item.wand.power.IIsChargeable;

public class SightWand extends Wand implements IIsHoldable, IIsChargeable {

    public SightWand() {
        super(25, Rarity.RARE, 1200, 0);
    }

    @Override
    public void fire(Level level, LivingEntity user, ItemStack wand, int timeCharged) {

    }

    @Override
    public int getChargeTime() {
        return 0;
    }

    @Override
    public int getHoldTime() {
        return 0;
    }
}

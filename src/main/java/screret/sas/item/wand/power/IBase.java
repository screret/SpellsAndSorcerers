package screret.sas.item.wand.power;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IBase {

    void fire(Level level, LivingEntity user, ItemStack wand, int timeCharged);

    int getCooldown();

    float getDamagePerHit(ItemStack stack);
}

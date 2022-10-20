package screret.sas.items.wands.powers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IBase {

    void shoot(ItemStack currentWand, Level level, Player player);
}

package screret.sas.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import screret.sas.SpellsAndSorcerers;

public class ModCreativeTab extends CreativeModeTab {

    public ModCreativeTab() {
        super(SpellsAndSorcerers.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ModItems.RAY_WAND.get());
    }
}

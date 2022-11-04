package screret.sas.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.ability.ModWandAbilities;
import screret.sas.api.wand.ability.WandAbilityInstance;

import static screret.sas.Util.addWand;

public class ModCreativeTab extends CreativeModeTab {

    public ModCreativeTab() {
        super(SpellsAndSorcerers.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        return addWand(new WandAbilityInstance(ModWandAbilities.SHOOT_RAY.get(), new WandAbilityInstance(ModWandAbilities.DAMAGE.get())), null);
    }

    public void fillItemList(NonNullList<ItemStack> items) {
        Util.addItems();
        items.addAll(Util.customWands.values());
        items.addAll(Util.customWandCores.values());

        for(Item item : ForgeRegistries.ITEMS) {
            item.fillItemCategory(this, items);
        }
    }
}

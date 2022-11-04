package screret.sas.item.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import screret.sas.api.capability.ability.WandAbilityProvider;
import screret.sas.api.wand.ability.WandAbilityRegistry;

public class WandCoreItem extends Item {

    public WandCoreItem() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = "item.sas.basic";
        if(stack.hasTag() && stack.getTag().contains("ability")){

            name = "ability.sas." + WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValue(new ResourceLocation(stack.getTag().getString("ability"))).getKey().getPath();
        }
        return Component.translatable(super.getDescriptionId(stack), Component.translatable(name));
    }
}

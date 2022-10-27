package screret.sas.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import screret.sas.SpellsAndSorcerers;
import screret.sas.ability.ModWandAbilities;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.api.wand.ability.WandAbilityRegistry;

public class ModCreativeTab extends CreativeModeTab {

    public ModCreativeTab() {
        super(SpellsAndSorcerers.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ModItems.WAND.get());
    }

    public void fillItemList(NonNullList<ItemStack> items) {
        items.add(addItem(new WandAbilityInstance(ModWandAbilities.SHOOT_RAY.get(), new WandAbilityInstance(ModWandAbilities.DAMAGE.get())), null));
        items.add(addItem(new WandAbilityInstance(ModWandAbilities.SHOOT_HOLD_DOWN.get(), new WandAbilityInstance(ModWandAbilities.HEAL.get())), new WandAbilityInstance(ModWandAbilities.HEAL_SELF.get())));
        items.add(addItem(new WandAbilityInstance(ModWandAbilities.SHOOT_ANGRY_RAY.get(), new WandAbilityInstance(ModWandAbilities.EXPLODE.get())), null));
        /*for(WandAbility ability : WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValues()){
            items.add(addItem(ability, null));
        }*/

        for(Item item : ForgeRegistries.ITEMS) {
            item.fillItemCategory(this, items);
        }
    }

    private ItemStack addItem(WandAbility main, @Nullable WandAbility crouch){
        var tag = new CompoundTag();
        var ability = new WandAbilityInstance(main);
        tag.put("basic_ability", ability.serializeNBT());
        if(crouch != null) {
            var ability1 = new WandAbilityInstance(crouch);
            tag.put("crouch_ability", ability1.serializeNBT());
        }
        return new ItemStack(ModItems.WAND.get(), 1, tag);
    }

    private ItemStack addItem(WandAbilityInstance main, @Nullable WandAbilityInstance crouch){
        var tag = new CompoundTag();
        tag.put("basic_ability", main.serializeNBT());
        if(crouch != null) {
            tag.put("crouch_ability", crouch.serializeNBT());
        }
        return new ItemStack(ModItems.WAND.get(), 1, tag);
    }
}

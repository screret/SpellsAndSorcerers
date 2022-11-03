package screret.sas.item;

import com.google.common.collect.Maps;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModCreativeTab extends CreativeModeTab {

    public ModCreativeTab() {
        super(SpellsAndSorcerers.MODID);
    }

    public final Map<ResourceLocation, ItemStack> customWandAdded = Maps.newHashMap();
    public final Map<ResourceLocation, ItemStack> customWandCoreAdded = Maps.newHashMap();

    @Override
    public ItemStack makeIcon() {
        return addItem(new WandAbilityInstance(ModWandAbilities.SHOOT_RAY.get(), new WandAbilityInstance(ModWandAbilities.DAMAGE.get())), null);
    }

    public void fillItemList(NonNullList<ItemStack> items) {
        items.add(addItem(new WandAbilityInstance(ModWandAbilities.SHOOT_RAY.get(), new WandAbilityInstance(ModWandAbilities.DAMAGE.get())), null));
        items.add(addItem(new WandAbilityInstance(ModWandAbilities.SHOOT_HOLD_DOWN.get(), new WandAbilityInstance(ModWandAbilities.HEAL.get())), new WandAbilityInstance(ModWandAbilities.HEAL_SELF.get())));
        items.add(addItem(new WandAbilityInstance(ModWandAbilities.SHOOT_ANGRY_RAY.get(), new WandAbilityInstance(ModWandAbilities.EXPLODE.get())), null));

        for(WandAbility ability : WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValues()){
            items.add(addItem(ability, null));
            addCore(ability);
        }

        for(Item item : ForgeRegistries.ITEMS) {
            item.fillItemCategory(this, items);
        }
    }

    private ItemStack addItem(WandAbility main, @Nullable WandAbility crouch){
        var tag = new CompoundTag();
        var ability = new WandAbilityInstance(main);
        tag.put(WandAbility.BASIC_ABILITY_KEY, ability.serializeNBT());
        if(crouch != null) {
            var ability1 = new WandAbilityInstance(crouch);
            tag.put(WandAbility.CROUCH_ABILITY_KEY, ability1.serializeNBT());
        }
        return new ItemStack(ModItems.WAND.get(), 1, tag);
    }

    private ItemStack addItem(WandAbilityInstance main, @Nullable WandAbilityInstance crouch){
        var tag = new CompoundTag();
        tag.put(WandAbility.BASIC_ABILITY_KEY, main.serializeNBT());
        if(crouch != null) {
            tag.put(WandAbility.CROUCH_ABILITY_KEY, crouch.serializeNBT());
        }
        var childestAbility = main;
        while (childestAbility.getChildren() != null && childestAbility.getChildren().size() > 0){
            childestAbility = childestAbility.getChildren().get(0);
        }
        var stack = new ItemStack(ModItems.WAND.get(), 1, tag);
        customWandAdded.put(childestAbility.getId(), stack);
        return stack;
    }

    private ItemStack addCore(WandAbility ability){
        var coreStack = new ItemStack(ModItems.WAND_CORE.get(), 1);
        var tag = new CompoundTag();
        tag.putString("ability", ability.toString());
        coreStack.setTag(tag);
        customWandCoreAdded.put(ability.getKey(), coreStack);
        return coreStack;
    }

    public Map<ResourceLocation, ItemStack> getCustomWands(){
        return customWandAdded;
    }
}

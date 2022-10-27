package screret.sas.api.wand.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WandAbilityInstance implements INBTSerializable<CompoundTag> {

    private WandAbility myAbility;
    private List<WandAbilityInstance> children;

    public WandAbilityInstance(@NotNull WandAbility ability, @Nullable WandAbilityInstance... children){
        this.myAbility = ability;
        this.children = Arrays.stream(children).toList();
    }

    public WandAbilityInstance(CompoundTag tag){
        this.deserializeNBT(tag);
    }

    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, Vec3Wrapped currentPos, int timeCharged) {
        var returnValue = InteractionResultHolder.fail(stack);
        returnValue = myAbility == null ? InteractionResultHolder.fail(stack) : myAbility.execute(level, user, stack, currentPos, timeCharged);
        for (var child : this.getChildren()){
            var val = child.execute(level, user, stack, currentPos, timeCharged).getResult();
            if(val == InteractionResult.FAIL)
                return InteractionResultHolder.fail(stack);
            /*else if(val == InteractionResult.CONSUME)
                return InteractionResultHolder.consume(stack);*/
        }
        return returnValue;
    }

    public ResourceLocation getId() {
        return myAbility.getKey();
    }

    public List<WandAbilityInstance> getChildren(){
        return this.children;
    }

    public WandAbility getAbility(){
        return myAbility;
    }

    public boolean isHoldable(){
        if(children != null){
            for (WandAbilityInstance ability : getChildren()){
                if(ability.isHoldable()) return true;
            }
        }
        return myAbility.isHoldable();
    }

    public boolean isChargeable(){
        if(children != null){
            for (WandAbilityInstance ability : getChildren()){
                if(ability.isChargeable()) return true;
            }
        }
        return myAbility.isChargeable();
    }

    public int getUseDuration(){
        var total = 0;
        if(children != null){
            for (WandAbilityInstance ability : getChildren()){
                if(ability.isHoldable()) total += ability.getUseDuration();
            }
        }
        return total + myAbility.getUseDuration();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", getId().toString());
        ListTag children = new ListTag();
        if(this.children == null) this.children = new ArrayList<>();
        for (WandAbilityInstance child : this.children){
            children.add(child.serializeNBT());
        }
        tag.put("children", children);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.myAbility = WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValue(new ResourceLocation(nbt.getString("id")));
        ListTag children = nbt.getList("children", 10);
        for (int i = 0; i < children.size(); ++i){
            var child = children.getCompound(i);
            WandAbilityInstance a = new WandAbilityInstance(child);
            this.children = new ArrayList<>();
            this.children.add(a);
        }
    }

    public static class Vec3Wrapped {
        public Vec3Wrapped(Vec3 obj){
            this.real = obj;
        }
        public Vec3 real;
    }

}

package screret.sas.api.capability;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import screret.sas.api.wand.ability.IWandAbility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapabilityWandAbility implements ICapabilityWandAbility, INBTSerializable<CompoundTag> {

    private IWandAbility ability;
    private IWandAbility crouchAbility;

    private Map<ResourceLocation, IWandAbility> allAbilities = new HashMap<>();

    public CapabilityWandAbility(IWandAbility ability, IWandAbility crouchAbility){
        this.ability = ability;
        this.crouchAbility = crouchAbility;
    }

    @Override
    public IWandAbility getCrouchAbility() {
        return crouchAbility;
    }

    @Override
    public IWandAbility getAbility() {
        return ability;
    }

    @Override
    public void addAbility(ResourceLocation loc, IWandAbility toAdd) {
        allAbilities.put(loc, toAdd);
    }

    @Override
    public void removeAbility(ResourceLocation loc) {
        allAbilities.remove(loc);
    }

    @Override
    public void setMainAbility(IWandAbility ability) {
        this.ability = ability;
    }

    @Override
    public void setCrouchAbility(IWandAbility ability) {
        this.crouchAbility = ability;
    }


    @Override
    public List<IWandAbility> getAll() {
        return Lists.newArrayList(ability, crouchAbility);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("basic_ability", ability.serializeNBT());
        tag.put("crouch_ability", crouchAbility.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ability.deserializeNBT(nbt.getCompound("basic_ability"));
        crouchAbility.deserializeNBT(nbt.getCompound("crouch_ability"));
    }
}

package screret.sas.api.capability;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import screret.sas.api.wand.ability.WandAbilityInstance;

import java.util.List;

public class CapabilityWandAbility implements ICapabilityWandAbility {

    private WandAbilityInstance ability;
    private WandAbilityInstance crouchAbility;

    public CapabilityWandAbility(WandAbilityInstance ability, WandAbilityInstance crouchAbility){
        this.ability = ability;
        this.crouchAbility = crouchAbility;
    }

    @Override
    public WandAbilityInstance getCrouchAbility() {
        return crouchAbility;
    }

    @Override
    public WandAbilityInstance getAbility() {
        return ability;
    }

    @Override
    public void setMainAbility(WandAbilityInstance ability) {
        this.ability = ability;
    }

    @Override
    public void setCrouchAbility(WandAbilityInstance ability) {
        this.crouchAbility = ability;
    }


    @Override
    public List<WandAbilityInstance> getAll() {
        return Lists.newArrayList(ability, crouchAbility);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if(ability != null) tag.put("basic_ability", ability.serializeNBT());
        if(crouchAbility != null) tag.put("crouch_ability", crouchAbility.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ability.deserializeNBT(nbt.getCompound("basic_ability"));
        if(crouchAbility != null) crouchAbility.deserializeNBT(nbt.getCompound("crouch_ability"));
    }
}

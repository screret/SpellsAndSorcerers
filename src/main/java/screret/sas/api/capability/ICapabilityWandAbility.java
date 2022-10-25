package screret.sas.api.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import screret.sas.api.wand.ability.IWandAbility;

import java.util.List;

public interface ICapabilityWandAbility {

    //void execute(IWandAbility ability, Level level, Player player, InteractionHand hand);

    IWandAbility getCrouchAbility();

    IWandAbility getAbility();

    void addAbility(ResourceLocation loc, IWandAbility toAdd);

    void removeAbility(ResourceLocation loc);

    void setMainAbility(IWandAbility ability);

    void setCrouchAbility(IWandAbility ability);

    List<IWandAbility> getAll();
}


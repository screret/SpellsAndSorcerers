package screret.sas.api.capability.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import screret.sas.api.wand.ability.IWandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;

import java.util.List;

public interface ICapabilityWandAbility extends INBTSerializable<CompoundTag>{

    //void execute(IWandAbility ability, Level level, Player player, InteractionHand hand);

    WandAbilityInstance getCrouchAbility();

    WandAbilityInstance getAbility();

    void setMainAbility(WandAbilityInstance ability);

    void setCrouchAbility(WandAbilityInstance ability);

    List<WandAbilityInstance> getAll();
}


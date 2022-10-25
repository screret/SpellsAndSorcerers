package screret.sas.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.energy.IEnergyStorage;


public class ModCapabilities {

    public static final Capability<ICapabilityWandAbility> WAND_ABILITY = CapabilityManager.get(new CapabilityToken<>(){});

}

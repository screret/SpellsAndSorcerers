package screret.sas.container;

import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.container.container.WandTableMenu;

public class ModContainers {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SpellsAndSorcerers.MODID);

    public static final RegistryObject<MenuType<WandTableMenu>> WAND_CRAFTING = MENU_TYPES.register("wand_crafting", () -> IForgeMenuType.create((id, inv, extraData) -> new WandTableMenu(id, inv)));
}

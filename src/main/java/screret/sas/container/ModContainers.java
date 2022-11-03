package screret.sas.container;

import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.container.container.WandCraftingMenu;

public class ModContainers {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SpellsAndSorcerers.MODID);

    public static final RegistryObject<MenuType<WandCraftingMenu>> WAND_CRAFTING = MENU_TYPES.register("wand", () -> IForgeMenuType.create((id, inv, extraData) -> {
        return new WandCraftingMenu(id, inv, ContainerLevelAccess.create(inv.player.level, extraData.readBlockPos()));
    }));
}

package screret.sas.client.event;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.ability.ability.SubAbility;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.client.gui.ManaBarOverlay;
import screret.sas.client.gui.WandTableScreen;
import screret.sas.client.model.WandModel;
import screret.sas.container.ModContainers;
import screret.sas.item.ModItems;

@Mod.EventBusSubscriber(modid = SpellsAndSorcerers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            MenuScreens.register(ModContainers.WAND_CRAFTING.get(), WandTableScreen::new);
        });
    }

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event) {
        event.register("wand", WandModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerModels(final ModelEvent.RegisterAdditional event) {
        for (var ability : WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValues()){
            if(ability instanceof SubAbility) {
                event.register(new ResourceLocation(ability.getKey().getNamespace(), "item/wand/" + ability.getKey().getPath()));
            }
        }
    }

    @SubscribeEvent
    public static void registerTextures(final TextureStitchEvent.Pre event) {
        TextureAtlas map = event.getAtlas();

        if (map.location() == InventoryMenu.BLOCK_ATLAS) {
            for (var ability : WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValues()){
                event.addSprite(new ResourceLocation(ability.getKey().getNamespace(), "item/wand/" + ability.getKey().getPath()));
            }
        }
    }

    @SubscribeEvent
    public static void registerGuiOverlay(final RegisterGuiOverlaysEvent event){
        event.registerAbove(new ResourceLocation("armor_level"), "mana", new ManaBarOverlay());
    }

    @SubscribeEvent
    public void registerItemColors(final RegisterColorHandlersEvent.Item event){
        event.register((stack, index) -> {
            if(stack.hasTag() && stack.getTag().contains("ability") && index == 1){
                var colorLocation = new ResourceLocation(stack.getTag().getString("ability"));
                if(WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().containsKey(colorLocation)){
                    return WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValue(colorLocation).getColor();
                }
            }
            return 0xFFFFFFFF;
        }, ModItems.WAND_CORE.get());
    }
}

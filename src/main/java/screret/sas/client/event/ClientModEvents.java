package screret.sas.client.event;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import screret.sas.SpellsAndSorcerers;
import screret.sas.ability.ability.SubAbility;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.client.gui.ManaBarOverlay;
import screret.sas.client.model.WandModel;

@Mod.EventBusSubscriber(modid = SpellsAndSorcerers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
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
}

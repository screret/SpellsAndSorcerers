package screret.sas.client.event;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import screret.sas.SpellsAndSorcerers;
import screret.sas.ability.ability.SubAbility;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.block.ModBlocks;
import screret.sas.block.block.SummonSignBlock;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.client.gui.ManaBarOverlay;
import screret.sas.client.gui.WandTableScreen;
import screret.sas.client.model.item.WandModel;
import screret.sas.client.renderer.blockentity.SummonSignBERenderer;
import screret.sas.client.renderer.entity.WizardRenderer;
import screret.sas.container.ModContainers;
import screret.sas.entity.ModEntities;
import screret.sas.item.ModItems;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.geckolib3.core.molang.LazyVariable;
import software.bernie.geckolib3.resource.GeckoLibCache;

@Mod.EventBusSubscriber(modid = SpellsAndSorcerers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            MenuScreens.register(ModContainers.WAND_CRAFTING.get(), WandTableScreen::new);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        GeckoLibCache.getInstance().parser.register(new LazyVariable("move_speed", () -> 0));
        event.registerEntityRenderer(ModEntities.WIZARD_TYPE.get(), WizardRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SUMMON_SIGN_BE.get(), SummonSignBERenderer::new);
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
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event){
        event.register((stack, index) -> {
            if(stack.hasTag() && stack.getTag().contains("ability") && index == 1){
                var colorLocation = new ResourceLocation(stack.getTag().getString("ability"));
                if(WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().containsKey(colorLocation)){
                    return WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValue(colorLocation).getColor();
                }
            }
            return 0xFFFFFFFF;
        }, ModItems.WAND_CORE.get());

        event.register((itemStack, layer) -> {
            BlockState blockstate = ((BlockItem)itemStack.getItem()).getBlock().defaultBlockState();
            return event.getBlockColors().getColor(blockstate, null, null, layer);
        }, ModItems.SUMMON_SIGN.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event){
        event.register((pState, pLevel, pPos, pTintIndex) -> pState.getValue(SummonSignBlock.COLOR).getFireworkColor(), ModBlocks.SUMMON_SIGN.get());
    }
}

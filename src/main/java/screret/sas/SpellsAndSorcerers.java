package screret.sas;

import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import screret.sas.ability.ModWandAbilities;
import screret.sas.alchemy.effect.ModMobEffects;
import screret.sas.alchemy.potion.ModPotions;
import screret.sas.api.capability.ability.ICapabilityWandAbility;
import screret.sas.api.capability.mana.ICapabilityMana;
import screret.sas.api.capability.mana.ManaProvider;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.block.ModBlocks;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.client.particle.ModParticles;
import screret.sas.config.SASConfig;
import screret.sas.container.ModContainers;
import screret.sas.data.conversion.provider.EyeConversionProvider;
import screret.sas.data.recipe.provider.ModRecipeProvider;
import screret.sas.data.recipe.provider.WandRecipeProvider;
import screret.sas.data.tag.SASBiomeTagsProvider;
import screret.sas.data.tag.SASBlockTagsProvider;
import screret.sas.data.tag.SASItemTagsProvider;
import screret.sas.enchantment.ModEnchantments;
import screret.sas.entity.ModEntities;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.entity.entity.WizardEntity;
import screret.sas.item.ModItems;
import screret.sas.recipe.ModRecipes;
import screret.sas.recipe.ingredient.WandAbilityIngredient;
import screret.sas.resource.EyeConversionManager;
import software.bernie.geckolib.GeckoLib;

import java.util.concurrent.CompletableFuture;

import static screret.sas.Util.addWand;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SpellsAndSorcerers.MODID)
public class SpellsAndSorcerers {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "sas";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpellsAndSorcerers() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(this::registerSerializers);
        modEventBus.addListener(this::addCreativeTabs);


        WandAbilityRegistry.init();
        WandAbilityRegistry.WAND_ABILITIES.register(modEventBus);
        ModWandAbilities.init();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        ModEnchantments.ENCHANTS.register(modEventBus);
        ModEnchantments.ENCHANTS_MINECRAFT.register(modEventBus);

        ModRecipes.RECIPE_TYPES.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);

        ModMobEffects.EFFECTS.register(modEventBus);
        ModPotions.POTIONS.register(modEventBus);

        ModContainers.MENU_TYPES.register(modEventBus);

        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        ModParticles.PARTICLES.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SASConfig.Client.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SASConfig.Server.serverSpec);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        event.enqueueWork(() ->  {
            ModPotions.registerPotionMixes();
        });
    }

    public void addCreativeTabs(final CreativeModeTabEvent.Register event) {
        Util.addItems();
        event.registerCreativeModeTab(new ResourceLocation(GeckoLib.MOD_ID, "spellsandsorcerers"),
                e -> e.icon(() -> new ItemStack(ModItems.WAND.get()))
                        .title(Component.translatable("itemGroup." + SpellsAndSorcerers.MODID))
                        .displayItems((enabledFeatures, entries, operatorEnabled) -> {
                            entries.acceptAll(Util.customWands.values());
                            entries.acceptAll(Util.customWandCores.values());
                            entries.accept(ModItems.HANDLE.get());
                            entries.accept(ModItems.CLOUD_BOTTLE.get());
                            entries.accept(ModItems.CTHULHU_EYE.get());
                            entries.accept(ModItems.GLINT.get());
                            entries.accept(ModItems.GLINT_ORE.get());
                            entries.accept(ModItems.PALANTIR.get());
                            entries.accept(ModItems.POTION_DISTILLERY.get());
                            entries.accept(ModItems.WAND_TABLE.get());
                            entries.accept(ModItems.SOUL_BOTTLE.get());
                            entries.accept(ModItems.SOULSTEEL_INGOT.get());
                            entries.accept(ModItems.SOULSTEEL_BLOCK.get());
                            entries.accept(ModItems.SOULSTEEL_AXE.get());
                            entries.accept(ModItems.SOULSTEEL_HOE.get());
                            entries.accept(ModItems.SOULSTEEL_PICKAXE.get());
                            entries.accept(ModItems.SOULSTEEL_SHOVEL.get());
                            entries.accept(ModItems.SOULSTEEL_SWORD.get());
                            entries.accept(ModItems.SOULSTEEL_HELMET.get());
                            entries.accept(ModItems.SOULSTEEL_CHESTPLATE.get());
                            entries.accept(ModItems.SOULSTEEL_LEGGINGS.get());
                            entries.accept(ModItems.SOULSTEEL_BOOTS.get());
                            entries.accept(ModItems.WIZARD_SPAWN_EGG.get());
                            entries.accept(ModItems.BOSS_WIZARD_SPAWN_EGG.get());
                        }));
    }

    public void addItemsVanillaTabs(final CreativeModeTabEvent.BuildContents event) {
        Util.addItems();
        if(event.getTab() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.SOULSTEEL_AXE.get());
            event.accept(ModItems.SOULSTEEL_SWORD.get());
            event.accept(ModItems.SOULSTEEL_HELMET.get());
            event.accept(ModItems.SOULSTEEL_CHESTPLATE.get());
            event.accept(ModItems.SOULSTEEL_LEGGINGS.get());
            event.accept(ModItems.SOULSTEEL_BOOTS.get());
            event.acceptAll(Util.customWands.values());
            event.acceptAll(Util.customWandCores.values());
        } else if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.SOULSTEEL_AXE.get());
            event.accept(ModItems.SOULSTEEL_HOE.get());
            event.accept(ModItems.SOULSTEEL_PICKAXE.get());
            event.accept(ModItems.SOULSTEEL_SHOVEL.get());
            event.accept(ModItems.CTHULHU_EYE.get());
        } else if (event.getTab() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.SOULSTEEL_BLOCK.get());
        } else if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.PALANTIR.get());
            event.accept(ModItems.POTION_DISTILLERY.get());
            event.accept(ModItems.WAND_TABLE.get());
        } else if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModItems.POTION_DISTILLERY.get());
        } else if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.HANDLE.get());
            event.accept(ModItems.CLOUD_BOTTLE.get());
            event.accept(ModItems.SOUL_BOTTLE.get());
            event.accept(ModItems.SOULSTEEL_INGOT.get());
        } else if (event.getTab() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.WIZARD_SPAWN_EGG.get());
            event.accept(ModItems.BOSS_WIZARD_SPAWN_EGG.get());
        }
    }

    public void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        SASBlockTagsProvider blockTags = new SASBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new SASItemTagsProvider(packOutput, lookupProvider, blockTags, existingFileHelper));

        gen.addProvider(event.includeServer(), new WandRecipeProvider(packOutput));
        gen.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
        gen.addProvider(event.includeServer(), new EyeConversionProvider(packOutput));

        gen.addProvider(event.includeServer(), new SASBiomeTagsProvider(packOutput, lookupProvider, existingFileHelper));

        //gen.addProvider(event.includeServer(), new ModBlockstateProvider(gen, existingFileHelper));
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event){
        event.register(ICapabilityWandAbility.class);
        event.register(ICapabilityMana.class);
    }

    @SubscribeEvent
    public void attachCapabilitiesPlayer(final AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;
        if(!SASConfig.Server.useMana.get()) return;
        event.addCapability(Util.resource("mana"), new ManaProvider());
    }

    public void registerEntityAttributes(final EntityAttributeCreationEvent event){
        event.put(ModEntities.WIZARD.get(), WizardEntity.createAttributes().build());
        event.put(ModEntities.BOSS_WIZARD.get(), BossWizardEntity.createAttributes().build());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRegister(final RegisterEvent event){
        Util.addItems();
    }


    public void registerSerializers(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, helper -> CraftingHelper.register(Util.resource("wand_ability"), WandAbilityIngredient.Serializer.INSTANCE));
    }

    @Mod.EventBusSubscriber(modid = SpellsAndSorcerers.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class ForgeBusEvents {
        @SubscribeEvent
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event){
            if(event.phase == TickEvent.Phase.END && event.player.tickCount % 20 == 0){
                event.player.getCapability(ManaProvider.MANA).ifPresent(cap -> {
                    cap.addMana(1, false);
                });
            }
        }

        @SubscribeEvent
        public static void onRightclickBlock(final PlayerInteractEvent.RightClickBlock event){
            if(event.getEntity().getItemInHand(event.getHand()).is(ModTags.Items.GLASS_BOTTLES)) {
                if(event.getLevel().getBlockState(event.getHitVec().getBlockPos()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)){
                    event.getEntity().awardStat(Stats.ITEM_USED.get(event.getEntity().getUseItem().getItem()));
                    ItemUtils.createFilledResult(event.getEntity().getUseItem(), event.getEntity(), new ItemStack(ModItems.SOUL_BOTTLE.get()));
                } else if(event.getEntity().getY() > 320 - 16){
                    event.getEntity().awardStat(Stats.ITEM_USED.get(event.getEntity().getUseItem().getItem()));
                    ItemUtils.createFilledResult(event.getEntity().getUseItem(), event.getEntity(), new ItemStack(ModItems.CLOUD_BOTTLE.get()));
                }
            }
        }

        @SubscribeEvent
        public static void registerReloadListeners(final AddReloadListenerEvent event){
            EyeConversionManager.INSTANCE = new EyeConversionManager();
            event.addListener(EyeConversionManager.INSTANCE);
        }
    }
}

package screret.sas;

import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import screret.sas.ability.ModWandAbilities;
import screret.sas.api.capability.ability.ICapabilityWandAbility;
import screret.sas.api.capability.mana.ICapabilityMana;
import screret.sas.api.capability.mana.ManaProvider;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.block.ModBlocks;
import screret.sas.config.SASConfig;
import screret.sas.container.ModContainers;
import screret.sas.data.recipe.provider.WandRecipeProvider;
import screret.sas.data.tag.SASBlockTagsProvider;
import screret.sas.data.tag.SASItemTagsProvider;
import screret.sas.enchantment.ModEnchantments;
import screret.sas.entity.ModEntities;
import screret.sas.item.ModCreativeTab;
import screret.sas.item.ModItems;
import screret.sas.recipe.ModRecipes;

import static screret.sas.Util.addWand;
import static screret.sas.Util.addWandCore;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SpellsAndSorcerers.MODID)
public class SpellsAndSorcerers {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "sas";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreativeModeTab SAS_TAB = new ModCreativeTab();

    public SpellsAndSorcerers() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::gatherData);

        WandAbilityRegistry.init();
        WandAbilityRegistry.WAND_ABILITIES.register(modEventBus);
        ModWandAbilities.init();

        // Register the Deferred Register to the mod event bus so blocks get registered
        ModBlocks.BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ModItems.ITEMS.register(modEventBus);
        ModEnchantments.ENCHANTS.register(modEventBus);
        ModEnchantments.ENCHANTS_MINECRAFT.register(modEventBus);

        ModRecipes.RECIPE_TYPES.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);

        ModContainers.MENU_TYPES.register(modEventBus);

        ModEntities.ENTITY_TYPES.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SASConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SASConfig.commonSpec);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code

    }

    public void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        SASBlockTagsProvider blockTags = new SASBlockTagsProvider(gen, existingFileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new SASItemTagsProvider(gen, blockTags, existingFileHelper));

        gen.addProvider(event.includeServer(), new WandRecipeProvider(gen));
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event){
        event.register(ICapabilityWandAbility.class);
        event.register(ICapabilityMana.class);
    }

    @SubscribeEvent
    public void attachCapabilitiesPlayer(final AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;
        event.addCapability(Util.resource("mana"), new ManaProvider());
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
        public static void onRightclickBlockSoulSand(final PlayerInteractEvent.RightClickBlock event){
            if(event.getEntity().getItemInHand(event.getHand()).is(ModTags.Items.GLASS_BOTTLES) && event.getLevel().getBlockState(event.getHitVec().getBlockPos()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                var stack = new ItemStack(ModItems.SOUL_BOTTLE.get());
                ItemUtils.createFilledResult(event.getEntity().getUseItem(), event.getEntity(), stack, false);
                event.setUseItem(Event.Result.DENY);
                event.setUseBlock(Event.Result.DENY);
            }
        }
    }
}

package screret.sas;

import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import screret.sas.ability.ModWandAbilities;
import screret.sas.ability.ability.SubAbility;
import screret.sas.api.capability.ICapabilityWandAbility;
import screret.sas.api.capability.WandAbilityProvider;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.block.ModBlocks;
import screret.sas.client.model.WandAbilityOverrideHandler;
import screret.sas.client.model.WandModel;
import screret.sas.client.renderer.item.CustomItemOverrides;
import screret.sas.client.renderer.item.StringItemPropertyFunction;
import screret.sas.enchantment.ModEnchantments;
import screret.sas.item.ModCreativeTab;
import screret.sas.item.ModItems;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SpellsAndSorcerers.MODID)
public class SpellsAndSorcerers {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "sas";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreativeModeTab SAS_TAB = new ModCreativeTab();

    public static final ResourceLocation WAND_ABILIY_PATH = new ResourceLocation(SpellsAndSorcerers.MODID, "wand_ability/");

    public SpellsAndSorcerers() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);

        WandAbilityRegistry.init();

        // Register the Deferred Register to the mod event bus so blocks get registered
        ModBlocks.BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ModItems.ITEMS.register(modEventBus);
        ModEnchantments.ENCHANTS.register(modEventBus);
        ModEnchantments.ENCHANTS_MINECRAFT.register(modEventBus);

        WandAbilityRegistry.WAND_ABILITIES.register(modEventBus);
        ModWandAbilities.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code

    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event){
        event.register(ICapabilityWandAbility.class);
    }

    /*public void attachCapabilities(final AttachCapabilitiesEvent<ItemStack> event) {
        if (!(event.getObject().getItem() instanceof WandItem)) return;

        CapabilityWandAbility backend = new CapabilityWandAbility(new WandAbilityInstance(ModWandAbilities.DUMMY.get()), new WandAbilityInstance(ModWandAbilities.DUMMY.get()));
        LazyOptional<ICapabilityWandAbility> optionalStorage = LazyOptional.of(() -> backend);

        ICapabilityProvider provider = new ICapabilitySerializable<CompoundTag>() {
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction direction) {
                if (cap == ModCapabilities.WAND_ABILITY) {
                    return optionalStorage.cast();
                }
                return LazyOptional.empty();
            }

            @Override
            public CompoundTag serializeNBT() {
                return backend.serializeNBT();
            }

            @Override
            public void deserializeNBT(CompoundTag tag) {
                backend.deserializeNBT(tag);
            }
        };

        event.addCapability(new ResourceLocation(SpellsAndSorcerers.MODID, "wand_abilities"), provider);
    }*/

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = SpellsAndSorcerers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event) {
            event.register("wand", WandModel.Loader.INSTANCE);
        }

        @SubscribeEvent
        public static void onRegisterModels(final ModelEvent.RegisterAdditional event) {
            for (var ability : WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValues()){
                if(ability instanceof SubAbility) {
                    event.register(new ResourceLocation(SpellsAndSorcerers.MODID, "item/wand/" + ability.getKey().getPath()));
                }
            }
        }

        @SubscribeEvent
        public static void registerTextures(final TextureStitchEvent.Pre event) {
            TextureAtlas map = event.getAtlas();

            if (map.location() == InventoryMenu.BLOCK_ATLAS) {
                for (var ability : WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getValues()){
                    event.addSprite(new ResourceLocation(SpellsAndSorcerers.MODID, "item/wand/" + ability.getKey().getPath()));
                }
            }
        }
    }
}

package screret.sas;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import screret.sas.api.capability.CapabilityWandAbility;
import screret.sas.api.capability.ICapabilityWandAbility;
import screret.sas.api.capability.ModCapabilities;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.block.ModBlocks;
import screret.sas.enchantment.ModEnchantments;
import screret.sas.item.ModCreativeTab;
import screret.sas.item.ModItems;
import screret.sas.item.wand.WandItem;

import javax.annotation.Nullable;

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

        // Register the Deferred Register to the mod event bus so blocks get registered
        ModBlocks.BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ModItems.ITEMS.register(modEventBus);
        ModEnchantments.ENCHANTS.register(modEventBus);
        ModEnchantments.ENCHANTS_MINECRAFT.register(modEventBus);

        WandAbilityRegistry.WAND_ABILITIES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code

    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event){
        event.register(ICapabilityWandAbility.class);
    }

    public void attachCapabilities(final AttachCapabilitiesEvent<ItemStack> event) {
        if (!(event.getObject().getItem() instanceof WandItem)) return;

        CapabilityWandAbility backend = new CapabilityWandAbility(null, null);
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
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code

        }
    }
}

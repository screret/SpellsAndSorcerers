package screret.sas.config;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import screret.sas.item.ModItems;

import java.util.Arrays;
import java.util.List;

public class SASConfig {

    public static class Client {
        public static final ForgeConfigSpec clientSpec;

        public static ForgeConfigSpec.IntValue manaBarX;
        public static ForgeConfigSpec.IntValue manaBarY;

        private static void setupConfig(ForgeConfigSpec.Builder builder){
            builder.comment("Spells & Sorcerers Client Configuration")
                    .push("client");

            manaBarX = builder
                    .comment("Mana bar's X position from center of screen.")
                    .translation("sas.configgui.manaBarX")
                    .defineInRange("manaBarX", -91, Integer.MIN_VALUE, Integer.MAX_VALUE);
            manaBarY = builder
                    .comment("Mana bar's Y position from bottom of screen.")
                    .translation("sas.configgui.manaBarY")
                    .defineInRange("manaBarY", 57, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        static {
            ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
            setupConfig(configBuilder);
            clientSpec = configBuilder.build();
        }
    }

    public static class Server {
        public static final ForgeConfigSpec serverSpec;
        public static ForgeConfigSpec.BooleanValue useMana;
        public static ForgeConfigSpec.BooleanValue dropWandCores;

        private static void setupConfig(ForgeConfigSpec.Builder builder){
            builder.comment("Spells & Sorcerers Common Configuration")
                    .push("common");

            useMana = builder
                    .comment("Is Mana used?")
                    .translation("sas.configgui.useMana")
                    .define("useMana", true);
            dropWandCores = builder
                    .comment("Do wizards drop wand cores?")
                    .translation("sas.configgui.dropWandCores")
                    .define("dropWandCores", true);
        }

        static {
            ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
            setupConfig(configBuilder);
            serverSpec = configBuilder.build();
        }
    }
}

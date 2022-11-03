package screret.sas.config;

import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SASConfig {

    public static class Client {
        public final ForgeConfigSpec.IntValue manaBarX;
        public final ForgeConfigSpec.IntValue manaBarY;

        public Client(ForgeConfigSpec.Builder builder){
            builder.comment("Spells & Sorcerers Client Configuration")
                    .push("client");

            manaBarX = builder
                    .comment("Mana bar's X position from center of screen.")
                    .translation("sas.configgui.manaBarX")
                    .defineInRange("manaBarX", -91, Integer.MIN_VALUE, Integer.MAX_VALUE);
            manaBarY = builder
                    .comment("Mana bar's Y position from bottom of screen.")
                    .translation("sas.configgui.manaBarY")
                    .defineInRange("manaBarY", -57, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
    }

    public static class Common {
        public final ForgeConfigSpec.BooleanValue useMana;

        public Common(ForgeConfigSpec.Builder builder){
            builder.comment("Spells & Sorcerers Common Configuration")
                    .push("common");

            useMana = builder
                    .comment("Is Mana used?")
                    .comment("sas.configgui.useMana")
                    .define("useMana", true);
        }
    }

    public static final ForgeConfigSpec clientSpec;
    public static final SASConfig.Client CLIENT;
    static {
        final Pair<SASConfig.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(SASConfig.Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static final ForgeConfigSpec commonSpec;
    public static final SASConfig.Common COMMON;
    static {
        final Pair<SASConfig.Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(SASConfig.Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }
}

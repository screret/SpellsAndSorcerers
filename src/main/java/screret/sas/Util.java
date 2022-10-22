package screret.sas;

import net.minecraft.world.level.Level;

public class Util {

    public static double randomInRange(Level level, double min, double max) {
        return (level.random.nextDouble() * (max - min)) + min;
    }


}

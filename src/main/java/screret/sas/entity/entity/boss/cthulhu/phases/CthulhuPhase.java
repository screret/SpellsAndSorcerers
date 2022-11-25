package screret.sas.entity.entity.boss.cthulhu.phases;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.*;
import screret.sas.entity.entity.boss.cthulhu.CthulhuEntity;
import screret.sas.entity.entity.boss.cthulhu.phases.phases.CthulhuDeathPhase;
import screret.sas.entity.entity.boss.cthulhu.phases.phases.CthulhuHoldingPatternPhase;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class CthulhuPhase<T extends CthulhuPhaseInstance> {

    private static CthulhuPhase<?>[] phases = new CthulhuPhase[0];
    public static final CthulhuPhase<CthulhuHoldingPatternPhase> HOLDING_PATTERN = create(CthulhuHoldingPatternPhase.class, "HoldingPattern");
    public static final CthulhuPhase<DragonStrafePlayerPhase> STRAFE_PLAYER = create(DragonStrafePlayerPhase.class, "StrafePlayer");
    public static final CthulhuPhase<DragonChargePlayerPhase> CHARGING_PLAYER = create(DragonChargePlayerPhase.class, "ChargingPlayer");
    public static final CthulhuPhase<CthulhuDeathPhase> DYING = create(CthulhuDeathPhase.class, "Dying");
    public static final CthulhuPhase<DragonHoverPhase> HOVERING = create(DragonHoverPhase.class, "Hover");
    private final Class<? extends CthulhuPhaseInstance> instanceClass;
    private final int id;
    private final String name;

    private CthulhuPhase(int pId, Class<? extends CthulhuPhaseInstance> pInstanceClass, String pName) {
        this.id = pId;
        this.instanceClass = pInstanceClass;
        this.name = pName;
    }

    public CthulhuPhaseInstance createInstance(CthulhuEntity pDragon) {
        try {
            Constructor<? extends CthulhuPhaseInstance> constructor = this.getConstructor();
            return constructor.newInstance(pDragon);
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }

    protected Constructor<? extends CthulhuPhaseInstance> getConstructor() throws NoSuchMethodException {
        return this.instanceClass.getConstructor(CthulhuEntity.class);
    }

    public int getId() {
        return this.id;
    }

    public String toString() {
        return this.name + " (#" + this.id + ")";
    }

    /**
     * Gets a phase by its ID. If the phase is out of bounds (negative or beyond the end of the phase array), returns
     * {@link #HOLDING_PATTERN}.
     */
    public static CthulhuPhase<?> getById(int pId) {
        return pId >= 0 && pId < phases.length ? phases[pId] : HOLDING_PATTERN;
    }

    public static int getCount() {
        return phases.length;
    }

    private static <T extends CthulhuPhaseInstance> CthulhuPhase<T> create(Class<T> pPhase, String pName) {
        CthulhuPhase<T> CthulhuPhase = new CthulhuPhase<>(phases.length, pPhase, pName);
        phases = Arrays.copyOf(phases, phases.length + 1);
        phases[CthulhuPhase.getId()] = CthulhuPhase;
        return CthulhuPhase;
    }
}

package screret.sas.entity.entity.boss.cthulhu.phases;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.slf4j.Logger;
import screret.sas.entity.entity.boss.cthulhu.CthulhuEntity;
import screret.sas.entity.entity.boss.cthulhu.CthulhuFight;

import javax.annotation.Nullable;

public class CthulhuPhaseManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    CthulhuEntity cthulhu;
    private final CthulhuPhaseInstance[] phases = new CthulhuPhaseInstance[EnderDragonPhase.getCount()];
    @Nullable
    private CthulhuPhaseInstance currentPhase;


    public CthulhuPhaseManager(CthulhuEntity cthulhu) {
        this.cthulhu = cthulhu;
        this.setPhase(CthulhuPhase.HOVERING);
    }

    public void setPhase(CthulhuPhase<?> pPhase) {
        if (this.currentPhase == null || pPhase != this.currentPhase.getPhase()) {
            if (this.currentPhase != null) {
                this.currentPhase.end();
            }

            this.currentPhase = this.getPhase(pPhase);
            if (!this.cthulhu.level.isClientSide) {
                this.cthulhu.getEntityData().set(EnderDragon.DATA_PHASE, pPhase.getId());
            }

            LOGGER.debug("Dragon is now in phase {} on the {}", pPhase, this.cthulhu.level.isClientSide ? "client" : "server");
            this.currentPhase.begin();
        }
    }

    public CthulhuPhaseInstance getCurrentPhase() {
        return this.currentPhase;
    }

    public <T extends CthulhuPhaseInstance> T getPhase(CthulhuPhase<T> pPhase) {
        int i = pPhase.getId();
        if (this.phases[i] == null) {
            this.phases[i] = pPhase.createInstance(this.cthulhu);
        }

        return (T)this.phases[i];
    }
}

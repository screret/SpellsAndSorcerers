package screret.sas;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class Util {

    private static final float PI_FLOAT = (float) Math.PI;
    private static final float PI_180 = (PI_FLOAT / 180F);


    public static double randomInRange(Level level, double min, double max) {
        return (level.random.nextDouble() * (max - min)) + min;
    }

    public static BlockHitResult getHitResult(Level level, LivingEntity entity, ClipContext.Fluid fluidInteractionMode, double distance) {
        var entityPosStuff = getEntityPos(entity, distance);
        return level.clip(new ClipContext(entity.getEyePosition(), entityPosStuff.to, ClipContext.Block.OUTLINE, fluidInteractionMode, entity));
    }

    public static EntityHitResult getHitResult(Level level, LivingEntity entity, Predicate<Entity> filter, double distance) {
        var entityPosStuff = getEntityPos(entity, distance);
        return ProjectileUtil.getEntityHitResult(entity, entity.getEyePosition(), entityPosStuff.to, AABB.ofSize(entityPosStuff.from, distance, distance, distance), filter, distance);
    }

    public static EntityPosStuff getEntityPos(LivingEntity entity, double distance){
        EntityPosStuff stuff = new EntityPosStuff();
        stuff.xRot = entity.getXRot();
        stuff.yRot = entity.getYRot();
        stuff.from = entity.position();
        float cosY = Mth.cos(-stuff.yRot * PI_180 - PI_FLOAT);
        float sinY = Mth.sin(-stuff.yRot * PI_180 - PI_FLOAT);
        float cosX = -Mth.cos(-stuff.xRot * PI_180);
        float sinX = Mth.sin(-stuff.xRot * PI_180);
        float sinCos = sinY * cosX;
        float cosCos = cosY * cosX;
        stuff.to = entity.getEyePosition().add((double)sinCos * distance, (double)sinX * distance, (double)cosCos * distance);

        return stuff;
    }

    public static void spawnParticlesInLine(Level level, Vec3 start, Vec3 end, ParticleOptions particle, int pointsPerLine, Vec3 randomDeviation, boolean alwaysRender){
        double d = start.distanceTo(end) / pointsPerLine;
        for (int i = 0; i < pointsPerLine; i++) {
            Vec3 pos = new Vec3(start.x, start.y, start.z);
            Vec3 direction = end.subtract(start).normalize();
            Vec3 v = direction.multiply(i * d, i * d, i * d);

            pos = pos.add(v);
            if(level.isClientSide){
                level.addParticle(particle, alwaysRender, pos.x, pos.y, pos.z, randomDeviation.x, randomDeviation.y, randomDeviation.z);
                continue;
            }
            ((ServerLevel)level).sendParticles(particle, pos.x, pos.y, pos.z, 1, randomDeviation.x, randomDeviation.y, randomDeviation.z, 0);
        }
    }

    public static class EntityPosStuff {
        public float xRot, yRot;
        public Vec3 from, to;
    }


}

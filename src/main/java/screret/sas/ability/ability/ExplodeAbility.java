package screret.sas.ability.ability;

import com.mojang.math.Vector3f;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;
import screret.sas.Util;
import screret.sas.api.wand.ability.WandAbility;

import java.util.EnumSet;

public class ExplodeAbility extends SubAbility {

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0xFFAA0000)), 2.0F);
    public static final Vec3 RANDOM_DEVIATION = new Vec3(0.125D, 0.125D, 0.125D);


    public ExplodeAbility() {
        super(100, 60, 1, true, ExplodeAbility.PARTICLE, EnumSet.of(HitFlags.BLOCK), 0xAA0000);
    }

    @Override
    public boolean isChargeable() {
        return true;
    }

    @Override
    public void doHit(ItemStack usedItem, LivingEntity user, LivingEntity hitEnt, float timeCharged) {
        var explosionPower = getDamagePerHit(usedItem) * timeCharged / 8;
        Explosion.BlockInteraction explosion = user instanceof Player ?
                Explosion.BlockInteraction.BREAK : ForgeEventFactory.getMobGriefingEvent(user.level, user) ?
                Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
        user.level.explode(user, hitEnt.getX(), hitEnt.getY(), hitEnt.getZ(), explosionPower, explosion);
    }

    @Override
    public void doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged) {
        var explosionPower = getDamagePerHit(usedItem) * timeCharged / 8;
        Explosion.BlockInteraction explosion = user instanceof Player ?
                Explosion.BlockInteraction.BREAK : ForgeEventFactory.getMobGriefingEvent(user.level, user) ?
                Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
        user.level.explode(user, hitPoint.x, hitPoint.y, hitPoint.z, explosionPower, explosion);
    }
}

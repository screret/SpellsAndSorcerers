package screret.sas.blockentity.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import screret.sas.blockentity.ModBlockEntities;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class PalantirBE extends BlockEntity implements IAnimatable {
    private static final float MAX_LOOK_X_INCREASE = 2f, MAX_LOOK_Y_INCREASE = 2f;

    public float xRot, yRot;

    private AnimationFactory factory = GeckoLibUtil.createFactory(this);


    public PalantirBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.PALANTIR_BE.get(), pPos, pBlockState);
    }


    private PlayState predicate(AnimationEvent<PalantirBE> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.palantir.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public static void eyeAnimationTick(Level pLevel, BlockPos pPos, BlockState pState, PalantirBE pBlockEntity) {
        Player player = pLevel.getNearestPlayer((double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.5D, (double)pPos.getZ() + 0.5D, 3.0D, false);
        if (player != null) {
            double x = player.getX() - ((double)pPos.getX() + 0.5D);
            double y = player.getZ() - ((double)pPos.getZ() + 0.5D);
            double z = player.getEyeY() - ((double) pPos.getY() + 0.3125D);


            double distanceXY = Math.sqrt(x * x + y * y);
            float toY = (float)(Mth.atan2(y, x) * Mth.RAD_TO_DEG) + 90.0F;
            float toX = (float) -(Mth.atan2(z, distanceXY) * Mth.RAD_TO_DEG);
            pBlockEntity.xRot = pBlockEntity.rotlerp(pBlockEntity.xRot, toX, MAX_LOOK_X_INCREASE);
            pBlockEntity.yRot = pBlockEntity.rotlerp(pBlockEntity.yRot, toY, MAX_LOOK_Y_INCREASE);
        } else {
            pBlockEntity.xRot += 0.2F;
        }

    }

    private float rotlerp(float pAngle, float pTargetAngle, float pMaxIncrease) {
        float f = Mth.wrapDegrees(pTargetAngle - pAngle);
        if (f > pMaxIncrease) {
            f = pMaxIncrease;
        }

        if (f < -pMaxIncrease) {
            f = -pMaxIncrease;
        }

        return pAngle + f;
    }
}

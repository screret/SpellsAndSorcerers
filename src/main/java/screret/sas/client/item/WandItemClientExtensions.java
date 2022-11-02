package screret.sas.client.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class WandItemClientExtensions implements IClientItemExtensions {

    public static final HumanoidModel.ArmPose POSE_USE_WAND = HumanoidModel.ArmPose.create("wand", true, (model, entity, arm) -> {
        model.rightArm.yRot = -0.1F + model.head.yRot;
        model.leftArm.yRot = 0.1F + model.head.yRot;
        model.rightArm.xRot = Mth.HALF_PI + model.head.xRot;
        model.leftArm.xRot = Mth.HALF_PI + model.head.xRot;
    });

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
        return WandItemClientExtensions.POSE_USE_WAND;
    }
}

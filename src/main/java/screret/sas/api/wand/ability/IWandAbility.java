package screret.sas.api.wand.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public interface IWandAbility extends INBTSerializable<CompoundTag> {

    IWandAbility[] getChildren();

    InteractionResultHolder<ItemStack> execute(Level level, Player player, InteractionHand hand);

    int getUseDuration();

    void onUsingTick(ItemStack stack, LivingEntity user, int usageTicks);

    void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft);

    int getCooldownDuration();

    float getBaseDamagePerHit();

    String getId();
}

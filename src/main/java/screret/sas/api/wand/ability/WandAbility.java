package screret.sas.api.wand.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WandAbility implements IWandAbility {

    private final IWandAbility[] children;
    private final int useDuration, cooldownDuration;
    private final float damagePerHit;
    private final boolean applyEnchants;

    public WandAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, IWandAbility... children){
        this.useDuration = useDuration;
        this.cooldownDuration = cooldownDuration;
        this.damagePerHit = damagePerHit;
        this.applyEnchants = applyEnchants;
        this.children = children;
    }

    @Override
    public IWandAbility[] getChildren() {
        return this.children;
    }

    @Override
    public InteractionResultHolder<ItemStack> execute(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        var returnValue = InteractionResultHolder.fail(itemstack);
        for (var child : this.getChildren()){
            var val = child.execute(level, player, hand).getResult();
            if(val == InteractionResult.FAIL)
                return InteractionResultHolder.fail(itemstack);
            else if(val == InteractionResult.CONSUME)
                return InteractionResultHolder.consume(itemstack);
        }
        return returnValue;
    }

    @Override
    public int getUseDuration() {
        return this.useDuration;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity user, int usageTicks) {

    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {

    }

    @Override
    public int getCooldownDuration() {
        return this.cooldownDuration;
    }

    @Override
    public float getBaseDamagePerHit() {
        return 0;
    }

    @Override
    public String getId() {
        return "base";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", getId());
        ListTag children = new ListTag();
        for (IWandAbility child : this.children){
            children.add(child.serializeNBT());
        }
        tag.put("children", children);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}

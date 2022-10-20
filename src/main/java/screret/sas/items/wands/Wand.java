package screret.sas.items.wands;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class Wand extends Item {

    public Wand(int durability, Rarity rarity) {
        super(new Properties().durability(durability).rarity(rarity));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {

    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return this.isEdible() ? entity.eat(level, stack) : stack;
    }
}

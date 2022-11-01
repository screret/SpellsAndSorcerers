package screret.sas.client.renderer.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface CustomItemPropertyFunction<T> extends ItemPropertyFunction {
    @Override
    default float call(ItemStack pStack, ClientLevel pLevel, LivingEntity pEntity, int pSeed) {
        return callNew(pStack, pLevel, pEntity, pSeed).hashCode();
    }

    T callNew(ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed);
}

package screret.sas.container.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import screret.sas.blockentity.blockentity.PotionDistilleryBE;
import screret.sas.container.container.PotionDistilleryMenu;

public class DistilleryFuelSlot extends SlotItemHandler {
    private final PotionDistilleryMenu menu;

    public DistilleryFuelSlot(PotionDistilleryMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;
    }

    public boolean mayPlace(ItemStack pStack) {
        return this.menu.isFuel(pStack) || isBucket(pStack);
    }

    public int getMaxStackSize(ItemStack pStack) {
        return isBucket(pStack) ? 1 : super.getMaxStackSize(pStack);
    }

    public static boolean isBucket(ItemStack pStack) {
        return pStack.is(Items.BUCKET);
    }
}

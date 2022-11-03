package screret.sas.container.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import screret.sas.block.ModBlocks;
import screret.sas.container.ModContainers;
import screret.sas.container.stackhandler.CraftOutputItemHandler;

public class WandCraftingMenu extends AbstractContainerMenu {

    private static final int INPUT_X_SIZE = 3, INPUT_Y_SIZE = 2;

    private final CraftingContainer inputSlots = new CraftingContainer(this, 3,2){
        @Override
        public void setChanged(){
            super.setChanged();
            WandCraftingMenu.this.slotsChanged(this);
        }
    };

    private final ItemStackHandler resultSlot = new ItemStackHandler(1);

    private final ContainerLevelAccess access;
    private final Player player;

    public WandCraftingMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(ModContainers.WAND_CRAFTING.get(), pContainerId);
        this.access = pAccess;
        this.player = pPlayerInventory.player;
        this.addSlot(new CraftOutputItemHandler(pPlayerInventory.player, this.inputSlots, this.resultSlot, 0, 124, 35));

        for(int y = 0; y < INPUT_Y_SIZE; ++y) {
            for(int x = 0; x < INPUT_X_SIZE; ++x) {
                this.addSlot(new Slot(this.inputSlots, x + y * 3, 30 + x * 18, 17 + y * 18));
            }
        }

        for(int k = 0; k < 3; ++k) {
            for(int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(pPlayerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for(int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(pPlayerInventory, l, 8 + l * 18, 142));
        }

    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            stackCopy = stack.copy();
            if (pIndex == 0) {
                if (!this.moveItemStackTo(stack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, stackCopy);
            } else if (pIndex >= 10 && pIndex < 46) {
                if (!this.moveItemStackTo(stack, 1, 10, false)) {
                    if (pIndex < 37) {
                        if (!this.moveItemStackTo(stack, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(stack, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(stack, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == stackCopy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, stack);
            if (pIndex == 0) {
                pPlayer.drop(stack, false);
            }
        }

        return stackCopy;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return super.stillValid(access, pPlayer, ModBlocks.WAND_CRAFTER.get());
    }
}

package screret.sas.block.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.container.container.WandTableMenu;

public class WandCraftingBlock extends Block {
    private static final Component CONTAINER_TITLE = Component.translatable("container.sas.wand_table");


    public WandCraftingBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((windowId, playerInventory, player) -> new WandTableMenu(windowId, playerInventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }
}

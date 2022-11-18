package screret.sas.item.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import screret.sas.SpellsAndSorcerers;
import screret.sas.resource.EyeConversionManager;

public class CthulhuEyeItem extends Item {
    public CthulhuEyeItem() {
        super(new Item.Properties().tab(SpellsAndSorcerers.SAS_TAB));
    }


    public InteractionResult useOn(UseOnContext pContext) {
        var player = pContext.getPlayer();
        ItemStack itemstack = pContext.getItemInHand();
        var level = pContext.getLevel();
        if(!level.isClientSide){
            BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (hitResult.getType() != HitResult.Type.MISS) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockpos = hitResult.getBlockPos();
                    if (!level.mayInteract(player, blockpos)) {
                        return InteractionResult.SUCCESS;
                    }

                    for (var block : EyeConversionManager.INSTANCE.getAllConversions()){
                        if(block.getValue().test(level.getBlockState(blockpos).getBlock())) {
                            level.setBlockAndUpdate(blockpos, block.getKey().defaultBlockState());
                            return InteractionResult.SUCCESS;
                        }
                    }

                }
            }

        }

        return InteractionResult.PASS;
    }
}

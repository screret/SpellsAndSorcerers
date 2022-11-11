package screret.sas.blockentity.blockentity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import screret.sas.ModTags;
import screret.sas.block.block.SummonSignBlock;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.config.SASConfig;
import screret.sas.entity.ModEntities;
import screret.sas.entity.entity.BossWizardEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SummonSignBE extends BlockEntity implements IAnimatable {
    private static final int TICKS_TO_SPAWN = 40;
    public static final VoxelShape ABOVE = Block.box(0.0D, 1.0D, 0.0D, 16.0D, 17.0D, 16.0D);

    private int ticksToSpawn = -1;
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public SummonSignBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SUMMON_SIGN_BE.get(), pPos, pBlockState);
    }

    public static List<ItemEntity> getItemsAtAndAbove(Level pLevel, SummonSignBE blockEntity) {
        return ABOVE.toAabbs().stream().flatMap((bounds) -> {
            return pLevel.getEntitiesOfClass(ItemEntity.class, bounds.move(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ()), EntitySelector.ENTITY_STILL_ALIVE).stream();
        }).collect(Collectors.toList());
    }

    public static void checkSpawn(Level pLevel, BlockPos pPos, BlockState pState, SummonSignBE pBlockEntity) {
        if (!pLevel.isClientSide) {
            if (pPos.getY() >= pLevel.getMinBuildHeight() && pLevel.getDifficulty() != Difficulty.PEACEFUL) {
                var items = getItemsAtAndAbove(pLevel, pBlockEntity);
                for (var stack : items) {
                    boolean allMatched = ForgeRegistries.ITEMS.tags().getTag(ModTags.Items.BOSS_SUMMON_ITEMS).stream().anyMatch((value) -> stack.getItem().is(value));
                    if(!allMatched)
                        return;
                }
                if(pBlockEntity.ticksToSpawn < 0){
                    pBlockEntity.ticksToSpawn = TICKS_TO_SPAWN;
                    pState.setValue(SummonSignBlock.TRIGGERED, true);
                    return;
                } else if(pBlockEntity.ticksToSpawn > 0){
                    pBlockEntity.ticksToSpawn--;
                    return;
                }

                BossWizardEntity boss = ModEntities.BOSS_WIZARD.get().create(pLevel);
                boss.moveTo(pPos.getX() + 0.5D, pPos.getY() + 1.55D, pPos.getZ() + 0.5D, 0.0F, 0.0F);
                boss.makeInvulnerable();
                for(ServerPlayer serverplayer : pLevel.getEntitiesOfClass(ServerPlayer.class, boss.getBoundingBox().inflate(50.0D))) {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, boss);
                }
                pLevel.addFreshEntity(boss);

                pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 11);
            }
        }
    }

    private PlayState predicate(AnimationEvent<SummonSignBE> event) {
        if(event.getAnimatable().getBlockState().getValue(SummonSignBlock.TRIGGERED)){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.summon_sign.summon", ILoopType.EDefaultLoopTypes.LOOP));
        }else{
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.summon_sign.idle", ILoopType.EDefaultLoopTypes.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}

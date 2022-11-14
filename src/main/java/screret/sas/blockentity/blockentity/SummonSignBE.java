package screret.sas.blockentity.blockentity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import screret.sas.ModTags;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.block.SummonSignBlock;
import screret.sas.blockentity.ModBlockEntities;
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

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SummonSignBE extends BlockEntity implements IAnimatable {
    private static final int TICKS_TO_SPAWN = 100;
    public static final VoxelShape INSIDE = Block.box(-1D, 0.0D, -1D, 17.0D, 16.0D, 17.0D);

    private int ticksToSpawn = -1;
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public SummonSignBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SUMMON_SIGN_BE.get(), pPos, pBlockState);
    }

    public static Set<ItemEntity> getItemsAt(Level pLevel, SummonSignBE blockEntity) {
        return INSIDE.toAabbs().stream()
                .flatMap((bounds) -> pLevel.getEntitiesOfClass(ItemEntity.class, bounds.move(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ()), EntitySelector.ENTITY_STILL_ALIVE).stream())
                .collect(Collectors.toSet());
    }

    public static boolean testForTag(Set<Item> tag, ItemStack pStack, RequiredCounter counter) {
        if (pStack != null) {
            for (var item : tag) {
                if (pStack.is(item)) {
                    tag.remove(item);
                    counter.count++;
                    return true;
                }
            }
        }
        return false;
    }

    private static class RequiredCounter {
        int count = 0;
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, SummonSignBE pBlockEntity) {
        if (pPos.getY() >= pLevel.getMinBuildHeight() && pLevel.getDifficulty() != Difficulty.PEACEFUL) {
            var itemEntities = getItemsAt(pLevel, pBlockEntity);
            Supplier<Stream<ItemStack>> items = () -> itemEntities.stream().map(ItemEntity::getItem);
            var requiredItems = ForgeRegistries.ITEMS.tags().getTag(ModTags.Items.BOSS_SUMMON_ITEMS);
            Set<Item> requiredSet = requiredItems.stream().collect(Collectors.toSet());
            var counter = new RequiredCounter();
            if(!items.get().allMatch(item -> testForTag(requiredSet, item, counter))){
                return;
            }
            if(itemEntities.size() >= counter.count){
                return;
            }

            if(pBlockEntity.ticksToSpawn < 0){
                pBlockEntity.ticksToSpawn = TICKS_TO_SPAWN;
                pBlockEntity.setChanged();
                pLevel.setBlockAndUpdate(pPos, pState.setValue(SummonSignBlock.TRIGGERED, true));
                return;
            } else if(pBlockEntity.ticksToSpawn > 0){
                --pBlockEntity.ticksToSpawn;
                pBlockEntity.setChanged();
                return;
            }

            for (var itemEntity : itemEntities){
                itemEntity.getItem().shrink(1);
            }

            BossWizardEntity boss = ModEntities.BOSS_WIZARD.get().create(pLevel);
            boss.setSpawningPosition(pPos);
            boss.moveTo(pPos.getX() + 0.5f, pPos.getY() + 1.55D, pPos.getZ() + 0.5f, 0.0F, 0.0F);
            boss.makeInvulnerable();
            for(ServerPlayer serverplayer : pLevel.getEntitiesOfClass(ServerPlayer.class, boss.getBoundingBox().inflate(50.0D))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, boss);
            }
            pLevel.addFreshEntity(boss);

            //pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 11);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("TimeToSpawn", this.ticksToSpawn);
    }

    @Override
    public void load(CompoundTag pTag) {
        if(pTag.contains("TimeToSpawn")) this.ticksToSpawn = pTag.getInt("TimeToSpawn");
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

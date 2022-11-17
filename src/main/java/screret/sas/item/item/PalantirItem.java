package screret.sas.item.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;
import screret.sas.client.renderer.item.PalantirItemRenderer;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.function.Consumer;

public class PalantirItem extends BlockItem implements IAnimatable {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public PalantirItem() {
        super(ModBlocks.PALANTIR.get(), new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON).tab(SpellsAndSorcerers.SAS_TAB));
    }


    private PlayState predicate(AnimationEvent<PalantirItem> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.palantir.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new PalantirItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}

package screret.sas.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import screret.sas.block.block.SummonSignBlock;
import screret.sas.blockentity.blockentity.PalantirBE;
import screret.sas.blockentity.blockentity.SummonSignBE;
import screret.sas.client.model.blockentity.PalantirModel;
import screret.sas.client.model.blockentity.SummonSignModel;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class PalantirBERenderer extends GeoBlockRenderer<PalantirBE> {
    public PalantirBERenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, new PalantirModel());
    }

    @Override
    public RenderType getRenderType(PalantirBE animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
    }
}

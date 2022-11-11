package screret.sas.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import screret.sas.block.block.SummonSignBlock;
import screret.sas.blockentity.blockentity.SummonSignBE;
import screret.sas.client.model.blockentity.SummonSignModel;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class SummonSignBERenderer extends GeoBlockRenderer<SummonSignBE> {
    public SummonSignBERenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, new SummonSignModel());
    }

    @Override
    public RenderType getRenderType(SummonSignBE animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
    }

    @Override
    public Color getRenderColor(SummonSignBE animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight) {
        var colors = animatable.getBlockState().getValue(SummonSignBlock.COLOR).getTextureDiffuseColors();
        return Color.ofRGB(colors[0], colors[1], colors[2]);
    }
}

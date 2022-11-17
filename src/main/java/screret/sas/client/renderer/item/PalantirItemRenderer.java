package screret.sas.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import screret.sas.client.model.blockentity.PalantirModel;
import screret.sas.item.item.PalantirItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class PalantirItemRenderer extends GeoItemRenderer<PalantirItem> {
    public PalantirItemRenderer() {
        super(new PalantirModel());
    }

    @Override
    public RenderType getRenderType(PalantirItem animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}

package screret.sas.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import screret.sas.SpellsAndSorcerers;

public class ModSheets {
    public static final ResourceLocation WAND_SHEET = new ResourceLocation(SpellsAndSorcerers.MODID, "textures/atlas/wands.png");
    public static RenderType WAND_SHEET_TYPE = RenderType.itemEntityTranslucentCull(WAND_SHEET);
}

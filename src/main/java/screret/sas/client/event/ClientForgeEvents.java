package screret.sas.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.entity.ModEntities;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.item.ModItems;

@Mod.EventBusSubscriber(modid = SpellsAndSorcerers.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

    private static BossWizardEntity hallucination;
    private static Vec3 randPos;

    @SubscribeEvent
    public static void renderEyeHallucinations(final RenderLevelStageEvent event){
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY){
            if(Minecraft.getInstance().player.getInventory().contains(new ItemStack(ModItems.CTHULHU_EYE.get()))){
                var level = Minecraft.getInstance().level;
                var clientPlayer = Minecraft.getInstance().player;
                var camEntPos = clientPlayer.position();

                if(hallucination == null){
                    hallucination = ModEntities.BOSS_WIZARD.get().create(level);
                    hallucination.setSilent(false);
                    hallucination.moveTo(camEntPos);
                    hallucination.setInvulnerable(true);
                    hallucination.setNoAi(true);
                    level.addFreshEntity(hallucination);
                }

                if(clientPlayer.tickCount % level.getRandom().nextIntBetweenInclusive(80, 120) == 0){
                    switch (level.getRandom().nextInt(3)) {
                        case 0 -> hallucination.playSound(SoundEvents.WITCH_AMBIENT, 10, 1);
                        case 1 -> hallucination.playSound(SoundEvents.CREEPER_PRIMED, 10, 1);
                        case 2 -> hallucination.playSound(SoundEvents.EVOKER_AMBIENT, 10, 1);
                        case 3 -> hallucination.playSound(SoundEvents.BEACON_AMBIENT, 10, 1);
                    }

                    if(level.getRandom().nextInt(3) > 1){
                        hallucination.setInvisible(true);
                    }else{
                        hallucination.setInvisible(false);
                        var randX = Util.randomInRange(level.getRandom(),-10, 10);
                        var randY = Util.randomInRange(level.getRandom(),-5, 5);
                        var randZ = Util.randomInRange(level.getRandom(),-5, -10);
                        randPos = new Vec3(camEntPos.x + randX, camEntPos.y + randY, camEntPos.z + randZ);
                        hallucination.moveTo(camEntPos.x + randX, camEntPos.y + randY, camEntPos.z + randZ);
                    }
                }


                var renderer = Minecraft.getInstance().getEntityRenderDispatcher();

                double xPos = hallucination.getX() - camEntPos.x;
                double zPos = hallucination.getZ() - camEntPos.z;
                hallucination.setYRot((float)((Mth.atan2(xPos, zPos) * Mth.RAD_TO_DEG) - 90.0F));

                double x = Mth.lerp(event.getPartialTick(), hallucination.xOld, hallucination.getX());
                double y = Mth.lerp(event.getPartialTick(), hallucination.yOld, hallucination.getY());
                double z = Mth.lerp(event.getPartialTick(), hallucination.zOld, hallucination.getZ());
                float headYRot = Mth.lerp(event.getPartialTick(), hallucination.yRotO, hallucination.getYRot());

                double playerX = Mth.lerp(event.getPartialTick(), clientPlayer.xOld, camEntPos.x);
                double playerY = Mth.lerp(event.getPartialTick(), clientPlayer.yOld, camEntPos.y);
                double playerZ = Mth.lerp(event.getPartialTick(), clientPlayer.zOld, camEntPos.z);

                //event.getPoseStack().translate(randX, randY, randZ);
                renderer.render(
                        hallucination,
                        x - playerX,
                        y - playerY,
                        z - playerZ,
                        headYRot,
                        event.getPartialTick(),
                        event.getPoseStack(),
                        Minecraft.getInstance().renderBuffers().bufferSource(),
                        renderer.getPackedLightCoords(hallucination, event.getPartialTick())
                );
                //event.getPoseStack().translate(-randX, -randY, -randZ);
            }

        }
    }

}

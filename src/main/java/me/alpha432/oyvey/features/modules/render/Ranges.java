package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Ranges
        extends Module {
    private final Setting<Boolean> hitSpheres = this.register(new Setting<Boolean>("HitSpheres", false));
    private final Setting<Boolean> circle = this.register(new Setting<Boolean>("Circle", true));
    private final Setting<Boolean> ownSphere = this.register(new Setting<Object>("OwnSphere", Boolean.FALSE, v -> this.hitSpheres.getValue()));
    private final Setting<Boolean> raytrace = this.register(new Setting<Object>("RayTrace", Boolean.FALSE, v -> this.circle.getValue()));
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    private final Setting<Double> radius = this.register(new Setting<Double>("Radius", 4.5, 0.1, 8.0));

    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 0, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 0, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));

    public Ranges() {
        super("Ranges", "Draws a circle around the player.", Module.Category.RENDER, false, false, false);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.circle.getValue().booleanValue()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            RenderManager renderManager = mc.getRenderManager();
            float hue = (float)(System.currentTimeMillis() % 7200L) / 7200.0f;
            Color color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
            ArrayList<Vec3d> hVectors = new ArrayList<Vec3d>();
            double x = Ranges.mc.player.lastTickPosX + (Ranges.mc.player.posX - Ranges.mc.player.lastTickPosX) * (double)event.getPartialTicks() - renderManager.renderPosX;
            double y = Ranges.mc.player.lastTickPosY + (Ranges.mc.player.posY - Ranges.mc.player.lastTickPosY) * (double)event.getPartialTicks() - renderManager.renderPosY;
            double z = Ranges.mc.player.lastTickPosZ + (Ranges.mc.player.posZ - Ranges.mc.player.lastTickPosZ) * (double)event.getPartialTicks() - renderManager.renderPosZ;
            GL11.glLineWidth((float)this.lineWidth.getValue().floatValue());
            GL11.glBegin((int)1);
            for (int i = 0; i <= 360; ++i) {
                Vec3d vec = new Vec3d(x + Math.sin((double)i * Math.PI / 180.0) * this.radius.getValue(), y + 0.1, z + Math.cos((double)i * Math.PI / 180.0) * this.radius.getValue());
                RayTraceResult result = Ranges.mc.world.rayTraceBlocks(new Vec3d(Ranges.mc.player.posX, Ranges.mc.player.posY + (double)Ranges.mc.player.getEyeHeight(), Ranges.mc.player.posZ), vec, false, false, true);
                if (result != null && this.raytrace.getValue().booleanValue()) {
                    OyVey.LOGGER.info("raytrace was not null");
                    hVectors.add(result.hitVec);
                    continue;
                }
                hVectors.add(vec);
            }
            for (int j = 0; j < hVectors.size() - 1; ++j) {
                GL11.glColor4f(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
                GL11.glVertex3d((double)((Vec3d)hVectors.get((int)j)).x, (double)((Vec3d)hVectors.get((int)j)).y, (double)((Vec3d)hVectors.get((int)j)).z);
                GL11.glVertex3d((double)((Vec3d)hVectors.get((int)(j + 1))).x, (double)((Vec3d)hVectors.get((int)(j + 1))).y, (double)((Vec3d)hVectors.get((int)(j + 1))).z);
                color = new Color(Color.HSBtoRGB(hue += 0.0027777778f, 1.0f, 1.0f));
            }
            GL11.glEnd();
            GlStateManager.resetColor();
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
        if (this.hitSpheres.getValue().booleanValue()) {
            for (EntityPlayer player : Ranges.mc.world.playerEntities) {
                if (player == null || player.equals((Object)Ranges.mc.player) && !this.ownSphere.getValue().booleanValue()) continue;
                Vec3d interpolated = EntityUtil.interpolateEntity((Entity)player, event.getPartialTicks());
                if (OyVey.friendManager.isFriend(player.getName())) {
                    GL11.glColor4f(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
                } else if (Ranges.mc.player.getDistance((Entity)player) >= 64.0f) {
                    GL11.glColor4f(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
                } else {
                    GL11.glColor4f(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
                }
                RenderUtil.drawSphere(interpolated.x, interpolated.y, interpolated.z, this.radius.getValue().floatValue(), 20, 15);
            }
        }
    }
}
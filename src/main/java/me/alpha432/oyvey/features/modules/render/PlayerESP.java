package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.RenderUtil;

import me.alpha432.oyvey.util.Timer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static me.alpha432.oyvey.util.MathUtil.cos;
import static me.alpha432.oyvey.util.MathUtil.sin;

public class PlayerESP extends Module {

    private final Setting<Integer> targetrange = this.register(new Setting<Integer>("target range", 8, 0, 20));
    private final Setting<Integer> scanred = this.register(new Setting<Integer>("Scan Red", 254, 0, 255));
    private final Setting<Integer> scangreen = this.register(new Setting<Integer>("Scan Green", 216, 0, 255));
    private final Setting<Integer> scanblue = this.register(new Setting<Integer>("Scan Blue", 177, 0, 255));

    private final Setting<Integer> scanalpha = this.register(new Setting<Integer>("Scan alpha", 255, 0, 255));

    private final Setting<Integer> scanLinewidth = this.register(new Setting<Integer>("Scan linewidth", (int) 1.5, 0, 4));

    // public Setting<Boolean> nettionesp = this.register(new Setting<Boolean>("Nettion esp", true));

    private int scanrange = 1;

    // public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", false));

    public Setting<Boolean> enemyattackrange = this.register(new Setting<Boolean>("enemy attackrange", true));

    public Setting<Boolean> scan = this.register(new Setting<Boolean>("scan effect", true));
    public Setting<Boolean> hitbox = this.register(hitbox = new Setting<Boolean>("hitBox", false));
    public Setting<Boolean> health = this.register(health = new Setting<Boolean>("Health", true));
    public Setting<Boolean> arrow = this.register(arrow = new Setting<Boolean>("Arrow", false));
    public Setting<Boolean> ping = this.register(ping = new Setting<Boolean>("ping", false));

    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));

    private final Setting<Integer> alpha = this.register(new Setting<Integer>("alpha", 255, 0, 255));

    private final Setting<Integer> Linewidth = this.register(new Setting<Integer>("linewidth", (int) 1.5, 0, 4));

    private int range = 3;

    private int rgb_c = 0;

    private boolean increasementYpos = true;

    private final Timer movetimer = new Timer();

    public PlayerESP() {
        super("PlayerESP", "nice playerEsp written by neil_huang007", Module.Category.RENDER, true, false, true);
    }

    EntityPlayer target = null;

    double targetypos = 0;

    @Override
    public void onEnable() {
        this.movetimer.reset();
    }

    @Override
    public void onTick() {
        if(increasementYpos){
            targetypos += 0.01;
            if (targetypos >= 2){
                increasementYpos = false;
            }
        }
        else{
            targetypos -= 0.01;
            if (targetypos <= 0){
                increasementYpos = true;
            }
        }
    }




    @Mod.EventHandler
    public void onRender3D(Render3DEvent event) {

        EntityPlayer entityPlayer = target = Target.getClosestEnemy();

        if (this.enemyattackrange.getValue()) {
            RenderUtil.pre3D();
            GL11.glLineWidth(1.0f);
            GL11.glBegin(3);
            GL11.glColor4f(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1.0f);
            for (double d = 0.0; d < 6.283185307179586; d += 0.12319971190548208) {
                final double x = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks() + sin((float) d) * this.range - mc.renderManager.renderPosX;
                final double y = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks() - mc.renderManager.renderPosY;
                final double z = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks() + cos((float) d) * this.range - mc.renderManager.renderPosZ;
                GL11.glVertex3d(x, y, z);
            }
            GL11.glEnd();
            RenderUtil.post3D();
        }
        // ESP
        if(target != null) {
            if (scan.getValue()) {
                RenderUtil.drawTargetCapsule(target, 0.67, true);
            } else if (scan.getValue()) {
                Color color = target.hurtTime > 0?new Color(-1618884):new Color(-13330213);
                double x;
                double y;
                double z;
                mc.getRenderManager();
                x = this.target.lastTickPosX + (this.target.posX - this.target.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX;
                mc.getRenderManager();
                y = this.target.lastTickPosY + (this.target.posY - this.target.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY;
                mc.getRenderManager();
                z = this.target.lastTickPosZ + (this.target.posZ - this.target.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ;
                x -= 0.5;
                z -= 0.5;
                y += this.target.getEyeHeight() + 0.35 - (this.target.isSneaking() ? 0.25 : 0.0);
                final double mid = 0.5;
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glTranslated(x + mid, y + mid, z + mid);
                GL11.glRotated(-this.target.rotationYaw % 360.0f, 0.0, 1.0, 0.0);
                GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
                GL11.glDisable(3553);
                GL11.glEnable(2848);
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
                GL11.glLineWidth(2.0f);
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.5f);
                RenderUtil.drawBoundingBox(new AxisAlignedBB(x + 0.2, y - 0.04, z + 0.2, x + 0.8, y + 0.01, z + 0.8));
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }
        }
    }

}
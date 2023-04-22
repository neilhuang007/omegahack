package me.alpha432.oyvey.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import me.alpha432.oyvey.OyVey;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static me.alpha432.oyvey.util.Util.mc;

public class Utils {

    public static boolean ring_c = false;

    public static boolean isPlayerInGame() {
        return mc.player != null && mc.world != null;
    }

    public static class Player {

        public static void hotkeyToSlot(int slot) {
            if(!isPlayerInGame())
                return;

            mc.player.inventory.currentItem = slot;
        }

        public static boolean isMoving() {
            return mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F;
        }


    }



    public static void drawBoxAroundEntity(Entity e, int type, double expand, double shift, int color, boolean damage) {
        if (e instanceof EntityLivingBase) {
            double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) Client.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosX;
            double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) Client.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosY;
            double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) Client.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosZ;
            float d = (float) expand / 40.0F;
            if (e instanceof EntityPlayer && damage && ((EntityPlayer) e).hurtTime != 0) {
                color = Color.RED.getRGB();
            }

            GlStateManager.pushMatrix();
            if (type == 3) {
                GL11.glTranslated(x, y - 0.2D, z);
                GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                GlStateManager.disableDepth();
                GL11.glScalef(0.03F + d, 0.03F + d, 0.03F + d);
                int outline = Color.black.getRGB();
                net.minecraft.client.gui.Gui.drawRect(-20, -1, -26, 75, outline);
                net.minecraft.client.gui.Gui.drawRect(20, -1, 26, 75, outline);
                net.minecraft.client.gui.Gui.drawRect(-20, -1, 21, 5, outline);
                net.minecraft.client.gui.Gui.drawRect(-20, 70, 21, 75, outline);
                if (color != 0) {
                    net.minecraft.client.gui.Gui.drawRect(-21, 0, -25, 74, color);
                    net.minecraft.client.gui.Gui.drawRect(21, 0, 25, 74, color);
                    net.minecraft.client.gui.Gui.drawRect(-21, 0, 24, 4, color);
                    net.minecraft.client.gui.Gui.drawRect(-21, 71, 25, 74, color);
//                } else {
//                    int st = Client.rainbowDraw(2L, 0L);
//                    int en = Client.rainbowDraw(2L, 1000L);
//                    dGR(-21, 0, -25, 74, st, en);
//                    dGR(21, 0, 25, 74, st, en);
//                    net.minecraft.client.gui.Gui.drawRect(-21, 0, 21, 4, en);
//                    net.minecraft.client.gui.Gui.drawRect(-21, 71, 21, 74, st);
//                }

                    GlStateManager.enableDepth();
                } else {
                    int i;
                    if (type == 4) {
                        EntityLivingBase en = (EntityLivingBase) e;
                        double r = en.getHealth() / en.getMaxHealth();
                        int b = (int) (74.0D * r);
                        int hc = r < 0.3D ? Color.red.getRGB() : (r < 0.5D ? Color.orange.getRGB() : (r < 0.7D ? Color.yellow.getRGB() : Color.green.getRGB()));
                        GL11.glTranslated(x, y - 0.2D, z);
                        GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                        GlStateManager.disableDepth();
                        GL11.glScalef(0.03F + d, 0.03F + d, 0.03F + d);
                        i = (int) (21.0D + shift * 2.0D);
                        net.minecraft.client.gui.Gui.drawRect(i, -1, i + 5, 75, Color.black.getRGB());
                        net.minecraft.client.gui.Gui.drawRect(i + 1, b, i + 4, 74, Color.darkGray.getRGB());
                        net.minecraft.client.gui.Gui.drawRect(i + 1, 0, i + 4, b, hc);
                        GlStateManager.enableDepth();
//                } else if (type == 6) {
//                    d3p(x, y, z, 0.699999988079071D, 45, 1.5F, color, color == 0);
//                } else {
//                    if (color == 0) {
//                        color = Client.rainbowDraw(2L, 0L);
//                    }
//
//                    float a = (float)(color >> 24 & 255) / 255.0F;
//                    float r = (float)(color >> 16 & 255) / 255.0F;
//                    float g = (float)(color >> 8 & 255) / 255.0F;
//                    float b = (float)(color & 255) / 255.0F;
//                    if (type == 5) {
//                        GL11.glTranslated(x, y - 0.2D, z);
//                        GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
//                        GlStateManager.disableDepth();
//                        GL11.glScalef(0.03F + d, 0.03F, 0.03F + d);
//                        int base = 1;
//                        d2p(0.0D, 95.0D, 10, 3, Color.black.getRGB());
//
//                        for(i = 0; i < 6; ++i) {
//                            d2p(0.0D, 95 + (10 - i), 3, 4, Color.black.getRGB());
//                        }
//
//                        for(i = 0; i < 7; ++i) {
//                            d2p(0.0D, 95 + (10 - i), 2, 4, color);
//                        }
//
//                        d2p(0.0D, 95.0D, 8, 3, color);
//                        GlStateManager.enableDepth();
//                    } else {
//                        AxisAlignedBB bbox = e.getEntityBoundingBox().expand(0.1D + expand, 0.1D + expand, 0.1D + expand);
//                        AxisAlignedBB axis = new AxisAlignedBB(bbox.minX - e.posX + x, bbox.minY - e.posY + y, bbox.minZ - e.posZ + z, bbox.maxX - e.posX + x, bbox.maxY - e.posY + y, bbox.maxZ - e.posZ + z);
//                        GL11.glBlendFunc(770, 771);
//                        GL11.glEnable(3042);
//                        GL11.glDisable(3553);
//                        GL11.glDisable(2929);
//                        GL11.glDepthMask(false);
//                        GL11.glLineWidth(2.0F);
//                        GL11.glColor4f(r, g, b, a);
//                        if (type == 1) {
//                            RenderGlobal.drawSelectionBoundingBox(axis);
//                        } else if (type == 2) {
//                            dbb(axis, r, g, b);
//                        }
//
//                        GL11.glEnable(3553);
//                        GL11.glEnable(2929);
//                        GL11.glDepthMask(true);
//                        GL11.glDisable(3042);
//                    }
//                }
//            }

                        GlStateManager.popMatrix();
                    }
                }
            }
        }
    }

    public static class Client {

        public static java.util.List<NetworkPlayerInfo> getPlayers() {
            java.util.List<NetworkPlayerInfo> yes = new ArrayList<>();
            List<NetworkPlayerInfo> mmmm = new ArrayList<>();
            try {
                yes.addAll(mc.getConnection().getPlayerInfoMap());
            } catch (NullPointerException r) {
                return yes;
            }

            for (NetworkPlayerInfo ergy43d : yes) {
                if (!mmmm.contains(ergy43d)) {
                    mmmm.add(ergy43d);
                }
            }

            return mmmm;
        }

        public static boolean othersExist() {
            for (Entity wut : mc.world.getLoadedEntityList()) {
                if (wut instanceof EntityPlayer) return true;
            }
            return false;
        }

        public static void setMouseButtonState(int mouseButton, boolean held) {
            MouseEvent m = new MouseEvent();

            ObfuscationReflectionHelper.setPrivateValue(MouseEvent.class, m, mouseButton, "button");
            ObfuscationReflectionHelper.setPrivateValue(MouseEvent.class, m, held, "buttonstate");
            MinecraftForge.EVENT_BUS.post(m);

            ByteBuffer buttons = ObfuscationReflectionHelper.getPrivateValue(Mouse.class, null, "buttons");
            buttons.put(mouseButton, (byte) (held ? 1 : 0));
            ObfuscationReflectionHelper.setPrivateValue(Mouse.class, null, buttons, "buttons");

        }

        public static net.minecraft.util.Timer getTimer() {
            return ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "timer", "field_71428_T");
        }

    }

}

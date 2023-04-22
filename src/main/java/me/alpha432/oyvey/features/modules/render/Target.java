package me.alpha432.oyvey.features.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.FontMod;
//import me.alpha432.oyvey.features.modules.combat.Killaura;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DecimalFormat;
import java.util.Objects;

public class Target
        extends Module {
    private static Target INSTANCE = new Target();
    public Setting<Boolean> targetHudBackground = this.register(new Setting<Boolean>("TargetHudBackground", true));
    public Setting<Integer> backgroundAlpha = this.register(new Setting<Object>("Background Alpha", Integer.valueOf(160), Integer.valueOf(0), Integer.valueOf(255), v -> this.targetHudBackground.getValue()));
    public Setting<Integer> targetHudX = this.register(new Setting<Integer>("TargetHudX", 0, 0, 1000));
    public Setting<Integer> targetHudY = this.register(new Setting<Integer>("TargetHudY", 0, 0, 1000));
    public Setting<Integer> red = this.register(new Setting<Integer>("Background-Red", 20, 0, 255));
    public Setting<Integer> green = this.register(new Setting<Integer>("Background-Green", 20, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<Integer>("Background-Blue", 20, 0, 255));
    private int startcolor1;
    private int endcolor1;

    public Target() {
        super("Target", "Displays Target", Module.Category.RENDER, false, false, true);
        this.setInstance();
    }

    public static Target getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Target();
        }
        return INSTANCE;
    }

    public static EntityPlayer getClosestEnemy() {
        EntityPlayer closestPlayer = null;
        for (EntityPlayer player : Target.mc.world.playerEntities) {
            if (player == Target.mc.player || OyVey.friendManager.isFriend(player)) continue;
            if (closestPlayer == null) {
                closestPlayer = player;
                continue;
            }
            if (Target.mc.player.getDistanceSq((Entity)player) >= Target.mc.player.getDistanceSq((Entity)closestPlayer)) continue;
            closestPlayer = player;
        }
        return closestPlayer;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (Target.fullNullCheck()) {
            return;
        }
        this.drawTargetHud(event.partialTicks, (EntityPlayer)Target.mc.player);
    }

    public void drawTargetHud(float partialTicks, EntityPlayer player) {
        EntityPlayer target = null;
        String pingStr = "";
        try {
            int responseTime = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(Target.mc.player.getUniqueID()).getResponseTime();
            pingStr = pingStr + responseTime + "";
        }
        catch (Exception responseTime) {
            // empty catch block
        }
//        EntityPlayer entityPlayer = target = Killaura.target instanceof EntityPlayer ? (EntityPlayer)Killaura.target : Target.getClosestEnemy();
        EntityPlayer entityPlayer = target = Target.getClosestEnemy();
        if (target == null) {
            return;
        }
        if (target.isDead) {
            target = null;
        }
        if (target != null) {
            int distancecolor = 0;
            int color = 0;
            int j;
            String hole;
            if (this.targetHudBackground.getValue().booleanValue()) {
                float f = target.getHealth() + target.getAbsorptionAmount();
                int j2 = f >= 33.0f ? ColorUtil.toRGBA(0, 255, 0, 255) : (f >= 30.0f ? ColorUtil.toRGBA(150, 255, 0, 255) : (f > 25.0f ? ColorUtil.toRGBA(75, 255, 0, 255) : (f > 20.0f ? ColorUtil.toRGBA(255, 255, 0, 255) : (f > 15.0f ? ColorUtil.toRGBA(255, 200, 0, 255) : (f > 10.0f ? ColorUtil.toRGBA(255, 150, 0, 255) : (f > 5.0f ? ColorUtil.toRGBA(255, 50, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255)))))));
                RenderUtil.drawRectangleCorrectly(this.targetHudX.getValue(), this.targetHudY.getValue(), 170, 90, ColorUtil.toRGBA(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.backgroundAlpha.getValue()));
                this.startcolor1 = j2;
                this.endcolor1 = j2;
                RenderUtil.drawGradientSideways(this.targetHudX.getValue().intValue(), (double)this.targetHudY.getValue().intValue() + 84.0, (double)this.targetHudX.getValue().intValue() + (double)f * 4.722222, (double)this.targetHudY.getValue().intValue() + 86.8, this.startcolor1, this.endcolor1);
            }
            GlStateManager.disableRescaleNormal();
            GlStateManager.setActiveTexture((int) OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            try {
                GuiInventory.drawEntityOnScreen((int)(this.targetHudX.getValue() + 30), (int)(this.targetHudY.getValue() + 60), (int)30, (float)0.0f, (float)0.0f, (EntityLivingBase)target);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            int distance = (int)target.getDistance((Entity)Target.mc.player);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
            this.renderer.drawStringWithShadow(target.getName(), this.targetHudX.getValue() + 60, this.targetHudY.getValue() + 10, ColorUtil.toRGBA(255, 255, 255, 255));
            if (!EntityUtil.isInHole((Entity)target)) {
                hole = "Vunerable";
                j = ColorUtil.toRGBA(255, 0, 0, 255);
                this.renderer.drawStringWithShadow("" + ChatFormatting.BOLD + hole, this.targetHudX.getValue() + 60, this.targetHudY.getValue() + this.renderer.getFontHeight() + 40, j);
            } else {
                hole = "Safe";
                j = ColorUtil.toRGBA(0, 255, 0, 255);
                this.renderer.drawStringWithShadow("" + ChatFormatting.BOLD + hole, this.targetHudX.getValue() + 60, this.targetHudY.getValue() + this.renderer.getFontHeight() + 40, j);
            }
            float healthLine = target.getHealth() + target.getAbsorptionAmount();
            int lineColor = healthLine >= 33.0f ? ColorUtil.toRGBA(0, 255, 0, 255) : (healthLine >= 30.0f ? ColorUtil.toRGBA(150, 255, 0, 255) : (healthLine > 25.0f ? ColorUtil.toRGBA(75, 255, 0, 255) : (healthLine > 20.0f ? ColorUtil.toRGBA(255, 255, 0, 255) : (healthLine > 15.0f ? ColorUtil.toRGBA(255, 200, 0, 255) : (healthLine > 10.0f ? ColorUtil.toRGBA(255, 150, 0, 255) : (healthLine > 5.0f ? ColorUtil.toRGBA(255, 50, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255)))))));
            DecimalFormat df = new DecimalFormat("##.#");
            this.renderer.drawStringWithShadow("", this.targetHudX.getValue() + 60 + this.renderer.getStringWidth(""), this.targetHudY.getValue() + 10, lineColor);
            Integer ping = (mc.getConnection().getPlayerInfo(target.getUniqueID()) == null ? 0 : mc.getConnection().getPlayerInfo(target.getUniqueID()).getResponseTime());
            int n = ping >= 100 ? ColorUtil.toRGBA(0, 255, 0, 255) : (color = ping > 50 ? ColorUtil.toRGBA(255, 255, 0, 255) : ColorUtil.toRGBA(255, 255, 255, 255));
            int n2 = distance <= 5 ? ColorUtil.toRGBA(255, 0, 0, 255) : (distance < 10 ? ColorUtil.toRGBA(255, 100, 0, 255) : (distance < 20 ? ColorUtil.toRGBA(255, 150, 0, 255) : (distance < 30 ? ColorUtil.toRGBA(255, 200, 0, 255) : (distance < 50 ? ColorUtil.toRGBA(255, 255, 0, 255) : (distancecolor = distance < 100 ? ColorUtil.toRGBA(150, 255, 0, 255) : ColorUtil.toRGBA(255, 255, 255, 255))))));
            if (FontMod.getInstance().isOn()) {
                this.renderer.drawStringWithShadow("Health: " + healthLine, this.targetHudX.getValue() + 60, this.targetHudY.getValue() + this.renderer.getFontHeight() + 12, lineColor);
            } else {
                this.renderer.drawStringWithShadow("Health: " + healthLine, this.targetHudX.getValue() + 60, this.targetHudY.getValue() + this.renderer.getFontHeight() + 10, lineColor);
            }
            this.renderer.drawStringWithShadow("Ping: " + pingStr, this.targetHudX.getValue() + 60, this.targetHudY.getValue() + this.renderer.getFontHeight() + 20, color);
            this.renderer.drawStringWithShadow("Distance: " + distance, this.targetHudX.getValue() + 60, this.targetHudY.getValue() + this.renderer.getFontHeight() + 30, distancecolor);
            this.drawOverlay(partialTicks, (Entity)target, this.targetHudX.getValue() + 120, this.targetHudY.getValue() + 35);
            GlStateManager.enableTexture2D();
            int iteration = 0;
            int i = this.targetHudX.getValue() + 50;
            int y = this.targetHudY.getValue() + this.renderer.getFontHeight() * 3 + 44;
            for (ItemStack is : target.inventory.armorInventory) {
                ++iteration;
                if (is.isEmpty()) continue;
                int x = i - 90 + (9 - iteration) * 20 + 2;
                GlStateManager.enableDepth();
                RenderUtil.itemRender.zLevel = 200.0f;
                if (FontMod.getInstance().isOn()) {
                    RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, i - 150 + (9 - iteration) * 20 + 2, y + 4);
                    RenderUtil.itemRender.renderItemOverlayIntoGUI(Target.mc.fontRenderer, is, i - 150 + (9 - iteration) * 20 + 2, y + 4, "");
                } else {
                    RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, i - 150 + (9 - iteration) * 20 + 2, y - 2);
                    RenderUtil.itemRender.renderItemOverlayIntoGUI(Target.mc.fontRenderer, is, i - 150 + (9 - iteration) * 20 + 2, y - 2, "");
                }
                RenderUtil.itemRender.zLevel = 0.0f;
                GlStateManager.enableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                String s = is.getCount() > 1 ? is.getCount() + "" : "";
                this.renderer.drawStringWithShadow(s, x - 50 - this.renderer.getStringWidth(s), y + 9, 0xFFFFFF);
                int dmg = 0;
                int itemDurability = is.getMaxDamage() - is.getItemDamage();
                float green = ((float)is.getMaxDamage() - (float)is.getItemDamage()) / (float)is.getMaxDamage();
                float red = 1.0f - green;
                dmg = 100 - (int)(red * 100.0f);
                if (FontMod.getInstance().isOn()) {
                    this.renderer.drawStringWithShadow(dmg + "", x - 47 - this.renderer.getStringWidth(dmg + ""), y - 2, ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
                    continue;
                }
                this.renderer.drawStringWithShadow(dmg + "", x - 47 - this.renderer.getStringWidth(dmg + ""), y - 8, ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
            }
        }
    }

    public void drawOverlay(float partialTicks, Entity player, int x, int y) {
        BlockPos westPos;
        Block west;
        BlockPos eastPos;
        Block east;
        BlockPos southPos;
        Block south;
        float yaw = 0.0f;
        int dir = MathHelper.floor((double)((double)(player.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
        switch (dir) {
            case 1: {
                yaw = 90.0f;
                break;
            }
            case 2: {
                yaw = -180.0f;
                break;
            }
            case 3: {
                yaw = -90.0f;
            }
        }
        BlockPos northPos = this.traceToBlock(partialTicks, yaw, player);
        Block north = this.getBlock(northPos);
        if (north != null && north != Blocks.AIR) {
            int damage = this.getBlockDamage(northPos);
            if (damage != 0) {
                RenderUtil.drawRect(x + 16, y, x + 32, y + 16, 0x60FF0000);
            }
            this.drawBlock(north, x + 16, y);
        }
        if ((south = this.getBlock(southPos = this.traceToBlock(partialTicks, yaw - 180.0f, player))) != null && south != Blocks.AIR) {
            int damage = this.getBlockDamage(southPos);
            if (damage != 0) {
                RenderUtil.drawRect(x + 16, y + 32, x + 32, y + 48, 0x60FF0000);
            }
            this.drawBlock(south, x + 16, y + 32);
        }
        if ((east = this.getBlock(eastPos = this.traceToBlock(partialTicks, yaw + 90.0f, player))) != null && east != Blocks.AIR) {
            int damage = this.getBlockDamage(eastPos);
            if (damage != 0) {
                RenderUtil.drawRect(x + 32, y + 16, x + 48, y + 32, 0x60FF0000);
            }
            this.drawBlock(east, x + 32, y + 16);
        }
        if ((west = this.getBlock(westPos = this.traceToBlock(partialTicks, yaw - 90.0f, player))) != null && west != Blocks.AIR) {
            int damage = this.getBlockDamage(westPos);
            if (damage != 0) {
                RenderUtil.drawRect(x, y + 16, x + 16, y + 32, 0x60FF0000);
            }
            this.drawBlock(west, x, y + 16);
        }
    }

    private int getBlockDamage(BlockPos pos) {
        for (DestroyBlockProgress destBlockProgress : Target.mc.renderGlobal.damagedBlocks.values()) {
            if (destBlockProgress.getPosition().getX() != pos.getX() || destBlockProgress.getPosition().getY() != pos.getY() || destBlockProgress.getPosition().getZ() != pos.getZ()) continue;
            return destBlockProgress.getPartialBlockDamage();
        }
        return 0;
    }

    private BlockPos traceToBlock(float partialTicks, float yaw, Entity player) {
        Vec3d pos = EntityUtil.interpolateEntity(player, partialTicks);
        Vec3d dir = MathUtil.direction(yaw);
        return new BlockPos(pos.x + dir.x, pos.y, pos.z + dir.z);
    }

    private Block getBlock(BlockPos pos) {
        Block block = Target.mc.world.getBlockState(pos).getBlock();
        if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) {
            return block;
        }
        return Blocks.AIR;
    }

    private void drawBlock(Block block, float x, float y) {
        ItemStack stack = new ItemStack(block);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.translate((float)x, (float)y, (float)0.0f);
        Target.mc.getRenderItem().zLevel = 501.0f;
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
        Target.mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
    }
}

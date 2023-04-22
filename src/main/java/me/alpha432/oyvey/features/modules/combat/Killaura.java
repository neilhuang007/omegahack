//package nettion.features.module.modules.combat;
//
//import me.alpha432.oyvey.features.modules.Module;
//import me.alpha432.oyvey.features.setting.Setting;
//import net.minecraft.client.settings.KeyBinding;
//
//import net.minecraftforge.fml.common.Mod;
//import org.lwjgl.opengl.GL11;
//
//
//
//import net.minecraft.client.Minecraft;
//
//import net.minecraft.util.*;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.monster.EntityMob;
//import net.minecraft.entity.passive.EntityAnimal;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.item.ItemSword;
//
//
//import static java.lang.Math.cos;
//import static java.lang.Math.sin;
//
//@NativeClass
//public class Killaura extends Module {
//    public static EntityLivingBase target;
//    private List targets = new ArrayList(0);
//
//    private final Setting<Integer> targetrange = this.register(new Setting<Integer>("target range", 8, 0, 20));
//    public static Mode<Enum> mode = new Mode<>("Mode", AuraMode.values(), AuraMode.Switch);
//    private final Numbers<Double> cps = new Numbers<>("CPS", 10.0, 1.0, 20.0, 1.0);
//    private final Numbers<Double> range = new Numbers<>("Range", 4.3, 1.0, 8.0, 0.1);
//    private final Numbers<Double> fov = new Numbers<>("Fov", 180.0, 30.0, 180.0, 1.0);
//    private final Option<Boolean> players = new Option<>("Players", true);
//    private final Option<Boolean> animals = new Option<>("Animals", true);
//    private final Option<Boolean> mobs = new Option<>("Mobs", false);
//    private final Option<Boolean> invisible = new Option<>("Invisible", false);
//    private final Option<Boolean> inventoryCheck = new Option<>("InventoryCheck", false);
//    private final Option<Boolean> keepSprint = new Option<>("KeepSprint", true);
//    private final Option<Boolean> visRange = new Option<>("Visualize Range", true);
//    private final Option<Boolean> autodis = new Option<>("AutoDisabler", true);
//    private final Option<Boolean> nosa = new Option<>("ScaffoldCheck",  false);
//    public static Mode<Enum> blockmode = new Mode<>("BlockMode", BlockMode.values(), BlockMode.Normal);
//    public static Mode<Enum> rotationmode = new Mode<>("Rotation", RotMode.values(), RotMode.Normal);
//    public static Mode<Enum> atime = new Mode<>("AttackTime", AT.values(), AT.Pre);
//    public static Mode<Enum> swingMod = new Mode<>("Swing", swingMode.values(), swingMode.Normal);
//    public static Mode<Enum> esp = new Mode<>("ESP", espMode.values(), espMode.Nettion);
//    public static Boolean isBlocking = false;
//    public static int kills;
//    private float iyaw;
//    private float ipitch;
//    private Comparator<Entity> angleComparator = Comparator.comparingDouble(e2 -> e2.getDistanceToEntity(mc.player));
//
//    public Killaura() {
//        super("KillAura", "op killaura",Module.Category.COMBAT,true,false,true);
//        this.addValues(cps, range, fov, mode, rotationmode, atime, blockmode, swingMod, esp, players, animals, mobs, invisible, nosa, inventoryCheck, keepSprint, autodis, visRange);
//    }
//
//    @Mod.EventHandler
//    private void onKills(EventPreUpdate e) {
//        if (target != null && (target.getHealth() <= 0 || target.isDead)) {
//            kills += 1;
//        }
//    }
//
//    @Override
//    public void onDisable() {
//        mc.gameSettings.keyBindUseItem.pressed = false;
//        target = null;
//        if (isBlocking) {
//            unBlock();
//        }
//    }
//
//    @Override
//    public void onEnable() {
//        this.iyaw = mc.thePlayer.rotationYaw;
//        this.ipitch = mc.thePlayer.rotationPitch;
//    }
//
//    @Mod.EventHandler
//    public void onRender3D(EventRender3D event) {
//        if (this.visRange.getValue()) {
//            RenderUtils.pre3D();
//            GL11.glLineWidth(1.0f);
//            GL11.glBegin(3);
//            GL11.glColor4f(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1.0f);
//            for (double d = 0.0; d < 6.283185307179586; d += 0.12319971190548208) {
//                final double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.getPartialTicks() + sin(d) * this.range.getValue() - RenderManager.renderPosX;
//                final double y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.getPartialTicks() - RenderManager.renderPosY;
//                final double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.getPartialTicks() + cos(d) * this.range.getValue() - RenderManager.renderPosZ;
//                GL11.glVertex3d(x, y, z);
//            }
//            GL11.glEnd();
//            RenderUtils.post3D();
//        }
//        // ESP
//        if(target != null) {
//            if (esp.getValue() == espMode.Nettion) {
//                RenderUtils.drawTargetCapsule(target, 0.67, true);
//            } else if (esp.getValue() == espMode.LiquidBounce) {
//                Color color = target.hurtTime > 0?new Color(-1618884):new Color(-13330213);
//                double x;
//                double y;
//                double z;
//                x = Killaura.target.lastTickPosX + (Killaura.target.posX - Killaura.target.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
//                mc.getRenderManager();
//                y = Killaura.target.lastTickPosY + (Killaura.target.posY - Killaura.target.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
//                mc.getRenderManager();
//                z = Killaura.target.lastTickPosZ + (Killaura.target.posZ - Killaura.target.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
//                x -= 0.5;
//                z -= 0.5;
//                y += Killaura.target.getEyeHeight() + 0.35 - (Killaura.target.isSneaking() ? 0.25 : 0.0);
//                final double mid = 0.5;
//                GL11.glPushMatrix();
//                GL11.glEnable(3042);
//                GL11.glBlendFunc(770, 771);
//                GL11.glTranslated(x + mid, y + mid, z + mid);
//                GL11.glRotated(-Killaura.target.rotationYaw % 360.0f, 0.0, 1.0, 0.0);
//                GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
//                GL11.glDisable(3553);
//                GL11.glEnable(2848);
//                GL11.glDisable(2929);
//                GL11.glDepthMask(false);
//                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
//                GL11.glLineWidth(2.0f);
//                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.5f);
//                RenderUtils.drawBoundingBox(new AxisAlignedBB(x + 0.2, y - 0.04, z + 0.2, x + 0.8, y + 0.01, z + 0.8));
//                GL11.glDisable(2848);
//                GL11.glEnable(3553);
//                GL11.glEnable(2929);
//                GL11.glDepthMask(true);
//                GL11.glDisable(3042);
//                GL11.glPopMatrix();
//            }
//        }
//    }
//
//    private TimerUtils SwitchTimer = new TimerUtils();
//
//    @EventHandler
//    private void onUpdate(EventPreUpdate event) {
//        this.setSuffix("Switch");
//        if (this.inventoryCheck.getValue() && mc.currentScreen != null) {
//            return;
//        }
//        if (this.nosa.getValue() && Nettion.instance.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()) {
//            return;
//        }
//        this.targets = this.getTargets();
//        if (target instanceof EntityPlayer || target instanceof EntityMob || target instanceof EntityAnimal) {
//            target = null;
//            if (isBlocking) {
//                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
//                unBlock();
//            }
//        }
//        targets.sort(this.angleComparator);
//        if (targets.size() > 0) {
//            if (mode.getValue() == AuraMode.Switch) {
//                if (SwitchTimer.delay(100L)) {
//                    SwitchTimer.reset();
//                }
//                if (target != null) {
//                    target = null;
//                }
//            }
//            target = (EntityLivingBase) this.targets.get(0);
//            if (target != null) {
//                if (rotationmode.getValue() == RotMode.Normal) {
//                    float[] rotations = Rotation.getAngles(target);
//                    float rotation1 = (float)((double)rotations[0]);
//                    float rotation2 = rotations[1] + 1;
//                    Rotation.setRotations(event, rotation1, rotation2);
//                } else if (rotationmode.getValue() == RotMode.Smooth) {
//                    float frac = MathHelper.clamp_float(1.0f - 50 / 100.0f, 0.1f, 1.0f);
//                    float[] rotations = Rotation.getAngles(target);
//                    this.iyaw += (rotations[0] - this.iyaw) * frac;
//                    this.ipitch += (rotations[1] - this.ipitch) * frac;
//                    event.setYaw(iyaw);
//                    event.setPitch(ipitch);
//                    Rotation.setRotations(event, iyaw, ipitch);
//                } else if (rotationmode.getValue() == RotMode.Test) {
//                    float frac = MathHelper.clamp_float(1.0f - 50 / 100.0f, 0.1f, 1.0f);
//                    float[] rotations = Rotation.getAngles(target);
//                    this.iyaw += (rotations[0] - mc.thePlayer.rotationYaw) * frac;
//                    this.ipitch += (rotations[1] - mc.thePlayer.rotationPitch) * frac;
//                    event.setYaw(iyaw);
//                    event.setPitch(ipitch);
//                    Rotation.setRotations(event, iyaw, ipitch);
//                }
//
//                if (this.shouldAttack()) {
//                    if (mc.thePlayer.getDistanceToEntity(target) <= range.getValue().floatValue()) {
//                        if (target.isEntityAlive()) {
//                            if (atime.getValue() == AT.Pre) {
//                                Attack(target);
//                            }
//                            if (!(blockmode.getValue() == BlockMode.None)) {
//                                block();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    private void onWorldChanged(EventWorldLoad e) {
//        if (this.autodis.getValue()) {
//            ClientUtils.sendClientMessage("KillAura has been automatically disabled.", 2500);
//            this.setEnabled(false);
//        }
//    }
//
//    @EventHandler
//    private void onPost(EventPostUpdate event) {
//        if (this.inventoryCheck.getValue() && mc.currentScreen != null) {
//            return;
//        }
//        if (targets.size() > 0) {
//            if (mode.getValue() == AuraMode.Switch) {
//                if (SwitchTimer.delay(100L)) {
//                    SwitchTimer.reset();
//                }
//                if (target != null) {
//                    target = null;
//                }
//            }
//            target = (EntityLivingBase) this.targets.get(0);
//            if (target != null) {
//                if (this.shouldAttack()) {
//                    if (mc.thePlayer.getDistanceToEntity(target) <= range.getValue().floatValue()) {
//                        if (target.isEntityAlive()) {
//                            if (atime.getValue() == AT.Post) {
//                                Attack(target);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    private void onTickAttack(EventTick e) {
//        if (this.inventoryCheck.getValue() && mc.currentScreen != null) {
//            return;
//        }
//        if (this.nosa.getValue() && Nettion.instance.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()) {
//            return;
//        }
//        if (targets.size() > 0) {
//            if (mode.getValue() == AuraMode.Switch) {
//                if (SwitchTimer.delay(100L)) {
//                    SwitchTimer.reset();
//                }
//                if (target != null) {
//                    target = null;
//                }
//            }
//            target = (EntityLivingBase) this.targets.get(0);
//            if (target != null) {
//                if (this.shouldAttack()) {
//                    if (mc.thePlayer.getDistanceToEntity(target) <= range.getValue().floatValue()) {
//                        if (target.isEntityAlive()) {
//                            if (atime.getValue() == AT.Tick) {
//                                Attack(target);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private final TimerUtils AttackTimer = new TimerUtils();
//
//    private boolean shouldAttack() {
//        return this.AttackTimer.hasReached(1000.0 / (this.cps.getValue() + ((double) 0)));
//    }
//
//    private void Attack(EntityLivingBase e) {
//        if (swingMod.getValue() == swingMode.Normal) {
//            mc.thePlayer.swingItem();
//        } else if (swingMod.getValue() == swingMode.Packet) {
//            mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
//        }
//        if (!this.keepSprint.getValue()) {
//            Minecraft.playerController.attackEntity(mc.thePlayer, e);
//        } else {
//            PacketUtils.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
//        }
//    }
//
//    @EventHandler
//    private void onUpdatePost(EventPostUpdate e) {
//
//    }
//
//    private void unBlock() {
//        if (isBlocking && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && (!(blockmode.getValue() == BlockMode.None) || mc.gameSettings.keyBindUseItem.isPressed())) {
//            if (blockmode.getValue() != BlockMode.Hypixel) {
//                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
//                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
//            }
//            if (blockmode.getValue() == BlockMode.Hypixel) {
//                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
//            }
//            mc.gameSettings.keyBindUseItem.pressed = false;
//            isBlocking = false;
//        }
//    }
//
//    private void block() {
//        if (target != null) {
//            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
//                if (blockmode.getValue() == BlockMode.Normal) {
//                    PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
//                    this.isBlocking = true;
//                } else if (blockmode.getValue() == BlockMode.Hypixel) {
//                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
//                    this.isBlocking = true;
//                }
//            }
//        }
//    }
//
//    public List<Entity> getTargets() {
//        return mc.world.loadedEntityList.stream().filter(e -> (double) mc.player.getDistanceSq((Entity) e) <= targetrange.getValue() && CanAttack((Entity) e)).collect(Collectors.toList());
//    }
//
//    private boolean CanAttack(Entity e) {
//        if (e == mc.thePlayer) {
//            return false;
//        }
//        AntiBot ab = (AntiBot) Nettion.instance.getModuleManager().getModuleByClass(AntiBot.class);
//        if (e != null && !Rotation.isVisibleFOV(e, fov.getValue().intValue())) {
//            return false;
//        }
//        if (ab.isServerBot(e) && ab.isEnabled()) {
//            return false;
//        }
//        if (!e.isEntityAlive()) {
//            return false;
//        }
//        if (FriendManager.isFriend(e.getName())) {
//            return false;
//        }
//        if (e instanceof EntityPlayer && players.getValue().booleanValue() && !Teams.isOnSameTeam(e)) {
//            return true;
//        }
//        if (e instanceof EntityMob && mobs.getValue().booleanValue()) {
//            return true;
//        }
//        if (e instanceof EntityAnimal && animals.getValue().booleanValue()) {
//            return true;
//        }
//        if (e.isInvisible() && invisible.getValue().booleanValue() && e instanceof EntityPlayer) {
//            return true;
//        }
//        return false;
//    }
//
//    public enum AuraMode {
//        Switch,
//    }
//
//    public enum BlockMode {
//        Normal,
//        Hypixel,
//        Fake,
//        None,
//    }
//
//    public enum RotMode {
//        Normal,
//        Smooth,
//        Test
//    }
//
//    public enum AT {
//        Pre,
//        Post,
//        Tick,
//    }
//
//    public enum swingMode {
//        Normal,
//        Packet,
//        None,
//    }
//
//    public enum espMode {
//        Nettion,
//        LiquidBounce,
//        None
//    }
//}
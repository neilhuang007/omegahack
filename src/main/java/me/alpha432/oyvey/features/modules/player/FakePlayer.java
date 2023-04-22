package me.alpha432.oyvey.features.modules.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

public class FakePlayer
        extends Module {
    private static FakePlayer INSTANCE = new FakePlayer();
    public Setting<Boolean> hollow = this.register(new Setting<Boolean>("Move", false));
    private EntityOtherPlayerMP otherPlayer;

    public FakePlayer() {
        super("FakePlayer", "fake player", Module.Category.PLAYER, false, false, false);
    }

    public static FakePlayer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakePlayer();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (this.otherPlayer != null) {
            Random random = new Random();
            this.otherPlayer.moveForward = FakePlayer.mc.player.moveForward + (float)random.nextInt(5) / 10.0f;
            this.otherPlayer.moveStrafing = FakePlayer.mc.player.moveStrafing + (float)random.nextInt(5) / 10.0f;
            if (this.hollow.getValue().booleanValue()) {
                this.travel(this.otherPlayer.moveStrafing, this.otherPlayer.moveVertical, this.otherPlayer.moveForward);
            }
        }
    }

    public void travel(float strafe, float vertical, float forward) {
        double d0 = this.otherPlayer.posY;
        float f1 = 0.8f;
        float f2 = 0.02f;
        float f3 = EnchantmentHelper.getDepthStriderModifier((EntityLivingBase)this.otherPlayer);
        if (f3 > 3.0f) {
            f3 = 3.0f;
        }
        if (!this.otherPlayer.onGround) {
            f3 *= 0.5f;
        }
        if (f3 > 0.0f) {
            f1 += (0.54600006f - f1) * f3 / 3.0f;
            f2 += (this.otherPlayer.getAIMoveSpeed() - f2) * f3 / 4.0f;
        }
        this.otherPlayer.moveRelative(strafe, vertical, forward, f2);
        this.otherPlayer.move(MoverType.SELF, this.otherPlayer.motionX, this.otherPlayer.motionY, this.otherPlayer.motionZ);
        this.otherPlayer.motionX *= (double)f1;
        this.otherPlayer.motionY *= (double)0.8f;
        this.otherPlayer.motionZ *= (double)f1;
        if (!this.otherPlayer.hasNoGravity()) {
            this.otherPlayer.motionY -= 0.02;
        }
        if (this.otherPlayer.collidedHorizontally && this.otherPlayer.isOffsetPositionInLiquid(this.otherPlayer.motionX, this.otherPlayer.motionY + (double)0.6f - this.otherPlayer.posY + d0, this.otherPlayer.motionZ)) {
            this.otherPlayer.motionY = 0.3f;
        }
    }

    @Override
    public void onEnable() {
        if (FakePlayer.mc.world == null || FakePlayer.mc.player == null) {
            this.toggle();
            return;
        }
        if (this.otherPlayer == null) {
            this.otherPlayer = new EntityOtherPlayerMP((World)FakePlayer.mc.world, new GameProfile(UUID.randomUUID(), "neil_huang007"));
            this.otherPlayer.copyLocationAndAnglesFrom((Entity)FakePlayer.mc.player);
            this.otherPlayer.inventory.copyInventory(FakePlayer.mc.player.inventory);
        }
        FakePlayer.mc.world.spawnEntity((Entity)this.otherPlayer);
    }

    @Override
    public void onDisable() {
        if (this.otherPlayer != null) {
            FakePlayer.mc.world.removeEntity((Entity)this.otherPlayer);
            this.otherPlayer = null;
        }
    }
}


package com.brandon3055.draconicevolution.world;

import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerBossInfo;

/**
 * Created by brandon3055 on 24/7/21
 */
public class ShieldedServerBossInfo extends ServerBossInfo {

    private float shieldPower = 0;
    private int crystals = 0;
    private boolean immune = false;

    public ShieldedServerBossInfo(ITextComponent name, Color color, Overlay overlay) {
        super(name, color, overlay);
    }

    public void setShieldPower(float shieldPower) {
        if (this.shieldPower != shieldPower) {
            this.shieldPower = shieldPower;
            if (isVisible()) {
                for (ServerPlayerEntity player : players) {
                    DraconicNetwork.sendBossShieldPacket(player, getId(), 2, e -> e.writeFloat(this.shieldPower));
                }
            }
        }
    }

    public void setCrystals(int crystals) {
        if (this.crystals != crystals) {
            this.crystals = crystals;
            if (isVisible()) {
                for (ServerPlayerEntity player : players) {
                    DraconicNetwork.sendBossShieldPacket(player, getId(), 3, e -> e.writeByte(this.crystals));
                }
            }
        }
    }

    public void setImmune(boolean immune) {
        if (this.immune != immune) {
            this.immune = immune;
            if (isVisible()) {
                for (ServerPlayerEntity player : players) {
                    DraconicNetwork.sendBossShieldPacket(player, getId(), 4, e -> e.writeBoolean(this.immune));
                }
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        boolean prev = isVisible();
        super.setVisible(visible);
        if (prev != visible) {
            for (ServerPlayerEntity player : players) {
                if (visible) {
                    DraconicNetwork.sendBossShieldPacket(player, getId(), 0, e -> e.writeFloat(shieldPower).writeByte(crystals).writeBoolean(immune));
                }else {
                    DraconicNetwork.sendBossShieldPacket(player, getId(), 1, null);
                }
            }
        }
    }

    @Override
    public void addPlayer(ServerPlayerEntity player) {
        if (players.add(player) && isVisible()) {
            player.connection.send(new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.ADD, this));
            DraconicNetwork.sendBossShieldPacket(player, getId(), 0, e -> e.writeFloat(shieldPower).writeByte(crystals).writeBoolean(immune));
        }
    }

    @Override
    public void removePlayer(ServerPlayerEntity player) {
        if (players.remove(player) && isVisible()) {
            player.connection.send(new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.REMOVE, this));
            DraconicNetwork.sendBossShieldPacket(player, getId(), 1, null);
        }
    }
}

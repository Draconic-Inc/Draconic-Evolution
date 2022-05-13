package com.brandon3055.draconicevolution.world;

import com.brandon3055.draconicevolution.network.DraconicNetwork;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

/**
 * Created by brandon3055 on 24/7/21
 */
public class ShieldedServerBossInfo extends ServerBossEvent {

    private float shieldPower = 0;
    private int crystals = 0;
    private boolean immune = false;

    public ShieldedServerBossInfo(Component name, BossBarColor color, BossBarOverlay overlay) {
        super(name, color, overlay);
    }

    public void setShieldPower(float shieldPower) {
        if (this.shieldPower != shieldPower) {
            this.shieldPower = shieldPower;
            if (isVisible()) {
                for (ServerPlayer player : players) {
                    DraconicNetwork.sendBossShieldPacket(player, getId(), 2, e -> e.writeFloat(this.shieldPower));
                }
            }
        }
    }

    public void setCrystals(int crystals) {
        if (this.crystals != crystals) {
            this.crystals = crystals;
            if (isVisible()) {
                for (ServerPlayer player : players) {
                    DraconicNetwork.sendBossShieldPacket(player, getId(), 3, e -> e.writeByte(this.crystals));
                }
            }
        }
    }

    public void setImmune(boolean immune) {
        if (this.immune != immune) {
            this.immune = immune;
            if (isVisible()) {
                for (ServerPlayer player : players) {
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
            for (ServerPlayer player : players) {
                if (visible) {
                    DraconicNetwork.sendBossShieldPacket(player, getId(), 0, e -> e.writeFloat(shieldPower).writeByte(crystals).writeBoolean(immune));
                }else {
                    DraconicNetwork.sendBossShieldPacket(player, getId(), 1, null);
                }
            }
        }
    }

    @Override
    public void addPlayer(ServerPlayer player) {
        if (players.add(player) && isVisible()) {
            player.connection.send(ClientboundBossEventPacket.createAddPacket(this));
            DraconicNetwork.sendBossShieldPacket(player, getId(), 0, e -> e.writeFloat(shieldPower).writeByte(crystals).writeBoolean(immune));
        }
    }

    @Override
    public void removePlayer(ServerPlayer player) {
        if (players.remove(player) && isVisible()) {
            player.connection.send(ClientboundBossEventPacket.createRemovePacket(this.getId()));
            DraconicNetwork.sendBossShieldPacket(player, getId(), 1, null);
        }
    }
}

package com.brandon3055.draconicevolution.entity.guardian;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.world.ChaosIslandFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

import java.util.List;

public enum GuardianSpawnState {
    START_WAIT_FOR_PLAYER {
        public void process(ServerLevel world, GuardianFightManager manager, List<EndCrystal> crystals, int ticks, BlockPos pos) {
            if (ticks % 20 == 0) {
                for (ServerPlayer player : manager.getTrackedPlayers()) {
                    if (pos.above(15).distSqr(player.blockPosition()) <= 15*15) {
                        DraconicEvolution.LOGGER.info("Player In Range. Guardian spawn progressing to PREPARING_TO_SUMMON_PILLARS");
                        manager.setRespawnState(PREPARING_TO_SUMMON_PILLARS);
                    }
                }
            }
        }
    },
    PREPARING_TO_SUMMON_PILLARS {
        public void process(ServerLevel world, GuardianFightManager manager, List<EndCrystal> crystals, int ticks, BlockPos pos) {
            if (ticks < 70) {
                if (ticks == 0 || ticks == 20 || ticks == 21 || ticks == 22 || ticks >= 65) {
                    world.levelEvent(3001, new BlockPos(pos.getX(), 128, pos.getZ()), 0);
                }
            } else {
                manager.setRespawnState(SUMMONING_PILLARS);
                DraconicEvolution.LOGGER.info("Guardian spawn progressing to SUMMONING_PILLARS");
            }
        }
    },
    SUMMONING_PILLARS {
        public void process(ServerLevel level, GuardianFightManager manager, List<EndCrystal> crystals, int ticks, BlockPos pos) {
            int spawnRate = 15;
            boolean spawn = ticks % spawnRate == 0;

            if (spawn) {
                BlockPos nextSpawn = manager.getNextCrystalPos(ticks == 0);
                if (nextSpawn != null) {
                    //Clear the spawn area
                    for (BlockPos blockpos : BlockPos.betweenClosed(nextSpawn.offset(-10, -10, -10), nextSpawn.offset(10, 10, 10))) {
                        level.removeBlock(blockpos, false);
                    }

                    ChaosIslandFeature.generateObelisk(level, nextSpawn, level.random);
                    level.setBlock(nextSpawn, DEContent.INFUSED_OBSIDIAN.get().defaultBlockState(), 3);
                    GuardianCrystalEntity crystal = new GuardianCrystalEntity(level.getLevel(), nextSpawn.getX() + 0.5, nextSpawn.getY() + 1, nextSpawn.getZ() + 0.5, manager.getUniqueID());
                    crystal.setInvulnerable(true);
                    level.addFreshEntity(crystal);
                    crystal.setBeamTarget(pos.offset(0, 80, 0));
                    manager.crystalSpawned();
                } else {
                    manager.setRespawnState(SUMMONING_GUARDIAN);
                    DraconicEvolution.LOGGER.info("Guardian spawn progressing to SUMMONING_GUARDIAN");
                }
            }
        }
    },
    SUMMONING_GUARDIAN {
        public void process(ServerLevel world, GuardianFightManager manager, List<EndCrystal> crystals, int ticks, BlockPos pos) {
            if (ticks >= 50) {
                manager.setRespawnState(END);
                manager.resetCrystals();
            } else if (ticks >= 30) {
                world.levelEvent(3001, pos.offset(0, 80, 0), 0);
            } else if (ticks < 5) {
                world.levelEvent(3001, pos.offset(0, 80, 0), 0);
            }
        }
    },
    END {
        public void process(ServerLevel world, GuardianFightManager manager, List<EndCrystal> crystals, int ticks, BlockPos pos) {
        }
    };

    private GuardianSpawnState() {
    }

    public abstract void process(ServerLevel world, GuardianFightManager manager, List<EndCrystal> crystals, int ticks, BlockPos pos);
}

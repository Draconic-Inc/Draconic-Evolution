package com.brandon3055.draconicevolution.common.world;

import com.brandon3055.brandonscore.common.utills.SimplexNoise;
import com.brandon3055.brandonscore.common.world.BlockCollection;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorImproved;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

/**
 * Created by brandon3055 on 1/9/2015.
 */
public class WorldGenChaosIsland {

    private int spawnHeight = 128;
    private int size;
    private BlockCollection blockCollection;
    public boolean initialized = false;

    public void initialize(Random random) {
        generate(blockCollection, random);
        initialized = true;
    }

    public BlockCollection getBlocks(Random random) {
        if (!initialized) initialize(random);
        return blockCollection;
    }

    public boolean generate(BlockCollection world, Random random) {
        // LogHelper.info("Generate");
        //		for (int y1 = y - 10; y1 < y + 10; y1++) {
        //			if (world.getBlock(x, y1, z) == Blocks.end_stone) {
        //				//LogHelper.info("cancel");
        //				return false;
        //			}
        //		}
        size = 400;

        generateCentre(world, random);
        //		generateBelt(world, random, size + 50, size + 200);
        //		generateObelisks(world, random);
        //		EntityChaosGuardian dragon = new EntityChaosGuardian(world);
        //		dragon.setPositionAndUpdate(x, 180, z);
        //		world.spawnEntityInWorld(dragon);
        return true;
    }

    double[] noiseData;

    private void generateCentre(BlockCollection blocks, Random rand) {
        // int centreThikness = 10; //multiplied by 2 and + 1
        int curve = 2;
        int diffStart = 20; // (int)((double)size * 0.1D);
        // int offPoint = size * curve;

        int coreDiameter = 90;

        NoiseGeneratorSimplex simX = new NoiseGeneratorSimplex(rand);
        NoiseGeneratorSimplex simY = new NoiseGeneratorSimplex(rand);
        NoiseGeneratorSimplex simZ = new NoiseGeneratorSimplex(rand);
        NoiseGeneratorImproved generatorImproved = new NoiseGeneratorImproved(rand);
        generatorImproved.populateNoiseArray(new double[size * 255 * size], 0, 0, 0, size, 255, size, 10, 10, 10, 500);
        NoiseGeneratorOctaves generatorOctaves = new NoiseGeneratorOctaves(rand, 8);

        boolean f = true;

        int randOffset = rand.nextInt(10000);

        for (int x = 0; x <= size; x++) {
            f = true;
            for (int z = 0; z <= size; z++) {
                for (int y = 0; y <= (size / 2); y++) {

                    double dist = Math.sqrt(x * x + (y - spawnHeight) * (y - spawnHeight) + z * z);

                    double xf, yf, zf;
                    xf = (double) x / size;
                    yf = (double) y / (size / 2);
                    zf = (double) z / size;

                    double density, center_falloff, plateau_falloff, densityZ;

                    center_falloff =
                            0.1; /// (Math.pow((xf - 0.5) * 1.5, 2) + Math.pow((yf - 1.0) * 0.8, 2) + Math.pow((zf -
                    // 0.5) * 1.5, 2));

                    double diameterScale = 100D;
                    // center_falloff = 1;//(1D - (dist / diameterScale)) * 4D;
                    if (center_falloff < 0) center_falloff = 0;

                    if (yf <= 0.8) {
                        plateau_falloff = 1.0;
                    } else if (0.8 < yf && yf < 0.9) {
                        plateau_falloff = 1.0 - (yf - 0.8) * 10.0;
                    } else {
                        plateau_falloff = 0.0;
                    }

                    density = 0.1;
                    for (int octave = 0; octave < 4; octave++) {
                        // density += Math.abs(SimplexNoise.noise(xf*Math.pow(2, octave), yf*Math.pow(2, octave),
                        // zf*Math.pow(2, octave)));
                    }

                    density *= center_falloff * plateau_falloff;

                    // if (y % 50 == 0) LogHelper.info((density) + " " + center_falloff + " " + yf);
                    // LogHelper.info(yf);
                    f = false;

                    //					//setup fields
                    //
                    //
                    //					dist += rand.nextInt(4) - 2;
                    //
                    //					//Generate core island
                    //					double yMod = dist / (500D/dist+1D);
                    //					if (dist <= coreDiameter && y <= spawnY + yMod){
                    //						blocks.setBlock(x, y, z, Blocks.end_stone);
                    //					}
                    //

                }
            }
        }
    }

    public static void generate(World world, int trueX, int y, int trueZ, int offsetX, int offsetZ) {
        int x = trueX - offsetX;
        int z = trueZ - offsetZ;

        int size = 300;

        // double dist = Math.sqrt(x*x + (y-spawnHeight)*(y-spawnHeight) + z*z);

        double xf, yf, zf;
        xf = (double) x / size;
        yf = (double) y / (256);
        zf = (double) z / size;

        double density, center_falloff, plateau_falloff, densityZ;

        center_falloff =
                0.1; /// (Math.pow((xf - 0.5) * 1.5, 2) + Math.pow((yf - 1.0) * 0.8, 2) + Math.pow((zf - 0.5) * 1.5,
        // 2));

        double diameterScale = 100D;
        // center_falloff = 1;//(1D - (dist / diameterScale)) * 4D;
        if (center_falloff < 0) center_falloff = 0;

        if (yf <= 0.8) {
            plateau_falloff = 1.0;
        } else if (0.8 < yf && yf < 0.9) {
            plateau_falloff = 1.0 - (yf - 0.8) * 10.0;
        } else {
            plateau_falloff = 0.0;
        }

        density = 0.1;
        for (int octave = 0; octave < 4; octave++) {
            density += Math.abs(
                    SimplexNoise.noise(xf * Math.pow(2, octave), yf * Math.pow(2, octave), zf * Math.pow(2, octave)));
        }

        // density *= center_falloff * plateau_falloff;

        if (y % 50 == 0) LogHelper.info((density) + " " + center_falloff + " " + yf);
        // LogHelper.info(yf);

        if (density > 1) world.setBlock(x + offsetX, y, z + offsetZ, Blocks.end_stone);
    }

    //					if(yf <= 0.8){
    //						plateau_falloff = 1.0F;
    //					}
    //					else if(0.8 < yf && yf < 0.9){
    //						plateau_falloff = 1.0F-(yf-0.8F)*10.0F;
    //					}
    //					else{
    //						plateau_falloff = 0.0F;
    //					}
    //
    //					center_falloff = 0.1F/ (float)(
    //							Math.pow((xf - 0.5F) * 1.5F, 2) +
    //									Math.pow((yf - 1.0F) * 0.8F, 2F) +
    //									Math.pow((zf - 0.5F) * 1.5F, 2F)
    //					);
    //					caves = (float)Math.pow(simplex_noise(1, xf * 5, yf * 5, zf * 5), 3);
    // density = ( simplex_noise(5, xf, yf * 0.5F, zf));// * center_falloff * plateau_falloff);

    // density *= Math.pow( noise((xf+1)*3.0F, (yf+1)*3.0F, (zf+1)*3.0F)+0.4, 1.8F);
    //					if(caves<0.5){
    //						density = 0;
    //					}

    // put(x, y, z, density>3.1 ? ROCK : 0);
    // endfor

    //	public float dot(float x, float y, float z, float... g){
    //		return x*g[0] + y*g[1] + z*g[2];
    //	}
    //
    // public static int[] perm = {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103,
    // 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117,
    // 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48,
    // 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102,
    // 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159,
    // 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85,
    // 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70,
    // 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104,
    // 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
    // 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93,
    // 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180, 151, 160, 137, 91, 90, 15, 131, 13, 201,
    // 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234,
    // 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136,
    // 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230,
    // 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208,
    // 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124,
    // 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183,
    // 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108,
    // 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179,
    // 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121,
    // 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156,
    // 180};
    //
    //	public float[][] grad = {
    //		{1.0F,1.0F,0.0F},{-1.0F,1.0F,0.0F},{1.0F,-1.0F,0.0F},{-1.0F,-1.0F,0.0F},
    //		{1.0F,0.0F,1.0F},{-1.0F,0.0F,1.0F},{1.0F,0.0F,-1.0F},{-1.0F,0.0F,-1.0F},
    //		{0.0F,1.0F,1.0F},{0.0F,-1.0F,1.0F},{0.0F,1.0F,-1.0F},{0.0F,-1.0F,-1.0F}
    //		};
    //
    // public float noise(float xin, float yin, float zin){
    //		float F3, G3, t, X0, Y0, Z0, x0, y0, z0, s, x1, y1, z1, x2, y2, z2, x3, y3, z3, t0, t1, t2, t3, n0, n1, n2, n3;
    //		int i, j, k, ii, jj, kk, i1, j1, k1, i2, j2, k2, gi0, gi1, gi2, gi3;
    //
    //		F3 = 1.0F/3.0F;
    //		s = (xin+yin+zin)*F3;
    //		i = (int)(xin+s);
    //		j = (int)(yin+s);
    //		k = (int)(zin+s);
    //		G3 = 1.0F/6.0F;
    //		t = (i+j+k)*G3;
    //		X0 = i-t;
    //		Y0 = j-t;
    //		Z0 = k-t;
    //		x0 = xin-X0;
    //		y0 = yin-Y0;
    //		z0 = zin-Z0;
    //
    //		if(x0 >= y0){
    //			if(y0 >= z0){
    //				i1=1; j1=0; k1=0; i2=1; j2=1; k2=0;
    //			}
    //			else if(x0 >= z0){
    //				i1=1; j1=0; k1=0; i2=1; j2=0; k2=1;
    //			}
    //			else{
    //				i1=0; j1=0; k1=1; i2=1; j2=0; k2=1;
    //			}
    //		}
    //		else{
    //			if(y0 < z0){
    //				i1=0; j1=0; k1=1; i2=0; j2=1; k2=1;
    //			}
    //			else if(x0 < z0){
    //				i1=0; j1=1; k1=0; i2=0; j2=1; k2=1;
    //			}
    //			else{
    //				i1=0; j1=1; k1=0; i2=1; j2=1; k2=0;
    //			}
    //		}
    //
    //		x1 = x0 - i1 + G3;
    //		y1 = y0 - j1 + G3;
    //		z1 = z0 - k1 + G3;
    //		x2 = x0 - i2 + 2.0F*G3;
    //		y2 = y0 - j2 + 2.0F*G3;
    //		z2 = z0 - k2 + 2.0F*G3;
    //		x3 = x0 - 1.0F + 3.0F*G3;
    //		y3 = y0 - 1.0F + 3.0F*G3;
    //		z3 = z0 - 1.0F + 3.0F*G3;
    //
    //		ii = i & 255;
    //		jj = j & 255;
    //		kk = k & 255;
    //
    //		gi0 = perm[ii+perm[jj+perm[kk]]] % 12;
    //		gi1 = perm[ii+i1+perm[jj+j1+perm[kk+k1]]] % 12;
    //		gi2 = perm[ii+i2+perm[jj+j2+perm[kk+k2]]] % 12;
    //		gi3 = perm[ii+1+perm[jj+1+perm[kk+1]]] % 12;
    //
    //		t0 = 0.6F - x0*x0 - y0*y0 - z0*z0;
    //		if(t0<0){
    //			n0 = 0.0F;
    //		}
    //		else{
    //			t0 *= t0;
    //			n0 = t0 * t0 * dot(x0, y0, z0, grad[gi0]);
    //		}
    //
    //		t1 = 0.6F - x1*x1 - y1*y1 - z1*z1;
    //		if(t1<0){
    //			n1 = 0.0F;
    //		}
    //		else{
    //			t1 *= t1;
    //			n1 = t1 * t1 * dot(x1, y1, z1, grad[gi1]);
    //		}
    //
    //		t2 = 0.6F - x2*x2 - y2*y2 - z2*z2;
    //		if(t2<0){
    //			n2 = 0.0F;
    //		}
    //		else{
    //			t2 *= t2;
    //			n2 = t2 * t2 * dot(x2, y2, z2, grad[gi2]);
    //		}
    //
    //		t3 = 0.6F - x3*x3 - y3*y3 - z3*z3;
    //		if(t3<0){
    //			n3 = 0.0F;
    //		}
    //		else{
    //			t3 *= t3;
    //			n3 = t3 * t3 * dot(x3, y3, z3, grad[gi3]);
    //		}
    //
    //		return 16.0F*(n0 + n1 + n2 + n3)+1.0F;
    //	}
    //
    //	public float simplex_noise(int octaves, float x, float y, float z){
    //		float value = 0.0F;
    //		int i;
    //		for(i=0; i<octaves; i++){
    //			value += noise(x*(float)Math.pow(2, i), y*(float)Math.pow(2, i), z*(float)Math.pow(2, i));
    //		}
    //		return value;
    //	}

    //	private void generateCentre(BlockCollection world, Random rand) {
    //		int centreThikness = 10; //multiplied by 2 and + 1
    //		int curve = 2;
    //		int diffStart = 20;//(int)((double)size * 0.1D);
    //		int r = size;
    //		int offPoint = size * curve;
    //
    //		for (int x = spawnX - r; x <= spawnX + r; x++) {
    //			for (int z = spawnZ - r; z <= spawnZ + r; z++) {
    //				for (int y = spawnY - (r / 2); y <= spawnY + (r / 2); y++) {
    //					if ((int) (Utills.getDistanceAtoB(x, y, z, spawnX, spawnY + offPoint + centreThikness, spawnZ)) >= offPoint
    // && (int) (Utills.getDistanceAtoB(x, y, z, spawnX, spawnY - offPoint - centreThikness, spawnZ)) >= offPoint &&
    // (int) (Utills.getDistanceAtoB(x, y, z, spawnX, spawnY, spawnZ)) <= r) {
    //
    //						int dist = (int) Math.sqrt((x-spawnX)*(x-spawnX) + (y-spawnY)*(y-spawnY) + (z-spawnZ)*(z-spawnZ));
    //
    //						int diffusionPCt = getDiffusionPct(dist - diffStart, size - diffStart);
    //						int yRand = (int) (Math.max(0, getDiffusionPctD(dist - 40, size - 40)) * (double) (40 - rand.nextInt(80)));
    //
    //						if (dist <= diffStart) {
    //							if (world.isAirBlock(x, y + yRand, z)) {
    //								world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
    //							}
    //						}
    //						else if (dist < (int) ((double) size * 0.3D) && diffusionPCt > rand.nextInt(3000)) {
    //							if (0.95F > rand.nextFloat()) {
    //								if (world.isAirBlock(x, y + yRand, z)) {
    //									world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
    //								}
    //							}
    //							else if (world.isAirBlock(x, y + yRand, z)) {
    //								world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
    //							}
    //						}
    //						else if (dist < (int) ((double) size * 0.4D) && diffusionPCt > rand.nextInt(4000)) {
    //							if (0.95F > rand.nextFloat()) {
    //								if (world.isAirBlock(x, y + yRand, z)) {
    //									world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
    //								}
    //							}
    //							else if (world.isAirBlock(x, y + yRand, z)) {
    //								world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
    //							}
    //						}
    //						else if (dist < (int) ((double) size * 0.5D) && diffusionPCt > rand.nextInt(5000)) {
    //							if (0.95F > rand.nextFloat()) {
    //								if (world.isAirBlock(x, y + yRand, z)) {
    //									world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
    //								}
    //							}
    //							else if (world.isAirBlock(x, y + yRand, z)) {
    //								world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
    //							}
    //						}
    //						else if (dist < (int) ((double) size * 0.6D) && diffusionPCt > rand.nextInt(6000)) {
    //							if (0.95F > rand.nextFloat()) {
    //								if (world.isAirBlock(x, y + yRand, z)) {
    //									world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
    //								}
    //							}
    //							else if (world.isAirBlock(x, y + yRand, z)) {
    //								world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
    //							}
    //						}
    //						else if (dist < (int) ((double) size * 0.7D) && diffusionPCt > rand.nextInt(7000)) {
    //							if (0.95F > rand.nextFloat()) {
    //								if (world.isAirBlock(x, y + yRand, z)) {
    //									world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
    //								}
    //							}
    //							else if (world.isAirBlock(x, y + yRand, z)) {
    //								world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
    //							}
    //						}
    //						else if (dist < (int) ((double) size * 0.8D) && diffusionPCt > rand.nextInt(8000)) {
    //							if (0.95F > rand.nextFloat()) {
    //								if (world.isAirBlock(x, y + yRand, z)) {
    //									world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
    //								}
    //							}
    //							else if (world.isAirBlock(x, y + yRand, z)) {
    //								world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
    //							}
    //						}
    //						else if (diffusionPCt > rand.nextInt(9000)) {
    //							if (0.95F > rand.nextFloat()) {
    //								if (world.isAirBlock(x, y + yRand, z)) {
    //									world.setBlock(x, y + yRand, z, Blocks.end_stone, 0, 2);
    //								}
    //							}
    //							else if (world.isAirBlock(x, y + yRand, z)) {
    //								world.setBlock(x, y + yRand, z, Blocks.obsidian, 0, 2);
    //							}
    //						}
    //					}
    //				}
    //			}
    //		}
    //	}

    private int getDiffusionPct(int dist, int maxDist) {
        double d = (double) dist / (double) maxDist;
        int i = Math.max(1, (int) (d * 1000D));
        return 1000 - i;
    }

    private double getDiffusionPctD(int dist, int maxDist) {
        double d = (double) dist / (double) maxDist;
        return d;
    }

    private void generateObelisks(BlockCollection world, Random rand) {

        //		for (int i = 0; i < 7; i++) {
        //			double rotation = i * 0.9D;
        //			int sX = spawnX + (int) (Math.sin(rotation) * 35D);
        //			int sZ = spawnZ + (int) (Math.cos(rotation) * 35D);
        //			generateObelisk(world, sX, spawnY + 10, sZ, false, rand);
        //		}
        //
        //		for (int i = 0; i < 14; i++) {
        //			double rotation = i * 0.45D;
        //			int sX = spawnX + (int) (Math.sin(rotation) * 70D);
        //			int sZ = spawnZ + (int) (Math.cos(rotation) * 70D);
        //			generateObelisk(world, sX, spawnY + 10, sZ, true, rand);
        //		}

    }

    private void generateObelisk(BlockCollection world, int x1, int y1, int z1, boolean outer, Random rand) {
        //		if (!outer) {
        //			world.setBlock(x1, y1 + 20, z1, ModBlocks.infusedObsidian, 0, 2);
        ////			if (!world.isRemote) {
        ////				EntityChaosCrystal crystal = new EntityChaosCrystal(world);
        ////				crystal.setPosition(x1 + 0.5, y1 + 21, z1 + 0.5);
        ////				world.spawnEntityInWorld(crystal);
        ////			}
        //			for (int y = y1; y < y1 + 20; y++) {
        //				world.setBlock(x1, y, z1, Blocks.obsidian, 0, 2);
        //				world.setBlock(x1 + 1, y, z1, Blocks.obsidian, 0, 2);
        //				world.setBlock(x1 - 1, y, z1, Blocks.obsidian, 0, 2);
        //				world.setBlock(x1, y, z1 + 1, Blocks.obsidian, 0, 2);
        //				world.setBlock(x1, y, z1 - 1, Blocks.obsidian, 0, 2);
        //				world.setBlock(x1 + 1, y, z1 + 1, Blocks.obsidian, 0, 2);
        //				world.setBlock(x1 - 1, y, z1 - 1, Blocks.obsidian, 0, 2);
        //				world.setBlock(x1 + 1, y, z1 - 1, Blocks.obsidian, 0, 2);
        //				world.setBlock(x1 - 1, y, z1 + 1, Blocks.obsidian, 0, 2);
        //			}
        //		} else {
        //			world.setBlock(x1, y1 + 40, z1, ModBlocks.infusedObsidian, 0, 2);
        ////			if (!world.isRemote) {
        ////				EntityChaosCrystal crystal = new EntityChaosCrystal(world);
        ////				crystal.setPosition(x1 + 0.5, y1 + 41, z1 + 0.5);
        ////				world.spawnEntityInWorld(crystal);
        ////			}
        //			int diff = 0;
        //			for (int y = y1 + 20; y < y1 + 40; y++) {
        //				diff++;
        //				double pct = (double) diff / 25D;
        //				int r = 3;
        //				for (int x = x1 - r; x <= x1 + r; x++) {
        //					for (int z = z1 - r; z <= z1 + r; z++) {
        //						if (Utills.getDistanceAtoB(x, z, x1, z1) <= r) {
        //							if (pct > rand.nextDouble()) world.setBlock(x, y, z, Blocks.obsidian, 0, 2);
        //						}
        //					}
        //				}
        //			}
        //
        //
        //			int cageS = 2;
        //			for (int x = x1 - cageS; x <= x1 + cageS; x++) {
        //				for (int y = y1 - cageS; y <= y1 + cageS; y++) {
        //					if (0.8F > rand.nextFloat()) world.setBlock(x, y + 41, z1 + cageS, Blocks.iron_bars, 0, 2);
        //					if (0.8F > rand.nextFloat()) world.setBlock(x, y + 41, z1 - cageS, Blocks.iron_bars, 0, 2);
        //				}
        //			}
        //			for (int z = z1 - cageS; z <= z1 + cageS; z++) {
        //				for (int y = y1 - cageS; y <= y1 + cageS; y++) {
        //					if (0.8F > rand.nextFloat()) world.setBlock(x1 + cageS, y + 41, z, Blocks.iron_bars, 0, 2);
        //					if (0.8F > rand.nextFloat()) world.setBlock(x1 - cageS, y + 41, z, Blocks.iron_bars, 0, 2);
        //				}
        //			}
        //			for (int z = z1 - cageS; z <= z1 + cageS; z++) {
        //				for (int x = x1 - cageS; x <= x1 + cageS; x++) {
        //					if (0.8F > rand.nextFloat()) world.setBlock(x, y1 + 44, z, Blocks.stone_slab, 6, 2);
        //				}
        //			}
        //
        //		}
    }

    private void generateBelt(BlockCollection world, Random random, int innerRadius, int outerRadius) {
        //		int r = outerRadius;
        //		for (int x = spawnX - r; x <= spawnX + r; x++) {
        //			for (int z = spawnZ - r; z <= spawnZ + r; z++) {
        //				int dist = (int) (Utills.getDistanceAtoB(x, z, spawnX, spawnZ));
        //				if (dist < outerRadius && dist >= innerRadius) {
        //					int y = spawnY + (int) ((double) (spawnX - x) * 0.2D) + (random.nextInt(10) - 5);
        //					if (0.1F > random.nextFloat()) world.setBlock(x, y, z, Blocks.end_stone, 0, 2);
        //					if (0.001F > random.nextFloat()) world.setBlock(x, y, z, ModBlocks.draconiumOre, 0, 2);
        //				}
        //			}
        //		}
    }
}

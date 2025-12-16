package net.vampirestudios.raaMaterials.material;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public final class MeteoritePlacer {
    public static void place(RandomSource rng, WorldGenLevel world, BlockPos chunkOrigin, MeteoriteSpec spec) {
//        final var replaceable = TargetMatcher.from(spec.replaceables());
//
//        for (int a = 0; a < spec.attemptsPerChunk(); a++) {
//            if (rng.nextDouble() > spec.chance()) continue;
//
//            int x = chunkOrigin.getX() + rng.nextInt(16);
//            int z = chunkOrigin.getZ() + rng.nextInt(16);
//            int surfaceY = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
//            int centerY = spec.surfaceOnly() ? surfaceY
//                    : surfaceY - rng.nextInt(1 + Math.max(0, spec.maxBuryDepth()));
//
//            int R = Mth.nextInt(rng, spec.minRadius(), spec.maxRadius());
//            float e = (float) spec.ellipticity();
//            float depth = (float) spec.depthScale();
//            BlockPos center = new BlockPos(x, centerY, z);
//
//            MeteoritePlacerImpl.carveCrater(world, center, R, e, depth, spec, rng, replaceable);
//            MeteoritePlacerImpl.placeCore(world, center.below(Mth.ceil(R * 0.35f)), R, spec, rng, replaceable);
//            MeteoritePlacerImpl.heatAlter(world, center, R, spec, rng);
//            MeteoritePlacerImpl.scatterFragments(world, center, R, spec, rng, replaceable);
//        }
    }
}

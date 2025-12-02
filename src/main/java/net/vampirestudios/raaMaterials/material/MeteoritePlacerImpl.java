package net.vampirestudios.raaMaterials.material;// imports you likely need:
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Predicate;

final class MeteoritePlacerImpl {
    private MeteoritePlacerImpl() {}

    /* ====== 1) CARVE CRATER + RIM ====== */
    static void carveCrater(WorldGenLevel w, BlockPos center, int R, float ellipticity, float depthScale,
                            MeteoriteSpec s, RandomSource r, Predicate<BlockState> replaceable) {
        final int rx = Mth.ceil(R * (1.0f + ellipticity));   // stretch X
        final int rz = Mth.ceil(R * (1.0f - ellipticity));   // squeeze Z
        final int ry = Mth.ceil(R * depthScale);             // depth

        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Core bowl carve
        for (int dx = -rx - 1; dx <= rx + 1; dx++) {
            for (int dz = -rz - 1; dz <= rz + 1; dz++) {
                // dynamic crater floor height relative to centerY (paraboloid-ish)
                final float nx = (float) dx / (float) rx;
                final float nz = (float) dz / (float) rz;
                final float dist = Mth.sqrt(nx * nx + nz * nz);
                if (dist > 1.15f) continue;

                // bowl profile: deeper at center, 0 at rim, slightly undercut rim to pop it
                final float t = Mth.clamp(1.0f - dist, 0.0f, 1.0f);
                final int bowlDepth = Math.max(0, Mth.floor(ry * (t * t) + 0.5f)); // quadratic

                // carve from surface downward a bit more than bowl, to ensure openness
                int topY = w.getHeight(Heightmap.Types.WORLD_SURFACE_WG, center.getX() + dx, center.getZ() + dz);
                int floorY = center.getY() - bowlDepth;
                int carveTop = Math.max(floorY + 1, topY);
                for (int y = carveTop; y >= floorY; y--) {
                    pos.set(center.getX() + dx, y, center.getZ() + dz);
                    BlockState bs = w.getBlockState(pos);
                    // aggressively convert loose materials (leaves/flowers) to air too
                    if (!bs.isAir()) {
                        if (replaceable.test(bs) || !bs.getFluidState().isEmpty() || bs.getBlock() == Blocks.GRASS_BLOCK
                                || bs.is(BlockTags.SAND) || bs.is(BlockTags.DIRT) || bs.is(Blocks.GRAVEL) || bs.is(BlockTags.SNOW)) {
                            w.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
                }

                // small chance to puddle lava in deepest center (rare spice)
                if (bowlDepth >= ry - 1 && r.nextFloat() < 0.05f) {
                    pos.set(center.getX() + dx, floorY, center.getZ() + dz);
                    if (w.getBlockState(pos).isAir()) {
                        w.setBlock(pos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }

        // Ejecta rim: raise around dist ≈ 1.0, with noisy thickness/height
        for (int dx = -rx - 2; dx <= rx + 2; dx++) {
            for (int dz = -rz - 2; dz <= rz + 2; dz++) {
                final float nx = (float) dx / (float) rx;
                final float nz = (float) dz / (float) rz;
                final float dist = Mth.sqrt(nx * nx + nz * nz);
                if (dist < 0.9f || dist > 1.3f) continue;

                final int rimHeight = (r.nextFloat() < 0.75f ? 1 : 2);
                final int x = center.getX() + dx;
                final int z = center.getZ() + dz;
                int y = w.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                // stack 1–2 blocks of slag/stone
                for (int h = 0; h < rimHeight; h++) {
                    pos.set(x, y + h, z);
                    if (w.getBlockState(pos).isAir()) {
                        w.setBlock(pos, pickRimBlock(s, r).defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    /* ====== 2) CORE + HALO ====== */
    static void placeCore(WorldGenLevel w, BlockPos coreCenter, int R, MeteoriteSpec s, RandomSource r,
                          Predicate<BlockState> replaceable) {
        final int rCore = Math.max(1, Mth.floor(R * (float) s.params().geometry().coreRatio()));
        final float rx = rCore * (1.0f + 0.15f * (r.nextFloat() - 0.5f)); // slight noise
        final float ry = rCore * (1.0f + 0.15f * (r.nextFloat() - 0.5f));
        final float rz = rCore * (1.0f + 0.15f * (r.nextFloat() - 0.5f));

        final Block core = resolveBlock(s.blocks().coreBlock());
        final Block halo = s.blocks().coreHaloBlock().get().getPath().equals("air") ? null : resolveBlock(s.blocks().coreHaloBlock().get());

        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -Mth.ceil(rx) - 1; dx <= Mth.ceil(rx) + 1; dx++) {
            for (int dy = -Mth.ceil(ry) - 1; dy <= Mth.ceil(ry) + 1; dy++) {
                for (int dz = -Mth.ceil(rz) - 1; dz <= Mth.ceil(rz) + 1; dz++) {
                    float nx = dx / rx, ny = dy / ry, nz = dz / rz;
                    float d = nx * nx + ny * ny + nz * nz;
                    pos.set(coreCenter.getX() + dx, coreCenter.getY() + dy, coreCenter.getZ() + dz);
                    BlockState cur = w.getBlockState(pos);
                    if (d <= 1.0f) {
                        if (replaceable.test(cur) || cur.isAir()) {
                            w.setBlock(pos, core.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    } else if (halo != null && d <= 1.12f) {
                        if (replaceable.test(cur) || cur.isAir()) {
                            w.setBlock(pos, halo.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }
    }

    /* ====== 3) THERMAL ALTERATION (GLASS / SLAG) ====== */
    static void heatAlter(WorldGenLevel w, BlockPos center, int R, MeteoriteSpec s, RandomSource r) {
        final int rad = R + 2;
        final Block slag = resolveBlock(s.blocks().slagBlock());
        final Block glass = resolveBlock(s.blocks().glassBlock());

        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -rad; dx <= rad; dx++) {
            for (int dz = -rad; dz <= rad; dz++) {
                int x = center.getX() + dx;
                int z = center.getZ() + dz;
                int y = w.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                // walk a short column downward (catch just-below-surface)
                for (int dy = 0; dy >= -3; dy--) {
                    pos.set(x, y + dy, z);
                    BlockState bs = w.getBlockState(pos);
                    if (bs.isAir()) continue;

                    // sand → glass (tektite feel)
                    if (bs.is(BlockTags.SAND)) {
                        if (r.nextDouble() < s.params().thermal().glassChance()) {
                            w.setBlock(pos, glass.defaultBlockState(), Block.UPDATE_ALL);
                        }
                        break;
                    }

                    // stone-ish → slag
                    if (isStoneLike(bs)) {
                        if (r.nextDouble() < s.params().thermal().slagChance()) {
                            w.setBlock(pos, slag.defaultBlockState(), Block.UPDATE_ALL);
                        }
                        break;
                    }

                    // soft soils, skip downward
                    if (bs.is(BlockTags.DIRT) || bs.is(Blocks.GRAVEL) || bs.is(BlockTags.SNOW)) {
                        if (r.nextDouble() < (s.params().thermal().slagChance() * 0.35)) {
                            w.setBlock(pos, slag.defaultBlockState(), Block.UPDATE_ALL);
                        }
                        break;
                    }
                }
            }
        }
    }

    /* ====== 4) STREWN FRAGMENTS ====== */
    static void scatterFragments(WorldGenLevel w, BlockPos center, int R, MeteoriteSpec s, RandomSource r,
                                 Predicate<BlockState> replaceable) {
        final Block frag = resolveBlock(s.blocks().coreBlock());
        final int base = s.params().thermal().strewnField();
        final int total = Mth.floor(base * (R / 8.0f));

        // Pick an impact direction; bias fragments “downrange”
        final float theta = r.nextFloat() * Mth.TWO_PI;
        final float bias = 0.65f; // 0 = uniform, 1 = tightly downrange

        for (int i = 0; i < total; i++) {
            float angle = wrapAngle(theta + sampleBiasedAngle(r, bias));
            float dist = Mth.lerp(r.nextFloat(), R * 1.0f, R * 3.0f) * (0.7f + 0.6f * r.nextFloat());

            int dx = Mth.floor(Mth.cos(angle) * dist);
            int dz = Mth.floor(Mth.sin(angle) * dist);
            int x = center.getX() + dx;
            int z = center.getZ() + dz;

            int y = w.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            BlockPos place = new BlockPos(x, y, z);

            // If air at top, try one block down so it embeds; else place on top if replaceable
            if (w.isEmptyBlock(place)) {
                BlockPos below = place.below();
                BlockState under = w.getBlockState(below);
                if (replaceable.test(under) || under.is(BlockTags.DIRT) || isStoneLike(under) || under.is(BlockTags.SAND)) {
                    w.setBlock(below, frag.defaultBlockState(), Block.UPDATE_ALL);
                }
            } else {
                BlockState top = w.getBlockState(place);
                if (replaceable.test(top) || top.is(BlockTags.DIRT) || isStoneLike(top) || top.is(BlockTags.SAND)) {
                    w.setBlock(place, frag.defaultBlockState(), Block.UPDATE_ALL);
                } else {
                    // nudge sideways a bit if blocked
                    Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(r);
                    BlockPos alt = place.relative(dir);
                    if (w.getBlockState(alt).isAir()) {
                        w.setBlock(alt, frag.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    /* ====== helpers ====== */
    private static Block pickRimBlock(MeteoriteSpec s, RandomSource r) {
        // mostly slag with bits of stone/gravel for texture
        if (r.nextFloat() < 0.75f) return resolveBlock(s.blocks().slagBlock()); // null ok for id-only
        return r.nextFloat() < 0.5f ? Blocks.STONE : Blocks.GRAVEL;
    }

    private static boolean isStoneLike(BlockState bs) {
        return bs.is(BlockTags.BASE_STONE_OVERWORLD) || bs.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                || bs.is(Blocks.DEEPSLATE) || bs.is(Blocks.TUFF) || bs.is(Blocks.CALCITE);
    }

    private static Block resolveBlock(net.minecraft.resources.ResourceLocation id) {
        // safe even with null world if you only use vanilla blocks in pickRimBlock
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(id);
    }

    private static float sampleBiasedAngle(RandomSource r, float biasTowardZero) {
        // Von Mises-lite: mix uniform with a tight gaussian around 0
        if (r.nextFloat() < biasTowardZero) {
            return (float) (r.nextGaussian() * 0.35f);
        }
        return (r.nextFloat() - 0.5f) * Mth.TWO_PI;
    }
    private static float wrapAngle(float a) {
        while (a <= -Mth.PI) a += Mth.TWO_PI;
        while (a >   Mth.PI) a -= Mth.TWO_PI;
        return a;
    }
}
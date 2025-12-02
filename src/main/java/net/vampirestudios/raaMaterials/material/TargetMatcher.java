package net.vampirestudios.raaMaterials.material;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.vampirestudios.raaMaterials.material.SpawnSpec.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

final class TargetMatcher {
    private TargetMatcher() {}

    static Predicate<BlockState> from(List<Target> targets) {
        // Very lightweight matcher:
        final List<Block> ids = new ArrayList<>();
        final List<TagKey<Block>> tags = new ArrayList<>();
        for (Target t : targets) {
            // Assumes your Target has either id() or tag() accessors — adjust to your class.
            if (t.id() != null) {
                ids.add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(t.id()));
            }
            if (t.tag() != null) {
                tags.add(TagKey.create(net.minecraft.core.registries.Registries.BLOCK, t.tag()));
            }
        }
        return state -> {
            if (state.isAir()) return true; // permissive carve
            Block b = state.getBlock();
            if (ids.contains(b)) return true;
            for (TagKey<Block> tag : tags) if (state.is(tag)) return true;
            return false;
        };
    }
}

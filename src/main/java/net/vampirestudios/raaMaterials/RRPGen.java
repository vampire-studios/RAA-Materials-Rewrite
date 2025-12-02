// RRPGen.java
package net.vampirestudios.raaMaterials;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;

public final class RRPGen {
    public static final RuntimeResourcePack PACK = RuntimeResourcePack.create("raa_materials:runtime");

    public static void init() {
        RRPCallback.AFTER_VANILLA.register(a -> a.add(PACK));
    }
}
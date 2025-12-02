package net.vampirestudios.raaMaterials;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Array;

public class Utils {
    public static String elementsDirPath(ResourceKey<? extends Registry<?>> resourceKey) {
        return resourceKey.location().getPath();
    }

    public static ResourceLocation appendToPath(ResourceLocation identifier, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(identifier.getNamespace(), identifier.getPath() + suffix);
    }

    public static ResourceLocation prependToPath(ResourceLocation identifier, String prefix) {
        return ResourceLocation.fromNamespaceAndPath(identifier.getNamespace(), prefix + identifier.getPath());
    }

    public static ResourceLocation appendAndPrependToPath(ResourceLocation identifier, String prefix, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(identifier.getNamespace(), prefix + identifier.getPath() + suffix);
    }

    public static <T> T[] stripNulls(T[] arr) {
        int i = 0;
        for (T t : arr) {
            if (t != null) {
                i++;
            }
        }
        //noinspection unchecked
        T[] newArr = (T[]) Array.newInstance(arr.getClass().getComponentType(), i);
        i = 0;
        for (T t : arr) {
            if (t != null) {
                newArr[i] = t;
                i++;
            }
        }
        return newArr;
    }

}

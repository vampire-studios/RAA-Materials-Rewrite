package net.vampirestudios.raaMaterials;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Array;

public class Utils {
    public static String elementsDirPath(ResourceKey<? extends Registry<?>> resourceKey) {
        return resourceKey.identifier().getPath();
    }

    public static Identifier appendToPath(Identifier identifier, String suffix) {
        return Identifier.fromNamespaceAndPath(identifier.getNamespace(), identifier.getPath() + suffix);
    }

    public static Identifier prependToPath(Identifier identifier, String prefix) {
        return Identifier.fromNamespaceAndPath(identifier.getNamespace(), prefix + identifier.getPath());
    }

    public static Identifier appendAndPrependToPath(Identifier identifier, String prefix, String suffix) {
        return Identifier.fromNamespaceAndPath(identifier.getNamespace(), prefix + identifier.getPath() + suffix);
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

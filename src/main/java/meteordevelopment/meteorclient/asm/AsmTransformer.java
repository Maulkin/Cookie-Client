/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.asm;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class AsmTransformer {
    public final String targetName;

    protected AsmTransformer(String targetName) {
        this.targetName = targetName;
    }

    public abstract void transform(ClassNode klass);

    protected MethodNode getMethod(ClassNode klass, MethodInfo methodInfo) {
        for (MethodNode method : klass.methods) {
            if (methodInfo.equals(method)) return method;
        }

        return null;
    }

    protected static void error(String message) {
        System.err.println(message);
        throw new RuntimeException(message);
    }

    protected static String mapClassName(String name) {
        return FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", name.replace('/', '.'));
    }
}

package org.enjoy.asm;

import org.objectweb.asm.MethodVisitor;

import java.util.HashSet;
import java.util.Set;

public class HandlerFilter {

    private static Set<String> exceptPackagePrefix = new HashSet<>();
    private static Set<String> exceptMethods = new HashSet<>();

    static
    {
        exceptPackagePrefix.add("helloExceptP");
        exceptMethods.add("helloexceptM");

    }


    public static boolean isNotNeedInject(String className){
        if(className == null) return false;
        for (String prefix : exceptPackagePrefix){
            if (className.startsWith(prefix)){
                return true;
            }
        }
        return false;

    }
    public static boolean isNotNeedInjectMethod(String methodName){
        if(methodName == null) return false;
        //return exceptMethods.add(methodName);
        return exceptMethods.contains(methodName);
    }
}

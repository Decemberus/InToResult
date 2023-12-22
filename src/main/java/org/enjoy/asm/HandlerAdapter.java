package org.enjoy.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

//对类做一些基本的判断等
public class HandlerAdapter extends ClassVisitor {
    private final String className;
    private final String fullClazzName;
    private final String simpleClassName;
    private boolean isInterface;
    public HandlerAdapter(final ClassVisitor classVisitor, String className) {
        super(ASM5, classVisitor);
        this.className = className;
        this.fullClazzName = className.replace('/','.');
        this.simpleClassName = className.substring(className.lastIndexOf("/") + 1);
    }

    //对interface类型进行判断
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.isInterface = (access & ACC_INTERFACE) != 0;
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        //接口与私有方法,抽象方法,native方法,桥接方法,合成方法就直接返回父类
        if (isInterface
            || (access & ACC_PRIVATE) != 0
            || (access & ACC_ABSTRACT) != 0
            || (access & ACC_NATIVE) != 0
            || (access & ACC_BRIDGE) != 0
            || (access & ACC_SYNTHETIC) != 0)
        {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        //过滤构造方法
        if ("<init>".equals(name) || "<clinit>".equals(name)) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        //过滤我们自己定义的不需要过滤的方法
        if (HandlerFilter.isNotNeedInjectMethod(name)) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }

        MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if (methodVisitor == null){
            return null;
        }
        return new MyMethodVisitor(access, name, descriptor, methodVisitor, className, fullClazzName, simpleClassName);
        //return new ProfilingMethodVisitor(access, name, descriptor, methodVisitor, className, fullClazzName, simpleClassName);

    }
}

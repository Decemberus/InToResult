package org.enjoy.agent;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import org.enjoy.asm.HandlerFilter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AgentTransform implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            if(HandlerFilter.isNotNeedInject(className)){
                return classfileBuffer;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return classfileBuffer;
    }

    public byte[] getByteCode(String className , byte[] classfilebuffer){
        ClassReader classReader = new ClassReader(classfilebuffer);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new HandlerFilter();
        classReader.accept(classVisitor,ClassReader.EXPAND_FRAMES);
    }
}

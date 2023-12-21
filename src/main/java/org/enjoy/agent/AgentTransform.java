package org.enjoy.agent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.enjoy.asm.HandlerAdapter;
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
        ClassVisitor classVisitor = new HandlerAdapter(classWriter , className);
        classReader.accept(classVisitor,ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }
}

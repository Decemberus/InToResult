package org.enjoy.agent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.enjoy.asm.HandlerAdapter;
import org.enjoy.asm.HandlerFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
            byte[] byteCode = getByteCode(className, classfileBuffer);
            outputClazz(byteCode,className);

            return byteCode;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public byte[] getByteCode(String className , byte[] classfilebuffer){
        ClassReader classReader = new ClassReader(classfilebuffer);
        ClassWriter classWriter = new ClassWriter(classReader,ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new HandlerAdapter(classWriter , className);
        classReader.accept(classVisitor,ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }
    private static void outputClazz(byte[] bytes, String className) {
        // 输出类字节码
        FileOutputStream out = null;
        try {
            //String pathName = ProfilingTransformer.class.getResource("/").getPath() + className + "SQM.class";
            String pathName = "D:\\Code_Project\\Java\\ASM\\src\\main\\java\\org\\enjoy\\modifying\\NewClass.class";
            out = new FileOutputStream(new File(pathName));
            System.out.println("ASM类输出路径：" + pathName);
            out.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

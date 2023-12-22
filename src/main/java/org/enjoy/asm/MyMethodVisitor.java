package org.enjoy.asm;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyMethodVisitor extends AdviceAdapter {

    private final String className;
    private final boolean isStaticMethod;
    private List<String> parameterTypeList = new ArrayList<>();
    private int parameterTypeCount = 0;
    private int currentLocal = 0;
    private int startTimeIdentifier;
    private int parameterIdentifier;
    private int methodId = -1;


    public MyMethodVisitor(int access, String methodName, String desc, MethodVisitor mv, String className, String fullClassName, String simpleClassName) {
        super(ASM5, mv, access, methodName, desc);
        this.className = className;
        isStaticMethod = 0 != (access & ACC_STATIC);
        //提取参数类型
        Matcher matcher = Pattern.compile("(L.*?;|\\[{0,2}L.*?;|[ZCBSIFJD]|\\[{0,2}[ZCBSIFJD]{1})").matcher(desc.substring(0, desc.lastIndexOf(')') + 1));
        while (matcher.find()) {
            parameterTypeList.add(matcher.group(1));
        }
        parameterTypeCount = parameterTypeList.size();
        methodId = MethodMonitor.generateMethodId(new MethodTag(fullClassName, simpleClassName, methodName, desc, parameterTypeList, desc.substring(desc.lastIndexOf(')') + 1)));

    }

    /**
     *
     */
    @Override
    protected void onMethodEnter() {
        methodStartTime();
        methodParameter();
    }

    //创建long l = System.nanoTime();这一句语句
    public void methodStartTime(){

        //mv.visitMethodInsn(Opcodes.INVOKESTATIC,"java/lang/System","nanoTime","()J",false);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,Type.getInternalName(System.class),"nanoTime","()J",false);
        currentLocal = newLocal(Type.LONG_TYPE);
        startTimeIdentifier = currentLocal;
        mv.visitVarInsn(LSTORE,currentLocal);
    }

    public void methodParameter(){
        //通过字节码的方式创建数组，Object[] var6 = new Object[](x);
        //为了执行效率分为两种情况
        int parameterCount = parameterTypeList.size();
        if (parameterCount <= 0) return;
        // 1. 初始化数组
        if (parameterCount >= 4) {
            mv.visitVarInsn(BIPUSH, parameterCount); // valuebyte值带符号扩展成int值入栈。
        } else {
            switch (parameterCount) {
                case 1:
                    mv.visitInsn(ICONST_1); // 1(int)值入栈
                    break;
                case 2:
                    mv.visitInsn(ICONST_2);// 2(int)值入栈
                    break;
                case 3:
                    mv.visitInsn(ICONST_3);// 3(int)值入栈
                    break;
                default:
                    mv.visitInsn(ICONST_0);// 0(int)值入栈
            }
        }
        mv.visitTypeInsn(ANEWARRAY, Type.getDescriptor(Object.class));

        // 局部变量
        int localCount = isStaticMethod ? -1 : 0;
        // 2. 给数组赋值
        for (int i = 0; i < parameterCount; i++) {
            mv.visitInsn(DUP);
            if (i > 5) {
                mv.visitVarInsn(BIPUSH, i);
            } else {
                switch (i) {
                    case 0:
                        mv.visitInsn(ICONST_0);
                        break;
                    case 1:
                        mv.visitInsn(ICONST_1);
                        break;
                    case 2:
                        mv.visitInsn(ICONST_2);
                        break;
                    case 3:
                        mv.visitInsn(ICONST_3);
                        break;
                    case 4:
                        mv.visitInsn(ICONST_4);
                        break;
                    case 5:
                        mv.visitInsn(ICONST_5);
                        break;
                }
            }

            String type = parameterTypeList.get(i);
            if ("Z".equals(type)) {
                mv.visitVarInsn(ILOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Boolean.class), "valueOf", "(Z)Ljava/lang/Boolean;", false);
            } else if ("C".equals(type)) {
                mv.visitVarInsn(ILOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Character.class), "valueOf", "(C)Ljava/lang/Character;", false);
            } else if ("B".equals(type)) {
                mv.visitVarInsn(ILOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Byte.class), "valueOf", "(B)Ljava/lang/Byte;", false);
            } else if ("S".equals(type)) {
                mv.visitVarInsn(ILOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Short.class), "valueOf", "(S)Ljava/lang/Short;", false);
            } else if ("I".equals(type)) {
                mv.visitVarInsn(ILOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf", "(I)Ljava/lang/Integer;", false);
            } else if ("F".equals(type)) {
                mv.visitVarInsn(FLOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Float.class), "valueOf", "(F)Ljava/lang/Float;", false);
            } else if ("J".equals(type)) {
                mv.visitVarInsn(LLOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Long.class), "valueOf", "(J)Ljava/lang/Long;", false);
                localCount++;
            } else if ("D".equals(type)) {
                mv.visitVarInsn(DLOAD, ++localCount);  //获取对应的参数
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Double.class), "valueOf", "(D)Ljava/lang/Double;", false);
                localCount++;
            } else {
                mv.visitVarInsn(ALOAD, ++localCount);  //获取对应的参数
            }
            mv.visitInsn(AASTORE);
        }

        parameterIdentifier = newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(ASTORE, parameterIdentifier);


    }

    /**
     * @param opcode
     */
    @Override
    protected void onMethodExit(int opcode) {
        if ((IRETURN <= opcode && opcode <= RETURN) || opcode == ATHROW) {
            probeMethodReturn(opcode);
            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            System.out.println("startTimeIdentifier：" + startTimeIdentifier);
            mv.visitLdcInsn(methodId);
            // 判断入参
            if (parameterTypeList.isEmpty()) {
                mv.visitInsn(ACONST_NULL);
            } else {
                mv.visitVarInsn(ALOAD, parameterIdentifier);
                System.out.println("parameterIdentifier：" + parameterIdentifier);
            }
            // 判断出参
            if (RETURN == opcode) {
                mv.visitInsn(ACONST_NULL);
            } else if (IRETURN == opcode) {
                mv.visitVarInsn(ILOAD, currentLocal);
                mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Integer.class), "valueOf", "(I)Ljava/lang/Integer;", false);
            } else {
                mv.visitVarInsn(ALOAD, currentLocal);
            }
            System.out.println("currentLocal：" + currentLocal);
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(MethodMonitor.class), "point", "(JI[Ljava/lang/Object;Ljava/lang/Object;)V", false);
        }
    }

    /**
     * 方法出参
     */
    private void probeMethodReturn(int opcode) {
        currentLocal = this.nextLocal;
        switch (opcode) {
            case RETURN:
                break;
            case ARETURN:
                mv.visitVarInsn(ASTORE, currentLocal); // 将栈顶引用类型值保存到局部变量indexbyte中。
                mv.visitVarInsn(ALOAD, currentLocal);  // 从局部变量indexbyte中装载引用类型值入栈。
                break;
            case IRETURN:
                visitVarInsn(ISTORE, currentLocal);
                visitVarInsn(ILOAD, currentLocal);
                break;
            case LRETURN:
                visitVarInsn(LSTORE, currentLocal);
                visitVarInsn(LLOAD, currentLocal);
                break;
            case DRETURN:
                visitVarInsn(DSTORE, currentLocal);
                visitVarInsn(DLOAD, currentLocal);
                break;
        }
    }
}


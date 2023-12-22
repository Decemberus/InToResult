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
    private Label from = new Label();
    private Label to = new Label();
    private Label target = new Label();
    @Override
    protected void onMethodEnter() {
        //记录启动时间
        methodStartTime();
        //记录方法入参信息
        methodParameter();
        visitLabel(from);
        visitTryCatchBlock(from,to,target,Type.getInternalName(Exception.class));
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
    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
        int methodParameterIndex = isStaticMethod ? index : index - 1;  // 可以打印方法中所有入参的名称，这也可以用于后续自定义插针
        if (0 <= methodParameterIndex && methodParameterIndex < parameterTypeList.size()) {
            MethodMonitor.setMethodParameterGroup(methodId, name);
        }
    }
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        //标志：try块结束
        mv.visitLabel(to);
        //标志：catch块开始位置
        mv.visitLabel(target);

        // 设置visitFrame：mv.visitFrame(Opcodes.F_FULL, 4, new Object[]{"java/lang/String", Opcodes.INTEGER, Opcodes.LONG, "[Ljava/lang/Object;"}, 1, new Object[]{"java/lang/Exception"});
        int nLocal = (isStaticMethod ? 0 : 1) + parameterTypeCount + (parameterTypeCount == 0 ? 1 : 2);
        Object[] localObjs = new Object[nLocal];
        int objIdx = 0;
        if (!isStaticMethod) {
            localObjs[objIdx++] = className;
        }
        for (String parameter : parameterTypeList) {
            if ("Z".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.INTEGER;
            } else if ("C".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.INTEGER;
            } else if ("B".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.INTEGER;
            } else if ("S".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.INTEGER;
            } else if ("I".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.INTEGER;
            } else if ("F".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.FLOAD;
            } else if ("J".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.LONG;
            } else if ("D".equals(parameter)) {
                localObjs[objIdx++] = Opcodes.DOUBLE;
            } else {
                localObjs[objIdx++] = parameter;
            }
        }
        localObjs[objIdx++] = Opcodes.LONG;
        if (parameterTypeCount > 0) {
            localObjs[objIdx] = "[Ljava/lang/Object;";
        }
        mv.visitFrame(Opcodes.F_FULL, nLocal, localObjs, 1, new Object[]{"java/lang/Exception"});

        // 异常信息保存到局部变量
        int local = newLocal(Type.LONG_TYPE);
        System.out.println("xxxx:" + local);
        mv.visitVarInsn(ASTORE, local);

        // 输出参数
        mv.visitVarInsn(LLOAD, startTimeIdentifier);
        mv.visitLdcInsn(methodId);
        if (parameterTypeList.isEmpty()) {
            mv.visitInsn(ACONST_NULL);
        } else {
            mv.visitVarInsn(ALOAD, parameterIdentifier);
        }
        mv.visitVarInsn(ALOAD, local);
        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(MethodMonitor.class), "point", "(JI[Ljava/lang/Object;Ljava/lang/Throwable;)V", false);

        // 抛出异常
        mv.visitVarInsn(ALOAD, local);
        mv.visitInsn(ATHROW);

        super.visitMaxs(maxStack, maxLocals);
    }
}


package com.geewaza.wangheng.study.jvm.datagulu11;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ASMTest {
	public static void main(String[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);  
		cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, "Example", null, "java/lang/Object", null);  
		MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null,  null);  
		mw.visitVarInsn(Opcodes.ALOAD, 0);  //this 入栈
		mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");  
		mw.visitInsn(Opcodes.RETURN);  
		mw.visitMaxs(0, 0);  
		mw.visitEnd();  
		mw = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main",  "([Ljava/lang/String;)V", null, null);  
		mw.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",  "Ljava/io/PrintStream;");  
		mw.visitLdcInsn("Hello world!");  
		mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",  "(Ljava/lang/String;)V");  
		mw.visitInsn(Opcodes.RETURN);  
		mw.visitMaxs(0,0);  
		mw.visitEnd();  
		final byte[] code = cw.toByteArray();  
		Class<?> exampleClass = new ClassLoader() {
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                return defineClass(name, code, 0, code.length);
            }
        }.loadClass("Example");
        Method method = exampleClass.getMethod("main", new Class[] { String[].class });
        // 数组参数的方法，反射调用方式看起来比较古怪
        method.invoke(null, (Object) new String[0]);
		exampleClass.getMethods()[0].invoke(null, new Object[] { null }); 
	}

}

package com.geewaza.wangheng.study.jvm.datagulu11;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ASMMain {
	public static void main(String[] args) throws Exception{
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);  
		cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, "Example", null, "java/lang/Object", null);
		MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null,  null);  
		mw.visitVarInsn(Opcodes.ALOAD, 0);  //this 入栈
		mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");  
		mw.visitInsn(Opcodes.RETURN);  
		mw.visitMaxs(0, 0);  
		mw.visitEnd();  
		mw = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main",  "([Ljava/lang/String;)V", null, null);
		mw.visitIntInsn(Opcodes.BIPUSH, 6);
		mw.visitVarInsn(Opcodes.ISTORE, 0);
		mw.visitIntInsn(Opcodes.BIPUSH, 7);
		mw.visitVarInsn(Opcodes.ISTORE, 1);
		mw.visitVarInsn(Opcodes.ILOAD, 0);
		mw.visitVarInsn(Opcodes.ILOAD, 1);
		mw.visitInsn(Opcodes.IADD);
		mw.visitInsn(Opcodes.ICONST_3);
		mw.visitInsn(Opcodes.IMUL);
		mw.visitVarInsn(Opcodes.ISTORE, 2);
		mw.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",  "Ljava/io/PrintStream;");
		mw.visitVarInsn(Opcodes.ILOAD, 2);
		mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",  "(I)V");
		mw.visitInsn(Opcodes.RETURN);  
		mw.visitMaxs(0, 0);  
		mw.visitEnd();  
		final byte[] code = cw.toByteArray();  
		Class<?> exampleClass = new ClassLoader() {
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                return defineClass(name, code, 0, code.length);
            }
        }.loadClass("Example");
		exampleClass.getMethods()[0].invoke(null, new Object[] { null }); 
	}
}

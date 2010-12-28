package org.codehaus.groovy2.compiler;

import java.io.PrintWriter;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy2.compiler.ast.Gen;
import org.codehaus.groovy2.compiler.ast.TypeChecker;
import org.codehaus.groovy2.compiler.type.TypeScope;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

public class Compiler {
  public static void compile(ClassNode classNode, String sourceFile, ClassVisitor cv) {
    try {
      TypeScope typeScope = new TypeScope();

      TypeChecker typeChecker = new TypeChecker(typeScope);
      typeChecker.typecheck(classNode);

      Gen gen = new Gen(sourceFile, cv, typeScope, typeChecker.getTypeMap());
      gen.gen(classNode);
      
      //DEBUG
      if (cv instanceof ClassWriter) {
        ClassWriter classWriter = (ClassWriter)cv;
        byte[] byteArray = classWriter.toByteArray();
        CheckClassAdapter.verify(new ClassReader(byteArray), true, new PrintWriter(System.out));
      }
      
      return;
    } catch(RuntimeException e) {
      e.printStackTrace();
      throw e;
    } catch(Error e) {
      e.printStackTrace();
      throw e;
    } 
  }
}

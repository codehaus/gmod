/*
 * Copyright 2003-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.classgen;

import groovy.lang.GroovyRuntimeException;

import java.util.Iterator;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy2.compiler.Compiler;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Generates Java class versions of Groovy classes using ASM.
 *
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @author <a href="mailto:b55r@sina.com">Bing Ran</a>
 * @author <a href="mailto:blackdrag@gmx.org">Jochen Theodorou</a>
 * @author <a href='mailto:the[dot]mindstorm[at]gmail[dot]com'>Alex Popescu</a>
 * @author Alex Tkachman
 * @version $Revision: 21223 $
 */
public class AsmClassGenerator extends ClassGenerator {
    
    public static final boolean CREATE_DEBUG_INFO = true;
    public static final boolean CREATE_LINE_NUMBER_INFO = true;
    public static final boolean ASM_DEBUG = false; // add marker in the bytecode to show source-bytecode relationship
    
    private final SourceUnit source;
    private final ClassVisitor cv;
    private final String sourceFile;
    
    public AsmClassGenerator(SourceUnit source, GeneratorContext context, ClassVisitor classVisitor, ClassLoader classLoader, String sourceFile) {
        super(classLoader);
        this.source = source;
        this.cv = classVisitor;
        this.sourceFile = sourceFile;
    }

    public SourceUnit getSourceUnit() {
        return source;
    }
    
    public void visitClass(ClassNode classNode) {
      
      // --- XXX --- Patch --- XXX ---
      try {
        Compiler.compile(classNode, sourceFile, cv);
      }
      catch (GroovyRuntimeException e) {
        e.setModule(classNode.getModule());
        throw e;
      }
    }
    
    /**
     * creates a MOP method name from a method
     *
     * @param method  the method to be called by the mop method
     * @param useThis if true, then it is a call on "this", "super" else
     * @return the mop method name
     */
    public static String getMopMethodName(MethodNode method, boolean useThis) {
        ClassNode declaringNode = method.getDeclaringClass();
        int distance = 0;
        for (; declaringNode != null; declaringNode = declaringNode.getSuperClass()) {
            distance++;
        }
        return (useThis ? "this" : "super") + "$" + distance + "$" + method.getName();
    }

    /**
     * method to determine if a method is a MOP method. This is done by the
     * method name. If the name starts with "this$" or "super$" but does not
     * contain "$dist$", then it is an MOP method
     *
     * @param methodName name of the method to test
     * @return true if the method is a MOP method
     */
    public static boolean isMopMethod(String methodName) {
        return (methodName.startsWith("this$") ||
                methodName.startsWith("super$")) && !methodName.contains("$dist$");
    }
    
    
    public static boolean containsSpreadExpression(Expression arguments) {
        List args = null;
        if (arguments instanceof TupleExpression) {
            TupleExpression tupleExpression = (TupleExpression) arguments;
            args = tupleExpression.getExpressions();
        } else if (arguments instanceof ListExpression) {
            ListExpression le = (ListExpression) arguments;
            args = le.getExpressions();
        } else {
            return arguments instanceof SpreadExpression;
        }
        for (Iterator iter = args.iterator(); iter.hasNext();) {
            if (iter.next() instanceof SpreadExpression) return true;
        }
        return false;
    }

    public static int argumentSize(Expression arguments) {
        if (arguments instanceof TupleExpression) {
            TupleExpression tupleExpression = (TupleExpression) arguments;
            int size = tupleExpression.getExpressions().size();
            return size;
        }
        return 1;
    }
    
    

    /**
     * @param fldExp
     */
    public void loadStaticField(FieldExpression fldExp) {
        /*MethodVisitor mv = controller.getMethodVisitor();
        FieldNode field = fldExp.getField();
        boolean holder = field.isHolder() && !controller.isInClosureConstructor();
        ClassNode type = field.getType();

        String ownerName = (field.getOwner().equals(controller.getClassNode()))
                ? controller.getInternalClassName()
                : BytecodeHelper.getClassInternalName(field.getOwner());
        if (holder) {
            mv.visitFieldInsn(GETSTATIC, ownerName, fldExp.getFieldName(), BytecodeHelper.getTypeDescription(type));
            mv.visitMethodInsn(INVOKEVIRTUAL, "groovy/lang/Reference", "get", "()Ljava/lang/Object;");
            controller.getOperandStack().push(ClassHelper.OBJECT_TYPE);
        } else {
            mv.visitFieldInsn(GETSTATIC, ownerName, fldExp.getFieldName(), BytecodeHelper.getTypeDescription(type));
            controller.getOperandStack().push(field.getType());
        }*/
    }

    /**
     * RHS instance field. should move most of the code in the BytecodeHelper
     *
     * @param fldExp
     */
    public void loadInstanceField(FieldExpression fldExp) {
        /*MethodVisitor mv = controller.getMethodVisitor();
        FieldNode field = fldExp.getField();
        boolean holder = field.isHolder() && !controller.isInClosureConstructor();
        ClassNode type = field.getType();
        String ownerName = (field.getOwner().equals(controller.getClassNode()))
                ? controller.getInternalClassName()
                : BytecodeHelper.getClassInternalName(field.getOwner());

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, ownerName, fldExp.getFieldName(), BytecodeHelper.getTypeDescription(type));

        if (holder) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "groovy/lang/Reference", "get", "()Ljava/lang/Object;");
            controller.getOperandStack().push(ClassHelper.OBJECT_TYPE);
        } else {
            controller.getOperandStack().push(field.getType());
        }*/
    }

    

    
    
    public static boolean isThisExpression(Expression expression) {
        if (expression instanceof VariableExpression) {
            VariableExpression varExp = (VariableExpression) expression;
            return varExp.getName().equals("this");
        }
        return false;
    }

    
    public void onLineNumber(ASTNode statement, String message) {
        /*MethodVisitor mv = controller.getMethodVisitor();
        
        if (statement==null) return;
        int line = statement.getLineNumber();
        this.currentASTNode = statement;

        if (line < 0) return;
        if (!ASM_DEBUG && line==lineNumber) return;

        lineNumber = line;
        if (mv != null) {
            Label l = new Label();
            mv.visitLabel(l);
            mv.visitLineNumber(line, l);
        }*/
    }
    
    public boolean addInnerClass(ClassNode innerClass) {
        //innerClass.setModule(controller.getClassNode().getModule());
        //return innerClasses.add(innerClass);
        return true;
    }
}

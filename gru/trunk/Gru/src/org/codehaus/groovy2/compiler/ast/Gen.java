package org.codehaus.groovy2.compiler.ast;

import static org.codehaus.groovy2.compiler.type.PrimitiveType.ANY;
import static org.codehaus.groovy2.compiler.type.PrimitiveType.INT;
import static org.codehaus.groovy2.compiler.type.PrimitiveType.*;
import static org.codehaus.groovy2.lang.MOPLinker.MOPKind.*;
import static org.codehaus.groovy2.lang.MOPLinker.MOPKind.MOP_INVOKE;
import static org.codehaus.groovy2.lang.MOPLinker.MOPKind.MOP_NEW_INSTANCE;
import static org.codehaus.groovy2.lang.MOPLinker.MOPKind.MOP_OPERATOR;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.D2F;
import static org.objectweb.asm.Opcodes.D2I;
import static org.objectweb.asm.Opcodes.D2L;
import static org.objectweb.asm.Opcodes.DCONST_0;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2;
import static org.objectweb.asm.Opcodes.F2D;
import static org.objectweb.asm.Opcodes.F2I;
import static org.objectweb.asm.Opcodes.F2L;
import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.I2B;
import static org.objectweb.asm.Opcodes.I2C;
import static org.objectweb.asm.Opcodes.I2D;
import static org.objectweb.asm.Opcodes.I2F;
import static org.objectweb.asm.Opcodes.I2L;
import static org.objectweb.asm.Opcodes.I2S;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.L2D;
import static org.objectweb.asm.Opcodes.L2F;
import static org.objectweb.asm.Opcodes.L2I;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.POP2;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SIPUSH;
import static org.objectweb.asm.Opcodes.T_BOOLEAN;
import static org.objectweb.asm.Opcodes.T_BYTE;
import static org.objectweb.asm.Opcodes.T_CHAR;
import static org.objectweb.asm.Opcodes.T_DOUBLE;
import static org.objectweb.asm.Opcodes.T_FLOAT;
import static org.objectweb.asm.Opcodes.T_INT;
import static org.objectweb.asm.Opcodes.T_LONG;
import static org.objectweb.asm.Opcodes.T_SHORT;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.RegexExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.BytecodeHelper;
import org.codehaus.groovy.classgen.BytecodeInstruction;
import org.codehaus.groovy.classgen.BytecodeSequence;
import org.codehaus.groovy2.compiler.type.PrimaryType;
import org.codehaus.groovy2.compiler.type.PrimitiveType;
import org.codehaus.groovy2.compiler.type.RuntimeType;
import org.codehaus.groovy2.compiler.type.Type;
import org.codehaus.groovy2.compiler.type.TypeScope;
import org.codehaus.groovy2.compiler.type.TypeVisitor;
import org.codehaus.groovy2.compiler.type.Types;
import org.codehaus.groovy2.lang.MOPLinker;
import org.codehaus.groovy2.lang.MOPLinker.MOPKind;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MHandle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Gen extends ASTBridgeVisitor<Void, GenEnv> {
  private final String sourceFile;
  private final ClassVisitor cv;
  private final TypeScope typeScope;
  private final Map<ASTNode, Type> typeMap;
  private int lineNumber = -1;
  
  public Gen(String sourceFile, ClassVisitor cv, TypeScope typeScope, Map<ASTNode, Type> typeMap) {
    this.sourceFile = sourceFile;
    this.cv = cv;
    this.typeScope = typeScope;
    this.typeMap = typeMap;
  }

  public void gen(ClassNode rootNode) {
    gen(rootNode, (GenEnv)null);
  }
  
  private void gen(ASTNode node, GenEnv env) {
    // output line number
    int lineNumber = node.getLineNumber();
    if (lineNumber != this.lineNumber) {
      if (env.mv != null) {
        Label label = new Label();
        env.mv.visitLabel(label);
        env.mv.visitLineNumber(lineNumber, label);
      }
      this.lineNumber = lineNumber;
    }
    
    accept(node, env);
  }

  
  // --- helper methods
  
  private Type getType(ASTNode node) {
    return typeMap.get(node);
  }
  
  private static org.objectweb.asm.Type asASMType(Type type) {
    return type.accept(TO_ASM_VISITOR, null);
  } // where
  private static final TypeVisitor<org.objectweb.asm.Type, Void> TO_ASM_VISITOR =
      new TypeVisitor<org.objectweb.asm.Type, Void>() {
        @Override
        public org.objectweb.asm.Type visitRuntimeType(RuntimeType type, Void unused) {
          return org.objectweb.asm.Type.getType(type.getType());
        }
        @Override
        public org.objectweb.asm.Type visitPrimitiveType(PrimitiveType type, Void unused) {
          switch(type) {
          case ANY:
          case NULL:
            return ASM_OBJECT_TYPE;
          case BOOLEAN:
            return org.objectweb.asm.Type.BOOLEAN_TYPE;
          case BYTE:
            return org.objectweb.asm.Type.BYTE_TYPE;
          case SHORT:
            return org.objectweb.asm.Type.SHORT_TYPE;
          case CHAR:
            return org.objectweb.asm.Type.CHAR_TYPE;
          case INT:
            return org.objectweb.asm.Type.INT_TYPE;
          case LONG:
            return org.objectweb.asm.Type.LONG_TYPE;
          case FLOAT:
            return org.objectweb.asm.Type.FLOAT_TYPE;
          case DOUBLE:
            return org.objectweb.asm.Type.DOUBLE_TYPE;
          case VOID:
            return org.objectweb.asm.Type.VOID_TYPE;
          }
          throw new AssertionError("unknown primitive type");
        }
        @Override
        public org.objectweb.asm.Type visitPrimaryType(PrimaryType type, Void unused) {
          return org.objectweb.asm.Type.getObjectType(type.getPrimaryNode().getName().replace('.', '/'));
        }
      };
  
  static final org.objectweb.asm.Type ASM_OBJECT_TYPE =
      org.objectweb.asm.Type.getObjectType("java/lang/Object");    
  
  private static int toASMArrayType(org.objectweb.asm.Type type) {
    switch(type.getSort()) {
    case org.objectweb.asm.Type.BOOLEAN:
      return T_BOOLEAN;
    case org.objectweb.asm.Type.BYTE:
      return T_BYTE;
    case org.objectweb.asm.Type.SHORT:
      return T_SHORT;
    case org.objectweb.asm.Type.CHAR:
      return T_CHAR;
    case org.objectweb.asm.Type.INT:
      return T_INT;
    case org.objectweb.asm.Type.LONG:
      return T_LONG;
    case org.objectweb.asm.Type.FLOAT:
      return T_FLOAT;
    case org.objectweb.asm.Type.DOUBLE:
      return T_DOUBLE;
    default:
      throw new AssertionError("unknown type "+type);  
    }
  }
  
  private static void invokeDynamic(MOPKind kind, String name, String desc, MethodVisitor mv) {
    mv.visitIndyMethodInsn(MOPLinker.mangle(kind, name),
        desc, INVOKEDYNAMIC_BOOTSTRAP_METHOD, OBJECT_EMPTY_ARRAY);
  }
  
  private static final MHandle INVOKEDYNAMIC_BOOTSTRAP_METHOD =
      new MHandle(MHandle.REF_invokeStatic,
          MOPLinker.class.getName().replace('.', '/'),
          "bootstrap",
          "(Ljava/dyn/MethodHandles$Lookup;Ljava/lang/String;Ljava/dyn/MethodType;)Ljava/dyn/CallSite;");
  private static final Object[] OBJECT_EMPTY_ARRAY = new Object[0];
  
  private static void genConversion(Type leftType, Type rightType, MethodVisitor mv) {
    if (leftType == rightType) {
      return;
    }
    
    //System.out.println("convert "+leftType+" <- "+rightType);
    
    if (leftType == VOID) { // convert to void
      org.objectweb.asm.Type rightASMType = asASMType(rightType);
      mv.visitInsn((rightASMType.getSize() == 1)? POP: POP2);
      return;
    }
    if (rightType == VOID) { // convert from void
      genFromVoidConversion(leftType, mv);
    }
    
    if (leftType == ANY) {
      if (rightType != NULL && rightType instanceof PrimitiveType) {
        genBoxConversion((PrimitiveType)rightType, mv);
        return;
      }
      // no conversion needed
      return;
    }
    if (leftType instanceof PrimitiveType) {
      PrimitiveType leftPrimitiveType = (PrimitiveType)leftType;
      
      PrimitiveType rightPrimitiveType = PrimitiveType.wrapperAsPrimitive(rightType);
      if (rightPrimitiveType != null) {  // right type is a wrapper
        genUnboxConversion(rightPrimitiveType, mv);
        if (leftType != rightPrimitiveType) {
          genPrimToPrimConversion(leftPrimitiveType, rightPrimitiveType, mv);
        }
        return;
      }
      
      if (rightType != ANY && rightType instanceof PrimitiveType) {
        genPrimToPrimConversion(leftPrimitiveType, (PrimitiveType)rightType, mv);
        return;
      }
      genDynConversion(leftType, rightType, mv);
      return;
    }
    
    // left type is a runtime or a primary type so can be a wrapper
    PrimitiveType leftPrimitiveType = PrimitiveType.wrapperAsPrimitive(leftType);
    if (leftPrimitiveType != null) {
      if (rightType != ANY && rightType instanceof PrimitiveType) {
        if (leftPrimitiveType != rightType) {
          genPrimToPrimConversion(leftPrimitiveType, (PrimitiveType)rightType, mv);
        }
        genBoxConversion((PrimitiveType)rightType, mv);
        return;
      }
    }
    
    if (rightType == NULL) {
      return;
    }
    
    //TODO widening conversion ?
    
    genDynConversion(leftType, rightType, mv);
  }
  
  
  private static void genDynConversion(Type leftType, Type rightType, MethodVisitor mv) {
    invokeDynamic(MOP_CONVERTER, "",
        "("+asASMType(rightType).getDescriptor()+")"+asASMType(leftType).getDescriptor(),
        mv);
  }

  private static void genFromVoidConversion(Type type, MethodVisitor mv) {
    if (type != ANY && type instanceof PrimitiveType) {
      switch((PrimitiveType)type) {
      case BOOLEAN:
      case BYTE:
      case SHORT:
      case CHAR:
      case INT:
        mv.visitInsn(ICONST_0);
        break;
      case LONG:
        mv.visitInsn(LCONST_0);
        break;
      case FLOAT:
        mv.visitInsn(FCONST_0);
        break;
      case DOUBLE:
        mv.visitInsn(DCONST_0);
        break;
      case VOID:
        break;
      default:
        throw new AssertionError("unknown primitive type");  
      }
    } else {
      mv.visitInsn(ACONST_NULL);
    }
  }

  private static void genBoxConversion(PrimitiveType type, MethodVisitor mv) {
    mv.visitMethodInsn(INVOKESTATIC, type.getWrapperName(), "valueOf",
        "("+asASMType(type).getDescriptor()+")L"+type.getWrapperName()+';');
  }
  
  private static void genUnboxConversion(PrimitiveType type, MethodVisitor mv) {
    mv.visitMethodInsn(INVOKEVIRTUAL, type.getWrapperName(), type.getUnwrappedMethodName(),
        "()"+asASMType(type).getDescriptor());
  }
  
  private static void genPrimToPrimConversion(PrimitiveType leftType, PrimitiveType rightType, MethodVisitor mv) {
    int opcode = PRIM_CONVERSION_OPCODES[rightType.ordinal() - 1][leftType.ordinal() - 1];
    if (opcode == NONE) {
      return;
    }
    mv.visitInsn(opcode);
  }
  
  private static final int NONE = 0;
  private static final int[][] PRIM_CONVERSION_OPCODES =
    //           BOOLEAN BYTE  SHORT CHAR  INT   LONG  FLOAT DOUBLE  // same order as in PrimitiveType  
    { 
    /*BOOLEAN*/  {NONE,  I2B,  I2S,  I2C,  NONE, I2L,  I2F,  I2D },
    /*BYTE*/     {NONE,  NONE, I2S,  I2C,  NONE, I2L,  I2F,  I2D },
    /*SHORT*/    {NONE,  NONE, NONE, I2C,  NONE, I2L,  I2F,  I2D },
    /*CHAR*/     {NONE,  NONE, NONE, NONE, NONE, I2L,  I2F,  I2D },
    /*INT*/      {NONE,  NONE, NONE, NONE, NONE, I2L,  I2F,  I2D },
    /*LONG*/     {L2I,   L2I,  L2I,  L2I,  L2I,  NONE, L2F,  L2D },
    /*FLOAT*/    {F2I,   F2I,  F2I,  F2I,  F2I,  F2L,  NONE, F2D },
    /*DOUBLE*/   {D2I,   D2I,  D2I,  D2I,  D2I,  D2L,  D2F,  NONE}
    };
      
  private void genDefaultConstructor(String internalSuperClassName, ClassVisitor cv) {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, internalSuperClassName, "<init>", "()V");
    mv.visitInsn(RETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }
  
  private void genStaticInit(ClassVisitor cv) {
    MethodVisitor mv = cv.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
    mv.visitCode();
    mv.visitLdcInsn(org.objectweb.asm.Type.getType(MOPLinker.class));
    mv.visitLdcInsn("bootstrap");
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/dyn/Linkage", "registerBootstrapMethod", "(Ljava/lang/Class;Ljava/lang/String;)V");
    mv.visitInsn(Opcodes.RETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }
  
  
  private static void genDefaultReturn(Type returnType, MethodVisitor mv) {
    genFromVoidConversion(returnType, mv);
    mv.visitInsn(asASMType(returnType).getOpcode(IRETURN));
  }
  
  private static void genIntegerConst(Integer boxedValue, MethodVisitor mv) {
    int value = boxedValue;
    if (value >= -1 && value <= 5) {
      mv.visitInsn(ICONST_0 + value);
      return;
    }
    if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
      mv.visitIntInsn(BIPUSH, value);
      return;
    }
    if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
      mv.visitIntInsn(SIPUSH, value);
      return;
    }
    mv.visitLdcInsn(boxedValue);
  }
  
  private void genBigIntegerConst(BigInteger value, MethodVisitor mv) {
    if (value.compareTo(BIG_INTEGER_MIN)>=0 && value.compareTo(BIG_INTEGER_MAX)<=0) {
      int intValue = value.intValue();
      switch(intValue) {
      case 0:
        mv.visitFieldInsn(GETSTATIC, "java/math/BigInteger", "ZERO", "Ljava/math/BigInteger;");
        return;
      case 1:
        mv.visitFieldInsn(GETSTATIC, "java/math/BigInteger", "ONE", "Ljava/math/BigInteger;");
        return;
      case 10:
        mv.visitFieldInsn(GETSTATIC, "java/math/BigInteger", "TEN", "Ljava/math/BigInteger;");
        return;
      }
      genIntegerConst(intValue, mv);
      mv.visitMethodInsn(INVOKESTATIC, "java/math/BigInteger", "valueOf", "(I)Ljava/math/BigInteger;");
      return;
    }
    mv.visitTypeInsn(NEW, "java/math/BigInteger");
    mv.visitInsn(DUP);
    mv.visitLdcInsn(value.toString());
    mv.visitMethodInsn(INVOKESPECIAL, "java/math/BigInteger", "<init>", "(Ljava/lang/String;)V");
  }
  
  private void genBigDecimalConst(BigDecimal value, MethodVisitor mv) {
    try {  // can be encoded using an int ?
      int intValue = value.intValueExact();
      switch(intValue) {
      case 0:
        mv.visitFieldInsn(GETSTATIC, "java/math/BigDecimal", "ZERO", "Ljava/math/BigDecimal;");
        return;
      case 1:
        mv.visitFieldInsn(GETSTATIC, "java/math/BigDecimal", "ONE", "Ljava/math/BigDecimal;");
        return;
      case 10:
        mv.visitFieldInsn(GETSTATIC, "java/math/BigDecimal", "TEN", "Ljava/math/BigDecimal;");
        return;
      }
      genIntegerConst(intValue, mv);
      mv.visitMethodInsn(INVOKESTATIC, "java/math/BigDecimal", "valueOf", "(I)Ljava/math/BigDecimal;");
      return;
    } catch(ArithmeticException e) {
      // not an int
    }
    
    // encode it using a string
    mv.visitTypeInsn(NEW, "java/math/BigDecimal");
    mv.visitInsn(DUP);
    mv.visitLdcInsn(value.toString());
    mv.visitMethodInsn(INVOKESPECIAL, "java/math/BigDecimal", "<init>", "(Ljava/lang/String;)V");
  }
  
  private static final BigInteger BIG_INTEGER_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
  private static final BigInteger BIG_INTEGER_MIN = BigInteger.valueOf(Integer.MIN_VALUE);

  
  private static void genClassConst(Class<?> value, MethodVisitor mv) {
    if (value.isPrimitive()) {
      PrimitiveType type = PrimitiveType.valueOf(value.getName().toUpperCase());
      mv.visitFieldInsn(GETSTATIC, type.getWrapperName(), "TYPE", "Ljava/lang/Class;");
      return;
    }
    mv.visitLdcInsn(org.objectweb.asm.Type.getType(value));
  }
  
  // --- visit
  
  @Override
  public Void visitClass(ClassNode node, GenEnv env) {
    String internalClassName = BytecodeHelper.getClassInternalName(node);
    String internalBaseClassName = BytecodeHelper.getClassInternalName(node.getSuperClass());

    System.out.println("generate "+internalClassName+" extends "+internalBaseClassName);
    
    cv.visit(
        Opcodes.V1_7,
        Opcodes.ACC_PUBLIC,
        internalClassName,
        BytecodeHelper.getGenericsSignature(node),
        internalBaseClassName,
        BytecodeHelper.getClassInternalNames(node.getInterfaces())
    );
    cv.visitSource(sourceFile, null);
    
    genDefaultConstructor(internalBaseClassName, cv);
    
    env = new GenEnv(null, node, null);
    for (PropertyNode pn : node.getProperties()) {
      visitProperty(pn, env);
    }
    for (FieldNode fn : node.getFields()) {
      visitField(fn, env);
    }
    for (ConstructorNode cn : node.getDeclaredConstructors()) {
      visitConstructor(cn, env);
    }
    for (MethodNode mn : node.getMethods()) {
      visitMethod(mn, env);
    }
    
    //genStaticInit(cv);
    
    cv.visitEnd();
    
    return null;
  }

  @Override
  public Void visitConstructor(ConstructorNode node, GenEnv param) {
    //FIXME
    return null;
  }

  @Override
  public Void visitMethod(MethodNode node, GenEnv env) {
    if (node.isSynthetic()) {  // don't generate synthetic method
      return null;
    }
    
    Parameter[] parameters = node.getParameters();
    int modifiers = node.getModifiers();
    String desc = BytecodeHelper.getMethodDescriptor(node.getReturnType(), parameters);
    String signature = BytecodeHelper.getGenericsMethodSignature(node);
    ClassNode[] exceptions = node.getExceptions();
    String[] exceptionsNames = (exceptions == null)? null: BytecodeHelper.getClassInternalNames(exceptions);
    MethodVisitor mv = cv.visitMethod(modifiers, node.getName(), desc, signature, exceptionsNames);

    if (!node.isAbstract()) {
      Statement code = node.getCode();
      
      mv.visitCode();
      int startSlot = (node.isStatic()? 0: 1);  // this -> slot 0
      gen(code, new GenEnv(mv, env.getClassNode(), new StackAllocator(startSlot)));
   
      Liveness liveness = Liveness.asLiveness(getType(code));
      if (liveness != Liveness.DEAD) {
        genDefaultReturn(getType(node), mv);
      }

      mv.visitMaxs(-1, -1);
    }
    mv.visitEnd();

    return null;
  }

  @Override
  public Void visitField(FieldNode node, GenEnv env) {
    ClassNode t = node.getType();
    FieldVisitor fv = cv.visitField(
        node.getModifiers(),
        node.getName(),
        BytecodeHelper.getTypeDescription(t),
        BytecodeHelper.getGenericsBounds(t),
        null);
    fv.visitEnd();
    return null;
  }

  @Override
  public Void visitProperty(PropertyNode node, GenEnv env) {
    // do nothing, already transformed to a field + getter/setter
    return null;
  }
  
  
  // --- statement
  
  @Override
  public Void visitBlockStatement(BlockStatement blockStatement, GenEnv env) {
    env = env.newAllocator();
    for(Statement statement: blockStatement.getStatements()) {
      gen(statement, env);
    }
    return null;
  }
  
  @Override
  public Void visitExpressionStatement(ExpressionStatement statement, GenEnv env) {
    Expression expression = statement.getExpression();
    gen(expression, env);
    Type type = getType(expression);
    if (type == VOID)
      return null;
    env.mv.visitInsn((asASMType(type).getSize() == 1)? POP: POP2);
    return null;
  }
  
  @Override
  public Void visitReturnStatement(ReturnStatement statement, GenEnv env) {
    Expression expression = statement.getExpression();
    gen(expression, env);
    
    Type returnType = getType(statement);
    Type expressionType = getType(expression);
    genConversion(returnType, expressionType, env.mv);
    
    env.mv.visitInsn(asASMType(returnType).getOpcode(IRETURN));
    return null;
  }
  
  @Override
  public Void visitBytecodeSequence(BytecodeSequence statement, GenEnv env) {
    System.out.println("bytecode sequence ...");
    
    for (Object part: statement.getInstructions()) {
      if (part == EmptyExpression.INSTANCE) {
        env.mv.visitInsn(ACONST_NULL);
      } else if (part instanceof Expression) {
        accept((Expression)part, env);
      } else if (part instanceof Statement) {
        accept((Statement)part, env);
      } else {
        BytecodeInstruction runner = (BytecodeInstruction) part;
        runner.visit(env.mv);
      }
    }
    return null;
  }
  
  
  // --- expressions
  
  @Override
  public Void visitConstantExpression(ConstantExpression expression, GenEnv env) {
    Type type = getType(expression);
    if (type == VOID) {  // don't generate a constant that will be discarded 
      return null;
    }
    
    Object value = expression.getValue();
    if (value == null) {
      genFromVoidConversion(type, env.mv);
      return null;
    }
    Class<?> constantClass = value.getClass();
    if (constantClass == Boolean.class) {
      env.mv.visitInsn((expression == ConstantExpression.TRUE)? ICONST_1: ICONST_0);
      return null;
    }
    if (constantClass == Integer.class) {
      genIntegerConst((Integer)value, env.mv);
      return null;
    }
    if (constantClass == BigInteger.class) {
      genBigIntegerConst((BigInteger)value, env.mv);
      return null;
    }
    if (constantClass == BigDecimal.class) {
      genBigDecimalConst((BigDecimal)value, env.mv);
      return null;
    }
    
    env.mv.visitLdcInsn(value);
    return null;
  }
  
  @Override
  public Void visitArrayExpression(ArrayExpression expression, GenEnv env) {
    for(Expression expr: expression.getSizeExpression()) {
      gen(expr, env);
      genConversion(INT, getType(expr), env.mv);
    }
    
    Type type = getType(expression); 
    Class<?> componentType;
    if (type instanceof RuntimeType &&
        (componentType = ((RuntimeType)type).getType().getComponentType()).isPrimitive()) {
      env.mv.visitIntInsn(NEWARRAY, toASMArrayType(org.objectweb.asm.Type.getType(componentType)));
    } else {
      env.mv.visitTypeInsn(ANEWARRAY, asASMType(Types.getComponent(type, typeScope)).getInternalName());
    }
    return null;
  }

  @Override
  public Void visitClassExpression(ClassExpression expression, GenEnv env) {
    ClassNode classNode = expression.getType();
    try {
      PrimitiveType type = PrimitiveType.valueOf(classNode.getName().toUpperCase());
      if (type != ANY) {
        env.mv.visitFieldInsn(GETSTATIC, type.getWrapperName(), "TYPE", "Ljava/lang/Class;");
        return null;
      }
    } catch(IllegalArgumentException e) {
      // do nothing
    }
    
    env.mv.visitLdcInsn(org.objectweb.asm.Type.getObjectType(BytecodeHelper.getClassInternalName(classNode)));
    return null;
  }

  @Override
  public Void visitDeclarationExpression(DeclarationExpression expression, GenEnv env) {
    visitAssignment(expression, env);
    return null;
  }
  
  private void visitAssignment(BinaryExpression expression, GenEnv env) {
    Expression leftExpression = expression.getLeftExpression();
    if (leftExpression instanceof PropertyExpression) {
      visitPropertyAssignment(expression, env);
      return;
    }
    if (leftExpression instanceof BinaryExpression) {
      visitArrayAssignment(expression, env);
      return;
    }
    
    if (!(leftExpression instanceof VariableExpression)) {
      throw new AssertionError("NYI "+leftExpression);
    }
    
    Expression rightExpression = expression.getRightExpression();
    gen(rightExpression, env);
    
    Type leftType = getType(leftExpression);
    Type rightType = getType(rightExpression);
    
    Type returnType = getType(expression);
    if (returnType != VOID) { // in an expression
      env.mv.visitInsn((asASMType(rightType).getSize() == 1)? DUP: DUP2);
    }
    
    Variable variable = ((VariableExpression)leftExpression).getAccessedVariable();
    int slot = env.getAllocator().getSlot(variable);
    genConversion(leftType, rightType, env.mv);
    
    env.mv.visitVarInsn(asASMType(leftType).getOpcode(ISTORE), slot);
    if (returnType != VOID) {
      genConversion(returnType, rightType, env.mv);
    }
  }
  
  private void visitPropertyAssignment(BinaryExpression expression, GenEnv env) {
    //FIXME b = foo.bar = 3 won't compile
    
    PropertyExpression propertyExpression = (PropertyExpression)expression.getLeftExpression();
    
    Expression receiverExpression = propertyExpression.getObjectExpression();
    gen(receiverExpression, env);
    Expression valueExpression = expression.getRightExpression();
    gen(valueExpression, env);
    
    String name = propertyExpression.getPropertyAsString();
    invokeDynamic(MOP_SET_PROPERTY, name,
        '(' + asASMType(getType(receiverExpression)).getDescriptor() +
        asASMType(getType(valueExpression)).getDescriptor() +
        ')' + asASMType(getType(expression)).getDescriptor(),
        env.mv);
    return;
  }
  
  private void visitArrayAssignment(BinaryExpression expression, GenEnv env) {
    //FIXME b = a[1] = 3 won't compile
    
    BinaryExpression arrayAccessExpression = (BinaryExpression)expression.getLeftExpression();
    
    Expression receiverExpression = arrayAccessExpression.getLeftExpression();
    gen(receiverExpression, env);
    Expression indexExpression = arrayAccessExpression.getRightExpression();
    gen(indexExpression, env);
    Expression valueExpression = expression.getRightExpression();
    gen(valueExpression, env);
    
    String desc = '(' +   
       asASMType(getType(receiverExpression)).getDescriptor() +
       asASMType(getType(indexExpression)).getDescriptor() +
       asASMType(getType(valueExpression)).getDescriptor() +
       ')' + asASMType(getType(expression)).getDescriptor();
    
    invokeDynamic(MOP_INVOKE, "putAt", desc.toString(), env.mv);
    return;
  }

  @Override
  public Void visitVariableExpression(VariableExpression expression, GenEnv env) {
    if (expression.isThisExpression()) {
      env.mv.visitVarInsn(ALOAD, 0);
      return null;
    }
    if (expression.isSuperExpression()) {
      throw new AssertionError("NYI");
    }
    
    Variable variable = expression.getAccessedVariable();
    int slot = env.getAllocator().getSlot(variable);
    Type type = getType(expression);
    env.mv.visitVarInsn(asASMType(type).getOpcode(ILOAD), slot);
    return null;
  }
  
  @Override
  public Void visitAttributeExpression(AttributeExpression expression, GenEnv env) {
    Expression receiverExpression = expression.getObjectExpression();
    gen(receiverExpression, env);
    Type receiverType = getType(receiverExpression);
    
    String name = expression.getPropertyAsString();
    if (name == null) {  //FIXME
      name = "FIXME";
    }
    
    invokeDynamic(MOP_GET_PROPERTY, name,
        org.objectweb.asm.Type.getMethodDescriptor(ASM_OBJECT_TYPE,
            new org.objectweb.asm.Type[]{asASMType(receiverType)}),
        env.mv);
    return null;
  }
  
  @Override
  public Void visitPropertyExpression(PropertyExpression expression, GenEnv env) {
    Expression receiverExpression = expression.getObjectExpression();
    gen(receiverExpression, env);
    
    String name = expression.getPropertyAsString();
    Type receiverType = getType(receiverExpression);
    Type type = getType(expression);
    invokeDynamic(MOP_GET_PROPERTY, name,
        '('+asASMType(receiverType).getDescriptor()+')'+asASMType(type),
        env.mv);
    return null;
  }
  
  @Override
  public Void visitFieldExpression(FieldExpression expression, GenEnv env) {
    env.mv.visitVarInsn(ALOAD, 0);
    env.mv.visitFieldInsn(GETFIELD,
        BytecodeHelper.getClassInternalName(env.getClassNode()),
        expression.getFieldName(),
        asASMType(getType(expression)).getDescriptor());
    return null;
  }
  
  @Override
  public Void visitSpreadExpression(SpreadExpression expression, GenEnv env) {
    gen(expression.getExpression(), env);  //FIXME
    return null;
  }
  
  @Override
  public Void visitBinaryExpression(BinaryExpression expression, GenEnv env) {
    //gen(expression.getLeftExpression(), env);
    //gen(expression.getRightExpression(), env);
    
    switch (expression.getOperation().getType()) {
      case org.codehaus.groovy.syntax.Types.EQUAL: // = assignment
        visitAssignment(expression, env);
        return null;

      case org.codehaus.groovy.syntax.Types.PLUS:      // +
        return visitBinaryOp(expression, "add", env);
      case org.codehaus.groovy.syntax.Types.MINUS:     // -
        return visitBinaryOp(expression, "subtract", env);
      case org.codehaus.groovy.syntax.Types.MULTIPLY:  // *
        return visitBinaryOp(expression, "multiply", env);
      case org.codehaus.groovy.syntax.Types.DIVIDE:    // /
        return visitBinaryOp(expression, "divide", env);

      case org.codehaus.groovy.syntax.Types.LEFT_SQUARE_BRACKET:  // array access
          visitArrayAccess(expression, env);
          return null;
        
      default:
        throw new AssertionError("binary "+expression.getOperation().getType()+": NIY");
    }
  }
  

  private Void visitBinaryOp(BinaryExpression expression, String name, GenEnv env) {
    Expression leftExpression = expression.getLeftExpression();
    gen(leftExpression, env);
    Expression rightExpression = expression.getRightExpression();
    gen(rightExpression, env);
    
    String desc = '(' +  
      asASMType(getType(leftExpression)).getDescriptor() +
      asASMType(getType(rightExpression)).getDescriptor() +
      ')' +
      asASMType(getType(expression)).getDescriptor();
    
    invokeDynamic(MOP_OPERATOR, name, desc, env.mv);
    return null;
  }
  
  private void visitArrayAccess(BinaryExpression expression, GenEnv env) {
    Expression receiverExpression = expression.getLeftExpression();
    gen(receiverExpression, env);
    Expression indexExpression = expression.getRightExpression();
    gen(indexExpression, env);
    
    String desc = '(' +   
       asASMType(getType(receiverExpression)).getDescriptor() +
       asASMType(getType(indexExpression)).getDescriptor() +
       ')' + asASMType(getType(expression)).getDescriptor();
    
    invokeDynamic(MOP_INVOKE, "getAt", desc.toString(), env.mv);
    return;
  }

  @Override
  public Void visitMethodCallExpression(MethodCallExpression expression, GenEnv env) {
    TupleExpression tuple = (TupleExpression)expression.getArguments();
    
    String name = expression.getMethodAsString();
    
    Expression receiverExpression = expression.getObjectExpression();
    gen(receiverExpression, env);
     
    StringBuilder desc = new StringBuilder().append('(');  
    desc.append(asASMType(getType(receiverExpression)).getDescriptor());
    for(Expression expr: tuple.getExpressions()) {
      gen(expr, env);
      desc.append(asASMType(getType(expr)).getDescriptor());
    }
    Type returnType = getType(expression);
    desc.append(")").append(asASMType(returnType).getDescriptor());
    
    invokeDynamic(MOP_INVOKE, name, desc.toString(), env.mv);
    return null;
  }
  
  @Override
  public Void visitConstructorCallExpression(ConstructorCallExpression expression, GenEnv env) {
    TupleExpression tuple = (TupleExpression)expression.getArguments();
    
    org.objectweb.asm.Type asmReceiverType = org.objectweb.asm.Type.getObjectType(
        BytecodeHelper.getClassInternalName(expression.getType()));
    env.mv.visitLdcInsn(asmReceiverType);
    
    StringBuilder desc = new StringBuilder().append("(Ljava/lang/Class;");
    
    List<Expression> expressions = tuple.getExpressions();
    int size = expressions.size();
    if (expression.isUsingAnonymousInnerClass()) {
      // FIXME, see translation of annonymous inner class
      // for now, there is a supplementary parameter, but not in the translation of the class
      size --;
    }
    for(int i=0; i<size; i++) {
      Expression expr = expressions.get(i);
      gen(expr, env);
      desc.append(asASMType(getType(expr)).getDescriptor());
    }
    Type returnType = getType(expression);
    desc.append(")").append(asASMType(returnType).getDescriptor());
    
    invokeDynamic(MOP_NEW_INSTANCE, "", desc.toString(), env.mv);
    return null;
  }

  @Override
  public Void visitBytecodeExpression(BytecodeExpression expression, GenEnv env) {
    System.out.println("bytecode expression "+expression);
    
    expression.visit(env.mv);
    return null;
  }
  
  
  
  // --- TODO
  
  @Override
  public Void visitForLoop(ForStatement forLoop, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitWhileLoop(WhileStatement loop, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitDoWhileLoop(DoWhileStatement loop, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitIfElse(IfStatement ifElse, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitAssertStatement(AssertStatement statement, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitTryCatchFinally(TryCatchStatement statement, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitSwitch(SwitchStatement statement, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitCaseStatement(CaseStatement statement, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitBreakStatement(BreakStatement statement, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitContinueStatement(ContinueStatement statement, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitThrowStatement(ThrowStatement statement, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitSynchronizedStatement(SynchronizedStatement statement,
      GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitCatchStatement(CatchStatement statement, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitStaticMethodCallExpression(
      StaticMethodCallExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitTernaryExpression(TernaryExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitShortTernaryExpression(ElvisOperatorExpression expression,
      GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitPrefixExpression(PrefixExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitPostfixExpression(PostfixExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitBooleanExpression(BooleanExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitClosureExpression(ClosureExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitTupleExpression(TupleExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitMapExpression(MapExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitMapEntryExpression(MapEntryExpression expression,
      GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitListExpression(ListExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitRangeExpression(RangeExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitMethodPointerExpression(MethodPointerExpression expression,
      GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitRegexExpression(RegexExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitGStringExpression(GStringExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitSpreadMapExpression(SpreadMapExpression expression,
      GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitNotExpression(NotExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitUnaryMinusExpression(UnaryMinusExpression expression,
      GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitUnaryPlusExpression(UnaryPlusExpression expression,
      GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitBitwiseNegationExpression(
      BitwiseNegationExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitCastExpression(CastExpression expression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitArgumentlistExpression(ArgumentListExpression expression,
      GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitClosureListExpression(
      ClosureListExpression closureListExpression, GenEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  

  
  
  // --- TODO

  
  
}
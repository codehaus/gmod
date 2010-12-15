package org.codehaus.groovy2.compiler.ast;

import groovy2.lang.MetaClass;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
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
import org.codehaus.groovy.classgen.BytecodeInstruction;
import org.codehaus.groovy.classgen.BytecodeSequence;
import org.codehaus.groovy2.compiler.type.PrimitiveType;
import org.codehaus.groovy2.compiler.type.Type;
import org.codehaus.groovy2.compiler.type.TypeScope;
import org.codehaus.groovy2.compiler.type.TypeVisitor;
import org.codehaus.groovy2.lang.RT;

import static org.codehaus.groovy2.compiler.ast.Liveness.*;
import static org.codehaus.groovy2.compiler.type.PrimitiveType.*;

public class TypeChecker extends ASTBridgeVisitor<Type, TypeCheckEnv> {
  private final HashMap<ASTNode, Type> typeMap =
      new HashMap<ASTNode, Type>();
  
  public Map<ASTNode, Type> getTypeMap() {
    return typeMap;
  }
  
  public void typecheck(ClassNode rootNode, TypeScope typeScope) {
    TypeCheckEnv env = new TypeCheckEnv(null, typeScope, null);
    typecheck(rootNode, env);
  }
  
  private Type typecheck(ASTNode node, TypeCheckEnv env) {
    Type type = accept(node, env);
    storeInTypeMap(node, type);
    return type;
  }
  
  private void storeInTypeMap(ASTNode node, Type type) {
    if (type == null) {
      throw new AssertionError("Invalid null type "+node);
    }
    typeMap.put(node, type);
  }
  
  private static final Type NULL_TYPE = new Type() {
    @Override
    public String getName() {
      return "<null type>";
    }
    @Override
    public <R, P> R accept(TypeVisitor<? extends R,? super P> visitor, P param) {
      throw new AssertionError(); 
    }
  };

  
  // --- helper methods
  
  private final static HashMap<String, MetaClass> primitiveMap;
  static {
    HashMap<String, MetaClass> map = new HashMap<String, MetaClass>();
    for(Class<?> clazz: new Class[] {
      boolean.class, byte.class, short.class, char.class,
      int.class, float.class, long.class, double.class
    }) {
      map.put(clazz.getName(), RT.getMetaClass(clazz));
    }
    primitiveMap = map;
  }
  
  /*
  private static Type fromClassNode(TypeScope typeScope, Variable variable) {
    if (variable.isDynamicTyped())
      return ANY;
    return fromClassNode(typeScope, variable.getOriginType());
  }*/
  
  private static Type fromClassNode(TypeScope typeScope, ClassNode classNode) {
    return typeScope.getType(classNode);
  }
  
  
  // --- visits
  
  @Override
  public Type visitClass(ClassNode node, TypeCheckEnv env) {
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
      storeInTypeMap(mn, visitMethod(mn, env));
    }
    return NULL_TYPE;
  }

  @Override
  public Type visitConstructor(ConstructorNode node, TypeCheckEnv param) {
    //FIXME
    return null;
  }

  @Override
  public Type visitMethod(MethodNode node, TypeCheckEnv env) {
    TypeScope typeScope = env.getTypeScope();
    Type returnType = (node.isDynamicReturnType())?ANY: fromClassNode(typeScope, node.getReturnType());
    typecheck(node.getCode(), new TypeCheckEnv(returnType, typeScope, null));
    
    // store returnType to be used by Gen pass
    return returnType;
  }

  @Override
  public Type visitField(FieldNode node, TypeCheckEnv env) {
    Type fieldType = fromClassNode(env.getTypeScope(), node.getType());
    Expression init = node.getInitialExpression();
    if (init != null) {
      typecheck(init, env.expectedType(fieldType));
    }
    return null;
  }

  @Override
  public Type visitProperty(PropertyNode node, TypeCheckEnv env) {
    return null;
  }
  
  
  // --- statement
  
  @Override
  public Type visitBlockStatement(BlockStatement blockStatement, TypeCheckEnv env) {
    Liveness liveness = ALIVE;
    for(Statement statement: blockStatement.getStatements()) {
      Liveness statementLiveness = asLiveness(typecheck(statement, env));
      if (statementLiveness == DEAD) {
        liveness = DEAD;
      }
    }
    return liveness;
  }
  
  @Override
  public Type visitExpressionStatement(ExpressionStatement statement, TypeCheckEnv env) {
    typecheck(statement.getExpression(), env.expectedType(VOID));
    return ALIVE;
  }
  
  @Override
  public Type visitReturnStatement(ReturnStatement statement, TypeCheckEnv env) {
    Type returnType = env.getReturnType();
    typecheck(statement.getExpression(), env.expectedType(returnType));
    
    // HACK: any return type means not DEAD (thus ALIVE)
    // but we need to store the return type for Gen pass
    return returnType;
  }
  
  @Override
  public Type visitBytecodeSequence(BytecodeSequence statement, TypeCheckEnv env) {
    Liveness liveness = ALIVE;
    for (Object part: statement.getInstructions()) {
      if (part == EmptyExpression.INSTANCE) {
        // do nothing ??
      } else if (part instanceof Expression) {
        accept((Expression)part, env.expectedType(ANY));
        //TODO add a pop here ?? 
      } else if (part instanceof Statement) {
        Liveness statementLiveness = asLiveness(accept((Statement) part, env));
        if (statementLiveness == DEAD) {
          liveness = DEAD;
        }
      } else {
        BytecodeInstruction runner = (BytecodeInstruction) part;
        // just check
      }
    }
    return liveness;
  }
  
  
  // --- expressions
  
  @Override
  public Type visitConstantExpression(ConstantExpression expression, TypeCheckEnv env) {
    if (env.getExpectedType() == VOID) { // don't generate a constant if not needed
      return VOID;
    }
    
    if (expression == ConstantExpression.NULL) {
      return ANY;
    }
    
    Class<?> clazz = expression.getValue().getClass();  // can be a boxed type
    PrimitiveType primitive = PrimitiveType.wrapperAsPrimitive(clazz);
    if (primitive != null) {
      return primitive;
    }
    return env.getTypeScope().getType(clazz);
  }
  
  @Override
  public Type visitDeclarationExpression(DeclarationExpression expression, TypeCheckEnv env) {
    return visitAssignment(expression, env);
  }
  
  private Type visitAssignment(BinaryExpression expression, TypeCheckEnv env) {
    Expression leftExpression = expression.getLeftExpression();
    if (leftExpression instanceof TupleExpression) {
      throw new AssertionError("NYI");
    }
    
    Type leftType = typecheck(leftExpression, env.expectedType(ANY));
    Type rightType = typecheck(expression.getRightExpression(), env.expectedType(leftType));
    
    // assignment in a statement or in an expression ?
    return (env.getExpectedType() == VOID)? VOID: rightType;
  }
  
  @Override
  public Type visitVariableExpression(VariableExpression expression, TypeCheckEnv env) {
    Variable variable = expression.getAccessedVariable();
    if (variable == null) {  // this or super have no associated variable ?
      return ANY;
    }
    
    return fromClassNode(env.getTypeScope(), variable.getOriginType());
  }
  
  @Override
  public Type visitClassExpression(ClassExpression expression, TypeCheckEnv env) {
    // add class type to the type scope as dependency
    fromClassNode(env.getTypeScope(), expression.getType());
    
    return env.getTypeScope().getType(Class.class);
  }
  
  @Override
  public Type visitAttributeExpression(AttributeExpression expression, TypeCheckEnv env) {
    typecheck(expression.getObjectExpression(), env);
    
    // String ?
    //typecheck(expression.getProperty(), env);
    
    return ANY;
  }
  
  @Override
  public Type visitPropertyExpression(PropertyExpression expression, TypeCheckEnv env) {
    Expression receiverExpression = expression.getObjectExpression();
    typecheck(receiverExpression, env);
    
    String propertyName = expression.getPropertyAsString();
    if (propertyName == null) {
      throw new AssertionError("NYI");
    }
    
    return ANY;
  }
  
  @Override
  public Type visitFieldExpression(FieldExpression expression, TypeCheckEnv env) {
    FieldNode field = expression.getField();
    if (field.isDynamicTyped()) {
      return ANY;
    }
    return fromClassNode(env.getTypeScope(), field.getOriginType());
  }
  
  @Override
  public Type visitSpreadExpression(SpreadExpression expression, TypeCheckEnv env) {
    // FIXME no true, Collection<E> -> List<E>
    return typecheck(expression.getExpression(), env);
  }
  
  @Override
  public Type visitBinaryExpression(BinaryExpression expression, TypeCheckEnv env) {
    //Type leftType = typecheck(expression.getLeftExpression(), env);
    //Type rightType = typecheck(expression.getRightExpression(), env);
    
    switch (expression.getOperation().getType()) {
      case org.codehaus.groovy.syntax.Types.EQUAL: // = assignment
        return visitAssignment(expression, env);
       
      case org.codehaus.groovy.syntax.Types.PLUS:      // + binary op
      case org.codehaus.groovy.syntax.Types.MINUS:     // - binary op
      case org.codehaus.groovy.syntax.Types.MULTIPLY:  // * binary op
      case org.codehaus.groovy.syntax.Types.DIVIDE:    // / binary op
        typecheck(expression.getLeftExpression(), env);
        typecheck(expression.getRightExpression(), env);
        return ANY;
        
      default:
        throw new AssertionError("binary "+expression.getOperation().getType()+": NIY");
    }
    
    /*
    if (leftType == Type.ANY || rightType == Type.ANY) {
      return Type.ANY;
    }
    
    MetaClass leftMetaClass = ((RuntimeType)leftType).getMetaClass();
    MetaClass rightMetaClass = ((RuntimeType)rightType).getMetaClass();
    
    String name = expression.getOperation().getText();
    FunctionType signature = new FunctionType(RT.getMetaClass(Object.class), rightMetaClass);
    MOPResult mopOperator = leftMetaClass.mopOperator(new MOPOperatorEvent(null, name, signature));
    
    */
  }
  
  @Override
  public Type visitMethodCallExpression(MethodCallExpression expression, TypeCheckEnv env) {
    String name = expression.getMethodAsString();
    if (name == null) {
      //throw new AssertionError("NYI");  //FIXME
      return ANY;
    }
    
    Expression arguments = expression.getArguments();
    if (!(arguments instanceof TupleExpression)) {
      throw new AssertionError("NYI");
    }
    
    typecheck(expression.getObjectExpression(), env);
    TupleExpression tuple = (TupleExpression)arguments;
    for(Expression expr: tuple.getExpressions()) {
      typecheck(expr, env.expectedType(ANY));
    }
    return env.getExpectedType();
  }
  
  @Override
  public Type visitConstructorCallExpression(ConstructorCallExpression expression, TypeCheckEnv env) {
    Expression arguments = expression.getArguments();
    if (!(arguments instanceof TupleExpression)) {
      throw new AssertionError("NYI");
    }
    
    TupleExpression tuple = (TupleExpression)arguments;
    for(Expression expr: tuple.getExpressions()) {
      typecheck(expr, env.expectedType(ANY));
    }
    return env.getExpectedType();
  }
  
  @Override
  public Type visitBytecodeExpression(BytecodeExpression expression, TypeCheckEnv env) {
    return ANY;
  }

  
  
  // --- TODO

  @Override
  public Type visitForLoop(ForStatement forLoop, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitWhileLoop(WhileStatement loop, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitDoWhileLoop(DoWhileStatement loop, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitIfElse(IfStatement ifElse, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitAssertStatement(AssertStatement statement, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitTryCatchFinally(TryCatchStatement statement,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitSwitch(SwitchStatement statement, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitCaseStatement(CaseStatement statement, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitBreakStatement(BreakStatement statement, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitContinueStatement(ContinueStatement statement,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitThrowStatement(ThrowStatement statement, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitSynchronizedStatement(SynchronizedStatement statement,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitCatchStatement(CatchStatement statement, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitStaticMethodCallExpression(
      StaticMethodCallExpression expression, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitTernaryExpression(TernaryExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitShortTernaryExpression(ElvisOperatorExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitPrefixExpression(PrefixExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitPostfixExpression(PostfixExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitBooleanExpression(BooleanExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitClosureExpression(ClosureExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitTupleExpression(TupleExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitMapExpression(MapExpression expression, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitMapEntryExpression(MapEntryExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitListExpression(ListExpression expression, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitRangeExpression(RangeExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitMethodPointerExpression(MethodPointerExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  

  @Override
  public Type visitRegexExpression(RegexExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitGStringExpression(GStringExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitArrayExpression(ArrayExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitSpreadMapExpression(SpreadMapExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitNotExpression(NotExpression expression, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitUnaryMinusExpression(UnaryMinusExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitUnaryPlusExpression(UnaryPlusExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitBitwiseNegationExpression(
      BitwiseNegationExpression expression, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitCastExpression(CastExpression expression, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitArgumentlistExpression(ArgumentListExpression expression,
      TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Type visitClosureListExpression(
      ClosureListExpression closureListExpression, TypeCheckEnv param) {
    // TODO Auto-generated method stub
    return null;
  }
}

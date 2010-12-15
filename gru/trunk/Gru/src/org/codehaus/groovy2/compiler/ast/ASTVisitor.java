package org.codehaus.groovy2.compiler.ast;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PropertyNode;
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
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;

public interface ASTVisitor<R, P> {

  // class & members
  
  public R visitClass(ClassNode node, P param) ;
  public R visitConstructor(ConstructorNode node, P param) ;
  public R visitMethod(MethodNode node, P param) ;
  public R visitField(FieldNode node, P param) ;
  public R visitProperty(PropertyNode node, P param) ;
  
  
  // statements

  public R  visitBlockStatement(BlockStatement statement, P param) ;

  public R  visitForLoop(ForStatement forLoop, P param) ;

  public R  visitWhileLoop(WhileStatement loop, P param) ;

  public R  visitDoWhileLoop(DoWhileStatement loop, P param) ;

  public R  visitIfElse(IfStatement ifElse, P param) ;

  public R  visitExpressionStatement(ExpressionStatement statement, P param) ;

  public R  visitReturnStatement(ReturnStatement statement, P param) ;

  public R  visitAssertStatement(AssertStatement statement, P param) ;

  public R  visitTryCatchFinally(TryCatchStatement statement, P param) ;

  public R  visitSwitch(SwitchStatement statement, P param) ;

  public R  visitCaseStatement(CaseStatement statement, P param) ;

  public R  visitBreakStatement(BreakStatement statement, P param) ;

  public R  visitContinueStatement(ContinueStatement statement, P param) ;

  public R  visitThrowStatement(ThrowStatement statement, P param) ;

  public R  visitSynchronizedStatement(SynchronizedStatement statement, P param) ;
    
  public R  visitCatchStatement(CatchStatement statement, P param) ;

  
  // expressions

  public R  visitMethodCallExpression(MethodCallExpression call, P param) ;

  public R  visitStaticMethodCallExpression(StaticMethodCallExpression expression, P param) ;

  public R  visitConstructorCallExpression(ConstructorCallExpression expression, P param) ;

  public R  visitTernaryExpression(TernaryExpression expression, P param) ;
    
  public R  visitShortTernaryExpression(ElvisOperatorExpression expression, P param) ;

  public R  visitBinaryExpression(BinaryExpression expression, P param) ;

  public R  visitPrefixExpression(PrefixExpression expression, P param) ;

  public R  visitPostfixExpression(PostfixExpression expression, P param) ;

  public R  visitBooleanExpression(BooleanExpression expression, P param) ;

  public R  visitClosureExpression(ClosureExpression expression, P param) ;

  public R  visitTupleExpression(TupleExpression expression, P param) ;

  public R  visitMapExpression(MapExpression expression, P param) ;

  public R  visitMapEntryExpression(MapEntryExpression expression, P param) ;

  public R  visitListExpression(ListExpression expression, P param) ;

  public R  visitRangeExpression(RangeExpression expression, P param) ;

  public R  visitPropertyExpression(PropertyExpression expression, P param) ;

  public R  visitAttributeExpression(AttributeExpression attributeExpression, P param) ;

  public R  visitFieldExpression(FieldExpression expression, P param) ;

  public R  visitMethodPointerExpression(MethodPointerExpression expression, P param) ;

  public R  visitConstantExpression(ConstantExpression expression, P param) ;

  public R  visitClassExpression(ClassExpression expression, P param) ;

  public R  visitVariableExpression(VariableExpression expression, P param) ;

  public R  visitDeclarationExpression(DeclarationExpression expression, P param) ;

  public R  visitRegexExpression(RegexExpression expression, P param) ;

  public R  visitGStringExpression(GStringExpression expression, P param) ;

  public R  visitArrayExpression(ArrayExpression expression, P param) ;

  public R  visitSpreadExpression(SpreadExpression expression, P param) ;

  public R  visitSpreadMapExpression(SpreadMapExpression expression, P param) ;

  public R  visitNotExpression(NotExpression expression, P param) ;

  public R  visitUnaryMinusExpression(UnaryMinusExpression expression, P param) ;

  public R  visitUnaryPlusExpression(UnaryPlusExpression expression, P param) ;

  public R  visitBitwiseNegationExpression(BitwiseNegationExpression expression, P param) ;

  public R  visitCastExpression(CastExpression expression, P param) ;

  public R  visitArgumentlistExpression(ArgumentListExpression expression, P param) ;

  public R  visitClosureListExpression(ClosureListExpression closureListExpression, P param) ;

  public R  visitBytecodeExpression(BytecodeExpression expression, P param) ;
}
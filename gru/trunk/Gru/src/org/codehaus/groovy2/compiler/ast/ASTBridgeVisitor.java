package org.codehaus.groovy2.compiler.ast;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GroovyClassVisitor;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
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
import org.codehaus.groovy.classgen.BytecodeSequence;

public abstract class ASTBridgeVisitor<R, P> implements ASTVisitor<R, P> {
  private final VisitorBridge visitorBridge =
      new VisitorBridge();
  
  P paramValue;
  R returnValue;
  
  public R accept(ASTNode node, P param) {
    try {
      if (node instanceof ClassNode) {
        return visitClass((ClassNode)node, param);
      }
      if (node instanceof BytecodeSequence) {
        return visitBytecodeSequence((BytecodeSequence)node, param);
      }
      paramValue = param;
      node.visit(visitorBridge);
      return returnValue;
    } finally {
      paramValue = null;
      returnValue = null;
    }
  }
  
  public GroovyClassVisitor asClassVisitor() {
    return visitorBridge;
  }
  
  public GroovyCodeVisitor asCodeVisitor() {
    return visitorBridge;
  }
  
  public abstract R visitBytecodeSequence(BytecodeSequence statement, P param);
  
  class VisitorBridge implements GroovyClassVisitor, GroovyCodeVisitor {
    @Override
    public void visitClass(ClassNode node) {
      returnValue = ASTBridgeVisitor.this.visitClass(node, paramValue);
    }
    @Override
    public void visitConstructor(ConstructorNode node) {
      returnValue = ASTBridgeVisitor.this.visitConstructor(node, paramValue);
    }
    @Override
    public void visitMethod(MethodNode node) {
      returnValue = ASTBridgeVisitor.this.visitMethod(node, paramValue);
    }
    @Override
    public void visitField(FieldNode node) {
      returnValue = ASTBridgeVisitor.this.visitField(node, paramValue);
    }
    @Override
    public void visitProperty(PropertyNode node) {
      returnValue = ASTBridgeVisitor.this.visitProperty(node, paramValue);
    }
    
 // statements

    //-------------------------------------------------------------------------

    @Override
    public void visitBlockStatement(BlockStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitBlockStatement(statement, paramValue);
    }

    @Override
    public void visitForLoop(ForStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitForLoop(statement, paramValue);
    }

    @Override
    public void visitWhileLoop(WhileStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitWhileLoop(statement, paramValue);
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitDoWhileLoop(statement, paramValue);
    }

    @Override
    public void visitIfElse(IfStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitIfElse(statement, paramValue);
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitExpressionStatement(statement, paramValue);
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitReturnStatement(statement, paramValue);
    }

    @Override
    public void visitAssertStatement(AssertStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitAssertStatement(statement, paramValue);
    }

    @Override
    public void visitTryCatchFinally(TryCatchStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitTryCatchFinally(statement, paramValue);
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitSwitch(statement, paramValue);
    }

    @Override
    public void visitCaseStatement(CaseStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitCaseStatement(statement, paramValue);
    }

    @Override
    public void visitBreakStatement(BreakStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitBreakStatement(statement, paramValue);
    }

    @Override
    public void visitContinueStatement(ContinueStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitContinueStatement(statement, paramValue);
    }

    @Override
    public void visitThrowStatement(ThrowStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitThrowStatement(statement, paramValue);
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitSynchronizedStatement(statement, paramValue);
    }
    
    @Override
    public void visitCatchStatement(CatchStatement statement) {
      returnValue = ASTBridgeVisitor.this.visitCatchStatement(statement, paramValue);
    }
    
    // expressions

    //-------------------------------------------------------------------------

    @Override
    public void visitMethodCallExpression(MethodCallExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitMethodCallExpression(expression, paramValue);
    }

    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitStaticMethodCallExpression(expression, paramValue);
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitConstructorCallExpression(expression, paramValue);
    }

    @Override
    public void visitTernaryExpression(TernaryExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitTernaryExpression(expression, paramValue);
    }
    
    @Override
    public void visitShortTernaryExpression(ElvisOperatorExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitShortTernaryExpression(expression, paramValue);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitBinaryExpression(expression, paramValue);
    }

    @Override
    public void visitPrefixExpression(PrefixExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitPrefixExpression(expression, paramValue);
    }

    @Override
    public void visitPostfixExpression(PostfixExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitPostfixExpression(expression, paramValue);
    }

    @Override
    public void visitBooleanExpression(BooleanExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitBooleanExpression(expression, paramValue);
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitClosureExpression(expression, paramValue);
    }

    @Override
    public void visitTupleExpression(TupleExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitTupleExpression(expression, paramValue);
    }

    @Override
    public void visitMapExpression(MapExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitMapExpression(expression, paramValue);
    }

    @Override
    public void visitMapEntryExpression(MapEntryExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitMapEntryExpression(expression, paramValue);
    }

    @Override
    public void visitListExpression(ListExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitListExpression(expression, paramValue);
    }

    @Override
    public void visitRangeExpression(RangeExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitRangeExpression(expression, paramValue);
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitPropertyExpression(expression, paramValue);
    }

    @Override
    public void visitAttributeExpression(AttributeExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitAttributeExpression(expression, paramValue);
    }

    @Override
    public void visitFieldExpression(FieldExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitFieldExpression(expression, paramValue);
    }

    @Override
    public void visitMethodPointerExpression(MethodPointerExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitMethodPointerExpression(expression, paramValue);
    }

    @Override
    public void visitConstantExpression(ConstantExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitConstantExpression(expression, paramValue);
    }

    @Override
    public void visitClassExpression(ClassExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitClassExpression(expression, paramValue);
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitVariableExpression(expression, paramValue);
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitDeclarationExpression(expression, paramValue);
    }

    @Override
    public void visitRegexExpression(RegexExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitRegexExpression(expression, paramValue);
    }

    @Override
    public void visitGStringExpression(GStringExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitGStringExpression(expression, paramValue);
    }

    @Override
    public void visitArrayExpression(ArrayExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitArrayExpression(expression, paramValue);
    }

    @Override
    public void visitSpreadExpression(SpreadExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitSpreadExpression(expression, paramValue);
    }

    @Override
    public void visitSpreadMapExpression(SpreadMapExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitSpreadMapExpression(expression, paramValue);
    }

    @Override
    public void visitNotExpression(NotExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitNotExpression(expression, paramValue);
    }

    @Override
    public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitUnaryMinusExpression(expression, paramValue);
    }

    @Override
    public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitUnaryPlusExpression(expression, paramValue);
    }

    @Override
    public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitBitwiseNegationExpression(expression, paramValue);
    }

    @Override
    public void visitCastExpression(CastExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitCastExpression(expression, paramValue);
    }

    @Override
    public void visitArgumentlistExpression(ArgumentListExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitArgumentlistExpression(expression, paramValue);
    }

    @Override
    public void visitClosureListExpression(ClosureListExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitClosureListExpression(expression, paramValue);
    }

    @Override
    public void visitBytecodeExpression(BytecodeExpression expression) {
      returnValue = ASTBridgeVisitor.this.visitBytecodeExpression(expression, paramValue);
    }
  }
}

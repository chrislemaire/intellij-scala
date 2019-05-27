package org.jetbrains.plugins.scala
package lang.psi.impl.base

import com.intellij.lang.ASTNode
import org.jetbrains.plugins.scala.lang.psi.api.base.{InterpolatedStringType, ScInterpolatedStringLiteral}
import org.jetbrains.plugins.scala.lang.psi.api.expr.{ScMethodCall, ScReferenceExpression}
import org.jetbrains.plugins.scala.lang.psi.types.result._

import scala.meta.intellij.QuasiquoteInferUtil

final class ScInterpolatedStringLiteralImpl(node: ASTNode)
  extends ScLiteralImpl(node) with ScInterpolatedStringLiteral {

  import InterpolatedStringType._
  import ScLiteralImpl._

  override def getType: StringType = getNode.getFirstChildNode.getText match {
    case "s" => STANDART
    case "f" => FORMAT
    case "id" => PATTERN
    case "raw" => RAW
    case _ => null
  }

  protected override def innerType: TypeResult = {
    getStringContextExpression match {
      case Some(mc: ScMethodCall) => mc.getInvokedExpr match {
        case expr: ScReferenceExpression if QuasiquoteInferUtil.isMetaQQ(expr) =>
          QuasiquoteInferUtil.getMetaQQExprType(this)
        case expr: ScReferenceExpression =>
          InterpolatedStringMacroTypeProvider.getTypeProvider(expr) match {
            case Some(typeProvider) => typeProvider.inferExpressionType(this)
            case None => mc.getNonValueType()
          }
        case _ => mc.getNonValueType()
      }
      case Some(expr) => expr.getNonValueType()
      case _ => Failure(s"Cannot find method ${getFirstChild.getText} of StringContext")
    }
  }

  def reference: Option[ScReferenceExpression] = {
    getFirstChild match {
      case ref: ScReferenceExpression => Some(ref)
      case _ => None
    }
  }

  override def isMultiLineString: Boolean = getText.endsWith(MultiLineQuote)

  override def isString: Boolean = true

  override def getValue: AnyRef = findChildByClassScala(classOf[ScLiteralImpl]) match {
    // FIXME: it is actually always "" because child with type ScLiteralImpl can't be found...
    case literal: ScLiteralImpl => literal.getValue
    case _ => ""
  }
}

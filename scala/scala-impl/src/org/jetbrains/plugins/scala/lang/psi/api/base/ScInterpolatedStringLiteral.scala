package org.jetbrains.plugins.scala
package lang
package psi
package api
package base

import org.jetbrains.plugins.scala.lang.psi.api.base.literals.ScStringLiteral
import org.jetbrains.plugins.scala.lang.psi.api.expr.{ScMethodCall, ScReferenceExpression}

/**
  * User: Dmitry Naydanov
  * Date: 3/17/12
  */
trait ScInterpolatedStringLiteral extends ScStringLiteral with ScInterpolated {

  def kind: base.ScInterpolatedStringLiteral.Kind

  def reference: Option[ScReferenceExpression]

  def referenceName: String

  def desugaredExpression: Option[(ScReferenceExpression, ScMethodCall)]
}

object ScInterpolatedStringLiteral {

  def unapply(literal: ScInterpolatedStringLiteral): Option[ScReferenceExpression] =
    literal.reference

  sealed trait Kind

  case object Standard extends Kind

  case object Format extends Kind

  case object Pattern extends Kind

  case object Raw extends Kind
}
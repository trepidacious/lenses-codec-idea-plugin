package org.rebeam.lenses.injector

import org.jetbrains.plugins.scala.lang.psi.impl.toplevel.typedef.SyntheticMembersInjector
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.{ScTypeDefinition, ScObject, ScClass}
import org.jetbrains.plugins.scala.lang.psi.impl.base.ScLiteralImpl
import org.jetbrains.plugins.scala.lang.psi.impl.statements.params.ScClassParameterImpl
import org.jetbrains.plugins.scala.lang.psi.types.result.TypingContext

import scala.collection.mutable.ArrayBuffer

class LensesCodecInjector extends SyntheticMembersInjector {
  override def injectFunctions(source: ScTypeDefinition): Seq[String] = {
    source match {
      // LensesCodec lenses generation
      case obj: ScObject =>
        obj.fakeCompanionClassOrCompanionClass match {
          case clazz: ScClass if clazz.findAnnotation("org.rebeam.lenses.macros.Lenses") != null => mkLens(obj)
          case _ => Seq.empty
        }
      case _ => Seq.empty
    }
  }

  private def mkLens(obj: ScObject): ArrayBuffer[String] = {
    val buffer = new ArrayBuffer[String]
    val clazz = obj.fakeCompanionClassOrCompanionClass.asInstanceOf[ScClass]
    val fields = clazz.allVals.collect({ case (f: ScClassParameterImpl, _) => f }).filter(_.isCaseClassVal)
    val prefix = Option(clazz.findAnnotation("org.rebeam.lenses.macros.Lenses").findAttributeValue("value")) match {
      case Some(literal: ScLiteralImpl) => literal.getValue.toString
      case _ => ""
    }
    fields.foreach({ i =>
      val template = if (clazz.typeParameters.isEmpty)
        // This is sometimes giving "Any" - would be nice to fix
        s"def $prefix${i.name}: _root_.org.rebeam.lenses.LensN[${clazz.qualifiedName}, ${i.getType(TypingContext.empty).map(_.canonicalText).getOrElse("Any")}] = ???"
      else {
        val tparams = s"[${clazz.typeParameters.map(_.getText).mkString(",")}]"
        s"def $prefix${i.name}$tparams: _root_.org.rebeam.lenses.LensN[${clazz.qualifiedName}$tparams, ${i.typeElement.get.calcType}] = ???"
      }
      buffer += template
    })
    buffer
  }
}
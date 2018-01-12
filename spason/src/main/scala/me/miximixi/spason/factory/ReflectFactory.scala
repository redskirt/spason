package me.miximixi.spason.factory

import com.sasaki.packages._
import independent._
import constant._
import constant.original._
import reflect._
import scala.reflect.runtime.universe._
import me.miximixi.spason.annotation.Alias._
  
/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-12-13 下午16:48:03
 * @Description 
 */
object ReflectFactory {
  def extractFieldAsPrimary[E: TT] = extractSingleFieldWhileAnnotation[E, P]

  private[spason] def annotationIs[T <: SA: TT](a: Annotation) = a.tree.tpe =:= typeOf[T]

  def extractNamespace2Aliase(annotations: Seq[Annotation]) = {
    val opAttr = annotations find (annotationIs[A] _)
    opAttr match {
      case Some(_) =>
        val Apply(_, Literal(Constant(namespace: String)) :: Literal(Constant(aliase: String)) :: Nil) = opAttr.head.tree
        (namespace.split("""\$""").map(s"`" + _ + "`.").reduce(_ + _), if (aliase nonEmpty) $s + aliase else $e)
      case None => ($e, $e)
    }
  }

  def extractInjectStatement(annotations: Seq[Annotation]) = {
    val opInject = annotations find (annotationIs[O] _)
    opInject match {
      case Some(_) =>
        val Apply(_, Literal(Constant(statement: String)) :: Nil) = opInject.head.tree
        statement
      case None => $e
    }
  }
}
package me.miximixi.spason.factory

import com.sasaki.packages._
import independent._
import constant._
import constant.original._
import reflect._
import scala.reflect.runtime.universe.typeOf
import me.miximixi.spason.annotation.Alias._

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-11-08 下午5:37:23
 * @Description 
 */
trait QueryBuilder {
  
  import me.miximixi.spason.factory.ReflectFactory._

  def buildStatement[T: TT](tableName: String, where: String = $e) =
    invokeNonNothing[T, String] { () =>
      s"""select -- partSelect\n${
        extractField2Annotations[T] /*field___annotations*/
          .filterNot(_._2.exists(annotationIs[I] _))
          .map {
            case (f, as) =>
              val p___a = extractNamespace2Aliase(as)
              val injectStatement = extractInjectStatement(as)

              {
                if (injectStatement.nonEmpty)
                  injectStatement
                else
                  p___a._1 /*prefix*/ + f /*field*/
              } + p___a._2 /*aliase*/
          }
          .mkString("\t", ",\n\t", "\n")
      } from \n$tableName\n
        where true -- partWhere\n 
        ${if (where nonEmpty) s"and $where " else $e}
        """
    }

  def buildJson[T: TT](r: com.sasaki.spark.enums.SparkType.Row) = 
    invokeNonNothing[T, String] { () =>
      s"{${
      	extractField2Type[T].map { case (f, t) =>
      	val k = s""""$f":"""
      	t match {
      	case t if t =:= typeOf[JLong]    => s"$k${ r.getAs[JLong](f) }"
      	case t if t =:= typeOf[JDouble]  => s"$k${ r.getAs[JDouble](f) }"
      	case t if t =:= typeOf[JBoolean] => s"$k${ r.getAs[JBoolean](f) }"
      	case t if t =:= typeOf[String]   => s"$k${ if (nonNull(r.getAs[String](f))) "\"" + r.getAs[String](f) + "\"" else $n }"
      	case t if t =:= typeOf[Any]      => s"$k${ r.getAs[String](f) }"
      	case _                           => throw new IllegalArgumentException(s"Unsupported type of argument: $t") 
      	}
      	}.mkString(",")
      }}"
    }
}    


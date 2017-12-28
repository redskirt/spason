package me.miximixi.spason.factory

import independent._
import reflect._
import com.sasaki.spark.enums.SparkType._

import me.miximixi.spason.enums._
import ReflectFactory._
import me.miximixi.spason.annotation.Alias._

/**
 * @Author Sasaki
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-10-13 下午2:28:01
 * @Description
 */
trait RDDRow2TargetColumnFunctionFactory {

  def buildFxMappingTargetRow[E: TT](
    o: Row,
    f_x: (JInt, Row) => E,
    g_f_x: ((JInt, Row) => E) => Seq[E]): Seq[E] =
    try g_f_x(f_x)
    catch {
      case t: Throwable =>
        t.printStackTrace()
        println(s"FAIL: fxBuildTarget --> $o"); Seq[E]()
    }

  def buildRow2TargetFx[T <: Product: TT](p: ProvideMode.Value = ProvideMode.SINGLE_TO_SINGLE) = 
    (o: Row) =>
    buildFxMappingTargetRow[T](
      o, 
      (i, o) => buildInstance[T](mappingRow2InstanceArgs[T](i, o): _*), 
      if (ProvideMode.SINGLE_TO_SINGLE == p) 
        buildSingleMappingFx[T](o) 
      else 
        buildMultipleMappingFx[T](o))

  def buildTargetDataFrameFields[T <: Product: TT] =
    extractField2Annotations[T].map { case (f, as) =>
        val p___a = extractNamespace2Aliase(as)
        if (as.exists(annotationIs[A] _) && p___a._2.nonEmpty) p___a._2 else f
    }

  protected def $Value[E](columnName: String, r: Row, i: JInt): Option[E] = {
    val o = r.getAs[Seq[E]](columnName)
    if (isNull(o) || o.isEmpty) None /* 第三层List为Null时值为Null*/ else Some(o(i))
  }

  protected def $Value[E](columnName: String, r: Row): Option[E] = {
    val o = r.getAs[E](columnName)
    if (isNull(o)) None else Some(o)
  }
  
  protected def $Size[E: TT](r: Row) = {
    val o = r.getAs[Seq[E]](extractFieldAsPrimary[E].orNull)
    if (isNull(o) || o.isEmpty) 1 /* 第三层List为Null时允许迭代外层赋值 */ else o.length
  }

  protected def buildSingleMappingFx[E: TT](o: Row) = (f_x: (JInt, Row) => E) => Seq[E](f_x(0, o))

  protected def buildMultipleMappingFx[E: TT](o: Row) = (f_x: (JInt, Row) => E) => for (i <- 0 until $Size[E](o)) yield f_x(i, o)

  private def mappingRow2InstanceArgs[E: TT](i: JInt, r: Row /*Row中[attr_1, attr_2, ... attr_i]属性列表的下标*/ ) = {
    import _root_.scala.reflect.runtime.universe._

    def allocate[T](i: JInt, r: Row, field: String): Option[T] =
      if (existsAnnotationFromField[E, M](field) && nonNull(i)) /*1->n，使用循环取值器*/
        $Value[T](field, r, i)
      else /*1->1，直接取值*/
        Some[T](r.getAs[T](field))

    extractField2Type[E] map {
      case (f, t) =>
        t match {
          case t if t =:= typeOf[JLong]              => allocate[JLong](i, r, f) orNull
          case t if t =:= typeOf[JDouble]            => allocate[JDouble](i, r, f) orNull
          case t if t =:= typeOf[JBoolean]           => allocate[JBoolean](i, r, f) orNull
          case t if t =:= typeOf[String]             => allocate[String](i, r, f) orNull
          case t if t =:= typeOf[Seq[String]]        => allocate[Seq[String]](i, r, f) orNull
          case t if t =:= typeOf[Seq[JLong]]         => allocate[Seq[JLong]](i, r, f) orNull
          case t if t =:= typeOf[JTimestamp]         => timestamp(allocate(i, r, f) orNull) orNull
          case _                                     => throw new IllegalArgumentException(s"Unsupported type: $t")
        }
    }
  }
}

object AutomatedTargetHolder extends Serializable with RDDRow2TargetColumnFunctionFactory {

  def buildTargetDataFrame[T <: Product: Manifest](rdd: RDD[Row], mode: ProvideMode.Value)(implicit _spark_ : Spark): DF =
    invokeNonNothing[T, DF] { () => 
       import _spark_.implicits._
      rdd.map(buildRow2TargetFx[T](mode)).flatMap(identity _).toDF(buildTargetDataFrameFields[T]: _*)
    } 
}


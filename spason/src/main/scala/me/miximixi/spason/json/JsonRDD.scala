package me.miximixi.spason.json

import reflect._
import scala.reflect.runtime.universe._
import com.sasaki.spark.enums.SparkType._
 
import com.sasaki.packages._
import independent._
import constant.original._
import reflect._

/**
  * @Author Sasaki
  * @Mail redskirt@outlook.com
  * @Timestamp 2017-12-5 上午10:46:47
  * @Description
  */
class JsonRDD(that: RDD[String])(implicit _spark_ : Spark) extends RDD[String](that) {

  def selfCheck[T <: Product: TT](autoInjectSchemeJson: Boolean): RDD[String] =
    invokeNonNothing[T, RDD[String]] { () =>
      val rdd_ = that filter isJson
      if (autoInjectSchemeJson)
        new JsonRDD(rdd_).inject[T]
      else
        rdd_
    }

  def createJsonMappedTempTable(table: String) = 
  	_spark_.read.json(that).createOrReplaceTempView(table)

  private def inject[T <: Product: TT](implicit _spark_ : Spark): RDD[String] = {
    val opBody = extractClassAnnotations[T]
      .find(o => typeIs[me.miximixi.spason.annotation.InstanceBody](o.tree.tpe))
    val fakeSchema___standardSample = opBody match {
      case Some(_) =>
        val Apply(_, Literal(Constant(standardSample: String)) :: Nil) = opBody.head.tree
        (true, standardSample)
      case None => (false, constant.$e)
    }

    lazy val _rddJson = _spark_.sparkContext.parallelize(Seq(fakeSchema___standardSample._2))

    if (fakeSchema___standardSample._1) /*注入标准行*/
      that union _rddJson
    else
      that
  } 

  override def compute(split: org.apache.spark.Partition, context: org.apache.spark.TaskContext): Iterator[String] = 
    that.compute(split, context)

  override protected def getPartitions: Array[org.apache.spark.Partition] = that.partitions
}
    
 
object ImplicitAdapter {
  
  implicit class RDD2JsonRDDAdapter(rdd: RDD[String])(implicit _spark_ : Spark) {
    val _rdd = new JsonRDD(rdd)
    def selfCheck[T <: Product: TT](autoInjectSchemeJson: Boolean) = _rdd.selfCheck[T](autoInjectSchemeJson)
    def createJsonMappedTempTable(table: String) = _rdd createJsonMappedTempTable(table)
  }
  
  implicit class Dataset2JsonRDDAdapter(ds: DS[String])(implicit _spark_ : Spark) {
	  val _rdd : JsonRDD = new JsonRDD(ds.rdd)
	  def selfCheck[T <: Product: TT](autoInjectSchemeJson: Boolean) = _rdd.selfCheck[T](autoInjectSchemeJson)
	  def createJsonMappedTempTable(table: String) = _rdd createJsonMappedTempTable(table)
  }
} 


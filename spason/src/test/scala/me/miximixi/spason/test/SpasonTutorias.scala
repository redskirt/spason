package me.miximixi.spason.test

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import me.miximixi.spason.enums.ProvideMode
import me.miximixi.spason.factory.{ AutomatedTargetHolder, QueryBuilder, RDDRow2TargetColumnFunctionFactory }

import com.sasaki.spark.SparkHandler
import com.sasaki.spark.enums.SparkType._
import me.miximixi.spason.json.JsonRDD

/**
 * @Author Sasaki 
 * @Mail wei.liu@suanhua.org
 * @Timestamp 2017-12-13 上午10:39:23
 * @Description 
 */
@RunWith(classOf[JUnitRunner])  
class SpasonTutorias extends FunSuite 
  with BeforeAndAfter 
  with QueryBuilder 
  with RDDRow2TargetColumnFunctionFactory  
  with SparkHandler {

  import me.miximixi.spason.json.ImplicitAdapter._
  import me.miximixi.spason.test.fake.FakeDatum._
  import me.miximixi.spason.test.poso._
   
  lazy val spark = initHandler("SpasonTutorias", defaultSettings, enableHive = true)
  import spark._
  implicit val spark_ = spark
  
  var _rddSingleJson: RDD[String] = _
  var _rddMultipleJson: RDD[String] = _
  var _rddIncompleteSchemeJson: RDD[String] = _
  
  before { 
    lazy val rddSingleJson = mockSingleJsonRDD
    _rddSingleJson = rddSingleJson
    
    lazy val rddMultipleJson = mockMultipleJsonRDD
    _rddMultipleJson = rddMultipleJson
    
    lazy val rddIncompleteSchemeJson = mockIncompleteSchemeJsonRDD
    _rddIncompleteSchemeJson = rddIncompleteSchemeJson
  }

  /**
   * 通过SINGLE_TO_SINGLE模式将批量JSON转换为表关系表。
   */
  test("1") {
    _rddSingleJson.selfCheck[SingleJson](false).createJsonMappedTempTable(T)
    
    val rddResult = sql(buildStatement[SingleJson](T)).toDF().rdd
    AutomatedTargetHolder.buildTargetDataFrame[SingleJson](rddResult, ProvideMode.SINGLE_TO_SINGLE).createTempView(T2)  
    sql(s"select * from $T2").show(false)
  }

  /**
   * 通过SINGLE_TO_MULTIPLE模式将批量JSON转换为表关系表。
   */
  test("2") {
    _rddMultipleJson.selfCheck[MultipleJson](false).createJsonMappedTempTable(T)  
    
    val rddResult = sql(buildStatement[MultipleJson](T)).toDF().rdd
    AutomatedTargetHolder.buildTargetDataFrame[MultipleJson](rddResult, ProvideMode.SINGLE_TO_MULTIPLE).createTempView(T2)  
    sql(s"select * from $T2").show(false)
  }
  
  /**
   * 转换Scheme不完整的JSON，注入标准的JSON样例。
   */
  test("3") {
	 _rddIncompleteSchemeJson.selfCheck[IncompleteSchemeJson](true).createJsonMappedTempTable(T)  
	  
	  val rddResult = sql(buildStatement[IncompleteSchemeJson](T, "id != -1")).toDF().rdd
	  AutomatedTargetHolder.buildTargetDataFrame[IncompleteSchemeJson](rddResult, ProvideMode.SINGLE_TO_MULTIPLE).createTempView(T2)  
	  sql(s"select * from $T2").show(false)
  }
  
  after {
    spark.stop()
  }

} 

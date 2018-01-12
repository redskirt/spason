package me.miximixi.spason.test.fake

import com.sasaki.packages._
import independent._
import com.sasaki.spark.enums.SparkType.Spark

/**
 * 
 */
object FakeDatum {
  import scala.util.Random
  import me.miximixi.spason.json.ImplicitAdapter._

  val random = new Random(10)
  val T = "T"
  val T2 = "T2"

  def mockSingleJsonRDD(implicit spark: Spark) = {
	  val jsons: Seq[String] = 
	    for (i <- 0 until 10) yield 
	    eraseMultiple(
        s"""{
  	      "id":$i,
  	      "name":"name-$i",
  	      "salary":${random.nextDouble()},
  	      "classInfo":{
  	          "lesson":{
  	            "lessonName":"",
  	            "score":${random.nextInt()}
  	          },
  	          "type":"A",
  	          "isGraduate":false
  	        },
  	      "timestamp":"$currentFormatTime"
	      }
	    """.trim, "\n", "\t", "  ")
	  spark.sparkContext.parallelize(jsons)
  }
  
  def mockMultipleJsonRDD(implicit spark: Spark) = {
    val jsons: Seq[String] =
      for (i <- 0 until 10) yield eraseMultiple(
        s"""{
					  "id":$i,
					  "name":"name-$i",
					  "salary":${random.nextDouble()},
					  "classInfo":{
					  "lesson":{
					  "lessonName":"",
					  "score":${random.nextInt()}
					  },
					  "type":"A",
					  "isGraduate":false
					  },
					  "contact":[
					    ${
					      {
  					      for(j <- 0 until random.nextInt(10)) yield
  					        s"""{"name":"name-$j","age":${ random.nextInt() }}"""
					      }.mkString(",")
					    } 
					  ],
					  "password":"password-$i",
					  "timestamp":"$currentFormatTime"
					  }
					  """.trim, "\n", "\t", "  ")
    spark.sparkContext.parallelize(jsons)
  }
  
  def mockIncompleteSchemeJsonRDD(implicit spark: Spark) = {
	  val jsons: Seq[String] =
			  for (i <- 0 until 3) yield eraseMultiple(
					  s"""{
					  "id":$i,
					  "name":"name-$i",
					  "salary":${random.nextDouble()},
					  "classInfo":{
					  "lesson":{
					  "lessonName":"",
					  "score":${random.nextInt()}
					  },
					  "type":"A",
					  "isGraduate":false
					  },
					  "contact":[
					  ${
						  {
							  for(j <- 0 until random.nextInt(10)) yield
							  s"""{"name":"name-$j","age":${ random.nextInt() }}"""
						  }.mkString(",")
					  } 
					  ],
					  "password":"password-$i"

					  }
					  """.trim, "\n", "\t", "  ")
		  spark.sparkContext.parallelize(jsons)
  }
}
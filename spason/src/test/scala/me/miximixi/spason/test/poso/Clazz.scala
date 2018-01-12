package me.miximixi.spason.test.poso

import com.sasaki.packages._
import constant._
import me.miximixi.spason.annotation._
/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-12-13 上午11:37:14
 * @Description 
 */
case class SingleJson(
  id: JLong,
  name: String,
  salary: JDouble,
  @AttributeMapping("classInfo$lesson", "") //
  lessonName: String,
  @AttributeMapping("classInfo$lesson", "") //
  score: JLong,
  @AttributeMapping("classInfo", "") //
  `type`: String,
  @AttributeMapping("classInfo", "") //
  isGraduate: JBoolean,
  timestamp: JTimestamp)

case class MultipleJson(
  id: JLong,
  name: String,
  salary: JDouble,
  @AttributeMapping("classInfo$lesson", "") //
  lessonName: String,
  @AttributeMapping("classInfo$lesson", "") //
  score: JLong,
  @AttributeMapping("classInfo", "") //
  `type`: String,
  @AttributeMapping("classInfo", "") //
  isGraduate: JBoolean,
  @Primary
  @Multiple
  @Inject("contact.name contactName")
  contactName: String,
  @Multiple
  @Inject("contact.age contactAge")
  contactAge: JLong,
  @Inject("md5(password) password")
  password: String,
  timestamp: JTimestamp)
  
@InstanceBody("""{"id":-1,"name":"name-9","salary":0.33373224333611495,"classInfo":{"lesson":{"lessonName":"","score":678465127},"type":"A","isGraduate":false},"contact":[{"name":"name-0","age":178859563},{"name":"name-1","age":37924559},{"name":"name-2","age":-956040048} ],"password":"password-9","timestamp":"2017-12-20 03:25:07"}""")
case class IncompleteSchemeJson(
		id: JLong,
		name: String,
		salary: JDouble,
		@AttributeMapping("classInfo$lesson", "") //
    lessonName: String,
    @AttributeMapping("classInfo$lesson", "") //
		score: JLong,
		@AttributeMapping("classInfo", "") //
    `type`: String,
    @AttributeMapping("classInfo", "") //
		isGraduate: JBoolean,
		@Primary
		@Multiple
		@Inject("contact.name contactName")
    contactName: String,
    @Multiple
    @Inject("contact.age contactAge")
		contactAge: JLong,
		@Inject("md5(password) password")
    password: String,
    timestamp: JTimestamp)
  
  
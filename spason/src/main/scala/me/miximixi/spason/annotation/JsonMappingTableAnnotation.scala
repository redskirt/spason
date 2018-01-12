package me.miximixi.spason.annotation

import scala.annotation.StaticAnnotation

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp 2017-10-31 下午6:10:54
 * @Description 注解类，主要解释部分由 @see QueryBuilder 和 Row2TargetColumnFunctionFactory完成
 */
final class Ignore extends StaticAnnotation 

final class Primary extends StaticAnnotation

final class Multiple extends StaticAnnotation

final class InstanceBody(standardSample: String) extends StaticAnnotation

final class Inject(statement: String) extends StaticAnnotation

final class AttributeMapping(namespace: String, aliase: String) extends StaticAnnotation

object Alias {
  private[spason]type I = Ignore
  private[spason]type O = Inject
  private[spason]type A = AttributeMapping
  private[spason]type P = Primary
  private[spason]type M = Multiple
}
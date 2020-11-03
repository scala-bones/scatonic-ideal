package com.bones.si.jdbc

package object load {

  class MissingDataException(message: String) extends Throwable(message)

  /** Required.  Checks parameter for null * */
  def req[X](x: X): X = {
    if (x == null) throw new MissingDataException(s"Can not be null")
    else x
  }

  def strOption(str: String): Option[String] = {
    if (str == null || str.isEmpty) None
    else Some(str)
  }

}

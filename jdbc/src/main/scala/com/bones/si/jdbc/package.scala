package com.bones.si

package object jdbc {

  /** Required.  Checks parameter for null **/
  def req[X](x: X): X = {
    if (x == null) throw new IllegalStateException(s"Can not be null")
    else x
  }

  def strOption(str: String) : Option[String] = {
    if (str == null || str.isEmpty) None
    else Some(str)
  }


}

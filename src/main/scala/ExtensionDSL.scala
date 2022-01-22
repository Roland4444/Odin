object ExtensionDSL extends App {
  println("Hello, world. I'm inside an object.")
  println(s"""Call highorder func!::>>${emptyfunc(retStr)}""")
  println(s"""INSIDE STR>>${strfunc(retStr)}""")

  def emptyfunc(Function: () => String) =
    println(s"""XIa ${Function()}""")

  def strfunc(Function: () => String): String =
    (s"""Return STR ${Function()}""")

  def retStr = () => "return FUNC"
}
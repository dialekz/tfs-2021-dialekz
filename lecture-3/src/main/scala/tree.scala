sealed trait Tree

case class Node(value: Int, left: Tree, right: Tree) extends Tree

case object RedLeaf extends Tree

case object YellowLeaf extends Tree

case object GreenLeaf extends Tree

object Tree {
  /**
   * Sum values of nodes that have either yellow or red leaf if no such leaves found sum is zero
   */
  def countYellowAndRedValues(tree: Tree): Int = tree match {
    case x: Node if hasYellowOrRedLeaves(x) ⇒ x.value + countYellowAndRedValues(x.left) + countYellowAndRedValues(x.right)
    case x: Node if !hasYellowOrRedLeaves(x) ⇒ countYellowAndRedValues(x.left) + countYellowAndRedValues(x.right)
    case _ ⇒ 0
  }

  def hasYellowOrRedLeaves(node: Node): Boolean = isYellowOrRed(node.left) || isYellowOrRed(node.right)

  def isYellowOrRed(tree: Tree): Boolean = tree match {
    case RedLeaf | YellowLeaf ⇒ true
    case _ ⇒ false
  }

  /**
   * Find max value in tree
   */
  def maxValue(tree: Tree): Option[Int] = tree match {
    case x: Node ⇒ Option(chooseBetweenThree(x.value, maxValue(x.left), maxValue(x.right)))
    case _ ⇒ Option.empty
  }

  def chooseBetweenThree(value: Int, left: Option[Int], right: Option[Int]): Int = (left, right) match {
    case (None, None) ⇒ value
    case (Some(x), None) ⇒ Math.max(value, x)
    case (None, Some(x)) ⇒ Math.max(value, x)
    case (Some(x), Some(y)) ⇒ Math.max(value, Math.max(x, y))
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    val node1: Node = Node(2, GreenLeaf, GreenLeaf)
    val node2: Node = Node(5, node1, RedLeaf)
    val node3: Node = Node(3, YellowLeaf, node2)
    val node4: Node = Node(9, GreenLeaf, node3)

    println(Tree.countYellowAndRedValues(node4)) // 8
    println(Tree.countYellowAndRedValues(node3)) // 8
    println(Tree.countYellowAndRedValues(node2)) // 5
    println(Tree.countYellowAndRedValues(node1)) // 0
    println()
    println(Tree.maxValue(node4)) // 9
    println(Tree.maxValue(node3)) // 5
    println(Tree.maxValue(node2)) // 5
    println(Tree.maxValue(node1)) // 2
    println(Tree.maxValue(RedLeaf)) // None
  }
}

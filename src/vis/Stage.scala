package vis

import scala.collection.mutable.Map
import scala.math

/** A stage is a two dimensional grid-based field that acts as an interface between simple grid coordinates and actual positional coordinates*/
class Stage {
  
  /**The walls that are aligned with the y-axis.*/
  private val verWalls = Map[(Int, Int), Wall]()
  /**The walls that are aligned with the x-axis.*/
  private val horWalls = Map[(Int, Int), Wall]()
  
  /**Get position in world from grid coordinates.*/
  def position(x: Int, y: Int) = Pos(Stage.SqWidth*x, Stage.SqWidth*y, 0)
  def wallPosition(x: Int, y: Int, dir: Dir) = Pos(Stage.SqWidth*(x + 0.5*dir.xMul), Stage.SqWidth*(y + 0.5*dir.yMul), 0)
  
  def getWall(x: Int, y: Int, dir: Dir) = {
    val container = getWallContainer(x, y, dir)
    container._3.get(container._1, container._2)
  }
  
  def getWalls: Vector[Wall] = (verWalls.values ++ horWalls.values).toVector
  
  def addWall(x: Int, y: Int, dir: Dir): Boolean = {
    if (this.getWall(x, y, dir).isEmpty) {
      val container = getWallContainer(x, y, dir)
      container._3(container._1 -> container._2) = new Wall(wallPosition(x, y, dir), dir, x, y)
      true
    } else false
  }
  
  def removeWall(x: Int, y: Int, dir: Dir): Boolean = {
    if (this.getWall(x, y, dir).isDefined) {
      val container = getWallContainer(x, y, dir)
      container._3.remove(container._1 -> container._2)
      true
    } else false 
  }
  
  /**A helper method to find out where in the two Map variables a certain wall is located.*/
  private def getWallContainer(x: Int, y: Int, dir: Dir): (Int, Int, Map[(Int, Int), Wall]) = {
    dir match {
      case d @ Dir.North => (x, y + 1, horWalls)
      case d @ Dir.East  => (x + 1, y, verWalls)
      case d @ Dir.South => (x, y, horWalls)
      case d @ Dir.West  => (x, y, verWalls)
    }
  }
}

object Stage {
  /**The width of a single square.*/
  val SqWidth: Double = 1.0
}

sealed abstract class Dir (val xMul: Int, val yMul: Int, val rot: Double) {
  def clockwise: Dir = {
    val index = Dir.Clockwise.indexOf(this) + 1
    if (index < 4) {
      Dir.Clockwise(index)
    } else {
      Dir.Clockwise.head
    }
  }
  
  def antiClockwise: Dir = {
    val index = Dir.AntiClockwise.indexOf(this) + 1
    if (index < 4) {
      Dir.AntiClockwise(index)
    } else {
      Dir.AntiClockwise.head
    }
  }
  
  def opposite: Dir = this.clockwise.clockwise
}

object Dir {
  case object North extends Dir(0, 1, 0)
  case object East extends Dir(1, 0, math.Pi * 1.5)
  case object South extends Dir(0, -1, math.Pi)
  case object West extends Dir(-1, 0, math.Pi * 0.5)
  
  val Clockwise = Vector[Dir](North, East, South, West)
  val AntiClockwise = Clockwise.reverse
}
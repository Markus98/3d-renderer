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
  
  def getWall(x: Int, y: Int, dir: Dir) = dir match {
    case Dir.North => horWalls.get(x, y + 1)
    case Dir.East  => verWalls.get(x + 1, y)
    case Dir.South => horWalls.get(x, y)
    case Dir.West  => verWalls.get(x, y)
  }
  
  def getWalls: Vector[Wall] = (verWalls.values ++ horWalls.values).toVector

  def addWall(x: Int, y: Int, dir: Dir): Boolean = {
    if (this.getWall(x, y, dir).isEmpty) {
      dir match {
        case d @ Dir.North => horWalls((x, y + 1)) = new Wall(wallPosition(x, y, d), d)
        case d @ Dir.East  => verWalls((x + 1, y)) = new Wall(wallPosition(x, y, d), d)
        case d @ Dir.South => horWalls((x, y)) = new Wall(wallPosition(x, y, d), d)
        case d @ Dir.West  => verWalls((x, y)) = new Wall(wallPosition(x, y, d), d)
      }
      true
    } else false
  }
}

object Stage {
  val SqWidth: Double = 1 // The width of a single square.
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
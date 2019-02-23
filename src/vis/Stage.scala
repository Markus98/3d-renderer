package vis

import scala.collection.mutable.Map
import scala.math

/** A stage is a two dimensional grid-based field that acts as an interface between simple grid coordinates and actual positional coordinates*/
class Stage {
  /**The walls that are aligned with the y-axis.*/
  val verWalls = Map[(Int, Int), Entity]()
  /**The walls that are aligned with the x-axis.*/
  val horWalls = Map[(Int, Int), Entity]()
  
  def position(x: Int, y: Int) = Pos(x, y, 0)
  
  def getWall(x: Int, y: Int, dir: Dir) = dir match {
    case Dir.North => horWalls.get(x, y+1)
    case Dir.East => verWalls.get(x+1, y)
    case Dir.South => horWalls.get(x, y)
    case Dir.West => verWalls.get(x, y)
  }
}

sealed abstract class Dir (val xDir: Int, val yDir: Int, val rot: Double) {
  
}

object Dir {
  case object North extends Dir(0, 1, 0)
  case object East extends Dir(1, 0, math.Pi * 1.5)
  case object South extends Dir(0, -1, math.Pi)
  case object West extends Dir(-1, 0, math.Pi * 0.5)
  
  val Clockwise = Vector[Dir](North, East, South, West)
}
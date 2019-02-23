package vis

import scala.collection.mutable.Buffer
import scala.math

case class Entity(pos: Pos) {
  var rot: Double = 0.0 // rotation in radians along z-axis
  val verticies = Buffer[Pos]() // Positions in relation to the entity's origin. The object's polygon is drawn in the same order as the buffer. 

  /** Calculates a single point's location with z-axis rotation applied*/
  def rotPos(p: Pos) = Pos(p.x*math.cos(rot) - p.y*math.sin(rot), p.x*math.sin(rot) + p.y*math.cos(rot), p.z)

  def rotPositions = verticies.map(rotPos)

  /** Vertex positions in global coordinate system*/
  def vertexPositions = rotPositions.map( p => Pos(p.x + pos.x, p.y + pos.y, p.z + pos.z) )
}

class Wall(p: Pos, dir: Dir) extends Entity(p) {
  this.rot = dir.rot
  this.verticies += (Pos(-0.5,0,0), Pos(-0.5,0, Wall.Height), Pos(0.5,0,0), Pos(0.5,0, Wall.Height))
}

object Wall {
  val Height = 2.0
}
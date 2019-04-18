package vis

import scala.collection.mutable.Buffer
import scala.math

case class Entity(pos: Pos, var rot: Double = 0.0) {
  //rotation in radians along z-axis
  
  val verticies = Buffer[Pos]() // Positions in relation to the entity's origin. The object's polygon is drawn in the same order as the buffer. 
  
  
  private var previousRot = 0.0
  private var rotatedPoints = this.verticies
  
  /** Returns the verticies of the entity with the rotation applied.*/
  def rotPositions = {
    if (rot != previousRot) {
      rotatedPoints = verticies.map(_.rotated(rot))
      previousRot = rot
    }
    rotatedPoints
  }

  /** Vertex positions in global coordinate system*/
  def vertexPositions = rotPositions.map( p => Pos(p.x + pos.x, p.y + pos.y, p.z + pos.z) )
}

class Wall(p: Pos, val dir: Dir) extends Entity(p) {
  this.rot = dir.rot
  this.verticies += (Pos(-0.5,0,0), Pos(-0.5,0, Wall.Height), Pos(0.5,0, Wall.Height), Pos(0.5,0,0))
}

object Wall {
  val Height = 2.0
}
package vis

import scala.collection.mutable.Buffer
import scala.math

case class Entity(pos: Pos) {
  var rot: Double = 0.0 // rotation in radians along z-axis
  val verticies = Buffer[Pos]() // positions in relation to the entity's origin

  // calculates a single point's location with z-axis rotation applied
  def rotPos(p: Pos) = Pos(p.x*math.cos(rot) - p.y*math.sin(rot), p.x*math.sin(rot) + p.y*math.cos(rot), p.z)

  def rotPositions = verticies.map(rotPos)

  // vertex positions in global coordinate system
  def vertexPositions = rotPositions.map(p => Pos(p.x + pos.x, p.y + pos.y, p.z + pos.z))
}

/*class Wall extends Entity {
  
}*/
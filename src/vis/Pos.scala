package vis

import scala.math._

// Represents a position in 3-dimensional space
case class Pos(x: Double, y: Double, z: Double) {
  // distance between two points
  def dist(p: Pos) = sqrt(pow(x - p.x, 2) + pow(y - p.y, 2) + pow(z - p.z, 2))
  // the distance between two points in the horizontal plane
  def horDist(p: Pos) = sqrt(pow(x - p.x, 2) + pow(y - p.y, 2))
}
package vis

import scala.math._

/**Represents a position in 3-dimensional space*/
case class Pos(var x: Double, var y: Double, var z: Double) {
  /**The distance between two points.*/
  def dist(p: Pos) = sqrt(pow(x - p.x, 2) + pow(y - p.y, 2) + pow(z - p.z, 2))
  /**The distance between two points in the xy-plane.*/
  def horDist(p: Pos) = sqrt(pow(x - p.x, 2) + pow(y - p.y, 2))
  /**The relative position of a position compared to this one.*/
  def relPos(p: Pos) = Pos(p.x - x, p.y - y, p.z - z)
  /**Rotates the point along the origin and the z-axis*/
  def rotated(rot: Double) = Pos(x*math.cos(rot) - y*math.sin(rot), x*math.sin(rot) + y*math.cos(rot), z)
  
  def apply(x: Double, y: Double, z: Double) = {
    this.x = x
    this.y = y
    this.z = z
  }
}
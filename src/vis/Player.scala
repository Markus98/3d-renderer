package vis

import vis._

class Player {
  val pos = Pos(0,0,0)
  var rot: Double = 0
  /**Indicates whether the player is moving towards the desired position in a stage.*/
  var moving = false
  
  //the player's desired position and direction in grid coordinates
  var gridX: Int = 0
  var gridY: Int = 0
  var dir: Dir = Dir.North
  
  def moveForward() = {
    gridX += dir.xMul
    gridY += dir.yMul
  }
  
  def moveBackward() = {
    gridX -= dir.xMul
    gridY -= dir.yMul
  }
  
  def turnLeft() = dir = dir.antiClockwise
  def turnRight() = dir = dir.clockwise
  
  
}

object Player {
  val Height = 1.0
  val MinView = 0.25
  val PixelMultiplier = 1800
}
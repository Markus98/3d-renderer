package vis

import vis._
import scala.math

class Player(val stage: Stage) {
  val pos = Pos(0,0,0)
  var rot: Double = 0
  /**Indicates whether the player is moving towards the desired position in a stage.*/
  var moving = false
  /**Indicates whether the player is rotating towards the desired rotational value.*/
  var rotating = false
  
  //the player's desired position and direction in grid coordinates
  var gridX: Int = 0
  var gridY: Int = 0
  var dir: Dir = Dir.North
  
  def moveForward() = {
    if (stage.getWall(gridX, gridY, dir).isEmpty) {
      gridX += dir.xMul
      gridY += dir.yMul
    }
  }
  
  def moveBackward() = {
    if (stage.getWall(gridX, gridY, dir.opposite).isEmpty) {
      gridX -= dir.xMul
      gridY -= dir.yMul
    }
  }
  
  def turnLeft() = dir = dir.antiClockwise
  def turnRight() = dir = dir.clockwise
  
  def toggleWall() = {
    if (stage.getWall(gridX, gridY, dir).isDefined)
      stage.removeWall(gridX, gridY, dir)
    else
      stage.addWall(gridX, gridY, dir)
  }
  
  /**Rotates a small amount towards desired direction according to deltaTime*/
  def rotateTowardsDesired(deltaTime: Double) = {
    val normRot = rot % (2*math.Pi)
    var rotArc = dir.rot - normRot 
    val rotAmount = Player.TurningSpeed*deltaTime
    
    if (rotArc > math.Pi) {
      rotArc -= 2*math.Pi
    } else if (rotArc < -math.Pi) {
      rotArc += 2*math.Pi
    }
    if (rotAmount < math.abs(rotArc)) {
      if(rotArc < 0) {
        rot -= rotAmount
      } else {
        rot += rotAmount
      }
    } else {
      rot = dir.rot
      rotating = false
    }
  }
  
  /**Moves a small amount towards desired location according to deltaTime*/
  def moveTowardsDesired(deltaTime: Double) = {
    val dest = stage.position(gridX, gridY)
    val xDist = pos.x - dest.x
    val yDist = pos.y - dest.y
    val moveDist = deltaTime * Player.MovementSpeed
    
    if (moveDist < math.abs(xDist)) {
      pos.x += moveDist * -math.signum(xDist)
    }
    if (moveDist < math.abs(yDist)) {
      pos.y += moveDist * -math.signum(yDist)
    }
    
    if (moveDist > math.abs(xDist) && moveDist > math.abs(yDist)) {
      pos.x = dest.x
      pos.y = dest.y
      moving = false
    }
  }
  
}

object Player {
  val Height = 1.0
  val MinView = 0.25
  val PixelMultiplier = 1800
  val MovementSpeed = 2.0  // m/s
  val TurningSpeed = 3.0   // rad/s
}
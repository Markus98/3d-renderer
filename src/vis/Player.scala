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
    //println(s"rotArc: ${rotArc/(2*math.Pi)},\t normRot: ${normRot/(2*math.Pi)},\t rotAmount: ${rotAmount/(2*math.Pi)},\t playerRot: ${rot/(2*math.Pi)}")
    if (rotAmount < math.abs(rotArc)) {
      if(rotArc < 0) {
        rot -= rotAmount
      } else {
        rot += rotAmount
      }
    } else {
      rot = dir.rot
      println(rot % (2*math.Pi))
      rotating = false
    }
  }
  
  def moveTowardsDesired(deltaTime: Double) = {
    
  }
  
}

object Player {
  val Height = 1.0
  val MinView = 0.25
  val PixelMultiplier = 1800
  val MovementSpeed = 1.0
  val TurningSpeed = 2.0
}
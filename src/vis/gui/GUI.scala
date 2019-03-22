package vis.gui

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Polygon
import scalafx.animation.AnimationTimer
import scala.collection.mutable.Buffer
import vis._
import scala.math

object GUI extends JFXApp {
  var game = new Game()
  game.player.rot = 0
  game.player.pos.y = 0
  
  game.stage.addWall(0, 2, Dir.East)
  game.stage.addWall(0, 2, Dir.West)
  
  game.stage.addWall(0, 3, Dir.East)
  game.stage.addWall(0, 4, Dir.East)
  game.stage.addWall(0, 5, Dir.East)
  game.stage.addWall(0, 6, Dir.East)
  game.stage.addWall(0, 7, Dir.East)
  game.stage.addWall(0, 8, Dir.East)
  
  game.stage.addWall(0, 3, Dir.West)
  game.stage.addWall(0, 4, Dir.West)
  game.stage.addWall(0, 5, Dir.West)
  game.stage.addWall(0, 6, Dir.West)
  game.stage.addWall(0, 7, Dir.West)
  game.stage.addWall(0, 8, Dir.West)
  
  game.stage.addWall(0, 8, Dir.North)
  
  
  game.stage.addWall(1, 1, Dir.North)
  game.stage.addWall(1, 1, Dir.East)
  game.stage.addWall(-9, 9, Dir.East)
  
  stage = new JFXApp.PrimaryStage {
    title.value = "3D Visualizator"
    val w = 1280
    val h = 720
    width = w
    height = h
    scene = new Scene {
      fill = White
      
      var prevTime: Long = 0
      val timer = AnimationTimer(t => {
        val deltaTime = (t - prevTime)/1000000000.0
        prevTime = t
        
        testRot(deltaTime)
        content = game.stage.getWalls.sortBy(game.getRelEntity(_).pos.origDist).reverse.map(drawEntity)
      })
      timer.start
    }
    
    def testRot(x: Double) = {
      game.player.rot += x
    }
    
    def drawEntity(ent: Entity): Polygon = {
      val rel = game.getRelEntity(ent)
      val drawPoints = Buffer[java.lang.Double]()
      
      val positions = rel.vertexPositions

      // this for loop calculates and adds the points where the object intersects with the clipping plane
      val tempPositions = positions.zipWithIndex
      for (pos <- tempPositions.filter(_._1.y < Player.MinView)) {
        val p0 = pos._1
        // get the verticies that are connected to said vertex.
        val connection1 = if (pos == tempPositions.head) tempPositions.last._1 else tempPositions(pos._2 - 1)._1
        val connection2 = if (pos == tempPositions.last) tempPositions.head._1 else tempPositions(pos._2 + 1)._1
        // check which connections intersect the clipping plane.
        val projectionPoints = Vector(connection2, connection1).filter(_.y > Player.MinView)
        // calculate new intersection points
        val newPoints = projectionPoints.map( p1 => {
          val t = (p0.y-Player.MinView)/(p0.y-p1.y)
          new Pos(
              (p1.x-p0.x)*t+p0.x,
              Player.MinView,
              (p1.z-p0.z)*t+p0.z)
        })
        positions -= p0
        newPoints.foreach(positions.insert(pos._2,_))
      }
      
      
      def calcPixel(p: Pos) = {
        val yDist = if (p.y <= Player.MinView) Player.MinView + 0.01 else p.y 
        val x = w/2.0 + (p.x/yDist*Player.MinView) * Player.PixelMultiplier
        val y = h/2.0 + ((Player.Height - p.z)/yDist*Player.MinView) * Player.PixelMultiplier
        (x,y)
      }
      
      for (point <- positions.map(calcPixel)) {
        drawPoints += point._1
        drawPoints += point._2
      }
      
      val col = (255*math.pow(1.2, -rel.pos.origDist)).toInt
      new Polygon() {
        points ++= drawPoints
        fill = rgb(col, 100, 100)
        stroke = Black
      }
    }
  }
}
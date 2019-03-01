package vis.gui

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Polygon
import scala.collection.mutable.Buffer
import vis._
import scala.math

object GUI extends JFXApp {
  var game = new Game()
  game.player.rot = 0
  game.player.pos.y = 4
  //game.stage.addWall(0, 0, Dir.East)
  
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
  game.stage.addWall(-5, 5, Dir.East)
  
  stage = new JFXApp.PrimaryStage {
    title.value = "3D Visualizator"
    val w = 1280
    val h = 720
    width = w
    height = h
    scene = new Scene {
      fill = White
      content = game.stage.getWalls.filter(game.getRelEntity(_).pos.y >= -0.5).map(drawEntity)
    }
    
    def drawEntity(ent: Entity): Polygon = {
      val rel = game.getRelEntity(ent)
      val drawPoints = Buffer[java.lang.Double]()
      
      def calcPixel(p: Pos) = {
        val yDist = if (p.y <= 0) 0.00001 else p.y 
        val x = w/2.0 + (p.x/yDist*Player.MinView) * Player.PixelMultiplier
        val y = h/2.0 + ((Player.Height - p.z)/yDist*Player.MinView) * Player.PixelMultiplier
        (x,y)
      }
      for (point <- rel.vertexPositions.map(calcPixel)) {
        drawPoints += point._1
        drawPoints += point._2
      }
      val col = (255*math.pow(1.2, -rel.pos.origDist)).toInt
      new Polygon() {
        points ++= drawPoints
        fill = rgb(col, col, col)
        stroke = Black
      }
    }
  }
}
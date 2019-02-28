package vis.gui

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Polygon
import scala.collection.mutable.Buffer
import vis._

object GUI extends JFXApp {
  var game = new Game()
  game.player.rot = 0
  game.stage.addWall(0, 2, Dir.East)
  game.stage.addWall(0, 2, Dir.West)
  game.stage.addWall(0, 2, Dir.North)
  game.stage.addWall(1, 1, Dir.North)
  game.stage.addWall(1, 1, Dir.East)
  
  stage = new JFXApp.PrimaryStage {
    title.value = "3D Visualizator"
    val w = 1280
    val h = 720
    width = w
    height = h
    scene = new Scene {
      fill = White
      content = game.stage.getWalls.map(drawEntity)
    }
    
    def drawEntity(ent: Entity): Polygon = {
      val rel = game.getRelEntity(ent)
      val drawPoints = Buffer[java.lang.Double]()
      
      def calcPixel(p: Pos) = {
        val multiplier = 2000
        val x = w/2.0 + (p.x/p.y*Player.MinView) * multiplier
        val y = h/2.0 + ((Player.Height - p.z)/p.y*Player.MinView) * multiplier
        (x,y)
      }
      for (point <- rel.vertexPositions.map(calcPixel)) {
        drawPoints += point._1
        drawPoints += point._2
      }
      val col = 240 - (rel.pos.dist(Pos(0,0,0))*35).toInt
      new Polygon() {
        points ++= drawPoints
        fill = rgb(col, col, col)
        stroke = Black
      }
    }
  }
}
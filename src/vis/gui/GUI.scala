package vis.gui

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Polygon
import scala.Vector

class GUI extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title.value = "Hello Stage"
    width = 600
    height = 450
    scene = new Scene {
      fill = LightGreen
      
      content = new Polygon {
        points ++= Vector(10, 10, 100, 100, 23, 45)
        fill = Green
        stroke = Black
      }
        
      //Polygon(10,10,30,10,10,30,30,30)
      
      /*content = new Rectangle {
        x = 25
        y = 40
        width = 100
        height = 100
        fill <== when(hover) choose Green otherwise Red
      }*/
    }
  }
}
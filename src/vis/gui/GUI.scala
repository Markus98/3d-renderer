package vis.gui

import java.io.File

import scala.collection.mutable.Buffer

import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.Includes.jfxActionEvent2sfx
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.Includes.jfxWindowEvent2sfx
import scalafx.Includes.observableList2ObservableBuffer
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.control.Label
import scalafx.scene.control.TextField
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color.Black
import scalafx.scene.paint.Color.White
import scalafx.scene.paint.Color.rgb
import scalafx.scene.shape.Polygon
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.Modality
import scalafx.stage.WindowEvent
import scalafx.stage.Stage
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Alert

import vis.{Entity, Player, Pos}
import vis.gen.GameGenerator
import vis.gen.CorruptedGameFileExeption
import vis.gen.CorruptedGameFileExeption

object GUI extends JFXApp {
  var game = GameGenerator.genGame(10,10,12)
  
  // define stuff for handling saving
  val saveDirectory = new File("./saves")
  if (!saveDirectory.exists())
    saveDirectory.mkdirs()
  val extensionFilter = new ExtensionFilter("Stage files", "*.stge")
  
  /**A window that opens when ESC is pressed. Contains save, load and stage generation.*/
  val menuWindow: Stage = new Stage {
    title.value = "Menu"
    resizable = false
    scene = new Scene(300, 150) {
      
      onKeyPressed = (e: KeyEvent) => if (e.code == KeyCode.ESCAPE) menuWindow.close()
      onShown = (e: WindowEvent) => errorField.text = ""
          
      val saveBtn = new Button("Save stage...")
      saveBtn.onAction = (e:ActionEvent) => {
        val chooser = new FileChooser {
          title = "Save Stage File"
          initialFileName = "stage.stge"
          initialDirectory = saveDirectory
          extensionFilters += extensionFilter
        }
        val selectedFile = chooser.showSaveDialog(menuWindow)
        if (selectedFile != null) {
          GameGenerator.saveGame(game, selectedFile)
          new Alert(AlertType.Information, "Game saved successfully.").showAndWait()
        }
      }
      
      val loadBtn = new Button("Load stage...")
      loadBtn.onAction = (e:ActionEvent) => {
        val chooser = new FileChooser {
          title = "Load Stage File"
          initialDirectory = saveDirectory
          extensionFilters += extensionFilter
        }
        val selectedFile = chooser.showOpenDialog(menuWindow)
        if (selectedFile != null) {
          try {
            game = GameGenerator.loadGame(selectedFile)
            menuWindow.close() 
          } catch {
            case e: CorruptedGameFileExeption =>
              new Alert(AlertType.Error, "Corrupted game file.").showAndWait()
          }
        }
      }
      
      val widthField = new TextField {text = "10"}
      val heightField = new TextField {text = "10"}
      val randomAmountField = new TextField {text = "0"}
      val errorField = new Label
      
      val generateBtn = new Button("Generate Stage")
      generateBtn.onAction = (e:ActionEvent) => {
        try {
          game = GameGenerator.genGame(widthField.text.value.toInt, heightField.text.value.toInt, randomAmountField.text.value.toInt)
          errorField.text = ""
        } catch {
          case e : Throwable => errorField.text = " Invalid parameters."
        }
      }
      
      val grid = new GridPane()
      grid.add(saveBtn, 0, 0)
      grid.add(loadBtn, 0, 1)
      grid.add(new Label("Width: "), 0, 3)
      grid.add(widthField, 1, 3)
      grid.add(new Label("Height: "), 0, 4)
      grid.add(heightField, 1, 4)
      grid.add(new Label("Random walls: "), 0, 5)
      grid.add(randomAmountField, 1, 5)
      grid.add(generateBtn, 0, 6)
      grid.add(errorField, 1, 6)
      
      content = grid
    }
    initOwner(stage)
    initModality(Modality.APPLICATION_MODAL)
  }
  
  stage = new JFXApp.PrimaryStage {
    title.value = "3D Visualizator"
    val w = 1280
    val h = 720
    width = w
    height = h + 31
    resizable = false
    
    scene = new Scene {
      fill = White
      
      onKeyPressed = (e: KeyEvent) => {
        if (!game.player.moving) {
          if (e.code == KeyCode.UP) {
            game.player.moveForward()
          } else if (e.code == KeyCode.DOWN) {
            game.player.moveBackward()
          }
          game.player.moving = true
        }
        if (!game.player.rotating) {
          if (e.code == KeyCode.LEFT) {
            game.player.turnLeft()
          } else if (e.code == KeyCode.RIGHT) {
            game.player.turnRight()
          }
          game.player.rotating = true
        }
        
        if (e.code == KeyCode.ESCAPE) {
          menuWindow.show()
        }
      }
      
      var prevTime: Long = 0
      /**An animation timer that handles updating the frames.*/
      val timer = AnimationTimer(t => {
        val deltaTime = (t - prevTime)/1000000000.0
        prevTime = t
        game.gameTick(deltaTime)
        content = game.stage.getWalls.sortBy(game.getRelEntity(_).pos.origDist).reverse.map(drawEntity)
      })
      timer.start
    }
    
    /**Returns a drawn polygon for an entity, given the current situation in Game object.*/
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
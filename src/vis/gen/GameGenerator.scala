package vis.gen

import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.Reader

import scala.collection.mutable.Buffer
import scala.util.Random

import vis.Dir
import vis.Game

object GameGenerator {
  /**Generates a game with a rectangle stage and random walls inside it specified by randomWalls.*/
  def genGame(width: Int, height: Int, randomWalls: Int): Game = {
    if (width < 1 || height < 1)
      throw new IllegalArgumentException("Width and height need to be at least 1.")
    if (randomWalls > (width-1)*height + (height-1)*width || randomWalls < 0)
      throw new IllegalArgumentException("Invalid amount of randomWalls.")
    
    val game = new Game()
    
    for (i <- 0 until width) {
      game.stage.addWall(i, 0, Dir.South)
      game.stage.addWall(i, height-1, Dir.North)
    }
    for (i <- 0 until height) {
      game.stage.addWall(0, i, Dir.West)
      game.stage.addWall(width-1, i, Dir.East)
    }
    
    // calculate all possible wall positions
    val horPositions = Buffer.tabulate((height-1)*width)(x => (x%width,x/width))
    val verPositions = Buffer.tabulate((width-1)*height)(x => (x%height,x/height))
    
    // add the walls to the game randomly
    val horAmount = randomWalls / 2
    Random.shuffle(horPositions).take(horAmount).foreach(pos => game.stage.addWall(pos._1, pos._2, Dir.North))
    Random.shuffle(verPositions).take(randomWalls - horAmount).foreach(pos => game.stage.addWall(pos._1, pos._2, Dir.East))
    
    game
  }
  
  /**Saves the current Game's Stage to a file.*/
  def saveGame(game: Game, file: File) {
    val writer = new FileWriter(file)
    val bufWriter = new BufferedWriter(writer)
    
    val walls = game.stage.getWalls
    
    for (wall <- walls) {
      wall.dir match {
        case Dir.North =>
          bufWriter.write("N")
        case Dir.East =>
          bufWriter.write("E")
        case Dir.South =>
          bufWriter.write("S")
        case Dir.West =>
          bufWriter.write("W")
      }
      bufWriter.write(s"${wall.x},${wall.y}")
    }
    
    bufWriter.flush()
    bufWriter.close()
    writer.close()
  }
  
  /**Loads a Game's Stage and returns it as a Game object.*/
  def loadGame(file: File): Game = {
    val game = new Game()
    val reader = new FileReader(file)
    
    var readChar = reader.read()
    while (readChar != -1) {
      val dir = readChar.toChar match {
        case 'N' => Dir.North
        case 'E' => Dir.East
        case 'S' => Dir.South
        case 'W' => Dir.West
        case char => throw new CorruptedGameFileExeption(s"Wrong direction prefix '$char'.")
      }
      val coords = try {
        readCoordinates(reader)
      } catch {
        case e: NumberFormatException => 
          throw new CorruptedGameFileExeption("Error reading coordinates.")
      }
      game.stage.addWall(coords._1, coords._2, dir)
      readChar = coords._3
    }
    reader.close()
    game
  }
  
  private def readCoordinates(reader: Reader): (Int, Int, Int) = {
    var nextPrefix = 0
    def readNumber(): Int = {
      val buffer = Buffer[Char]()
      var readChar = reader.read()
      while (readChar != -1 && readChar.toChar.isDigit) {
        buffer.append(readChar.toChar)
        readChar = reader.read()
      }
      if (readChar != ',') nextPrefix = readChar
      buffer.mkString.toInt
    }
    // (x, y, nextPrefix)
    (readNumber(),readNumber(), nextPrefix)
  }
}

class CorruptedGameFileExeption(desc: String) extends IOException(desc)
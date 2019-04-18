package vis.gen

import vis._
import scala.collection.mutable.Buffer
import scala.util.Random
import java.io._

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
  
  def saveGame(game: Game, file: File) {
    println(file.createNewFile())
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
  
  
  private def readFully(result: Array[Char], input: Reader) = {
      var cursor = 0

      while (cursor != result.length) {
        var numCharactersRead = input.read(result, cursor, result.length - cursor)

        if (numCharactersRead == -1) {
          throw new IOException("Unexpected end of file.")
        }

        cursor += numCharactersRead
      }

    }
}
package vis.gen

import vis._
import scala.collection.mutable.Buffer
import scala.util.Random

object GameGenerator {
  def genGame(width: Int, height: Int, randomWalls: Int): Game = {
    if (randomWalls > (width-1)*height + (height-1)*width || randomWalls < 0) {
      throw new IllegalArgumentException("Invalid amount of randomWalls.")
    }
    val game = new Game()
    
    for (i <- 0 until width) {
      game.stage.addWall(i, 0, Dir.South)
      game.stage.addWall(i, height-1, Dir.North)
    }
    
    for (i <- 0 until width) {
      game.stage.addWall(0, i, Dir.West)
      game.stage.addWall(width-1, i, Dir.East)
    }
    
    val horWalls = Buffer.tabulate((height-1)*width)(x => (x%(width-1),x/(width-1)))
    val verWalls = Buffer.tabulate((width-1)*height)(x => (x%height,x/height))
    
    val horAmount = randomWalls / 2
    Random.shuffle(horWalls).take(horAmount).foreach(pos => game.stage.addWall(pos._1, pos._2, Dir.North))
    Random.shuffle(verWalls).take(randomWalls - horAmount).foreach(pos => game.stage.addWall(pos._1, pos._2, Dir.East))
    
    game
  }
}
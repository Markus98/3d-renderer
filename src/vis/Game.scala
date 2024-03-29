package vis

import scala.collection.mutable.Buffer

class Game {
  val stage = new Stage()
  val player = new Player(stage)
  
  /**Returns a new instance of a given entity whose position and rotation have been made
   * relative to the player's position and rotation.*/
  def getRelEntity(ent: Entity): Entity = {
    val relEntity = new Entity(player.pos.relPos(ent.pos).rotated(-player.rot), ent.rot-player.rot)
    relEntity.verticies ++= ent.verticies
    relEntity
  }
  
  def gameTick(dt: Double) {
    if (player.rotating)
      player.rotateTowardsDesired(dt)
    if (player.moving)
      player.moveTowardsDesired(dt)
  }
}
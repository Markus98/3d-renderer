package vis

import scala.collection.mutable.Buffer

class Game {
  val player = new Player()
  val stage = new Stage()
  val entities = Buffer[Entity]()
  
  /**Returns a new instance of a given entity whose position and rotation have been made
   * relative to the player's position and rotation.*/
  def getRelEntity(ent: Entity): Entity = {
    val relEntity = new Entity(player.pos.relPos(ent.pos).rotated(-player.rot), -player.rot)
    relEntity.verticies ++= ent.verticies
    relEntity
  }
  
}
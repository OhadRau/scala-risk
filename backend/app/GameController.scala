import scala.collection.mutable.ListBuffer

class GameController(val PlayerCount: Integer)  {
  var Players: ListBuffer[Player] = ListBuffer[Player]();

  for ( i <- 1 to PlayerCount)  {
    Players += new Player(i+"");
  }

}

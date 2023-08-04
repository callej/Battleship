package battleship

fun placeShip(game: BattleShip , ship: Ships, coordString: String): Boolean {
    val (first, second) = coordString.trim().split(Regex("\\s+")).map { it.toCoord() }
    val correctPlacement = game.addShip(Ship(ship, Location(first, second)))
    if (!correctPlacement.valid) {
        println("\nError! ${correctPlacement.info} Try again:\n")
        return false
    }
    return true
}

fun initPlayer(player: Player) {
    println("${player.name}, place your ships to the game field")
    println("\n${player.gameField}")
    for (ship in Ships.values()) {
        println("\nEnter the coordinates of the ${ship.str} (${ship.size} cells):\n")
        while (!placeShip(player.gameField, ship, readln())) {}
        println("\n${player.gameField}")
    }
    print("\nPress Enter and pass the move to another player\n...").run { readln() }
}

fun main() {
    val players = listOf(Player("Player 1", BattleShip()), Player("Player 2", BattleShip()))
    players.forEach { initPlayer(it) }
    var turn = players.lastIndex
    while (true) {
        turn = (turn + 1) % players.size
        println("\n${players[(turn + 1) % players.size].gameField.foggy()}")
        println(players[(turn + 1) % players.size].gameField.divider())
        println(players[turn].gameField)
        var validShot = println("\n${players[turn].name}, it's your turn:\n").run { players[(turn + 1) % players.size].gameField.shot(readln().toCoord()) }
        while (!validShot.valid) {
            validShot = println("\nError! ${validShot.info} Try again:\n").run { players[(turn + 1) % players.size].gameField.shot(readln().toCoord()) }
        }
        if (!players[(turn + 1) % players.size].gameField.shipsRemain()) break
        println("\n${validShot.info}")
        print("Press Enter and pass the move to another player\n...").run { readln() }
    }
    println("\nCongratulations! ${players[turn].name}, You Won!")
}
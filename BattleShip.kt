package battleship

const val ROWS = 10
const val COLS = 10

fun String.toCoord() = Coordinate(this.drop(1).toInt() - 1, this.first().code - 'A'.code)

enum class Ships(val str: String, val size: Int) {
    AIRCRAFT_CARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);
}

data class Player(val name: String, val gameField: BattleShip)

data class Validation(val valid: Boolean, val info: String = "")

data class Coordinate(val x: Int, val y: Int)

data class Location(private val first: Coordinate, private val second: Coordinate) {
    val start = Coordinate(minOf(first.x, second.x), minOf(first.y, second.y))
    val end = Coordinate(maxOf(first.x, second.x), maxOf(first.y, second.y))
}

class Ship(private val ship: Ships, val loc: Location) {
    fun validShip(): Validation {
        if ((this.loc.start.x == this.loc.end.x && (this.loc.end.y - this.loc.start.y + 1) != ship.size) ||
            (this.loc.start.y == this.loc.end.y && (this.loc.end.x - this.loc.start.x + 1) != ship.size)) {
            return Validation(false, "Wrong length of the ${this.ship.str}!")
        }
        if (this.loc.start.x != this.loc.end.x && this.loc.start.y != this.loc.end.y) {
            return Validation(false, "Wrong ship location!")
        }
        return Validation(true)
    }
}

class BattleShip {
    private val gameField = List(COLS) { List(ROWS) { '~' }.toMutableList() }
    private val shipLocations = List(COLS) { List<Ship?>(ROWS) { null }.toMutableList() }
    private val ships = mutableListOf<Ship>()

    private fun validCoordinate(c: Coordinate) = c.x in gameField.indices && c.y in gameField[1].indices

    private fun validLocation(ship: Ship): Validation {
        if (!validCoordinate(ship.loc.start) || !validCoordinate(ship.loc.end)) {
            return Validation(false, "Location outside of the game board!")
        }
        val checkShip = ship.validShip()
        if (!checkShip.valid) return checkShip
        for (y in maxOf(0, ship.loc.start.y - 1)..minOf(this.gameField[1].lastIndex, ship.loc.end.y + 1)) {
            for (x in maxOf(0, ship.loc.start.x - 1)..minOf(this.gameField.lastIndex, ship.loc.end.x + 1)) {
                if (gameField[x][y] != '~') {
                    return Validation(false, "You placed it too close to another one.")
                }
            }
        }
        return Validation(true)
    }

    fun addShip(ship: Ship): Validation {
        val correctPlacement = validLocation(ship)
        if (!correctPlacement.valid) return correctPlacement
        for (y in ship.loc.start.y..ship.loc.end.y) {
            for (x in ship.loc.start.x..ship.loc.end.x) {
                gameField[x][y] = 'O'
                shipLocations[x][y] = ship
            }
        }
        this.ships.add(ship)
        return Validation(true)
    }

    private fun hitInfo(target: Coordinate): String {
        val shipStatus = mutableListOf<Char>()
        val ship = shipLocations[target.x][target.y]
        if (ship != null) {
            for (y in ship.loc.start.y..ship.loc.end.y) {
                for (x in ship.loc.start.x..ship.loc.end.x) {
                    shipStatus.add(this.gameField[x][y])
                }
            }
        }
        return if (shipStatus.all { it == 'X' }) {
            "You sank a ship!"
        } else {
            "You hit a ship!"
        }
    }

    fun shot(target: Coordinate): Validation {
        if (!validCoordinate(target)) return Validation(false, "You entered the wrong coordinates!")
        when (gameField[target.x][target.y]) {
            'O' -> { gameField[target.x][target.y] = 'X'; return Validation(true, hitInfo(target)) }
            '~' -> { gameField[target.x][target.y] = 'M'; return Validation(true,"You missed!") }
            'X', 'M' -> return Validation(true, "You have already made a shot here.")
            else -> return Validation(true)
        }
    }

    fun shipsRemain() = gameField.any { it.any { it == 'O' } }

    fun divider() = "-".repeat(2 * gameField.size + 2)

    fun foggy(): String {
        val str = StringBuilder("   ${(1..gameField.size).toList().joinToString(" ")}")
        repeat(gameField[1].size) { row -> str.append("\n${(row + 'A'.code).toChar()} ")
            repeat(gameField.size) { col -> str.append(if (gameField[col][row] == 'O') " ~" else " ${gameField[col][row]}") } }
        return str.toString()
    }

    override fun toString(): String {
        val str = StringBuilder("   ${(1..gameField.size).toList().joinToString(" ")}")
        repeat(gameField[1].size) { row -> str.append("\n${(row + 'A'.code).toChar()} ")
            repeat(gameField.size) { col -> str.append(" ${gameField[col][row]}") } }
        return str.toString()
    }
}
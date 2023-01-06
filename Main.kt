package minesweeper
import java.lang.IndexOutOfBoundsException
import kotlin.random.Random
fun main() {
    val game = Minesweeper()
    game.playGame()
}
enum  class Symbols(val value: Char) {
    MINE('X'),
    VOID('.'),
    SET('*'),
    FREE('/')
}
class Minesweeper(){
    var noMines = 0
    var safeCells = 0
    var safeCellsFound = 0
    var cmdCount = 0
    var memoryField = createField()
    var gamingField = MutableList(9){MutableList(9){Symbols.VOID.value} }
    val visited = MutableList(9){ MutableList(9) {false}}
    fun createField(): MutableList<MutableList<Char>> {
        println("How many mines do you want on the field?")
        noMines = readln().toInt()
        safeCells = 81 - noMines
        var field = MutableList(9){ MutableList(9) {Symbols.VOID.value}}
        field = createBombs(field, noMines)
        return field
    }
    fun printField(field: MutableList<MutableList<Char>>){
        println(" │123456789│\n—│—————————│")
        field.forEachIndexed{i, el -> println("${i + 1}|${el.joinToString("")}|") }
        println("—│—————————│")
    }
    fun playGame() {
        var minesFound = 0
        printField(gamingField)
        while(true) {
            if(noMines == minesFound || safeCells == safeCellsFound)  {
                println("Congratulations! You found all the mines!")
                return
            }
            println("Set/unset mines marks or claim a cell as free:")
            val userInput = readln()
            val(x,y) = userInput.split(" ").subList(0,2).map { it.toInt() - 1 }
            val option = userInput.split(" ")[2]
            when(option) {
                "mine" -> {
                    if(isSet(gamingField[y][x]))  {
                        gamingField[y][x] = Symbols.VOID.value
                        printField(gamingField)
                        continue
                    }
                    if(isMine(memoryField[y][x])) minesFound++
                    cmdCount++
                    gamingField[y][x] = Symbols.SET.value
                }
                "free" -> {
                    if(cmdCount == 0 && isMine(memoryField[y][x])) {
                            cmdCount++
                            var field = MutableList(9){ MutableList(9) {Symbols.VOID.value}}
                            memoryField = createBombs(field, noMines)
                        }
                    if(isMine(memoryField[y][x])) {
                        memoryField.forEachIndexed{i, el -> el.forEachIndexed{j, it -> if(it == Symbols.MINE.value) gamingField[i][j] = it}}
                        printField(gamingField)
                        println("You stepped on a mine and failed!")
                        return
                    } else {
                        cmdCount++
                        floodFill(x, y)
                    }
                }
            }
            printField(gamingField)
        }
    }
    fun floodFill(x: Int, y: Int, ) {
        if(!isValid(x, y)) return
        if(memoryField[y][x] == Symbols.VOID.value && !visited[x][y]){
            safeCellsFound++
            gamingField[y][x] = Symbols.FREE.value
            visited[x][y] = true
            for(i in -1..1){
                for(j in -1..1) {
                    if( i == 0 && j ==0) continue
                    floodFill(x + j, y + i)
                }
            }
        } else if(memoryField[y][x].isDigit()) {
            safeCellsFound++
            gamingField[y][x] = memoryField[y][x]
        }
}
    fun isValid(x: Int, y: Int, ) : Boolean {
        if( x > 8 || x < 0 || y > 8 || y < 0 ) return false
        return true
    }
    fun createBombs(field: MutableList<MutableList<Char>>, noMines: Int ) : MutableList<MutableList<Char>>{
        var minesCreated = 0
        while(minesCreated < noMines) {
            val x = Random.nextInt(0,9)
            val y = Random.nextInt(0,9)
            if(field[y][x] != Symbols.MINE.value) {
                field[y][x] = Symbols.MINE.value
                minesCreated++
                for (i in -1..1) {
                    try {
                        for (j in -1..1) {
                            try {
                                if (field[y + i][x + j] == Symbols.VOID.value) field[y + i][x + j] = '1'
                                else if (field[y + i][x + j].isDigit()) field[y + i][x + j] = field[y + i][x + j].nextChar()
                            } catch (e: IndexOutOfBoundsException) {
                                continue
                            }
                        }
                    } catch(e: IndexOutOfBoundsException) {
                        continue
                    }
                }
            }
            else continue
        }
        return field
    }
    fun isMine(value: Char) : Boolean = value == Symbols.MINE.value
    fun isSet(value: Char) : Boolean = value == Symbols.SET.value
}
fun Char.nextChar() = (this.code + 1).toChar()
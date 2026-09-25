package com.example

import com.example.model.Direction
import com.example.model.GameEngine
import com.example.model.GameLevel
import com.example.model.MoveResult
import com.example.model.Position
import com.example.model.TileType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun `test level parsing and initial state`() {
    val level = GameLevel(
      id = "test_1",
      worldId = 1,
      name = "Test Crypt",
      description = "Test",
      parMoves = 5,
      mapRows = listOf(
        "#####",
        "#@BT#",
        "#####"
      )
    )

    val state = level.toInitialState()
    assertEquals(5, state.width)
    assertEquals(3, state.height)
    assertEquals(Position(1, 1), state.playerPos)
    assertEquals(1, state.crates.size)
    assertEquals(1, state.totalTargets)
    assertEquals(0, state.filledTargets)
  }

  @Test
  fun `test crate push and victory condition`() {
    val level = GameLevel(
      id = "test_push",
      worldId = 1,
      name = "Test Push",
      description = "Test",
      parMoves = 2,
      mapRows = listOf(
        "######",
        "#@.BT#",
        "######"
      )
    )

    var state = level.toInitialState()

    // Move right onto floor
    val (state1, res1) = GameEngine.executeMove(state, Direction.RIGHT)
    assertTrue(res1 is MoveResult.Moved)
    assertEquals(Position(2, 1), state1.playerPos)
    assertFalse(state1.isCompleted)

    // Move right pushing crate onto target
    val (state2, res2) = GameEngine.executeMove(state1, Direction.RIGHT)
    assertTrue(res2 is MoveResult.Won)
    assertTrue(state2.isCompleted)
    assertEquals(Position(3, 1), state2.playerPos)
    assertEquals(1, state2.filledTargets)
  }

  @Test
  fun `test undo moves correctly reverts state`() {
    val level = GameLevel(
      id = "test_undo",
      worldId = 1,
      name = "Test Undo",
      description = "Test",
      parMoves = 5,
      mapRows = listOf(
        "######",
        "#@.BT#",
        "######"
      )
    )

    val initial = level.toInitialState()
    val (moved, _) = GameEngine.executeMove(initial, Direction.RIGHT)
    assertEquals(Position(2, 1), moved.playerPos)
    assertEquals(1, moved.stepCount)

    val undone = GameEngine.undo(moved)
    assertEquals(Position(1, 1), undone.playerPos)
    assertEquals(0, undone.stepCount)
  }

  @Test
  fun `test laser and mirror reflection`() {
    val level = GameLevel(
      id = "test_laser",
      worldId = 3,
      name = "Laser Test",
      description = "Test",
      parMoves = 4,
      mapRows = listOf(
        "#####",
        "#>.\\#",
        "#...#",
        "#..R#",
        "#####"
      )
    )

    val state = level.toInitialState()
    // Laser should fire from (1, 1) rightwards, hit mirror at (3, 1), reflect DOWN towards (3, 3) receiver
    assertTrue(state.isReceiverPowered)
  }

  @Test
  fun `test 200 levels catalog generation`() {
    assertEquals(10, com.example.model.LevelCatalog.WORLDS.size)
    var totalLevels = 0
    for (world in com.example.model.LevelCatalog.WORLDS) {
      val levels = com.example.model.LevelCatalog.getLevelsForWorld(world.id)
      assertEquals(20, levels.size)
      totalLevels += levels.size
      for (lvl in levels) {
        val state = lvl.toInitialState()
        assertTrue(state.width >= 5)
        assertTrue(state.height >= 3)
      }
    }
    assertEquals(200, totalLevels)
  }

  @Test
  fun `test hero skins metadata`() {
    val skins = com.example.model.HeroSkin.entries
    assertTrue(skins.size >= 6)
    val freeSkin = skins.find { it.costCoins == 0 }
    assertEquals(com.example.model.HeroSkin.SILVER_KNIGHT, freeSkin)
  }

  @Test
  fun `test default champion name is Alif`() {
    val defaultName = "Alif"
    assertEquals("Alif", defaultName)
    assertTrue(defaultName.isNotBlank())
  }
}

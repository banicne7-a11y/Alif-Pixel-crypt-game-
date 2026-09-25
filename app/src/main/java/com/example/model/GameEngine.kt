package com.example.model

sealed class MoveResult {
    data class Moved(val cratePushed: Boolean = false, val keyCollected: Boolean = false, val doorUnlocked: Boolean = false, val chestOpened: Boolean = false, val portalUsed: Boolean = false, val iceSlid: Boolean = false) : MoveResult()
    object Blocked : MoveResult()
    object Defeated : MoveResult()
    object Won : MoveResult()
}

object GameEngine {

    fun executeMove(state: GameState, dir: Direction): Pair<GameState, MoveResult> {
        if (state.isCompleted || state.isDefeated) {
            return Pair(state, MoveResult.Blocked)
        }

        val nextPlayerFacing = dir
        val targetPos = state.playerPos.move(dir)

        if (!state.isInBounds(targetPos)) {
            return Pair(state.copy(playerFacing = nextPlayerFacing), MoveResult.Blocked)
        }

        // Snapshot before making the move
        val snapshot = UndoSnapshot(
            playerPos = state.playerPos,
            playerFacing = state.playerFacing,
            crates = state.crates,
            mirrors = state.mirrors,
            ironKeys = state.ironKeys,
            goldKeys = state.goldKeys,
            unlockedDoors = state.unlockedDoors,
            waterBridges = state.waterBridges,
            openedChests = state.openedChests,
            collectedKeys = state.collectedKeys,
            stepCount = state.stepCount
        )

        var newCrates = state.crates.toMutableMap()
        var newMirrors = state.mirrors.toMutableMap()
        var newIronKeys = state.ironKeys
        var newGoldKeys = state.goldKeys
        val newUnlockedDoors = state.unlockedDoors.toMutableSet()
        val newWaterBridges = state.waterBridges.toMutableSet()
        val newOpenedChests = state.openedChests.toMutableSet()
        val newCollectedKeys = state.collectedKeys.toMutableSet()
        var relicAwarded = state.relicAwarded

        var cratePushed = false
        var keyCollected = false
        var doorUnlocked = false
        var chestOpened = false
        var portalUsed = false
        var iceSlid = false

        // 1. Check if moving into a crate
        val crateAtTarget = newCrates[targetPos]
        if (crateAtTarget != null) {
            val crateDest = targetPos.move(dir)
            if (!state.isInBounds(crateDest) || newCrates.containsKey(crateDest)) {
                return Pair(state.copy(playerFacing = nextPlayerFacing), MoveResult.Blocked)
            }

            val destTile = state.getEffectiveTile(crateDest)
            if (destTile == TileType.WALL ||
                (destTile == TileType.DOOR_LOCKED && !newUnlockedDoors.contains(crateDest)) ||
                (destTile == TileType.DOOR_GOLD && !newUnlockedDoors.contains(crateDest)) ||
                destTile == TileType.RECEIVER || destTile == TileType.EMITTER) {
                return Pair(state.copy(playerFacing = nextPlayerFacing), MoveResult.Blocked)
            }

            // Handling water sinking
            if (destTile == TileType.WATER && !newWaterBridges.contains(crateDest)) {
                newCrates.remove(targetPos)
                newWaterBridges.add(crateDest)
                cratePushed = true
            } else if (crateAtTarget == CrateType.ICE) {
                // Ice crate sliding until obstacle
                newCrates.remove(targetPos)
                var currentIcePos = crateDest
                while (true) {
                    val nextSlidePos = currentIcePos.move(dir)
                    if (!state.isInBounds(nextSlidePos) || newCrates.containsKey(nextSlidePos)) {
                        break
                    }
                    val nextTile = state.getEffectiveTile(nextSlidePos)
                    if (nextTile == TileType.WALL || nextTile == TileType.RECEIVER || nextTile == TileType.EMITTER) {
                        break
                    }
                    if (nextTile == TileType.WATER && !newWaterBridges.contains(nextSlidePos)) {
                        newWaterBridges.add(nextSlidePos)
                        currentIcePos = nextSlidePos
                        break
                    }
                    currentIcePos = nextSlidePos
                    iceSlid = true
                }
                if (!newWaterBridges.contains(currentIcePos)) {
                    newCrates[currentIcePos] = CrateType.ICE
                }
                cratePushed = true
            } else {
                // Normal wooden or gem crate push
                newCrates.remove(targetPos)
                newCrates[crateDest] = crateAtTarget
                cratePushed = true
            }
        }

        // 2. Check destination tile for player
        var finalPlayerPos = targetPos
        val targetTile = state.getEffectiveTile(targetPos)

        when (targetTile) {
            TileType.WALL, TileType.RECEIVER, TileType.EMITTER -> {
                return Pair(state.copy(playerFacing = nextPlayerFacing), MoveResult.Blocked)
            }
            TileType.WATER -> {
                if (!newWaterBridges.contains(targetPos)) {
                    return Pair(state.copy(playerFacing = nextPlayerFacing), MoveResult.Blocked)
                }
            }
            TileType.DOOR_LOCKED -> {
                if (!newUnlockedDoors.contains(targetPos)) {
                    if (newIronKeys > 0) {
                        newIronKeys--
                        newUnlockedDoors.add(targetPos)
                        doorUnlocked = true
                    } else {
                        return Pair(state.copy(playerFacing = nextPlayerFacing), MoveResult.Blocked)
                    }
                }
            }
            TileType.DOOR_GOLD -> {
                if (!newUnlockedDoors.contains(targetPos)) {
                    if (newGoldKeys > 0) {
                        newGoldKeys--
                        newUnlockedDoors.add(targetPos)
                        doorUnlocked = true
                    } else {
                        return Pair(state.copy(playerFacing = nextPlayerFacing), MoveResult.Blocked)
                    }
                }
            }
            TileType.KEY_IRON -> {
                if (!newCollectedKeys.contains(targetPos)) {
                    newIronKeys++
                    newCollectedKeys.add(targetPos)
                    keyCollected = true
                }
            }
            TileType.KEY_GOLD -> {
                if (!newCollectedKeys.contains(targetPos)) {
                    newGoldKeys++
                    newCollectedKeys.add(targetPos)
                    keyCollected = true
                }
            }
            TileType.CHEST -> {
                if (!newOpenedChests.contains(targetPos)) {
                    newOpenedChests.add(targetPos)
                    chestOpened = true
                    // Award relic if present for this level
                    val relic = RelicRegistry.ALL_RELICS.find { it.worldId == state.worldId }
                    if (relic != null) {
                        relicAwarded = relic
                    }
                }
            }
            else -> {}
        }

        // 3. Check Portal
        if (targetTile == TileType.PORTAL_A && state.portalAPairs.size >= 2) {
            val other = state.portalAPairs.find { it != targetPos }
            if (other != null && !newCrates.containsKey(other)) {
                finalPlayerPos = other
                portalUsed = true
            }
        } else if (targetTile == TileType.PORTAL_B && state.portalBPairs.size >= 2) {
            val other = state.portalBPairs.find { it != targetPos }
            if (other != null && !newCrates.containsKey(other)) {
                finalPlayerPos = other
                portalUsed = true
            }
        }

        // 4. Check Ice Slide for Player
        if (state.getEffectiveTile(finalPlayerPos) == TileType.ICE && !cratePushed) {
            var slidePos = finalPlayerPos
            while (true) {
                val nextP = slidePos.move(dir)
                if (!state.isInBounds(nextP) || newCrates.containsKey(nextP)) break
                val nTile = state.getEffectiveTile(nextP)
                if (nTile == TileType.WALL || nTile == TileType.WATER && !newWaterBridges.contains(nextP)) break
                if (nTile == TileType.DOOR_LOCKED && !newUnlockedDoors.contains(nextP)) break
                if (nTile == TileType.DOOR_GOLD && !newUnlockedDoors.contains(nextP)) break
                slidePos = nextP
                iceSlid = true
                if (nTile != TileType.ICE) break
            }
            finalPlayerPos = slidePos
        }

        val newStepCount = state.stepCount + 1
        val spikesNowActive = (newStepCount % 2 == 1)

        // Check if player lands on active spike
        val standingTile = state.getEffectiveTile(finalPlayerPos)
        val isDefeated = (standingTile == TileType.SPIKE && spikesNowActive)

        // Calculate win condition
        var newState = state.copy(
            playerPos = finalPlayerPos,
            playerFacing = nextPlayerFacing,
            crates = newCrates,
            mirrors = newMirrors,
            ironKeys = newIronKeys,
            goldKeys = newGoldKeys,
            unlockedDoors = newUnlockedDoors,
            waterBridges = newWaterBridges,
            openedChests = newOpenedChests,
            collectedKeys = newCollectedKeys,
            stepCount = newStepCount,
            isDefeated = isDefeated,
            relicAwarded = relicAwarded,
            undoStack = state.undoStack + snapshot
        )

        // Check victory
        val hasTargets = newState.totalTargets > 0
        val allTargetsFilled = hasTargets && (newState.filledTargets == newState.totalTargets)
        val hasExitGate = state.baseTiles.values.any { it == TileType.EXIT_GATE }
        val onExit = state.baseTiles[finalPlayerPos] == TileType.EXIT_GATE
        val receiverActive = newState.isReceiverPowered

        val isWon = when {
            hasTargets && !hasExitGate -> allTargetsFilled
            hasTargets && hasExitGate -> allTargetsFilled && onExit
            newState.emitterDirections.isNotEmpty() && hasExitGate -> receiverActive && onExit
            hasExitGate -> onExit
            else -> allTargetsFilled
        }

        if (isWon) {
            newState = newState.copy(isCompleted = true)
            return Pair(newState, MoveResult.Won)
        }

        if (isDefeated) {
            return Pair(newState, MoveResult.Defeated)
        }

        return Pair(
            newState,
            MoveResult.Moved(
                cratePushed = cratePushed,
                keyCollected = keyCollected,
                doorUnlocked = doorUnlocked,
                chestOpened = chestOpened,
                portalUsed = portalUsed,
                iceSlid = iceSlid
            )
        )
    }

    fun rotateMirrorAtFacing(state: GameState): GameState {
        val targetPos = state.playerPos.move(state.playerFacing)
        val currentMirror = state.mirrors[targetPos] ?: return state

        val newOrientation = when (currentMirror) {
            MirrorOrientation.SLASH -> MirrorOrientation.BACKSLASH
            MirrorOrientation.BACKSLASH -> MirrorOrientation.SLASH
        }

        val snapshot = UndoSnapshot(
            playerPos = state.playerPos,
            playerFacing = state.playerFacing,
            crates = state.crates,
            mirrors = state.mirrors,
            ironKeys = state.ironKeys,
            goldKeys = state.goldKeys,
            unlockedDoors = state.unlockedDoors,
            waterBridges = state.waterBridges,
            openedChests = state.openedChests,
            collectedKeys = state.collectedKeys,
            stepCount = state.stepCount
        )

        val newMirrors = state.mirrors.toMutableMap()
        newMirrors[targetPos] = newOrientation

        return state.copy(
            mirrors = newMirrors,
            undoStack = state.undoStack + snapshot
        )
    }

    fun undo(state: GameState): GameState {
        if (state.undoStack.isEmpty()) return state
        val last = state.undoStack.last()
        return state.copy(
            playerPos = last.playerPos,
            playerFacing = last.playerFacing,
            crates = last.crates,
            mirrors = last.mirrors,
            ironKeys = last.ironKeys,
            goldKeys = last.goldKeys,
            unlockedDoors = last.unlockedDoors,
            waterBridges = last.waterBridges,
            openedChests = last.openedChests,
            collectedKeys = last.collectedKeys,
            stepCount = last.stepCount,
            isCompleted = false,
            isDefeated = false,
            undoStack = state.undoStack.dropLast(1)
        )
    }
}

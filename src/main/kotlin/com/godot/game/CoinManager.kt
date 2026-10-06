package com.godot.game

import godot.annotation.Script
import godot.api.Node

@Script
class CoinManager : Node() {

    companion object {

        @Volatile
        var instance: CoinManager? = null

        const val WIN_COINS = 25
    }

    @Volatile
    var collectedCoin = 0
        private set

    override fun _ready() {
        instance = this
    }

    fun onCollect() {
        collectedCoin++
    }

    fun onReset() {
        collectedCoin = 0
    }

    fun hasWon(): Boolean {
        return collectedCoin >= WIN_COINS
    }
}
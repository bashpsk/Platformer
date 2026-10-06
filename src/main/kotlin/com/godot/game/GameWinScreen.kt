package com.godot.game

import godot.annotation.Register
import godot.annotation.Script
import godot.api.Control

@Script
class GameWinScreen : Control() {

    override fun _ready() {
        SoundManager.instance?.apply {
            stopLevelMusic()
            playMenuMusic()
        }
    }

    @Register
    fun onMenuButtonPressed() {
        SoundManager.instance?.playButtonPressed()
        getTree()?.changeScene(GameMenuScenePath)
    }
}
package com.godot.game

import godot.annotation.Register
import godot.annotation.Script
import godot.api.Control

@Script
class GameOverScreen : Control() {

    override fun _ready() {
        SoundManager.instance?.apply {
            stopLevelMusic()
            playMenuMusic()
        }
    }

    @Register
    fun onPlayButtonPressed() {
        SoundManager.instance?.playButtonPressed()
        getTree()?.changeScene(Level1ScenePath)
    }

    @Register
    fun onMenuButtonPressed() {
        SoundManager.instance?.playButtonPressed()
        getTree()?.changeScene(GameMenuScenePath)
    }
}
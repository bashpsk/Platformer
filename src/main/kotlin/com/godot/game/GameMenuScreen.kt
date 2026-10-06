package com.godot.game

import godot.annotation.Register
import godot.annotation.Script
import godot.api.Control

@Script
class GameMenuScreen : Control() {

    @Register
    fun onPlayButtonPressed() {
        SoundManager.instance?.apply {
			stopMenuMusic()
			playButtonPressed()
		}
        getTree()?.changeScene(Level1ScenePath)
    }
}
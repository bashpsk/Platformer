package com.godot.game

import godot.annotation.Script
import godot.api.AudioStreamPlayer
import godot.api.Control

@Script
class SoundManager : Control() {

    companion object {

        @Volatile
        var instance: SoundManager? = null
    }

    override fun _ready() {
        instance = this
        playMenuMusic()
    }

    fun playButtonPressed() {
        getStreamPlayer("SoundButtonPressed")?.play()
    }

    fun playCoinCollect() {
        getStreamPlayer("SoundCoinCollect")?.play()
    }

    fun playEnemyLose() {
        getStreamPlayer("SoundEnemyLose")?.play()
    }

    fun playFallGameOver() {
        getStreamPlayer("SoundFallGameOver")?.play()
    }

    fun playMenuMusic() {
        getStreamPlayer("MusicMenu")?.play()
    }

    fun stopMenuMusic() {
        getStreamPlayer("MusicMenu")?.stop()
    }

    fun playLevelMusic() {
        getStreamPlayer("MusicLevel")?.play()
    }

    fun stopLevelMusic() {
        getStreamPlayer("MusicLevel")?.stop()
    }

    private fun getStreamPlayer(path: String): AudioStreamPlayer? {
        return instance?.getNodeOrNull(path) as? AudioStreamPlayer
    }
}
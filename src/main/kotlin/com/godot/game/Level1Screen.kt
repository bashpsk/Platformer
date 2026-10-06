package com.godot.game

import godot.annotation.Script
import godot.api.Node3D

@Script
class Level1Screen : Node3D() {

    override fun _ready() {
        SoundManager.instance?.apply {
            stopMenuMusic()
            playLevelMusic()
        }
        CoinManager.instance?.onReset()
    }
}
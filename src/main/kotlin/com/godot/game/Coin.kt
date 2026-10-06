package com.godot.game

import godot.annotation.Export
import godot.annotation.Register
import godot.annotation.Script
import godot.api.*
import godot.core.StringName
import godot.global.GD

@Script
class Coin : Area3D() {

    private val ROTATION_SPEED = 2.0F

    private var animationPlayer: AnimationPlayer? = null
    private var coinLabel: Label? = null

    @Export
    var hud: CanvasLayer? = null

    override fun _ready() {
        animationPlayer = getNodeOrNull("AnimationPlayer") as? AnimationPlayer
        coinLabel = hud?.getNodeOrNull("CoinLabel") as? Label
    }

    override fun _process(delta: Double) {
        addSpinning()
    }

    @Register
    fun onBodyEntered(body: Node3D) {
        CoinManager.instance?.onCollect()
        SoundManager.instance?.playCoinCollect()
        coinLabel?.text = "${CoinManager.instance?.collectedCoin ?: 0}".padStart(2, '0')
        setCollisionLayerValue(3, false)
        setCollisionMaskValue(1, false)
        animationPlayer?.play("bounce")
        if (CoinManager.instance?.hasWon() == true) getTree()?.changeScene(GameWinScenePath)
    }

    @Register
    fun onAnimationPlayerAnimationFinished(animName: StringName) {
        queueFree()
    }

    private fun addSpinning() {
        rotateY(GD.degToRad(ROTATION_SPEED).toFloat())
    }
}
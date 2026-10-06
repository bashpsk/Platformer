package com.godot.game

import godot.annotation.Export
import godot.annotation.Register
import godot.annotation.Script
import godot.api.*
import godot.core.Vector3
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import kotlinx.coroutines.launch

@Script
class Enemy : CharacterBody3D() {

    private val gravity = ProjectSettings.getSetting(GravityPath) as? Double ?: 9.8

    private var rayCast: RayCast3D? = null
    private var animationPlayer: AnimationPlayer? = null
    private var soundSquash: AudioStreamPlayer? = null

    private var isTurning = false

    @Export
    var direction: Vector3 = Vector3.ZERO

    @Export
    var turnAroundAtEdges = false

    @Export
    var speed = 2.0

    override fun _ready() {
        rayCast = getNodeOrNull("RayCast3D") as? RayCast3D
        animationPlayer = getNodeOrNull("AnimationPlayer") as? AnimationPlayer
        soundSquash = getNodeOrNull("SoundSquash") as? AudioStreamPlayer
    }

    override fun _process(delta: Double) {
        addGravity(delta = delta)
        addMovement()
        moveAndSlide()
        if (hasTurnAround()) turnAround()
    }

    @Register
    fun onSideCheckerBodyEntered(body: Node3D) {
        SoundManager.instance?.playEnemyLose()
        getTree()?.changeScene(GameOverScenePath)
    }

    @Register
    fun onTopCheckerBodyEntered(body: Node3D) {
        godotCoroutine().launch {
            animationPlayer?.play("Squash")
            soundSquash?.play()
            (body as? Player)?.enemySquashBounce()
            (getNodeOrNull("SideChecker") as? Area3D)?.setCollisionMaskValue(1, false)
            (getNodeOrNull("TopChecker") as? Area3D)?.setCollisionMaskValue(1, false)
            direction = Vector3.ZERO
            speed = 0.0
            getTree()?.createTimer(1.5)?.timeout?.await()
            queueFree()
        }
    }

    private fun addGravity(delta: Double) {
        if (!isOnFloor()) velocityMutate { y -= delta * gravity }
    }

    private fun addMovement() {
        velocityMutate {
            x = direction.x * speed
            z = direction.z * speed
        }
    }

    private fun turnAround() {
        godotCoroutine().launch {
            isTurning = true
            val turnTween = getTree()?.createTween()
            val directionBackup = direction
            direction = Vector3.ZERO
            turnTween?.tweenProperty(
                this@Enemy,
                "rotation_degrees",
                Vector3(0, 180, 0),
                0.6
            )?.asRelative()
            getTree()?.createTimer(0.6)?.timeout?.await()
            direction = (directionBackup * -1).apply { y = 0.0 }
            isTurning = false
        }
    }

    private fun hasTurnAround(): Boolean {
        return (isOnWall() && !isTurning)
                || (rayCast?.isColliding() == false && isOnFloor() && !isTurning && turnAroundAtEdges)
    }
}
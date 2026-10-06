package com.godot.game

import godot.annotation.Register
import godot.annotation.Script
import godot.api.*
import godot.core.Vector3
import godot.global.GD

@Script
class Player : CharacterBody3D() {

    private val SPEED = 5.0
    private val JUMP_VELOCITY = 5.0
    private val CAMERA_ROTATION = 30.0

    private val gravity = ProjectSettings.getSetting(GravityPath) as? Double ?: 9.8

    private var cameraController: Node3D? = null
    private var playerMesh: Node3D? = null
    private var rayCast: RayCast3D? = null
    private var animationPlayer: AnimationPlayer? = null
    private var soundJump: AudioStreamPlayer? = null

    override fun _ready() {
        cameraController = getNodeOrNull("CameraController") as? Node3D
        playerMesh = getNodeOrNull("Armature") as? Node3D
        rayCast = getNodeOrNull("RayCast3D") as? RayCast3D
        animationPlayer = getNodeOrNull("AnimationPlayer") as? AnimationPlayer
        soundJump = getNodeOrNull("SoundJump") as? AudioStreamPlayer
    }

    override fun _process(delta: Double) {
        addGravity(delta = delta)
        addJump()
        addMovement()
        moveAndSlide()
        addCameraMovement()
        addCameraRotation()
        alignWithGround(floorNormal = rayCast?.getCollisionNormal()?.takeIf { isOnFloor() } ?: Vector3.UP)
    }

    @Register
    fun onFallZoneBodyEntered(body: Node3D) {
        SoundManager.instance?.playFallGameOver()
        getTree()?.changeScene(GameOverScenePath)
    }

    fun enemySquashBounce() {
        velocityMutate { y = JUMP_VELOCITY * 0.7 }
    }

    private fun addGravity(delta: Double) {
        if (!isOnFloor()) velocityMutate { y -= delta * gravity }
    }

    private fun addJump() {
        if (isOnFloor() && Input.isActionPressed("ui_accept")) {
            soundJump?.play()
            velocityMutate { y = JUMP_VELOCITY }
        }
    }

    private fun addMovement() {
        val inputDirection = Input.getVector(
            negativeX = "ui_left",
            positiveX = "ui_right",
            negativeY = "ui_up",
            positiveY = "ui_down"
        )
        val direction = (cameraController?.transform?.basis ?: transform.basis) * Vector3(
            inputDirection.x,
            0,
            inputDirection.y
        ).normalized()

        when {

            isOnFloor() && Input.isActionPressed("ui_accept") -> animationPlayer?.play("Jump")
            isOnFloor() && !inputDirection.isZeroApprox() -> animationPlayer?.play("Run")
            isOnFloor() && inputDirection.isZeroApprox() -> animationPlayer?.play("Idle")
        }

        if (!inputDirection.isZeroApprox()) cameraController?.rotationDegrees?.y?.let { cameraY ->
            playerMesh?.rotationDegreesMutate { y = cameraY - GD.radToDeg(inputDirection.angle()) - 90 }
        }

        if (direction.isNormalized()) velocityMutate {
            x = direction.x * SPEED
            z = direction.z * SPEED
        } else velocityMutate {
            x = GD.moveToward(direction.x, 0.0, SPEED)
            z = GD.moveToward(direction.z, 0.0, SPEED)
        }
    }

    private fun addCameraMovement() {
        cameraController?.let { controller ->
            controller.position = GD.lerp(controller.position, position, 0.15)
        }
    }

    private fun addCameraRotation() {
        if (Input.isActionJustPressed("camera_left")) {
            cameraController?.rotateY(GD.degToRad(-CAMERA_ROTATION).toFloat())
        }
        if (Input.isActionJustPressed("camera_right")) {
            cameraController?.rotateY(GD.degToRad(CAMERA_ROTATION).toFloat())
        }
    }

    private fun alignWithGround(floorNormal: Vector3) {
        val xTransform = globalTransform
        xTransform.apply {
            basis {
                y = floorNormal
                x = -z.cross(floorNormal)
            }
            basis = basis.orthonormalized()
        }
        globalTransform = globalTransform.interpolateWith(xTransform, 0.3)
    }
}
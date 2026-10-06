package com.godot.game

import godot.annotation.Export
import godot.annotation.Register
import godot.annotation.Script
import godot.api.AnimatableBody3D
import godot.api.Node3D
import godot.api.Tween
import godot.core.Vector3

@Script
class MovablePlatform : AnimatableBody3D() {

    @Export
    var a = Vector3.ZERO

    @Export
    var b = Vector3.ZERO

    @Export
    var duration = 2.0

    @Export
    var delay = 1.0

    override fun _process(delta: Double) {

    }

    @Register
    fun onMoveZoneBodyEntered(body: Node3D) {
        getTree()?.createTween()?.tweenProperty(
            this@MovablePlatform,
            "position",
            if (position == a) b else a,
            duration
        )?.setDelay(delay)?.setTrans(Tween.TransitionType.SINE)
    }
}
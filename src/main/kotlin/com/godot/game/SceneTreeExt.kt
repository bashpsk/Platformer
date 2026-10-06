package com.godot.game

import godot.api.SceneTree

fun SceneTree.resetCurrentScene() {
    callDeferred("reload_current_scene")
}

fun SceneTree.changeScene(path: String) {
    callDeferred("change_scene_to_file", path)
}
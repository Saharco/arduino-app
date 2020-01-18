package com.technion.columbus.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2

class GameScreenGestureListener(private val camera: OrthographicCamera) :
    GestureDetector.GestureListener {

    companion object {
        const val MIN_ZOOM = 4f
        const val MAX_ZOOM = 0.33f
    }

    private var currentCameraZoom = camera.zoom // default is 1f

    override fun zoom(initialDistance: Float, distance: Float): Boolean {
        camera.zoom = (initialDistance / distance) * currentCameraZoom
        camera.zoom = maxOf(
            minOf(
                (initialDistance / distance) * currentCameraZoom,
                MIN_ZOOM
            ),
            MAX_ZOOM
        )
        camera.update()
        return true
    }

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
        camera.translate(-deltaX * currentCameraZoom, deltaY * currentCameraZoom)
        camera.update()
        return false
    }

    override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        currentCameraZoom = camera.zoom
        return false
    }


    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        return false
    }

    override fun pinchStop() {
    }

    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        return false
    }

    override fun longPress(x: Float, y: Float): Boolean {
        return false
    }

    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun pinch(
        initialPointer1: Vector2?,
        initialPointer2: Vector2?,
        pointer1: Vector2?,
        pointer2: Vector2?
    ): Boolean {
        return false
    }
}

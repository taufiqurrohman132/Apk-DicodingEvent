package com.example.dicodingeventaplication.utils

import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView

class BounceEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {
    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return object : EdgeEffect(view.context) {
            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                view.invalidate()
            }

            override fun onRelease() {
                super.onRelease()
                view.invalidate()
            }

            override fun onAbsorb(velocity: Int) {
                super.onAbsorb(velocity)
                view.invalidate()
            }
        }
    }
}
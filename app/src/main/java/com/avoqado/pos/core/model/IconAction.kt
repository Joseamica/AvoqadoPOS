package com.avoqado.pos.core.model

import android.content.Context

data class IconAction(
    val iconType: IconType,
    val flowStep: FlowStep,
    val context: Context
)
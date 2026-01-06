package com.example.packetworldapp.dto

import com.example.packetworldapp.poko.Conductor
import com.google.gson.annotations.SerializedName

data class RSAutenticacionConductor(
    var error: Boolean = false,

    var mensaje: String = "",

    @SerializedName("colaborador")
    var conductor: Conductor? = null
)
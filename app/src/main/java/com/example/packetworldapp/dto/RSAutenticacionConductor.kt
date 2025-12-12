package com.example.packetworldapp.dto

import com.example.packetworldapp.poko.Conductor

data class RSAutenticacionConductor(
    val error : Boolean,
    val mensaje : String,
    val conductor : Conductor?
)

package com.ulsee.shiba.data.response

data class AddPerson(
    val command: Int,
    val `data`: Data1,
    val detail: String,
    val status: Int,
    val transmit_cast: Int
)

data class Data1(
    val personId: String
)
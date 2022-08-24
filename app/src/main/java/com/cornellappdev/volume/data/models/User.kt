package com.cornellappdev.volume.data.models

data class User(
    val uuid: String,
    val followedPublicationIDs: List<String>
)

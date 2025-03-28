package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Friend (
    val id: Int? = null,
    val username: String,
    val name: String,
    val profilePicture: String? = null
)
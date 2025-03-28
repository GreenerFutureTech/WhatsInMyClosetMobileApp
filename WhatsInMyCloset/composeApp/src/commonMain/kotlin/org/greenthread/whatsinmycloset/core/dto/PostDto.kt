package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostDto (
    val id: Int,
    val outfitId: Int,
    val creatorId: Int,
    val createdAt: String? = null
)
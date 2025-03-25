package org.greenthread.whatsinmycloset.features.tabs.social.domain

// Model for a post
data class Post(
    val id: Int,
    val outfit: String,
    val username: String,
    val date: String
)
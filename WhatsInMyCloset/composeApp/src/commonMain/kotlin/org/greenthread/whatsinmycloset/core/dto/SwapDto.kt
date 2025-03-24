package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class SwapDto(
    val id: String,
    val itemId: ItemDto,
    val userId: Int,
    val status: String,
    val registeredAt: String,
    val updatedAt: String? = null
)

@Serializable
data class OtherSwapDto(
    val swap: SwapDto,
    val user: MessageUserDto
)

fun SwapDto.toOtherSwapDto(user: MessageUserDto): OtherSwapDto {
    return OtherSwapDto(
        swap = this,
        user = user
    )
}
@Serializable
data class SwapStatusDto(
    val id: String,
    val itemId: String,
    val userId: Int,
    val status: String,
    val registeredAt: String,
    val updatedAt: String? = null
)



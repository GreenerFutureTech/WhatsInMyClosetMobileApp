package org.greenthread.whatsinmycloset.features.tabs.swap.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

object SwapDtoSerializer : KSerializer<SwapDto> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SwapDto") {
        element<Int>("id")
        element<String>("itemId")
        element<String>("userId")
        element<String>("mediaUrl")
        element<String>("status")
        element<String>("registeredAt")
        element<String>("updatedAt")
    }

    override fun deserialize(decoder: Decoder): SwapDto = decoder.decodeStructure(descriptor) {
        var id: Int? = null
        var itemId: String? = null
        var userId: String? = null
        var mediaUrl: String? = null
        var status: String? = null
        var registeredAt: String? = null
        var updatedAt: String? = null

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> id = decodeIntElement(descriptor, index)
                1 -> itemId = decodeStringElement(descriptor, index)
                2 -> userId = decodeStringElement(descriptor, index)
                3 -> mediaUrl = decodeStringElement(descriptor, index)
                4 -> status = decodeStringElement(descriptor, index)
                5 -> registeredAt = decodeStringElement(descriptor, index)
                6 -> updatedAt = decodeStringElement(descriptor, index)

                CompositeDecoder.DECODE_DONE -> break
                else -> throw SerializationException("Unexpected index $index")
            }
        }


        return@decodeStructure SwapDto(
            id ?: throw SerializationException("Missing id"),
            itemId ?: throw SerializationException("Missing itemId"),
            userId ?: throw SerializationException("Missing userId"),
            mediaUrl ?: throw SerializationException("Missing mediaUrl"),
            status ?: throw SerializationException("Missing status"),
            registeredAt ?: throw SerializationException("Missing registeredAt"),
            updatedAt ?: throw SerializationException("Missing updatedAt"),
        )
    }

    override fun serialize(encoder: Encoder, value: SwapDto) = encoder.encodeStructure(descriptor) {
        encodeIntElement(descriptor, 0, value.id)
        encodeStringElement(descriptor, 1, value.itemId)
        encodeStringElement(descriptor, 2, value.userId)
        encodeStringElement(descriptor, 3, value.mediaUrl)
        encodeStringElement(descriptor, 4, value.status)
        encodeStringElement(descriptor, 5, value.registeredAt)
        encodeStringElement(descriptor, 6, value.updatedAt)
    }
}

package org.greenthread.whatsinmycloset.core.dto

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
        element<String>("id")
        element<String>("itemId")
        element<Int>("userId")
        element<String>("status")
        element<String>("condition")
        element<String>("brand")
        element<String>("registeredAt")
        element<String>("updatedAt")
    }

    override fun deserialize(decoder: Decoder): SwapDto = decoder.decodeStructure(descriptor) {
        var id: String? = null
        var itemId: String? = null
        var userId: Int? = null
        var status: String? = null
        var condition: String? = null
        var brand: String? = null
        var registeredAt: String? = null
        var updatedAt: String? = null

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> id = decodeStringElement(descriptor, index)
                1 -> itemId = decodeStringElement(descriptor, index)
                2 -> userId = decodeIntElement(descriptor, index)
                3 -> status = decodeStringElement(descriptor, index)
                4 -> condition = decodeStringElement(descriptor, index)
                5 -> brand = decodeStringElement(descriptor, index)
                6 -> registeredAt = decodeStringElement(descriptor, index)
                7 -> updatedAt = decodeStringElement(descriptor, index)

                CompositeDecoder.DECODE_DONE -> break
                else -> throw SerializationException("Unexpected index $index")
            }
        }


        return@decodeStructure SwapDto(
            id ?: throw SerializationException("Missing id"),
            itemId ?: throw SerializationException("Missing itemId"),
            userId ?: throw SerializationException("Missing userId"),
            status ?: throw SerializationException("Missing status"),
            condition ?: throw SerializationException("Missing condition"),
            brand ?: throw SerializationException("Missing brand"),
            registeredAt ?: throw SerializationException("Missing registeredAt"),
            updatedAt ?: throw SerializationException("Missing updatedAt"),
        )
    }

    override fun serialize(encoder: Encoder, value: SwapDto) = encoder.encodeStructure(descriptor) {
        encodeStringElement(descriptor, 0, value.id)
        encodeStringElement(descriptor, 1, value.itemId)
        encodeIntElement(descriptor, 2, value.userId)
        encodeStringElement(descriptor, 3, value.status)
        encodeStringElement(descriptor, 4, value.condition)
        encodeStringElement(descriptor, 5, value.brand)
        encodeStringElement(descriptor, 6, value.registeredAt)
        encodeStringElement(descriptor, 7, value.updatedAt)
    }
}

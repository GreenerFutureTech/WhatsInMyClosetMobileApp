package org.greenthread.whatsinmycloset.core.domain.models

import org.greenthread.whatsinmycloset.core.dto.MessageUserDto

object MessageManager {
    var currentOtherUser: MessageUserDto? = null
        private set

    fun setCurrentOtherUser(otherUser: MessageUserDto) {
        currentOtherUser = otherUser
    }

    fun clearCurrentOtherUser() {
        currentOtherUser = null
    }
}
package org.greenthread.whatsinmycloset.core.domain.models

data class Session(
    /**
     * The ID of the session
     */
    val id: Long,

    /**
     * The account this session belongs to
     */
    val account: Account,

    /**
     * The token returned when the session is created
     * used to authenticate subsequent requests
     */
    //val token: Token,


    /**
     * The time the session was created at
     */
    //val createdAt: Date,

    /**
     * The time the session was last updated at
     */
    //val updatedAt: Date
) {}
//    constructor(sessionEntity: SessionEntity) : this(
//        id = sessionEntity.id,
//        account = Account(sessionEntity.accountEntity),
//        token = Token(sessionEntity.tokenEntity),
//        createdAt = sessionEntity.createdAt,
//        updatedAt = sessionEntity.updatedAt
//    )
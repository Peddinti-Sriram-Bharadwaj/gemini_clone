package com.example.gemini_clone

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import java.util.Date

class Message(private val id: String, private val text : String, private val user: IUser, private val createdAt: Date) : IMessage {
    override fun getId(): String {
        return id
    }

    override fun getText(): String {
       return text
    }

    override fun getUser(): IUser {
        return user
    }

    override fun getCreatedAt(): Date {
       return createdAt
    }

}
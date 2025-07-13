package com.example.gemini_clone

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.stfalcon.chatkit.commons.models.MessageContentType
import java.util.Date

class Message(private val id: String, private val text : String, private val user: IUser, private val createdAt: Date, private val imageUrl: String?) : IMessage, MessageContentType.Image {
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

    override fun getImageUrl(): String? {
        if(imageUrl == null){
            return null
        }
        else{
            return imageUrl
        }
    }

}
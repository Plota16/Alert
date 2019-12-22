package com.plocki.alert.models

enum class LikeType(val type: Int?) {
    LIKE(1),
    DISLIKE(-1),
    NONE(null);

    companion object {
        private val map = values().associateBy(LikeType::type)
        fun getNameByType(type: Int?) = map[type]
    }

}
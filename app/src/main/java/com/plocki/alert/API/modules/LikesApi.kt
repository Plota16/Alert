package com.plocki.alert.API.modules

import com.apollographql.apollo.ApolloCall
import com.plocki.alert.API.ApolloInstance
import com.plocki.alert.CreateOrUpdateLikeMutation
import com.plocki.alert.DeleteLikeMutation
import com.plocki.alert.type.CreateLikeDto
import com.plocki.alert.type.DeleteLikeDto

object LikesApi {
    fun createOrUpadateLike(createLikeDto: CreateLikeDto, callback: ApolloCall.Callback<CreateOrUpdateLikeMutation.Data>) {
        val mutation = CreateOrUpdateLikeMutation.builder().likeData(createLikeDto).build()

        ApolloInstance.mutate(
            mutation,
            callback
        )
    }

    fun deleteLike(deleteLikeDto: DeleteLikeDto, callback: ApolloCall.Callback<DeleteLikeMutation.Data>) {
        val mutation = DeleteLikeMutation.builder().likeData(deleteLikeDto).build()

        ApolloInstance.mutate(
            mutation,
            callback
        )
    }
}
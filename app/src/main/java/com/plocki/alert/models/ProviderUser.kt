package com.plocki.alert.models

import com.plocki.alert.type.CreateOAuthUserDto
import com.plocki.alert.type.OauthProvider

class ProviderUser (
    var providerType: ProviderType,
    private var tokenId: String
) {

    fun createOAuthUserDto(): CreateOAuthUserDto {
        return CreateOAuthUserDto.builder()
            .oauthId(tokenId)
            .oauthProvider(OauthProvider.valueOf(providerType.provider))
            .build()
    }

    override fun toString(): String {
        return "ProviderUser(providerType=$providerType, tokenId='$tokenId')"
    }


}
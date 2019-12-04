package com.plocki.alert.models

import com.plocki.alert.type.CreateOAuthUserDto
import com.plocki.alert.type.OauthProvider

class ProviderUser (
    var providerType: ProviderType,
    var tokenId: String
) {

    fun createOAuthUserDto(): CreateOAuthUserDto {
        val createOAuthUserDto = CreateOAuthUserDto.builder()
            .oauthId(this.tokenId)
            .oauthProvider(OauthProvider.valueOf(this.providerType.provider))
            .build()
        return createOAuthUserDto
    }

}
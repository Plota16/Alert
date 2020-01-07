package com.plocki.alert.models

import android.content.Context
import com.plocki.alert.type.CreateReportDto

class Report(
    var category: Category,
    var description: String
) {

    fun createReportDto(context: Context?): CreateReportDto {
        val reportBuilder = CreateReportDto.builder()
            .description(this.description)
            .category(this.category.uuid)

        return reportBuilder.build()
    }
}
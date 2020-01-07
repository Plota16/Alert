package com.plocki.alert.models

import android.content.Context
import com.plocki.alert.type.CreateReportDto

class Report(
    var category: Category,
    var description: String,
    var event: Event
) {

    fun createReportDto(context: Context?): CreateReportDto {
        val reportBuilder = CreateReportDto.builder()
            .description(this.description)
            .category(this.category.uuid)
            .event(this.event.UUID.toString())

        return reportBuilder.build()
    }
}
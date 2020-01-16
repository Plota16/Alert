package com.plocki.alert.models

import com.plocki.alert.type.CreateReportDto

class Report(
    var category: String,
    var description: String,
    var event: Event
) {

    fun createReportDto(): CreateReportDto {
        val reportBuilder = CreateReportDto.builder()
            .description(this.description)
            .event(this.event.UUID.toString())
            .category(this.category)

        return reportBuilder.build()
    }
}
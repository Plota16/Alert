package com.plocki.alert.models

import com.apollographql.apollo.api.FileUpload
import com.google.android.gms.maps.model.LatLng
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.type.CreateEventDto
import com.plocki.alert.type.PointInput
import java.io.File
import java.util.*

class Event (
    var UUID : UUID,
    var coords: LatLng,
    var title: String,
    var image: String?,
    var description : String?,
    var category: Category,
    var creator : Int
) {
    companion object {
        fun fromResponse(
            uuid: String,
            coords: AllEventsQuery.Coords, // TODO: Make class for Coords
            title: String,
            image: String?,
            description: String?,
            category: Category,
            creator: Int
        ): Event {
            return Event(
                UUID.fromString(uuid),
                LatLng(coords.x(), coords.y()),
                title,
                image,
                description,
                category,
                creator
            )
        }
    }

    fun createEventDto(): CreateEventDto {
        var eventBuilder = CreateEventDto.builder()
            .title(this.title)
            .description(this.description)
            .coords(
                (PointInput.builder()
                    .x(this.coords.latitude)
                    .y(this.coords.longitude).build())
            )
            .categoryName(this.category.title)


        if (this.image != "") {
            val image = File(this.image)
            eventBuilder
                .imageData(FileUpload("image/jpg", image))
        }

        return eventBuilder.build()
    }

    override fun toString(): String {
        return "Event(UUID=$UUID, coords=$coords, image='$image', title='$title', desctription=$description, category=$category, creator=$creator)"
    }
}
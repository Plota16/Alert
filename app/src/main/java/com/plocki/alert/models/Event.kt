package com.plocki.alert.models

import android.content.Context
import com.apollographql.apollo.api.FileUpload
import com.google.android.gms.maps.model.LatLng
import com.plocki.alert.AllEventsQuery
import com.plocki.alert.type.CreateEventDto
import com.plocki.alert.type.PointInput
import id.zelory.compressor.Compressor
import java.io.File
import java.util.*

class Event (
    var UUID : UUID,
    var coordinates: LatLng,
    var title: String,
    var image: String?,
    var description : String?,
    var category: Category,
    var creator : Int,
    var totalLikes: Int? = null,
    var userLike: LikeType? = null
) {
    companion object {
        fun fromResponse(
            uuid: String,
            coordinates: AllEventsQuery.Coords, // TODO: Make class for Coords
            title: String,
            image: String?,
            description: String?,
            category: Category,
            creator: Int,
            totalLikes: Int? = null,
            userLike: LikeType? = null
        ): Event {
            return Event(
                UUID.fromString(uuid),
                LatLng(coordinates.x(), coordinates.y()),
                title,
                image,
                description,
                category,
                creator,
                totalLikes,
                userLike
            )
        }
    }

    fun createEventDto(context: Context?): CreateEventDto {
        val eventBuilder = CreateEventDto.builder()
            .title(this.title)
            .description(this.description)
            .coords(
                (PointInput.builder()
                    .x(this.coordinates.latitude)
                    .y(this.coordinates.longitude).build())
            )
            .category(this.category.uuid)


        if (this.image != "") {
            val image = File(this.image!!)
            println("BEFORE"  + image.length())
            val compressedImage = Compressor(context)
                .setMaxWidth(1280)
                .setMaxHeight(720)
                .setQuality(90)
                .compressToFile(image)
            println("AFTER " + compressedImage.length())
            eventBuilder
                .imageData(FileUpload("image/jpg", compressedImage))
        }

        return eventBuilder.build()
    }

    override fun toString(): String {
        return "Event(UUID=$UUID, coordinates=$coordinates, image='$image', title='$title', desctription=$description, category=$category, creator=$creator, totalLikes=$totalLikes, userLike=$userLike)"
    }
}
package com.application.moviex.models

import android.os.Parcel
import android.os.Parcelable

data class MovieModel(
    var title: String = "", var poster_path: String = "", var release_date: String = "",
    var id: Int = 0, var vote_average: Float = 0f, var overview: String = "", var runtime: Int = 0,
    var original_language: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(title)
        dest?.writeString(poster_path)
        dest?.writeString(release_date)
        dest?.writeInt(id)
        dest?.writeFloat(vote_average)
        dest?.writeString(overview)
        dest?.writeInt(runtime)
        dest?.writeString(original_language)
    }

    companion object CREATOR : Parcelable.Creator<MovieModel> {
        override fun createFromParcel(parcel: Parcel): MovieModel {
            return MovieModel(parcel)
        }

        override fun newArray(size: Int): Array<MovieModel?> {
            return arrayOfNulls(size)
        }
    }
}
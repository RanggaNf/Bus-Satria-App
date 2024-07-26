package com.bussatriaapp.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.vectorResource
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun vectorToBitmapDescriptor(
    context: Context,
    @DrawableRes vectorResId: Int,
    tintColor: Color
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setTint(tintColor.toArgb())
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
fun vectorToBitmap(
    context: Context,
    @DrawableRes vectorResId: Int,
    @ColorInt tintColor: Int? = null
): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
    if (tintColor != null) {
        vectorDrawable.setTint(tintColor)
    }
    vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun calculateArrowPosition(start: LatLng, end: LatLng, fraction: Double): LatLng {
    return LatLng(
        start.latitude + (end.latitude - start.latitude) * fraction,
        start.longitude + (end.longitude - start.longitude) * fraction
    )
}

fun calculateArrowRotation(start: LatLng, end: LatLng): Float {
    val latDiff = end.latitude - start.latitude
    val lngDiff = end.longitude - start.longitude
    return (Math.toDegrees(atan2(lngDiff, latDiff)).toFloat() + 270) % 360
}

fun calculateDistance(start: LatLng, end: LatLng): Double {
    val earthRadius = 6371000.0 // Radius bumi dalam meter

    val lat1 = Math.toRadians(start.latitude)
    val lat2 = Math.toRadians(end.latitude)
    val lon1 = Math.toRadians(start.longitude)
    val lon2 = Math.toRadians(end.longitude)

    val dLat = lat2 - lat1
    val dLon = lon2 - lon1

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(lat1) * cos(lat2) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c
}

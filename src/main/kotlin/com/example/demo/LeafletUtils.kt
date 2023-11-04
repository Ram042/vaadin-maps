package com.example.demo

import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import software.xdev.vaadin.maps.leaflet.MapContainer
import software.xdev.vaadin.maps.leaflet.basictypes.LIcon
import software.xdev.vaadin.maps.leaflet.basictypes.LIconOptions
import software.xdev.vaadin.maps.leaflet.basictypes.LLatLng
import software.xdev.vaadin.maps.leaflet.layer.ui.LMarker
import software.xdev.vaadin.maps.leaflet.layer.ui.LMarkerOptions
import software.xdev.vaadin.maps.leaflet.layer.vector.LPolygon
import software.xdev.vaadin.maps.leaflet.registry.LComponentManagementRegistry
import java.util.*

@VaadinDsl
public fun mapContainer(
    reg: LComponentManagementRegistry,
    closure: (@VaadinDsl MapContainer).() -> Unit
): MapContainer {
    val mapContainer = MapContainer(reg)
    mapContainer.apply(closure)
    return mapContainer
}

public fun LMarker(
    reg: LComponentManagementRegistry,
    lat: Double, lng: Double,
    closure: (@VaadinDsl LMarkerOptions).() -> Unit
): LMarker {
    val lMarkerOptions = LMarkerOptions(closure)
    return LMarker(reg, LLatLng(reg, lat, lng), lMarkerOptions)
}

public fun LMarkerOptions(
    closure: (@VaadinDsl LMarkerOptions).() -> Unit
): LMarkerOptions {
    val lMarkerOptions = LMarkerOptions()
    lMarkerOptions.apply(closure)
    return lMarkerOptions
}

public fun LIconOptions(
    closure: (@VaadinDsl LIconOptions).() -> Unit
): LIconOptions {
    val lMarkerOptions = LIconOptions()
    lMarkerOptions.apply(closure)
    return lMarkerOptions
}

public fun LIcon(
    reg: LComponentManagementRegistry,
    closure: (@VaadinDsl LIconOptions).() -> Unit
): LIcon {
    return LIcon(reg, LIconOptions().apply(closure))
}


data class Point(val lat: Double, val lng: Double)

data class Polygon(val points: MutableList<Point> = mutableListOf())

/**
 * To DB
 */
data class Zone(val uuid: UUID, val polygon: Polygon)


data class ZoneUi(
    val zone: Zone,
    val zoneListElement: HorizontalLayout,
    var polygonlayer: LPolygon,
    val vertices: MutableList<LMarker> = mutableListOf()
)

data class PointUi(val uuid: UUID, val point: Point, var lMarker: LMarker, val pointListElement: Component)
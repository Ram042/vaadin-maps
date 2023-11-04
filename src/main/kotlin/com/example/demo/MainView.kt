package com.example.demo

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.ClientCallable
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.html.NativeLabel
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.Route
import elemental.json.JsonObject
import org.intellij.lang.annotations.Language
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.xdev.vaadin.maps.leaflet.basictypes.LLatLng
import software.xdev.vaadin.maps.leaflet.basictypes.LPoint
import software.xdev.vaadin.maps.leaflet.layer.raster.LTileLayer
import software.xdev.vaadin.maps.leaflet.layer.ui.LMarker
import software.xdev.vaadin.maps.leaflet.layer.vector.LCircleMarker
import software.xdev.vaadin.maps.leaflet.layer.vector.LCircleMarkerOptions
import software.xdev.vaadin.maps.leaflet.layer.vector.LPolygon
import software.xdev.vaadin.maps.leaflet.registry.LComponentManagementRegistry
import software.xdev.vaadin.maps.leaflet.registry.LDefaultComponentManagementRegistry
import java.util.*

/**
 * The main view contains a button and a click listener.
 */
@Route("")
//@NpmPackage("leaflet", version = "1.9.4")
//@NpmPackage("@geoman-io/leaflet-geoman-free", version = "2.14.2")
//@NpmPackage("@turf/turf", version = "6.5.0")
@JsModule("leaflet/dist/leaflet.js")
@CssImport("leaflet/dist/leaflet.css")
//@JsModule("@geoman-io/leaflet-geoman-free/dist/leaflet-geoman.min.js")
//@CssImport("@geoman-io/leaflet-geoman-free/dist/leaflet-geoman.css")
//@JsModule("@turf/turf/turf.min.js")
//@JsModule("@turf/turf")
//@JavaScript("https://unpkg.com/@turf/turf@6/turf.min.js")
//@JavaScript("https://unpkg.com/@turf/helpers@6.5.0/dist/js/index.js")
class MainView : HorizontalLayout() {

    private val logger: Logger = LoggerFactory.getLogger(MainView::class.java);
    private val MAP_ID = "l-map-id"

    private val zones: MutableMap<UUID, ZoneUi> = mutableMapOf()
    private val points: MutableMap<UUID, PointUi> = mutableMapOf()

    init {
        setSizeFull()
    }

    private val reg: LComponentManagementRegistry = LDefaultComponentManagementRegistry(this)

    private val mapContainer = mapContainer(reg) {
        flexGrow = 1.0
        setSizeFull()
    }

    private var map = mapContainer.getlMap()

    init {
        add(mapContainer)
        map.apply {
            addLayer(LTileLayer.createDefaultForOpenStreetMapTileServer(reg))
            setView(LLatLng(reg, 55.7494, 37.6192), 13)
            addLayer(LCircleMarker(reg, LLatLng(reg, 55.75, 37.62), LCircleMarkerOptions().withRadius(10)))
        }
        setId(MAP_ID)
    }

    private val zoneListTab = verticalLayout {
        this.width = null;
    }
    private val pointList = verticalLayout {
        this.width = null;
    }

    fun addAddPointButton(target: @VaadinDsl HorizontalLayout): Button {
        target.apply {
            return@addAddPointButton button("Добавить точку") {
            }
        }
    }

    private lateinit var addPointButton: Button;

    private fun addPointButtonSetDefaultListener() {
        addPointButton.onLeftClick {
            activeZone = null;
            map.off()
            map.on("click", "e => document.getElementById('$MAP_ID').\$server.addMarker(e.latlng)")
            addPointButton.text = "Отмена"
            addPointButton.style.setBackground("darkgoldenrod")
            it.unregisterListener();
            addPointButtonSetCancelListener()
        }
    }

    private fun addPointButtonSetCancelListener() {
        addPointButton.onLeftClick {
            activeZone = null
            map.off()
            addPointButton.text = "Добавить точку"
            addPointButton.style.setBackground(null)
            it.unregisterListener();
            addPointButtonSetDefaultListener()
        }
    }


    private val toolBar = horizontalLayout {
        button("Создать зону") {
            onLeftClick {
                activeZone = null;
                map.off()
                map.on("click", "e => document.getElementById('$MAP_ID').\$server.addZone(e.latlng)")
            }
        }
        addPointButton = addAddPointButton(this)
        addPointButtonSetDefaultListener()
    }

    init {
        add(verticalLayout {
            add(toolBar, zoneListTab, pointList)
        })
    }

    var activeZone: ZoneUi? = null;

    private val pointIcon = LIcon(reg) {
        iconUrl = "marker.png"
        iconSize = LPoint(reg, 16, 16)
        iconAnchor = LPoint(reg, 8, 8)
    }

    @ClientCallable
    fun addZone(e: JsonObject) {
        logger.info("Add zone $e")
        val lat = e.getNumber("lat")
        val lng = e.getNumber("lng")

        if (activeZone == null) {
            // новая зона
            val zoneId = UUID.randomUUID();
            val zone = zones.putAndGet(
                zoneId, ZoneUi(
                    Zone(zoneId, Polygon(mutableListOf(Point(lat, lng)))),
                    horizontalLayout {
                        style.setBackground("#90ee9059")
                        style.setPadding("1em")
                        nativeLabel {
                            text = "Зона $zoneId"

                        }
                        nativeLabel {
                            text = "Точек: "
                            nativeLabel(0.toString()) {
                                this.classNames.add("app-point-count")
                            }
                        }
                        button {
                            text = "Сохранить"
                            onLeftClick {
                                if (zones.get(zoneId)!!.vertices.size < 3) {
                                    errorNotification("Нужно не менее 3 точек!").open()
                                } else {
                                    map.off()
                                    activeZone = null
                                    zones[zoneId]!!.zoneListElement.style.setBackground(null)
                                }
                            }
                        }
                        button {
                            text = "Удалить"
                            onLeftClick {
                                map.off()
                                activeZone = null
                                with(zones.remove(zoneId)!!) {
                                    zoneListElement.removeFromParent()
                                    map.removeLayer(polygonlayer)
                                    this.vertices.forEach(map::removeLayer)
                                }
                            }
                        }
                    },
                    LPolygon(reg, mutableListOf(LLatLng(reg, lat, lng))),
                    mutableListOf()
                )
            )
            activeZone = zone
            val lMarker = LMarker(reg, lat, lng) {
                this.icon = pointIcon
            }
            map.addLayer(lMarker)
            zone.vertices.add(lMarker)
            map.addLayer(zone.polygonlayer)

            zoneListTab.add(zone.zoneListElement)
        } else {
            // +1 вершина к зоне
            val lastZone = activeZone!!
            if (!checkPolygonValid(lastZone.zone.polygon, Point(lat, lng))) {
                errorNotification("Границы зоны пересекаются").open()
                return
            }
            lastZone.zone.polygon.points.add(Point(lat, lng))
            lastZone.polygonlayer.addLatLng(LLatLng(reg, lat, lng), null)
            val polygon = reg.clientComponentJsAccessor(lastZone.polygonlayer)

            val lMarker = LMarker(reg, lat, lng) {
                this.icon = pointIcon
            }
            map.addLayer(lMarker)
            lastZone.vertices.add(lMarker)

            @Language("javascript")
            var script = """
                        document.getElementById("${lastZone.zone.uuid}-area")
                        console.info($polygon.toGeoJSON());       
                        """.trimIndent()
            UI.getCurrent().page.executeJs(script)
        }
    }


    @ClientCallable
    fun addMarker(e: JsonObject) {
        val uuid = UUID.randomUUID();
        logger.info("Add marker $e")
        val lat = e.getNumber("lat")
        val lng = e.getNumber("lng")
        val lMarker = LMarker(reg, LLatLng(reg, lat, lng))

        points.putAndGet(
            uuid,
            PointUi(uuid,
                Point(lat, lng),
                lMarker,
                with(pointList) {
                    horizontalLayout {
                        nativeLabel(lat.toString()) { }
                        nativeLabel(lng.toString()) { }
                        button("Delete") {
                            onLeftClick {
                                with(points[uuid]!!) {
                                    map.removeLayer(this.lMarker)
                                    pointList.remove(this.pointListElement)
                                }
                            }
                        }
                    }
                }
            )
        )

        map.addLayer(lMarker)
        recalculatePoints()
    }

    private fun recalculatePoints() {
        zones.values.forEach { zone ->
            val rawlabel = zone.zoneListElement.findChild { label ->
                label.classNames.contains("app-point-count")
            }
            if (rawlabel == null) return@forEach
            val label = rawlabel as NativeLabel
            var insideCount = 0
            points.values.forEach { point ->
                if (checkPointInsidePolygon(zone.zone.polygon, point.point)) {
                    insideCount++
                }
            }
            label.text = insideCount.toString()
        }
    }

}



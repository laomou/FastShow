import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

Rectangle {
    id: tile
    property int index: -1
    property string imageSource
    property bool isActive: false
    property real scale: 1.0
    property real offsetX: 0
    property real offsetY: 0
    property real rotation: 0
    property bool flip_h: false

    signal toggleActive(int index)
    signal translateUpdated(var data)
    signal zoomUpdated(var data)
    signal overlayRequested(var data)

    QtObject {
        id: internal
        property real initScale: 1.0
        property real baseScale: 1.0
        property real minScale: 0.1
        property real maxScale: 50.0
        property real scaleFactor: 1.1
        property bool dragging: false
        property point lastPos: Qt.point(0, 0)
        property int originalWidth: 0
        property int originalHeight: 0
        property var originalState: ({})
    }

    border {
        width: 1
        color: isActive ? "#FF3B30" : "#FFFFFF"
    }
    clip: true
    antialiasing: true

    function translate(dx, dy) {
        offsetX += dx
        offsetY += dy
    }

    function zoom(factor) {
        scale = Math.max(internal.minScale, Math.min(internal.maxScale,
                                                     scale * factor))
    }

    function rotate(angle) {
        rotation += angle
    }

    function flip_horizontal() {
        flip_h = !flip_h
    }

    function overlay(targetTile) {
        internal.originalState = {
            "imageSource": imageSource,
            "scale": scale,
            "offsetX": offsetX,
            "offsetY": offsetY,
            "rotation": rotation,
            "flip_h": flip_h
        }

        if (targetTile) {
            imageSource = targetTile.imageSource
            scale = targetTile.scale
            offsetX = targetTile.offsetX
            offsetY = targetTile.offsetY
            rotation = targetTile.rotation
            flip_h = targetTile.flip_h
        }
    }

    function cancelOverlay() {
        if (internal.originalState) {
            imageSource = internal.originalState.imageSource
            scale = internal.originalState.scale
            offsetX = internal.originalState.offsetX
            offsetY = internal.originalState.offsetY
            rotation = internal.originalState.rotation
            flip_h = internal.originalState.flip_h
        }
    }

    Rectangle {
        id: toolbar
        width: parent.width
        height: 40
        color: "#99000000"
        z: 1

        RowLayout {
            anchors.fill: parent
            spacing: 2
            anchors.margins: 2

            RowLayout {
                spacing: 2
                Layout.alignment: Qt.AlignLeft
                IconButton {
                    iconSource: "qrc:/images/zoom-out.svg"
                    onClicked: function () {
                        tile.zoom(1 / internal.scaleFactor)
                    }
                }
                Label {
                    text: (tile.scale * 100).toFixed(0) + "%"
                    color: "white"
                    Layout.preferredWidth: 60
                    Layout.preferredHeight: parent.height
                    Layout.alignment: Qt.AlignVCenter
                    verticalAlignment: Text.AlignVCenter
                    horizontalAlignment: Text.AlignHCenter
                }
                IconButton {
                    iconSource: "qrc:/images/zoom-in.svg"
                    onClicked: function () {
                        tile.zoom(internal.scaleFactor)
                    }
                }
            }

            RowLayout {
                spacing: 2
                Layout.alignment: Qt.AlignLeft
                IconButton {
                    iconSource: "qrc:/images/zoom-actual.svg"
                    onClicked: function () {
                        tile.scale = 1.0
                    }
                }
                IconButton {
                    iconSource: "qrc:/images/zoom-adapt.svg"
                    onClicked: function () {
                        tile.scale = internal.initScale
                    }
                }
            }

            RowLayout {
                spacing: 2
                Layout.alignment: Qt.AlignLeft
                IconButton {
                    iconSource: "qrc:/images/rotate-left.svg"
                    onClicked: function () {
                        tile.rotate(-90)
                    }
                }
                IconButton {
                    iconSource: "qrc:/images/rotate-right.svg"
                    onClicked: function () {
                        tile.rotate(90)
                    }
                }
            }

            RowLayout {
                spacing: 2
                Layout.alignment: Qt.AlignLeft
                IconButton {
                    iconSource: "qrc:/images/flip-h.svg"
                    onClicked: function () {
                        tile.flip_horizontal()
                    }
                }
            }

            RowLayout {
                spacing: 2
                Layout.alignment: Qt.AlignLeft
                IconButton {
                    visible: (index % 2 === 1)
                    iconSource: "qrc:/images/left-cmp.svg"
                    onPressed: function () {
                        overlayRequested({
                                             "sourceIndex": index,
                                             "direction": "left",
                                             "overlay": true
                                         })
                    }
                    onReleased: function () {
                        overlayRequested({
                                             "sourceIndex": index,
                                             "direction": "left",
                                             "overlay": false
                                         })
                    }
                }
                IconButton {
                    visible: (index % 2 === 0)
                    iconSource: "qrc:/images/right-cmp.svg"
                    onPressed: function () {
                        overlayRequested({
                                             "sourceIndex": index,
                                             "direction": "right",
                                             "overlay": true
                                         })
                    }
                    onReleased: function () {
                        overlayRequested({
                                             "sourceIndex": index,
                                             "direction": "right",
                                             "overlay": false
                                         })
                    }
                }
            }

            Item {
                Layout.fillWidth: true
            }
        }
    }

    Image {
        id: contentImage
        anchors {
            left: parent.left
            right: parent.right
            top: toolbar.bottom
            bottom: parent.bottom
        }
        source: imageSource
        fillMode: Image.PreserveAspectFit
        visible: status === Image.Ready
        smooth: true
        mipmap: true

        transform: [
            Scale {
                origin.x: contentImage.width / 2
                origin.y: contentImage.height / 2
                xScale: (flip_h ? -1 : 1) * (scale / internal.baseScale)
                yScale: scale / internal.baseScale
            },
            Rotation {
                origin.x: contentImage.width / 2
                origin.y: contentImage.height / 2
                angle: rotation
            },
            Translate {
                x: offsetX
                y: offsetY
            }
        ]

        onStatusChanged: {
            if (status === Image.Ready) {
                Qt.callLater(() => {
                                 internal.baseScale = Math.min(
                                     width / sourceSize.width,
                                     height / sourceSize.height)
                                 internal.initScale = Math.max(
                                     0.1,
                                     Math.round(internal.baseScale * 10) / 10)
                                 tile.scale = internal.initScale
                             })
            }
        }
    }

    MouseArea {
        anchors {
            left: parent.left
            right: parent.right
            top: toolbar.bottom
            bottom: parent.bottom
        }
        acceptedButtons: Qt.LeftButton
        hoverEnabled: true
        scrollGestureEnabled: false

        onPressed: function (event) {
            if (event.button === Qt.LeftButton) {
                internal.dragging = true
                internal.lastPos = Qt.point(event.x, event.y)
            }
        }

        onReleased: function (event) {
            internal.dragging = false
        }

        onPositionChanged: function (event) {
            if (internal.dragging) {
                const dx = event.x - internal.lastPos.x
                const dy = event.y - internal.lastPos.y
                internal.lastPos = Qt.point(event.x, event.y)

                translate(dx, dy)

                translateUpdated({
                                     "sourceIndex": index,
                                     "dx": dx,
                                     "dy": dy
                                 })
            }
        }

        onWheel: function (event) {
            const delta = event.angleDelta.y > 0 ? -1 : 1
            const factor = delta > 0 ? 1 / internal.scaleFactor : internal.scaleFactor

            zoom(factor)

            zoomUpdated({
                            "sourceIndex": index,
                            "factor": factor
                        })

            event.accepted = true
        }

        onDoubleClicked: function () {
            toggleActive(index)
        }
    }
}

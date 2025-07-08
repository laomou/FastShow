import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

Rectangle {
    id: tile
    property int index: -1
    property string imageSource
    property bool isActive: false
    property real userScale: 1.0
    property real offsetX: 0
    property real offsetY: 0
    property real rotation: 0
    property bool flipH: false

    signal toggleActive(int index)
    signal translateUpdated(var data)
    signal zoomUpdated(var data)
    signal overlayRequested(var data)

    QtObject {
        id: internal
        property real baseScale: 1.0
        property real minScale: 0.1
        property real maxScale: 50.0
        property real scaleFactor: 1.1
        property int imgWidth: 0
        property int imgHeight: 0
        property real scaleWidth: 1.0
        property real scaleHeight: 1.0
        property var originalState: ({})
    }

    border {
        width: 1
        color: isActive ? "#FF3B30" : "#FFFFFF"
    }
    clip: true

    function clamp(min, max, v) {
        return v < min ? min : (v > max ? max : v)
    }

    function translate(dx, dy) {
        const scaledW = internal.scaleWidth
        const scaledH = internal.scaleHeight
        let dispW, dispH
        if (rotation % 180 === 0) {
            dispW = scaledW
            dispH = scaledH
        } else {
            dispW = scaledH
            dispH = scaledW
        }

        const offsetLimitX = Math.max((dispW - img.width) / 2, 0)
        const offsetLimitY = Math.max((dispH - img.height) / 2, 0)

        offsetX = clamp(-offsetLimitX, offsetLimitX, offsetX + dx)
        offsetY = clamp(-offsetLimitY, offsetLimitY, offsetY + dy)
    }

    function zoom(factor) {
        userScale = clamp(internal.minScale, internal.maxScale,
                          userScale * factor)

        if (userScale <= internal.baseScale) {
            offsetX = offsetY = 0
        }

        internal.scaleWidth = internal.imgWidth * userScale
        internal.scaleHeight = internal.imgHeight * userScale
    }

    function rotate(angle) {
        rotation = (rotation + angle) % 360
        if (rotation < 0) {
            rotation += 360
        }
    }

    function flip_horizontal() {
        flipH = !flipH
    }

    function overlay(targetTile) {
        internal.originalState = {
            "imageSource": imageSource,
            "userScale": userScale,
            "offsetX": offsetX,
            "offsetY": offsetY,
            "rotation": rotation,
            "flipH": flipH
        }

        if (targetTile) {
            imageSource = targetTile.imageSource
            userScale = targetTile.userScale
            offsetX = targetTile.offsetX
            offsetY = targetTile.offsetY
            rotation = targetTile.rotation
            flipH = targetTile.flipH
        }
    }

    function cancelOverlay() {
        var s = internal.originalState
        if (s) {
            imageSource = s.imageSource
            userScale = s.userScale
            offsetX = s.offsetX
            offsetY = s.offsetY
            rotation = s.rotation
            flipH = s.flipH
        }
    }

    Image {
        id: img
        anchors.fill: tile
        source: imageSource
        fillMode: Image.PreserveAspectFit
        visible: status === Image.Ready
        smooth: true
        mipmap: true

        transform: Matrix4x4 {
            matrix: {
                var m = Qt.matrix4x4()
                const cx = width / 2
                const cy = height / 2
                var localOffsetX = tile.offsetX, localOffsetY = tile.offsetY
                m.translate(Qt.vector3d(cx, cy, 0))
                if (tile.flipH) {
                    m.scale(-1, 1, 1)
                }
                m.rotate(tile.rotation, Qt.vector3d(0, 0, 1))
                m.scale(tile.userScale / internal.baseScale,
                        tile.userScale / internal.baseScale, 1)
                m.translate(Qt.vector3d(-cx, -cy, 0))
                if (tile.flipH) {
                    localOffsetX *= -1
                }
                if (tile.rotation === 90) {
                    localOffsetX = tile.offsetY
                    localOffsetY = -tile.offsetX
                } else if (tile.rotation === 180) {
                    localOffsetX = -tile.offsetX
                    localOffsetY = -tile.offsetY
                } else if (tile.rotation === 270) {
                    localOffsetX = -tile.offsetY
                    localOffsetY = tile.offsetX
                }
                m.translate(Qt.vector3d(localOffsetX, localOffsetY, 0))
                return m
            }
        }

        onStatusChanged: {
            if (status === Image.Ready) {
                Qt.callLater(() => {
                                 internal.imgWidth = sourceSize.width
                                 internal.imgHeight = sourceSize.height
                                 internal.baseScale = Math.min(
                                     img.width / sourceSize.width,
                                     img.height / sourceSize.height)
                                 tile.userScale = internal.baseScale
                             })
            }
        }
    }

    Rectangle {
        anchors.fill: tile
        color: "transparent"
        visible: isActive
        Rectangle {
            width: 1
            height: 24
            color: "red"
            anchors.verticalCenter: parent.verticalCenter
            anchors.horizontalCenter: parent.horizontalCenter
        }

        Rectangle {
            width: 24
            height: 1
            color: "red"
            anchors.horizontalCenter: parent.horizontalCenter
            anchors.verticalCenter: parent.verticalCenter
        }
    }

    MouseArea {
        anchors {
            left: tile.left
            right: tile.right
            top: toolbar.bottom
        }
        height: tile.height - toolbar.height
        acceptedButtons: Qt.LeftButton
        hoverEnabled: true
        scrollGestureEnabled: false

        property bool isDragging: false
        property point lastPos: Qt.point(0, 0)

        onPressed: function (event) {
            if (event.button === Qt.LeftButton) {
                isDragging = true
                lastPos = Qt.point(event.x, event.y)
            }
        }

        onReleased: function (event) {
            isDragging = false
        }

        onPositionChanged: function (event) {
            if (isDragging) {
                const dx = event.x - lastPos.x
                const dy = event.y - lastPos.y
                lastPos = Qt.point(event.x, event.y)

                translate(dx, dy)

                translateUpdated({
                                     "sourceIndex": index,
                                     "dx": dx,
                                     "dy": dy
                                 })
            }
        }

        onWheel: function (event) {
            const factor = event.angleDelta.y > 0 ? internal.scaleFactor : 1 / internal.scaleFactor

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

    Rectangle {
        id: toolbar
        width: tile.width
        height: 32
        color: "#99000000"

        RowLayout {
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
                    text: (tile.userScale * 100).toFixed(0) + "%"
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
                        tile.userScale = 1.0
                        tile.offsetX = tile.offsetY = 0
                    }
                }
                IconButton {
                    iconSource: "qrc:/images/zoom-adapt.svg"
                    onClicked: function () {
                        tile.userScale = internal.baseScale
                        tile.offsetX = tile.offsetY = 0
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
                Layout.fillHeight: true
            }
        }
    }
}

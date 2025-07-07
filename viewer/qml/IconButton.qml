import QtQuick

Rectangle {
    id: root
    property alias iconSource: icon.source
    property alias iconWidth: icon.width
    property alias iconHeight: icon.height
    signal clicked()
    signal pressed()
    signal released()

    width: 32
    height: 32
    radius: 4
    color: mouseArea.pressed ? "#dddddd" : "transparent"
    border.color: mouseArea.hovered ? "#cccccc" : "transparent"
    border.width: 1

    Image {
        id: icon
        anchors.centerIn: parent
        width: 24
        height: 24
        source: ""
    }

    MouseArea {
        id: mouseArea
        anchors.fill: parent
        hoverEnabled: true
        onClicked: root.clicked()
        onPressed: root.pressed()
        onReleased: root.released()
    }
}

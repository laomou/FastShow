import QtQuick
import QtQuick.Window
import QtQuick.Controls
import QtQuick.Layouts

Window {
    id: root
    width: 800
    height: 600
    visible: true
    title: "FastShow Image Viewer"

    RowLayout {
        anchors.fill: parent
        spacing: 2

        ScrollView {
            Layout.preferredWidth: 300
            Layout.fillHeight: true
            clip: true

            TreeView {
                model: folderModel

                delegate: Item {
                    required property string name

                    Row {
                        Text {
                            text: name
                        }
                    }

                    TapHandler {
                        onTapped: {
                            folderModel.fetchMore(model.index)
                        }
                    }
                }
            }
        }

        ColumnLayout {
            Layout.fillWidth: true
            Layout.fillHeight: true
            spacing: 2

            RowLayout {
                Layout.fillWidth: true
                Layout.preferredHeight: 40

                Button {
                    text: "↑"
                }

                TextField {
                    id: addressBar
                    Layout.fillWidth: true
                    placeholderText: "Path"
                    text: ""
                }

                TextField {
                    id: searchBox
                    Layout.preferredWidth: 150
                    placeholderText: "Search..."
                }
            }
        }
    }
}

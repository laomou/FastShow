#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include "folder_model.h"

int main(int argc, char *argv[]) {
    QGuiApplication app(argc, argv);
    QQmlApplicationEngine engine;

    FolderModel folderModel;
    engine.rootContext()->setContextProperty("folderModel", &folderModel);

    QObject::connect(
        &engine,
        &QQmlApplicationEngine::objectCreationFailed,
        &app,
        []() { QCoreApplication::exit(-1); },
        Qt::QueuedConnection);

    engine.loadFromModule("tt", "Explorer");

    return app.exec();
}

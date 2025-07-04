#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include "rgb_image_provider.h"

int main(int argc, char *argv[]) {
    QGuiApplication app(argc, argv);
    QQmlApplicationEngine engine;

    RGBImageProvider rgb_iamge_provider;
    engine.addImageProvider("rgb", &rgb_iamge_provider);

    QObject::connect(
        &engine,
        &QQmlApplicationEngine::objectCreationFailed,
        &app,
        []() { QCoreApplication::exit(-1); },
        Qt::QueuedConnection);

    engine.loadFromModule("ui", "ImageViewer");

    return app.exec();
}

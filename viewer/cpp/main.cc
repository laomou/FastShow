#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include "rgba_image_provider.h"
#include "jraw_image_provider.h"

int main(int argc, char *argv[]) {
    QGuiApplication app(argc, argv);
    QQmlApplicationEngine engine;

    RGBAImageProvider rgba_image_provider;
    engine.addImageProvider("rgba", &rgba_image_provider);
    JRawImageProvider jraw_image_provider;
    engine.addImageProvider("jraw", &jraw_image_provider);

    QObject::connect(
        &engine,
        &QQmlApplicationEngine::objectCreationFailed,
        &app,
        []() { QCoreApplication::exit(-1); },
        Qt::QueuedConnection);

    engine.loadFromModule("ui", "ImageViewer");

    return app.exec();
}

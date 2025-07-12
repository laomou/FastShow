#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include "jraw_image_provider.h"
#include "rgba_image_provider.h"
#include "app_controller.h"


int main(int argc, char *argv[]) {
  QGuiApplication app(argc, argv);

  AppController controller;
  controller.parseArguments(app);

  QQmlApplicationEngine engine;
  engine.rootContext()->setContextProperty("appController", &controller);
  auto* rgbaProvider = new RGBAImageProvider();
  auto* jrawProvider = new JRawImageProvider();
  engine.addImageProvider("rgba", rgbaProvider);
  engine.addImageProvider("jraw", jrawProvider);

  QObject::connect(
      &engine, &QQmlApplicationEngine::objectCreationFailed, &app,
      []() { QCoreApplication::exit(-1); }, Qt::QueuedConnection);

  engine.loadFromModule("ui", "ImageViewer");

  return app.exec();
}

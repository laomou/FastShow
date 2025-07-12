#include "app_controller.h"
#include <QDir>

void AppController::parseArguments(const QCoreApplication& app) {
    QCommandLineParser parser;
    parser.setApplicationDescription("Image Viewer");
    parser.addHelpOption();

    QCommandLineOption imgOption("img", "Load image files (supports multiple)", "imgFile");
    QCommandLineOption rawOption("raw", "Load raw image files (supports multiple)", "rawFile");
    parser.addOption(imgOption);
    parser.addOption(rawOption);

    if (!parser.parse(app.arguments())) {
        qWarning() << parser.errorText();
        parser.showHelp(1);
    }

    if (parser.isSet("help")) {
        parser.showHelp(0);
    }

    auto processPaths = [this](const QStringList& paths, const QString& provider) {
        for (const QString &path : paths) {
            QFileInfo info(path);
            if (!info.exists()) {
                qWarning() << "File not found:" << path;
                continue;
            }

            QString filePath = info.absoluteFilePath();
            m_imagePaths << QString("image://%1/%2").arg(provider).arg(filePath);
        }
    };

    processPaths(parser.values(imgOption), "rgba");
    processPaths(parser.values(rawOption), "jraw");
}

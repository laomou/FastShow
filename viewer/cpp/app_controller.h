#ifndef APP_CONTROLLER_H
#define APP_CONTROLLER_H

#include <QCommandLineParser>
#include <QFileInfo>
#include <QCommandLineOption>

class AppController : public QObject {
    Q_OBJECT
    Q_PROPERTY(QStringList imagePaths READ imagePaths CONSTANT)
public:
    QStringList m_imagePaths;

    QStringList imagePaths() const { return m_imagePaths; }

    void parseArguments(const QCoreApplication& app);
};

#endif // APP_CONTROLLER_H

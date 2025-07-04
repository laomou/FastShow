#ifndef FOLDER_NODE_H
#define FOLDER_NODE_H

#include <QString>
#include <QVector>

struct FolderNode {
    QString name;
    QString path;
    FolderNode* parent = nullptr;
    QVector<FolderNode*> children;
    bool isLoaded = false;
};

#endif // FOLDER_NODE_H
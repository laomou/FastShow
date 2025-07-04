#include "folder_model.h"

#include <QDir>
#include <QFileInfoList>

FolderModel::FolderModel(QObject* parent) : QAbstractItemModel(parent) {
  root = new FolderNode{"Root", "/", nullptr};
}

FolderModel::~FolderModel() { delete root; }

QModelIndex FolderModel::index(int row, int column, const QModelIndex& parent) const {
  FolderNode* parentNode =
      parent.isValid() ? static_cast<FolderNode*>(parent.internalPointer())
                       : root;
  if (row < 0 || row >= parentNode->children.size()) return QModelIndex();
  return createIndex(row, column, parentNode->children[row]);
}

QModelIndex FolderModel::parent(const QModelIndex& index) const {
  if (!index.isValid()) return QModelIndex();
  FolderNode* child = static_cast<FolderNode*>(index.internalPointer());
  FolderNode* parent = child->parent;
  if (!parent || parent == root) return QModelIndex();
  FolderNode* grandparent = parent->parent;
  int row = grandparent ? grandparent->children.indexOf(parent) : 0;
  return createIndex(row, 0, parent);
}

int FolderModel::rowCount(const QModelIndex& parent) const {
  FolderNode* node = parent.isValid()
                         ? static_cast<FolderNode*>(parent.internalPointer())
                         : root;
  return node->children.size();
}

int FolderModel::columnCount(const QModelIndex&) const { return 1; }

QVariant FolderModel::data(const QModelIndex& index, int role) const {
  if (!index.isValid()) return QVariant();
  FolderNode* node = static_cast<FolderNode*>(index.internalPointer());
  if (role == Qt::DisplayRole || role == Qt::UserRole + 1) return node->name;
  if (role == Qt::UserRole + 2) return node->path;
  return QVariant();
}

QHash<int, QByteArray> FolderModel::roleNames() const {
  return {{Qt::UserRole + 1, "name"}, {Qt::UserRole + 2, "path"}};
}

bool FolderModel::canFetchMore(const QModelIndex& parent) const {
  FolderNode* node = parent.isValid()
                         ? static_cast<FolderNode*>(parent.internalPointer())
                         : root;
  return !node->isLoaded;
}

void FolderModel::fetchMore(const QModelIndex& parent) {
  FolderNode* node = parent.isValid()
                         ? static_cast<FolderNode*>(parent.internalPointer())
                         : root;
  if (node->isLoaded) return;

  QDir dir(node->path);
  QFileInfoList entries = dir.entryInfoList(QDir::Dirs | QDir::NoDotAndDotDot);
  beginInsertRows(parent, 0, entries.size() - 1);
  for (const QFileInfo& info : entries) {
    auto* child =
        new FolderNode{info.fileName(), info.absoluteFilePath(), node};
    node->children.append(child);
  }
  endInsertRows();
  node->isLoaded = true;
}


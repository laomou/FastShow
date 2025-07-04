#ifndef FOLDER_MODEL_H
#define FOLDER_MODEL_H

#include <QAbstractItemModel>
#include "folder_node.h"
#include <memory>

class FolderModel : public QAbstractItemModel {
  Q_OBJECT
 public:
  explicit FolderModel(QObject* parent = nullptr);
  ~FolderModel();

  QModelIndex index(int row, int column, const QModelIndex& parent = QModelIndex()) const override;
  QModelIndex parent(const QModelIndex& index) const override;
  int rowCount(const QModelIndex& parent = QModelIndex()) const override;
  int columnCount(const QModelIndex& parent = QModelIndex()) const override;
  QVariant data(const QModelIndex& index, int role = Qt::DisplayRole) const override;
  QHash<int, QByteArray> roleNames() const override;
  bool canFetchMore(const QModelIndex& parent) const override;
  void fetchMore(const QModelIndex& parent) override;

 private:
  FolderNode* root;
  void loadChildren(FolderNode* node);
};

#endif // FOLDER_MODEL_H

package b6.website.rpc;

import b6.persistence.CatalogDao;
import b6.persistence.model.SortType;
import b6.persistence.model.generated.B6DB;
import b6.rpc.model.catalog.Catalog;
import b6.website.util.Id2Str;
import com.google.protobuf.ByteString;
import com.sleepycat.je.Environment;
import com.sleepycat.je.Transaction;
import com.truward.bdb.transaction.BdbTransactionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexander Shabanov
 */
public final class DefaultCatalogRestService extends BdbTransactionSupport implements CatalogRestService {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final CatalogDao catalogDao;

  public DefaultCatalogRestService(@Nonnull Environment environment, @Nonnull CatalogDao catalogDao) {
    super(environment);
    this.catalogDao = catalogDao;
  }

  @Override
  public Catalog.GetItemReply getItem(Catalog.GetItemRequest request) {
    final ByteString id = Id2Str.toByteString(request.getId());

    return withTransaction((tx) -> {
      final B6DB.CatalogItemResult itemResult = catalogDao.getById(tx, id);
      return Catalog.GetItemReply.newBuilder()
          .setItem(getCatalogItem(tx, itemResult))
          .build();
    });
  }

  @Override
  public Catalog.GetItemsReply getItems(Catalog.GetItemsRequest request) {
    final ByteString relatedItemId = Id2Str.toByteString(request.getRelatedItemId());
    final ByteString startItemId = Id2Str.toByteString(request.getCursor());

    return withTransaction(tx -> {
      final List<B6DB.CatalogItemResult> items = catalogDao.getCatalogItems(tx, relatedItemId, startItemId,
          request.getNameFilter(), request.getTypeFilter(), toSortType(request.getSortType()), request.getLimit());

      final String cursor;
      if (request.getLimit() > 0 && request.getLimit() == items.size()) {
        cursor = Id2Str.fromByteString(items.get(items.size() - 1).getId());
      } else {
        cursor = "";
      }

      return Catalog.GetItemsReply.newBuilder()
          .addAllItems(items.stream().map(i -> getCatalogItem(tx, i)).collect(Collectors.toList()))
          .setCursor(cursor)
          .build();
    });
  }

  @Override
  public Catalog.SetFavoriteReply setFavorite(Catalog.SetFavoriteRequest request) {
    log.warn("setFavorite - not implemented");
    return Catalog.SetFavoriteReply.newBuilder().setIsFavorite(request.getIsFavorite()).build();
  }

  @Override
  public Catalog.GetFavoriteItemsReply getFavoriteItems(Catalog.GetFavoriteItemsRequest request) {
    log.warn("getFavoriteItems - not implemented");
    return Catalog.GetFavoriteItemsReply.newBuilder()
        .build();
  }

  //
  // Private
  //

  SortType toSortType(Catalog.SortType sortType) {
    switch (sortType) {
      case DEFAULT:
        return SortType.DEFAULT;

      case TITLE_DESCENDING:
        return SortType.TITLE_DESCENDING;

      case TITLE_ASCENDING:
        return SortType.TITLE_ASCENDING;

      default:
        throw new UnsupportedOperationException("Unsupported sortType=" + sortType);
    }
  }

  // VisibleForTesting
  Catalog.CatalogItem getCatalogItem(Transaction tx, B6DB.CatalogItemResult catalogItem) {
    final Catalog.CatalogItem.Builder builder = Catalog.CatalogItem.newBuilder();

    if (catalogItem.hasBook()) {
      final Catalog.Book.Builder bookBuilder = Catalog.Book.newBuilder()
          .addAllDownloadItems(catalogItem.getBook().getDownloadItemsList().stream()
              .map(this::toRpcDownloadItem).collect(Collectors.toList()));

      final List<B6DB.Relation> relations = catalogDao.getRelationsTo(tx, catalogItem.getId(), 0, Integer.MAX_VALUE);
      for (final B6DB.Relation relation : relations) {
        switch (relation.getType()) {
          case CatalogDao.LANGUAGE_TYPE:
            bookBuilder.setLanguage(getNamed(tx, relation.getFromId()));
            break;

          case CatalogDao.GENRE_TYPE:
            bookBuilder.addGenres(getNamed(tx, relation.getFromId()));
            break;

          default:
            // TODO: authors, etc...
        }
      }

      builder.setBook(bookBuilder.build());
    }

    return builder
        .setId(Id2Str.fromByteString(catalogItem.getId()))
        .setType(catalogItem.getItem().getType())
        .setTitle(catalogItem.getItem().getTitle())
        .build();
  }

  Catalog.Named getNamed(Transaction tx, ByteString key) {
    final B6DB.CatalogItemResult catalogItem = catalogDao.getById(tx, key);
    return Catalog.Named.newBuilder()
        .setId(Id2Str.fromByteString(catalogItem.getId()))
        .setTitle(catalogItem.getItem().getTitle())
        .build();
  }

  // VisibleForTesting
  Catalog.DownloadItem toRpcDownloadItem(B6DB.DownloadItem downloadItem) {
    return Catalog.DownloadItem.newBuilder()
        .setDescriptorText(downloadItem.getDescriptorText())
        .setDownloadUrl("https://www.amazon.com/robots.txt")
        .setFileSize(downloadItem.getFileSize())
        .build();
  }
}

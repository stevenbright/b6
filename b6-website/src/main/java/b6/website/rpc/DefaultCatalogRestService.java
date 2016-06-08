package b6.website.rpc;

import b6.model.catalog.Book;
import b6.model.catalog.CatalogItem;
import b6.model.catalog.DownloadItem;
import b6.model.catalog.Named;
import b6.rpc.model.catalog.Catalog;
import b6.website.model.catalog.SortType;
import b6.website.service.CatalogService;
import b6.website.util.Id2Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Alexander Shabanov
 */
public final class DefaultCatalogRestService implements CatalogRestService {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final CatalogService catalogService;

  public DefaultCatalogRestService(@Nonnull CatalogService catalogService) {
    this.catalogService = Objects.requireNonNull(catalogService, "catalogService");
  }

  @Override
  public Catalog.GetItemReply getItem(Catalog.GetItemRequest request) {
    final long id = Id2Str.toLong(request.getId());
    final CatalogItem item = catalogService.getItem(id);
    return Catalog.GetItemReply.newBuilder()
        .setItem(toRpcCatalogItem(item))
        .build();
  }

  @Override
  public Catalog.GetItemsReply getItems(Catalog.GetItemsRequest request) {
    final List<CatalogItem> items = catalogService.getItems(Id2Str.toLong(request.getRelatedItemId()),
        Id2Str.toLong(request.getCursor()), request.getNameFilter(), request.getTypeFilter(),
        toSortType(request.getSortType()), request.getLimit());

    final String cursor;
    if (request.getLimit() > 0 && request.getLimit() == items.size()) {
      cursor = Id2Str.fromLong(items.get(items.size() - 1).getId());
    } else {
      cursor = "";
    }

    return Catalog.GetItemsReply.newBuilder()
        .setCursor(cursor)
        .addAllItems(items.stream().map(this::toRpcCatalogItem).collect(Collectors.toList()))
        .build();
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
  Catalog.CatalogItem toRpcCatalogItem(CatalogItem catalogItem) {
    final Catalog.CatalogItem.Builder builder = Catalog.CatalogItem.newBuilder();
    if (catalogItem.getExtension() instanceof Book) {
      final Book book = (Book) catalogItem.getExtension();
      final Catalog.Book.Builder bookBuilder = Catalog.Book.newBuilder()
          .addAllAuthors(toRpcNamedList(book.getAuthors()))
          .addAllGenres(toRpcNamedList(book.getGenres()))
          .addAllOrigins(book.getDownloadItems()
              .stream().map(di -> toRpcNamed(di.getOrigin())).collect(Collectors.toList()))
          .addAllDownloadItems(book.getDownloadItems()
              .stream().map(this::toRpcDownloadItem).collect(Collectors.toList()));

      if (!book.getLanguages().isEmpty()) {
        // TODO: make list
        bookBuilder.setLanguage(toRpcNamed(book.getLanguages().get(0)));
      } else {
        log.error("Can't assign language using item={}", catalogItem);
      }

      builder.setBook(bookBuilder.build());
    } else if (catalogItem.getExtension() != null) {
      throw new IllegalStateException("Unknown extension of catalogItem=" + catalogItem);
    }

    return builder
        .setId(Id2Str.fromLong(catalogItem.getId()))
        .setType(catalogItem.getType())
        .setTitle(catalogItem.getTitle())
        .build();
  }

  // VisibleForTesting
  Catalog.DownloadItem toRpcDownloadItem(DownloadItem downloadItem) {
    return Catalog.DownloadItem.newBuilder()
        .setDescriptorText("FB2")
        .setDownloadUrl("https://www.amazon.com/robots.txt")
        .setFileSize(downloadItem.getFileSize())
        .build();
  }

  // VisibleForTesting
  static List<Catalog.Named> toRpcNamedList(List<Named> namedList) {
    return namedList.stream().map(DefaultCatalogRestService::toRpcNamed).collect(Collectors.toList());
  }

  // VisibleForTesting
  static Catalog.Named toRpcNamed(Named named) {
    return Catalog.Named.newBuilder().setId(Id2Str.fromLong(named.getId())).setTitle(named.getTitle()).build();
  }
}

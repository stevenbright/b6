package b6.website.rpc;

import b6.catalog.model.Catalog;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alexander Shabanov
 */
public final class StubCatalogRestService implements CatalogRestService {

  public static final int DEFAULT_LIMIT = 16;

  private final List<Catalog.CatalogItem> items = new ArrayList<>(100);
  private final Map<String, Set<String>> relations = new HashMap<>(100);

  public StubCatalogRestService() {
    addSampleItems();
  }

  @Override
  public Catalog.GetItemReply getItem(Catalog.GetItemRequest request) {
    final Catalog.GetItemReply.Builder replyBuilder = Catalog.GetItemReply.newBuilder();
    final String id = request.getId();

    for (final Catalog.CatalogItem item : items) {
      if (item.getId().equals(id)) {
        replyBuilder.setItem(item);
        break;
      }
    }

    return replyBuilder.build();
  }

  @Override
  public Catalog.GetItemsReply getItems(Catalog.GetItemsRequest request) {
    if (request.getLimit() < 0) {
      throw new IllegalArgumentException("Invalid limit=" + request.getLimit());
    }

    final Catalog.GetItemsReply.Builder builder = Catalog.GetItemsReply.newBuilder();
    final int size = request.getLimit() == 0 ? DEFAULT_LIMIT : Math.min(DEFAULT_LIMIT, request.getLimit());
    final Catalog.SortType sortType = request.getSortType() == null ? Catalog.SortType.DEFAULT : request.getSortType();
    final Optional<Catalog.CatalogItem> cursorItem = items.stream()
        .filter(item -> item.getId().equals(request.getCursor())).findFirst();
    final Set<String> relatedItemIds = relations.get(request.getRelatedItemId());

    final List<Catalog.CatalogItem> resultItems = items
        .stream()
        .filter(item -> {
          if ((StringUtils.hasLength(request.getTypeFilter()) && !item.getType().equals(request.getTypeFilter())) ||
              (StringUtils.hasLength(request.getNameFilter()) && !item.getTitle().startsWith(request.getNameFilter())) ||
              ((relatedItemIds != null) && !relatedItemIds.contains(item.getId()))) {
            return false;
          }

          if (!cursorItem.isPresent()) {
            return true;
          } else if (sortType == Catalog.SortType.TITLE_ASCENDING) {
            return cursorItem.get().getTitle().compareTo(item.getTitle()) < 0;
          } else if (sortType == Catalog.SortType.TITLE_DESCENDING) {
            return cursorItem.get().getTitle().compareTo(item.getTitle()) > 0;
          }

          return cursorItem.get().getId().compareTo(item.getId()) < 0; // start with IDs greater than given one
        })
        .sorted((o1, o2) -> {
          switch (sortType) {
            case TITLE_ASCENDING:
              return o1.getTitle().compareTo(o2.getTitle());
            case TITLE_DESCENDING:
              return o2.getTitle().compareTo(o1.getTitle());
          }
          return o1.getId().compareTo(o2.getId());
        })
        .limit(size)
        .collect(Collectors.toList());

    if (resultItems.size() == size) {
      builder.setCursor(resultItems.get(size - 1).getId());
    }

    return builder.addAllItems(resultItems).build();
  }

  @Override
  public Catalog.SetFavoriteReply setFavorite(Catalog.SetFavoriteRequest request) {
    final boolean isFavorite = request.getIsFavorite();
    final String itemId = request.getItemId();

    final Optional<Catalog.CatalogItem> itemOpt = items.stream().filter(i -> i.getId().equals(itemId)).findFirst();
    if (itemOpt.isPresent()) {
      final Catalog.CatalogItem oldItem = itemOpt.get();
      final int index = items.indexOf(oldItem);
      if (index < 0) {
        throw new IllegalStateException("index < 0");
      }

      items.set(index, Catalog.CatalogItem.newBuilder(oldItem).setIsFavorite(isFavorite).build());
    }

    return Catalog.SetFavoriteReply.newBuilder()
        .setIsFavorite(isFavorite)
        .build();
  }

  @Override
  public Catalog.GetFavoriteItemsReply getFavoriteItems(Catalog.GetFavoriteItemsRequest request) {
    final int size = request.getLimit() == 0 ? DEFAULT_LIMIT : Math.min(DEFAULT_LIMIT, request.getLimit());
    final String cursor = request.getCursor();

    final List<Catalog.CatalogItem> resultItems = items
        .stream()
        .filter(item -> !((cursor != null) && !(cursor.compareTo(item.getId()) < 0)) && item.getIsFavorite())
        .sorted((lhs, rhs) -> lhs.getId().compareTo(rhs.getId()))
        .limit(size)
        .collect(Collectors.toList());

    final Catalog.GetFavoriteItemsReply.Builder resultBuilder = Catalog.GetFavoriteItemsReply.newBuilder()
        .addAllItems(resultItems);
    if (resultItems.size() == size) {
      resultBuilder.setCursor(resultItems.get(size - 1).getId());
    }

    return resultBuilder.build();
  }

  //
  // Private
  //

  private Catalog.CatalogItem.Builder catalogItemBuilder(String id, String type, String title) {
    return Catalog.CatalogItem.newBuilder()
        .setId(id)
        .setType(type)
        .setTitle(title);
  }

  private Catalog.CatalogItem catalogItem(String id, String type, String title) {
    return catalogItemBuilder(id, type, title).build();
  }

  private Catalog.Named lookupNamed(String id) {
    for (final Catalog.CatalogItem item : items) {
      if (id.equals(item.getId())) {
        return named(item.getId(), item.getTitle());
      }
    }
    throw new IllegalStateException("No item with id=" + id);
  }

  private Catalog.Named named(String id, String title) {
    return Catalog.Named.newBuilder().setId(id).setTitle(title).build();
  }

  private void addSampleItems() {
    items.clear();

    items.add(catalogItem("AEN", "language", "en"));
    items.add(catalogItem("ARU", "language", "ru"));

    items.add(catalogItem("A0020", "genre", "sci-fi"));
    items.add(catalogItem("A0030", "genre", "novel"));
    items.add(catalogItem("A0025", "genre", "short"));

    items.add(catalogItem("A1001", "person", "Jack London"));
    items.add(catalogItem("A1002", "person", "Edgar Poe"));
    items.add(catalogItem("A1003", "person", "Stephen King"));
    items.add(catalogItem("A1004", "person", "Joe Hill"));
    items.add(catalogItem("A1005", "person", "Arkady Strugatsky"));
    items.add(catalogItem("A1006", "person", "Boris Strugatsky"));
    items.add(catalogItem("A1007", "person", "Victor Pelevin"));
    items.add(catalogItem("A1008", "person", "Jason Ciaramella"));

    items.add(catalogItem("A0730", "origin", "librus"));
    items.add(catalogItem("A0725", "origin", "wiki"));

    // Pelevin Books
    items.add(catalogItemBuilder("A9108", "book", "Hermit and Sixfinger").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1007"))
        .build()).build());

    items.add(catalogItemBuilder("A9109", "book", "The Yellow Arrow").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1007"))
        .build()).build());

    items.add(catalogItemBuilder("A9110", "book", "Pineapple Water for the Fair Lady").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1007"))
        .build()).build());

    items.add(catalogItemBuilder("A9111", "book", "Empire V").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1007"))
        .build()).build());

    items.add(catalogItemBuilder("A9112", "book", "Batman Apollo").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1007"))
        .build()).build());

    items.add(catalogItemBuilder("A9113", "book", "Buddha's Little Finger").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1007"))
        .build()).build());

    items.add(catalogItemBuilder("A9114", "book", "Babylon").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1007"))
        .build()).build());

    items.add(catalogItemBuilder("A9115", "book", "t").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1007"))
        .build()).build());

    items.add(catalogItemBuilder("A9116", "book", "S.N.U.F.F.").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1007"))
        .build()).build());

    items.add(catalogItemBuilder("A9117", "book", "Numbers").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1007"))
        .build()).build());

    // Strugatsky Books
    items.add(catalogItemBuilder("A9030", "book", "From Beyond").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());

    items.add(catalogItemBuilder("A9031", "book", "The Land of Crimson Clouds").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());

    items.add(catalogItemBuilder("A9033", "book", "The Way to Amalthea").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());

    items.add(catalogItemBuilder("A9034", "book", "Space Apprentice").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());

    items.add(catalogItemBuilder("A9035", "book", "Escape Attempt").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());

    items.add(catalogItemBuilder("A9036", "book", "Far Rainbow").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());

    items.add(catalogItemBuilder("A9005", "book", "Hard to Be a God").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .addOrigins(lookupNamed("A0730"))
        .addDownloadItems(Catalog.DownloadItems.newBuilder()
            .setDownloadUrl("https://www.quora.com/robots.txt")
            .setFileSize(6540)
            .setDescriptorText("FB2"))
        .addDownloadItems(Catalog.DownloadItems.newBuilder()
            .setDownloadUrl("https://www.google.com/robots.txt")
            .setFileSize(3245)
            .setDescriptorText("TXT"))
        .build()).build());

    items.add(catalogItemBuilder("A9038", "book", "Monday Begins on Saturday").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());

    items.add(catalogItemBuilder("A9039", "book", "The Ugly Swans").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());

    items.add(catalogItemBuilder("A9040", "book", "Tale of the Troika").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());

    items.add(catalogItemBuilder("A9041", "book", "The Dead Montaineer's Hotel").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());

    items.add(catalogItemBuilder("A9042", "book", "Roadside Picnic").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());

    items.add(catalogItemBuilder("A9043", "book", "Definitely Maybe").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0020")).addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("ARU"))
        .addAuthors(lookupNamed("A1005")).addAuthors(lookupNamed("A1006"))
        .build()).build());


    // Jack London's books
    items.add(catalogItemBuilder("A9000", "book", "White Fang").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build()).build());

    items.add(catalogItemBuilder("A9001", "book", "The Sea Wolf").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build()).build());

    items.add(catalogItemBuilder("A9002", "book", "Martin Eden").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build()).build());

    items.add(catalogItemBuilder("A9010", "book", "The Star Rover").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build()).build());

    items.add(catalogItemBuilder("A9012", "book", "The People of the Abyss").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build()).build());

    items.add(catalogItemBuilder("A9013", "book", "Burning Daylight").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build()).build());

    items.add(catalogItemBuilder("A9014", "book", "Before Adam").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build()).build());

    items.add(catalogItemBuilder("A9015", "book", "South Sea Tales").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build()).build());

    // shuffle items in a collection
    Collections.shuffle(items);

    checkForDuplicates();
    buildRelations();
  }

  private void checkForDuplicates() {
    final Map<String, Catalog.CatalogItem> idMap = new HashMap<>(items.size() * 2);
    for (final Catalog.CatalogItem item : items) {
      final Catalog.CatalogItem oldItem = idMap.put(item.getId(), item);
      if (oldItem != null) {
        throw new AssertionError("Duplicate id=" + item.getId() + ", old=" + oldItem + ", new=" + item);
      }
    }
  }

  private void buildRelations() {
    relations.clear();

    for (final Catalog.CatalogItem item : items) {
      if (!item.hasBook()) {
        continue;
      }

      final Catalog.Book book = item.getBook();
      addBookRelation(relations, item.getId(), book.getAuthorsList());
      addBookRelation(relations, item.getId(), book.getOriginsList());
      addBookRelation(relations, item.getId(), book.getGenresList());
      addBookRelation(relations, item.getId(), Collections.singletonList(book.getLanguage()));
    }
  }

  private static void addBookRelation(Map<String, Set<String>> relations,
                                      String target,
                                      Iterable<Catalog.Named> sources) {
    for (final Catalog.Named source : sources) {
      Set<String> ids = relations.get(source.getId());
      if (ids == null) {
        ids = new HashSet<>();
        relations.put(source.getId(), ids);
      }

      ids.add(target);
    }
  }
}

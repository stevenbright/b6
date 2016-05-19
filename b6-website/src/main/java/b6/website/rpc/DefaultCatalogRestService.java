package b6.website.rpc;

import b6.catalog.model.Catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexander Shabanov
 */
public final class DefaultCatalogRestService implements CatalogRestService {
  public static final int DEFAULT_LIMIT = 5;

  private List<Catalog.CatalogItem> items = new ArrayList<>();

  public DefaultCatalogRestService() {
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

    final int size = request.getLimit() == 0 ? DEFAULT_LIMIT : Math.min(DEFAULT_LIMIT, request.getLimit());
    final List<Catalog.CatalogItem> resultItems = items
        .stream()
        .filter(item -> request.getCursor() == null || request.getCursor().compareTo(item.getId()) < 0)
        .sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
        .limit(size)
        .collect(Collectors.toList());
    final String resultCursor = (resultItems.size() == size ? resultItems.get(size - 1).getId() : null);

    return Catalog.GetItemsReply.newBuilder()
        .addAllItems(resultItems)
        .setCursor(resultCursor)
        .build();
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


    items.add(catalogItemBuilder("A9000", "book", "White Fang").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build())
        .build());

    items.add(catalogItemBuilder("A9001", "book", "The Sea Wolf").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build())
        .build());

    items.add(catalogItemBuilder("A9002", "book", "Martin Eden").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build())
        .build());

    items.add(catalogItemBuilder("A9010", "book", "The Star Rover").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build())
        .build());

    items.add(catalogItemBuilder("A9012", "book", "The People of the Abyss").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build())
        .build());

    items.add(catalogItemBuilder("A9013", "book", "Burning Daylight").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build())
        .build());

    items.add(catalogItemBuilder("A9014", "book", "Before Adam").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build())
        .build());

    items.add(catalogItemBuilder("A9015", "book", "South Sea Tales").setBook(Catalog.Book.newBuilder()
        .addGenres(lookupNamed("A0030"))
        .setLanguage(lookupNamed("AEN"))
        .addAuthors(lookupNamed("A1001"))
        .build())
        .build());

    // shuffle items in a collection
    Collections.shuffle(items);
  }
}

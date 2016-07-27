package b6.persistence;

import b6.persistence.model.SortType;
import b6.persistence.model.generated.B6DB;
import b6.persistence.support.DefaultCatalogDao;
import b6.persistence.testSupport.BdbEnvironmentTestSupport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.ByteString;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author Alexander Shabanov
 */
public final class CatalogDaoTest extends BdbEnvironmentTestSupport {

  private CatalogDao catalogDao;

  private final B6DB.CatalogItemExtension itemLang = B6DB.CatalogItemExtension.newBuilder()
      .setItem(B6DB.CatalogItem.newBuilder()
          .setTitle("en")
          .setType(CatalogDao.LANGUAGE_TYPE)
          .build())
      .build();

  @Before
  public void initCatalogDao() {
    openTestEnvironment();
    catalogDao = new DefaultCatalogDao(getEnvironment(), getDatabaseConfigurer());
  }

  @Test
  public void shouldInsertAndLookupCatalogElement() {
    // Given:
    final B6DB.CatalogItemExtension item = itemLang;

    // When:
    final ByteString id = catalogDao.insert(null, item);

    // Then:
    final B6DB.CatalogItemResult actual = catalogDao.getById(null, id);
    assertSame(item, actual, id);
  }

  @Test
  public void shouldInsertAndUpdateToBook() {
    // Given:
    final B6DB.CatalogItemExtension item1 = itemLang;
    final B6DB.CatalogItemExtension item2 = itemBuilder("The Catcher", CatalogDao.BOOK_TYPE)
        .setBook(B6DB.BookExtension.newBuilder()
            .addDownloadItems(B6DB.DownloadItem.newBuilder()
                .setFileSize(1000)
                .setDescriptorText("Text")
                .setDownloadId("adsad"))
            .build())
        .build();

    // When:
    final ByteString id = catalogDao.insert(null, item1);
    assertSame(item1, catalogDao.getById(null, id), id);
    catalogDao.update(null, id, item2);

    // Then:
    final B6DB.CatalogItemResult actual = catalogDao.getById(null, id);
    assertSame(item2, actual, id);
  }

  @Test
  public void shouldRetrieveEmptyRelations() {
    assertEquals(ImmutableList.of(), catalogDao.getRelationsFrom(null, ByteString.EMPTY, 0, 10));
  }

  @Test
  public void shouldRetrieveRelations() {
    // Given:
    final ByteString idLang = catalogDao.insert(null, itemLang);
    final ByteString idGenre1 = catalogDao.insert(null, itemBuilder("fantasy", CatalogDao.GENRE_TYPE).build());
    final ByteString idGenre2 = catalogDao.insert(null, itemBuilder("novel", CatalogDao.GENRE_TYPE).build());
    final ByteString idBook1 = catalogDao.insert(null, itemBuilder("Book1", CatalogDao.BOOK_TYPE).build());
    final ByteString idBook2 = catalogDao.insert(null, itemBuilder("Book2", CatalogDao.BOOK_TYPE).build());

    // When:
    final B6DB.Relation relBook1Genre1 = insertRelation(idBook1, idGenre1, CatalogDao.GENRE_TYPE);
    final B6DB.Relation relBook1Genre2 = insertRelation(idBook1, idGenre2, CatalogDao.GENRE_TYPE);
    final B6DB.Relation relBook1Lang = insertRelation(idBook1, idLang, CatalogDao.GENRE_TYPE);
    final B6DB.Relation relBook2Lang = insertRelation(idBook2, idLang, CatalogDao.GENRE_TYPE);

    // Then:
    assertEquals(ImmutableList.of(relBook2Lang), catalogDao.getRelationsFrom(null, idBook2, 0, 2));
    final Set<B6DB.Relation> book1Rels = ImmutableSet.of(relBook1Genre1, relBook1Genre2, relBook1Lang);
    assertEquals(book1Rels, ImmutableSet.copyOf(catalogDao.getRelationsFrom(null, idBook1, 0, 4)));

    // - Pagination Test (get from-relations one by one):
    final Set<B6DB.Relation> book1RelationSet = new HashSet<>(10);
    for (int i = 0; i < 3; ++i) {
      final List<B6DB.Relation> rels = catalogDao.getRelationsFrom(null, idBook1, i, 1);
      assertEquals(1, rels.size());
      assertFalse(book1RelationSet.contains(rels.get(0)));
      assertTrue("Pagination returned duplicate relation " + rels, book1RelationSet.add(rels.get(0)));
    }
    assertEquals(book1Rels, book1RelationSet);

    // When - insert duplicate relations
    catalogDao.insertRelation(null, relBook1Genre1);
    catalogDao.insertRelation(null, relBook1Genre2);
    catalogDao.insertRelation(null, relBook1Lang);
    catalogDao.insertRelation(null, relBook2Lang);

    // Then - they should be disregarded
    assertEquals(book1Rels, ImmutableSet.copyOf(catalogDao.getRelationsFrom(null, idBook1, 0, 4)));
    assertEquals(book1Rels, ImmutableSet.copyOf(catalogDao.getRelationsFrom(null, idBook1, 0, 4)));

    // Check to-relations
    assertEquals(ImmutableSet.of(relBook1Lang, relBook2Lang), ImmutableSet.copyOf(catalogDao.getRelationsTo(null, idLang, 0, 4)));
    assertEquals(ImmutableList.of(relBook1Genre1), catalogDao.getRelationsTo(null, idGenre1, 0, 4));
    assertEquals(ImmutableList.of(relBook1Genre2), catalogDao.getRelationsTo(null, idGenre2, 0, 4));
    assertTrue(catalogDao.getRelationsTo(null, idBook1, 0, 4).isEmpty());
    assertTrue(catalogDao.getRelationsTo(null, idBook2, 0, 4).isEmpty());

    // - Pagination Test (get to-relations one by one):
    final Set<B6DB.Relation> langRelationSet = new HashSet<>(4);
    for (int i = 0; i < 2; ++i) {
      final List<B6DB.Relation> rels = catalogDao.getRelationsTo(null, idLang, i, 1);
      assertEquals(1, rels.size());
      assertFalse(langRelationSet.contains(rels.get(0)));
      assertTrue("Pagination returned duplicate relation " + rels, langRelationSet.add(rels.get(0)));
    }
    assertEquals(ImmutableSet.of(relBook1Lang, relBook2Lang), langRelationSet);
  }

  @Test
  public void shouldGetItems() {
    // End test
    assertTrue(catalogDao.getCatalogItems(null, ByteString.EMPTY, ByteString.EMPTY, "", "",
        SortType.DEFAULT, 5).isEmpty());

    // Given:
    final ByteString idLang = catalogDao.insert(null, itemLang);
    final ByteString idGenre1 = catalogDao.insert(null, itemBuilder("fantasy", CatalogDao.GENRE_TYPE).build());
    final ByteString idGenre2 = catalogDao.insert(null, itemBuilder("novel", CatalogDao.GENRE_TYPE).build());
    final ByteString idBook1 = catalogDao.insert(null, itemBuilder("Book 4", CatalogDao.BOOK_TYPE).build());
    final ByteString idBook2 = catalogDao.insert(null, itemBuilder("A Book 1", CatalogDao.BOOK_TYPE).build());
    final ByteString idBook3 = catalogDao.insert(null, itemBuilder("A Book 2", CatalogDao.BOOK_TYPE).build());
    final ByteString idBook4 = catalogDao.insert(null, itemBuilder("A Book 5", CatalogDao.BOOK_TYPE).build());
    final ByteString idBook5 = catalogDao.insert(null, itemBuilder("Book 3", CatalogDao.BOOK_TYPE).build());

    // Test:
    final List<B6DB.CatalogItemResult> items = catalogDao.getCatalogItems(null, ByteString.EMPTY, ByteString.EMPTY,
        "", "", SortType.DEFAULT, 9);

    assertEquals(8, items.size());

    insertRelation(idBook1, idGenre1, CatalogDao.GENRE_TYPE);
    insertRelation(idBook2, idGenre1, CatalogDao.GENRE_TYPE);
    insertRelation(idBook3, idGenre1, CatalogDao.GENRE_TYPE);
    insertRelation(idBook2, idGenre2, CatalogDao.GENRE_TYPE);
    insertRelation(idBook4, idGenre2, CatalogDao.GENRE_TYPE);
    insertRelation(idBook1, idLang, CatalogDao.GENRE_TYPE);
    insertRelation(idBook2, idLang, CatalogDao.GENRE_TYPE);

    final List<B6DB.CatalogItemResult> items1 = catalogDao.getCatalogItems(null, ByteString.EMPTY, ByteString.EMPTY,
        "", CatalogDao.BOOK_TYPE, SortType.TITLE_ASCENDING, 5);
    assertEquals(ImmutableList.of(idBook2, idBook3, idBook4, idBook5, idBook1), items1.stream()
        .map(B6DB.CatalogItemResult::getId).collect(Collectors.toList()));
  }

  @Test
  public void shouldAccountForCursor() {
    // Given:
    final ByteString idBook1 = catalogDao.insert(null, itemBuilder("DDD", CatalogDao.BOOK_TYPE).build());
    final ByteString idBook2 = catalogDao.insert(null, itemBuilder("CCC", CatalogDao.BOOK_TYPE).build());
    final ByteString idBook3 = catalogDao.insert(null, itemBuilder("BBB", CatalogDao.GENRE_TYPE).build());
    final ByteString idBook4 = catalogDao.insert(null, itemBuilder("AAA", CatalogDao.BOOK_TYPE).build());
    final ByteString idBook5 = catalogDao.insert(null, itemBuilder("BBB", CatalogDao.BOOK_TYPE).build());

    // When:
    final List<B6DB.CatalogItemResult> r1 = catalogDao.getCatalogItems(null, ByteString.EMPTY, ByteString.EMPTY,
        "", "", SortType.DEFAULT, 10);

    // Then:
    assertEquals(ImmutableSet.of(idBook1, idBook2, idBook3, idBook4, idBook5), r1.stream()
        .map(B6DB.CatalogItemResult::getId)
        .collect(Collectors.toSet()));

    // When:
    final List<B6DB.CatalogItemResult> r2 = catalogDao.getCatalogItems(null, ByteString.EMPTY, ByteString.EMPTY,
        "", CatalogDao.BOOK_TYPE, SortType.TITLE_ASCENDING, 10);

    // Then:
    assertEquals(ImmutableList.of(idBook4, idBook5, idBook2, idBook1), r2.stream()
        .map(B6DB.CatalogItemResult::getId)
        .collect(Collectors.toList()));

    // When:
    final List<B6DB.CatalogItemResult> r3 = catalogDao.getCatalogItems(null, ByteString.EMPTY, ByteString.EMPTY,
        "", CatalogDao.BOOK_TYPE, SortType.TITLE_DESCENDING, 10);

    // Then:
    assertEquals(ImmutableList.of(idBook1, idBook2, idBook5, idBook4), r3.stream()
        .map(B6DB.CatalogItemResult::getId)
        .collect(Collectors.toList()));

    // When:
    final List<B6DB.CatalogItemResult> r4 = catalogDao.getCatalogItems(null, ByteString.EMPTY, idBook5,
        "", CatalogDao.BOOK_TYPE, SortType.TITLE_ASCENDING, 10);

    // Then:
    assertEquals(ImmutableList.of(idBook2, idBook1), r4.stream()
        .map(B6DB.CatalogItemResult::getId)
        .collect(Collectors.toList()));

    // When:
    final List<B6DB.CatalogItemResult> r5 = catalogDao.getCatalogItems(null, ByteString.EMPTY, idBook2,
        "", CatalogDao.BOOK_TYPE, SortType.TITLE_DESCENDING, 10);

    // Then:
    assertEquals(ImmutableList.of(idBook5, idBook4), r5.stream()
        .map(B6DB.CatalogItemResult::getId)
        .collect(Collectors.toList()));
  }

  @Test
  public void shouldAccountForCursorInDuplicateItems() {
    // Given:
    final ByteString id1 = catalogDao.insert(null, itemBuilder("AAA", CatalogDao.ORIGIN_TYPE).build());
    final ByteString id2 = catalogDao.insert(null, itemBuilder("AAA", CatalogDao.LANGUAGE_TYPE).build());
    final ByteString id3 = catalogDao.insert(null, itemBuilder("AAA", CatalogDao.GENRE_TYPE).build());
    final ByteString id4 = catalogDao.insert(null, itemBuilder("AAA", CatalogDao.BOOK_TYPE).build());
    final ByteString id5 = catalogDao.insert(null, itemBuilder("AAA", CatalogDao.PERSON_TYPE).build());

    final List<ByteString> ids = ImmutableList.of(id1, id2, id3, id4, id5);
    assertCanRetrieveInChunks(ids, SortType.DEFAULT);
    assertCanRetrieveInChunks(ids, SortType.TITLE_DESCENDING);
    assertCanRetrieveInChunks(ids, SortType.TITLE_ASCENDING);
  }

  //
  // Private
  //

  private void assertCanRetrieveInChunks(List<ByteString> expected, SortType sortType) {
    final List<B6DB.CatalogItemResult> actual = new ArrayList<>();
    for (ByteString cursor = ByteString.EMPTY;;) {
      final List<B6DB.CatalogItemResult> res = catalogDao.getCatalogItems(null, ByteString.EMPTY, cursor, "", "",
          sortType, 1);
      if (res.isEmpty()) {
        break;
      }

      cursor = res.get(0).getId();
      actual.addAll(res);
    }

    assertEquals(ImmutableSet.copyOf(expected), actual.stream().map(B6DB.CatalogItemResult::getId)
        .collect(Collectors.toSet()));
  }

  private static void assertSame(B6DB.CatalogItemExtension item, B6DB.CatalogItemResult result, ByteString id) {
    B6DB.CatalogItemExtension.Builder itemBuilder = B6DB.CatalogItemExtension.newBuilder().setItem(result.getItem());
    if (result.hasBook()) {
      itemBuilder.setBook(result.getBook());
    }
    assertEquals(item, itemBuilder.build());
    assertEquals(id, result.getId());
  }

  private B6DB.Relation insertRelation(ByteString fromId, ByteString toId, String type) {
    final B6DB.Relation relation = B6DB.Relation.newBuilder()
        .setType(type)
        .setFromId(fromId)
        .setToId(toId)
        .build();

    catalogDao.insertRelation(null, relation);

    return relation;
  }

  private static B6DB.CatalogItemExtension.Builder itemBuilder(String title, String type) {
    return B6DB.CatalogItemExtension.newBuilder()
        .setItem(B6DB.CatalogItem.newBuilder()
            .setTitle(title)
            .setType(type)
            .build());
  }
}

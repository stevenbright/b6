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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    final B6DB.CatalogItemExtension actual = catalogDao.getById(null, id);
    assertEquals(item, actual);
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
    assertEquals(item1, catalogDao.getById(null, id));
    catalogDao.update(null, id, item2);

    // Then:
    final B6DB.CatalogItemExtension actual = catalogDao.getById(null, id);
    assertEquals(item2, actual);
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
    final B6DB.Relation relBook1Genre1 = B6DB.Relation.newBuilder().setType(CatalogDao.GENRE_TYPE)
        .setFromId(idBook1).setToId(idGenre1).build();
    final B6DB.Relation relBook1Genre2 = B6DB.Relation.newBuilder().setType(CatalogDao.GENRE_TYPE)
        .setFromId(idBook1).setToId(idGenre2).build();
    final B6DB.Relation relBook1Lang = B6DB.Relation.newBuilder().setType(CatalogDao.GENRE_TYPE)
        .setFromId(idBook1).setToId(idLang).build();
    catalogDao.insertRelation(null, relBook1Genre1);
    catalogDao.insertRelation(null, relBook1Genre2);
    catalogDao.insertRelation(null, relBook1Lang);

    final B6DB.Relation relBook2Lang = B6DB.Relation.newBuilder().setType(CatalogDao.GENRE_TYPE)
        .setFromId(idBook2).setToId(idLang).build();
    catalogDao.insertRelation(null, relBook2Lang);

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
    // Given:
    final ByteString idLang = catalogDao.insert(null, itemLang);
    final ByteString idGenre1 = catalogDao.insert(null, itemBuilder("fantasy", CatalogDao.GENRE_TYPE).build());
    final ByteString idGenre2 = catalogDao.insert(null, itemBuilder("novel", CatalogDao.GENRE_TYPE).build());
    final ByteString idBook1 = catalogDao.insert(null, itemBuilder("Book 2", CatalogDao.BOOK_TYPE).build());
    final ByteString idBook2 = catalogDao.insert(null, itemBuilder("A Book 1", CatalogDao.BOOK_TYPE).build());

    // Test:
    final List<B6DB.CatalogItemExtension> items = catalogDao.getCatalogItems(null, ByteString.EMPTY, ByteString.EMPTY,
        "", "", SortType.DEFAULT, 5);

    assertEquals(5, items.size());
  }

  //
  // Private
  //

  private static B6DB.CatalogItemExtension.Builder itemBuilder(String title, String type) {
    return B6DB.CatalogItemExtension.newBuilder()
        .setItem(B6DB.CatalogItem.newBuilder()
            .setTitle(title)
            .setType(type)
            .build());
  }
}

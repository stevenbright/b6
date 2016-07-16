package b6.persistence;

import b6.persistence.model.generated.B6DB;
import com.google.protobuf.ByteString;
import com.sleepycat.je.Transaction;

/**
 * @author Alexander Shabanov
 */
public interface CatalogDao {
  String BOOK_TYPE = "book";
  String LANGUAGE_TYPE = "language";
  String GENRE_TYPE = "genre";

  ByteString insert(Transaction tx, B6DB.CatalogItemExtension item);

  void update(Transaction tx, ByteString id, B6DB.CatalogItemExtension item);

  B6DB.CatalogItemExtension getById(Transaction tx, ByteString id);
}

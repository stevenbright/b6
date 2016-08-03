package b6.persistence.support;

import b6.persistence.CatalogDao;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author Alexander Shabanov
 */
@Transactional(propagation = Propagation.REQUIRED)
public final class DefaultCatalogDao implements CatalogDao {
  private final JdbcOperations db;

  public DefaultCatalogDao(JdbcOperations jdbcOperations) {
    this.db = Objects.requireNonNull(jdbcOperations, "jdbcOperations");
  }
}

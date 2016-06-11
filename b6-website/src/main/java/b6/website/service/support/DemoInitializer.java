package b6.website.service.support;

import b6.model.catalog.CatalogItem;
import b6.website.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * A class, that performs initialization when application starts.
 *
 * @author Alexander Shabanov
 */
public final class DemoInitializer {
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final CatalogService catalogService;

  public DemoInitializer(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @PostConstruct
  public void init() {
    log.info("Demo mode turned on, insert sample items");

    final long[] ids = {
        catalogService.persistItem(CatalogItem.builder().title("en").type("language").build()),
        catalogService.persistItem(CatalogItem.builder().title("novel").type("genre").build()),
        catalogService.persistItem(CatalogItem.builder().title("ZM").type("origin").build()),
    };

    log.info("Persist base items, ids={}", ids);
  }
}

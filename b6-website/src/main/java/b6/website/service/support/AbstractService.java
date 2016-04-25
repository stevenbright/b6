package b6.website.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates common logic: exception translation, logging, metrics, etc.
 */
public abstract class AbstractService {
  protected final Logger log = LoggerFactory.getLogger(getClass());
}

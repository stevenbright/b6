package b6.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/CatalogDaoTest-context.xml")
@Transactional
public class CatalogDaoTest {
  @Resource
  CatalogDao catalogService;

  @Test
  public void shouldPass() {
    assertEquals(1, 1);
  }
}

package org.teiath.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.teiath.data.search.RouteSearchCriteria;
import org.teiath.service.crp.ListRoutesService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "/org/teiath/test/config/test_config.xml")
public class ListRoutesServiceTest {

	@Autowired
	ListRoutesService service;

	@Test
	public void testProductService() {
//		service.searchRoutes(new RouteSearchCriteria());
//		logger.info("Test message info");
//		logger.debug("Test message debug");
//		testFindById();
//		testCreateProduct();
//		testDeleteProduct();
	}
}
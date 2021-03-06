package org.springframework.samples.petclinic.web;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.model.Discount;
import org.springframework.samples.petclinic.model.Product;
import org.springframework.samples.petclinic.service.DiscountService;
import org.springframework.samples.petclinic.service.ProductService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DiscountController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class DiscountControllerTest {

	private static final int TEST_PRODUCT_ID = 1;
	private static final int TEST_DISCOUNT_ID = 1;
	private static final int TEST_DISCOUNT_ID_2 = 2;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DiscountService discountService;
	
	@MockBean
	private ProductService productService;
	
	private Discount testDiscount;
	private Discount testDiscount2;
	
	@BeforeEach
	void setup() {
		testDiscount = new Discount();
		testDiscount.setFinishDate(LocalDate.now().plusDays(5));
		testDiscount.setStartDate(LocalDate.now());
		testDiscount.setId(TEST_DISCOUNT_ID);
		testDiscount.setPercentage(50.0);
		
		testDiscount2 = new Discount();
		testDiscount2.setFinishDate(LocalDate.now().plusDays(2));
		testDiscount2.setStartDate(LocalDate.now());
		testDiscount2.setId(TEST_DISCOUNT_ID_2);
		testDiscount2.setPercentage(10.0);
		
		Product product1 = new Product();
		product1.setDiscount(testDiscount);
		
		given(this.discountService.findDiscountById(TEST_DISCOUNT_ID)).willReturn(this.testDiscount);
		given(this.discountService.findDiscountById(TEST_DISCOUNT_ID_2)).willReturn(this.testDiscount2);
		given(this.productService.findProductById(TEST_PRODUCT_ID)).willReturn(product1);
	}

	@WithMockUser(value = "spring")
	@Test
	void testInitNewDiscountForm() throws Exception {
		mockMvc.perform(get("/shops/*/products/{productId}/discounts/new", TEST_PRODUCT_ID)).andExpect(status().isOk())
				.andExpect(view().name("discounts/createOrUpdateDiscountForm"));
	}

	@WithMockUser(value = "spring")
	@Test
	void testProcessNewDiscountFormSuccess() throws Exception {
		mockMvc.perform(post("/shops/1/products/{productId}/discounts/new", TEST_PRODUCT_ID).with(csrf())
				.param("startDate", "2020/07/02")
				.param("finishDate", "2020/09/03")
				.param("percentage", "20.0")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/shops/1/products/{productId}"));
	}

	@WithMockUser(value = "spring")
	@Test
	void testProcessNewDiscountFormHasErrors() throws Exception {
		mockMvc.perform(post("/shops/1/products/{productId}/discounts/new", TEST_PRODUCT_ID).with(csrf())
				.param("startDate", "2020/08/06").param("finishDate", "2020/08/01"))
				.andExpect(model().attributeHasErrors("discount")).andExpect(model().attributeHasFieldErrors("discount", "percentage"))
				.andExpect(model().attributeHasFieldErrorCode("discount", "finishDate", "wrongDate")).andExpect(status().isOk())
				.andExpect(view().name("discounts/createOrUpdateDiscountForm"));
	}

	
	@WithMockUser(value = "spring")
	@Test
	void testInitEditDiscountForm() throws Exception {
		mockMvc.perform(get("/shops/1/products/{productId}/discounts/{discountId}/edit", TEST_PRODUCT_ID, TEST_DISCOUNT_ID)).andExpect(status().isOk())
				.andExpect(model().attributeExists("discount"))
				.andExpect(model().attribute("product", this.productService.findProductById(TEST_PRODUCT_ID)))
				.andExpect(model().attribute("discount", hasProperty("startDate", is(LocalDate.now()))))
				.andExpect(model().attribute("discount", hasProperty("finishDate", is(LocalDate.now().plusDays(5)))))
				.andExpect(model().attribute("discount", hasProperty("percentage", is(50.0))))
				.andExpect(model().attribute("discount", hasProperty("id", is(TEST_DISCOUNT_ID))))
				.andExpect(view().name("discounts/createOrUpdateDiscountForm"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testProcessEditDiscountFormSuccess() throws Exception {
		mockMvc.perform(post("/shops/1/products/{productId}/discounts/{discountId}/edit", TEST_PRODUCT_ID, TEST_DISCOUNT_ID).with(csrf())
				.param("startDate", "2020/08/02")
				.param("finishDate", "2020/09/03")
				.param("percentage", "20.0").param("id", "1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/shops/1/products/{productId}"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testProcessEditDiscountFormHasErrors() throws Exception {
		mockMvc.perform(post("/shops/1/products/{productId}/discounts/{discountId}/edit", TEST_PRODUCT_ID, TEST_DISCOUNT_ID).with(csrf())
				.param("startDate", "2020/07/06")
				.param("finishDate", "2020/07/01").param("id", "1"))
				.andExpect(model().attributeHasErrors("discount")).andExpect(model().attributeHasFieldErrors("discount", "percentage"))
				.andExpect(model().attributeHasFieldErrorCode("discount", "finishDate", "wrongDate")).andExpect(status().isOk())
				.andExpect(view().name("discounts/createOrUpdateDiscountForm"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testDeleteDiscountSuccess() throws Exception {
		mockMvc.perform(get("/shops/1/products/{productId}/discounts/{discountId}/delete", TEST_PRODUCT_ID, TEST_DISCOUNT_ID)).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/shops/1/products/{productId}"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testDeleteDiscountErrorProductDiscountNotEqual() throws Exception {
		mockMvc.perform(get("/shops/1/products/{productId}/discounts/{discountId}/delete", TEST_PRODUCT_ID, TEST_DISCOUNT_ID_2)).andExpect(status().isOk())
				.andExpect(view().name("/exception"));
	}
}
package com.demo.Store.controller.admin;

import com.demo.Store.model.Product;
import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.model.dto.ProductDto;
import com.demo.Store.model.dto.mapper.ProductMapper;
import com.demo.Store.service.MessageSourceService;
import com.demo.Store.service.ProductService;
import com.demo.Store.util.ValidationConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

import static com.demo.Store.util.TestUtils.OPERATION_RESULT;
import static com.demo.Store.util.TestUtils.getOperationResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private MessageSourceService messageSourceService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ProductController controller;

    private ProductDto productDto;
    private Product product;

    @BeforeEach
    public void setUp() {
        productDto = new ProductDto();
        product = new Product();
    }

    @Test
    void validationBuildsExpectedValuesAndMessages() {
        String testMinLength = "testMinLength %s";
        String testMinNumber = "testMinNumber %s";
        String testMaxNumber = "testMaxNumber %s";
        when(messageSourceService.getMessage("store.validation.MinLength")).thenReturn(testMinLength);
        when(messageSourceService.getMessage("store.validation.MinNumber")).thenReturn(testMinNumber);
        when(messageSourceService.getMessage("store.validation.MaxNumber")).thenReturn(testMaxNumber);
        Map<String, Object> validation = controller.validation();
        assertTrue(validation.containsKey("minPrice"));
        assertTrue(validation.containsKey("maxPrice"));
        assertTrue(validation.containsKey("detailedDescMinLength"));
        assertEquals(validation.get("minPriceMessage"), String.format(testMinNumber, ValidationConstants.MIN_PRICE));
        assertEquals(validation.get("maxPriceMessage"), String.format(testMaxNumber, ValidationConstants.MAX_PRICE));
        assertEquals(validation.get("detailedDescMinLengthMessage"), String.format(testMinLength, ValidationConstants.DETAILED_DESC_MIN_LENGTH));
    }

    @Test
    void productsReturnsListViewAndAddsMappedProducts() {
        List<Product> products = List.of(product);
        List<ProductDto> mappedProducts = List.of(productDto);
        when(productService.getAll(null, null)).thenReturn(products);
        when(productMapper.toDtos(products)).thenReturn(mappedProducts);
        String view = controller.products(model);
        assertEquals("admin/product-list", view);
        verify(model).addAttribute("products", mappedProducts);
    }

    @Test
    void addProductGetReturnsManagementView() {
        String result = controller.newProductForm(model);
        assertEquals("admin/product-management", result);
        verify(model).addAttribute("product", productDto);
    }

    @Test
    void addProductReturnsAddRedirectOnValidationError() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String validationFailed = "Validation failed";
        when(bindingResult.getAllErrors()).thenReturn(List.of(new ObjectError("product", validationFailed)));
        OperationResultDto operationResult = getOperationResult(false, validationFailed);
        String result = controller.addProduct(productDto, bindingResult, redirectAttributes);
        assertEquals("redirect:/admin/products/new", result);
        verify(redirectAttributes).addFlashAttribute(OPERATION_RESULT, operationResult);
        verify(redirectAttributes).addFlashAttribute(productDto);
        verify(productService, never()).addOrUpdate(any());
    }

    @Test
    void addProductReturnsListRedirectOnSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productMapper.toProduct(productDto)).thenReturn(product);
        OperationResultDto operationResult = getOperationResult(true, "Saved");
        when(productService.addOrUpdate(product)).thenReturn(operationResult);
        String result = controller.addProduct(productDto, bindingResult, redirectAttributes);
        assertEquals("redirect:/admin/products/list", result);
        verify(redirectAttributes).addFlashAttribute("operationResult", operationResult);
    }

    @Test
    void editProductGetReturnsManagementView() {
        when(productService.getById(7L)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);
        String result = controller.editProductGet("7", model);
        assertEquals("admin/product-management", result);
        verify(model).addAttribute("product", productDto);
    }

    @Test
    void deleteProductCallsServiceAndRedirects() {
        OperationResultDto result = getOperationResult(true, "Deleted");
        when(productService.deleteById(12L)).thenReturn(result);
        String view = controller.deleteProduct("12", redirectAttributes);
        assertEquals("redirect:/admin/products/list", view);
        verify(redirectAttributes).addFlashAttribute("operationResult", result);
    }

}

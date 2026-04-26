package com.demo.Store.controller.admin;

import com.demo.Store.model.dto.OperationResultDto;
import com.demo.Store.model.dto.ProductDto;
import com.demo.Store.model.dto.mapper.ProductMapper;
import com.demo.Store.service.MessageSourceService;
import com.demo.Store.service.ProductService;
import com.demo.Store.util.ValidationConstants;
import com.demo.Store.util.ValidationUtils;
import jakarta.validation.Valid;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final MessageSourceService messageSourceService;

    public ProductController(ProductService productService, ProductMapper productMapper, MessageSourceService messageSourceService) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.messageSourceService = messageSourceService;
    }

    @ModelAttribute("validation")
    public Map<String, Object> validation() {
        Map<String, Object> map = new HashMap<>();
        map.put("minPrice", ValidationConstants.MIN_PRICE);
        map.put("maxPrice", ValidationConstants.MAX_PRICE);
        map.put("minQuantity", ValidationConstants.MIN_QUANTITY);
        map.put("maxQuantity", ValidationConstants.MAX_QUANTITY);
        map.put("detailedDescMinLength", ValidationConstants.DETAILED_DESC_MIN_LENGTH);
        map.put("simpleDescMinLength", ValidationConstants.SIMPLE_DESC_MIN_LENGTH);
        String minLengthMessage = messageSourceService.getMessage("store.validation.MinLength");
        String minNumberMessage = messageSourceService.getMessage("store.validation.MinNumber");
        String maxNumberMessage = messageSourceService.getMessage("store.validation.MaxNumber");
        map.put("minPriceMessage", String.format(minNumberMessage, ValidationConstants.MIN_PRICE));
        map.put("maxPriceMessage", String.format(maxNumberMessage, ValidationConstants.MAX_PRICE));
        map.put("minQuantityMessage", String.format(minNumberMessage, ValidationConstants.MIN_QUANTITY));
        map.put("maxQuantityMessage", String.format(maxNumberMessage, ValidationConstants.MAX_QUANTITY));
        map.put("detailedDescMinLengthMessage", String.format(minLengthMessage, ValidationConstants.DETAILED_DESC_MIN_LENGTH));
        map.put("simpleDescMinLengthMessage", String.format(minLengthMessage, ValidationConstants.SIMPLE_DESC_MIN_LENGTH));
        return map;
    }

    @GetMapping("/list")
    public String products(Model model) {
        model.addAttribute("products", productMapper.toDtos(productService.getAll(null, null)));
        return "admin/product-list";
    }

    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new ProductDto());
        return "admin/product-management";
    }

    @PostMapping("/new")
    public String addProduct(@Valid @ModelAttribute ProductDto product, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        return doProductOperation(product, bindingResult, redirectAttributes, true);
    }

    @GetMapping("/edit")
    public String editProductGet(@RequestParam String id, Model model) {
        model.addAttribute("product", productMapper.toDto(productService.getById(NumberUtils.toLong(id))));
        return "admin/product-management";
    }

    @PostMapping("/edit")
    public String editProductPost(@Valid @ModelAttribute ProductDto product, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        return doProductOperation(product, bindingResult, redirectAttributes, false);
    }

    @PostMapping("/delete")
    public String deleteProduct(@RequestParam String id, RedirectAttributes redirectAttributes) {
        OperationResultDto deletionResult = productService.deleteById(NumberUtils.toLong(id));
        redirectAttributes.addFlashAttribute("operationResult", deletionResult);
        return "redirect:/admin/products/list";
    }

    private String doProductOperation(ProductDto product, BindingResult bindingResult, RedirectAttributes redirectAttributes, boolean isNewProduct) {
        String redirectionEndpoint = isNewProduct ? "new" : "edit";
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("operationResult", ValidationUtils.getValidationErrorResult(bindingResult));
            redirectAttributes.addFlashAttribute(product);
            return String.format("redirect:/admin/products/%s", redirectionEndpoint);
        }
        OperationResultDto operationResultDto = productService.addOrUpdate(productMapper.toProduct(product));
        redirectAttributes.addFlashAttribute("operationResult", operationResultDto);
        return "redirect:/admin/products/list";
    }
}

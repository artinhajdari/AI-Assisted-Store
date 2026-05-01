package com.demo.Store.controller;

import com.demo.Store.model.dto.ProductDto;
import com.demo.Store.model.dto.mapper.ProductMapper;
import com.demo.Store.service.ChatGenService;
import com.demo.Store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class StoreController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @Autowired
    public StoreController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<ProductDto> search(@RequestParam(required = false) String query, @RequestParam(required = false) Integer page) throws Exception {
        return productMapper.toDtos(productService.semanticSearch(query, page));
    }

}

package com.demo.Store.repository;

import com.demo.Store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
    select (count(p) > 0)
    from Product p
    where p.name = :#{#product.name}
      and (
            p.displayedDescription = :#{#product.displayedDescription}
         or p.aiDescription = :#{#product.aiDescription}
      )
      and (:#{#product.id} is null or p.id <> :#{#product.id})
    """)
    boolean existsDuplicate(@Param("product") Product product);

}

package com.product.product.repository;

import com.product.product.domain.ProductInvoices;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductInvoicesRepository extends JpaRepository<ProductInvoices, Long> {
}

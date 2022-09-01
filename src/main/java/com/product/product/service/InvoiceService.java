package com.product.product.service;

import com.product.product.domain.*;

import java.io.IOException;
import java.util.List;

public interface InvoiceService {

    List<Invoice> getInvoices();

    List<ProductInvoices> getProductsInvoices();

    Invoice findInvoiceById(Long id);

    Invoice addNewInvoice(User user) throws IOException;

    ProductInvoices addNewProductInvoices(Product product, String quantity, Invoice invoice) throws IOException;

    void deleteInvoice(Long id) throws IOException;

    void deleteProductInvoices(Long id) throws IOException;
}

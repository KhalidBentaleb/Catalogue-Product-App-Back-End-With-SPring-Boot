package com.product.product.service.impl;

import com.product.product.domain.*;
import com.product.product.repository.InvoiceRepository;
import com.product.product.repository.ProductInvoicesRepository;
import com.product.product.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private InvoiceRepository invoiceRepository;
    private ProductInvoicesRepository productInvoicesRepository;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, ProductInvoicesRepository productInvoicesRepository) {
        this.invoiceRepository = invoiceRepository;
        this.productInvoicesRepository = productInvoicesRepository;
    }

    @Override
    public List<Invoice> getInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public List<ProductInvoices> getProductsInvoices() {
        return productInvoicesRepository.findAll();
    }

    @Override
    public Invoice findInvoiceById(Long id) {
        return invoiceRepository.findInvoiceById(id);
    }

    @Override
    public Invoice addNewInvoice(User user) throws IOException {
        Invoice invoice = new Invoice();
        invoice.setUser(user);
        invoiceRepository.save(invoice);
        return invoice;
    }

    @Override
    public ProductInvoices addNewProductInvoices(Product product, String quantity, Invoice invoice) throws IOException {
        ProductInvoices productInvoices = new ProductInvoices();
        productInvoices.setProduct(product);
        productInvoices.setQuantity(quantity);
        productInvoices.setInvoice(invoice);
        productInvoicesRepository.save(productInvoices);
        return productInvoices;
    }

    @Override
    public void deleteInvoice(Long id) throws IOException {
        invoiceRepository.deleteById(id);
    }

    @Override
    public void deleteProductInvoices(Long id) throws IOException {
        productInvoicesRepository.deleteById(id);
    }
}

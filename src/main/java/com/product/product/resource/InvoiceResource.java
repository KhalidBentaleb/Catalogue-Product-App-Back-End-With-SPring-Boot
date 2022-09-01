package com.product.product.resource;

import com.product.product.domain.*;
import com.product.product.exception.ExceptionHandling;
import com.product.product.service.InvoiceService;
import com.product.product.service.ProductService;
import com.product.product.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = { "/", "/invoice"})
public class InvoiceResource extends ExceptionHandling {
    public static final String INVOICE_DELETED_SUCCESSFULLY = "Invoice deleted successfully";
    private InvoiceService invoiceService;
    private UserService userService;
    private ProductService productService;

    public InvoiceResource(InvoiceService invoiceService, UserService userService, ProductService productService) {
        this.invoiceService = invoiceService;
        this.userService = userService;
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<Invoice> addNewInvoice(@RequestParam("username") String username) throws IOException {
        User user = userService.findUserByUsername(username);
        Invoice newInvoice = invoiceService.addNewInvoice(user);
        return new ResponseEntity<>(newInvoice, OK);
    }

    @PostMapping("/productinvoices/add")
    public ResponseEntity<ProductInvoices> addNewProductInvoices(@RequestParam("productName") String productName,
                                                                 @RequestParam("invoiceId") Long invoiceId,
                                                                 @RequestParam("quantity") String quantity) throws IOException {
        Product product = productService.findProductByName(productName);
        Invoice invoice = invoiceService.findInvoiceById(invoiceId);
        ProductInvoices newProductInvoices = invoiceService.addNewProductInvoices(product, quantity, invoice);
        return new ResponseEntity<>(newProductInvoices, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getInvoices();
        return new ResponseEntity<>(invoices, OK);
    }

    @GetMapping("/productinvoices/list")
    public ResponseEntity<List<ProductInvoices>> getAllProductInvoices() {
        List<ProductInvoices> productInvoices = invoiceService.getProductsInvoices();
        return new ResponseEntity<>(productInvoices, OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpResponse> deleteInvoice(@PathVariable("id") Long id) throws IOException {
        invoiceService.deleteInvoice(id);
        return response(OK, INVOICE_DELETED_SUCCESSFULLY);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
}

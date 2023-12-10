package storage;
import exeption.IdNotFoundException;
import exeption.OutOfStockException;
import model.Product;
import model.enums.ProductType;
import util.StorageSerializeUtil;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class ProductStorage implements Serializable {
    private final Set<Product> PRODUCTS = new HashSet<>();

    public void addProduct(Product product) {
        PRODUCTS.add(product);
        StorageSerializeUtil.serializeProductStorage(this);
    }

    public void deleteProductById(String productId) throws IdNotFoundException {
        boolean removed = PRODUCTS.removeIf(product -> product.getId().equals(productId));
        if (removed) {
            StorageSerializeUtil.serializeProductStorage(this);
        } else {
            throw new IdNotFoundException(productId + " this id dose not found");
        }
    }

    public int toCountProductPriceByQuantity(String productId, int quantity) throws OutOfStockException {
        for (Product product : PRODUCTS) {
            if (product.getId().equals(productId) && product.getStockQty() > quantity) {
                return (int) (product.getPrice() * quantity);
            }
        }
        throw new OutOfStockException(quantity + " this quantity dose not available, please try another quantity");
    }

    public void printAllProducts() {
        boolean exist = false;
        for (Product product : PRODUCTS) {
            System.out.println(product);
            exist = true;
        }
        if (!exist) {
            System.out.println("Not products at the moment");
        }
    }

    public void searchProduct(String searchCriteria) {
        boolean exist = false;
        for (Product product : PRODUCTS) {
            if (product.getId().equals(searchCriteria)
                    || product.getName().contains(searchCriteria)
                    || product.getDescription().contains(searchCriteria)) {
                System.out.println(product);
                exist = true;
            }
        }
        if (!exist) {
            System.out.println("Product does not found");
        }
    }

    public void handleDelivery(Product product, int deliveredQuantity) {
        if (product.getStockQty() >= deliveredQuantity) {
            product.setStockQty(product.getStockQty() - deliveredQuantity);
            StorageSerializeUtil.serializeProductStorage(this);
        } else {
            System.out.println("Insufficient stock for product: " + product.getName());
        }
    }

    public void printAllProductType() {
        ProductType[] productTypes = ProductType.values();
        for (ProductType productType : productTypes) {
            System.out.println(productType);
        }
    }

    public Product getProductById(String productId) {
        for (Product product : PRODUCTS) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
    }
}

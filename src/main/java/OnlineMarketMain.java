import exeption.IdNotFoundException;
import exeption.OutOfStockException;
import model.Order;
import model.Product;
import model.enums.OrderStatus;
import model.enums.PaymentMethod;
import model.enums.ProductType;
import model.enums.UserType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import storage.OrderStorage;
import storage.ProductStorage;
import storage.UserStorage;
import util.GenerateUUID;
import util.StorageSerializeUtil;
import model.User;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class OnlineMarketMain implements Command {
    static Scanner scanner = new Scanner(System.in);
    static GenerateUUID generateUUID = new GenerateUUID();
    static User currentUser = null;
    static OrderStorage orderStorage = StorageSerializeUtil.deserializeOrderStorage();
    static ProductStorage productStorage = StorageSerializeUtil.deserializeProductStorage();
    static UserStorage userStorage = StorageSerializeUtil.deserializeUserStorage();

    public static void main(String[] args) {
        boolean isRune = true;

        while (isRune) {
            Command.printCommandsForGlobalMenu();
            String choiceGlobalMenu = scanner.nextLine();

            switch (choiceGlobalMenu) {
                case EXIT -> isRune = false;
                case REGISTER -> register();
                case LOGIN -> login();
                default -> System.out.println("You are enter an error!");
            }
        }
    }

    private static void userCommands() {
        boolean isRun = true;
        while (isRun) {
            Command.printCommandsForUser();
            String choiceUser = scanner.nextLine();
            switch (choiceUser) {
                case LOGOUT -> {
                    isRun = false;
                    currentUser = null;
                }
                case PRINT_ALL_PRODUCTS -> printAllProducts();
                case BUY_PRODUCT -> buyProduct();
                case PRINT_MY_ORDERS -> printUserMyOrder();
                case CANCEL_ORDER_BY_ID -> canselOrderById();
                case SEARCH_PRODUCT -> searchProduct();
                default -> System.out.println("You are enter an error!");
            }
        }
    }

    private static void adminCommands() {
        boolean isRun = true;
        while (isRun) {
            Command.printCommandsForAdmin();
            String choiceAdmin = scanner.nextLine();
            switch (choiceAdmin) {
                case LOGOUT -> {
                    isRun = false;
                    currentUser = null;
                }
                case ADD_PRODUCT -> addProduct();
                case REMOVE_PRODUCT_BY_ID -> deleteProductById();
                case PRINT_PRODUCTS -> printAllProducts();
                case PRINT_USERS -> printAllUsers();
                case PRINT_ORDERS -> printAllOrders();
                case CHANGE_ORDER_STATUS -> changeOrderStatus();
                case EXPORT_DATA_TO_EXCEL -> exportDataToExcel();
                default -> System.out.println("You are enter an error!");
            }
        }
    }

    private static void exportDataToExcel () {
        System.out.println("Enter teh path:");
        String path = scanner.nextLine();
        File filePath = new File(path);

        List<Order> orders = orderStorage.getORDERS();
        if (filePath.exists() && filePath.isDirectory()) {
            try (Workbook workbook = new XSSFWorkbook()){
                Sheet orderIdSheet = workbook.createSheet("orders");
                extractedHead(orderIdSheet);
                int rowIndex = 1;
                for (Order order : orders) {
                    rowIndex = getRowIndex(order, orderIdSheet, rowIndex);
                }
                workbook.write(new FileOutputStream(new File(filePath, "report_" + System.currentTimeMillis() + ".xlsx")));
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    private static int getRowIndex(Order order, Sheet orderIdSheet, int rowIndex) {
        Row row = orderIdSheet.createRow(rowIndex++);
        Cell orderIdWrite = row.createCell(0);
        orderIdWrite.setCellValue(order.getId());
        Cell userIdWrite = row.createCell(1);
        userIdWrite.setCellValue(order.getUser().getId());
        Cell userNameWrite = row.createCell(2);
        userNameWrite.setCellValue(order.getUser().getName());
        Cell productIdWrite = row.createCell(3);
        productIdWrite.setCellValue(order.getProduct().getId());
        Cell productNameWrite = row.createCell(4);
        productNameWrite.setCellValue(order.getProduct().getName());
        Cell productQtyWrite = row.createCell(5);
        productQtyWrite.setCellValue(order.getProduct().getStockQty());
        Cell productPriceWrite = row.createCell(6);
        productPriceWrite.setCellValue(order.getPrice());
        return rowIndex;
    }

    private static void extractedHead(Sheet orderIdSheet) {
        Row headRow = orderIdSheet.createRow(0);
        Cell idCell = headRow.createCell(0);
        idCell.setCellValue("Order ID");
        Cell userIdCell = headRow.createCell(1);
        userIdCell.setCellValue("User ID");
        Cell userName = headRow.createCell(2);
        userName.setCellValue("User name");
        Cell productId = headRow.createCell(3);
        productId.setCellValue("Product ID");
        Cell productName = headRow.createCell(4);
        productName.setCellValue("Product Name");
        Cell productQty = headRow.createCell(5);
        productQty.setCellValue("Product qty");
        Cell productPrice = headRow.createCell(6);
        productPrice.setCellValue("Product price");
    }

    private static void searchProduct() {
        System.out.println("Enter the product(id,name or description)");
        String searchCriteria = scanner.nextLine();
        productStorage.searchProduct(searchCriteria);
    }

    private static void changeOrderStatus() {
        try {
            System.out.println("Enter order id:");
            String orderId = scanner.nextLine();
            if (orderStorage.getOrderById(orderId) != null) {
                System.out.println("Enter new status:");
                orderStorage.printOrderStatus();
                OrderStatus orderStatus = OrderStatus.valueOf(scanner.nextLine().toUpperCase());
                orderStorage.changeOrderStatus(orderStatus, orderId);
                StorageSerializeUtil.serializeOrderStorage(orderStorage);
            } else System.out.println(orderId + " id dose not found");
        } catch (IllegalArgumentException | IdNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void buyProduct() {
        String orderId = generateUUID.uuid();
        double toCountProductPriceByQuantity;
        System.out.println("Please choice product by id");
        productStorage.printAllProducts();
        String choiceProductById = scanner.nextLine();

        if (productStorage.getProductById(choiceProductById) != null) {
            System.out.println("Please choice quantity product with you want to buy:");
            int quantity = Integer.parseInt(scanner.nextLine());
            System.out.println("Please choice payment metod:");
            orderStorage.printAllPaymentMethods();

            PaymentMethod paymentMethod;
            try {
                toCountProductPriceByQuantity = productStorage.toCountProductPriceByQuantity(choiceProductById, quantity);
                paymentMethod = PaymentMethod.valueOf(scanner.nextLine().toUpperCase());
            } catch (OutOfStockException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
                return;
            }

            System.out.println("Product id - " + choiceProductById);
            System.out.println("Product quantity - " + quantity);
            System.out.println("Product price - " + toCountProductPriceByQuantity);
            System.out.println("Payment metod - " + paymentMethod);
            System.out.println("You accept your order? (yes/no)");
            String acceptOrder = scanner.nextLine();

            if (acceptOrder.equalsIgnoreCase("yes")) {
                User userFromStorage = userStorage.getUserById(currentUser.getId());
                Product productFromStorage = productStorage.getProductById(choiceProductById);
                Date date = new Date();
                if (userFromStorage != null && productFromStorage != null) {
                    Order order = new Order(orderId, userFromStorage, productFromStorage, date, toCountProductPriceByQuantity, quantity, OrderStatus.NEW, paymentMethod);
                    orderStorage.addOrder(order);
                    StorageSerializeUtil.serializeProductStorage(productStorage);
                    System.out.println("Order placed successfully.");
                } else {
                    System.out.println("Payment is cancelled");
                }
            } else {
                System.out.println("Invalid payment");
            }
        }
    }

    private static void printAllUsers() {
        System.out.println("--------------");
        userStorage.printOnlyAllUsers();
        System.out.println("--------------");
    }

    private static void printAllProducts() {
        System.out.println("--------------");
        productStorage.printAllProducts();
        System.out.println("--------------");
    }

    private static void printAllOrders() {
        System.out.println("--------------");
        orderStorage.printAllOrders();
        System.out.println("--------------");
    }

    private static void printUserMyOrder() {
        System.out.println("--------------");
        orderStorage.printUserMyOrders(currentUser.getId());
        System.out.println("--------------");
    }

    private static void deleteProductById() {
        printAllProducts();
        System.out.println("Enter product id for delete");
        String productId = scanner.nextLine();

        if (productStorage.getProductById(productId) != null) {
            try {
                productStorage.deleteProductById(productId);
            } catch (IdNotFoundException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Product with id " + productId + " successful deleted");
            StorageSerializeUtil.serializeProductStorage(productStorage);
        } else {
            System.out.println("Product with id " + productId + " dose not found");
        }
    }

    private static void canselOrderById() {
        System.out.println("Choice order by id:");
        printUserMyOrder();
        String orderId = scanner.nextLine();
        if (orderStorage.getOrderById(orderId) != null) {
            try {
                orderStorage.cancelOrderById(orderId);
            } catch (IdNotFoundException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Order with id " + orderId + " successful cansel");
            StorageSerializeUtil.serializeOrderStorage(orderStorage);
        } else System.out.println("Order with id " + orderId + " dose not found");
    }

    private static void addProduct() {
        String productId = generateUUID.uuid();
        try {
            System.out.println("Enter product stockQty:");
            int stockQty = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter product name:");
            String name = scanner.nextLine();
            System.out.println("Enter product description:");
            String description = scanner.nextLine();
            System.out.println("Enter product price:");
            double price = Double.parseDouble(scanner.nextLine());
            productStorage.printAllProductType();
            ProductType productType = ProductType.valueOf(scanner.nextLine().toUpperCase());
            Product product = new Product(productId, stockQty, name, description, price, productType);
            productStorage.addProduct(product);
            StorageSerializeUtil.serializeProductStorage(productStorage);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void register() {
        String userId = generateUUID.uuid();
        UserType userOrAdmin;
        boolean isRegister;

        try {
            System.out.println("You are User or Admin");
            userOrAdmin = UserType.valueOf(scanner.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("You are entered an error, please try again");
            return;
        }
        System.out.println("Enter name:");
        String name = scanner.nextLine();
        System.out.println("Enter email:");
        String email = scanner.nextLine();
        isRegister = userStorage.isValidEmail(email);

        if (!isRegister) {
            System.out.println("Invalid email");
            return;
        }

        System.out.println("Enter password:");
        String password = scanner.nextLine();
        System.out.println("Confirm password:");
        String confirmPassword = scanner.nextLine();

        if (!confirmPassword.equals(password)) {
            return;
        }

        User user = new User(userId, name, email, password, userOrAdmin);
        currentUser = user;
        userStorage.addUser(user);

        System.out.println("Registration successful");
        if (userOrAdmin == UserType.ADMIN) {
            adminCommands();
        } else if (userOrAdmin == UserType.USER) {
            userCommands();
        }
    }

    private static void login() {
        System.out.println("Enter email:");
        String email = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        currentUser = userStorage.getUserBYEmailAndPassword(email, password);

        if (currentUser != null) {
            if (currentUser.getUserType() == UserType.ADMIN) {
                adminCommands();
            } else if (currentUser.getUserType() == UserType.USER) {
                userCommands();
            }
        } else {
            System.out.println("Invalid Login, please try again");
        }
    }
}

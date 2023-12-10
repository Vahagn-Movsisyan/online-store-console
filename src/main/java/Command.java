public interface Command {
    //Commands for global menu
    String EXIT = "0";
    String LOGIN = "1";
    String REGISTER = "2";

    //Commands for Admin
    String LOGOUT = "0";
    String ADD_PRODUCT = "1";
    String REMOVE_PRODUCT_BY_ID = "2";
    String PRINT_PRODUCTS = "3";
    String PRINT_USERS = "4";
    String PRINT_ORDERS = "5";
    String CHANGE_ORDER_STATUS = "6";
    String EXPORT_DATA_TO_EXCEL = "7";

    //Commands fo User
    String PRINT_ALL_PRODUCTS = "1";
    String BUY_PRODUCT = "2";
    String PRINT_MY_ORDERS = "3";
    String CANCEL_ORDER_BY_ID = "4";
    String SEARCH_PRODUCT = "5";

    static void printCommandsForGlobalMenu() {
        System.out.println(EXIT + " For exit");
        System.out.println(LOGIN + " For login");
        System.out.println(REGISTER + " For register");
    }

    static void printCommandsForAdmin() {
        System.out.println(LOGOUT + " - logout");
        System.out.println(ADD_PRODUCT + " - add product");
        System.out.println(REMOVE_PRODUCT_BY_ID + " - remove product by id");
        System.out.println(PRINT_PRODUCTS + " - print products");
        System.out.println(PRINT_USERS + " - print users (only USERS)");
        System.out.println(PRINT_ORDERS + " - print orders");
        System.out.println(CHANGE_ORDER_STATUS + " - change order status");
        System.out.println(EXPORT_DATA_TO_EXCEL + " - export data to excel");
    }

    static void printCommandsForUser() {
        System.out.println(LOGOUT + " - logout");
        System.out.println(PRINT_ALL_PRODUCTS + " - print all products");
        System.out.println(BUY_PRODUCT + " - buy product by product id");
        System.out.println(PRINT_MY_ORDERS + " - print my orders");
        System.out.println(CANCEL_ORDER_BY_ID + " - cancel order by id");
        System.out.println(SEARCH_PRODUCT + " - search product by(id,name or description)");
    }
}

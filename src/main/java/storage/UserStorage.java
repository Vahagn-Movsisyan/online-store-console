package storage;

import model.User;
import model.enums.UserType;
import util.StorageSerializeUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserStorage implements Serializable {
    private final Map<String, User> USERS = new HashMap<>();

    public void addUser(User user) {
        USERS.put(user.getId(), user);
        StorageSerializeUtil.serializeUserStorage(this);
    }

    public void printOnlyAllUsers() {
        boolean exist = false;
        for (User user : USERS.values()) {
            if (user.getUserType() == UserType.USER) {
                System.out.println(user);
                exist = true;
            }
        }
        if (!exist) {
            System.out.println("Not users at the moment");
        }
    }

    public User getUserBYEmailAndPassword(String email, String password) {
        for (User user : USERS.values()) {
            if (user != null && user.getEmail() != null && user.getPassword() != null &&
                    user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public User getUserById(String userId) {
        User user = USERS.get(userId);
        if (USERS.get(userId).equals(user)) {
            return user;
        }
        return null;
    }

    public boolean isValidEmail(String email) {
        if (!isValidEmailFormat(email)) {
            System.out.println("Invalid email format");
            return false;
        }
        if (isEmailExists(email)) {
            System.out.println(email + " this email already exist");
            return false;
        }
        return true;
    }

    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isEmailExists(String email) {
        for (User user : USERS.values()) {
            if (user != null && user.getEmail() != null && user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
}

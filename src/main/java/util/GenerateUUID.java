package util;

import java.util.UUID;

public class GenerateUUID {
    public String uuid () {
        String orderId = UUID.randomUUID().toString();
        String[] uuids = orderId.split("-");
        orderId = uuids[0];
        return orderId;
    }
}

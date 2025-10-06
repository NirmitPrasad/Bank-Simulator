package com.bfe.route.repository.cache;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;

public class AccountCacheTest {

    @Test
    void testAccountCacheStoreRetrieve() {
        Map<Integer, String> accountCache = new HashMap<>();
        accountCache.put(101, "Nirmit");
        accountCache.put(102, "Rahul");

        assertEquals("Nirmit", accountCache.get(101));
        assertEquals("Rahul", accountCache.get(102));
    }
}

package sirup.service.user.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResultTest {

    Result<String, String> successResult;
    Result<String, String> failedResult;

    @Before
    public void setUp() {
        successResult = Result.success("SUCCESS");
        failedResult = Result.error("ERROR");
    }

    @Test
    public void failed() {
        assertFalse(failedResult.succeeded());
        assertTrue(failedResult.failed());
    }

    @Test
    public void succeeded() {
        assertTrue(successResult.succeeded());
        assertFalse(successResult.failed());
    }

    @Test
    public void getOr() {
        assertEquals(successResult.getOr("OR"),"SUCCESS");
        assertNotNull(failedResult.getOr("OR"));
    }

    @Test
    public void get() {
        assertNotNull(successResult.get());
        assertNull(failedResult.get());
    }

    @Test
    public void error() {
        assertNull(successResult.error());
        assertNotNull(failedResult.error());
    }
}
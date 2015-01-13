package uk.gov.ida.analytics;

import org.junit.Test;

import static org.junit.Assert.*;

public class CustomVariableTest {

    @Test
    public void testGetAsJson_shouldFormatJsonToPiwikCvarFormat() throws Exception {
        CustomVariable customVariable = new CustomVariable(1, "foo", "bar");

        String customVariableAsJson = customVariable.getAsJson();

        assertEquals(customVariableAsJson, "{\"1\":[\"foo\",\"bar\"]}");
    }
}

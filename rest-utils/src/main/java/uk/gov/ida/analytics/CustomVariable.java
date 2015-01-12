package uk.gov.ida.analytics;


import static java.lang.String.format;

public class CustomVariable {
    private final int index;
    private final String name;
    private final String value;

    public CustomVariable(int index, String name, String value) {
        this.index = index;
        this.name = name;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    String getAsJson() {
        return format("{\"%s\":[\"%s\",\"%s\"]}", getIndex(), getName(), getValue());
    }
}

package org.wahlzeit.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Sport {
    private static AtomicInteger id_counter;
    public final SportType sport_type;
    public final String name;
    private String[] additionalAttributes; // Attribute Names
    private String[] additionalAttributesValues;  // Attribute Values

    public Sport(SportType st, String name) {
        this.sport_type = st;
        this.name = name;

    }

    public Sport(SportType st, String name, String[] additionalAttributes,
            String[] additionalAttributesValues) {
        assertIsNonNullArgument(st, "SportType - Sport Constructor");
        assertIsSameLength(additionalAttributes, additionalAttributesValues);
        this.sport_type = st;
        this.name = name;
        this.additionalAttributes = additionalAttributes;
        this.additionalAttributesValues = additionalAttributesValues;
    }

    public int getID() {
        return id_counter.incrementAndGet();
    }

    public SportType getType() {
        return sport_type;
    }

    public String getName() {
        return name;
    }

    public String getSpecificAdditionalAttribute(String attribute_name) {
        int index = getIndex(attribute_name);
        return additionalAttributesValues[index];
    }

    public String getSpecificAdditionalAttribute(int index) {
        if (additionalAttributes.length >= index) {
            throw new IllegalArgumentException("Not a valid index");
        }
        return additionalAttributesValues[index];
    }

    public String[] getAdditionalAttributes() {
        return this.additionalAttributes;
    }

    public String[] getAdditionalAttributesValues() {
        return this.additionalAttributesValues;
    }

    public void setSpecificAdditionalAttribute(String attribute_name, String value) {
        int index = getIndex(attribute_name);
        additionalAttributesValues[index] = value;
    }

    public void setSpecificAdditionalAttribute(int index, String value) {
        if (additionalAttributes.length >= index) {
            throw new IllegalArgumentException("Not a valid index");
        } else {
            additionalAttributes[index] = value;
        }
    }

    private int getIndex(String attribute_name) {
        for (int i = 0; i < additionalAttributes.length; i++) {
            if (additionalAttributes[i] == attribute_name) {
                return i;
            }
        }
        throw new IllegalArgumentException(attribute_name + " is not a valid attribute name");
    }

    protected void assertIsNonNullArgument(Object arg, String label) {
        if (arg == null) {
            throw new IllegalArgumentException(label + " should not be null");
        }
    }

    protected void assertIsSameLength(String[] arg1, String[] arg2) {
        if (arg1.length != arg2.length) {
            throw new IllegalArgumentException("Array lengths should be the same");
        }
    }
}
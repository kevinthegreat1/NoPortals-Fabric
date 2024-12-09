package com.kevinthegreat.noportals.option;

public class SimpleBooleanOption {
    private final String name;
    private final String translationKey;
    private boolean value;

    public SimpleBooleanOption(String name, String translationKey) {
        this.name = name;
        this.translationKey = translationKey;
    }

    public String getName() {
        return name;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}

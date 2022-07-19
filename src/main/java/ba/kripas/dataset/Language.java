package ba.kripas.dataset;

public enum Language {
    C_CPP("C_CPP");

    Language(String jplagConfigIdentifier) {
        this.jplagConfigIdentifier = jplagConfigIdentifier;
    }

    private final String jplagConfigIdentifier;

    public String getJplagConfigIdentifier() {
        return jplagConfigIdentifier;
    }
}

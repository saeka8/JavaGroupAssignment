package inputhandler.validator;

public abstract class InputValidator {
    private String input;

    protected InputValidator(String input) {
        // stash the raw input so subclasses can reuse it
        this.input = input;
    }

    // abstract method so subclasses decide what valid means
    public abstract boolean isValid();

    protected String getInput() {
        // shared getter to keep field private
        return input;
    }
}
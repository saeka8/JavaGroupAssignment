package inputhandler.validator;

public class PasswordValidator extends InputValidator {
    public PasswordValidator(String input) {
        super(input);
    }

    @Override
    public boolean isValid() {
        // for now we just make sure it is not empty and long enough
        String password = getInput();
        return password != null && password.length() >= 5;
    }
}

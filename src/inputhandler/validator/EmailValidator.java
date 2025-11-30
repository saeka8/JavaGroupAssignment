package inputhandler.validator;

public class EmailValidator extends InputValidator {

    public EmailValidator(String input) {
        super(input);
    }

    @Override
    public boolean isValid() {
        // super tiny check, needs @ and a dot somewhere
        String email = getInput();
        return email != null && email.contains("@") && email.contains(".");
    }

}
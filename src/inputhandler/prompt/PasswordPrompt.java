package inputhandler.prompt;

import inputhandler.validator.InputValidator;
import inputhandler.validator.PasswordValidator;

public class PasswordPrompt extends InputPrompt {
    @Override
    protected String getPromptMessage() {
        // simple helper so the base class can print the right text
        String passwordPromptMessage = "Please enter your password: ";
        return passwordPromptMessage;
    }

    @Override
    protected InputValidator createValidator(String input) {
        // plug in password validator to check length rules
        PasswordValidator passwordValidator = new PasswordValidator(input);
        return passwordValidator;
    }
}
package inputhandler.prompt;

import inputhandler.validator.EmailValidator;
import inputhandler.validator.InputValidator;

public class UsernamePrompt extends InputPrompt {
    @Override
    protected String getPromptMessage() {
        // message reused every time someone logs in
        String usernamePromptMessage = "Please enter your username: ";
        return usernamePromptMessage;
    }
    @Override
    protected InputValidator createValidator(String input) {
        // use email validator so usernames follow email format
        EmailValidator emailValidator = new EmailValidator(input);
        return emailValidator;
    }

}

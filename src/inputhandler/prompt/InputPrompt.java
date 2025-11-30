package inputhandler.prompt;

import java.util.Scanner;
import inputhandler.validator.*;

public abstract class InputPrompt {
    private static final Scanner CONSOLE = new Scanner(System.in);

    // each prompt knows its own message text
    protected abstract String getPromptMessage();
    // validator can look at the raw input and decide rules
    protected abstract InputValidator createValidator(String input);

    // shared console reader so we reuse one scanner
    public static String readLine() {
        return CONSOLE.nextLine();
    }

    // keep asking until the validator validates
    public String promptUntilValid() {
        while (true) {
            System.out.print(getPromptMessage());
            String input = CONSOLE.nextLine();
            InputValidator validator = createValidator(input);
            if (validator.isValid()) {
                return input;
            } else {
                System.out.println("Invalid input. Please try again.. ");
            }
        }
    }
}
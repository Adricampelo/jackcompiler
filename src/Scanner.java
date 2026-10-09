
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;

class Scanner {
    private final byte[] input;
    private int current = 0;

    public Scanner(byte[] input) {
        this.input = input;
    }

    private boolean isAtEnd() {
        return current >= input.length;
    }

    private char advance() {
        return (char) (input[current++] & 0xFF);
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }

        return (char) (input[current] & 0xFF);
    }

    private char peekNext() {
        if (current + 1 >= input.length) {
            return '\0';
        }

        return (char) (input[current + 1] & 0xFF);
    }
}

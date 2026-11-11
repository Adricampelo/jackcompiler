
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class VMWriter implements AutoCloseable {

    private final Writer output;

    private static final Set<String> SEGMENTS = Set.of(
            "constant", "argument", "local", "static",
            "this", "that", "pointer", "temp"
    );

    private static final Set<String> ARITHMETIC_COMMANDS = Set.of(
            "add", "sub", "neg",
            "eq", "gt", "lt",
            "and", "or", "not"
    );

    public VMWriter(Path outputFile) throws IOException {
        this(Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8));
    }

    public VMWriter(Writer output) {
        this.output = output;
    }

    private void writeLine(String command) throws IOException {
        output.write(command);
        output.write(System.lineSeparator());
    }

    public void writePush(String segment, int index) throws IOException {
        validateSegment(segment);
        validateIndex(segment, index);

        writeLine("push " + segment + " " + index);
    }

    public void writePop(String segment, int index) throws IOException {
        validateSegment(segment);

        if (segment.equals("constant")) {
            throw new IllegalArgumentException(
                    "Não é permitido usar pop no segmento constant."
            );
        }

        validateIndex(segment, index);

        writeLine("pop " + segment + " " + index);
    }

    public void writeArithmetic(String command) throws IOException {
        if (!ARITHMETIC_COMMANDS.contains(command)) {
            throw new IllegalArgumentException(
                    "Comando aritmético inválido: " + command
            );
        }

        writeLine(command);
    }

    public void writeLabel(String label) throws IOException {
        validateName(label);
        writeLine("label " + label);
    }

    public void writeGoto(String label) throws IOException {
        validateName(label);
        writeLine("goto " + label);
    }

    public void writeIf(String label) throws IOException {
        validateName(label);
        writeLine("if-goto " + label);
    }

    public void writeCall(String name, int numberOfArgs) throws IOException {
        validateName(name);
        validateNonNegative(numberOfArgs, "Número de argumentos");

        writeLine("call " + name + " " + numberOfArgs);
    }

    public void writeFunction(String name, int numberOfLocals)
            throws IOException {
        validateName(name);
        validateNonNegative(numberOfLocals, "Número de variáveis locais");

        writeLine("function " + name + " " + numberOfLocals);
    }

    public void writeReturn() throws IOException {
        writeLine("return");
    }

    private void validateSegment(String segment) {
        if (!SEGMENTS.contains(segment)) {
            throw new IllegalArgumentException(
                    "Segmento VM inválido: " + segment
            );
        }
    }

    private void validateIndex(String segment, int index) {
        validateNonNegative(index, "Índice");

        if (segment.equals("pointer") && index > 1) {
            throw new IllegalArgumentException(
                    "O segmento pointer aceita apenas os índices 0 e 1."
            );
        }

        if (segment.equals("temp") && index > 7) {
            throw new IllegalArgumentException(
                    "O segmento temp aceita índices de 0 a 7."
            );
        }

        if (segment.equals("constant") && index > 32767) {
            throw new IllegalArgumentException(
                    "A constante deve estar entre 0 e 32767."
            );
        }
    }

    private void validateNonNegative(int value, String description) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    description + " não pode ser negativo."
            );
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()
                || name.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException(
                    "Nome inválido: " + name
            );
        }
    }

    @Override
    public void close() throws IOException {
        output.close();
    }
}

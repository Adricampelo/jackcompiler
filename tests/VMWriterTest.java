
import java.io.StringWriter;

public class VMWriterTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        testWritePush();
        testWritePop();
        testWriteArithmetic();
        testWriteLabel();
        testWriteGoto();
        testWriteIf();
        testWriteCall();
        testWriteFunction();
        testWriteReturn();

        testInvalidSegment();
        testInvalidPopConstant();
        testInvalidPointerIndex();
        testInvalidTempIndex();
        testInvalidConstantIndex();
        testInvalidArithmetic();
        testInvalidNegativeArguments();
        testInvalidName();

        System.out.println("\n===== RESULTADO DOS TESTES =====");
        System.out.println("Aprovados: " + passed);
        System.out.println("Reprovados: " + failed);

        if (failed > 0) {
            throw new AssertionError("Existem testes que falharam.");
        }
    }

    private static void testWritePush() {
        test("writePush", () -> {
            StringWriter output = new StringWriter();

            try (VMWriter writer = new VMWriter(output)) {
                writer.writePush("constant", 10);
                writer.writePush("local", 0);
            }

            assertEquals(
                    "push constant 10\npush local 0",
                    output.toString()
            );
        });
    }

    private static void testWritePop() {
        test("writePop", () -> {
            StringWriter output = new StringWriter();

            try (VMWriter writer = new VMWriter(output)) {
                writer.writePop("local", 0);
                writer.writePop("static", 2);
            }

            assertEquals(
                    "pop local 0\npop static 2",
                    output.toString()
            );
        });
    }

    private static void testWriteArithmetic() {
        test("writeArithmetic", () -> {
            StringWriter output = new StringWriter();

            try (VMWriter writer = new VMWriter(output)) {
                writer.writeArithmetic("add");
                writer.writeArithmetic("sub");
                writer.writeArithmetic("neg");
                writer.writeArithmetic("eq");
                writer.writeArithmetic("gt");
                writer.writeArithmetic("lt");
                writer.writeArithmetic("and");
                writer.writeArithmetic("or");
                writer.writeArithmetic("not");
            }

            assertEquals(
                    "add\nsub\nneg\neq\ngt\nlt\nand\nor\nnot",
                    output.toString()
            );
        });
    }

    private static void testWriteLabel() {
        test("writeLabel", () -> {
            StringWriter output = new StringWriter();

            try (VMWriter writer = new VMWriter(output)) {
                writer.writeLabel("LOOP_START");
            }

            assertEquals("label LOOP_START", output.toString());
        });
    }

    private static void testWriteGoto() {
        test("writeGoto", () -> {
            StringWriter output = new StringWriter();

            try (VMWriter writer = new VMWriter(output)) {
                writer.writeGoto("LOOP_START");
            }

            assertEquals("goto LOOP_START", output.toString());
        });
    }

    private static void testWriteIf() {
        test("writeIf", () -> {
            StringWriter output = new StringWriter();

            try (VMWriter writer = new VMWriter(output)) {
                writer.writeIf("IF_TRUE");
            }

            assertEquals("if-goto IF_TRUE", output.toString());
        });
    }

    private static void testWriteCall() {
        test("writeCall", () -> {
            StringWriter output = new StringWriter();

            try (VMWriter writer = new VMWriter(output)) {
                writer.writeCall("Math.multiply", 2);
            }

            assertEquals("call Math.multiply 2", output.toString());
        });
    }

    private static void testWriteFunction() {
        test("writeFunction", () -> {
            StringWriter output = new StringWriter();

            try (VMWriter writer = new VMWriter(output)) {
                writer.writeFunction("Main.main", 2);
            }

            assertEquals("function Main.main 2", output.toString());
        });
    }

    private static void testWriteReturn() {
        test("writeReturn", () -> {
            StringWriter output = new StringWriter();

            try (VMWriter writer = new VMWriter(output)) {
                writer.writeReturn();
            }

            assertEquals("return", output.toString());
        });
    }

    private static void testInvalidSegment() {
        testException("Segmento inválido", () -> {
            new VMWriter(new StringWriter())
                    .writePush("invalid", 0);
        });
    }

    private static void testInvalidPopConstant() {
        testException("Pop no segmento constant", () -> {
            new VMWriter(new StringWriter())
                    .writePop("constant", 10);
        });
    }

    private static void testInvalidPointerIndex() {
        testException("Índice inválido de pointer", () -> {
            new VMWriter(new StringWriter())
                    .writePush("pointer", 2);
        });
    }

    private static void testInvalidTempIndex() {
        testException("Índice inválido de temp", () -> {
            new VMWriter(new StringWriter())
                    .writePop("temp", 8);
        });
    }

    private static void testInvalidConstantIndex() {
        testException("Constante acima do limite", () -> {
            new VMWriter(new StringWriter())
                    .writePush("constant", 32768);
        });
    }

    private static void testInvalidArithmetic() {
        testException("Operação aritmética inválida", () -> {
            new VMWriter(new StringWriter())
                    .writeArithmetic("multiply");
        });
    }

    private static void testInvalidNegativeArguments() {
        testException("Número negativo de argumentos", () -> {
            new VMWriter(new StringWriter())
                    .writeCall("Main.main", -1);
        });
    }

    private static void testInvalidName() {
        testException("Nome de função inválido", () -> {
            new VMWriter(new StringWriter())
                    .writeFunction("Main main", 0);
        });
    }

    private static void test(String name, TestAction action) {
        try {
            action.run();
            passed++;
            System.out.println("[APROVADO] " + name);
        } catch (Throwable e) {
            failed++;
            System.out.println("[REPROVADO] " + name);
            System.out.println("  Motivo: " + e.getMessage());
        }
    }

    private static void testException(String name, TestAction action) {
        test(name, () -> {
            try {
                action.run();
            } catch (IllegalArgumentException e) {
                return;
            }

            throw new AssertionError(
                    "Era esperada uma IllegalArgumentException."
            );
        });
    }



    private static void assertEquals(String expected, String actual) {
        expected = expected.replace("\r\n", "\n");
        actual = actual.replace("\r\n", "\n");
        
        if (actual.endsWith("\n")) {
            actual = actual.substring(0, actual.length() - 1);
        }

        if (!expected.equals(actual)) {
            throw new AssertionError(
                    "Esperado: [" + expected + "]" +
                            "\nObtido: [" + actual + "]"
            );
        }
    }



    @FunctionalInterface
    private interface TestAction {
        void run() throws Exception;
    }
}

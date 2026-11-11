import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        boolean generateXml = false;
        List<String> paths = new ArrayList<>();

        for (String arg : args) {
            if (arg.equals("--xml")) {
                generateXml = true;
            } else {
                paths.add(arg);
            }
        }

        if (paths.size() != 1) {
            System.err.println("Uso: java Main <arquivo.jack | pasta> [--xml]");
            System.err.println("  Gera um .vm para cada .jack, na mesma pasta do .jack.");
            System.err.println("  --xml  também gera os XMLs de tokens e de sintaxe (entregas anteriores).");
            System.exit(1);
        }

        Path input = Path.of(paths.get(0));
        int failures = 0;

        try {
            List<Path> files = findJackFiles(input);

            if (files.isEmpty()) {
                System.err.println("Nenhum arquivo .jack encontrado em: " + input);
                System.exit(1);
            }

            for (Path file : files) {
                try {
                    if (generateXml) {
                        compileXml(file);
                    }

                    compile(file);
                } catch (IllegalArgumentException
                         | UnsupportedOperationException e) {
                    System.err.println(file + ": " + e.getMessage());
                    failures++;
                }
            }

        } catch (IOException e) {
            System.err.println("Erro de leitura/escrita: " + e.getMessage());
            System.exit(1);
        }

        if (failures > 0) {
            System.err.println(failures + " arquivo(s) com erro.");
            System.exit(1);
        }
    }

    private static List<Path> findJackFiles(Path input) throws IOException {
        if (Files.isDirectory(input)) {
            try (Stream<Path> files = Files.list(input)) {
                return files
                        .filter(p -> p.toString().endsWith(".jack"))
                        .sorted()
                        .toList();
            }
        }

        if (Files.isRegularFile(input)) {
            return List.of(input);
        }

        throw new IOException("Caminho não encontrado: " + input);
    }

    private static String baseName(Path file) {
        String fileName = file.getFileName().toString();
        return fileName.substring(0, fileName.length() - 5);
    }

    
    private static void compile(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        Path target = file.resolveSibling(baseName(file) + ".vm");

        try (CompilationEngine engine = new CompilationEngine(bytes, target)) {
            engine.compileClass();
        } catch (RuntimeException e) {
            // Não deixa um .vm incompleto na pasta
            Files.deleteIfExists(target);
            throw e;
        }

        System.out.println("VM gerado: " + target);
    }

    /** Comportamento das entregas anteriores: XML de tokens e de sintaxe. */
    private static void compileXml(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        String baseName = baseName(file);

        String tokensXml = XmlWriter.write(new Scanner(bytes));

        Parser parser = new Parser(bytes);
        parser.compileClass();

        Path syntaxTarget = file.resolveSibling(baseName + "P.xml");
        Files.writeString(syntaxTarget, parser.getXmlOutput(),
                StandardCharsets.UTF_8);

        Path parent = file.toAbsolutePath().getParent();
        Path tokensTarget = Path.of("output", "tokens")
                .resolve(parent.getFileName().toString())
                .resolve(baseName + "T.xml");

        Files.createDirectories(tokensTarget.getParent());
        Files.writeString(tokensTarget, tokensXml, StandardCharsets.UTF_8);

        System.out.println("XML sintático gerado: " + syntaxTarget);
        System.out.println("XML de tokens gerado: " + tokensTarget);
    }
}
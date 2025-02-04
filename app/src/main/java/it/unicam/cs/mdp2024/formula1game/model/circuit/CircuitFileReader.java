package it.unicam.cs.mdp2024.formula1game.model.circuit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CircuitFileReader {
    public static char[][] readFromFile(String filePath) throws IOException {
        List<char[]> trackLines = new ArrayList<>();
        int expectedWidth = -1;

        // Ottieni lo stream dalla risorsa nel classpath
        InputStream inputStream = CircuitFileReader.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) {
            throw new IOException("Circuito non trovato: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (expectedWidth == -1) {
                    expectedWidth = line.length();
                } else if (line.length() != expectedWidth) {
                    throw new IOException("Lunghezza riga non valida alla riga " + lineNumber + 
                                       ". Attesa: " + expectedWidth + ", trovata: " + line.length());
                }

                // Controlla caratteri validi
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (c != 'S' && c != '#' && c != '.' && c != '*' && c != '@') {
                        throw new IOException("Carattere non valido '" + c + "' alla posizione " + (i+1) + 
                                           " della riga " + lineNumber);
                    }
                }

                trackLines.add(line.toCharArray());
            }

            if (trackLines.isEmpty()) {
                throw new IOException("Il file del circuito è vuoto");
            }

            // Verifica presenza partenza e arrivo
            boolean hasStart = false;
            boolean hasFinish = false;
            int startCount = 0;
            int finishCount = 0;

            System.out.println("Debug: Verifica delle celle di partenza e arrivo...");
            for (int y = 0; y < trackLines.size(); y++) {
                char[] row = trackLines.get(y);
                for (int x = 0; x < row.length; x++) {
                    char c = row[x];
                    if (c == 'S') {
                        hasStart = true;
                        startCount++;
                        System.out.println("Debug: Trovata cella di partenza in posizione (" + x + "," + y + ")");
                    }
                    if (c == '*') {
                        hasFinish = true;
                        finishCount++;
                        System.out.println("Debug: Trovata cella di arrivo in posizione (" + x + "," + y + ")");
                    }
                }
            }

            System.out.println("Debug: Trovate " + startCount + " celle di partenza e " + finishCount + " celle di arrivo");

            if (!hasStart) {
                throw new IOException("Punto di partenza (S) non trovato nel circuito");
            }
            if (!hasFinish) {
                throw new IOException("Linea di arrivo (*) non trovata nel circuito");
            }

            return trackLines.toArray(new char[0][]);
        }
    }
}

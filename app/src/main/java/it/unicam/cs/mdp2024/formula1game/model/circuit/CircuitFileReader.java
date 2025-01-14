package it.unicam.cs.mdp2024.formula1game.model.circuit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CircuitFileReader {
    public static char[][] readFromFile(String filePath) throws IOException {
        List<char[]> trackLines = new ArrayList<>();
        int expectedWidth = -1;

        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim(); // Rimuove spazi iniziali e finali

                if (line.isEmpty()) {
                    continue; // Ignora righe vuote
                }

                if (expectedWidth == -1) {
                    expectedWidth = line.length(); // Imposta la larghezza attesa dalla prima riga
                } else if (line.length() != expectedWidth) {
                    throw new IOException("Invalid circuit: Row lengths are inconsistent.");
                }

                // Controlla che la riga contenga solo caratteri validi
                for (char c : line.toCharArray()) {
                    if (c != 'S' && c != '#' && c != '.' && c != '*') {
                        throw new IOException("Invalid character in circuit: " + c);
                    }
                }

                trackLines.add(line.toCharArray());
            }
        }

        // Verifica che ci siano almeno una partenza ('S') e un arrivo ('*')
        boolean hasStart = false;
        boolean hasFinish = false;

        for (char[] row : trackLines) {
            for (char c : row) {
                if (c == 'S')
                    hasStart = true;
                if (c == '*')
                    hasFinish = true;
            }
        }

        if (!hasStart) {
            throw new IOException("Invalid circuit: No starting points ('S') found.");
        }
        if (!hasFinish) {
            throw new IOException("Invalid circuit: No finish line ('*') found.");
        }

        // Converti la lista in una matrice
        return trackLines.toArray(new char[trackLines.size()][]);
    }
}

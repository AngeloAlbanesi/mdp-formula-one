package it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint;

import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;

import java.util.*;

/**
 * Gestisce lo stato dei checkpoint per ogni giocatore.
 * Tiene traccia di quali checkpoint sono stati attraversati e verifica
 * se un movimento attraversa un checkpoint.
 */
public class CheckpointManager {
    private final Map<IPlayer, Set<IPosition>> checkpointsReached;
    private final List<List<IPosition>> checkpointLines;
    private final Map<IPlayer, Integer> nextCheckpointIndex;
    private final ICircuit circuit;

    public CheckpointManager(ICircuit circuit) {
        this.circuit = circuit;
        this.checkpointsReached = new HashMap<>();
        this.checkpointLines = new DefaultCheckpointFinder(circuit.getGrid()).findCheckpointLines();
        this.nextCheckpointIndex = new HashMap<>();
    }

    /**
     * Inizializza il tracking dei checkpoint per un giocatore
     */
    public void initializePlayer(IPlayer player) {
        checkpointsReached.put(player, new HashSet<>());
        nextCheckpointIndex.put(player, 0);
    }

    /**
     * Verifica se il movimento da oldPosition a newPosition attraversa un checkpoint
     * e aggiorna lo stato se necessario
     */
    public boolean checkAndUpdateCheckpoints(IPlayer player, IPosition oldPosition, IPosition newPosition) {
        if (!checkpointsReached.containsKey(player)) {
            initializePlayer(player);
        }

        // Ottieni l'indice del prossimo checkpoint da attraversare
        int currentIndex = nextCheckpointIndex.get(player);
        
        // Se abbiamo attraversato tutti i checkpoint, non c'è altro da fare
        if (currentIndex >= checkpointLines.size()) {
            return false;
        }

        // Ottieni la linea del checkpoint corrente
        List<IPosition> currentCheckpointLine = checkpointLines.get(currentIndex);
        
        // Verifica se il movimento attraversa la linea del checkpoint
        if (intersectsCheckpointLine(oldPosition, newPosition, currentCheckpointLine)) {
            // Aggiungi tutte le posizioni di questo checkpoint ai checkpoint raggiunti
            checkpointsReached.get(player).addAll(currentCheckpointLine);
            
            // Aggiorna l'indice del prossimo checkpoint
            nextCheckpointIndex.put(player, currentIndex + 1);
            
            return true;
        }

        return false;
    }

    /**
     * Verifica se un segmento di movimento interseca una linea di checkpoint
     */
    private boolean intersectsCheckpointLine(IPosition start, IPosition end, List<IPosition> checkpointLine) {
        // Per ogni coppia di punti consecutivi nella linea del checkpoint
        for (int i = 0; i < checkpointLine.size() - 1; i++) {
            IPosition cp1 = checkpointLine.get(i);
            IPosition cp2 = checkpointLine.get(i + 1);
            
            // Verifica se il segmento di movimento interseca il segmento del checkpoint
            if (segmentsIntersect(start, end, cp1, cp2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se due segmenti si intersecano
     */
    private boolean segmentsIntersect(IPosition p1, IPosition p2, IPosition q1, IPosition q2) {
        // Implementazione semplificata: verifica se il movimento attraversa 
        // la linea del checkpoint considerando il movimento come un segmento rettilineo
        int p1x = p1.getColumn(), p1y = p1.getRow();
        int p2x = p2.getColumn(), p2y = p2.getRow();
        int q1x = q1.getColumn(), q1y = q1.getRow();
        int q2x = q2.getColumn(), q2y = q2.getRow();

        // Calcola l'orientamento dei triangoli formati dai punti
        int o1 = orientation(p1x, p1y, p2x, p2y, q1x, q1y);
        int o2 = orientation(p1x, p1y, p2x, p2y, q2x, q2y);
        int o3 = orientation(q1x, q1y, q2x, q2y, p1x, p1y);
        int o4 = orientation(q1x, q1y, q2x, q2y, p2x, p2y);

        // I segmenti si intersecano se gli orientamenti sono diversi
        return (o1 != o2 && o3 != o4);
    }

    /**
     * Calcola l'orientamento di tre punti (p, q, r)
     * Restituisce:
     * 0 -> Collineari
     * 1 -> Senso orario
     * 2 -> Senso antiorario
     */
    private int orientation(int px, int py, int qx, int qy, int rx, int ry) {
        int val = (qy - py) * (rx - qx) - (qx - px) * (ry - qy);
        if (val == 0) return 0;
        return (val > 0) ? 1 : 2;
    }

    /**
     * Restituisce la posizione del prossimo checkpoint da raggiungere per il giocatore
     */
    public IPosition getNextCheckpoint(IPlayer player) {
        if (!nextCheckpointIndex.containsKey(player)) {
            initializePlayer(player);
        }

        int index = nextCheckpointIndex.get(player);
        if (index >= checkpointLines.size()) {
            return null;
        }

        // Restituisce il punto centrale della linea del checkpoint
        List<IPosition> checkpointLine = checkpointLines.get(index);
        return checkpointLine.get(checkpointLine.size() / 2);
    }

    /**
     * Verifica se il giocatore ha attraversato tutti i checkpoint
     */
    public boolean hasCompletedAllCheckpoints(IPlayer player) {
        return nextCheckpointIndex.getOrDefault(player, 0) >= checkpointLines.size();
    }
}
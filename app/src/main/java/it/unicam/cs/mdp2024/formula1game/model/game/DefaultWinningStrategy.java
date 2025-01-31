package it.unicam.cs.mdp2024.formula1game.model.game;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementazione predefinita della strategia di vittoria.
 * La gara continua finché tutti i giocatori non hanno completato il giro
 * o sono diventati inattivi. Il vincitore è il primo giocatore che ha
 * raggiunto il traguardo.
 */
public class DefaultWinningStrategy implements IWinningStrategy {

    private final GameConfiguration config;
    private IPlayer firstToFinish;

    /**
     * Crea una nuova strategia di vittoria con configurazione personalizzata.
     * 
     * @param config la configurazione del gioco
     */
    public DefaultWinningStrategy(GameConfiguration config) {
        this.config = config;
        this.firstToFinish = null;
    }

    /**
     * Crea una nuova strategia di vittoria con configurazione predefinita.
     */
    public DefaultWinningStrategy() {
        this(new GameConfiguration());
    }

    @Override
    public IPlayer determineWinner(List<IPlayer> players,
            ICircuit circuit,
            Map<IPlayer, Integer> laps,
            int turnCount,
            int maxTurns) {
        // Verifica parametri
        if (players == null || circuit == null || laps == null) {
            throw new IllegalArgumentException("I parametri non possono essere null");
        }

        // Se c'è già un primo arrivato ed è ancora attivo, è il vincitore
        if (firstToFinish != null && firstToFinish.isActive()) {
            return firstToFinish;
        }

        // Se abbiamo raggiunto il limite di turni o tutti i giocatori hanno finito/sono
        // inattivi
        if (turnCount >= maxTurns || players.stream().noneMatch(IPlayer::isActive)) {
            return determineWinnerByPosition(players, circuit, laps);
        }

        return null; // La partita continua
    }

    @Override
    public boolean isGameOver(List<IPlayer> players,
            ICircuit circuit,
            Map<IPlayer, Integer> laps,
            int turnCount,
            int maxTurns) {
        // Verifica parametri
        if (players == null || circuit == null || laps == null) {
            throw new IllegalArgumentException("I parametri non possono essere null");
        }

        // Controlla se tutti i giocatori hanno completato il giro richiesto o sono
        // inattivi
        boolean allPlayersFinishedOrInactive = players.stream()
                .allMatch(player -> !player.isActive() || // giocatore inattivo (si è schiantato)
                        laps.get(player) >= config.getRequiredLaps() // ha completato i giri richiesti
                );

        // La partita termina se:
        // 1. Tutti i giocatori hanno finito o sono inattivi, oppure
        // 2. È stato raggiunto il limite massimo di turni, oppure
        // 3. Non ci sono più giocatori attivi
        return allPlayersFinishedOrInactive ||
                turnCount >= maxTurns ||
                players.stream().noneMatch(IPlayer::isActive);
    }

    @Override
    public boolean updateLaps(IPlayer player, Map<IPlayer, Integer> laps) {
        // Verifica parametri
        if (player == null || laps == null) {
            throw new IllegalArgumentException("I parametri non possono essere null");
        }
        if (!laps.containsKey(player)) {
            throw new IllegalArgumentException("Il giocatore non è presente nella mappa dei giri");
        }

        // Aggiorna il conteggio dei giri
        int currentLaps = laps.get(player);
        int newLaps = currentLaps + 1;
        laps.put(player, newLaps);

        // Se è il primo a completare i giri richiesti, memorizzalo
        if (newLaps >= config.getRequiredLaps() && firstToFinish == null) {
            firstToFinish = player;
            return true; // Segnala che il giocatore ha completato tutti i giri richiesti
        }

        return false;
    }

    /**
     * Determina il vincitore in base alla posizione quando si raggiunge il limite
     * di turni
     * o non ci sono più giocatori attivi.
     * 
     * @param players lista dei giocatori
     * @param circuit il circuito di gioco
     * @param laps    mappa dei giri completati per giocatore
     * @return il giocatore vincitore, o null se nessun vincitore
     */
    private IPlayer determineWinnerByPosition(List<IPlayer> players,
            ICircuit circuit,
            Map<IPlayer, Integer> laps) {
        // Trova il numero massimo di giri completati da un giocatore attivo
        int maxLaps = players.stream()
                .filter(IPlayer::isActive)
                .mapToInt(laps::get)
                .max()
                .orElse(-1);

        if (maxLaps == -1)
            return null;

        // Filtra i giocatori con il massimo numero di giri
        List<IPlayer> playersAtMaxLaps = players.stream()
                .filter(p -> p.isActive() && laps.get(p) == maxLaps)
                .collect(Collectors.toList());

        if (playersAtMaxLaps.isEmpty())
            return null;
        if (playersAtMaxLaps.size() == 1)
            return playersAtMaxLaps.get(0);

        // In caso di parità per giri, usa la distanza dalla posizione di riferimento
        final IPosition referencePosition = getReferencePosition(circuit);

        return playersAtMaxLaps.stream()
                .min((p1, p2) -> {
                    int dist1 = p1.getCar().getPosition().manhattanDistanceTo(referencePosition);
                    int dist2 = p2.getCar().getPosition().manhattanDistanceTo(referencePosition);
                    return Integer.compare(dist1, dist2);
                })
                .orElse(null);
    }

    /**
     * Ottiene la posizione di riferimento per il calcolo delle distanze in caso di
     * parità.
     * 
     * @param circuit il circuito di gioco
     * @return la posizione di riferimento
     */
    private IPosition getReferencePosition(ICircuit circuit) {
        try {
            return circuit.getStartPositions().get(config.getReferenceStartPosition());
        } catch (IndexOutOfBoundsException e) {
            // Se la posizione di riferimento non è valida, usa la terza posizione di
            // partenza
            return circuit.getStartPositions().get(3);
        }
    }
}
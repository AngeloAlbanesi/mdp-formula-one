# Piano di Rifattorizzazione per Game.java

## Obiettivo

Rendere la classe `Game` responsabile solo del flusso generale della partita (SRP), demandando le logiche specifiche a classi esterne.

## Interventi Principali

1. **Gestione Turni (ITurnManager)**  
   - Spostare in questa interfaccia (e relative implementazioni) la logica di avanzamento del turno e selezione del giocatore corrente.

2. **Determinazione Vincitore (IWinningStrategy)**  
   - Delegare a un'implementazione di questa interfaccia la logica di calcolo del vincitore.

3. **Caricamento Giocatori (IPlayerLoader)**  
   - Delegare a questa interfaccia la lettura dei dati dei giocatori (file o DB), rimuovendo da `Game` ogni dettaglio di caricamento.

4. **Validazione Mosse (ICircuit / ICar)**  
   - Delegare la logica di validazione posizioni, collisioni e fisica al circuito (`ICircuit`) e alle macchine (`ICar`).

## Struttura Semplificata

- **Classe `Game`**  
  - Mantiene:
    - Riferimenti a `ITurnManager`, `IWinningStrategy`, `ICircuit`.
    - Metodi chiave per il flusso:
      - `start()`: avvia la partita e richiama `executeTurn()` finché `isGameOver()` non è vero.
      - `executeTurn()`: coordina l'esecuzione del turno tramite `ITurnManager`.
      - `isGameOver()`: controlla stato finale partita.
      - `getWinner()`: richiede a `IWinningStrategy` il vincitore.
  - Non include logiche di: caricamento giocatori, calcolo del vincitore, fisica e validazione mossa.

- **Interfaccia `ITurnManager`**  
  - Methods:
    - `getCurrentPlayer()`
    - `nextTurn()`
    - `resetTurn()`
  - Implementazione custom per definire la rotazione dei turni.

- **Interfaccia `IWinningStrategy`**  
  - Methods:
    - `calculateWinner(List<IPlayer> players, ICircuit circuit)`
  - Implementazioni possibili: primo che taglia il traguardo, punteggio accumulato, ecc.

- **Interfaccia `IPlayerLoader`**  
  - Methods:
    - `List<IPlayer> loadPlayers()`
  - Implementazioni possibili: file, DB, input utente.

## Passaggi di Rifattorizzazione

1. **Separare Metodi**  
   - Rimuovere tutto il codice da `Game` che gestisce i turni e inserirlo in `ITurnManager`/`TurnManager`.
   - Rimuovere la determinazione del vincitore da `Game` e inserirla in `IWinningStrategy`.
   - Rimuovere il caricamento diretto dei giocatori e inserirlo in `IPlayerLoader`.

2. **Iniezione Dipendenze**  
   - Far sì che `Game` riceva `ITurnManager`, `IWinningStrategy` e `ICircuit` come parametri (costruttore o setter).

3. **Responsabilità Distinte**  
   - `Game` si occupa solamente di coordinare l’esecuzione del turno, verificare se la partita è conclusa e chiedere all’algoritmo di winning di calcolare il vincitore.

4. **Test e Convalida**  
   - Verificare che ogni parte funzioni isolatamente e che i test su `Game` controllino solo il flusso generale.

Con questa architettura `Game` rimane un motore di orchestrazione, delegando le singole responsabilità a componenti specializzate.

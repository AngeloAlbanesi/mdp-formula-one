package it.unicam.cs.mdp2024.formula1game.model.strategy;

/**
 * Classe che rappresenta i pesi utilizzati per bilanciare i diversi fattori
 * nella strategia di movimento.
 */
public class MovementWeights {
    private double pathEfficiencyWeight; // Peso per l'ottimizzazione del percorso
    private double speedControlWeight; // Peso per il controllo della velocità
    private double collisionAvoidanceWeight; // Peso per l'evitamento delle collisioni
    private double checkpointAlignmentWeight; // Peso per l'allineamento ai checkpoint

    /**
     * Costruttore con valori di default bilanciati.
     */
    public MovementWeights() {
        this(1.0, 1.0, 1.0, 1.0);
    }

    /**
     * Costruttore con pesi personalizzati.
     */
    public MovementWeights(double pathEfficiency, double speedControl,
            double collisionAvoidance, double checkpointAlignment) {
        this.pathEfficiencyWeight = pathEfficiency;
        this.speedControlWeight = speedControl;
        this.collisionAvoidanceWeight = collisionAvoidance;
        this.checkpointAlignmentWeight = checkpointAlignment;
    }

    public double getPathEfficiencyWeight() {
        return pathEfficiencyWeight;
    }

    public void setPathEfficiencyWeight(double pathEfficiencyWeight) {
        this.pathEfficiencyWeight = pathEfficiencyWeight;
    }

    public double getSpeedControlWeight() {
        return speedControlWeight;
    }

    public void setSpeedControlWeight(double speedControlWeight) {
        this.speedControlWeight = speedControlWeight;
    }

    public double getCollisionAvoidanceWeight() {
        return collisionAvoidanceWeight;
    }

    public void setCollisionAvoidanceWeight(double collisionAvoidanceWeight) {
        this.collisionAvoidanceWeight = collisionAvoidanceWeight;
    }

    public double getCheckpointAlignmentWeight() {
        return checkpointAlignmentWeight;
    }

    public void setCheckpointAlignmentWeight(double checkpointAlignmentWeight) {
        this.checkpointAlignmentWeight = checkpointAlignmentWeight;
    }
}
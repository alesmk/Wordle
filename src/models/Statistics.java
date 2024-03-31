package models;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p>
 * Progetto finale
 * </p>
 * 
 * La classe <b> Statistics </b> rappresenta le statistiche di un utente.
 * 
 * @author Alessia Anile
 */
public class Statistics {

	/** Numero di partite giocate */
	private int playedGamesNum;
	/** Numero di partite vinte */
	private int wins;
	/** Percentuale di partite vinte */
	private float winsPerc;
	/** Lunghezza dell'ultimo streak di vittorie */
	private int lastStreak;
	/** Lunghezza del massimo streak di vittorie */
	private int maxStreak;
	/** Array contenente la guess distribution */
	private int[] guessDistribution;

	/**
	 * Costruttore vuoto della classe Statistics per inizializzare le statistiche a 0.
	 */
	public Statistics() {
		this.playedGamesNum = 0;
		this.winsPerc = 0;
		this.lastStreak = 0;
		this.maxStreak = 0;
		this.guessDistribution = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
	}
	
	/**
	 * Costruttore della classe Statistics
	 * @param playedGamesNum numero di partite giocate
	 * @param wins numero di partite vinte
	 * @param winsPerc percentuale di partite vinte
	 * @param lastStreak lunghezza dell'ultimo streak di vittorie
	 * @param maxStreak lunghezza del massimo streak di vittorie
	 * @param guessDistribution array contenente la guess distribution
	 */
	public Statistics(int playedGamesNum, int wins, float winsPerc, int lastStreak, int maxStreak,
			int[] guessDistribution) {
		this.playedGamesNum = playedGamesNum;
		this.winsPerc = winsPerc;
		this.lastStreak = lastStreak;
		this.maxStreak = maxStreak;
		this.guessDistribution = guessDistribution;
	}

	/**
	 * @return numero di partite giocate
	 */
	public int getPlayedGamesNum() {
		return playedGamesNum;
	}

	/**
	 * @param playedGamesNum numero di partite giocate da settare
	 */
	public void setPlayedGamesNum(int playedGamesNum) {
		this.playedGamesNum = playedGamesNum;
	}

	/**
	 * @return numero di partite vinte 
	 */
	public int getWins() {
		return wins;
	}

	/**
	 * @param wins numero di partite vinte da settare
	 */
	public void setWins(int wins) {
		this.wins = wins;
	}

	/**
	 * @return percentuale di partite vinte
	 */
	public float getWinsPerc() {
		return winsPerc;
	}

	/**
	 * @param winsPerc percentuale di partite vinte da settare
	 */
	public void setWinsPerc(float winsPerc) {
		this.winsPerc = winsPerc;
	}
	
	/**
	 * @return lunghezza dell'ultimo streak di vittorie
	 */
	public int getLastStreak() {
		return lastStreak;
	}

	/**
	 * @param lastStreak lunghezza dell'ultimo streak di vittorie da settare
	 */
	public void setLastStreak(int lastStreak) {
		this.lastStreak = lastStreak;
	}

	/**
	 * @return lunghezza del massimo streak di vittorie
	 */
	public int getMaxStreak() {
		return maxStreak;
	}

	/**
	 * @param maxStreak lunghezza del massimo streak di vittorie da settare
	 */
	public void setMaxStreak(int maxStreak) {
		this.maxStreak = maxStreak;
	}

	/**
	 * 
	 * @return stringa contenente la guess distribution, concatenando ogni valore con un ";"
	 */
	public String getStringGuessDistribution() {
		String gS = "";
		for(int i=0; i<12; i++)
			gS += guessDistribution[i]+";";
		return gS;
	}
	
	/**
	 * @return array contenente la guess distribution
	 */
	public int[] getGuessDistribution() {
		return guessDistribution;
	}

	/**
	 * @param guessDistribution array contenente la guess distribution da settare

	 */
	public void setGuessDistribution(int[] guessDistribution) {
		this.guessDistribution = guessDistribution;
	}

}
package models;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p>
 * Progetto finale
 * </p>
 * 
 * La classe <b> UserStatistics </b> rappresenta le informazioni pubbliche di un utente.
 * 
 * @author Alessia Anile
 */
public class UserStatistics {

	/** Username dell'utente */
	private String username;
	/** Statistiche dell'utente */
	private Statistics stats;

	/**
	 * Costruttore vuoto della classe UserStatistics
	 */
	public UserStatistics() {

	}

	/**
	 * Costruttore della classe UserStatistics
	 * @param username username dell'utente
	 * @param stats statistiche dell'utente
	 */
	public UserStatistics(String username, Statistics stats) {
		this.username = username;
		this.stats = stats;
	}

	/**
	 * @return username dell'utente
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username username dell'utente da settare
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return statistiche dell'utente
	 */
	public Statistics getStats() {
		return stats;
	}

	/**
	 * @param stats statistiche dell'utente da settare
	 */
	public void setStats(Statistics stats) {
		this.stats = stats;
	}
}

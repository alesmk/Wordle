package models;

import java.util.Calendar;
import java.util.Date;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p>
 * Progetto finale
 * </p>
 * 
 * La classe <b> User </b> rappresenta le informazioni relative a un utente.
 * 
 * @author Alessia Anile
 */
public class User extends UserStatistics{
	/** Hash della password dell'utente */
	private int hashedPassword;
	/** Data dell'ultima partita dell'utente */
	private Date lastGameDate;
	/** Stato di attività dell'utente. False se inattivo, true se attivo */
	private boolean active; 

	/**
	 * Costruttore vuoto della classe User
	 */
	public User() {
		
	}
	
	/**
	 * Costruttore della classe User
	 * @param username username dell'utente
	 * @param hashedPassword hash della password dell'utente
	 * @param stats statistiche dell'utente
	 * @param lastGameDate data dell'ultima partita dell'utente
	 * @param active stato di attività dell'utente
	 */
	public User(String username, int hashedPassword, Statistics stats, Date lastGameDate, boolean active){
		super(username, stats); 
		this.hashedPassword = hashedPassword;
		this.active = active;		

		// Se lastGameDate non è valida, imposta a 01-01-1970 01:00:00
		if(lastGameDate == null) 
			this.lastGameDate = new Date(0L);
		else
			this.lastGameDate = lastGameDate;
	}

	/**
	 * @return data dell'ultima partita dell'utente
	 */ 
	public Date getLastGameDate() {
		return lastGameDate;
	}
	
	/**
	 * Setta la data dell'ultima partita dell'utente al momento corrente
	 */
	public void setLastGameDate() {
		this.lastGameDate = Calendar.getInstance().getTime();
	}
	
	/**
	 * @return hash della password dell'utente
	 */
	public int getHashedPassword() {
		return hashedPassword;
	}

	/**
	 * @return stato di attività dell'utente
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active stato di attività dell'utente da settare
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
}

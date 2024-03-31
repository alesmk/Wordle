package models;

import java.util.Date;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p>
 * Progetto finale
 * </p>
 * 
 * La classe <b> History </b> rappresenta lo storico delle Secret Word.
 * 
 * @author Alessia Anile
 */
public class History {
	/** Data in cui è stata impostata la Secret Word */
	private Date date;
	/** Secret Word */
	private String secretWord;
	/** Numero della Secret Word */
	private int number;
		
	/**
	 * Costruttore della classe History
	 * @param date data in cui è stata impostata la Secret Word
	 * @param secretWord Secret Word
	 * @param number numero della Secret Word
	 */
	public History(Date date, String secretWord, int number) {
		this.date = date;
		this.secretWord = secretWord;
		this.number = number;
	}
	
	/**
	 * @return data in cui è stata impostata la Secret Word
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * @param date data in cui è stata impostata la Secret Word da settare
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return Secret Word
	 */
	public String getSecretWord() {
		return secretWord;
	}
	
	/**
	 * @param secretWord Secret Word da settare
	 */
	public void setSecretWord(String secretWord) {
		this.secretWord = secretWord;
	}
	
	/**
	 * @return numero della Secret Word
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * @param number numero della Secret Word da settare
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	
}

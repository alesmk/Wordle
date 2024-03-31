package models;

import java.util.Date;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p>
 * Progetto finale
 * </p>
 * 
 * La classe <b> Game </b> rappresenta le informazioni relative alla partita in
 * corso. Le informazioni sono: la Secret Word, il relativo numero e la data del
 * suo ultimo aggiornamento.
 * 
 * @author Alessia Anile
 */
public class Game {

	/** Data dell'ultimo aggiornamento della Secret Word */
	private Date lastUpdate;
	/** Secret Word corrente */
	private String secretWord;
	/** Numero della Secret Word */
	private int wordNumber;

	/** 
	 * Costruttore della classe Game
	 * @param lastUpdate data dell'ultimo aggiornamento della Secret Word
	 * @param secretWord Secret Word
	 * @param wordNumber numero della Secret Word
	 */
	public Game(Date lastUpdate, String secretWord, int wordNumber) {
		this.lastUpdate = lastUpdate;
		this.secretWord = secretWord;
		this.wordNumber = wordNumber;
	}

	/**
	 * @return data dell'ultimo aggiornamento della Secret Word
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate data dell'ultimo aggiornamento della Secret Word da settare
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
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
	public int getWordNumber() {
		return wordNumber;
	}

	/**
	 * @param wordNumber numero della Secret Word da settare
	 */
	public void setWordNumber(int wordNumber) {
		this.wordNumber = wordNumber;
	}

}

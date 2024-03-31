package models;

import java.util.ArrayList;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p>
 * Progetto finale
 * </p>
 * 
 * La classe <b> Sharing </b> rappresenta le informazioni relative ai risultati condivisi delle partite.
 * 
 * @author Alessia Anile
 */
public class Sharing {

	/** Numero della Secret Word relativa */
	private int wordNumber;
	/** Numero di tentativi effettuati */
	private int numTentativi;
	/** Struttura dati contenente gli indizi per ogni tentativo */
	private ArrayList<String> indiziTot;

	/**
	 * Costruttore della classe Sharing
	 * @param wordNumber numero della Secret Word relativa
	 * @param numTentativi numero di tentativi effettuati
	 * @param indiziTot struttura dati contenente gli indizi per ogni tentativo
	 */
	public Sharing(int wordNumber, int numTentativi, ArrayList<String> indiziTot) {
		this.wordNumber = wordNumber;
		this.numTentativi = numTentativi;
		this.indiziTot = indiziTot;
	}

	/**
	 * @return numero della Secret Word relativa
	 */
	public int getWordNumber() {
		return wordNumber;
	}

	/**
	 * @param wordNumber numero della Secret Word relativa da settare
	 */
	public void setWordNumber(int wordNumber) {
		this.wordNumber = wordNumber;
	}

	/**
	 * @return numero di tentativi effettuati
	 */
	public int getNumTentativi() {
		return numTentativi;
	}

	/**
	 * @param numTentativi numero di tentativi effettuati da settare
	 */
	public void setNumTentativi(int numTentativi) {
		this.numTentativi = numTentativi;
	}

	/**
	 * @return struttura dati contenente gli indizi per ogni tentativo
	 */
	public ArrayList<String> getIndiziTot() {
		return indiziTot;
	}

	/**
	 * @param indiziTot struttura dati contenente gli indizi per ogni tentativo da settare
	 */
	public void setIndiziTot(ArrayList<String> indiziTot) {
		this.indiziTot = indiziTot;
	}

}

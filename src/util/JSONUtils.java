package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import models.User;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p> Progetto finale </p>
 * 
 * La classe <b> JSONUtils </b> contiene metodi di utilità per operare sui file JSON.
 * 
 * @author Alessia Anile
 */

public class JSONUtils extends Utils {

	/**
	 * Metodo generico per la deserializzazione di un file JSON
	 * @param <T> tipo atteso per la deserializzazione
	 * @param filename nome del file da deserializzare
	 * @param typeToken token del tipo atteso per la deserializzazione
	 * @return oggetto del tipo atteso risultante dalla deserializzazione
	 * @throws IOException in caso di errori I/O durante l'utilizzo del reader
	 */
	public static synchronized <T> T readJsonFile(String filename, TypeToken<T> typeToken) throws IOException {
		// Verifico che il filename sia corretto
		File file = new File(filename);
		if (!file.exists() || file.isDirectory()) {
			System.err.printf("Errore: %s non è un file valido!\n", filename);
			System.exit(1);
		}

		// Eseguo l'apertura e la deserializzazione del file
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
		T content = gson.fromJson(reader, typeToken.getType());

		closeReader(reader);
		return content;
	}

	/**
	 * Metodo generico per la serializzazione di un file JSON
	 * @param <T> tipo utilizzato per la serializzazione
	 * @param filename nome del file da serializzare
	 * @param content contenuto da serializzare nel file JSON
	 * @throws IOException in caso di errori I/O durante l'utilizzo del writer
	 */
	public static synchronized <T> void writeJsonFile(String filename, T content) throws IOException {
		Writer writer = new FileWriter(filename);
		
		// Eseguo la deserializzazione del file
		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat(DATE_FORMAT).create();
		gson.toJson(content, writer);
		
		closeWriter(writer);
	}
	

	/**
	 * Metodo per aggiornare il file JSON degli utenti evitando inconsistenze
	 * @param usersFile filename del file degli utenti
	 * @param player utente da aggiornare o aggiungere
	 * @throws IOException in caso di errori I/O durante la lettura / scrittura del file JSON
	 */
	public static synchronized void updateUsersFile(String usersFile, User player) throws IOException {	
		
		// Leggo il file JSON degli utenti aggiornato e cerco l'utente
		ArrayList<User> usersList = readJsonFile(usersFile, new TypeToken<ArrayList<User>>() {});
		User user = usersList.stream().filter((u -> u.getUsername().equals(player.getUsername()))).findAny().orElse(null);

		if(user != null) 
			usersList.set(usersList.indexOf(user), player);
		else 
			usersList.add(player);
		
		// Riporto le modifiche sul file JSON degli utenti
		writeJsonFile(usersFile, usersList);		
	}

}

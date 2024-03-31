package server;

import models.History;
import models.ServerConfig;
import util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.reflect.TypeToken;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p> Progetto finale </p>
 * 
 * La classe <b> WordHandler </b> gestisce il gestore dell'aggiornamento delle Secret Words.
 * 
 * @author Alessia Anile
 */
public class WordHandler implements Runnable {
	/** Nome del file di configurazione */
	private String configFile;
	/** Oggetto contenente la configurazione del server */
	private ServerConfig config;
	/** Nome del file contenente lo storico delle Secret Words */
	private String historyFile;
	/** Struttura dati contenente lo storico delle Secret Words */
	private ArrayList<History> historyList;
	/** Array di stringhe contenente le parole del vocabolario */ 
	private String[] vocabulary;
	
	/**
	 * Costruttore della classe WordHandler
	 * @param configFile nome del file di configurazione
	 * @throws IOException in caso di errori I/O durante la lettura dei file JSON
	 */
	public WordHandler(String configFile) throws IOException {
		this.configFile = configFile;
		this.config = JSONUtils.readJsonFile(configFile, new TypeToken<ServerConfig>() {
		});
		this.historyFile = config.getHistoryFile();
		this.historyList = JSONUtils.readJsonFile(historyFile, new TypeToken<ArrayList<History>>() {
		});
		this.vocabulary = Utils.readFile(config.getVocabularyFile());

		System.out.println("[WH] Avvio WordHandler");
	}

	/**
	 * Metodo del task per effettuare periodicamente l'aggiornamento delle Secret Word. 
	 * Sceglie una nuova Secret Word e la imposta.
	 */
	public void run() {
		
		// Genero un numero casuale che sarà l'indice della nuova Secret Word nel file del vocabolario
		int random = ThreadLocalRandom.current().nextInt(0, vocabulary.length);
		
		// Raccolgo le informazioni necessarie per aggiungere una nuova Secret Word alla historyList: la data dell'aggiornamento,
		// la parola stessa e il relativo numero
		String secretWord = vocabulary[random];
		Date now = Utils.getNowDate();
		int number = historyList.size() != 0 ? historyList.get(historyList.size() - 1).getNumber() + 1 : 1; 
		
		config.getGame().setSecretWord(secretWord);
		config.getGame().setLastUpdate(now);
		config.getGame().setWordNumber(number);

		historyList.add(new History(now, secretWord, number));

		// Aggiorno il file di configurazione del server, dato che contiene informazioni sulla Secret Word attuale, e 
		// il file dello storico delle Secret Words.
		try {
			JSONUtils.writeJsonFile(configFile, config);
			JSONUtils.writeJsonFile(historyFile, historyList);
		} catch (IOException e) {
			System.err.println("[WH] Errore scrittura History file.");
			System.exit(1);
		}

		System.out.println("\n[WH] Nuova SW impostata\n");
	}
}

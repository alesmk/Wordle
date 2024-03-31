package server;

import util.*;
import models.ServerConfig;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.reflect.TypeToken;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p>
 * Progetto finale
 * </p>
 * 
 * La classe <b> ServerMain </b> avvia il server effettuandone la
 * configurazione iniziale.
 * 
 * @author Alessia Anile
 */
public class ServerMain {

	private static ArrayList<Socket> connectedClients;
	
	public static void main(String[] args) {
		System.out.println("\t-----WORDLE SERVER-----");

		System.out.println("\n[SM] Server in esecuzione...");

		// Nome del file di configurazione del server
		String serverConfigFile = "serverConfig.json";
		// Struttura dati contenete tutti i socket dei client connessi
		connectedClients = new ArrayList<Socket>();
		
		ServerConfig serverConfig = null;
		ExecutorService clients = null;
		TerminationHandler t = null;
		Thread wordHandler = null;

		// Leggo il file di configurazione e ne memorizzo il contenuto in serverConfig
		try {
			serverConfig = JSONUtils.readJsonFile(serverConfigFile, new TypeToken<ServerConfig>() {
			});
		} catch (IOException e) {
			System.err.println("[SM] Impossibile avviare ServerMain. Errore nella lettura della configurazione.");
			System.exit(1);
		}

		// Creo il socket del server
		try (ServerSocket serverSocket = new ServerSocket(serverConfig.getPort())) {
			
			// Inizializzo il thread pool per la gestione dei client
			clients = Executors.newCachedThreadPool();

			// Inizializzo e avvio il gestore dell'aggiornamento delle Secret Words
			try {
				long updateFrequency = Utils.getUpdateFrequencyMillis(serverConfig);
				long lastUpdate = Utils.getMillis(serverConfig.getGame().getLastUpdate());
				
				// Calcolo il momento del prossimo aggiornamento e sottraggo il momento attuale
				long initialDelay = (lastUpdate+updateFrequency) - Utils.getMillis(Utils.getNowDate());
								
				ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();				
				WordHandler _wordHandler = new WordHandler(serverConfigFile);
				wordHandler = new Thread(_wordHandler);
				scheduler.scheduleAtFixedRate(wordHandler, initialDelay, updateFrequency, TimeUnit.MILLISECONDS);
			} catch (IOException e) {
				System.err.println("[SM] Impossibile avviare WordHandler");
				System.exit(1);
			}
			
			// Inizializzo il terminatore del server
			t = new TerminationHandler(serverSocket, connectedClients, clients, wordHandler);
			Runtime.getRuntime().addShutdownHook(t);

			
			// Il server si pone in attesa di nuove connessioni e le avvia
			while (!t.isTerminated()) {
				try {
					Socket clientSocket = serverSocket.accept();
					connectedClients.add(clientSocket);
					clients.execute(new Session(serverConfigFile, clientSocket));
				} catch (IOException e) {
					if(!t.isTerminated())
						System.err.println("[SM] Eccezione... " + e.getLocalizedMessage());
				}
			}

		} catch (BindException e) {
			System.err.println("[SM] Impossibile avviare ServerMain. Porta occupata.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("[SM] Impossibile avviare ServerMain.");
			System.exit(1);
		}

		System.out.println("[SM] Terminato.");

	}
	
	/**
	 * Rimuove il socket dalla struttura dati connectedClients
	 * @param clientSocket socket da rimuovere
	 */
	public static void disconnect(Socket clientSocket) {
		connectedClients.remove(clientSocket);
	}

}

package server;

import util.*;
import models.ServerConfig;
import models.Statistics;
import models.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.google.gson.reflect.TypeToken;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p>
 * Progetto finale
 * </p>
 * 
 * La classe <b> Session </b> gestisce la comunicazione e l'interazione con il
 * client, lato server.
 * 
 * @author Alessia Anile
 */
public class Session extends Thread {
	/** Socket del client connesso */
	private Socket clientSocket;
	/** Informazioni sull'utente autenticato */
	private User player;
	/** Nome del file contenente il vocabolario da utilizzare */
	private String[] vocabulary;
	/** Nome del file contenente le informazioni relative agli utenti */
	private String usersFile;
	/** Nome del file di configurazione del server */
	private String serverConfigFile;
	/** Configurazione del server */
	private ServerConfig serverConfig;
	/** Reader utilizzato per la lettura dal socket */
	private BufferedReader reader;
	/** Writer utilizzato per la scrittura sul socket */
	private BufferedWriter writer;
	/** Numero di tentativi effettuati dall'utente */
	private int numTentativi;
	/** Flag che indica se l'utente ha sospeso il gioco */
	private boolean quit;
	/** Flag che indica se l'utente ha condiviso il risultato della partita */
	private boolean shared;
	/** Struttura dati contenente gli indizi per ogni tentativo */
	private ArrayList<String> indiziTot;
	/** Numero massimo di tentativi permessi */
	private static final int N_MAX_TENTATIVI = 12;

	/**
	 * Costruttore della classe Session
	 * 
	 * @param serverConfigFile nome del file di configurazione del server
	 * @param clientSocket     socket del client connesso
	 * @throws IOException in caso di errori I/O durante la lettura dal file JSON
	 */
	public Session(String serverConfigFile, Socket clientSocket) throws IOException {
		this.player = new User();
		this.serverConfigFile = serverConfigFile;
		this.clientSocket = clientSocket;
		this.reader = Utils.newReader(clientSocket);
		this.writer = Utils.newWriter(clientSocket);

		this.serverConfig = JSONUtils.readJsonFile(serverConfigFile, new TypeToken<ServerConfig>() {
		});
		this.usersFile = serverConfig.getUsersFile();

		this.vocabulary = Utils.readFile(serverConfig.getVocabularyFile());

		this.numTentativi = 0;
		this.quit = false;
		this.shared = false;
		this.indiziTot = new ArrayList<String>();
	}

	/**
	 * @param clientSocket socket del client connesso da settare
	 */
	public void setSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	/**
	 * @return socket del client connesso
	 */
	public Socket getSocket() {
		return this.clientSocket;
	}

	/**
	 * Metodo da eseguire per gestire la sessione
	 */
	@Override
	public void run() {
		System.out.println(Utils.ANSI_GREEN + "\n[S] Avvio connessione" + Utils.ANSI_RESET);
		int comando, esito = 0;
		String mess = "", indizi = "", username, password;
		String[] splittedMess;

		// Leggo il comando dal client
		try {
			mess = Utils.read(reader);
		} catch (IOException e) {
			System.err.println("[S] Errore lettura.");
			return;
		}

		// Fase di autenticazione
		splittedMess = mess.split(";");
		comando = Integer.parseInt(splittedMess[2]);
		username = splittedMess[0];
		password = splittedMess[1];

		// Effettuo l'hashing della password
		int hashedPsw = password.hashCode();

		if (comando == 1)
			esito = register(username, hashedPsw);
		else if (comando == 2)
			esito = login(username, hashedPsw);

		System.out.println("[S] Invio esito: " + esito);

		// Invio l'esito al client
		try {
			Utils.write(writer, esito);
		} catch (IOException e) {
			System.err.println("[S] Errore scrittura.");
			if (player != null && player.isActive())
				terminateSession();
			return;
		}

		// Se non ho eseguito login con successo, termino la connessione
		if (esito != 1)
			return;

		// FASE DI AUTENTICAZIONE COMPLETATA
		esito = 0;
		System.out.println(Utils.ANSI_PURPLE + "[S] [" + player.getUsername() + "] Fase di autenticazione completata"
				+ Utils.ANSI_RESET);
		do {

			// Leggo la richiesta del client
			try {
				mess = Utils.read(reader);
			} catch (IOException e) {
				System.err.println("[S] [" + player.getUsername() + "] Errore lettura.");
				terminateSession();
				return;
			}

			System.out.println("\n[S] [" + player.getUsername() + "] Ricevuto: " + mess);
			comando = Integer.parseInt(mess);

			// Aggiorno la configurazione
			try {
				serverConfig = JSONUtils.readJsonFile(serverConfigFile, new TypeToken<ServerConfig>() {
				});
			} catch (IOException e) {
				System.err.println("[S] [" + player.getUsername() + "] Errore scrittura.");
				terminateSession();
				return;
			}

			if (comando == 2) {
				mess = sendMeStatistics();
				// Invio la risposta al client
				try {
					Utils.write(writer, mess);
				} catch (IOException e) {
					System.err.println("[S] [" + player.getUsername() + "] Errore scrittura.");
					terminateSession();
					return;
				}

				System.out.println("[S] [" + player.getUsername() + "] Inviato: " + mess);
				continue;
			} else if (comando == 1)
				esito = playWORDLE();
			else if (comando == 3)
				esito = share();
			else if (comando == 5)
				esito = logout(username);
			else
				continue;

			// Invio l'esito al client
			try {
				Utils.write(writer, esito);
			} catch (IOException e) {
				System.err.println("[S] [" + player.getUsername() + "] Errore scrittura.");
				terminateSession();
				return;
			}

			System.out.println("[S] [" + player.getUsername() + "] Inviato esito: " + esito);

			// Se l'utente ha richiesto di giocare e può farlo
			if (comando == 1 && esito > 0) {
				while (numTentativi < N_MAX_TENTATIVI) {

					indizi = receiveWords();

					if (indizi == null) {
						return;
					} else if (indizi.length() != 1) {
						indiziTot.add(indizi);
						esito = 1;
					} else {
						esito = Integer.parseInt(indizi);
					}

					try {
						Utils.write(writer, esito);
					} catch (IOException e) {
						System.err.println("[S] [" + player.getUsername() + "] Errore scrittura.");
						terminateSession();
						return;
					}

					if (esito == 5) {
						quit = true;
						break;
					} else if (esito == 0) {
						updateStatistics(true, numTentativi);
						indiziTot.add("0");
						break;
					} else if (esito > 1) {
						continue;
					}

					try {
						Utils.write(writer, indizi);
					} catch (IOException e) {
						System.err.println("[S] [" + player.getUsername() + "] Errore scrittura.");
						terminateSession();
						return;
					}

					numTentativi++;
				}
				if (numTentativi == 12)
					updateStatistics(false, -1);

			}
		} while (comando != 5);

		// A questo punto termino la sessione ed eseguo il logout
		if (terminateSession() == -1) {  
			System.err.println("[S] [" + player.getUsername() + "] Errore durante il logout");
		}

		System.out.println(Utils.ANSI_GREEN + "*BYE BYE " + player.getUsername() + "*" + Utils.ANSI_RESET);

	}

	/**
	 * Metodo per effettuare la registrazione lato server
	 * 
	 * @param username  username comunicato
	 * @param hashedPsw hash della password comunicata
	 * @return valore che indica se è l'operazione è andata a buon fine
	 */
	private int register(String username, int hashedPsw) {
		// Se la password è vuota, errore
		if (hashedPsw == 0) {
			ServerMain.disconnect(clientSocket);
			return -2;
		}
		
		ArrayList<User> usersList=null;
		try {
			usersList = JSONUtils.readJsonFile(usersFile, new TypeToken<ArrayList<User>>() {
			});
		} catch (IOException e) {
			System.err.println("[S] Errore scrittura sul file di configurazione.");
			ServerMain.disconnect(clientSocket);
			return -1;
		}
		
		// Se esiste già un utente con lo username passato, errore
		if (usersList.stream().anyMatch(user -> user.getUsername().equals(username))) {
			ServerMain.disconnect(clientSocket);
			return -1;
		}

		// Aggiungo un nuovo utente a userList e sovrascrivo il file JSON degli utenti
		User user = new User(username, hashedPsw, new Statistics(), null, false);
		try {
			JSONUtils.updateUsersFile(usersFile, user);
		} catch (IOException e) {
			System.err.println("[S] Errore scrittura sul file di configurazione.");
			ServerMain.disconnect(clientSocket);
			return -1;
		}

		ServerMain.disconnect(clientSocket);
		return 2;
	}

	/**
	 * Metodo per accedere a un account esistente
	 * 
	 * @param username  username comunicato
	 * @param hashedPsw hash della password comunicata
	 * @return valore che indica se è l'operazione è andata a buon fine
	 */
	private int login(String username, int hashedPsw) {
		ArrayList<User> usersList=null;
		try {
			usersList = JSONUtils.readJsonFile(usersFile, new TypeToken<ArrayList<User>>() {
			});
		} catch (IOException e) {
			System.err.println("[S] Errore scrittura sul file di configurazione.");
			ServerMain.disconnect(clientSocket);
			return -1;
		}
		// Cerco nella userList un utente con lo stesso username e con la stessa
		// password
		player = usersList.stream()
				.filter((user -> user.getUsername().equals(username) && user.getHashedPassword() == hashedPsw))
				.findAny().orElse(null);

		if (player == null) {
			ServerMain.disconnect(clientSocket);
			return -1;
		}

		if (player.isActive()) {
			ServerMain.disconnect(clientSocket);
			return -2;
		}

		// L'utente adesso è attivo
		player.setActive(true);
		try {
			JSONUtils.updateUsersFile(usersFile, player);
		} catch (IOException e) {
			System.err.println("[S] Errore scrittura sul file di configurazione.");
			terminateSession();
			return -1;
		}

		return 1;
	}

	/**
	 * Metodo per avviare la procedura di logout
	 * 
	 * @param username username dell'utente che vuole eseguire il logout
	 * @return valore che indica se è l'operazione è andata a buon fine
	 */
	private int logout(String username) {
		if (!player.getUsername().equals(username))
			return -1;

		return 1;
	}

	/**
	 * Metodo per terminare la sessione di un utente, rendendolo inattivo
	 * @return valore che indica se è l'operazione è andata a buon fine
	 */
	private int terminateSession() {

		System.out.println("[S] [" + player.getUsername() + "] Eseguo il logout per l'utente.");

		player.setActive(false);
		ServerMain.disconnect(clientSocket);
		try {
			JSONUtils.updateUsersFile(usersFile, player);
		} catch (IOException e) {
			System.err.println("[S] [" + player.getUsername() + "] Errore scrittura sul file di configurazione.");
		}

		try {
			Utils.closeSocket(clientSocket);
		} catch (IOException e) {
			System.err.println("[S] [" + player.getUsername() + "] Errore chiusura socket.");
			return -1;
		}

		return 1;
	}

	/**
	 * Metodo per gestire la partita lato server, chiedendo di avviarla
	 * 
	 * @return valore che indica se è l'operazione è andata a buon fine
	 */
	private int playWORDLE() {

		Date lastGameDate = player.getLastGameDate();
		Date lastUpdateDate = serverConfig.getGame().getLastUpdate();

		int esito = 0;
		if (lastUpdateDate.before(lastGameDate) && !quit)
			return esito;

		if (quit) {
			// Caso quit
			quit = false;
			if (lastUpdateDate.after(lastGameDate)) {
				System.out.println("[S] [" + player.getUsername() + "] La parola è cambiata");
				numTentativi = 0;
				indiziTot.clear();
				esito = 2;
			} else {
				System.out.println("[S] [" + player.getUsername() + "] Partita ripresa");
				esito = 3;
			}

		} else {
			// Inizio una nuova partita
			indiziTot.clear();
			numTentativi = 0;
			shared = false;
			esito = 1;
		}

		// Aggiorno la data dell'ultima partita dell'utente
		player.setLastGameDate();
		try {
			JSONUtils.updateUsersFile(usersFile, player);
		} catch (IOException e) {
			System.err.println("[S] [" + player.getUsername() + "] Errore scrittura sul file di configurazione.");
			terminateSession();
			return -1;
		}

		return esito;
	}

	/**
	 * Metodo per leggere le parole inserite e richiedere gli indizi
	 * 
	 * @return stringa contenente gli indizi relativi al tentativo
	 */
	private String receiveWords() {
		// Leggo una parola
		String parola = "";
		try {
			parola = Utils.read(reader);
		} catch (IOException e) {
			System.err.println("[S] [" + player.getUsername() + "] Errore lettura.");
			terminateSession();
			return null;
		}

		// Richiedo gli indizi
		String indizi = getIndizi(parola);

		System.out.println("[S] [" + player.getUsername() + "] Parola inserita: " + parola);
		System.out.println("[S] [" + player.getUsername() + "] Indizi: " + indizi);

		return indizi;
	}

	/**
	 * Metodo per ottenere gli indizi relativi a una parola
	 * 
	 * @param parola della quale si vogliono ottenere gli indizi
	 * @return stringa contenente gli indizi relativi alla parola
	 */
	private String getIndizi(String parola) {
		String indizi = "";

		// Leggo la Secret Word
		String goal = serverConfig.getGame().getSecretWord();
		System.out.println("\n[S] [" + player.getUsername() + "] Secret Word: " + goal);

		// Ottengo gli indizi
		if (parola.trim().equalsIgnoreCase("QUIT"))
			indizi = "5";
		else if (parola.contains(" "))
			indizi = "4";
		else if (parola.length() != 10)
			indizi = "3";
		else if (!Arrays.asList(vocabulary).contains(parola))
			indizi = "2";
		else if (parola.equals(goal))
			indizi = "0";
		else {
			// Inizializzo gli indizi e una stringa contenente tutte le parole della Secret
			// Word trovate nella parola inserita
			char[] tmpIndizi = new char[] { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' };
			String foundLetters = "";
			char letter = 0;
			int i;

			// Cerco le lettere corrispondenti tra la parola inserita e la Secret Word
			for (i = 0; i < 10; i++) {
				letter = parola.charAt(i);
				if (parola.charAt(i) == goal.charAt(i)) {
					tmpIndizi[i] = '+';
					foundLetters += letter;
				}
			}
			for (i = 0; i < 10; i++) {
				letter = parola.charAt(i);
				if (goal.indexOf(letter) != -1) {
					// La parola inserita contiene una parola contenuta anche nella Secret Word:
					// se la lettera è già contenuta in foundLetters lascio la X, altrimenti la
					// segnalo
					// come presente nella Secret Word
					if (foundLetters.indexOf(letter) != -1)
						continue;
					tmpIndizi[i] = '?';
					foundLetters += letter;
				}

				indizi = new String(tmpIndizi);
			}

		}
		return indizi;
	}

	/**
	 * Metodo per formattare l'invio delle statistiche aggiornate dell'utente
	 * 
	 * @return statistiche dell'utente
	 */
	private String sendMeStatistics() {

		Statistics stat = player.getStats();
		System.out.println("[S] [" + player.getUsername() + "] Statistiche inviate");

		return player.getUsername() + ";" + stat.getPlayedGamesNum() + ";" + stat.getWinsPerc() + ";"
				+ stat.getLastStreak() + ";" + stat.getMaxStreak() + ";" + stat.getStringGuessDistribution();
	}

	/**
	 * Metodo per formattare l'invio del risultato della partita
	 * 
	 * @return stringa contenente il risultato della partita
	 */
	private String statsToShare() {
		if (numTentativi != 12)
			numTentativi++;

		String res = serverConfig.getGame().getWordNumber() + ";" + numTentativi + ":";

		for (String i : indiziTot)
			res += i + ";";

		return res;
	}

	/**
	 * Metodo per aggiornare le statistiche dell'utente autenticato
	 * 
	 * @param win          indica se l'ultima partita è stata vinta o meno
	 * @param numTentativi numero di tentativi effettuati nell'ultima partita
	 */
	private void updateStatistics(boolean win, int numTentativi) {
		
		// Leggo le statistiche dell'utente
		Statistics stats = player.getStats();

		// Numero di partite giocate
		int numPartiteGiocate = stats.getPlayedGamesNum() + 1;
		stats.setPlayedGamesNum(numPartiteGiocate);

		int numPartiteVinte = stats.getWins();

		if (win) {
			// Numero di partite vinte
			numPartiteVinte++;
			stats.setWins(numPartiteVinte);

			// Lunghezza dell'ultimo streak di vittorie
			int lastStreak = stats.getLastStreak() + 1;
			stats.setLastStreak(lastStreak);

			// Lunghezza del massimo streak
			int maxStreak = stats.getMaxStreak();
			if (lastStreak > maxStreak)
				stats.setMaxStreak(lastStreak);

			// Guess Distribution
			int[] guessDistrib = stats.getGuessDistribution();
			guessDistrib[numTentativi]++;
			stats.setGuessDistribution(guessDistrib);
		} else {
			stats.setLastStreak(0);
		}

		// Percentuale di partite vinte
		float percPartiteVinte = (numPartiteVinte / (float) numPartiteGiocate) * 100;
		stats.setWinsPerc(percPartiteVinte);

		// Setto le statistiche dell'utente e aggiorno il relativo file JSON
		player.setStats(stats);
		try {
			JSONUtils.updateUsersFile(usersFile, player);
		} catch (IOException e) {
			System.err.println("[S] [" + player.getUsername() + "] Errore scrittura sul file di configurazione.");
			terminateSession();
			return;
		}
	}

	/**
	 * Metodo per condividere i risultati della partita sul gruppo sociale
	 * 
	 * @return valore che indica se è l'operazione è andata a buon fine
	 */
	private int share() {

		if (indiziTot.isEmpty() || quit) {
			System.out.println("[S] [" + player.getUsername() + "] Non c'è niente da condividere...");
			return 0;
		}
		if (shared) {
			System.out.println("[S] [" + player.getUsername() + "] Risultato già condiviso...");
			return 2;
		}

		DatagramSocket multicastServer = null;
		InetAddress ia = null;
		String address = serverConfig.getMulticastAddress();

		// Preparo il multicastServer
		try {
			ia = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			System.err.println("[S] [" + player.getUsername() + "] Errore multicast.");
			return -1;
		}

		byte[] toSendData = null;
		DatagramPacket toSendPacket = null;
		try {
			multicastServer = new DatagramSocket();
		} catch (SocketException e) {
			System.err.println("[S] [" + player.getUsername() + "] " + e.getMessage());
			terminateSession();
			return -1;
		}

		// Condivido il risultato
		try {
			toSendData = statsToShare().getBytes("US-ASCII");
			toSendPacket = new DatagramPacket(toSendData, toSendData.length, ia, serverConfig.getMulticastPort());
			multicastServer.send(toSendPacket);
		} catch (IOException | RuntimeException e) {
			System.err.println("[S] [" + player.getUsername() + "] Errore invio multicast.");
			terminateSession();
		}

		shared = true;

		System.out.println("[S] [" + player.getUsername() + "] Sharing..");
		multicastServer.close();
		return 1;
	}

}

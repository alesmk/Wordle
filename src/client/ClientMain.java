package client;

import models.ClientConfig;
import models.Sharing;
import util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;
import com.google.gson.reflect.TypeToken;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p>
 * Progetto finale
 * </p>
 * 
 * La classe <b> ClientMain </b> gestisce l'interazione con l'utente e la
 * comunicazione con il server.
 * 
 * @author Alessia Anile
 */
public class ClientMain {

	/** Scanner utilizzato per l'inserimento dei comandi per le richieste dell'utente */
	private static Scanner sc = new Scanner(System.in);
	/** Nome del file di configurazione del client */
	private static String clientConfigFile = "clientConfig.json";
	/** Struttura dati per i messaggi del gruppo multicast */
	private static ArrayList<Sharing> sharings = new ArrayList<Sharing>();
	/** Numero massimo di tentativi permessi durante un gioco */
	private static final int N_MAX_TENTATIVI = 12;

	public static void main(String[] args) {

		// Variabile per memorizzare la configurazione
		ClientConfig clientConfig = null;

		// Leggo il file di configurazione
		try {
			clientConfig = JSONUtils.readJsonFile(clientConfigFile, new TypeToken<ClientConfig>() {
			});
		} catch (IOException e) {
			System.err.println("[C] Impossibile avviare ClientMain.");
			System.exit(1);
		}

		Socket socket = null;
		String multicastAddress = clientConfig.getMulticastAddress();
		String serverAddress = clientConfig.getServerAddress();
		int comando, result = 0;
		int port = clientConfig.getPort(), multicastPort = clientConfig.getMulticastPort();
		boolean comandoErr;

		System.out.println("\t-----WORDLE CLIENT-----");
		do {
			comando = 0;
			comandoErr = true;

			do {
				System.out.println("\n*****************************************");
				System.out.println("* Inserisci il numero del comando:\t*");
				System.out.println("* 1) Crea un nuovo account \t\t*");
				System.out.println("* 2) Accedi a un account esistente \t*");
				System.out.println("* 3) Esci \t\t\t\t*");
				System.out.println("*****************************************");
				System.out.print("> ");

				// Leggo il comando inserito dall'utente
				try {
					comando = Integer.parseInt(sc.nextLine()); 
				} catch (NumberFormatException e) {
					;
				}

				comandoErr = comando < 1 || comando > 3;
				if (comandoErr)
					System.out.println(
							Utils.ANSI_RED + "Inserimento non valido. Inserire di nuovo..." + Utils.ANSI_RESET);

			} while (comandoErr);

			if (comando == 1) {
				result = register(serverAddress, port);
				if (result == -1)
					System.out.println(
							Utils.ANSI_RED + "✖ Registrazione fallita: username già esistente." + Utils.ANSI_RESET);
				else if (result == -2)
					System.out.println(Utils.ANSI_RED + "✖ Registrazione fallita: password vuota." + Utils.ANSI_RESET);
				else 
					System.out.println(Utils.ANSI_GREEN + "☑ Registrazione andata a buon fine!" + Utils.ANSI_RESET);

			} else if (comando == 2) {
				if ((socket = login(serverAddress, port)) == null) {
					continue;
				} else {
					System.out.println(Utils.ANSI_GREEN + "☑ Login andato a buon fine!" + Utils.ANSI_RESET);

					// La fase di autenticazione è terminata e ho ottenuto il socket da passare alla fase successiva

					executeAuthenticated(socket, multicastPort, multicastAddress);
				}

			}
		
		// Finchè non viene inserito il comando per uscire.
		} while (comando != 3);
		
		System.out.println("\t ------Bye Bye------");
		sc.close();
	}

	/**
	 * Metodo per leggere le credenziali inserite dall'utente
	 * @param c comando scelto 
	 * @return stringa contenente la concatenazione di username, password e comando
	 */
	private static String insertCrendentials(int c) {
		System.out.print("Username > ");
		String username = sc.nextLine(); 
		System.out.print("Password > ");
		String password = sc.nextLine().trim(); 
		return username + ";" + password + ";" + c;
	}

	/**
	 * Metodo per richiedere di creare un nuovo account
	 * @param serverAddress indirizzo del server
	 * @param port porta del server
	 * @return valore che indica se è l'operazione è andata a buon fine
	 */
	private static int register(String serverAddress, int port) {
		Socket socket = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;

		try {
			socket = Utils.newSocket(serverAddress, port);
			reader = Utils.newReader(socket);
			writer = Utils.newWriter(socket);
		} catch (IOException e) {
			System.err.println("[C] Impossibile avviare la connessione.");
			System.exit(1);
		}

		System.out.println(Utils.ANSI_CYAN + "\n --- REGISTRAZIONE ---" + Utils.ANSI_RESET);

		String credenziali = "";
		credenziali = insertCrendentials(1);

		// Invio le credenziali al server
		try {
			Utils.write(writer, credenziali);
		} catch (IOException e) {
			System.err.println("[C] Errore scrittura.");
			System.exit(1);
		}

		// Leggo l'esito dal server
		int esito = -1;
		try {
			esito = Integer.parseInt(Utils.read(reader));
		} catch (IOException e) {
			System.err.println("[C] Errore lettura.");
			System.exit(1);
		}

		try {
			Utils.closeReader(reader);
			Utils.closeWriter(writer);
			Utils.closeSocket(socket);
		} catch (IOException e) {
			System.err.println("[C] Errore chiusura della connessione.");
		}

		return esito;
	}

	/**
	 * Metodo per richiedere di accedere a un account esistente
	 * @param serverAddress indirizzo del server
	 * @param port porta del server
	 * @return socket per avviare la connessione persistente
	 */
	private static Socket login(String serverAddress, int port) {
		Socket socket = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			socket = Utils.newSocket(serverAddress, port);
			reader = Utils.newReader(socket);
			writer = Utils.newWriter(socket);
		} catch (IOException e) {
			System.err.println("[C] Impossibile avviare la connessione.");
			System.exit(1);
		}

		System.out.println(Utils.ANSI_CYAN + "\n --- LOGIN ---" + Utils.ANSI_RESET);

		String credenziali = "";
		credenziali = insertCrendentials(2);

		// Invio le credenziali al server
		try {
			Utils.write(writer, credenziali);
		} catch (IOException e) {
			System.err.println("[C] Errore scrittura.");
			System.exit(1);
		}

		// Leggo l'esito dal server
		int esito = 0;
		try {
			esito = Integer.parseInt(Utils.read(reader));
		} catch (IOException e) {
			System.err.println("[C] Errore lettura.");
			System.exit(1);
		}

		if (esito == -1) {
			System.out.println(Utils.ANSI_RED + " ✖ Login fallito: credenziali errate." + Utils.ANSI_RESET);
			try {
				Utils.closeSocket(socket);
			} catch (IOException e) {
				System.err.println("[C] Errore chiusura socket.");
			}
			socket = null;
		} else if (esito == -2) {
			System.out.println(Utils.ANSI_RED + " ✖ Login fallito: sessione già esistente." + Utils.ANSI_RESET);
			try {
				Utils.closeSocket(socket);
			} catch (IOException e) {
				System.err.println("[C] Errore chiusura socket.");
			}
			socket = null;
		}

		// Inizializzo la struttura dati
		sharings.clear();
		
		return socket;
	}

	/**
	 * Metodo per avviare il gioco lato client
	 * @param esito risultato ricevuto dal server in merito all'operazione
	 * @param numTentativi numero di tentativi effettuati
	 * @param reader reader utilizzato per la lettura dal socket
	 * @param writer writer utilizzato per la scrittura sul socket
	 * @return numero di tentativi effettuati fino ad ora
	 */
	private static int playWordle(int esito, int numTentativi, BufferedReader reader, BufferedWriter writer) {
		System.out.printf("\n");
		if (esito == 0) {
			System.out.println("Hai già giocato !");
			return numTentativi;
		} else if (esito == 2) {
			System.out.println("La parola è cambiata! Nuovo gioco...");
			numTentativi = 0;
		} else if (esito == 1) {
			System.out.println("Bene, non hai ancora giocato !");
			numTentativi = 0;
		} else {
			System.out.println("Bentornato :)");
		}

		String parola, indizi = null;
		while (numTentativi < N_MAX_TENTATIVI) {
			
			// Richiedo una nuova parola all'utente
			System.out.println("\nTentativi rimanenti: " + (N_MAX_TENTATIVI - numTentativi));
			System.out.print("Inserisci una parola (quit) > ");
			
			parola = sc.nextLine();
			
			// Invio la parola al server
			try {
				Utils.write(writer, parola);
			} catch (IOException e) {
				System.err.println("[C] Errore scrittura.");
				System.exit(1);
			}

			// Leggo l'esito dal server
			try {
				esito = Integer.parseInt(Utils.read(reader));
			} catch (IOException e) {
				System.err.println("[C] Errore lettura.");
				System.exit(1);
			}

			if (esito == 5) {
				// Caso quit
				System.out.println("A più tardi!");
				break;
			} else if (esito == 0) {
				// Caso vittoria 
				printIndizi("0", parola);
				System.out.println("Parola indovinata!");
				break;
			}  
			else if (esito == 1) {
				// Caso parola non vincente 
				// Leggo gli indizi dal server e li mostro all'utente
				try {
					indizi = Utils.read(reader);
				} catch (IOException e) {
					System.err.println("[C] Errore lettura.");
					System.exit(1);
				}
				if (indizi == null)
					continue;
				printIndizi(indizi, parola);
				numTentativi++; 
			} else if (esito == 2) {
				System.out.println("La parola non è presente nel vocabolario");
			} else if (esito == 3) {
				System.out.println("La lunghezza della parola è errata (10 caratteri)");
			} else if (esito == 4) {
				System.out.println("La parola contiene spazi");
			}

		}

		if (numTentativi == 12)
			System.out.println("Mi dispiace, hai perso :(");

		return numTentativi;
	}

	/**
	 * Metodo per mostrare le statistiche dell'utente
	 * @param userStats stringa rappresentante le statistiche dell'utente
	 */
	private static void printStats(String userStats) {
		String[] stat = userStats.split(";");

		System.out.println("\n***************STATISTICHE***************\n");

		if (userStats.isEmpty()) {
			System.out.println("Non ci sono statistiche da mostrare");
			return; 
		}

		System.out.println("* Username: " + stat[0]);
		System.out.println("* Numero di partite giocate: " + Integer.parseInt(stat[1]));
		System.out.printf("* Percentuale di partite vinte: %.2f%%\n", Float.parseFloat(stat[2]));
		System.out.println("* Lunghezza dell'ultimo streak: " + Integer.parseInt(stat[3]));
		System.out.println("* Lunghezza del massimo streak: " + Integer.parseInt(stat[4]));
		System.out.println("* Guess Distribution: ");
		for (int i = 5; i < 5 + N_MAX_TENTATIVI; i++) {
			System.out.printf("\t%s*%d: ", (i - 4) < 10 ? " " : "", i - 4);

			for (int j = 0; j < Integer.parseInt(stat[i]); j++)
				System.out.printf(Utils.ANSI_GREEN + "■" + Utils.ANSI_RESET);

			if (Integer.parseInt(stat[i]) != 0)
				System.out.printf(Utils.ANSI_YELLOW + " " + Integer.parseInt(stat[i]) + Utils.ANSI_RESET);

			System.out.printf("\n");
		}

		System.out.println("\n*****************************************");
	}

	/**
	 * Metodo per mostrare gli indizi
	 * @param indizi indizi ricevuti dal server
	 * @param parola parola inserita dall'utente come tentativo
	 */
	private static void printIndizi(String indizi, String parola) {
		// Se indizi equivale a 0, la parola è stata indovinata
		if (indizi.equals("0"))
			indizi = "++++++++++";

		System.out.print("\t\t");
		for (int l = 0; l < 10; l++) {
			if (indizi.charAt(l) == '+')
				System.out.print(Utils.ANSI_GREEN + parola.charAt(l) + Utils.ANSI_RESET);
			else if (indizi.charAt(l) == '?')
				System.out.print(Utils.ANSI_YELLOW + parola.charAt(l) + Utils.ANSI_RESET);
			else
				System.out.print(parola.charAt(l));
		}
		System.out.print("\n\n");
	}

	/**
	 * Metodo per mostrare i risultati condivisi dagli utenti del gruppo multicast a cui l'utente appartiene
	 */
	private static void showMeSharing() {
		// Se sharings è vuoto, non sono stati ricevuti risultati
		if (sharings.isEmpty()) {
			System.out.println("Non ci sono notifiche da mostrare");
			return;
		}

		for (Sharing s : sharings) {
			System.out.println("*****************************************");
			System.out.printf("\t\tWORDLE %d: %d/%d\n", s.getWordNumber(), s.getNumTentativi(), N_MAX_TENTATIVI);

			for (String indizi : s.getIndiziTot())
				printIndizi(indizi, "▪▪▪▪▪▪▪▪▪▪");
				
			System.out.println("*****************************************");
		}
	}

	/**
	 * Metodo per inizializzare il MulticastReader per la lettura multicast
	 * @param multicastSocket socket per la lettura multicast
	 * @return thread che gestisce la ricezione con il multicastReader
	 */
	private static Thread initMulticastReader(MulticastSocket multicastSocket) {
		Thread multicastReader = null;
		try {
			MulticastReader _multicastReader = new MulticastReader(multicastSocket, sharings);
			multicastReader = new Thread(_multicastReader);
			multicastReader.start();
		} catch (IOException e) {
			System.err.println("[C] Impossibile avviare MulticastReader");
			System.exit(1);
		}
		return multicastReader;
	}

	/**
	 * Metodo per unirsi al gruppo multicast
	 * @param multicastPort numero di porta multicast
	 * @param address indirizzo del gruppo multicast
	 * @return multicastSocket per la lettura multicast
	 */
	private static MulticastSocket joinMulticast(int multicastPort, String address) {
		MulticastSocket multicastSocket = null;
		InetAddress ia = null;

		// Unirsi al gruppo multicast
		try {
			multicastSocket = new MulticastSocket(multicastPort); 
			ia = InetAddress.getByName(address);
			multicastSocket.joinGroup(ia);
		} catch (IOException ex) {
			System.out.println("[C] Errore multicast");
		}

		return multicastSocket;
	}

	/**
	 * Metodo per abbandonare il gruppo multicast
	 * @param address indirizzo del gruppo multicast
	 * @param multicastSocket socket per la lettura multicast
	 * @param multicastReader thread che gestisce la ricezione con il multicastReader
	 */
	private static void leaveMulticast(String address, MulticastSocket multicastSocket, Thread multicastReader) {
		InetAddress ia = null;

		// Abbandono il gruppo multicast
		try {
			ia = InetAddress.getByName(address);
			multicastSocket.leaveGroup(ia);
		} catch (IOException e) {
			System.err.println("[C] Errore abbandono gruppo multicast.");
		}

		// Termino il thread 
		multicastReader.interrupt();
		multicastSocket.close();
		try {
			multicastReader.join();
		} catch (InterruptedException e) {
			System.out.println("[C] Eccezione MulticastReader join");
		}
	}

	/**
	 * Metodo per l'esecuzione autenticata del client
	 * @param socket socket della connessione
	 * @param multicastPort numero di porta multicast
	 * @param address indirizzo del gruppo multicast
	 */
	private static void executeAuthenticated(Socket socket, int multicastPort, String address) {
		BufferedReader reader = null;
		BufferedWriter writer = null;

		try {
			reader = Utils.newReader(socket);
			writer = Utils.newWriter(socket);
		} catch (IOException e) {
			System.err.println("[C] Impossibile avviare la connessione.");
			System.exit(1);
		}

		// Mi unisco al gruppo multicast e avvio il thread che gestisce la ricezione con il multicastReader
		MulticastSocket multicastSocket = joinMulticast(multicastPort, address);
		Thread multicastReader = initMulticastReader(multicastSocket);

		int comando, numTentativi = 0, esito = 0;
		boolean comandoErr;
		String mess = "";

		do {
			comando = 0;
			comandoErr = true;
			do {
				System.out.println("\n*****************************************");
				System.out.println("* Inserisci il numero del comando: \t*");
				System.out.println("* 1) Gioca \t\t\t\t*");
				System.out.println("* 2) Visualizza le tue statistiche \t*");
				System.out.println("* 3) Condividi le tue statistiche \t*");
				System.out.println("* 4) Mostra i risultati dei miei amici \t*");
				System.out.println("* 5) Logout \t\t\t\t*");
				System.out.println("*****************************************");
				System.out.print("> ");

				try {
					comando = Integer.parseInt(sc.nextLine());
				} catch (NumberFormatException e) {
					;
				}

				comandoErr = comando < 1 || comando > 5;
				if (comandoErr)
					System.out.println(
							Utils.ANSI_RED + "Inserimento non valido. Inserire di nuovo..." + Utils.ANSI_RESET);

			} while (comandoErr);

			// Invio al server il comando
			try {
				Utils.write(writer, comando);
			} catch (IOException e) {
				System.err.println("[C] Errore scrittura.");
				System.exit(1);
			}

			if (comando == 4) {
				showMeSharing();
				continue;
			}

			// Leggo l'esito dal server
			try {
				mess = Utils.read(reader);
			} catch (IOException e) {
				System.err.println("[C] Errore lettura.");
				System.exit(1);
			}

			if (comando == 2) {
				printStats(mess);
				continue;
			}
			
			esito = Integer.parseInt(mess);
			if (comando == 1) {
				numTentativi = playWordle(esito, numTentativi, reader, writer);
			} else if (comando == 3) {
				if (esito == 0)
					System.out.println("Non c'è niente da condividere");
				else if (esito == 2)
					System.out.println("Risultato già condiviso...");
				else
					System.out.println("Share in corso...");
			}

			// Se il server risponde con il codice -1, c'è stato un errore: termino.
			if (esito == -1) {
				System.err.println(" ✖ C'è stato un ERRORE! Bye bye!");
				return;
			}

		} while (comando != 5);
		System.out.println(Utils.ANSI_GREEN + "☑ Logout andato a buon fine!" + Utils.ANSI_RESET);

		// Chiudo il socket, azzero la struttura dati sharings e abbandono il gruppo multicast
		try {
			Utils.closeSocket(socket);
		} catch (IOException e) {
			System.err.println("[C] Errore chiusura socket.");
		}
		sharings.clear();
		leaveMulticast(address, multicastSocket, multicastReader);
	}
}

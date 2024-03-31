package util;

import models.ServerConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p> Progetto finale </p>
 * 
 * La classe <b> Utils </b> contiene metodi di utilità che possono essere utilizzati da server e client.
 * 
 * @author Alessia Anile
 */
public class Utils {
	
	// Costanti per la definizione dei colori della console
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	/** Costante per il formato della data utilizzato */
	protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ssz";

	
	/**
	 * Metodo per la lettura di un file riga per riga
	 * @param filename nome del file da leggere
	 * @return un array di stringhe contenente tutte le righe del file
	 * @throws IOException in caso di errori I/O durante l'utilizzo del reader
	 * @throws FileNotFoundException se il file non esiste
	 */
	public static String[] readFile(String filename) throws IOException, FileNotFoundException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
		
		// Lettura di tutte le righe del file
		String[] lines = reader.lines().<String>toArray(String[]::new);
		
		closeReader(reader);
		return lines;
	}

	/**
	 * Metodo per la creazione di un nuovo socket 
	 * @param host host name, o null per l'indirizzo di loopback
	 * @param port numero di porta
	 * @return il socket creato
	 * @throws IOException in caso di errori I/O durante la creazione del socket
	 */
	public static Socket newSocket(String host, int port) throws IOException {
		return new Socket(host, port);
	}

	/**
	 * Metodo per la creazione di un nuovo BufferedReader 
	 * @param socket socket da utilizzare per la lettura
	 * @return il reader creato
	 * @throws IOException in caso di errori I/O durante la creazione del reader
	 */
	public static BufferedReader newReader(Socket socket) throws IOException {
		return new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	/**
	 * Metodo per la creazione di un nuovo BufferedWriter 
	 * @param socket socket da utilizzare per la scrittura
	 * @return il writer creato
	 * @throws IOException in caso di errori I/O durante la creazione del writer
	 */
	public static BufferedWriter newWriter(Socket socket) throws IOException {
		return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	/**
	 * Metodo per la lettura da un BufferedReader
	 * @param reader reader da utilizzare per la lettura
	 * @return la stringa letta
	 * @throws IOException in caso di errori I/O durante la lettura
	 * @throws SocketException in caso di errori I/O causati dal socket utilizzato
	 */
	public static String read(BufferedReader reader) throws IOException, SocketException {
		String message = null;
		int i = 0;

		do {
			try {
				// Lettura
				message = reader.readLine();
				System.out.flush();
			} catch (SocketException e) {
				// Propago l'eccezione
				throw e;
			}catch (IOException e) {
				// Dopo cinque tentativi, propago l'eccezione 
				if(i==5) throw e;
			}
			i++;
		} while (message == null);

		return message;
	}

	/**
	 * Metodo per la scrittura su un BufferedWriter
	 * @param <T> tipo generico di messaggio da inviare
	 * @param writer writer utilizzato per la scrittura
	 * @param message contenuto da scrivere, del tipo T
	 * @throws IOException in caso di errori I/O durante la scrittura
	 * @throws SocketException in caso di errori I/O causati dal socket utilizzato
	 */
	public static <T> void write(BufferedWriter writer, T message) throws IOException, SocketException {
		int i=0;
		
		while(i<5) {
			try {
				// Scrivo il contenuto
				writer.write(message + "\r\n");
				writer.flush();
				i=5;
			} catch (SocketException e) {
				// Propago l'eccezione 
				throw e;
			} catch(IOException e) {
				// Dopo cinque tentativi, propago l'eccezione 
				if(i==5) throw e;
			}
			i++;
		}
	}

	/**
	 * Metodo generico per la chiusura di un reader
	 * @param <T> tipo generico del reader
	 * @param reader reader da chiudere
	 * @throws IOException in caso di errori I/O durante la chiusura
	 */
	public static <T extends Reader> void closeReader(T reader) throws IOException {
		reader.close();
	}

	/**
	 * Metodo generico per la chiusura di un writer
	 * @param <T> tipo generico del writer
	 * @param writer writer da chiudere
	 * @throws IOException in caso di errori I/O durante la chiusura
	 */
	public static <T extends Writer> void closeWriter(Writer writer) throws IOException {
		writer.close();
	}

	/**
	 * Metodo per la chiusura di un socket
	 * @param socket socket da chiudere
	 * @throws IOException in caso di errori I/O durante la chiusura
	 */
	public static void closeSocket(Socket socket) throws IOException {
		if(socket.isClosed()) return;
		socket.close();
	}

	/**
	 * Metodo per ottenere data e ora correnti
	 * @return oggetto di tipo Date
	 */
	public static Date getNowDate() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * Metodo per effettuare il parsing di una stringa e ottenere un oggetto di tipo Date, 
	 * utilizzando il formato standard definito dalla classe
	 * @param stringDate data da parsare in formato String
	 * @return oggetto di tipo Date contenente il risultato del parsing. 
	 * In caso di input non valido o vuoto, viene restituita la data 01-01-1970 01:00:00
	 */
	public static Date getDateFromString(String stringDate) {
		Date date = null;

		try {
			date = new SimpleDateFormat(DATE_FORMAT).parse(stringDate);
		} catch (ParseException e) { 
			// Se stringDate è una stringa vuota o non valida, viene inizializzata al 01-01-1970 01:00:00
			date = new Date(0L);
		} 

		return date;
	}

	/**
	 * Metodo per ottenere il numero di millisecondi a partire da un oggetto di tipo Date
	 * @param date data di cui si vuole ottenere il numero di millisecondi
	 * @return il numero di millisecondi
	 */
	public static long getMillis(Date date) {
		long millis;
		
		// Se la data non è valida, il risultato è 0
		if (date == null) {
			millis = 0;
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			millis = calendar.getTimeInMillis();
		}

		return millis;
	}

	/**
	 * Metodo per ottenere la frequenza di aggiornamento della Secret Word in millisecondi
	 * @param config configurazione contenente la frequenza di aggiornamento e la sua unità di misura
	 * @return frequenza di aggiornamento in millisecondi
	 */
	public static long getUpdateFrequencyMillis(ServerConfig config) {
		long updateFrequency = config.getUpdateFrequency();
		String updateFrequencyUnit = config.getUpdateFrequencyUnit();
		
		// Converto la frequenza di aggiornamento (updateFrequency) letta dal file di
		// configurazione
		// in secondi, in base all'unità di tempo settata anch'essa nel file di
		// configurazione.
		// Se non è stata settata, si considera secondi come unità di default.
		if (updateFrequencyUnit.equals("m"))
			updateFrequency *= 60;
		else if (updateFrequencyUnit.equals("h"))
			updateFrequency *= 3600;
		else if (updateFrequencyUnit.equals("g"))
			updateFrequency *= 86400;

		// Converto in millisecondi
		updateFrequency *= 1000;
		
		return updateFrequency;
	}
	

}

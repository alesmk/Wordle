package models;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * <p>
 * Progetto finale
 * </p>
 * 
 * La classe <b> ServerConfig </b> rappresenta la configurazione del server.
 * 
 * @author Alessia Anile
 */
public class ServerConfig {
	
	/** Numero di porta del server */
	private int port;
	/** Numero di porta multicast */
	private int multicastPort;
	/** Indirizzo del gruppo multicast */
	private String multicastAddress;
	/** Informazioni relative alla partita in corso */
	private Game game;
	/** Frequenza di aggiornamento della Secret Word */
	private int updateFrequency;
	/** Unità di tempo relativa alla frequenza di aggiornamento della Secret Word [g/h/m/[s]] */
	private String updateFrequencyUnit;
	/** Nome del file contenente lo storico delle Secret Words */
	private String historyFile;
	/** Nome del file contenente il vocabolario da utilizzare */
	private String vocabularyFile;
	/** Nome del file contenente le informazioni relative agli utenti */
	private String usersFile;
	
	
	/**
	 * @param port numero di porta del server
	 * @param multicastPort numero di porta multicast
	 * @param multicastAddress indirizzo del gruppo multicast
	 * @param game informazioni relative a una partita
	 * @param updateFrequency frequenza di aggiornamento della Secret Word
	 * @param updateFrequencyUnit unità di tempo relativa alla frequenza di aggiornamento della Secret Word
	 * @param historyFile nome del file contenente il vocabolario da utilizzare
	 * @param vocabularyFile nome del file contenente il vocabolario da utilizzare
	 * @param usersFile nome del file contenente le informazioni relative agli utenti
	 */
	public ServerConfig(int port, int multicastPort, String multicastAddress, Game game, int updateFrequency, String updateFrequencyUnit,
			String historyFile, String vocabularyFile, String usersFile) {
		this.port = port;
		this.multicastPort = multicastPort;
		this.multicastAddress = multicastAddress;
		this.game = game;
		this.updateFrequency = updateFrequency;
		this.updateFrequencyUnit = updateFrequencyUnit;
		this.historyFile = historyFile;
		this.vocabularyFile = vocabularyFile;
		this.usersFile = usersFile;
	}

	/**
	 * @return numero di porta del server
	 */
	public int getPort() {
		return port;
	}


	/**
	 * @param port numero di porta del server da settare
	 */
	public void setPort(int port) {
		this.port = port;
	}


	/**
	 * @return numero di porta multicast
	 */
	public int getMulticastPort() {
		return multicastPort;
	}


	/**
	 * @param multicastPort numero di porta multicast da settare
	 */
	public void setMulticastPort(int multicastPort) {
		this.multicastPort = multicastPort;
	}

	/**
	 * @return indirizzo del gruppo multicast
	 */
	public String getMulticastAddress() {
		return multicastAddress;
	}


	/**
	 * @param multicastAddress indirizzo del gruppo multicast da settare
	 */
	public void setMulticastAddress(String multicastAddress) {
		this.multicastAddress = multicastAddress;
	}
	
	/**
	 * @return informazioni relative alla partita corrente
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * @param game informazioni relative alla partita corrente da settare
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * @return frequenza di aggiornamento della Secret Word
	 */
	public int getUpdateFrequency() {
		return updateFrequency;
	}

	/**
	 * @param updateFrequency frequenza di aggiornamento della Secret Word da settare
	 */
	public void setUpdateFrequency(int updateFrequency) {
		this.updateFrequency = updateFrequency;
	}

	/**
	 * @return unità di tempo relativa alla frequenza di aggiornamento della Secret Word
	 */
	public String getUpdateFrequencyUnit() {
		return updateFrequencyUnit;
	}

	/**
	 * @param updateFrequencyUnit unità di tempo relativa alla frequenza di aggiornamento della Secret Word da settare
	 */
	public void setUpdateFrequencyUnit(String updateFrequencyUnit) {
		this.updateFrequencyUnit = updateFrequencyUnit;
	}	
	
	/**
	 * @return nome del file contenente lo storico delle Secret Words
	 */
	public String getHistoryFile() {
		return historyFile;
	}

	/**
	 * @param historyFile nome del file contenente lo storico delle Secret Words da settare
	 */
	public void setHistoryFile(String historyFile) {
		this.historyFile = historyFile;
	}

	/**
	 * @return nome del file contenente il vocabolario da utilizzare 
	 */
	public String getVocabularyFile() {
		return vocabularyFile;
	}

	/**
	 * @param vocabularyFile nome del file contenente il vocabolario da utilizzare da settare
	 */
	public void setVocabularyFile(String vocabularyFile) {
		this.vocabularyFile = vocabularyFile;
	}

	/**
	 * @return nome del file contenente le informazioni relative agli utenti 
	 */
	public String getUsersFile() {
		return usersFile;
	}

	/**
	 * @param usersFile nome del file contenente le informazioni relative agli utenti 
	 */
	public void setUsersFile(String usersFile) {
		this.usersFile = usersFile;
	}

}

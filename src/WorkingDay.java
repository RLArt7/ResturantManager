import java.util.logging.Logger;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;


public class WorkingDay {
	private static Logger theLogger;
	private static FileHandler theFileHandler;
	
	
	public WorkingDay(String loggerName) throws SecurityException, IOException{
		theLogger = Logger.getLogger(loggerName);
		theFileHandler = new FileHandler(loggerName + ".xml",true);
	}
}

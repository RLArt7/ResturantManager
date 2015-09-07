package RestBL;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;


public class WorkingDay{
	private static Logger theLogger;
	private FileHandler theFileHandler;
	
	
	public WorkingDay(String loggerName ,Object object){
		try {
		theLogger = Logger.getLogger(loggerName.trim());
		theFileHandler = new FileHandler(loggerName + ".txt");
//			theHandler = new FileHandler("ConfigLog.txt");
			theLogger.addHandler(theFileHandler);
			theFileHandler.setFilter(new ObjectFilter(object));
			theFileHandler.setFormatter(new TheFormatter());
//			theLogger.removeHandler(theFileHandler);
		
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		theLogger.setUseParentHandlers(false);
	}
	public void setLoggerLevel(Level level){
		theLogger.setLevel(level);
	}
	public Logger logger(){
		return theLogger;
	}
	public FileHandler theFileHandler(){
		return theFileHandler;
	}
}

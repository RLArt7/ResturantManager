package RestBL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

//import jdk.nashorn.internal.runtime.linker.JavaAdapterFactory;

public class TheFormatter extends Formatter {

	@Override
	public String format(LogRecord rec) {
		StringBuffer buf = new StringBuffer(1000);
		buf.append(new java.util.Date().toLocaleString());
		buf.append("[" + rec.getLevel()+"]");
		buf.append(" :");
		buf.append(formatMessage(rec));
		buf.append("\n");
		
		return buf.toString();
	}
}

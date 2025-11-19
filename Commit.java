import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Commit{
    String commitID;
    String message;
    String timestamp;
    HashMap<String,String> snapshot;
    Commit parent;
    public Commit(String commitID, String message,HashMap<String,String> snapshot, Commit parent){
        this.commitID=commitID;
        this.message=message;
        this.snapshot=snapshot;
        this.parent=parent;
    
    DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    this.timestamp=LocalDateTime.now().format(formatter);
    }
}

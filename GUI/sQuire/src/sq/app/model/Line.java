package sq.app.model;
import java.sql.Timestamp;
import java.util.ArrayList;

// @author Joe
public class Line {
	
	private static Timestamp latestGreatestEdit = null;
	private int LineID = 0;
	private int NextLineID = 0;
	private String LineText = "";
	private Timestamp Timestamp = null;
	private Boolean Locked = false;
	private int LineNumber = -1;
	private int LastEditorID = -1;
	
	public Line(){
		this.setID(-1);
		this.setNextID(-1);
		this.setLastEditorID(-1);
		this.setText("");
		this.setTimestamp(Timestamp);
		this.setLocked(false);
	}
	
	public Line(int lineId, int lineNumber, int lastEditorID, int nextLineID, String lineText, Timestamp timestamp){
		this.setID(lineId);
		this.setLastEditorID(lastEditorID);
		this.setLineNumber(lineNumber);
		this.setNextID(nextLineID);
		this.setText(lineText);
		this.setTimestamp(timestamp);
		this.setLocked(false);
	}
	
	public int getID(){
		return this.LineID;
	}
	
	public void setID(int ID) {
        this.LineID = ID;
    }

	public int getLineNumber(){
		return this.LineNumber;
	}
	
	public void setLineNumber(int lineNumber) {
        this.LineNumber = lineNumber;
    }

	public int getLastEditorID(){
		return this.LastEditorID;
	}
	
	public void setLastEditorID(int lastEditorID) {
        this.LastEditorID = lastEditorID;
    }

	public int getNextID(){
		return this.NextLineID;
	}
	
	public void setNextID(int ID) {
        this.NextLineID = ID;
    }

	public String getText(){
		return this.LineText;
	}
	
	public void setText(String text) {
        this.LineText = text;
    }

	public Boolean getLocked(){
		return this.Locked;
	}
	
	public void setLocked(Boolean locked) {
        this.Locked = locked;
    }

	public Timestamp getTimestamp(){
		return this.Timestamp;
	}
	
	public void setTimestamp(Timestamp timestamp) {
        this.Timestamp = timestamp;
        if (this.Timestamp != null && latestGreatestEdit == null || this.Timestamp.after(latestGreatestEdit)){
        	latestGreatestEdit = this.Timestamp;
        }
    }
	
	public static Timestamp GetLatestGreatestEditTime(){
		return latestGreatestEdit;
	}
}

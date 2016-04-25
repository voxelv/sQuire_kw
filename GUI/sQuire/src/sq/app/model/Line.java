package sq.app.model;
import java.sql.Timestamp;

public class Line {
	
	private static Timestamp latestGreatestEdit = null;
	private int LineID = 0;
	private int NextLineID = 0;
	private String LineText = "";
	private Timestamp Timestamp = null;
	
	public Line(){
		this.setID(0);
		this.setNextID(0);
		this.setText("");
		this.setTimestamp(Timestamp);
	}
	
	public Line(int lineId, int nextLineID, String lineText, Timestamp timestamp){
		this.setID(lineId);
		this.setNextID(nextLineID);
		this.setText(lineText);
		this.setTimestamp(timestamp);
	}
	
	public int getID(){
		return this.LineID;
	}
	
	public void setID(int ID) {
        this.LineID = ID;
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

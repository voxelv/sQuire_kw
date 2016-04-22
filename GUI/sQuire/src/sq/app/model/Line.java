package sq.app.model;

public class Line {
	
	private int LineID;
	private int NextLineID;
	private String LineText;
	
	public Line(){
		this.LineID = 0;
	}
	
	public Line(int lineId, int nextLineID, String lineText){
		this.LineID = lineId;
		this.NextLineID = nextLineID;
		this.LineText = lineText;
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
	
	public void setLineID(String text) {
        this.LineText = text;
    }

}

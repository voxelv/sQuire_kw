package sq.app.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import sq.app.model.Line;

//@Author Joe
public class LineDictionary {

	private ArrayList<Integer> lockedLines = new ArrayList<Integer>();
	private ArrayList<Integer> lockedIDs = new ArrayList<Integer>();
	private ArrayList<Line> lineList = new ArrayList<Line>();
	private HashMap<Integer,Line> idMap = new HashMap<Integer,Line>();
	private int currentUserID = -1;
	private ArrayList<Line> changeList = new ArrayList<Line>();
	
	private static final long serialVersionUID = 1L;
	
	public void add(Line value) {
		if (idMap.containsValue(value)){
			this.removeID(value.getID());
		}
		

		if (value.getLocked())
		{
			lockIt(value);
		}
		idMap.put(value.getID(), value);
		lineList.add(value.getLineNumber(), value);
    }
	
	public void setUserID(int id){
		currentUserID = id;
	}
	
	public void addAll(List<Line> lines){
		for (Line line : lines){
			this.add(line);
		}
	}
	
	public void resetTo(List<Line> lines){
		this.clear();
		this.addAll(lines);
	}
	
	public Line getLine(Integer line){
		return lineList.get(line);
	}
	
	public Line getID(Integer id){
		return idMap.get(id);
	}
	
	public int getIDfromLine(Integer line){
		return lineList.get(line).getID();
	}
	
	public int getLinefromID(Integer id){
		return idMap.get(id).getLineNumber();
	}
	
	public void removeID(Integer key){
		Line l = idMap.get(key);
		if (l != null){
			idMap.remove(l);
			lineList.remove(l);
			unlockIt(l);
		}
	}
	
	public void removeLine(Integer key){
		Line l = lineList.get(key);
		if (l != null){
			idMap.remove(l);
			lineList.remove(l);
			unlockIt(l);
		}
	}
	
	public void lockLine(Integer key){
		Line l = lineList.get(key);
		if (l != null){
			l.setLocked(true);
			idMap.put(l.getID(),l);
			lineList.add(key, l);
			lockIt(l);
		}
	}
	
	public void lockLinebyID(Integer key){
		Line l = idMap.get(key);
		if (l != null){
			l.setLocked(true);
			idMap.put(key, l);
			lineList.set(l.getLineNumber(),l);
			lockIt(l);
		}
	}
	
	public void unLockLine(Integer key){
		Line l = lineList.get(key);
		if (l != null){
			l.setLocked(false);
			idMap.put(l.getID(),l);
			lineList.set(key, l);
			unlockIt(l);
		}
	}
	
	public void unLockID(Integer key){
		Line l = idMap.get(key);
		if (l != null){
			l.setLocked(false);
			idMap.put(key, l);
			lineList.set(l.getLineNumber(),l);
			unlockIt(l);
		}
	}
	
	public void updateLineNumber(Integer oldLine, Integer newLine){
		Line l = lineList.get(oldLine);
		if (l != null){
			lineList.remove(l);
			l.setLineNumber(newLine);
			idMap.put(l.getID(),l);
			lockIt(l);
		}
	}
	
	public void updateLineNumberbyID(Integer id, Integer newLine){
		Line l = idMap.get(id);
		if (l != null){
			lineList.remove(l);
			l.setLineNumber(newLine);
			idMap.put(id, l);
			lockIt(l);
		}
	}
	
	public void updateText(Integer line, String text){
		Line l = lineList.get(line);
		if (l != null){
			l.setText(text);
			idMap.put(l.getID(),l);
			lineList.set(line, l);
		}
	}
	public void updateText(Integer line, String text, Timestamp tstamp, int lastEditorID){
		Line l = lineList.get(line);
		if (l != null){
			l.setText(text);
			l.setTimestamp(tstamp);
			l.setLastEditorID(lastEditorID);
			idMap.put(l.getID(),l);
			lineList.set(line, l);
		}
	}
	
	public void updateTextbyID(Integer id, String text, Timestamp tstamp, int lastEditorID){
		Line l = idMap.get(id);
		if (l != null){
			l.setText(text);
			l.setTimestamp(tstamp);
			l.setLastEditorID(lastEditorID);
			idMap.put(id, l);
			lineList.set(l.getLineNumber(),l);
		}
	}

	public void updateTextbyID(Integer id, String text){
		Line l = idMap.get(id);
		if (l != null){
			l.setText(text);
			idMap.put(id, l);
			lineList.set(l.getLineNumber(),l);
		}
	}
	
	public int getSize(){
		return lineList.size();
	}
	
	public void clear(){
		lineList.clear();
		idMap.clear();
		lockedLines.clear();
		lockedIDs.clear();
	}
	
	private void lockIt(Line l){
		if (l.getLastEditorID() != currentUserID){
			if (!lockedIDs.contains(l.getID())){
				lockedIDs.add(l.getID());
			}		
			if (!lockedLines.contains(l.getLineNumber())){
				lockedLines.add(l.getLineNumber());
			}		
		}		
	}
	private void unlockIt(Line l){
		try{
			if (lockedIDs.contains(l.getID())){
				lockedIDs.remove(lockedIDs.indexOf(l.getID()));
			}		
			if (lockedLines.contains(l.getLineNumber())){
				lockedLines.remove(lockedLines.indexOf(l.getLineNumber()));
			}		
		}
		catch(Exception e){
			e = e;
		}
	}
	
	public void MarkChanged(int lineNumber){
		synchronized (changeList) {
			changeList.add(getLine(lineNumber));
		}
	}
	
	public ArrayList<Line> GetChangeList(){
		synchronized (changeList) {
			ArrayList<Line> copy = (ArrayList<Line>)changeList.clone();
			changeList.clear();
			return copy;
		}
	}
	
	public Boolean HasChanges(){
		synchronized (changeList) {
			return !changeList.isEmpty();
		}
	}
	
	public ArrayList<Integer> getLockedLines(){
		return (ArrayList<Integer>)lockedLines.clone();
	}

	public ArrayList<Integer> getLockedIDs(){
		return (ArrayList<Integer>)lockedIDs.clone();
	}
}
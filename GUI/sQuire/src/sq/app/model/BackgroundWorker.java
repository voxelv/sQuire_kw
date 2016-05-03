package sq.app.model;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import sq.app.model.editor.EditorCodeArea;

public class BackgroundWorker extends Thread{
	private EditorCodeArea editor;
	private ServerConnection server;
	
	public BackgroundWorker(EditorCodeArea editor, ServerConnection server)
	{
		this.editor = editor;
		this.server = server;
	}
	
	@Override
    public void run()
    {
		while (sq.app.MainApp.GetServer().getStatus())
		{
            try {
        		if (editor.GetFileID() >= 0){
            		JSONObject jo = new JSONObject();
                	jo.put("fileID", String.valueOf(editor.GetFileID()));
                	Object data = null;
                	try{
                		// this appears to kill my connection?
                		data = server.sendSingleRequest("project", "getLineLocks", jo);
            		}
                	catch(Exception e){
                		//do nothing
                	}
            		Object out = null;
            		if (data != null){
            			JSONArray ja = (JSONArray)new org.json.simple.parser.JSONParser().parse((String)data);
            			ArrayList<Integer> locked = new ArrayList<Integer>();
            			for(Object o : ja.toArray()){
            				JSONObject joo = (JSONObject)o;
            				locked.add(Integer.parseInt((String)joo.get("pflid")));
            			}
            			editor.SetLockedParagraphs(locked);
            		}
                	
            		jo = new JSONObject();
                	jo.put("fileID", String.valueOf(editor.GetFileID()));
                	jo.put("time", String.valueOf(editor.GetLatestEditTime().getTime()/1000+1));
                	try{
                		data = server.sendSingleRequest("project", "getLineChanges", jo);
                	}
                	catch(Exception e){
                		e.printStackTrace(System.out);
                		System.out.println("Exception: " + e.getMessage());
                	}
            		out = null;
            		if (data != null){
            			try{
	            			JSONArray ja = (JSONArray)new org.json.simple.parser.JSONParser().parse((String)data);
	            			if (ja != null && ja.size()>0)
	            			{
	            				for (Object o : ja.toArray())
	            				{
	            					jo = (JSONObject)o;
	            					String newText = (String)jo.get("text");
	            					int lineID = Integer.parseInt((String)jo.get("pflid"));
	            					int lastEditor = Integer.parseInt((String)jo.get("lastEditor"));
	            					int nextLineID = -1;
	            					if ((Object)jo.get("nextid") != null){
	            						nextLineID = Integer.parseInt((String)jo.get("nextid"));	            					
	            					}
	            					editor.lineDictionary.updateNextID(lineID, nextLineID);
	            					int lineNumber = editor.GetLineIndexFromID(lineID);
	            					editor.lineDictionary.updateTextbyID(lineID, newText, Timestamp.valueOf((String)jo.get("timeEdited")), lastEditor);
	            					editor.lineDictionary.MarkChanged(lineNumber);
	            					editor.checkIt.set(!editor.checkIt.get());
	            				}
            				}
            			}
            			catch(Exception e){
            				// this seems to happen because it was empty
                    		e.printStackTrace(System.out);
                    		//System.out.println("Exception: " + e.getMessage());
            			}
            		}
            		java.lang.Thread.sleep(1000);
        		}
            	// Sleep for a while before running action again
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
            	System.out.println("Client Polling error: " + e.getMessage());
            } 
            catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }// End of While loop
		
    }// End of run() function
	
	
	
}

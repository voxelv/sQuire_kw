package sq.app.model;

import org.json.simple.JSONObject;

import sq.app.model.editor.EditorCodeArea;

public class BackgroundWorker extends Thread{
	private EditorCodeArea editor;
	private LineDictionary dictionary;
	private ServerConnection server;
	
	public BackgroundWorker(EditorCodeArea editor, LineDictionary dictionary, ServerConnection server)
	{
		this.editor = editor;
		this.dictionary = dictionary;
		this.server = server;
	}
	
	@Override
    public void run()
    {
		while (sq.app.MainApp.GetServer().getStatus())
		{
            try {
            	
            	
            	// Sleep for a while before running action again
                Thread.sleep(200);
            } catch (InterruptedException e) {
                break;
            }
            
        }// End of While loop
		
    }// End of run() function
	
	
	
}

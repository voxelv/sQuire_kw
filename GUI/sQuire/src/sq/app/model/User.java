package sq.app.model;

public class User {
	
	private int UserID;
	
	public User(){
		this.UserID = 0;
	}
	
	public User(int ID){
		this.UserID = ID;
	}
	
	public int getUserID(){
		return UserID;
	}
	
	public void setUserID(int ID) {
        this.UserID = ID;
    }

}

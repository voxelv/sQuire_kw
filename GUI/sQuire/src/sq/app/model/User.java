package sq.app.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

	private final StringProperty Username;
	private final StringProperty Email;
	private final StringProperty Password;
	
	public User(){
		this(null);
	}
	
	public User(String Username){
		this.Username = new SimpleStringProperty(Username);
		
		this.Email = new SimpleStringProperty("yeah@yeah.yeah");
		this.Password = new SimpleStringProperty("YEAH!");
	}
	
	public String getUsername(){
		return Username.get();
	}
	
	public void setUsername(String Username) {
        this.Username.set(Username);
    }
	
	public StringProperty UsernameProperty(){
		return Username;
	}
	
	public String getEmail(){
		return Email.get();
	}
	
	public void setEmail(String Email) {
        this.Email.set(Email);
    }
	
	public StringProperty EmailProperty(User name){
		return name.Email;
	}
	
	public String getPassword(){
		return Password.get();
	}
	
	public void setPassword(String Password) {
        this.Password.set(Password);
    }
	
	public StringProperty PasswordProperty(){
		return Password;
	}
}

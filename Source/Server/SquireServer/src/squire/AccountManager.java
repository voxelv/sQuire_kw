package squire;

import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;

public class AccountManager {
	private DBConnector dbc;
	ClientBuilder builder;
	Client client;
	Tenant tenant;
	ApplicationList applications;
	com.stormpath.sdk.application.Application app;
	private UserAccount loggedInAccount;
	
	
	public AccountManager(DBConnector dbc)
	{
		this.dbc = dbc;
		loggedInAccount = null;
		builder = null;
		client = null;
		tenant = null;
		applications = null;
		app = null;
		
		
	}
	
	public void connectToStormpath()
	{
		if (builder == null)
		{
			this.builder = Clients.builder();
	    	Properties properties = new Properties();
	    	properties.setProperty("apiKey.id", "3OIJT00DLSL8BQ3UCLR5X6C6S");
	    	properties.setProperty("apiKey.secret", "CGMCI0ul9ZR3hDiDIvcocccav3KOKcpHZ2doeqUm1i8");
	    	ApiKey apiKey = ApiKeys.builder().setProperties(properties).build();
	        this.client = Clients.builder().setApiKey(apiKey).build();
	        this.tenant = client.getCurrentTenant();
	        this.applications = tenant.getApplications(Applications.where(Applications.name().eqIgnoreCase("sQuire")));
	        this.app = applications.iterator().next();
		}
	}
	
	public String CreateAccount(String fName, String lName, String uName, String email, String pword)
	{
		connectToStormpath();
		
		Account account = client.instantiate(Account.class);

		String output = "";
		
        account
            .setGivenName(fName)
            .setSurname(lName)
            .setUsername(uName)
            .setEmail(email)
            .setPassword(pword);
//            .getCustomData().put("favoriteColor", "white");
        try {
            app.createAccount(account);
            output = "Success";
            this.CreateLocalAccount(uName);
//            createresult.setText("Account '" + uname.getText() + "' Created");
        } catch (Exception e) {
//            createresult.setText(e.getMessage());
            output = e.getMessage();
        }
        
        return output;
	}
	
	private int GetLocalAccountID(String username)
	{
		String query = "SELECT userID from Users where userName=?";
		String[] values = new String[1];
		values[0] = username;
		int userID = -1;
		
		try {
			JSONArray idList = this.dbc.query(query, values);
			userID = Integer.parseInt( (String)((JSONObject)idList.get(0)).get("userID") ); 
		} catch (SQLException e) {
//			e.printStackTrace();
		}
		
		return userID;
	}
	
	private void CreateLocalAccount(String username)
	{
		String query = "INSERT INTO Users (userName) values (?)";
		String[] values = new String[1];
		values[0] = username;
		
		try {
			this.dbc.query(query, values);
		} catch (SQLException e) {
//			e.printStackTrace();
		}
	}
	
	public String Login( String uName, String pWord )
	{
		connectToStormpath();
		AuthenticationRequest request = new UsernamePasswordRequest(uName, pWord);
		String output = "";
		
        //Now let's authenticate the account with the application:
        try {
            AuthenticationResult result = app.authenticateAccount(request);
            Account account = result.getAccount();
            this.loggedInAccount = new UserAccount();
            this.loggedInAccount.stormpathAccount = account;
            this.loggedInAccount.localUserID = this.GetLocalAccountID(uName);
            
            output = "Success";
        } catch (ResourceException ex) {
            output = ex.getMessage();
        }
        
        return output;
	}
	
	public void touchCurrentUserAccount()
	{
		if (this.loggedInAccount != null)
		{
			String query = "UPDATE Users set lastOnline=NOW() where userID=?";
			String[] values = new String[1];
			values[0] = String.valueOf(this.loggedInAccount.localUserID);
			
			try {
				this.dbc.query(query, values);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String getOnlineUsers()
	{
		String query = "select * from Users where lastOnline >= NOW() - INTERVAL 1 MINUTE;";
		String[] values = new String[0];
		
		JSONArray output = null;
		try {
			output = this.dbc.query(query, values);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			output = new JSONArray();
			e.printStackTrace();
		}
		
		return output.toJSONString();
	}
	
	public int GetUserAccountID()
	{
		if (this.loggedInAccount != null)
			return this.loggedInAccount.localUserID;
		else
			return -1;
	}
	
	private class UserAccount {
		public Account stormpathAccount;
		public int localUserID;
		
		public UserAccount()
		{
			this.localUserID = -1;
			this.stormpathAccount = null;
		}
	}
}


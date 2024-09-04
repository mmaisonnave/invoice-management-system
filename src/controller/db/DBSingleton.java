package controller.db;
 
public class DBSingleton {
	private static DBEngine instance = null;
	
	public DBSingleton(){}
	
	public static DBEngine getInstance(){
		if (instance == null)
			instance = new DBEngine();
		return instance;
	}

}

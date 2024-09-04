package exception;

@SuppressWarnings("serial")
public class InvalidBudgetException extends Exception{
	public InvalidBudgetException(String msj){
		super(msj);
	}

}

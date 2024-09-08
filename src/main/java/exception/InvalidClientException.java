package exception;

@SuppressWarnings("serial")
public class InvalidClientException extends Exception {
	
	public InvalidClientException(String msj){
		super(msj);
	}

}

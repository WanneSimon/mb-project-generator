package cc.wanforme.mbgenerator.exception;

public class MBException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MBException() {}

	public MBException(String msg) {
		super(msg);
	}
	
    public MBException(Throwable cause) {
        super(cause);
    }
    
    public MBException(String message, Throwable cause) {
        super(message, cause);
    }
	
}

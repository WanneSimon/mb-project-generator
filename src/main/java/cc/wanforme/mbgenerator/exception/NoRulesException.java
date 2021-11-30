package cc.wanforme.mbgenerator.exception;

public class NoRulesException extends MBException {

	private static final long serialVersionUID = 1L;

	public NoRulesException(String msg) {
		super(msg);
	}

	public NoRulesException(String string, Throwable e) {
		super(string, e);
	}
	
}

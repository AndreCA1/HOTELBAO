package ifmg.edu.br.HOTELBAO.services.exceptions;

public class DataBaseException extends RuntimeException {
    public DataBaseException() {
        super();
    }
    public DataBaseException(String message) {
        super(message);
    }
}

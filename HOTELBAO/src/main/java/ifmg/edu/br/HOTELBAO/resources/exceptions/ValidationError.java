package ifmg.edu.br.HOTELBAO.resources.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandartError{
    private List<FieldMessage> fieldMessages = new ArrayList<FieldMessage>();

    public ValidationError(){

    }

    public List<FieldMessage> getFieldMessages() {
        return fieldMessages;
    }

    public void setFieldMessages(List<FieldMessage> fieldMessage) {
        this.fieldMessages = fieldMessage;
    }

    public void addFieldMessage(String field, String message){
        this.fieldMessages.add(new FieldMessage(field, message));
    }
}

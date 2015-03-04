

import java.io.Serializable;

public class ChatMessage implements Serializable {
	protected static final long serialVersionUID = 1L;
    /*These are the types of messages, MESSAGE denotes regular messages between client and server
     *and LOGOUT denotes the message sent went logout is clicked in client app
     **/
    static final int MESSAGE = 1, LOGOUT = 2;
    private int type;
    private String message;

    ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    int getType() {
        return type;
    }
    
    String getMessage() {
        return message;
    }

}


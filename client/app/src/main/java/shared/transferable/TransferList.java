package shared.transferable;


import java.util.ArrayList;

public class TransferList extends ArrayList<Transferable> {
    private TransferList(){}

    public TransferList(NetCommands command, RequestID id){
        super.add(command);
        super.add(id);
    }
}

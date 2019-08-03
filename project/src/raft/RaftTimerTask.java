package raft;

import java.util.TimerTask;

public class RaftTimerTask extends TimerTask {

    private RaftNode raftNode;

    public RaftTimerTask(RaftNode _raftNode){
        this.raftNode = _raftNode;
    }

    @Override
    public void run() {
        if(!raftNode.isLeader()) {
            raftNode.postulate();
        }
    }
}

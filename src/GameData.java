import java.util.ArrayList;
import java.util.List;

public class GameData {
    private List<Session> sessions;
    public GameData(){
        this.sessions = new ArrayList<>();
    }

    // adds a turn to the correct game session. If one does not exit, a session will be created.
    public void organizeTurn(Turn turn) {
        if (!sessions.isEmpty()) {
            for (Session session : sessions) {
                if (session.getSessionID() == turn.getGameSessionID()) {
                    session.addTurn(turn);
                    return;
                }
            }
        }
        this.sessions.add(new Session(turn.getGameSessionID(), turn));
    }

    public List<Session> getSessions() {
        return sessions;
    }

    @Override
    public String toString() {
        return "GameData{" +
                "sessions=" + sessions +
                '}';
    }
}
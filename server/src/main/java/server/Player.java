package server;

public class Player {
    private final String name;

    private final int turn;

    public Player(String name, int turn) {
        this.name = name;
        this.turn = turn;
    }

    public String getName() {
        return name;
    }


}

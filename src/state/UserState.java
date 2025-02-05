package state;

public class UserState {
    private static State currentState; // Текущее состояние пользователя

    public static State setCurrentState(State state) {
        currentState = state;
        return currentState;
    }
    public static State getCurrentState() {
        return currentState;
    }
}

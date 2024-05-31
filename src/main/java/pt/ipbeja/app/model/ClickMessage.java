package pt.ipbeja.app.model;

public class ClickMessage extends Message {
    private final Position pos;
    private final char c;

    public ClickMessage(Position pos, char c) {
        super();
        this.pos = pos;
        this.c = c;
    }

    @Override
    public String getMessage() {
        return String.format("(%d, %c) -> %c", this.pos.line(), this.pos.col() + (int) 'A', this.c);
    }
}

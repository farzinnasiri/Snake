package snake;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Square {
    private int x, y, size;

    public Square(int size) {

        this.size = size;

    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public Rectangle getBound() {
        return new Rectangle(this.x, this.y, size, size);
    }

    public boolean isCollision(Square o) {
        if (o == this) {
            return false;
        }
        return getBound().intersects(o.getBound());

    }

    public void render(Graphics2D g2) {
        g2.fillRect(this.x + 1, this.y + 1, this.size, this.size);
    }

    public void renderWall(Graphics2D g2) {
        g2.fillRect(this.x + 1, this.y + 1, this.size, this.size);
    }


}

package ruinnel.net.minesweeper.bean;

/**
 * Created by ruinnel on 2017. 4. 9..
 */
public class Event {
  public boolean isLongClick;
  public Cell cell;

  public Event(boolean isLongClick, Cell cell) {
    this.isLongClick = isLongClick;
    this.cell = cell;
  }

  @Override
  public String toString() {
    return "Event{" +
      "isLongClick=" + isLongClick +
      ", cell=" + cell +
      '}';
  }
}

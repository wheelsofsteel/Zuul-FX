package csvLoader.headers;

import java.util.List;
import java.util.stream.Collectors;

import csvEditor.CSVEditorCell;
import csvEditor.EditCSVController;
import javafx.collections.ObservableList;

public class DirectionHeader extends Header {
	private static final List<Integer> INDEX_RANGE = List.of(2, 3, 4, 5);
	private final String direction;

	public DirectionHeader(int idx) {
		super(HeaderEnum.DIRECTION);
		switch(idx) {
		case 2:
			direction = "north";
			break;
		case 3:
			direction = "east";
			break;
		case 4:
			direction = "south";
			break;
		case 5:
			direction = "west";
			break;
		default:
			System.err.println("Index " + idx + " could not be bound to a direction. Game will behave unexpectedly");
			direction = null;
		}
	}

	@Override
	public String validateFieldText(String textFieldValue) {
		ObservableList<ObservableList<CSVEditorCell>> rooms = EditCSVController.getRooms();
		List<String> roomNames = rooms.stream().map(e -> e.get(0).getProperty().getValue())
				.collect(Collectors.toList());

		if (!textFieldValue.matches("\\w+")) {
			return "Field must be one word in length with no spaces.";
		}

		if (!roomNames.contains(textFieldValue) && !textFieldValue.equals("null")) {
			return "Not a valid direction";
		}

		return null;
	}

	public static boolean matchesIndexCondition(int csvIndex) {
		return INDEX_RANGE.contains(csvIndex);
	}

	public String getDirection() {
		return direction;
	}
}

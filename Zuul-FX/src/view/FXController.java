package view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import csvLoader.CSVDataHandler;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import npc.NPC;
import view.ContextMenus.DropContextMenu;
import view.ContextMenus.ItemsContextMenu;
import view.ContextMenus.ActorContextMenu;
import zuul.GameController;
import zuul.GameType;
import zuul.CommandHandler;

public class FXController {
	private String csvPath;
	private final int SCENE_WIDTH = 900;
	private final int SCENE_HEIGHT = 600;
	private Stage stage;
	private CommandHandler commandHandler;
	
	@FXML
	private Label totalWeight;
	@FXML
	private TextArea gameText;
	public StringProperty gameTextProperty = new SimpleStringProperty("");
	@FXML
	private ListView<String> inventory, itemsInRoom, actorsInRoom;
	@FXML
	private Button buttonGoWest, buttonGoEast, buttonGoSouth, buttonGoNorth, buttonLook;
	@FXML
	private MenuItem menuItemStartCustomGame, menuItemLoadCustomGame, menuItemStartDefaultGame;

	public String getCSVPath() {
		return csvPath;
	}

	@FXML
	public void quitProgram() {
		Platform.exit();
		System.exit(0);
	}

	private void setDirectionButtons() {
		ArrayList<String> exits = GameController.getCurrentRoom().getAllDirections();
		Button[] btnList = { buttonGoWest, buttonGoEast, buttonGoNorth, buttonGoSouth };
		for (Button btn : btnList) {
			btn.setDisable(true);
			for (String direction : exits) {
				String btnDirection = btn.getId();
				if (btnDirection.equals(direction.toLowerCase())) {
					btn.setDisable(false);
				}
			}
		}
	}

	/**
	 * Binds the data of the game data's array and the view's ListView
	 */
	public void updateView() {
		itemsInRoom.setItems(GameController.getCurrentRoom().getItemNames());
		actorsInRoom.setItems(GameController.getCurrentRoom().getActorNames());
	}

	public void lookClicked() {
		commandHandler.handleCommand(new String[] { "Look" });
	}

	public void goClicked(MouseEvent event) {
		Button tmp = (Button) event.getSource();
		String direction = tmp.getId();
		gameTextProperty.set("");
		Platform.runLater(() -> commandHandler.handleCommand(new String[] { "Go", direction }));
		Platform.runLater(() -> setDirectionButtons());
		Platform.runLater(() -> updateView());
	}

	private void enableAllButtons() {
		buttonLook.setDisable(false);
	}

	public void startGame() {
		GameController.start();
		inventory.setItems(GameController.getCurrentPlayer().getInvModel().getInventoryNames());
		totalWeight.textProperty().bind(GameController.getCurrentPlayer().getInvModel().getTotalWeightProp());
		setDirectionButtons();
		enableAllButtons();
		updateView();
	}

	public void openFileChooser() {
		FileChooser fileChooser = new FileChooser();
		ExtensionFilter filter = new ExtensionFilter("CSV", "*.csv", "*.csv");
		fileChooser.setTitle("Select file...");
		fileChooser.getExtensionFilters().add(filter);
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			openFile(file);
		}
	}

	private void openFile(File file) {
		csvPath = file.getPath();
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setContentText("File loaded successfully.");
		a.showAndWait();
		initEditCSVView();
	}

	public void startDefaultGame() {
		String path = "src/csvLoader/defaultRoomData.csv";
		CSVDataHandler csvEditor = new CSVDataHandler(path);
		List<List<String>> roomData = csvEditor.getRoomData();
		GameController.initRooms(roomData, GameType.DEFAULT);
		menuItemLoadCustomGame.setDisable(true);
		menuItemStartDefaultGame.setDisable(true);
		startGame();
	}

	public void println(String ele) {
		System.out.println(ele);
		String newLine = System.getProperty("line.separator");
		gameTextProperty.setValue(gameTextProperty.getValue() + ele);
		gameTextProperty.setValue(gameTextProperty.getValue() + newLine + newLine);
	}

	public void printf(String ele) {
		System.out.println(ele);
		gameTextProperty.setValue(gameTextProperty.getValue() + ele);

	}

	public void printError(String error) {
		Alert a = new Alert(AlertType.ERROR);
		a.setContentText(error);
		a.show();
	}

	public void setStage(Stage primaryStage) {
		stage = primaryStage;
	}

	public void initDialogView(NPC npc) {
		DialogView dialogView = new DialogView(npc);
		dialogView.startDialog();
	}

	public void initEditCSVView() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../csvEditor/editCSVView.fxml"));
		try {
			Parent parent = fxmlLoader.load();
			Scene scene = new Scene(parent, SCENE_WIDTH, SCENE_HEIGHT);
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(scene);
			stage.showAndWait();
			menuItemStartCustomGame.setDisable(false);
			menuItemLoadCustomGame.setDisable(true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setContextMenus() {
		ItemsContextMenu itemsContextMenu = new ItemsContextMenu();
		itemsInRoom.setCellFactory(itemsContextMenu.getContextMenu(commandHandler));
		DropContextMenu dropContextMenu = new DropContextMenu();
		inventory.setCellFactory(dropContextMenu.getContextMenu(commandHandler));
		ActorContextMenu talkContextMenu = new ActorContextMenu();
		actorsInRoom.setCellFactory(talkContextMenu.getContextMenu());
	}

	FXController() {
		commandHandler = new CommandHandler();
	}

	public void onLoad() {
		gameText.textProperty().bind(gameTextProperty);
		setContextMenus();
		// If the game text overflows ensures the scroll is positioned to the bottom for
		// the new text to be visible.
		gameTextProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				gameText.selectPositionCaret(gameText.getLength());
				gameText.deselect();
			}
		});
	}

}

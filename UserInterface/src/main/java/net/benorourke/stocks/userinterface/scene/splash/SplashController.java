package net.benorourke.stocks.userinterface.scene.splash;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import net.benorourke.stocks.userinterface.StockApplication;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.util.Constants;

import java.util.Random;

public class SplashController extends Controller
{
	public static final double VBOX_PADDING = 50;

	private final SplashModel model;

	@FXML private AnchorPane root;

	// |- root
	//    -| GridPane (pane)
	//       |- HBox (centreHBox) (for horizontal center alignment)
	//          |- VBox (vBox) (for vertical center alignment)
	//	           |- field

	public SplashController()
    {
		this.model = new SplashModel(this);
	}

	@FXML
	public void initialize()
	{
		StockApplication.debug("Initialised SplashController");

		GridPane pane = new GridPane();
		bindX(pane, root);
		bindY(pane, root);
		pane.setAlignment(Pos.CENTER);
		root.getChildren().add(pane);

		HBox centreHBox = new HBox();
		bind(centreHBox, pane, 0.85);
		pane.getChildren().add(centreHBox);

		VBox vBox = new VBox();
		vBox.setPadding(new Insets(VBOX_PADDING, VBOX_PADDING, VBOX_PADDING, VBOX_PADDING));
		bind(vBox, centreHBox);
		centreHBox.getChildren().add(vBox);

		initScrollPane(vBox);
	}

	private void initScrollPane(VBox parent)
	{
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		bind(scrollPane, parent);

		FlowPane flowPane = new FlowPane();
		flowPane.setHgap(20);
		flowPane.setVgap(20);
		flowPane.setColumnHalignment(HPos.CENTER);
		flowPane.setRowValignment(VPos.CENTER);
		bindY(flowPane, scrollPane);
		bindX(flowPane, scrollPane, 0.9);
		Random rand = new Random();
		for (int i = 0; i < 4; i ++)
		{
			VBox item = createPaneItem(flowPane);
			item.setPrefWidth(250);
			item.setPrefHeight(item.getPrefWidth() * Constants.INVERSE_ASPECT_RATIO);
			item.setPadding(new Insets(20));

			Color[] colors = new Color[] {Color.YELLOW, Color.BLUE, Color.BLACK};
			item.setBackground(new Background(new BackgroundFill(colors[rand.nextInt(colors.length)],
																 CornerRadii.EMPTY, Insets.EMPTY)));
			flowPane.getChildren().add(item);
		}

		parent.getChildren().add(scrollPane);
		scrollPane.setContent(flowPane);
	}

	private VBox createPaneItem(FlowPane flowPane)
	{
		VBox vBox = new VBox();
		vBox.getChildren().add(new Label("Test"));
		return vBox;
	}

	public SplashModel getModel()
    {
		return model;
	}

}

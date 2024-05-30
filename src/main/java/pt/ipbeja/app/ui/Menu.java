package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.ipbeja.app.model.GameOptions;
import pt.ipbeja.app.model.WordOrientations;

import static pt.ipbeja.app.model.WSModel.MAX_SIDE_LEN;
import static pt.ipbeja.app.model.WSModel.MIN_SIDE_LEN;

public class Menu extends VBox {
    public Menu(OnStartHandler handler, GameOptions opts) {
        Button btn = new Button("Start");
        NumberInput lines = new NumberInput("lines:\t", opts.getLines());
        lines.setMin(MIN_SIDE_LEN);
        lines.setMax(MAX_SIDE_LEN);
        lines.setHandler(opts::setLines);
        NumberInput columns = new NumberInput("columns:\t", opts.getColumns());
        columns.setMin(MIN_SIDE_LEN);
        columns.setMax(MAX_SIDE_LEN);
        columns.setHandler(opts::setColumns);

        NumberInput max = new NumberInput("maximum amount of words:\t", opts.getMaxWords());
        max.setMin(1);
        max.setHandler(opts::setMaxWords);

        NumberInput min = new NumberInput("minimum length of an word:\t", opts.getMinWordSize());
        min.setMin(1);

        NumberInput wilds = new NumberInput("Number of wild cards:\t", opts.getNumberOfWilds());
        wilds.setMin(1);
        wilds.setMax(min.getVal());
        min.setHandler(newVal -> {
            wilds.setMax(newVal);
            wilds.setVal(wilds.getVal());
            opts.setMinWordSize(newVal);
        });
        wilds.setHandler(opts::setNumberOfWilds);

        // https://docs.oracle.com/javafx/2/ui_controls/radio-button.htm
        final ToggleGroup group = new ToggleGroup();
        RadioButton manual = new RadioButton("Manual");
        manual.setUserData(ProviderMode.MANUAL);
        RadioButton db = new RadioButton("Database");
        db.setUserData(ProviderMode.DB);
        manual.setToggleGroup(group);
        db.setToggleGroup(group);
        db.setSelected(true);

        CheckBox diagonalAllowed = new CheckBox("Allow diagonal words?");
        diagonalAllowed.setOnAction(event -> {
            if (diagonalAllowed.isSelected()) {
                opts.addOrientationAllowed(WordOrientations.DIAGONAL);
            } else {
                opts.removeOrientationAllowed(WordOrientations.DIAGONAL);
            }
        });

        btn.setOnAction(event -> handler.onStart(opts, (ProviderMode) group.getSelectedToggle().getUserData()));

        this.getChildren().addAll(lines, columns, new HBox(manual, db), max, min, diagonalAllowed, wilds, btn);

        this.setAlignment(Pos.CENTER);
    }

    public interface OnStartHandler {
        void onStart(GameOptions opts, ProviderMode mode);
    }

    public enum ProviderMode { MANUAL, DB }
}

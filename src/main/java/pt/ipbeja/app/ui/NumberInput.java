package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NumberInput extends HBox {
    private @Nullable OnChangeHandler handler = null;
    private int min, max;
    private int val;
    private final @NotNull Label valLabel;

    public NumberInput(String label, int defaultValue) {
        this.val = defaultValue;
        this.setAlignment(Pos.CENTER);
        this.valLabel = new Label(String.valueOf(this.val));
        Button inc = new Button("^");
        Button dec = new Button("v");
        inc.setOnAction(event -> {
            if ((this.val + 1) <= this.max) {
                if (this.handler != null) {
                    this.handler.onChange(this.val, this.val + 1);
                }
                this.val++;
                this.update();
            }
        });
        dec.setOnAction(event -> {
            if ((this.val - 1) >= this.min) {
                if (this.handler != null) {
                    this.handler.onChange(this.val, this.val - 1);
                }
                this.val--;
                this.update();
            }
        });
        this.getChildren().addAll(
                new Label(label),
                this.valLabel,
                new VBox(inc, dec)
        );
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getVal() {
        return val;
    }

    private void update() {
        this.valLabel.setText(String.valueOf(this.val));
    }

    public void setHandler(@Nullable OnChangeHandler handler) {
        this.handler = handler;
    }

    public interface OnChangeHandler {
        void onChange(int oldVal, int newVal);
    }
}

package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class NumberInput extends HBox {
    private final @NotNull Label valLabel;
    private @Nullable OnChangeHandler handler = null;
    private Optional<Integer> min, max;
    private int val;

    public NumberInput(String label, int defaultValue) {
        this.val = defaultValue;
        this.max = Optional.empty();
        this.min = Optional.empty();
        this.setAlignment(Pos.CENTER);
        this.valLabel = new Label(String.valueOf(this.val));
        Button inc = new Button("^");
        Button dec = new Button("v");
        inc.setOnAction(event -> {
            if (this.max.isEmpty() || (this.val + 1) <= this.max.get()) {
                if (this.handler != null) {
                    this.handler.onChange(this.val, this.val + 1);
                }
                this.val++;
                this.update();
            }
        });
        dec.setOnAction(event -> {
            if (this.min.isEmpty() || (this.val - 1) >= this.min.get()) {
                if (this.handler != null) {
                    this.handler.onChange(this.val, this.val - 1);
                }
                this.val--;
                this.update();
            }
        });
        this.getChildren().addAll(new Label(label), this.valLabel, new VBox(inc, dec));
    }


    public void setMin(int min) {
        this.min = Optional.of(min);
    }


    public void setMax(int max) {
        this.max = Optional.of(max);
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

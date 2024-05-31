package pt.ipbeja.app.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class NumberInput extends HBox {
    private final Label valLabel;
    private OnChangeHandler handler;
    private Optional<Integer> min, max;
    private int val;

    public NumberInput(String label, int defaultValue) {
        super();
        this.val = defaultValue;
        this.max = Optional.empty();
        this.min = Optional.empty();
        this.setAlignment(Pos.CENTER);
        this.valLabel = new Label(String.valueOf(this.val));
        Button inc = new Button("^");
        Button dec = new Button("v");
        inc.setOnAction(event -> {
            if (this.max.isEmpty() || (this.val + 1) <= this.max.get()) {
                if (null != this.handler) {
                    this.handler.onChange(this.val + 1);
                }
                this.val++;
                this.update();
            }
        });
        dec.setOnAction(event -> {
            if (this.min.isEmpty() || (this.val - 1) >= this.min.get()) {
                if (null != this.handler) {
                    this.handler.onChange(this.val - 1);
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
        return this.val;
    }

    public void setVal(int val) {
        this.val = Math.max(Math.min(val, this.max.orElse(Integer.MAX_VALUE)), this.min.orElse(Integer.MIN_VALUE));
        if (null != this.handler) {
            this.handler.onChange(this.val);
        }
        this.update();
    }

    private void update() {
        this.valLabel.setText(String.valueOf(this.val));
    }

    public void setHandler(OnChangeHandler handler) {
        this.handler = handler;
    }

    public interface OnChangeHandler {
        void onChange(int newVal);
    }
}

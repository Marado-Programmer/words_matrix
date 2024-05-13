package pt.ipbeja.app.model.message_to_ui;

import org.jetbrains.annotations.NotNull;

/**
 * Message to be sent from the model so that the interface updates the positions in the list
 *
 * @author anonymized
 * @version 2024/04/14
 */
public interface MessageToUI {
    @NotNull String getMessage();
}

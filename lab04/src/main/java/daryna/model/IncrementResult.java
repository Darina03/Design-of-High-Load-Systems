package daryna.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IncrementResult {

    private final boolean success;
    private final String message;

}

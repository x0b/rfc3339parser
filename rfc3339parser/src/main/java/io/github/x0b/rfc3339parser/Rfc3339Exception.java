package io.github.x0b.rfc3339parser;

import java.text.ParseException;

public class Rfc3339Exception extends ParseException {

    /**
     * Constructs a ParseException with the specified detail message and
     * offset.
     * A detail message is a String that describes this particular exception.
     *
     * @param message     the detail message
     * @param errorOffset the position where the error is found while parsing.
     */
    Rfc3339Exception(String message, int errorOffset) {
        super(message, errorOffset);
    }

    Rfc3339Exception(String message) {
        super(message, -1);
    }

}

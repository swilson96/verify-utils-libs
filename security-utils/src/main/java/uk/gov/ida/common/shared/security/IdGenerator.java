package uk.gov.ida.common.shared.security;

import java.util.UUID;

public class IdGenerator {

    /**
     * Creates and returns a unique id.
     * The id is a legal NCName, starting with a letter or an underscore. (Digests are not allowed).
     * @return an id.
     */
    public String getId() {
        return String.format("_%s", UUID.randomUUID().toString());
    }
}

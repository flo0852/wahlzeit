package org.wahlzeit.model;

import java.lang.annotation.Repeatable;

@Repeatable(Patterns.class)
public @interface PatternInstance {

    String patternName();

    String[] participants();
}

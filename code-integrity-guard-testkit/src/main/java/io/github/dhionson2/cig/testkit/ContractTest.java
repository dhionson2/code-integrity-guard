package io.github.dhionson2.cig.testkit;

import java.lang.annotation.*;

/** Associates a JUnit invocation with stable test IDs; execution evidence alone never verifies a security contract. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface ContractTest { String[] value(); }

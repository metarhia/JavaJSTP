package com.metarhia.jstp.compiler.annotations;

import com.metarhia.jstp.core.Handlers.ManualHandler;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by lundibundi on 3/13/17.
 */
@Target({ElementType.TYPE})
public @interface JSTPHandler {

  Class value() default ManualHandler.class;
}

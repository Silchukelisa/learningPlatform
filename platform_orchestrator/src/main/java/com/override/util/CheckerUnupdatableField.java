package com.override.util;

import com.override.annotation.Unupdatable;
import com.override.exception.UnupdatableDataException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class CheckerUnupdatableField<T> {

    public void executeCheck(@NotNull T currentValue, T newValue) {

        try {
            for(Field field : currentValue.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Unupdatable.class)) {

                    field.setAccessible(true);

                    if( (field.get(currentValue) != null) &&
                        (field.get(currentValue) != field.get(newValue))) {
                        throw new UnupdatableDataException("Attempt to change data in the locked field");
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }

    }

}

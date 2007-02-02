package com.sitescape.team.module.definition.index;

import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.team.InternalException;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.util.ReflectHelper;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderUtil {
    
    public static Field[] buildField(DefinableEntity entity, String dataElemName, String fieldBuilderClassName, Map args) {
        try {
            Class fieldBuilderClass = ReflectHelper.classForName(fieldBuilderClassName);
            FieldBuilder fieldBuilder = (FieldBuilder) fieldBuilderClass.newInstance();
            return fieldBuilder.buildField(entity, dataElemName, args);
        } catch (ClassNotFoundException e) {
            throw new InternalException (e);
        } catch (InstantiationException e) {
            throw new InternalException (e);
        } catch (IllegalAccessException e) {
            throw new InternalException (e);
        }
    }
    
}

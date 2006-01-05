package com.sitescape.ef.module.definition.index;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import org.apache.lucene.document.Field;

import com.sitescape.ef.search.BasicIndexUtils;

/**
 *
 * @author Jong Kim
 */
public class FieldBuilderSelect extends AbstractFieldBuilder {

    public String makeFieldName(String dataElemName) {
        // e.g. data element name = "abc" -> field name = "select#abc"
        return INDEXING_TYPE_SELECT + BasicIndexUtils.DELIMITER + dataElemName;
    }
    
    protected Field[] build(String dataElemName, Set dataElemValue, Map args) {
        // This default radio implementation ignores args.  
        
        Field[] fields = new Field[dataElemValue.size()];
        String fieldName = makeFieldName(dataElemName);
       
        String val;
        Field field;
        int i = 0;
        for(Iterator it = dataElemValue.iterator(); it.hasNext(); i++) {
            val = (String) it.next();
	        field = new Field(fieldName, val, false, true, false);
	        fields[i] = field;
        }
        
        return fields;
    }

}

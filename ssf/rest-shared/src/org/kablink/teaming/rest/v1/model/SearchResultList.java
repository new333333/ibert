/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
 *
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 *
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 *
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 *
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: david
 * Date: 5/18/12
 * Time: 12:45 PM
 */
@XmlRootElement (name = "results")
public class SearchResultList<T> {
    private Integer first;
    private Integer count = 0;
    private Integer total = 0;
    private String next;
    private List<T> results = new ArrayList<T>();

    public SearchResultList() {
        first = 0;
    }

    public SearchResultList(Integer first) {
        this.first = first;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public void setNextIfNecessary(String nextUrl, Map<String, String> nextParams) {
        if (nextUrl!=null) {
            int offset = first + count;
            if (offset<total) {
                Map<String, String> params = new LinkedHashMap<String, String>();
                if (nextParams!=null) {
                    params.putAll(nextParams);
                }
                params.put("first", Integer.toString(offset));
                params.put("count", Integer.toString(count));
                StringBuilder builder = new StringBuilder(nextUrl);
                boolean first = true;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (first) {
                        builder.append("?");
                        first = false;
                    } else {
                        builder.append("&");
                    }
                    try {
                        builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                               .append("=")
                               .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
                setNext(builder.toString());
            }
        }
    }

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    public List<T> getResults() {
        return results;
    }

    public void append(T obj) {
        results.add(obj);
        if (count<results.size()) {
            count = results.size();
        }
        if (total<count) {
            total = count;
        }
    }

    public void appendAll(Collection<T> obj) {
        results.addAll(obj);
        if (count<results.size()) {
            count = results.size();
        }
        if (total<count) {
            total = count;
        }
    }

    public void appendAll(T [] objs) {
        for (T obj : objs) {
            results.add(obj);
        }
        if (count<results.size()) {
            count = results.size();
        }
        if (total<count) {
            total = count;
        }
    }

}

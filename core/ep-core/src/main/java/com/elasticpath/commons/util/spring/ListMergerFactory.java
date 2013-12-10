package com.elasticpath.commons.util.spring;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.config.ListFactoryBean;

/** Spring utility class for merging two list beans. */
public class ListMergerFactory extends ListFactoryBean {

    private List<Object> mergeList;
    
    public void setMergeList(final List<Object> mergeList) {
        this.mergeList = mergeList;
    }

    /** Concatenate <code>mergeList</code> onto the end of <code>sourceList</code>.
     * @return Merged list
     */
	protected List<Object> createInstance() {
    	@SuppressWarnings("unchecked")
        List<Object> newList = super.createInstance();
        for (Iterator<Object> iter = mergeList.iterator(); iter.hasNext();) {
            Object element = iter.next();
            newList.add(element);
        }
        return newList;
    }
}

package com.frxs.delivery.model;

import java.io.Serializable;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/08
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class SectionListItem implements Serializable {

    public static final int ITEM = 0;
    public static final int SECTION = 1;

    public Object item;
    public final int type;
    public String section;
    public int sectionId;

    public int sectionPosition;
    public int listPosition;

    public SectionListItem(final Object item, final int type, String section) {
        super();
        this.item = item;
        this.type = type;
        this.section = section;
    }

    public SectionListItem(final Object item, final int type, String section, int sectionId) {
        super();
        this.item = item;
        this.type = type;
        this.section = section;
        this.sectionId = sectionId;
    }

    public Object getItem()
    {
        return item;
    }

    public void setItem(Object item)
    {
        this.item = item;
    }

    public int getType()
    {
        return type;
    }

    public String getSection()
    {
        return section;
    }

    public int getSectionId()
    {
        return sectionId;
    }

    public void setSectionId(int sectionId)
    {
        this.sectionId = sectionId;
    }

    public void setSection(String section)
    {
        this.section = section;
    }

    @Override
    public String toString() {
        return item.toString();
    }
}

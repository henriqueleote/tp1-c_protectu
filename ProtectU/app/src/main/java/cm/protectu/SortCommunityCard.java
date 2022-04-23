package cm.protectu;

import java.util.Comparator;

public class SortCommunityCard implements Comparator<CommunityCard> {

    @Override
    public int compare(CommunityCard t1, CommunityCard t2) {
        return t2.getDate().compareTo(t1.getDate());
    }
}

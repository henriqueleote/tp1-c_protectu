package cm.protectu.Community;

import java.util.Comparator;

import cm.protectu.Community.CommunityCard;

public class SortCommunityCardClass implements Comparator<CommunityCard> {

    @Override
    public int compare(CommunityCard t1, CommunityCard t2) {
        return t2.getDate().compareTo(t1.getDate());
    }
}

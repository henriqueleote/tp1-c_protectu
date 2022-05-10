package cm.protectu.MissingBoard;

import java.util.Comparator;

import cm.protectu.Community.CommunityCard;

public class SortMissingBoardCardClass implements Comparator<MissingCardClass> {

    @Override
    public int compare(MissingCardClass t1, MissingCardClass t2) {
        return t2.getDate().compareTo(t1.getDate());
    }
}

package com.devgale.gale3;

/**
 * Created by ATIV on 2015-02-26.
 */
import java.text.Collator;
import java.util.Comparator;

public class ListData {
    /**
     * 리스트 정보를 담고 있을 객체 생성
     */

    // 제목
    public String mTitle;
    // 링크 주소
    public String mUrl;

    /**
     * 알파벳 이름으로 정렬
     **/
    public static final Comparator<ListData> ALPHA_COMPARATOR = new Comparator<ListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ListData mListDate_1, ListData mListDate_2) {
            return sCollator.compare(mListDate_1.mTitle, mListDate_2.mTitle);
        }
    };
}
package dal;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LinhLTHE200306
 */
public class PaginationUtil {

    public static List<Integer> buildPageNumbers(int currentPage, int totalPages) {
        List<Integer> pages = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            if (i == 1 || i == totalPages || Math.abs(i - currentPage) <= 1) {
                pages.add(i);
            } else if (pages.get(pages.size() - 1) != 0) {
                pages.add(0);
            }
        }
        return pages;
    }
}

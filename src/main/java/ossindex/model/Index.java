package ossindex.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ossindex.Category;

/**
 * Created by Administrator on 13-7-11.
 */
public class Index {
    public List<Category> categories;

    public Map<String, List<String>> roots =new HashMap<String, List<String>>();


}
